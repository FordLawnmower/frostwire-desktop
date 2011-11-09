package com.frostwire.alexandria.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LibraryDatabase {

    public static final int OBJECT_NOT_SAVED_ID = -1;
    public static final int OBJECT_INVALID_ID = -2;
    public static final int STARRED_PLAYLIST_ID = -3;

    public static final int LIBRARY_DATABASE_VERSION = 3;

    private final File _databaseFile;
    private final String _name;

    private Connection _connection;

    private boolean _closed;

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public LibraryDatabase(File databaseFile) {
        _databaseFile = databaseFile;

        File path = databaseFile;
        _name = databaseFile.getName();

        _connection = openOrCreateDatabase(path, _name);
    }

    public File getDatabaseFile() {
        return _databaseFile;
    }

    public String getName() {
        return _name;
    }

    public boolean isClosed() {
        return _closed;
    }

    public synchronized List<List<Object>> query(String statementSql, Object... arguments) {
        if (isClosed()) {
            return new ArrayList<List<Object>>();
        }

        return query(_connection, statementSql, arguments);
    }

    /**
     * This method is synchronized due to possible concurrent issues, specially
     * during recently generated id retrieval.
     * @param expression
     * @return
     */
    public synchronized int update(String statementSql, Object... arguments) {
        if (isClosed()) {
            return -1;
        }

        return update(_connection, statementSql, arguments);
    }

    /**
     * This method is synchronized due to possible concurrent issues, specially
     * during recently generated id retrieval.
     * @param expression
     * @return
     */
    public synchronized int insert(String statementSql, Object... arguments) {
        if (isClosed()) {
            return OBJECT_INVALID_ID;
        }

        if (!statementSql.toUpperCase().startsWith("INSERT")) {
            return OBJECT_INVALID_ID;
        }

        if (update(statementSql, arguments) != -1) {
            return getIdentity();
        }

        return OBJECT_INVALID_ID;
    }

    public synchronized void close() {
        if (isClosed()) {
            return;
        }

        _closed = true;

        try {
            Statement statement = _connection.createStatement();
            statement.execute("SHUTDOWN");
            _connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void dump() {
        if (isClosed()) {
            return;
        }

        try {
            new DumpDatabase(this, new File(_databaseFile, "dump.txt")).dump();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Connection onUpdateDatabase(Connection connection, int oldVersion, int newVersion) {

        if (oldVersion == 1 && newVersion > 2) {
            setupInternetRadioStationsTable(connection, true, true);
            setupLuceneIndex(connection);
        }

        if (oldVersion == 2 && newVersion == 3) {
            setupLuceneIndex(connection);
        }

        update(connection, "UPDATE Library SET version = ?", LIBRARY_DATABASE_VERSION);

        return connection;
    }

    private Connection openConnection(File path, String name, boolean createIfNotExists) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("jdbc:h2:");
            sb.append(new File(path, name).getAbsolutePath());

            if (!createIfNotExists) {
                sb.append(";ifexists=true");
            }
            return DriverManager.getConnection(sb.toString(), "SA", "");
        } catch (Exception e) {
            return null;
        }
    }

    private Connection createDatabase(File path, String name) {
        Connection connection = openConnection(path, name, true);

        // STRUCTURE CREATION

        //update(connection, "DROP TABLE Library IF EXISTS CASCADE");
        update(connection, "CREATE TABLE Library (libraryId INTEGER IDENTITY, name VARCHAR(500), version INTEGER)");

        //update(connection, "DROP TABLE Playlists IF EXISTS CASCADE");
        update(connection, "CREATE TABLE Playlists (playlistId INTEGER IDENTITY, name VARCHAR(500), description VARCHAR(10000))");
        update(connection, "CREATE INDEX idx_Playlists_name ON Playlists (name)");

        //update(connection, "DROP TABLE PlaylistItems IF EXISTS CASCADE");
        update(connection,
                "CREATE TABLE PlaylistItems (playlistItemId INTEGER IDENTITY, filePath VARCHAR(10000), fileName VARCHAR(500), fileSize BIGINT, fileExtension VARCHAR(10), trackTitle VARCHAR(500), trackDurationInSecs REAL, trackArtist VARCHAR(500), trackAlbum VARCHAR(500), coverArtPath VARCHAR(10000), trackBitrate VARCHAR(10), trackComment VARCHAR(500), trackGenre VARCHAR(20), trackNumber VARCHAR(6), trackYear VARCHAR(6), playlistId INTEGER, starred BOOLEAN)");
        update(connection, "CREATE INDEX idx_PlaylistItems_filePath ON PlaylistItems (filePath)");
        update(connection, "CREATE INDEX idx_PlaylistItems_starred ON PlaylistItems (starred)");

        setupInternetRadioStationsTable(connection, true, false);

        setupLuceneIndex(connection);

        setupInternetRadioStationsTable(connection, false, true);

        // INITIAL DATA
        update(connection, "INSERT INTO Library (name , version) VALUES (?, ?)", name, LIBRARY_DATABASE_VERSION);

        return connection;
    }

    private Connection openOrCreateDatabase(File path, String name) {
        Connection connection = openConnection(path, name, false);
        if (connection == null) {
            return createDatabase(path, name);
        } else {
            int databaseVersion = getDatabaseVersion(connection);
            if (databaseVersion < LIBRARY_DATABASE_VERSION) {
                return onUpdateDatabase(connection, databaseVersion, LIBRARY_DATABASE_VERSION);
            } else {
                return connection;
            }
        }
    }

    private List<List<Object>> convertResultSetToList(ResultSet resultSet) throws SQLException {
        ResultSetMetaData meta = resultSet.getMetaData();
        int numColums = meta.getColumnCount();
        int i;

        List<List<Object>> result = new LinkedList<List<Object>>();

        while (resultSet.next()) {
            List<Object> row = new ArrayList<Object>(numColums);
            for (i = 1; i <= numColums; i++) {
                row.add(resultSet.getObject(i));
            }
            result.add(row);
        }
        return result;
    }

    private int getIdentity() {
        if (isClosed()) {
            return OBJECT_INVALID_ID;
        }

        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = _connection.createStatement();
            resultSet = statement.executeQuery("CALL IDENTITY()");

            resultSet.next();

            return resultSet.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
        }

        return OBJECT_INVALID_ID;
    }

    private List<List<Object>> query(Connection connection, String statementSql, Object... arguments) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(statementSql);

            if (arguments != null) {
                for (int i = 0; i < arguments.length; i++) {
                    statement.setObject(i + 1, arguments[i]);
                }
            }

            resultSet = statement.executeQuery();

            return convertResultSetToList(resultSet);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
        }

        return new ArrayList<List<Object>>();
    }

    private int update(Connection connection, String statementSql, Object... arguments) {

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(statementSql);

            if (arguments != null) {
                for (int i = 0; i < arguments.length; i++) {
                    statement.setObject(i + 1, arguments[i]);
                }
            }

            return statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
        }

        return -1;
    }

    private int getDatabaseVersion(Connection connection) {
        List<List<Object>> query = query(connection, "SELECT version FROM Library");
        return query.size() > 0 ? (Integer) query.get(0).get(0) : -1;
    }

    private void setupInternetRadioStationsTable(final Connection connection, boolean createTable, boolean loadData) {
        if (createTable) {
            update(connection,
                    "CREATE TABLE InternetRadioStations (internetRadioStationId INTEGER IDENTITY, name VARCHAR(10000), description VARCHAR(10000), url VARCHAR(10000), bitrate VARCHAR(100), type VARCHAR(100), website VARCHAR(10000), genre VARCHAR(10000), pls VARCHAR(100000), bookmarked BOOLEAN)");
            update(connection, "CREATE INDEX idx_InternetRadioStations_name ON InternetRadioStations (name)");
        }

        if (loadData) {
            new Thread(new Runnable() {
                public void run() {
                    InternetRadioStationsData data = new InternetRadioStationsData();

                    for (List<Object> row : data.getData()) {
                        update(connection, "INSERT INTO InternetRadioStations (name, description, url, bitrate, type, website, genre, pls, bookmarked) VALUES (LEFT(?, 10000), LEFT(?, 10000), LEFT(?, 10000), LEFT(?, 100), LEFT(?, 100), LEFT(?, 10000), LEFT(?, 10000), LEFT(?, 100000), false)",
                                row.toArray());
                        Thread.yield();
                    }
                }
            }, "Load Internet Radio Data").start();
        }
    }

    private void setupLuceneIndex(Connection connection) {
        update(connection, "CREATE ALIAS IF NOT EXISTS FTL_INIT FOR \"org.h2.fulltext.FullTextLucene.init\"");
        update(connection, "CALL FTL_INIT()");

        update(connection, "CALL FTL_CREATE_INDEX('PUBLIC', 'PLAYLISTITEMS', 'FILEPATH, TRACKTITLE, TRACKARTIST, TRACKALBUM, TRACKGENRE, TRACKYEAR')");
        update(connection, "CALL FTL_CREATE_INDEX('PUBLIC', 'INTERNETRADIOSTATIONS', 'NAME, DESCRIPTION, GENRE')");
    }
}

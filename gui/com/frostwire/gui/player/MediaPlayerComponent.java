/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.frostwire.gui.player;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.frostwire.gui.library.LibraryMediator;
import com.frostwire.gui.library.LibraryUtils;
import com.frostwire.mplayer.MediaPlaybackState;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.gui.MPlayerMediator;
import com.limegroup.gnutella.gui.MediaButton;
import com.limegroup.gnutella.gui.MediaSlider;
import com.limegroup.gnutella.gui.RefreshListener;
import com.limegroup.gnutella.gui.themes.SkinCustomUI;
import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.gui.themes.ThemeObserver;
import com.limegroup.gnutella.util.FrostWireUtils;
import com.limegroup.gnutella.util.Tagged;

/**
 * This class sets up JPanel with MediaPlayer on it, and takes care of GUI
 * MediaPlayer events.
 */
public final class MediaPlayerComponent implements MediaPlayerListener, RefreshListener, ThemeObserver {

    public static final String STREAMING_AUDIO = "Streaming Audio";

    /**
     * Constant for the play button.
     */
    private final MediaButton PLAY_BUTTON = new MediaButton(I18n.tr("Play"), "play_up", "play_dn");

    /**
     * Constant for the pause button.
     */
    private final MediaButton PAUSE_BUTTON = new MediaButton(I18n.tr("Pause"), "pause_up", "pause_dn");

    private JPanel PLAY_PAUSE_BUTTON_CONTAINER;

    /**
     * Constant for the forward button.
     */
    private final MediaButton NEXT_BUTTON = new MediaButton(I18n.tr("Next"), "forward_up", "forward_dn");

    /**
     * Constant for the rewind button.
     */
    private final MediaButton PREV_BUTTON = new MediaButton(I18n.tr("Previous"), "rewind_up", "rewind_dn");

    /**
     * Constant for the volume control
     */
    private final MediaSlider VOLUME = new MediaSlider("volume_labels");

    /**
     * Constant for the progress bar
     */
    private final JProgressBar PROGRESS = new JProgressBar();

    private final JLabel progressCurrentTime = new JLabel("--:--:--");

    private final JLabel progressSongLength = new JLabel("--:--:--");

    /**
     * The media player.
     */
    private final MediaPlayer PLAYER;

    /**
     * The ProgressBar dimensions for showing the name & play progress.
     */
    private final Dimension progressBarDimension = new Dimension(10, 5);

    /**
     * Volume slider dimensions for adjusting the audio level of a song
     */
    private final Dimension volumeSliderDimension = new Dimension(70, 19);

    /**
     * The current song that is playing
     */
    private MediaSource currentPlayListItem;

    /**
     * The lazily constructed media panel.
     */
    private JPanel myMediaPanel = null;

    private float _progress;

    private JToggleButton SHUFFLE_BUTTON;

    private JToggleButton LOOP_BUTTON;

    private CardLayout PLAY_PAUSE_CARD_LAYOUT;

    /**
     * Constructs a new <tt>MediaPlayerComponent</tt>.
     */
    public MediaPlayerComponent() {
        PLAYER = MediaPlayer.instance();
        PLAYER.addMediaPlayerListener(this);

        GUIMediator.addRefreshListener(this);
        ThemeMediator.addThemeObserver(this);

    }

    /**
     * Gets the media panel, constructing it if necessary.
     */
    public JPanel getMediaPanel(boolean showPlaybackModeControls) {
        if (myMediaPanel == null)
            myMediaPanel = constructMediaPanel();
        return myMediaPanel;
    }

    /**
     * Constructs the media panel.
     * 
     * @param showPlaybackModeControls
     */
    private JPanel constructMediaPanel() {
        int tempHeight = 1;

        // create sliders
        PROGRESS.setMinimumSize(progressBarDimension);
        //PROGRESS.setMaximumSize(progressBarDimension);
        PROGRESS.setPreferredSize(progressBarDimension);
        PROGRESS.setMaximum(3600);
        PROGRESS.setEnabled(false);

        VOLUME.setMaximumSize(volumeSliderDimension);
        VOLUME.setPreferredSize(volumeSliderDimension);
        VOLUME.setMinimum(0);
        VOLUME.setValue(50);
        VOLUME.setMaximum(100);
        VOLUME.setEnabled(true);
        VOLUME.setOpaque(false);

        // setup buttons
        registerListeners();

        // add everything
        JPanel panel = new JPanel();
        panel.putClientProperty(SkinCustomUI.CLIENT_PROPERTY_GRADIENT_BACKGROUND, ThemeMediator.CURRENT_THEME.getCustomUI().getPlayerBackground());
        panel.setLayout(new MigLayout("flowy", "[][][][][][grow][][]"));

        tempHeight = 80;
        panel.setMinimumSize(new Dimension(100, tempHeight));

        panel.add(PREV_BUTTON, "wrap, span 1 2");

        PLAY_PAUSE_CARD_LAYOUT = new CardLayout();
        PLAY_PAUSE_BUTTON_CONTAINER = new JPanel(PLAY_PAUSE_CARD_LAYOUT);
        PLAY_PAUSE_BUTTON_CONTAINER.setOpaque(false);

        PLAY_PAUSE_BUTTON_CONTAINER.add(PLAY_BUTTON, "PLAY");
        PLAY_PAUSE_BUTTON_CONTAINER.add(PAUSE_BUTTON, "PAUSE");
        panel.add(PLAY_PAUSE_BUTTON_CONTAINER, "wrap, span 1 2");

        panel.add(NEXT_BUTTON, "wrap, span 1 2");

        panel.add(new JSeparator(SwingConstants.VERTICAL), "wrap, span 1 2, growy");

        // set font for time labels.
        Font f = new Font(progressCurrentTime.getFont().getFontName(), Font.PLAIN, 10);

        progressCurrentTime.setForeground(ThemeMediator.CURRENT_THEME.getCustomUI().getLightForegroundColor());
        progressCurrentTime.setFont(f);

        progressSongLength.setForeground(ThemeMediator.CURRENT_THEME.getCustomUI().getLightForegroundColor());
        progressSongLength.setFont(f);

        Dimension timeLabelsDimension = new Dimension(45, 11);
        progressCurrentTime.setMinimumSize(timeLabelsDimension);
        progressCurrentTime.setPreferredSize(timeLabelsDimension);
        progressSongLength.setPreferredSize(timeLabelsDimension);
        progressSongLength.setMinimumSize(timeLabelsDimension);

        panel.add(progressCurrentTime, "wrap, span 1 2, aligny bottom");
        JLabel l = new JLabel("Test Test");
        l.setForeground(Color.WHITE);
        panel.add(l, "");
        panel.add(PROGRESS, "wrap, growx");
        panel.add(progressSongLength, "wrap, span 1 2, aligny bottom");

        panel.add(new JSeparator(SwingConstants.VERTICAL), "wrap, span 1 2, growy");

        initPlaylistPlaybackModeControls();
        panel.add(SHUFFLE_BUTTON, "");
        panel.add(VOLUME, "wrap, span 2 1");
        panel.add(LOOP_BUTTON, "");

        return panel;
    }

    public void initPlaylistPlaybackModeControls() {
        SHUFFLE_BUTTON = new JToggleButton();
        SHUFFLE_BUTTON.setBorderPainted(false);
        SHUFFLE_BUTTON.setContentAreaFilled(false);
        SHUFFLE_BUTTON.setBackground(null);
        SHUFFLE_BUTTON.setIcon(GUIMediator.getThemeImage("shuffle_off"));
        SHUFFLE_BUTTON.setSelectedIcon(GUIMediator.getThemeImage("shuffle_on"));
        SHUFFLE_BUTTON.setToolTipText(I18n.tr("Shuffle songs"));
        SHUFFLE_BUTTON.setSelected(PLAYER.isShuffle());

        LOOP_BUTTON = new JToggleButton();
        LOOP_BUTTON.setBorderPainted(false);
        LOOP_BUTTON.setContentAreaFilled(false);
        LOOP_BUTTON.setBackground(null);
        LOOP_BUTTON.setIcon(GUIMediator.getThemeImage("loop_off"));
        LOOP_BUTTON.setSelectedIcon(GUIMediator.getThemeImage("loop_on"));
        LOOP_BUTTON.setToolTipText(I18n.tr("Repeat songs"));
        LOOP_BUTTON.setSelected(PLAYER.getRepeatMode() == RepeatMode.All);

        SHUFFLE_BUTTON.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PLAYER.setShuffle(SHUFFLE_BUTTON.isSelected());
            }
        });

        LOOP_BUTTON.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PLAYER.setRepeatMode((LOOP_BUTTON.isSelected()) ? RepeatMode.All : RepeatMode.None);
            }
        });

    }

    private void showPauseButton() {
        PLAY_PAUSE_CARD_LAYOUT.show(PLAY_PAUSE_BUTTON_CONTAINER, "PAUSE");
    }

    private void showPlayButton() {
        PLAY_PAUSE_CARD_LAYOUT.show(PLAY_PAUSE_BUTTON_CONTAINER, "PLAY");
    }

    public void registerListeners() {
        PLAY_BUTTON.addActionListener(new PlayListener());
        PAUSE_BUTTON.addActionListener(new PauseListener());
        NEXT_BUTTON.addActionListener(new NextListener());
        PREV_BUTTON.addActionListener(new BackListener());
        VOLUME.addChangeListener(new VolumeSliderListener());
        PROGRESS.addMouseListener(new ProgressBarMouseAdapter());
    }

    public void unregisterListeners() {
        PLAY_BUTTON.removeActionListener(new PlayListener());
        PAUSE_BUTTON.removeActionListener(new PauseListener());
        NEXT_BUTTON.removeActionListener(new NextListener());
        PREV_BUTTON.removeActionListener(new BackListener());
        VOLUME.removeChangeListener(new VolumeSliderListener());
        PROGRESS.removeMouseListener(new ProgressBarMouseAdapter());
    }

    /**
     * Updates the audio player.
     */
    public void refresh() {
        PLAYER.refresh();
    }

    public void updateTheme() {
        PLAY_BUTTON.updateTheme();
        PAUSE_BUTTON.updateTheme();
        NEXT_BUTTON.updateTheme();
        PREV_BUTTON.updateTheme();
        VOLUME.updateTheme();
    }

    /**
     * Updates the current progress of the progress bar, on the Swing thread.
     */
    private void setProgressValue(final int update) {
        GUIMediator.safeInvokeLater(new Runnable() {
            public void run() {
                PROGRESS.setValue(update);
            }
        });
    }

    /**
     * Enables or disables the skipping action on the progress bar safely from
     * the swing event queue
     * 
     * @param enabled
     *            - true to allow skipping, false otherwise
     */
    private void setProgressEnabled(final boolean enabled) {
        GUIMediator.safeInvokeLater(new Runnable() {
            public void run() {
                PROGRESS.setEnabled(enabled);
            }
        });
        setProgressValue(0);
    }

    /**
     * Updates the volume based on the position of the volume slider
     */
    private void setVolumeValue() {
        VOLUME.repaint();
        PLAYER.setVolume(((float) VOLUME.getValue()) / VOLUME.getMaximum());
    }

    /**
     * Begins playing the loaded song
     */
    public void play() {

        if (PLAYER.getCurrentMedia() != null) {
            if (PLAYER.getState() == MediaPlaybackState.Paused || PLAYER.getState() == MediaPlaybackState.Playing) {
                PLAYER.togglePause();
            }
        } else {
            if (GUIMediator.instance().getSelectedTab() != null && GUIMediator.instance().getSelectedTab().equals(GUIMediator.Tabs.LIBRARY)) {
                LibraryMediator.instance().playCurrentSelection();
            }
        }
    }

    /**
     * Toggles full screen view
     */
    public void toggleFullScreen() {
        MPlayerMediator.instance().showPlayerWindow(true);
    }

    /**
     * Pauses the currently playing audio file.
     */
    public void pauseSong() {
        PLAYER.togglePause();
    }

    /**
     * Stops the currently playing audio file.
     */
    public void stopSong() {
        PLAYER.stop();
    }

    public void seek(float percent) {
        if (currentPlayListItem != null && currentPlayListItem.getURL() == null && PLAYER.canSeek()) {
            float timeInSecs = PLAYER.getDurationInSecs() * percent;
            PLAYER.seek(timeInSecs);
        }
    }

    /**
     * @return the current song that is playing, null if there is no song loaded
     *         or the song is streaming audio
     */
    public MediaSource getCurrentMedia() {
        return currentPlayListItem;
    }

    /**
     * This event is thrown everytime a new media is opened and is ready to be
     * played.
     */
    @Override
    public void mediaOpened(MediaPlayer mediaPlayer, MediaSource mediaSource) {
        currentPlayListItem = mediaSource;

        setVolumeValue();
        if (mediaSource.getURL() == null && PLAYER.canSeek()) {
            setProgressEnabled(true);
            progressSongLength.setText(LibraryUtils.getSecondsInDDHHMMSS((int) PLAYER.getDurationInSecs()));
        } else {
            setProgressEnabled(false);
            progressSongLength.setText("--:--:--");
        }
    }

    /**
     * This event is thrown a number of times a second. It updates the current
     * frames that have been read, along with position and bytes read
     */
    public void progressChange(MediaPlayer mediaPlayer, float currentTimeInSecs) {
        _progress = currentTimeInSecs;
        progressCurrentTime.setText(LibraryUtils.getSecondsInDDHHMMSS((int) _progress));

        if (currentPlayListItem != null && currentPlayListItem.getURL() == null) {
            progressSongLength.setText(LibraryUtils.getSecondsInDDHHMMSS((int) PLAYER.getDurationInSecs()));
        }

        if (currentPlayListItem != null && currentPlayListItem.getURL() == null && PLAYER.canSeek()) {
            setProgressEnabled(true);
            float progressUpdate = ((PROGRESS.getMaximum() * currentTimeInSecs) / PLAYER.getDurationInSecs());
            setProgressValue((int) progressUpdate);
        }
    }

    public void stateChange(MediaPlayer mediaPlayer, MediaPlaybackState state) {
        if (state == MediaPlaybackState.Opening) {
            setVolumeValue();
        } else if (state == MediaPlaybackState.Stopped || state == MediaPlaybackState.Closed) {
            setProgressValue(PROGRESS.getMinimum());
            progressCurrentTime.setText("--:--:--");
            progressSongLength.setText("--:--:--");
            showPlayButton();
        } else if (state == MediaPlaybackState.Playing) {
            showPauseButton();
        } else if (state == MediaPlaybackState.Paused) {
            showPlayButton();
        }
    }

    /**
     * Begins playing the loaded song in url of args.
     */
    String playSong(Map<String, String> args) {

        // Tagged<String> urlString = FrostWireUtils.getArg(args, "url",
        // "AddToPlaylist");
        // if (!urlString.isValid())
        // return urlString.getValue();
        // String url = urlString.getValue();
        //
        // // Find the song with this url
        // PlaylistMediator pl = GUIMediator.getPlayList();
        // List<PlayListItem> songs = pl.getSongs();
        // PlayListItem targetTrack = null;
        // for (PlayListItem it : songs) {
        // try {
        // String thatOne = URLDecoder.decode(it.getURI().toString());
        // String thisOne = URLDecoder.decode(url);
        // if (thatOne.equals(thisOne)) {
        // targetTrack = it;
        // break;
        // }
        // } catch (IOException e) {
        // // ignore
        // }
        // }
        //
        // if (targetTrack != null) {
        // loadSong(targetTrack);
        // return "ok";
        // }
        //
        // if (PLAYER.getStatus() == MediaPlaybackState.Paused ||
        // PLAYER.getStatus() == MediaPlaybackState.Playing)
        // PLAYER.unpause();
        // else {
        // loadSong(currentPlayListItem);
        // }

        return "ok";
    }

    /**
     * Returns "ok" on success and a failure message on failure after taking an
     * index into the playlist and remove it.
     * 
     * @param index
     *            index of the item to remove
     * @return "ok" on success and a failure message on failure after taking an
     *         index into the playlist and remove it;
     */
    String removeFromPlaylist(int index) {
        // PlaylistMediator pl = GUIMediator.getPlayList();
        // if (pl.removeFileFromPlaylist(index)) {
        // return "ok";
        // }
        return "invalid.index: " + index;
    }

    /**
     * Returns "ok" on success and a failure message on failure after taking an
     * index into the playlist and remove it.
     * 
     * @param index
     *            index of the item to remove
     * @return "ok" on success and a failure message on failure after taking an
     *         index into the playlist and remove it;
     */
    String playIndexInPlaylist(int index) {
        // PlaylistMediator pl = GUIMediator.getPlayList();
        // if (pl.removeFileFromPlaylist(index)) {
        // return "ok";
        // }
        return "invalid.index: " + index;
    }

    /**
     * @return <code>PROGRESS.getValue() + "\t" + PROGRESS.getMaximum()</code>
     *         or <code>"stopped"</code> if we're not playing
     */
    String getProgress() {
        String res;
        if (isPlaying()) {
            int secs = PROGRESS.getValue();
            int length = PROGRESS.getMaximum();
            System.out.println(secs + ":" + length);
            res = secs + "\t" + length;
        } else {
            res = "stopped";
        }
        return res.toString();
    }

    String addToPlaylist(Map<String, String> args) {

        Tagged<String> urlString = FrostWireUtils.getArg(args, "url", "AddToPlaylist");
        if (!urlString.isValid())
            return urlString.getValue();

        Tagged<String> nameString = FrostWireUtils.getArg(args, "name", "AddtoPlaylist");
        if (!nameString.isValid())
            return nameString.getValue();

        Tagged<String> lengthString = FrostWireUtils.getArg(args, "length", "AddtoPlaylist");
        if (!lengthString.isValid())
            return lengthString.getValue();

        Tagged<String> artistString = FrostWireUtils.getArg(args, "artist", "AddtoPlaylist");
        if (!artistString.isValid())
            return artistString.getValue();

        Tagged<String> albumString = FrostWireUtils.getArg(args, "album", "AddtoPlaylist");
        if (!albumString.isValid())
            return albumString.getValue();

        // We won't accept full URLs
        // String baseDir = "http://riaa.com";
        // int port = 0;
        // if (port > 0) {
        // baseDir += ":" + port;
        // }

        // String url = baseDir + urlString.getValue();
        // try {
        // String decodedURL = URLDecoder.decode(url);
        // URL u = new URL(decodedURL);
        // PlayListItem song = new PlayListItem(u.toURI(), new AudioSource(u),
        // nameString.getValue(), false);
        // GUIMediator.instance().launchAudio(song);
        // } catch (IOException e) {
        // ErrorService.error(e, "invalid URL:" + url);
        // return "ERROR:invalid.url:" + url;
        // } catch (URISyntaxException e) {
        // ErrorService.error(e, "invalid URL:" + url);
        // return "ERROR:invalid.url:" + url;
        // }
        return "ok";
    }

    String playURL(Map<String, String> args) {

        Tagged<String> urlString = FrostWireUtils.getArg(args, "url", "PlayURL");
        if (!urlString.isValid())
            return urlString.getValue();

        // We won't accept full URLs
        // String baseDir = "http://riaa.com";
        // int port = 0;
        // if (port > 0) {
        // baseDir += ":" + port;
        // }

        // String url = baseDir + urlString.getValue();
        // String name = getName(url);
        // try {
        // String decodedURL = URLDecoder.decode(url);
        // URL u = new URL(decodedURL);
        // PlayListItem song = new PlayListItem(u.toURI(), new AudioSource(u),
        // name, false);
        // GUIMediator.instance().launchAudio(song);
        // } catch (IOException e) {
        // ErrorService.error(e, "invalid URL:" + url);
        // return "ERROR:invalid.url:" + url;
        // } catch (URISyntaxException e) {
        // ErrorService.error(e, "invalid URL:" + url);
        // return "ERRORinvalid.url:" + url;
        // }
        return "ok";
    }

    // private String getName(String url) {
    // int ilast = url.lastIndexOf('/');
    // if (ilast == -1) {
    // ilast = url.lastIndexOf('\\');
    // }
    // if (ilast == -1) {
    // return url;
    // }
    // return url.substring(ilast + 1);
    // }

    private boolean isPlaying() {
        return !(PLAYER.getState() == MediaPlaybackState.Stopped || PLAYER.getState() == MediaPlaybackState.Uninitialized || PLAYER.getState() == MediaPlaybackState.Paused || PLAYER.getState() == MediaPlaybackState.Failed);
    }

    /**
     * Attempts to stop a song if its playing any song
     * 
     * Returns true if it actually stopped, false if there was no need to do so.
     * 
     * */
    public boolean attemptStop() {

        if (PLAYER.getState() != MediaPlaybackState.Stopped) {
            PLAYER.stop();
            return true;
        }

        return false;
    }

    /**
     * Disables the Volume Slider, And Pause Button
     */
    public void disableControls() {
        VOLUME.setEnabled(false);
        PAUSE_BUTTON.setEnabled(false);
    }

    /**
     * Enables the Volume Slider, And Pause Button
     * 
     */
    public void enableControls() {
        VOLUME.setEnabled(true);
        PAUSE_BUTTON.setEnabled(true);
    }

    /**
     * Listens for the play button being pressed.
     */
    private class PlayListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            play();
        }
    }

    /**
     * Listens for the next button being pressed.
     */
    private class NextListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            next();
        }
    }

    private void next() {
        MediaSource currentMedia = PLAYER.getCurrentMedia();

        if (currentMedia != null) {
            MediaSource nextSong = null;

            if (PLAYER.isShuffle()) {
                nextSong = PLAYER.getNextRandomSong(currentMedia);
            } else {
                nextSong = PLAYER.getNextMedia(currentMedia);
            }

            if (nextSong != null) {
                PLAYER.asyncLoadMedia(nextSong, true, true);
            }
        }
    }

    /**
     * Listens for the back button being pressed.
     */
    private class BackListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            back();
        }
    }

    private void back() {
        MediaSource currentMedia = PLAYER.getCurrentMedia();

        if (currentMedia != null) {
            MediaSource previousSong = PLAYER.getPreviousMedia(currentMedia);

            if (previousSong != null) {
                PLAYER.asyncLoadMedia(previousSong, true, true);
            }
        }
    }

    /**
     * Listens for the pause button being pressed.
     */
    private class PauseListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            pauseSong();
        }
    }

    /**
     * This listener is added to the progressbar to process when the user has
     * skipped to a new part of the song with a mouse
     */
    private class ProgressBarMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            seek(e.getX() * 1.0f / ((Component) e.getSource()).getWidth());
        }
    }

    /**
     * This listener is added to the volume slider to process whene the user has
     * adjusted the volume of the audio player
     */
    private class VolumeSliderListener implements ChangeListener {
        /**
         * If the user moved the thumb, adjust the volume of the player
         */
        public void stateChanged(ChangeEvent e) {
            setVolumeValue();
        }
    }

    @Override
    public void volumeChange(MediaPlayer mediaPlayer, double currentVolume) {
        VolumeSliderListener oldListener = (VolumeSliderListener) VOLUME.getChangeListeners()[0];
        VOLUME.removeChangeListener(oldListener);
        VOLUME.setValue((int) (VOLUME.getMaximum() * currentVolume));
        VOLUME.addChangeListener(oldListener);
    }

    @Override
    public void icyInfo(MediaPlayer mediaPlayer, String data) {

    }
}
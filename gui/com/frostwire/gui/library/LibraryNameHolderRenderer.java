/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011, 2012, FrostWire(R). All rights reserved.
 *
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

package com.frostwire.gui.library;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.TableUI;
import javax.swing.table.TableCellRenderer;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.animation.StateTransitionTracker;
import org.pushingpixels.substance.internal.animation.StateTransitionTracker.StateContributionInfo;
import org.pushingpixels.substance.internal.ui.SubstanceTableUI;
import org.pushingpixels.substance.internal.ui.SubstanceTableUI.TableCellId;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceStripingUtils;
import org.pushingpixels.substance.internal.utils.UpdateOptimizationInfo;
import org.pushingpixels.substance.internal.utils.border.SubstanceTableCellBorder;

import com.frostwire.gui.player.MediaSource;
import com.frostwire.gui.player.DeviceMediaSource;
import com.frostwire.gui.player.InternetRadioAudioSource;
import com.frostwire.gui.player.MediaPlayer;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.themes.SkinTableCellRenderer;
import com.limegroup.gnutella.gui.themes.ThemeSettings;

/**
 * 
 * @author gubatron
 * @author aldenml
 * 
 */
public final class LibraryNameHolderRenderer extends JPanel implements TableCellRenderer, SkinTableCellRenderer {

    private static final long serialVersionUID = -1624943333769190212L;

    private static final Logger LOG = Logger.getLogger(LibraryNameHolderRenderer.class.getName());

    private JLabel labelText;
    private JLabel labelPlay;
    private JLabel labelDownload;

    private LibraryNameHolder libraryNameHolder;

    public LibraryNameHolderRenderer() {
        setupUI();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        //if (!SubstanceLookAndFeel.isCurrentLookAndFeel())
        //    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        TableUI tableUI = table.getUI();
        SubstanceTableUI ui = (SubstanceTableUI) tableUI;

        // Recompute the focus indication to prevent flicker - JTable
        // registers a listener on selection changes and repaints the
        // relevant cell before our listener (in TableUI) gets the
        // chance to start the fade sequence. The result is that the
        // first frame uses full opacity, and the next frame starts the
        // fade sequence. So, we use the UI delegate to compute the
        // focus indication.
        hasFocus = ui.isFocusedCell(row, column);

        TableCellId cellId = new TableCellId(row, column);

        StateTransitionTracker.ModelStateInfo modelStateInfo = ui.getModelStateInfo(cellId);
        ComponentState currState = ui.getCellState(cellId);
        // special case for drop location
        JTable.DropLocation dropLocation = table.getDropLocation();
        boolean isDropLocation = (dropLocation != null && !dropLocation.isInsertRow() && !dropLocation.isInsertColumn() && dropLocation.getRow() == row && dropLocation.getColumn() == column);

        if (!isDropLocation && (modelStateInfo != null)) {
            if (ui.hasRolloverAnimations() || ui.hasSelectionAnimations()) {
                Map<ComponentState, StateContributionInfo> activeStates = modelStateInfo.getStateContributionMap();
                SubstanceColorScheme colorScheme = getColorSchemeForState(table, ui, currState);
                if (currState.isDisabled() || (activeStates == null) || (activeStates.size() == 1)) {
                    super.setForeground(new ColorUIResource(colorScheme.getForegroundColor()));
                } else {
                    float aggrRed = 0;
                    float aggrGreen = 0;
                    float aggrBlue = 0;
                    for (Map.Entry<ComponentState, StateTransitionTracker.StateContributionInfo> activeEntry : modelStateInfo.getStateContributionMap().entrySet()) {
                        ComponentState activeState = activeEntry.getKey();
                        SubstanceColorScheme scheme = getColorSchemeForState(table, ui, activeState);
                        Color schemeFg = scheme.getForegroundColor();
                        float contribution = activeEntry.getValue().getContribution();
                        aggrRed += schemeFg.getRed() * contribution;
                        aggrGreen += schemeFg.getGreen() * contribution;
                        aggrBlue += schemeFg.getBlue() * contribution;
                    }
                    super.setForeground(new ColorUIResource(new Color((int) aggrRed, (int) aggrGreen, (int) aggrBlue)));
                }
            } else {
                SubstanceColorScheme scheme = getColorSchemeForState(table, ui, currState);
                super.setForeground(new ColorUIResource(scheme.getForegroundColor()));
            }
        } else {
            SubstanceColorScheme scheme = getColorSchemeForState(table, ui, currState);
            if (isDropLocation) {
                scheme = SubstanceColorSchemeUtilities.getColorScheme(table, ColorSchemeAssociationKind.TEXT_HIGHLIGHT, currState);
            }
            super.setForeground(new ColorUIResource(scheme.getForegroundColor()));
        }

        SubstanceStripingUtils.applyStripedBackground(table, row, this);

        this.setFont(table.getFont());

        TableCellId cellFocusId = new TableCellId(row, column);

        StateTransitionTracker focusStateTransitionTracker = ui.getStateTransitionTracker(cellFocusId);

        Insets regInsets = ui.getCellRendererInsets();
        if (hasFocus || (focusStateTransitionTracker != null)) {
            SubstanceTableCellBorder border = new SubstanceTableCellBorder(regInsets, ui, cellFocusId);

            // System.out.println("[" + row + ":" + column + "] hasFocus : "
            // + hasFocus + ", focusState : " + focusState);
            if (focusStateTransitionTracker != null) {
                border.setAlpha(focusStateTransitionTracker.getFocusStrength(hasFocus));
            }

            // special case for tables with no grids
            if (!table.getShowHorizontalLines() && !table.getShowVerticalLines()) {
                this.setBorder(new CompoundBorder(new EmptyBorder(table.getRowMargin() / 2, 0, table.getRowMargin() / 2, 0), border));
            } else {
                this.setBorder(border);
            }
        } else {
            this.setBorder(new EmptyBorder(regInsets.top, regInsets.left, regInsets.bottom, regInsets.right));
        }

        this.setData((LibraryNameHolder) value, currState, table, row, column);
        this.setOpaque(false);
        this.setEnabled(table.isEnabled());
        return this;
    }

    private SubstanceColorScheme getColorSchemeForState(JTable table, SubstanceTableUI ui, ComponentState state) {
        UpdateOptimizationInfo updateOptimizationInfo = ui.getUpdateOptimizationInfo();
        if (state == ComponentState.ENABLED) {
            if (updateOptimizationInfo == null) {
                return SubstanceColorSchemeUtilities.getColorScheme(table, state);
            } else {
                return updateOptimizationInfo.getDefaultScheme();
            }
        } else {
            if (updateOptimizationInfo == null) {
                return SubstanceColorSchemeUtilities.getColorScheme(table, ColorSchemeAssociationKind.HIGHLIGHT, state);
            } else {
                return updateOptimizationInfo.getHighlightColorScheme(state);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public final void paint(Graphics g) {
        super.paint(g);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected final void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    protected final void paintBorder(Graphics g) {
        super.paintBorder(g);
    }

    @Override
    public final void paintComponents(Graphics g) {
        super.paintComponents(g);
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, 1.0);

        GridBagConstraints c;

        labelText = new JLabel();
        labelText.setHorizontalTextPosition(SwingConstants.LEFT);
        c = new GridBagConstraints();
        c.gridx = GridBagConstraints.RELATIVE;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(labelText, c);

        labelPlay = new JLabel(GUIMediator.getThemeImage("search_result_play_over"));
        labelPlay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                labelPlay_mouseReleased(e);
            }
        });
        c = new GridBagConstraints();
        c.gridx = GridBagConstraints.RELATIVE;
        c.ipadx = 1;
        add(labelPlay, c);

        labelDownload = new JLabel(GUIMediator.getThemeImage("search_result_download_over"));
        labelDownload.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                labelDownload_mouseReleased(e);
            }
        });
        c = new GridBagConstraints();
        c.gridx = GridBagConstraints.RELATIVE;
        c.ipadx = 1;
        add(labelDownload, c);
    }

    private void labelPlay_mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (libraryNameHolder != null && libraryNameHolder.getDataLine() != null) {

                MediaSource mediaSource = null;
                List<MediaSource> filesView = null;
                boolean playNextSong = false;
                Object dataLine = libraryNameHolder.getDataLine();

                if (dataLine instanceof LibraryFilesTableDataLine) {
                    mediaSource = new MediaSource(((LibraryFilesTableDataLine) dataLine).getFile());
                    filesView = LibraryFilesTableMediator.instance().getFilesView();
                    playNextSong = true;
                } else if (dataLine instanceof LibraryPlaylistsTableDataLine) {
                    mediaSource = new MediaSource(((LibraryPlaylistsTableDataLine) dataLine).getPlayListItem());
                    filesView = LibraryPlaylistsTableMediator.instance().getFilesView();
                    playNextSong = true;
                } else if (dataLine instanceof LibraryInternetRadioTableDataLine) {
                    LibraryInternetRadioTableDataLine irDataLine = (LibraryInternetRadioTableDataLine) dataLine;
                    mediaSource = new InternetRadioAudioSource(irDataLine.getInitializeObject().getUrl(), irDataLine.getInitializeObject());
                    filesView = LibraryInternetRadioTableMediator.instance().getFilesView();
                    playNextSong = false;
                } else if (dataLine instanceof LibraryDeviceTableDataLine) {
                    LibraryDeviceTableDataLine dl = (LibraryDeviceTableDataLine) dataLine;
                    Device device = LibraryMediator.instance().getLibraryExplorer().getSelectedDeviceFiles();
                    if (device != null) {
                        String url = device.getDownloadURL(dl.getInitializeObject());
                        mediaSource = new DeviceMediaSource(url, device, dl.getInitializeObject());
                        filesView = LibraryDeviceTableMediator.instance().getFilesView();
                        playNextSong = true;
                    }
                }

                if (mediaSource != null && !isSourceBeingPlayed()) {
                    labelPlay.setVisible(false);
                    MediaPlayer.instance().asyncLoadMedia(mediaSource, true, playNextSong, null, filesView);
                }
            }
        }
    }

    private void labelDownload_mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (libraryNameHolder != null && libraryNameHolder.getDataLine() != null) {
                Object dataLine = libraryNameHolder.getDataLine();

                if (dataLine instanceof LibraryDeviceTableDataLine) {
                    Device device = LibraryMediator.instance().getLibraryExplorer().getSelectedDeviceFiles();
                    if (device != null) {
                        LibraryDeviceTableMediator.instance().downloadSelectedItems();
                    }
                }
            }
        }
    }

    private void setData(LibraryNameHolder value, ComponentState state, JTable table, int row, int column) {
        try {
            libraryNameHolder = value;
            labelText.setText(value.toString());
            boolean showButtons = state.equals(ComponentState.ROLLOVER_SELECTED) || state.equals(ComponentState.ROLLOVER_UNSELECTED);
            labelPlay.setVisible(showButtons && !isSourceBeingPlayed() && isPlayableDataLine());
            labelDownload.setVisible(showButtons && isDownloadableFromOtherDevice());
            setFontColor(libraryNameHolder.isPlaying(), table, row, column);
        } catch (Throwable e) {
            LOG.warning("Error puting data in name holder renderer");
        }
    }

    private boolean isDownloadableFromOtherDevice() {
        boolean result = false;
        if (libraryNameHolder != null && libraryNameHolder.getDataLine() != null) {
            Object dataLine = libraryNameHolder.getDataLine();

            if (dataLine instanceof LibraryDeviceTableDataLine) {

                Device device = LibraryMediator.instance().getLibraryExplorer().getSelectedDeviceFiles();
                if (device != null && !device.isLocal()) {
                    result = true;
                }
            }
        }
        return result;

    }

    private boolean isPlayableDataLine() {
        Object dl = libraryNameHolder.getDataLine();
        if (dl instanceof LibraryFilesTableDataLine) {
            return MediaPlayer.isPlayableFile(((LibraryFilesTableDataLine) dl).getFile());
        } else if (dl instanceof LibraryPlaylistsTableDataLine) {
            return true;
        } else if (dl instanceof LibraryInternetRadioTableDataLine) {
            return true;
        } else if (dl instanceof LibraryDeviceTableDataLine) {
            return MediaPlayer.isPlayableFile(((LibraryDeviceTableDataLine) dl).getInitializeObject().filePath);
        } else {
            return false;
        }
    }

    private boolean isSourceBeingPlayed() {
        if (libraryNameHolder == null) {
            return false;
        }

        return libraryNameHolder.isPlaying();
    }

    /**
     * Check what font color to use if this song is playing. 
     */
    private void setFontColor(boolean isPlaying, JTable table, int row, int column) {
        if (!libraryNameHolder.isExists()) {
            setForeground(ThemeSettings.FILE_NO_EXISTS_DATA_LINE_COLOR.getValue());
        } else if (isPlaying) {
            labelText.setForeground(ThemeSettings.PLAYING_DATA_LINE_COLOR.getValue());
        } else {
            Color color = Color.BLACK;
            if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
                color = getSubstanceForegroundColor(table, row, column);
            } else {
                color = UIManager.getColor("Table.foreground");
            }

            labelText.setForeground(color);
        }
    }

    private Color getSubstanceForegroundColor(JTable table, int row, int column) {
        TableUI tableUI = table.getUI();
        SubstanceTableUI ui = (SubstanceTableUI) tableUI;
        TableCellId cellId = new TableCellId(row, column);
        ComponentState currState = ui.getCellState(cellId);

        SubstanceColorScheme scheme = getColorSchemeForState(table, ui, currState);

        return scheme.getForegroundColor();
    }
}
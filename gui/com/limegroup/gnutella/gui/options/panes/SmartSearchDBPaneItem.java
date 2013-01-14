package com.limegroup.gnutella.gui.options.panes;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.gui.search.LocalSearchEngine;
import com.limegroup.gnutella.settings.SearchSettings;


public final class SmartSearchDBPaneItem extends AbstractPaneItem {

    public final static String TITLE = I18n.tr("Smart Search");
    
    public final static String LABEL = I18n.tr("The Smart Search database is used to speed up individual file searches, it's how FrostWire remembers information about .torrent contents.");

	private JLabel _numTorrentsLabel;
	private JLabel _numFilesLabel;
	
	private JCheckBox smartSearchEnabled;
	
	private long _numTorrents = 0;
	private long _numFiles = 0;
 

    /**
     * The constructor constructs all of the elements of this 
     * <tt>AbstractPaneItem</tt>.
     *
     * @param key the key for this <tt>AbstractPaneItem</tt> that the
     *            superclass uses to generate locale-specific keys
     */
    public SmartSearchDBPaneItem() {
        super(TITLE, LABEL);

        Font font = new Font("dialog",Font.BOLD,12);
        _numTorrentsLabel = new JLabel();
        _numFilesLabel = new JLabel();
        
        _numTorrentsLabel.setFont(font);
        _numFilesLabel.setFont(font);
        
        smartSearchEnabled = new JCheckBox(I18n.tr("Enable Smart Search"), SearchSettings.SMART_SEARCH_ENABLED.getValue());
        
        LabeledComponent numTorrentsComp = new LabeledComponent(I18n.tr("Total torrents indexed"), _numTorrentsLabel);
        LabeledComponent numFilesComp = new LabeledComponent(I18n.tr("Total files indexed"), _numFilesLabel);
        
        add(getVerticalSeparator());
        
        JButton resetButton = new JButton("Reset Smart Search Database");
        resetButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GUIMediator.safeInvokeLater(new Runnable() {
					@Override
					public void run() {
						resetSmartSearchDB();
						initOptions();
					}
				});
			};
		});
        
        
        add(smartSearchEnabled);

        add(getVerticalSeparator());
        
        add(numTorrentsComp.getComponent());
        add(numFilesComp.getComponent());
        
        add(getVerticalSeparator());
        
        add(resetButton);
    }
    
    protected void resetSmartSearchDB() {
        int showConfirmDialog = JOptionPane.showConfirmDialog(
                GUIMediator.getAppFrame(),
                I18n.tr("If you continue you will erase all the information related to\n" + _numTorrents + " torrents and " + _numFiles
                        + " files that FrostWire has learned to speed up your search results.\nDo you wish to continue?"), I18n.tr("Are you sure?"), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (showConfirmDialog == JOptionPane.YES_OPTION) {
            LocalSearchEngine.instance().resetDB();
        }
    }

	/**
     * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
     *
     * Sets the options for the fields in this <tt>PaneItem</tt> when the 
     * window is shown.
     */
    public void initOptions() {
        _numTorrents = LocalSearchEngine.instance().getTotalTorrents();
        _numTorrentsLabel.setText(String.valueOf(_numTorrents));

        _numFiles = LocalSearchEngine.instance().getTotalFiles();
        _numFilesLabel.setText(String.valueOf(_numFiles));
        
        smartSearchEnabled.setSelected(SearchSettings.SMART_SEARCH_ENABLED.getValue());
    }
    
    /**
     * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
     *
     * Applies the options currently set in this window, displaying an
     * error message to the user if a setting could not be applied.
     *
     * @throws IOException if the options could not be applied for some reason
     */
    public boolean applyOptions() throws IOException {
        SearchSettings.SMART_SEARCH_ENABLED.setValue(smartSearchEnabled.isSelected());
        return true;
    }
    
    public boolean isDirty() {
        return false;
    }
}

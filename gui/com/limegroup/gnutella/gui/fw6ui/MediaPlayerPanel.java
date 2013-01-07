package com.limegroup.gnutella.gui.fw6ui;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicPanelUI;

import com.limegroup.gnutella.gui.fw6ui.mediaplayerpanel.MPButtonPanel;
import com.limegroup.gnutella.gui.fw6ui.mediaplayerpanel.MPProgressPanel;
import com.limegroup.gnutella.gui.fw6ui.mediaplayerpanel.MPUtils;
import com.limegroup.gnutella.gui.fw6ui.mediaplayerpanel.MPVolumePanel;


public class MediaPlayerPanel extends JPanel {
	
	private static final Color BACKGROUND_COLOR = new Color(58, 103, 141, 255);
	
	private static final long serialVersionUID = -1858398833731590955L;
	
	public MediaPlayerPanel() {
		
		initializeUI();
	}
	
	private void initializeUI() {		
		setBackground(BACKGROUND_COLOR);
		setOpaque(true);
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		add(new MPButtonPanel());
		add(MPUtils.createSeparator(SwingConstants.VERTICAL));
		add(new MPProgressPanel());
		add(MPUtils.createSeparator(SwingConstants.VERTICAL));
		add(new MPVolumePanel());
	}

}

package com.limegroup.gnutella.gui.fw6ui.mediaplayerpanel;


import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.frostwire.gui.mplayer.ProgressSlider;
import com.frostwire.gui.mplayer.ProgressSliderListener;
import com.frostwire.gui.player.MPlayerUIEventHandler;
import com.frostwire.gui.player.MediaPlayer;
import com.frostwire.gui.player.MediaPlayerAdapter;
import com.frostwire.gui.player.MediaSource;

public class MPProgressPanel extends JPanel {

	private static final long serialVersionUID = 2626046650466778244L;

	private ProgressSlider progressSlider = null;
	private JLabel currentMediaText = null;
	
	public MPProgressPanel() {
		
		initializeUI();
		
		MediaPlayer.instance().addMediaPlayerListener(new MediaPlayerAdapter() {
			@Override public void progressChange(MediaPlayer mediaPlayer, float currentTimeInSecs) {
				progressSlider.setTotalTime(mediaPlayer.getDurationInSecs());
				progressSlider.setCurrentTime(currentTimeInSecs);
			}
			@Override public void mediaOpened(MediaPlayer mediaPlayer, MediaSource mediaSource) {
				setMediaText(mediaPlayer);
			}
		});
	}
	
	private void initializeUI() {
		setLayout(new BorderLayout());
		setOpaque(false);
		setBorder( BorderFactory.createEmptyBorder(12,0,12,0));
		
		// wrapper panel
		JPanel wrapper = new JPanel();
		wrapper.setLayout( new BoxLayout(wrapper, BoxLayout.PAGE_AXIS));
		wrapper.setOpaque(false);
		
		// title label
		currentMediaText = new JLabel();
        currentMediaText.setFont(new Font("DIALOG", Font.PLAIN, 12));
        wrapper.add(currentMediaText, BorderLayout.PAGE_START);
        clearMediaText();
		
		// progress slider
		progressSlider = new ProgressSlider();
		progressSlider.addProgressSliderListener(new ProgressSliderListener() {
			@Override public void onProgressSliderTimeValueChange(float seconds) {
				MPlayerUIEventHandler.instance().onSeekToTime(seconds);
			}
			@Override public void onProgressSliderMouseDown() {
				MPlayerUIEventHandler.instance().onProgressSlideStart();
			}
			@Override public void onProgressSliderMouseUp() {
				MPlayerUIEventHandler.instance().onProgressSlideEnd();
			}
		});
		
		wrapper.add(progressSlider, BorderLayout.CENTER);
		add(wrapper);
	}
	
	private void setMediaText(MediaPlayer mediaPlayer) {
		
		MediaSource source = mediaPlayer.getCurrentMedia();
		
		currentMediaText.setText(source.getTitleText());
		currentMediaText.setToolTipText(source.getToolTipText());
	}
	
	private void clearMediaText() {
		currentMediaText.setText(" ");
		currentMediaText.setToolTipText("");
	}
}

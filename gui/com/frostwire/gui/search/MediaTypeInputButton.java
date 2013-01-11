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

package com.frostwire.gui.search;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandMenuButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.common.popup.JCommandPopupMenu;
import org.pushingpixels.flamingo.api.common.popup.JPopupPanel;
import org.pushingpixels.flamingo.api.common.popup.PopupPanelCallback;
import org.pushingpixels.substance.internal.utils.SubstanceTextUtilities;

import com.frostwire.gui.filters.TableLineFilter;
import com.limegroup.gnutella.MediaType;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.gui.search.MediaTypeFilter;
import com.limegroup.gnutella.gui.search.NamedMediaType;
import com.limegroup.gnutella.gui.search.SearchMediator;
import com.limegroup.gnutella.gui.search.SearchResultDataLine;
import com.limegroup.gnutella.gui.search.SearchResultMediator;
import com.limegroup.gnutella.settings.SearchSettings;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public class MediaTypeInputButton extends JCommandButton {

    private static final String IMAGE_RESOURCE_PATH = "org/limewire/xml/image/";

    /**
     * The property that the media type is stored in.
     */
    private static final String NAMED_MEDIA_TYPE = "NAMED_MEDIA_TYPE";

    private JCommandPopupMenu menu;

    public MediaTypeInputButton() {
        super(I18n.tr("Media"));

        menu = new JCommandPopupMenu();
        addSchemas(menu);

        setPopupCallback(new PopupPanelCallback() {
            @Override
            public JPopupPanel getPopupPanel(JCommandButton commandButton) {
                JCommandPopupMenu menu = new JCommandPopupMenu();
                addSchemas(menu);
                return menu;
            }
        });
        setCommandButtonKind(JCommandButton.CommandButtonKind.POPUP_ONLY);

        putClientProperty(SubstanceTextUtilities.ENFORCE_FG_COLOR, true);
        setForeground(Color.WHITE);
    }

    private void addSchemas(JCommandPopupMenu menu) {
        NamedMediaType nmt;

        nmt = NamedMediaType.getFromDescription(MediaType.SCHEMA_AUDIO);
        addMediaType(menu, nmt, I18n.tr("Search For Audio Files, Including mp3, wav, ogg, and More"));

        nmt = NamedMediaType.getFromDescription(MediaType.SCHEMA_VIDEO);
        addMediaType(menu, nmt, I18n.tr("Search For Video Files, Including avi, mpg, wmv, and More"));

        nmt = NamedMediaType.getFromDescription(MediaType.SCHEMA_IMAGES);
        addMediaType(menu, nmt, I18n.tr("Search For Image Files, Including jpg, gif, png and More"));

        nmt = NamedMediaType.getFromDescription(MediaType.SCHEMA_PROGRAMS);
        addMediaType(menu, nmt, I18n.tr("Search for Program Files, Including exe, zip, gz, and More"));

        nmt = NamedMediaType.getFromDescription(MediaType.SCHEMA_DOCUMENTS);
        addMediaType(menu, nmt, I18n.tr("Search for Document Files, Including html, txt, pdf, and More"));

        nmt = NamedMediaType.getFromDescription(MediaType.SCHEMA_TORRENTS);
        addMediaType(menu, nmt, I18n.tr("Search for Torrents!"));
    }

    private void addMediaType(JCommandPopupMenu menu, NamedMediaType nmt, String toolTip) {
        final JCommandMenuButton button = new JCommandMenuButton(nmt.getName(), getIcon(nmt));

        button.putClientProperty(NAMED_MEDIA_TYPE, nmt);
        button.setPopupRichTooltip(new RichTooltip(nmt.getName(), toolTip));

        if (SearchSettings.LAST_MEDIA_TYPE_USED.getValue().contains(nmt.getMediaType().getMimeType())) {
            // button.setSelected(true);
            //lastSelectedButton = button;
            setText(nmt.getName());
        }

        if (SearchSettings.LAST_MEDIA_TYPE_USED.getValue().isEmpty() && nmt.getMediaType().equals(MediaType.getAudioMediaType())) {
            //button.setSelected(true);
            setText(nmt.getName());
        }

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onMediaTypeChanged(button);
            }
        });

        menu.addMenuButton(button);
    }

    protected void onMediaTypeChanged(JCommandMenuButton button) {
        NamedMediaType nmt = (NamedMediaType) button.getClientProperty(NAMED_MEDIA_TYPE);

        String mimeType = nmt.getMediaType().getMimeType();
        SearchSettings.LAST_MEDIA_TYPE_USED.setValue(mimeType);

        //        if (lastSelectedButton != null) {
        //            lastSelectedButton.setSelected(false);
        //        }
        //
        //        lastSelectedButton = button;
        //        lastSelectedButton.setSelected(true);
        setText(nmt.getName());

        updateSearchResults(new MediaTypeFilter());
    }

    private void updateSearchResults(TableLineFilter<SearchResultDataLine> filter) {
        List<SearchResultMediator> resultPanels = SearchMediator.getSearchResultDisplayer().getResultPanels();
        for (SearchResultMediator resultPanel : resultPanels) {
            resultPanel.filterChanged(filter, 2);
        }
    }

    private ResizableIcon getIcon(NamedMediaType nmt) {
        String path = IMAGE_RESOURCE_PATH + nmt.getMediaType().getMimeType() + ".png";
        URL url = getClass().getClassLoader().getResource(path);
        return ImageWrapperResizableIcon.getIcon(url, new Dimension(64, 64));
    }
}

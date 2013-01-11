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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.frostwire.gui.components.GoogleSearchField;
import com.limegroup.gnutella.MediaType;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.gui.actions.FileMenuActions;
import com.limegroup.gnutella.gui.search.SearchInformation;
import com.limegroup.gnutella.gui.search.SearchMediator;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public final class GeneralSearchInput extends JPanel {

    private MediaTypeInputButton mediaButton;
    private GoogleSearchField searchField;

    public GeneralSearchInput() {
        setupUI();
    }

    private void setupUI() {
        setOpaque(false);
        setLayout(new MigLayout());

        mediaButton = new MediaTypeInputButton();
        add(mediaButton);

        searchField = new GoogleSearchField();
        searchField.setPrompt(I18n.tr("Search or enter URL"));
        searchField.setMinimumSize(new Dimension(100, 27));
        searchField.setFont(deriveFont(searchField, 2f));
        searchField.addActionListener(new SearchListener());
        add(searchField);
    }

    private Font deriveFont(JComponent c, float size) {
        Font origFont = c.getFont();
        return origFont.deriveFont(origFont.getSize2D() + size);
    }

    /**
     * Listener for starting a new search.
     */
    private final class SearchListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String query = searchField.getText();

            //start a download from the search box by entering a URL.
            if (FileMenuActions.openMagnetOrTorrent(query)) {
                searchField.setText("");
                searchField.hidePopup();
                return;
            }

            final SearchInformation info = SearchInformation.createTitledKeywordSearch(query, null, MediaType.getTorrentMediaType(), query);

            // If the search worked, store & clear it.
            if (SearchMediator.triggerSearch(info) != null) {
                if (info.isKeywordSearch()) {

                    searchField.addToDictionary();

                    // Clear the existing search.
                    searchField.setText("");
                    searchField.hidePopup();
                }
            }
        }
    }
}

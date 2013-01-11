/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011, 2012, FrostWire(TM). All rights reserved.
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

package com.limegroup.gnutella.gui.themes;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public interface ThemeSetter {

    public String getName();

    public void apply();

    public SkinCustomUI getCustomUI();

    public ComponentUI createCheckBoxMenuItemUI(JComponent comp);

    public ComponentUI createMenuBarUI(JComponent comp);

    public ComponentUI createMenuItemUI(JComponent comp);

    public ComponentUI createMenuUI(JComponent comp);

    public ComponentUI createPopupMenuSeparatorUI(JComponent comp);

    public ComponentUI createPopupMenuUI(JComponent comp);

    public ComponentUI createRadioButtonMenuItemUI(JComponent comp);

    public ComponentUI createTextAreaUI(JComponent comp);

    public ComponentUI createListUI(JComponent comp);

    public ComponentUI createComboBoxUI(JComponent comp);

    public ComponentUI createTreeUI(JComponent comp);

    public ComponentUI createTableUI(JComponent comp);

    public ComponentUI createRangeSliderUI(JComponent comp);

    public ComponentUI createTabbedPaneUI(JComponent comp);

    public ComponentUI createProgressBarUI(JComponent comp);
    
    public ComponentUI createPanelUI(JComponent comp);
}

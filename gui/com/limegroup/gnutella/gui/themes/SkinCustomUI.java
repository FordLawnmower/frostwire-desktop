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

import java.awt.Color;

import javax.swing.border.TitledBorder;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public interface SkinCustomUI {

    public static final String CLIENT_PROPERTY_GRADIENT_BACKGROUND = "CLIENT_PROPERTY_GRADIENT_BACKGROUND";

    public static final String CLIENT_PROPERTY_DARK_DARK_NOISE = "CLIENT_PROPERTY_DARK_DARK_NOISE";
    public static final String CLIENT_PROPERTY_DARK_NOISE = "CLIENT_PROPERTY_DARK_NOISE";
    public static final String CLIENT_PROPERTY_LIGHT_NOISE = "CLIENT_PROPERTY_LIGHT_NOISE";

    public Color getDarkDarkNoise();

    public Color getDarkNoise();

    public Color getLightNoise();

    public Color getDarkBorder();

    public Color getLightBorder();

    public TitledBorder createTitledBorder(String title);

    public Color getLightForegroundColor();

    public Color getTabButtonForegroundColor();

    public Color getFilterTitleTopColor();

    public Color getFilterTitleColor();
    
    public SkinLinearGradient getApplicationHeaderBackground();
    
    public SkinLinearGradient getPlayerBackground();
}

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

package com.limegroup.gnutella.gui.themes.setters;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import com.limegroup.gnutella.gui.themes.SkinCustomUI;
import com.limegroup.gnutella.gui.themes.SkinLinearGradient;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public class SubstanceCustomUI implements SkinCustomUI {

    public static final Color DARK_DARK_NOISE = new Color(0xC4D6E0);

    public static final Color DARK_NOISE = new Color(0xD5E5ED);

    public static final Color LIGHT_NOISE = new Color(0xF2FBFF);

    public static final Color DARK_BORDER = new Color(0xA9BDC7);

    public static final Color LIGHT_BORDER = new Color(0xCDD9DE);

    public static final Color LIGHT_FOREGROUND = new Color(0xFFFFFF);

    public static final Color TAB_BUTTON_FOREGROUND = new Color(0x6489a8);

    public Color getDarkDarkNoise() {
        return DARK_DARK_NOISE;
    }

    @Override
    public Color getDarkNoise() {
        return DARK_NOISE;
    }

    @Override
    public Color getLightNoise() {
        return LIGHT_NOISE;
    }

    @Override
    public Color getDarkBorder() {
        return DARK_BORDER;
    }

    @Override
    public Color getLightBorder() {
        return LIGHT_BORDER;
    }

    public TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(title);
    }

    @Override
    public Color getLightForegroundColor() {
        return LIGHT_FOREGROUND;
    }

    public Color getTabButtonForegroundColor() {
        return TAB_BUTTON_FOREGROUND;
    }

    public Color getFilterTitleTopColor() {
        return new Color(0xffffff);
    }

    public Color getFilterTitleColor() {
        //0xfdf899 - yellow
        return new Color(0xfdf899);
    }

    @Override
    public SkinLinearGradient getApplicationHeaderBackground() {
        return new SkinLinearGradient(Color.BLUE, Color.RED, false);
    }

    @Override
    public SkinLinearGradient getPlayerBackground() {
        return new SkinLinearGradient(Color.BLUE, Color.RED, false);
    }
}
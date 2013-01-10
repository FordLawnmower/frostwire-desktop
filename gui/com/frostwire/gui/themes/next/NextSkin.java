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

package com.frostwire.gui.themes.next;

import java.awt.Color;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ColorSchemeSingleColorQuery;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceColorSchemeBundle;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter;
import org.pushingpixels.substance.api.painter.border.FractionBasedBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.MatteDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.FractionBasedFillPainter;
import org.pushingpixels.substance.api.painter.highlight.ClassicHighlightPainter;
import org.pushingpixels.substance.api.painter.overlay.BottomLineOverlayPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public class NextSkin extends SubstanceSkin {

    public static final String NAME = "Next";

    public NextSkin() {
        SubstanceSkin.ColorSchemes schemes = SubstanceSkin.getColorSchemes("org/limewire/gui/resources/next.colorschemes");

        SubstanceColorScheme activeScheme = schemes.get("Next Active");
        SubstanceColorScheme enabledScheme = schemes.get("Next Enabled");
        SubstanceColorScheme disabledScheme = schemes.get("Next Disabled");

        SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(activeScheme, enabledScheme, disabledScheme);

        // borders
        SubstanceColorScheme activeBorderScheme = schemes.get("Next Active Border");
        SubstanceColorScheme enabledBorderScheme = schemes.get("Next Enabled Border");
        SubstanceColorScheme disabledBorderScheme = schemes.get("Next Enabled Border");
        defaultSchemeBundle.registerColorScheme(enabledBorderScheme, ColorSchemeAssociationKind.BORDER, ComponentState.ENABLED);
        defaultSchemeBundle.registerColorScheme(activeBorderScheme, ColorSchemeAssociationKind.BORDER, ComponentState.DEFAULT);
        defaultSchemeBundle.registerColorScheme(disabledBorderScheme, ColorSchemeAssociationKind.BORDER, ComponentState.DISABLED_DEFAULT, ComponentState.DISABLED_SELECTED, ComponentState.DISABLED_UNSELECTED);

        // states
        SubstanceColorScheme defaultScheme = schemes.get("Next Default");
        SubstanceColorScheme defaultBorderScheme = schemes.get("Next Default Border");
        defaultSchemeBundle.registerColorScheme(defaultScheme, ComponentState.DEFAULT);
        defaultSchemeBundle.registerColorScheme(defaultBorderScheme, ColorSchemeAssociationKind.BORDER, ComponentState.DEFAULT);

        SubstanceColorScheme pressedScheme = schemes.get("Next Pressed");
        SubstanceColorScheme pressedBorderScheme = schemes.get("Next Pressed Border");
        defaultSchemeBundle.registerColorScheme(pressedScheme, ComponentState.PRESSED_SELECTED, ComponentState.PRESSED_UNSELECTED);
        defaultSchemeBundle.registerColorScheme(pressedBorderScheme, ColorSchemeAssociationKind.BORDER, ComponentState.PRESSED_SELECTED, ComponentState.PRESSED_UNSELECTED);

        SubstanceColorScheme selectedScheme = schemes.get("Next Selected");
        SubstanceColorScheme selectedBorderScheme = schemes.get("Next Selected Border");
        defaultSchemeBundle.registerColorScheme(selectedScheme, ComponentState.SELECTED, ComponentState.ROLLOVER_SELECTED);
        defaultSchemeBundle.registerColorScheme(selectedBorderScheme, ColorSchemeAssociationKind.BORDER, ComponentState.SELECTED, ComponentState.ROLLOVER_SELECTED);

        SubstanceColorScheme backgroundScheme = schemes.get("Next Background");

        this.registerDecorationAreaSchemeBundle(defaultSchemeBundle, backgroundScheme, DecorationAreaType.NONE);

        this.registerAsDecorationArea(activeScheme, DecorationAreaType.PRIMARY_TITLE_PANE);

        this.addOverlayPainter(new BottomLineOverlayPainter(new ColorSchemeSingleColorQuery() {
            @Override
            public Color query(SubstanceColorScheme scheme) {
                return scheme.getDarkColor().darker();
            }
        }), DecorationAreaType.PRIMARY_TITLE_PANE);

        this.buttonShaper = new ClassicButtonShaper();
        this.watermark = null;
        this.fillPainter = new FractionBasedFillPainter("Next", new float[] { 0.0f, 0.49999f, 0.5f, 0.65f, 1.0f }, new ColorSchemeSingleColorQuery[] { ColorSchemeSingleColorQuery.EXTRALIGHT, ColorSchemeSingleColorQuery.LIGHT, ColorSchemeSingleColorQuery.MID, ColorSchemeSingleColorQuery.LIGHT,
                ColorSchemeSingleColorQuery.ULTRALIGHT });

        this.decorationPainter = new MatteDecorationPainter();
        this.highlightPainter = new ClassicHighlightPainter();

        this.borderPainter = new FractionBasedBorderPainter("Next", new float[] { 0.0f, 0.5f, 1.0f }, new ColorSchemeSingleColorQuery[] { ColorSchemeSingleColorQuery.MID, ColorSchemeSingleColorQuery.DARK, ColorSchemeSingleColorQuery.ULTRADARK });
        this.highlightBorderPainter = new ClassicBorderPainter();
    }

    public String getDisplayName() {
        return NAME;
    }
}

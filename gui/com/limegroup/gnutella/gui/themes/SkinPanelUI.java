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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.ui.SubstancePanelUI;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public class SkinPanelUI extends SubstancePanelUI {

    public static ComponentUI createUI(JComponent comp) {
        return ThemeMediator.CURRENT_THEME.createPanelUI(comp);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        if (!SubstanceLookAndFeel.isCurrentLookAndFeel())
            return;

        SkinLinearGradient gradient = null;
        Object property = c.getClientProperty(SkinCustomUI.CLIENT_PROPERTY_GRADIENT_BACKGROUND);

        if (property instanceof SkinLinearGradient) {
            gradient = (SkinLinearGradient) property;
        }

        if (gradient != null) {
            Point2D start = new Point2D.Float(0, 0);
            Point2D end = new Point2D.Float(0, c.getHeight());
            float[] dist = { 0.0f, 1.0f };
            Color[] colors = { gradient.getStart(), gradient.getEnd() };
            LinearGradientPaint paint = new LinearGradientPaint(start, end, dist, colors);

            Graphics2D graphics = (Graphics2D) g.create();
            // optimization - do not call fillRect on graphics
            // with anti-alias turned on
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics.setPaint(paint);
            graphics.fillRect(0, 0, c.getWidth(), c.getHeight());

            graphics.dispose();

            super.paint(g, c);
        } else {
            super.update(g, c);
        }
    }
}

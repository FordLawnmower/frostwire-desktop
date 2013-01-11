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

import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.UIDefaults;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public class SkinPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 182325604729450397L;
	
	@Override
	public void addSeparator() {
		add(new SkinPopupMenu.Separator());
	}
	
	@Override
	public JMenuItem add(Action a) {
	    SkinMenuItem mi = createActionComponent(a);
        mi.setAction(a);
        add(mi);
        return mi;
	}
	
	@Override
	protected SkinMenuItem createActionComponent(Action a) {
	    SkinMenuItem mi = new SkinMenuItem() {
            /**
             * 
             */
            private static final long serialVersionUID = 957774055453803270L;

            protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
                PropertyChangeListener pcl = createActionChangeListener(this);
                if (pcl == null) {
                    pcl = super.createActionPropertyChangeListener(a);
                }
                return pcl;
            }
        };
        mi.setHorizontalTextPosition(JButton.TRAILING);
        mi.setVerticalTextPosition(JButton.CENTER);
        return mi;
	}

	/**
	 * A popup menu-specific separator.
	 */
	static public class Separator extends JSeparator {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2601567679361541634L;

		public Separator() {
			super(JSeparator.HORIZONTAL);
		}

		/**
		 * Returns the name of the L&F class that renders this component.
		 * 
		 * @return the string "PopupMenuSeparatorUI"
		 * @see JComponent#getUIClassID
		 * @see UIDefaults#getUI
		 */
		public String getUIClassID() {
			return "PopupMenuSeparatorUI";
		}
	}
}

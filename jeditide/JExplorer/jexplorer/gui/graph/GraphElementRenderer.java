/*
 * GraphElementRenderer.java
 * Copyright (C) 2003 Amedeo Farello
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jexplorer.gui.graph;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import jexplorer.JExplorerPlugin;
import jexplorer.JExplorerConstants;

import jexplorer.model.members.MElement;
/*------------------------------------------------------------------------------------------------------------------------------------
	GraphElementRenderer
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* An element renderer for the <code>Graph</code> class.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
*/
public class GraphElementRenderer extends JPanel implements JExplorerConstants
{
MElement		m_RefElement;
Rectangle		m_Frame = new Rectangle();
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	GraphElementRenderer()
{
setOpaque(false);
setBorder(BorderFactory.createEmptyBorder(DRAWING_BOX_SPACING, DRAWING_BOX_SPACING, DRAWING_BOX_SPACING, DRAWING_BOX_SPACING));
setAlignmentX(Component.LEFT_ALIGNMENT);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getGraphElementRendererComponent
------------------------------------------------------------------------------------------------------------------------------------*/
public Component getGraphElementRendererComponent(
					Graph		inGraph,
					MElement		inElement,		// element to display
					boolean		inIsSelected)	// is the element selected
{
m_RefElement = inElement;
setFont(m_RefElement.getFont());
return(this);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	paintComponent
------------------------------------------------------------------------------------------------------------------------------------*/
protected void		paintComponent(Graphics g)
{
m_RefElement.draw((Graphics2D)g, this);
}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void validate() {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void revalidate() {}
   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void repaint(long tm, int x, int y, int width, int height) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void repaint(Rectangle r) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	// Strings get interned...
	if (propertyName=="text")
	    super.firePropertyChange(propertyName, oldValue, newValue);
    }

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

    /**
     * A subclass of GraphElementRenderer that implements UIResource.
     * DefaultListCellRenderer doesn't implement UIResource
     * directly so that applications can safely override the
     * cellRenderer property with GraphElementRenderer subclasses.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
//    public static class UIResource extends GraphElementRenderer
//        implements javax.swing.plaf.UIResource
//    {
//    }

}
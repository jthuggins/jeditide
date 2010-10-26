/*
 * IndexCellRenderer.java
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

package jexplorer.gui.index;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.FontMetrics;

import javax.swing.JList;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import javax.swing.DefaultListCellRenderer;

import jexplorer.JExplorerConstants;

import jexplorer.gui.IndexDockable;

import jexplorer.model.members.MElement;
import jexplorer.model.members.MClass;
import jexplorer.model.members.MMember;
import jexplorer.model.members.MField;
import jexplorer.model.members.MConstructor;
import jexplorer.model.members.MMethod;
/*------------------------------------------------------------------------------------------------------------------------------------
	IndexCellRenderer
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A cell renderer for the global index of <code>IndexDockable</code>.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public class IndexCellRenderer extends DefaultListCellRenderer implements JExplorerConstants
{
static protected final int	AUX_ICONS_COUNT			= 2;
static protected final	int	DEFAULT_ICON_TEXT_GAP	= 4;

static protected final	int	COLOR_BOX_WIDTH			= 10;
static protected final	int	COLOR_BOX_HEIGHT		= 16;
static protected final	int	COLOR_BOX_GAP			= 2;

boolean			m_UnderLined	= false;
ImageIcon[]		m_AuxIcons		= new ImageIcon[AUX_ICONS_COUNT];
Color			m_PackageColor;
IndexDockable	m_Owner;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	IndexCellRenderer(IndexDockable inOwner)
{
m_Owner = inOwner;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	paintComponent
------------------------------------------------------------------------------------------------------------------------------------*/
protected void		paintComponent(Graphics g)
{
super.paintComponent(g);

int		xPos = getIcon() != null ? getIcon().getIconWidth() : 0;

// paint the underline, if necessary

if(m_UnderLined)
	{
	FontMetrics fm  = getFontMetrics(getFont());
	int			x	= xPos + Math.max(0, getIconTextGap() + 1);

	g.drawLine(	x,
				getHeight() - 2,
				x + fm.stringWidth(getText()),
				getHeight() - 2);
	}

// paint the package color, if necessary

if(m_PackageColor != null)
	{
	g.setColor(m_PackageColor);

	g.fillRect(	xPos + COLOR_BOX_GAP,
				(getHeight() - COLOR_BOX_HEIGHT) / 2,
				COLOR_BOX_WIDTH,
				COLOR_BOX_HEIGHT);

	g.setColor(Color.black);

	g.drawRect(	xPos + COLOR_BOX_GAP,
				(getHeight() - COLOR_BOX_HEIGHT) / 2,
				COLOR_BOX_WIDTH,
				COLOR_BOX_HEIGHT - 1);

	g.setColor(getForeground());
	xPos += (COLOR_BOX_GAP + COLOR_BOX_WIDTH + COLOR_BOX_GAP);
	}

// paint auxiliary icons

for(int i = 0; i < AUX_ICONS_COUNT; i++)
	{
	if(m_AuxIcons[i] != null)
		{
		m_AuxIcons[i].paintIcon(
			this, g, xPos, (getHeight() - m_AuxIcons[i].getIconHeight()) / 2);
		xPos += m_AuxIcons[i].getIconWidth();
		}
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getListCellRendererComponent
------------------------------------------------------------------------------------------------------------------------------------*/
public Component getListCellRendererComponent(	JList	inList,
												Object	inValue,		// value to display
												int		inIndex,		// cell index
												boolean	inIsSelected,	// is the cell selected
												boolean	inCellHasFocus)	// the list and the cell have the focus
{
// precompute
super.getListCellRendererComponent(	inList,
									inValue,
									inIndex,
									inIsSelected,
									inCellHasFocus);

m_UnderLined	= false;
m_PackageColor	= null;

for(int i = 0; i < AUX_ICONS_COUNT; i++)
	m_AuxIcons[i] = null;

setIconTextGap(DEFAULT_ICON_TEXT_GAP);

// element (MClass | MInterface)

if(inValue instanceof MElement)
	{
	MElement	e = (MElement)inValue;

	m_UnderLined =	m_Owner.getModel().getProperties().getOption(PM_SHOW_ACCESS_SYMBOLS) &&
					e.isStatic();

	setFont			(e.getFont());
	setForeground	(e.getTextColor());
	setText			(e.getIndexText());
	setToolTipText	(e.getToolTipText());
	setIcon			(e instanceof MClass ?
						(e.isNestedElement() ? ICON_NESTEDCLASS : ICON_CLASS) :
						(e.isNestedElement() ? ICON_NESTEDINTERFACE : ICON_INTERFACE));

	if(m_Owner.getModel().getProperties().getOption(PM_COLORIZE_PACKAGES))
		m_PackageColor = e.getPackageColor();

	if(m_Owner.getModel().getProperties().getOption(PM_SHOW_ACCESS_SYMBOLS))
		{
		switch(e.getAccess())
			{
			case ACCESS_PUBLIC:		m_AuxIcons[0] = ICON_PUBLIC;	break;
			case ACCESS_PROTECTED:	m_AuxIcons[0] = ICON_PROTECTED;	break;
			case ACCESS_PRIVATE:	m_AuxIcons[0] = ICON_PRIVATE;	break;
			case ACCESS_PACKAGE:	m_AuxIcons[0] = ICON_PACKAGE;	break;
			}
		}

	if(e.isDeprecated())
		m_AuxIcons[1] = ICON_DEPRECATED;
	}

// member (MField | MConstructor | MMethod)

else if(inValue instanceof MMember)
	{
	MMember			m = (MMember)inValue;

	m_UnderLined =	m_Owner.getModel().getProperties().getOption(PM_SHOW_ACCESS_SYMBOLS) &&
					m.isStatic();

	setFont			(m.getFont());
	setForeground	(m.getTextColor());
	setText			(m.getIndexText());
	setToolTipText	(m.getToolTipText());

		 if(m instanceof MField)		setIcon(ICON_FIELD);
	else if(m instanceof MConstructor)	setIcon(ICON_CONSTRUCTOR);
	else if(m instanceof MMethod)		setIcon(ICON_METHOD);

	if(m_Owner.getModel().getProperties().getOption(PM_COLORIZE_PACKAGES))
		m_PackageColor = m.getContainer().getPackageColor();

	if(m_Owner.getModel().getProperties().getOption(PM_SHOW_ACCESS_SYMBOLS))
		{
		switch(m.getAccess())
			{
			case ACCESS_PUBLIC:		m_AuxIcons[0] = ICON_PUBLIC;	break;
			case ACCESS_PROTECTED:	m_AuxIcons[0] = ICON_PROTECTED;	break;
			case ACCESS_PRIVATE:	m_AuxIcons[0] = ICON_PRIVATE;	break;
			case ACCESS_PACKAGE:	m_AuxIcons[0] = ICON_PACKAGE;	break;
			}
		}

	if(m.isDeprecated())
		m_AuxIcons[1] = ICON_DEPRECATED;
	}

// if this item is selected, use the standard
// foreground color to ensure proper text visibility

if(inIsSelected)
	setForeground(inList.getSelectionForeground());

// adjust the space for the possible auxiliary icons

if(m_PackageColor != null)
	setIconTextGap(getIconTextGap() + COLOR_BOX_GAP + COLOR_BOX_WIDTH + COLOR_BOX_GAP);

for(int i = 0; i < AUX_ICONS_COUNT; i++)
	if(m_AuxIcons[i] != null)
		setIconTextGap(getIconTextGap() + m_AuxIcons[i].getIconWidth());

return(this);
}
}

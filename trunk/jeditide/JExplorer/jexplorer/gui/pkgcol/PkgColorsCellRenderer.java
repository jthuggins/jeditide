/*
 * PkgColorsCellRenderer.java
 * Copyright (C) 2001-2002 Amedeo Farello
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package jexplorer.gui.pkgcol;

import java.awt.Component;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.FontMetrics;
import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.DefaultListCellRenderer;

import javax.swing.border.EmptyBorder;
/*------------------------------------------------------------------------------------------------------------------------------------
	PkgColorsCellRenderer
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A cell renderer for the <code>PkgColorsDialog</code>.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public class PkgColorsCellRenderer extends DefaultListCellRenderer
{
		private			PkgColorsModel	m_PkgColorsModel;
		private			String			m_PackageName;

static	private			EmptyBorder		s_NoFocusBorder	= new EmptyBorder(1, 1, 1, 1);
static	private			Dimension		s_PreferredSize;
static	private			int				s_FontAscent;

static	private final	int				COLOR_BOX_WIDTH	= 30;
static	private final	int				COLOR_BOX_GAP	= 5;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public PkgColorsCellRenderer(PkgColorsModel inPkgColorsModel)
{
m_PkgColorsModel = inPkgColorsModel;

setOpaque(true);
setBorder(s_NoFocusBorder);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	paintComponent
------------------------------------------------------------------------------------------------------------------------------------*/
protected void		paintComponent(Graphics inG)
{
Graphics2D	g			= (Graphics2D)inG;
Insets      m			= getInsets();
Color		savedColor	= getForeground();

// paint background

g.setColor(getBackground());
g.fillRect(0, 0, getWidth(), getHeight());
g.setColor(getForeground());

// draw package color box

g.setPaint(m_PkgColorsModel.getColor(m_PackageName));
g.fillRect(m.left, m.top, COLOR_BOX_WIDTH, getHeight() - (m.top + m.bottom + 1));

g.setPaint(Color.black);
g.drawRect(m.left, m.top, COLOR_BOX_WIDTH, getHeight() - (m.top + m.bottom + 1));

// draw package name

g.setPaint(savedColor);
g.drawString(m_PackageName, m.left + COLOR_BOX_WIDTH + COLOR_BOX_GAP, m.top + s_FontAscent);
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
m_PackageName = (inValue == null) ? "" : inValue.toString();

setComponentOrientation(inList.getComponentOrientation());
setBorder((inCellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : s_NoFocusBorder);
setFont(inList.getFont());

Graphics	g = inList.getGraphics();

if(g != null)
	{
	FontMetrics		fm  = g.getFontMetrics();
	Insets      	m   = getInsets();

	s_FontAscent	= fm.getAscent();
	s_PreferredSize	= new Dimension(50, m.top + fm.getHeight() + m.bottom);

	setPreferredSize(s_PreferredSize);
	}

if(inIsSelected)
	{
	setBackground(inList.getSelectionBackground());
	setForeground(inList.getSelectionForeground());
	}
else
	{
	setBackground(inList.getBackground());
	setForeground(inList.getForeground());
	}

setEnabled(inList.isEnabled());
return(this);
}
}


/*
 * DetailsCellRenderer.java
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

package jexplorer.gui.detail;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Color;

import javax.swing.JTree;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.UIManager;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

import jexplorer.JExplorerConstants;

import jexplorer.gui.IndexDockable;

import jexplorer.model.members.MElement;
import jexplorer.model.members.MMember;
import jexplorer.model.members.MClass;

import jexplorer.model.members.MField;
import jexplorer.model.members.MConstructor;
import jexplorer.model.members.MMethod;
/*------------------------------------------------------------------------------------------------------------------------------------
	DetailsCellRenderer
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A cell renderer for the details panel of <code>IndexDockable</code>.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public class DetailsCellRenderer extends DefaultTreeCellRenderer implements JExplorerConstants
{
static protected final int	AUX_ICONS_COUNT			= 5;
static protected final int	DEFAULT_ICON_TEXT_GAP	= 4;

boolean			m_UnderLined	= false;
ImageIcon[]		m_AuxIcons		= new ImageIcon[AUX_ICONS_COUNT];
IndexDockable	m_Owner;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	DetailsCellRenderer(IndexDockable inOwner)
{
m_Owner = inOwner;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	updateUI
------------------------------------------------------------------------------------------------------------------------------------*/
public void			updateUI()
{
super.updateUI();

// I think this code should be in the DefaultTreeCellRenderer implementation,
// especially since the 'drawsFocusBorderAroundIcon' variable is declared private

setLeafIcon		(UIManager.getIcon("Tree.leafIcon"));
setClosedIcon	(UIManager.getIcon("Tree.closedIcon"));
setOpenIcon		(UIManager.getIcon("Tree.openIcon"));

setTextSelectionColor			(UIManager.getColor("Tree.selectionForeground"));
setTextNonSelectionColor		(UIManager.getColor("Tree.textForeground"));
setBackgroundSelectionColor		(UIManager.getColor("Tree.selectionBackground"));
setBackgroundNonSelectionColor	(UIManager.getColor("Tree.textBackground"));
setBorderSelectionColor			(UIManager.getColor("Tree.selectionBorderColor"));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	paintComponent
------------------------------------------------------------------------------------------------------------------------------------*/
protected void		paintComponent(Graphics g)
{
super.paintComponent(g);

int		xPos = getIcon() != null ? getIcon().getIconWidth() : 0;

// paint underline

if(m_UnderLined)
	{
	g.drawLine(	xPos + Math.max(0, getIconTextGap()),
				getHeight() - 2,
				getWidth() - 2,
				getHeight() - 2);
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
	getTreeCellRendererComponent
------------------------------------------------------------------------------------------------------------------------------------*/
public Component getTreeCellRendererComponent(	JTree	inTree,
												Object	inValue,
												boolean	inIsSelected,
												boolean	inIsExpanded,
												boolean	inIsLeaf,
												int		inRow,
												boolean	inHasFocus)
{
// precompute
super.getTreeCellRendererComponent(	inTree,
									inValue,
									inIsSelected,
									inIsExpanded,
									inIsLeaf,
									inRow,
									inHasFocus);

m_UnderLined = false;

for(int i = 0; i < AUX_ICONS_COUNT; i++)
	m_AuxIcons[i] = null;

setIconTextGap(DEFAULT_ICON_TEXT_GAP);

DefaultMutableTreeNode	node = (DefaultMutableTreeNode)inValue;

// element (MElement)

if(node.getUserObject() instanceof MElement)
	{
	MElement		e = (MElement)node.getUserObject();

	m_UnderLined =	m_Owner.getModel().getProperties().getOption(PM_SHOW_ACCESS_SYMBOLS) &&
					e.isStatic();

	setFont			(e.getFont());
	setForeground	(e.getTextColor());
	setText			(e.getDetailsText());
	setToolTipText	(e.getToolTipText());
	setIcon			(e instanceof MClass ?
						(e.isNestedElement() ? ICON_NESTEDCLASS : ICON_CLASS) :
						(e.isNestedElement() ? ICON_NESTEDINTERFACE : ICON_INTERFACE));

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

else if(node.getUserObject() instanceof MMember)
	{
	MMember		m = (MMember)node.getUserObject();

	m_UnderLined =	m_Owner.getModel().getProperties().getOption(PM_SHOW_ACCESS_SYMBOLS) &&
					m.isStatic();

	setFont			(m.getFont());
	setForeground	(m.getTextColor());
	setText			(m.getDetailsText());
	setToolTipText	(m.getToolTipText());

	if(m instanceof MField)
		{
		setIcon(ICON_FIELD);
		if(m.isTransient())	m_AuxIcons[0] = ICON_TRANSIENT;
		if(m.isVolatile())	m_AuxIcons[1] = ICON_VOLATILE;
		}
	else if(m instanceof MConstructor)
		{
		setIcon(ICON_CONSTRUCTOR);
		}
	else if(m instanceof MMethod)
		{
		setIcon(ICON_METHOD);
		if(m.isNative())		m_AuxIcons[0] = ICON_NATIVE;
		if(m.isSynchronized())	m_AuxIcons[1] = ICON_SYNCHRONIZED;
		}

	if(m.isThrowing())
		m_AuxIcons[2] = ICON_THROWS;

	if(m_Owner.getModel().getProperties().getOption(PM_SHOW_ACCESS_SYMBOLS))
		{
		switch(m.getAccess())
			{
			case ACCESS_PUBLIC:		m_AuxIcons[3] = ICON_PUBLIC;	break;
			case ACCESS_PROTECTED:	m_AuxIcons[3] = ICON_PROTECTED;	break;
			case ACCESS_PRIVATE:	m_AuxIcons[3] = ICON_PRIVATE;	break;
			case ACCESS_PACKAGE:	m_AuxIcons[3] = ICON_PACKAGE;	break;
			}
		}

	if(m.isDeprecated())
		m_AuxIcons[4] = ICON_DEPRECATED;
	}

// default nodes

else
	{
	setIcon			(null);
	setToolTipText	(null);
	}

// if this item is selected, use the standard
// foreground color to ensure proper text visibility

if(inIsSelected)
	setForeground(getTextSelectionColor());

// adjust the space for the possible auxiliary icons

for(int i = 0; i < AUX_ICONS_COUNT; i++)
	if(m_AuxIcons[i] != null)
		setIconTextGap(getIconTextGap() + m_AuxIcons[i].getIconWidth());

return(this);
}
}

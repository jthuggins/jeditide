/*
 * ModelManagerCellRenderer.java
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

package jexplorer.gui.model;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JList;
import javax.swing.Icon;

import javax.swing.DefaultListCellRenderer;

import projectviewer.ProjectManager;
import projectviewer.vpt.VPTProject;

import jexplorer.JExplorerConstants;
import jexplorer.JExplorerPlugin;

import jexplorer.model.Model;
import jexplorer.model.ModelManager;

import jexplorer.model.properties.PMModelStatus;
/*------------------------------------------------------------------------------------------------------------------------------------
	ModelManagerCellRenderer
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A cell renderer for the <code>ModelManagerDialog</code>.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.10
 */
public class ModelManagerCellRenderer
	extends DefaultListCellRenderer
	implements JExplorerConstants
{
static protected final int	AUX_ICONS_COUNT			= 2;
static protected final int	DEFAULT_ICON_TEXT_GAP	= 4;

Icon[]			m_AuxIcons = new Icon[AUX_ICONS_COUNT];
ModelManager	m_Owner;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	ModelManagerCellRenderer(ModelManager inOwner)
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

for(int i = 0; i < AUX_ICONS_COUNT; i++)
	m_AuxIcons[i] = null;

setIconTextGap(DEFAULT_ICON_TEXT_GAP);

VPTProject	project = (VPTProject)inValue;

setFont(ProjectManager.getInstance().isLoaded(project.getName()) ?
			JExplorerPlugin.getUIFontListBold() :
			JExplorerPlugin.getUIFontList());

setText(project.getName());

Model			model	= m_Owner.getModel(project);
PMModelStatus	status	= model != null ?
							model.getProperties().getModelStatus() :
							null;

setIcon(model != null ?
			ICON_MODEL :
			ICON_BLANK);

m_AuxIcons[0] =
	model != null ?
		status.getRebuildOptionIcon() :
		ICON_BLANK;

m_AuxIcons[1] =
	model != null ?
		status.getStatusIcon() :
		ICON_BLANK;

// if this item is selected, use the standard
// foreground color to ensure proper text visibility

if(inIsSelected)
	setForeground(inList.getSelectionForeground());

// adjust the space for the possible auxiliary icons

for(int i = 0; i < AUX_ICONS_COUNT; i++)
	if(m_AuxIcons[i] != null)
		setIconTextGap(getIconTextGap() + m_AuxIcons[i].getIconWidth());

// if there is no model or the model has any ParsingManager at work, disable this cell
setEnabled(model != null ? !status.isParsing(): false);

return(this);
}
}

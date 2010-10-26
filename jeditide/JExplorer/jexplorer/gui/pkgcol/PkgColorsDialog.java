/*
 * PkgColorsDialog.java
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

package jexplorer.gui.pkgcol;

import java.awt.Color;
import java.awt.Component;

import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.JColorChooser;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

import jexplorer.gui.ListDialog;
import jexplorer.model.properties.PMPackageColors;
/*------------------------------------------------------------------------------------------------------------------------------------
	PkgColorsDialog
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A dialog to choose colors for the packages of a <code>Model</code>.
 *
 * @author	Amedeo Farello
 * @version	0.3, 2003.05.26
 */
public class PkgColorsDialog extends ListDialog
{
private PMPackageColors	m_Owner;
private PkgColorsModel	m_PkgColorsModel;
private int				m_PresetIndex = 0;

final static private String[] s_PresetColors = {
	"#ffcc66",
	"#ffff99",
	"#d5affc",
	"#ccff99",
	"#99ccff",
	"#ccccff",
	"#ffccff",
	"#ccffff"
	};
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public PkgColorsDialog(Component inParentComponent, PMPackageColors inOwner)
{
super("jexplorer.ui.pkgdlg", inParentComponent, true, new PkgColorsModel(inOwner));

m_Owner = inOwner;

m_List.setCellRenderer(new PkgColorsCellRenderer((PkgColorsModel)m_List.getModel()));

setTitle(	jEdit.getProperty(m_PropertiesPrefix + ".title") +
			m_Owner.getModel().getProject().getName());

// setup buttons

m_ButtonsPanel.setLayout(new BoxLayout(m_ButtonsPanel, BoxLayout.X_AXIS));

m_ButtonsPanel.add(m_Add_Btn);
m_ButtonsPanel.add(Box.createHorizontalStrut(4));
m_ButtonsPanel.add(m_Remove_Btn);
m_ButtonsPanel.add(Box.createHorizontalStrut(4));
m_ButtonsPanel.add(m_Edit_Btn);
m_ButtonsPanel.add(Box.createHorizontalStrut(8));
m_ButtonsPanel.add(Box.createGlue());
m_ButtonsPanel.add(m_OK_Btn);
m_ButtonsPanel.add(Box.createHorizontalStrut(4));
m_ButtonsPanel.add(m_Cancel_Btn);

// finish initialization

pack();
GUIUtilities.loadGeometry(this, m_PropertiesPrefix);
updateButtonsStatus();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doOK
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doOK()
{
super.doOK();
m_Owner.setValues(((PkgColorsModel)m_List.getModel()).getValues());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isAddBtnAvailable
------------------------------------------------------------------------------------------------------------------------------------*/
protected boolean	isAddBtnAvailable()
{
return true;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isRemoveBtnAvailable
------------------------------------------------------------------------------------------------------------------------------------*/
protected boolean	isRemoveBtnAvailable()
{
return !m_List.isSelectionEmpty();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isEditBtnAvailable
------------------------------------------------------------------------------------------------------------------------------------*/
protected boolean	isEditBtnAvailable()
{
return !m_List.isSelectionEmpty();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doAdd
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doAdd()
{
String	packageName = JOptionPane.showInputDialog(
							this,
							jEdit.getProperty("jexplorer.ui.pkgdlg.addPackageDialog.label"),
							jEdit.getProperty("jexplorer.ui.pkgdlg.addPackageDialog.title"),
							JOptionPane.QUESTION_MESSAGE);

if(packageName != null && !packageName.equals(""))
	((PkgColorsModel)m_List.getModel()).addDefinition(packageName, getNextPresetColor());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doRemove
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doRemove()
{
String		packageName = (String)m_List.getSelectedValue();

if(packageName != null)
	{
	m_List.clearSelection();
	((PkgColorsModel)m_List.getModel()).removeDefinition(packageName);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doEdit
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doEdit()
{
String		packageName = (String)m_List.getSelectedValue();

if(packageName != null)
	{
	Color	theColor = JColorChooser.showDialog(
				this,
				jEdit.getProperty("jexplorer.ui.pkgdlg.colorChooser.title"),
				((PkgColorsModel)m_List.getModel()).getColor(packageName));

	if(theColor != null)
		((PkgColorsModel)m_List.getModel()).addDefinition(packageName, theColor);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getNextPresetColor
------------------------------------------------------------------------------------------------------------------------------------*/
private Color	getNextPresetColor()
{
m_PresetIndex = m_PresetIndex >= s_PresetColors.length ? 0 : m_PresetIndex;
return GUIUtilities.parseColor(s_PresetColors[m_PresetIndex++], Color.white);
}
}

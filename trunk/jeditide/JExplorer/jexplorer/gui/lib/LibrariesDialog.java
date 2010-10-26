/*
 * LibrariesDialog.java
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

package jexplorer.gui.lib;

import java.io.File;

import java.util.Iterator;

import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

import jexplorer.gui.ListDialog;
import jexplorer.model.properties.PMLibraries;
/*------------------------------------------------------------------------------------------------------------------------------------
	LibrariesDialog
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A dialog to select libraries for a <code>Model</code>.
 *
 * @author	Amedeo Farello
 * @version	0.3, 2003.05.26
 */
public class LibrariesDialog extends ListDialog
{
private PMLibraries	m_Owner;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public LibrariesDialog(Component inParentComponent, PMLibraries inOwner)
{
super("jexplorer.ui.libdlg", inParentComponent, true, new DefaultListModel());

m_Owner = inOwner;

setTitle(	jEdit.getProperty(m_PropertiesPrefix + ".title") +
			m_Owner.getModel().getProject().getName());

// setup buttons

m_ButtonsPanel.setLayout(new BoxLayout(m_ButtonsPanel, BoxLayout.X_AXIS));

m_ButtonsPanel.add(m_Add_Btn);
m_ButtonsPanel.add(Box.createHorizontalStrut(4));
m_ButtonsPanel.add(m_Remove_Btn);
m_ButtonsPanel.add(Box.createHorizontalStrut(8));
m_ButtonsPanel.add(Box.createGlue());
m_ButtonsPanel.add(m_OK_Btn);
m_ButtonsPanel.add(Box.createHorizontalStrut(4));
m_ButtonsPanel.add(m_Cancel_Btn);

// finish initialization

pack();
GUIUtilities.loadGeometry(this, m_PropertiesPrefix);
updateButtonsStatus();

for(Iterator it = m_Owner.getIterator(); it.hasNext();)
	((DefaultListModel)getListModel()).addElement(it.next());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doOK
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doOK()
{
super.doOK();
m_Owner.setValues(((DefaultListModel)getListModel()).elements());
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
return false;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doAdd
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doAdd()
{
JFileChooser	chooser = new JFileChooser();

chooser.setAcceptAllFileFilterUsed(false);
chooser.addChoosableFileFilter(new FileChooserFilter());
chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
	((DefaultListModel)m_List.getModel()).addElement(chooser.getSelectedFile().getAbsolutePath());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doRemove
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doRemove()
{
String		libPath = (String)m_List.getSelectedValue();

if(libPath != null)
	{
	m_List.clearSelection();
	((DefaultListModel)m_List.getModel()).removeElement(libPath);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doEdit
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doEdit()
{
}
/*------------------------------------------------------------------------------------------------------------------------------------
	FileChooserFilter [nested class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class FileChooserFilter extends FileFilter
{
/*------------------------------------------------------------------------------------------------------------------------------------
	accept
------------------------------------------------------------------------------------------------------------------------------------*/
public boolean	accept(File inFile)
{
if(	inFile.isDirectory() ||
	inFile.getName().endsWith(".jar") ||
	inFile.getName().endsWith(".zip"))
		return(true);

return(false);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getDescription
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getDescription()
{
return "Java libraries";
}
}
}

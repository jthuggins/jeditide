/*
 * ModelManagerDialog.java
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

import java.io.File;

import java.util.EventObject;
import java.util.Iterator;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

import projectviewer.ProjectManager;
import projectviewer.vpt.VPTProject;

import jexplorer.JExplorerPlugin;
import jexplorer.JExplorerConstants;

import jexplorer.gui.ListDialog;
import jexplorer.gui.lib.LibrariesDialog;
import jexplorer.gui.pkgcol.PkgColorsDialog;

import jexplorer.model.ModelManager;
import jexplorer.model.ModelManagerListener;
import jexplorer.model.ModelManagerEvent;
import jexplorer.model.Model;
import jexplorer.model.ModelListener;

import jexplorer.model.properties.PMModelStatus;
/*------------------------------------------------------------------------------------------------------------------------------------
	ModelManagerDialog
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A dialog to create, remove and setup <code>Model</code>s.
 *
 * @author	Amedeo Farello
 * @version	0.3, 2003.05.26
 */
public class ModelManagerDialog
	extends ListDialog
	implements JExplorerConstants, ModelManagerListener, ModelListener
{
private ModelManager	m_Owner;

private JButton			m_Libraries_Btn;
private JButton			m_PackageColors_Btn;

private ButtonsListener	m_ButtonsListener	= new ButtonsListener();
private StatusUpdater	m_StatusUpdater		= new StatusUpdater();
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public ModelManagerDialog(ModelManager inOwner)
{
super("jexplorer.ui.mgrdlg", jEdit.getActiveView(), false, new DefaultListModel());

m_Owner = inOwner;

m_List.setCellRenderer(new ModelManagerCellRenderer(inOwner));
m_Cancel_Btn.setText(jEdit.getProperty("common.close"));

// create custom buttons

m_Libraries_Btn = new JButton();
m_Libraries_Btn.addActionListener(m_ButtonsListener);
m_Libraries_Btn.setFont(JExplorerPlugin.getUIFont());
m_Libraries_Btn.setText(jEdit.getProperty(m_PropertiesPrefix + ".libBtn.label"));
m_Libraries_Btn.setToolTipText(jEdit.getProperty(m_PropertiesPrefix + ".libBtn.tooltip"));

m_PackageColors_Btn = new JButton();
m_PackageColors_Btn.addActionListener(m_ButtonsListener);
m_PackageColors_Btn.setFont(JExplorerPlugin.getUIFont());
m_PackageColors_Btn.setText(jEdit.getProperty(m_PropertiesPrefix + ".pkgBtn.label"));
m_PackageColors_Btn.setToolTipText(jEdit.getProperty(m_PropertiesPrefix + ".pkgBtn.tooltip"));

// setup buttons

m_ButtonsPanel.setLayout(new BoxLayout(m_ButtonsPanel, BoxLayout.Y_AXIS));

JPanel	topPanel = new JPanel();
topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
m_ButtonsPanel.add(topPanel);

m_ButtonsPanel.add(Box.createVerticalStrut(4));

JPanel	btmPanel = new JPanel();
btmPanel.setLayout(new BoxLayout(btmPanel, BoxLayout.X_AXIS));
m_ButtonsPanel.add(btmPanel);

topPanel.add(m_Edit_Btn);
topPanel.add(Box.createHorizontalStrut(4));
topPanel.add(m_Libraries_Btn);
topPanel.add(Box.createHorizontalStrut(4));
topPanel.add(m_PackageColors_Btn);

btmPanel.add(m_Add_Btn);
btmPanel.add(Box.createHorizontalStrut(4));
btmPanel.add(m_Remove_Btn);
btmPanel.add(Box.createHorizontalStrut(8));
btmPanel.add(Box.createGlue());
btmPanel.add(m_Cancel_Btn);

// finish initialization

pack();
GUIUtilities.loadGeometry(this, m_PropertiesPrefix);
updateButtonsStatus();

m_Owner.addListener(this);

Iterator	it;

// fills the list with the ProjectViewer projects
for(it = ProjectManager.getInstance().getProjects(); it.hasNext();)
	((DefaultListModel)getListModel()).addElement(it.next());

// connect the dialog to the models
for(it = m_Owner.getModelsIterator(); it.hasNext();)
	{
	Model	model = (Model)it.next();

	model.addListener(this);
	model.getProperties().getModelStatus().addActionListener(m_StatusUpdater);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doOK
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doOK()
{
// this dialog responds to 'Cancel' only
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doCancel
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doCancel()
{
super.doCancel();

m_Owner.removeListener(this);

// disconnect the dialog from the models
for(Iterator it = m_Owner.getModelsIterator(); it.hasNext();)
	{
	Model	model = (Model)it.next();

	model.removeListener(this);
	model.getProperties().getModelStatus().removeActionListener(m_StatusUpdater);
	}

m_Owner.modelsDialogCanceled();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isAddBtnAvailable
------------------------------------------------------------------------------------------------------------------------------------*/
protected boolean	isAddBtnAvailable()
{
VPTProject	project = (VPTProject)m_List.getSelectedValue();

return(	project != null &&
		m_Owner.getModel(project) == null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isRemoveBtnAvailable
------------------------------------------------------------------------------------------------------------------------------------*/
protected boolean	isRemoveBtnAvailable()
{
VPTProject	project = (VPTProject)m_List.getSelectedValue();

return(	project != null &&
		m_Owner.getModel(project) != null &&
		!m_Owner.getModel(project).getProperties().getModelStatus().isParsing());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isEditBtnAvailable
------------------------------------------------------------------------------------------------------------------------------------*/
protected boolean	isEditBtnAvailable()
{
VPTProject	project = (VPTProject)m_List.getSelectedValue();

return(	project != null &&
		m_Owner.getModel(project) != null &&
		!m_Owner.getModel(project).getProperties().getModelStatus().isParsing());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	updateButtonsStatus
------------------------------------------------------------------------------------------------------------------------------------*/
public void		updateButtonsStatus()
{
super.updateButtonsStatus();

m_Libraries_Btn		.setEnabled(isEditBtnAvailable());
m_PackageColors_Btn	.setEnabled(isEditBtnAvailable());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doAdd
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doAdd()
{
VPTProject	project = (VPTProject)m_List.getSelectedValue();

if(project != null && m_Owner.getModel(project) == null)
	m_Owner.addModel(project);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doRemove
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doRemove()
{
VPTProject	project = (VPTProject)m_List.getSelectedValue();

if(project != null && m_Owner.getModel(project) != null)
	{
	int	ret = JOptionPane.showConfirmDialog(
			this,
			jEdit.getProperty("jexplorer.ui.mgrdlg.removeDialog.label"),
			project.getName(),
			JOptionPane.YES_NO_OPTION,
			JOptionPane.WARNING_MESSAGE);

	if(ret == 0)
		m_Owner.removeModel(project);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doEdit
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doEdit()
{
VPTProject	project = (VPTProject)m_List.getSelectedValue();

if(project != null)
	{
	Model	model = m_Owner.getModel(project);

	if(model != null)
		new ModelDialog(this, model).show();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doLibraries
------------------------------------------------------------------------------------------------------------------------------------*/
private void	doLibraries()
{
VPTProject	project = (VPTProject)m_List.getSelectedValue();

if(project != null)
	{
	Model	model = m_Owner.getModel(project);

	if(model != null)
		new LibrariesDialog(this, model.getProperties().getLibraries()).show();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doPackageColors
------------------------------------------------------------------------------------------------------------------------------------*/
private void	doPackageColors()
{
VPTProject	project = (VPTProject)m_List.getSelectedValue();

if(project != null)
	{
	Model	model = m_Owner.getModel(project);

	if(model != null)
		new PkgColorsDialog(this, model.getProperties().getPackageColors()).show();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	modelAdded [implements ModelManagerListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		modelAdded(ModelManagerEvent inEvt)
{
inEvt.getModel().addListener(this);
inEvt.getModel().getProperties().getModelStatus().addActionListener(m_StatusUpdater);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	modelRemoved [implements ModelManagerListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		modelRemoved(ModelManagerEvent inEvt)
{
inEvt.getModel().removeListener(this);
inEvt.getModel().getProperties().getModelStatus().removeActionListener(m_StatusUpdater);

repaint();
updateButtonsStatus();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	modelRenamed [implements ModelManagerListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		modelRenamed(ModelManagerEvent inEvt)
{
repaint();
updateButtonsStatus();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	modelParsing [implements ModelListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		modelParsing(EventObject inEvt)
{
repaint();
updateButtonsStatus();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	modelUpdated [implements ModelListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		modelUpdated(EventObject inEvt)
{
repaint();
updateButtonsStatus();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	ButtonsListener [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class	ButtonsListener implements ActionListener
{
public void		actionPerformed(ActionEvent evt)
{
	 if(evt.getSource() == m_Libraries_Btn)		doLibraries();
else if(evt.getSource() == m_PackageColors_Btn)	doPackageColors();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	StatusUpdater [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class StatusUpdater implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
repaint();
}
}
}

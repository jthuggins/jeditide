/*
 * ModelDialog.java
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JButton;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

import jexplorer.JExplorerPlugin;
import jexplorer.gui.JExplorerDialog;
import jexplorer.model.Model;
import jexplorer.model.properties.PMModelStatus;
/*------------------------------------------------------------------------------------------------------------------------------------
	ModelDialog
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A dialog setup a <code>Model</code>.
 *
 * @author	Amedeo Farello
 * @version	0.3, 2003.05.26
 */
public class ModelDialog extends JExplorerDialog
{
private Model			m_Model;
private JRadioButton	m_RebuildOnChanges;
private JRadioButton	m_RebuildWhenSaved;
private JRadioButton	m_RebuildNever;
private JButton			m_Update_Btn;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public ModelDialog(Component inParentComponent, Model inModel)
{
super(	"jexplorer.ui.moddlg",
		inParentComponent,
		true);

setTitle(	jEdit.getProperty(m_PropertiesPrefix + ".title") +
			inModel.getProject().getName());

m_Model = inModel;

// content panel

int		rebuild = m_Model.getProperties().getModelStatus().getRebuildOption();
JPanel	panel	= new JPanel(new GridLayout(0, 1));

panel.setBorder(BorderFactory.createTitledBorder(jEdit.getProperty(m_PropertiesPrefix + ".rebuild.label")));
m_ContentPanel.add(panel, BorderLayout.CENTER);

m_RebuildOnChanges = new JRadioButton();
m_RebuildOnChanges.setFont(JExplorerPlugin.getUIFont());
m_RebuildOnChanges.setText(jEdit.getProperty(m_PropertiesPrefix + ".change.label"));
m_RebuildOnChanges.setToolTipText(jEdit.getProperty(m_PropertiesPrefix + ".change.tooltip"));
m_RebuildOnChanges.setSelected(rebuild == PMModelStatus.REBUILD_ON_CHANGES);
panel.add(m_RebuildOnChanges);

m_RebuildWhenSaved = new JRadioButton();
m_RebuildWhenSaved.setFont(JExplorerPlugin.getUIFont());
m_RebuildWhenSaved.setText(jEdit.getProperty(m_PropertiesPrefix + ".save.label"));
m_RebuildWhenSaved.setToolTipText(jEdit.getProperty(m_PropertiesPrefix + ".save.tooltip"));
m_RebuildWhenSaved.setSelected(rebuild == PMModelStatus.REBUILD_WHEN_SAVED);
panel.add(m_RebuildWhenSaved);

m_RebuildNever = new JRadioButton();
m_RebuildNever.setFont(JExplorerPlugin.getUIFont());
m_RebuildNever.setText(jEdit.getProperty(m_PropertiesPrefix + ".never.label"));
m_RebuildNever.setToolTipText(jEdit.getProperty(m_PropertiesPrefix + ".never.tooltip"));
m_RebuildNever.setSelected(rebuild == PMModelStatus.REBUILD_NEVER);
panel.add(m_RebuildNever);

ButtonGroup		group = new ButtonGroup();

group.add(m_RebuildOnChanges);
group.add(m_RebuildWhenSaved);
group.add(m_RebuildNever);

// setup buttons

m_ButtonsPanel.setLayout(new BoxLayout(m_ButtonsPanel, BoxLayout.X_AXIS));

m_Update_Btn = new JButton();
m_Update_Btn.addActionListener(new UpdateBtnListener());
m_Update_Btn.setFont(JExplorerPlugin.getUIFont());
m_Update_Btn.setText(jEdit.getProperty(m_PropertiesPrefix + ".buildBtn.label"));
m_Update_Btn.setToolTipText(jEdit.getProperty(m_PropertiesPrefix + ".buildBtn.tooltip"));

m_ButtonsPanel.add(m_Update_Btn);
m_ButtonsPanel.add(Box.createHorizontalStrut(8));
m_ButtonsPanel.add(Box.createGlue());
m_ButtonsPanel.add(m_OK_Btn);
m_ButtonsPanel.add(Box.createHorizontalStrut(4));
m_ButtonsPanel.add(m_Cancel_Btn);

// finish initialization

pack();
GUIUtilities.loadGeometry(this, m_PropertiesPrefix);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doOK
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doOK()
{
super.doOK();

PMModelStatus	status	= m_Model.getProperties().getModelStatus();
int				rebuild	= status.getRebuildOption();

	 if(m_RebuildOnChanges.isSelected() &&
		rebuild != PMModelStatus.REBUILD_ON_CHANGES)
			status.setRebuildOption(PMModelStatus.REBUILD_ON_CHANGES);

else if(m_RebuildWhenSaved.isSelected() &&
		rebuild != PMModelStatus.REBUILD_WHEN_SAVED)
			status.setRebuildOption(PMModelStatus.REBUILD_WHEN_SAVED);

else if(m_RebuildNever.isSelected() &&
		rebuild != PMModelStatus.REBUILD_NEVER)
			status.setRebuildOption(PMModelStatus.REBUILD_NEVER);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	UpdateBtnListener [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class	UpdateBtnListener implements ActionListener
{
public void		actionPerformed(ActionEvent evt)
{
if(evt.getSource() == m_Update_Btn)
	m_Model.updateModel();
}
}
}

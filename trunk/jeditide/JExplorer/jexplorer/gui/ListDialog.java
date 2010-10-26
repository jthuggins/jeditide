/*
 * ListDialog.java
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

package jexplorer.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import org.gjt.sp.jedit.jEdit;

import jexplorer.JExplorerPlugin;
/*------------------------------------------------------------------------------------------------------------------------------------
	ListDialog
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A base class for all list-based JExplorer plugin dialogs.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public abstract class ListDialog extends JExplorerDialog
{
protected JList			m_List;

protected JButton		m_Add_Btn;
protected JButton		m_Edit_Btn;
protected JButton		m_Remove_Btn;

private ButtonsEnabler	m_ButtonsEnabler	= new ButtonsEnabler();
private ButtonsListener	m_ButtonsListener	= new ButtonsListener();
/*------------------------------------------------------------------------------------------------------------------------------------
	abstract methods
------------------------------------------------------------------------------------------------------------------------------------*/
abstract protected void		doAdd();
abstract protected void		doRemove();
abstract protected void		doEdit();
abstract protected boolean	isAddBtnAvailable();
abstract protected boolean	isRemoveBtnAvailable();
abstract protected boolean	isEditBtnAvailable();
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public ListDialog(	String		inPropertiesPrefix,
					Component	inParentComponent,
					boolean		inModal,
					ListModel	inListModel)
{
super(	inPropertiesPrefix,
		inParentComponent,
		inModal);

addWindowListener(new WindowAdapter()
	{
	// give the focus to content when activated
	public void windowActivated(WindowEvent inEvt)
		{ m_List.getParent().requestFocus(); }
	});

// create the list

if(inListModel != null)
	m_List = new JList(inListModel);
else
	m_List = new JList();

m_List.addListSelectionListener(m_ButtonsEnabler);
m_List.addMouseListener(new MouseAdapter()
	{
	public void mouseClicked(MouseEvent e)
		{
		if(	e.getClickCount() == 2 &&
			isEditBtnAvailable())
				doEdit();
		}
	});

JScrollPane	scroller = new JScrollPane();

scroller.setViewportView(m_List);
m_ContentPanel.add(scroller, BorderLayout.CENTER);

// create custom buttons

m_Add_Btn = new JButton();
m_Add_Btn.addActionListener(m_ButtonsListener);
m_Add_Btn.setFont(JExplorerPlugin.getUIFont());
m_Add_Btn.setText(jEdit.getProperty(m_PropertiesPrefix + ".addBtn.label"));
m_Add_Btn.setToolTipText(jEdit.getProperty(m_PropertiesPrefix + ".addBtn.tooltip"));

m_Remove_Btn = new JButton();
m_Remove_Btn.addActionListener(m_ButtonsListener);
m_Remove_Btn.setFont(JExplorerPlugin.getUIFont());
m_Remove_Btn.setText(jEdit.getProperty(m_PropertiesPrefix + ".removeBtn.label"));
m_Remove_Btn.setToolTipText(jEdit.getProperty(m_PropertiesPrefix + ".removeBtn.tooltip"));

m_Edit_Btn = new JButton();
m_Edit_Btn.addActionListener(m_ButtonsListener);
m_Edit_Btn.setFont(JExplorerPlugin.getUIFont());
m_Edit_Btn.setText(jEdit.getProperty(m_PropertiesPrefix + ".editBtn.label"));
m_Edit_Btn.setToolTipText(jEdit.getProperty(m_PropertiesPrefix + ".editBtn.tooltip"));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getListModel
------------------------------------------------------------------------------------------------------------------------------------*/
public ListModel	getListModel()
{
return m_List.getModel();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	updateButtonsStatus
------------------------------------------------------------------------------------------------------------------------------------*/
public void		updateButtonsStatus()
{
m_Add_Btn	.setEnabled(isAddBtnAvailable());
m_Remove_Btn.setEnabled(isRemoveBtnAvailable());
m_Edit_Btn	.setEnabled(isEditBtnAvailable());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	ButtonsEnabler [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class ButtonsEnabler implements ListSelectionListener
{
public void	valueChanged(ListSelectionEvent inEvt)
{
if(!inEvt.getValueIsAdjusting())
	updateButtonsStatus();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	ButtonsListener [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class	ButtonsListener implements ActionListener
{
public void		actionPerformed(ActionEvent evt)
{
	 if(evt.getSource() == m_Add_Btn)		doAdd();
else if(evt.getSource() == m_Edit_Btn)		doEdit();
else if(evt.getSource() == m_Remove_Btn)	doRemove();
}
}
}

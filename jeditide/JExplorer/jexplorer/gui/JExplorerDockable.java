/*
 * JExplorerDockable.java
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

import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;
import java.util.EventObject;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Component;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.swing.JList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Box;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.jEdit;

import jexplorer.JExplorerConstants;
import jexplorer.JExplorerPlugin;

import jexplorer.model.Model;
import jexplorer.model.ModelManager;
import jexplorer.model.ModelManagerListener;
import jexplorer.model.ModelManagerEvent;
import jexplorer.model.ModelListener;

import jexplorer.model.properties.PropertyManager;
import jexplorer.model.selection.SelectionListener;
import jexplorer.model.selection.SelectionEvent;
/*------------------------------------------------------------------------------------------------------------------------------------
	JExplorerDockable
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* A base class for the JExplorer plugin dockable windows.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
*/
public abstract class JExplorerDockable
	extends		JPanel
	implements	EBComponent,
				SelectionListener,
				ModelManagerListener,
				ModelListener,
				JExplorerConstants
{
protected	View				m_View;
protected	Model				m_Model;
protected	JComboBox			m_ModelsCombo;

static private boolean			s_DisableCombo;

// control panel related items
private JButton					m_Back_Btn;
private JButton					m_BackHist_Btn;
private JButton					m_Forw_Btn;
private JButton					m_ForwHist_Btn;
private HistoryButtonsListener	m_HistoryBtnsListener	= new HistoryButtonsListener();
protected Vector				m_ToolbarComponents		= new Vector();

final static private String NO_MODEL = jEdit.getProperty("jexplorer.ui.noModel");

abstract public void	updateContent();
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public JExplorerDockable(View inView, String inPosition)
{
super(new BorderLayout());

if(inPosition.equals(DockableWindowManager.FLOATING))
	setPreferredSize(new Dimension(500, 250));

m_View = inView;

// build the models combo box
m_ModelsCombo = new JComboBox();
m_ModelsCombo.setRenderer(new ModelsComboCellRenderer());
m_ModelsCombo.addItemListener(new ModelsComboListener());

Dimension	dim = m_ModelsCombo.getPreferredSize();
dim.width = Integer.MAX_VALUE;
m_ModelsCombo.setMaximumSize(dim);
m_ModelsCombo.setFont(JExplorerPlugin.getUIFont());
m_ModelsCombo.setToolTipText(jEdit.getProperty("jexplorer.ui.modelSelector.tooltip"));

modelsComboSetup();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getModel
------------------------------------------------------------------------------------------------------------------------------------*/
public Model	getModel()
{
return m_Model;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addNotify
------------------------------------------------------------------------------------------------------------------------------------*/
public void addNotify()
{
super.addNotify();
EditBus.addToBus(this);
ModelManager.getInstance().addListener(this);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	removeNotify
------------------------------------------------------------------------------------------------------------------------------------*/
public void removeNotify()
{
super.removeNotify();
EditBus.removeFromBus(this);
ModelManager.getInstance().removeListener(this);
unregister();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	register
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	register(Model inModel)
{
unregister();
m_Model = inModel;
ModelManager.getInstance().registerDockable(this, m_View, m_Model);

// tell the selection model to inform us when selection changes
m_Model.getSelectionModel().addSelectionListener(this);

// connect the user interface components to the related property managers
for(Enumeration e = m_ToolbarComponents.elements(); e.hasMoreElements(); )
	{
	JComponent		c	= (JComponent)e.nextElement();
	PropertyManager	pm	= m_Model.getProperties().getPM(c.getName());

	pm.attachComponent(c);
	}

// connect the history buttons
m_Back_Btn		.addActionListener(m_HistoryBtnsListener);
m_Forw_Btn		.addActionListener(m_HistoryBtnsListener);
m_BackHist_Btn	.addActionListener(m_HistoryBtnsListener);
m_ForwHist_Btn	.addActionListener(m_HistoryBtnsListener);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	unregister
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	unregister()
{
if(m_Model != null)
	{
	// disconnect from the model
	ModelManager.getInstance().unregisterDockable(this);

	// disconnect from the selection model
	m_Model.getSelectionModel().removeSelectionListener(this);

	// disconnect the user interface components from the related property managers
	for(Enumeration e = m_ToolbarComponents.elements(); e.hasMoreElements(); )
		{
		JComponent		c	= (JComponent)e.nextElement();
		PropertyManager	pm	= m_Model.getProperties().getPM(c.getName());

		pm.detachComponent(c);
		}

	// connect the history buttons
	m_Back_Btn		.removeActionListener(m_HistoryBtnsListener);
	m_Forw_Btn		.removeActionListener(m_HistoryBtnsListener);
	m_BackHist_Btn	.removeActionListener(m_HistoryBtnsListener);
	m_ForwHist_Btn	.removeActionListener(m_HistoryBtnsListener);

	m_Model = null;
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	modelAdded [implements ModelManagerListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		modelAdded(ModelManagerEvent inEvt)
{
modelsComboSetup();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	modelRemoved [implements ModelManagerListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		modelRemoved(ModelManagerEvent inEvt)
{
if(inEvt.getModel() == m_Model)
	{
	unregister();
	updateContent();
	}

modelsComboSetup();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	modelRenamed [implements ModelManagerListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		modelRenamed(ModelManagerEvent inEvt)
{
modelsComboSetup();
m_ModelsCombo.repaint();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	modelParsing [implements ModelListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		modelParsing(EventObject inEvt)
{
// no operation
}
/*------------------------------------------------------------------------------------------------------------------------------------
	modelUpdated [implements ModelListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		modelUpdated(EventObject inEvt)
{
updateContent();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	updateSelection
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	updateSelection()
{
m_Back_Btn.setEnabled		(m_Model != null ? m_Model.getSelectionModel().canGoBack() : false);
m_BackHist_Btn.setEnabled	(m_Model != null ? m_Model.getSelectionModel().canGoBack() : false);

m_Forw_Btn.setEnabled		(m_Model != null ? m_Model.getSelectionModel().canGoForward() : false);
m_ForwHist_Btn.setEnabled	(m_Model != null ? m_Model.getSelectionModel().canGoForward() : false);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	modelsComboSetup
------------------------------------------------------------------------------------------------------------------------------------*/
public void		modelsComboSetup()
{
s_DisableCombo = true;

m_ModelsCombo.removeAllItems();
m_ModelsCombo.addItem(NO_MODEL);

for(Iterator it = ModelManager.getInstance().getModelsIterator(); it.hasNext();)
	m_ModelsCombo.addItem(it.next());

if(m_Model != null)
	m_ModelsCombo.setSelectedItem(m_Model);

s_DisableCombo = false;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	buildHistoryToolbar
------------------------------------------------------------------------------------------------------------------------------------*/
protected JToolBar	buildHistoryToolbar()
{
JToolBar	historyToolbar = new JToolBar();

historyToolbar.setFloatable(false);
historyToolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);

m_Back_Btn = new JButton();
m_Back_Btn.setIcon(ICON_BTN_BACK);
m_Back_Btn.setMargin(BUTTONS_MARGINS);
m_Back_Btn.setRequestFocusEnabled(false);
m_Back_Btn.setToolTipText(jEdit.getProperty("jexplorer.ui.history.back.tooltip"));
historyToolbar.add(m_Back_Btn);

m_BackHist_Btn = new JButton();
m_BackHist_Btn.setIcon(ICON_BTN_OPEN_POPUP);
m_BackHist_Btn.setMargin(BUTTONS_MARGINS);
m_BackHist_Btn.setRequestFocusEnabled(false);
m_BackHist_Btn.setToolTipText(jEdit.getProperty("jexplorer.ui.history.backPopup.tooltip"));
historyToolbar.add(m_BackHist_Btn);

historyToolbar.add(Box.createHorizontalStrut(6));

m_Forw_Btn = new JButton();
m_Forw_Btn.setIcon(ICON_BTN_FORWARD);
m_Forw_Btn.setMargin(BUTTONS_MARGINS);
m_Forw_Btn.setRequestFocusEnabled(false);
m_Forw_Btn.setToolTipText(jEdit.getProperty("jexplorer.ui.history.forw.tooltip"));
historyToolbar.add(m_Forw_Btn);

m_ForwHist_Btn = new JButton();
m_ForwHist_Btn.setIcon(ICON_BTN_OPEN_POPUP);
m_ForwHist_Btn.setMargin(BUTTONS_MARGINS);
m_ForwHist_Btn.setRequestFocusEnabled(false);
m_ForwHist_Btn.setToolTipText(jEdit.getProperty("jexplorer.ui.history.forwPopup.tooltip"));
historyToolbar.add(m_ForwHist_Btn);

return(historyToolbar);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setupToolbarComponent
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	setupToolbarComponent(JPanel inContainer, Component inComponent)
{
inContainer.add(inComponent);			// put the component inside the container
m_ToolbarComponents.add(inComponent);	// save a reference to the component
}
/*------------------------------------------------------------------------------------------------------------------------------------
	HistoryButtonsListener [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class HistoryButtonsListener implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
JButton		b = (JButton)inEvt.getSource();

	 if(b.equals(m_Back_Btn))		m_Model.getSelectionModel().back();
else if(b.equals(m_Forw_Btn))		m_Model.getSelectionModel().forward();
else if(b.equals(m_BackHist_Btn))	m_Model.getSelectionModel().doBackPopup(m_BackHist_Btn);
else if(b.equals(m_ForwHist_Btn))	m_Model.getSelectionModel().doForwardPopup(m_ForwHist_Btn);
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	ModelsComboListener [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class ModelsComboListener implements ItemListener
{
public void itemStateChanged(ItemEvent inEvt)
{
if(	inEvt.getStateChange() != ItemEvent.SELECTED ||
	s_DisableCombo)
		return;

if(inEvt.getItem() instanceof Model)	// user chose a Model
	register((Model)inEvt.getItem());
else									// user chose "NO_MODEL"
	unregister();

updateContent();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	ModelsComboCellRenderer [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private static class	ModelsComboCellRenderer extends DefaultListCellRenderer
{
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

if(inValue instanceof Model)
	setText(((Model)inValue).getProject().getName());

return(this);
}
}
}

/*
 * GraphDockable.java
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

import java.util.Enumeration;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.BoxLayout;
import javax.swing.Box;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.PropertiesChanged;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import jexplorer.gui.graph.Graph;
import jexplorer.gui.graph.GraphModel;
import jexplorer.gui.graph.GraphSelectionListener;
import jexplorer.gui.graph.GraphSelectionEvent;

import jexplorer.model.Model;

import jexplorer.model.members.MElement;

import jexplorer.model.properties.PMOption;
import jexplorer.model.properties.PMChoice;
import jexplorer.model.properties.PMModelStatus;

import jexplorer.model.selection.SelectionListener;
import jexplorer.model.selection.SelectionEvent;
/*------------------------------------------------------------------------------------------------------------------------------------
	GraphDockable
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* A Graph window for the JExplorer plugin.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
*/
public class GraphDockable extends JExplorerDockable
{
static private final boolean	DEBUG = false;

private Graph			m_Graph;
private boolean			m_IsGraphAutoSelection	= false;
private GUpdater		m_GraphUpdater			= new GUpdater();
private GBuilder		m_GraphBuilder			= new GBuilder();
private GPainter		m_GraphPainter			= new GPainter();
private GPositioner		m_GraphPositioner		= new GPositioner();

final static private String[] s_Layouts = {
	jEdit.getProperty("jexplorer.ui.graphLayout1"),
	jEdit.getProperty("jexplorer.ui.graphLayout2"),
	jEdit.getProperty("jexplorer.ui.graphLayout3")
	};

final static private String[] s_Relatives = {
	jEdit.getProperty("jexplorer.ui.selectedRelatives1"),
	jEdit.getProperty("jexplorer.ui.selectedRelatives2"),
	jEdit.getProperty("jexplorer.ui.selectedRelatives3")
	};
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public GraphDockable(View inView, String inPosition)
{
super(inView, inPosition);

// create the control panel
JPanel	controlPanel = new JPanel();
controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
add(controlPanel, BorderLayout.NORTH);

// add the standard Project selector
controlPanel.add(m_ModelsCombo);
setupToolbarComponent(controlPanel, PMModelStatus.createLabel(PM_MODEL_STATUS));

// create the history toolbar
controlPanel.add(buildHistoryToolbar());

// create the custom toolbar
setupToolbarComponent(controlPanel, PMOption.createToggleButton(PM_USE_SHORT_NAMES,		ICON_BTN_SHORT_NAMES));
setupToolbarComponent(controlPanel, PMOption.createToggleButton(PM_SHOW_ACCESS_SYMBOLS,	ICON_BTN_SYMBOLS));

setupToolbarComponent(controlPanel, PMOption.createToggleButton(PI_SHOW_CLASSES,		ICON_CLASS));
setupToolbarComponent(controlPanel, PMOption.createToggleButton(PI_SHOW_INTERFACES,		ICON_INTERFACE));
setupToolbarComponent(controlPanel, PMOption.createToggleButton(PI_SHOW_NESTED,			/*ICON_NESTEDELEMENT*/ICON_NESTEDCLASS));

setupToolbarComponent(controlPanel, PMChoice.createComboBox(PG_CURRENT_LAYOUT, s_Layouts));

setupToolbarComponent(controlPanel, PMOption.createToggleButton(PG_CENTER_SELECTION,	ICON_BTN_SEL_CENTER));
setupToolbarComponent(controlPanel, PMOption.createToggleButton(PG_FILTER_SELECTION,	ICON_BTN_SEL_FILTER));
setupToolbarComponent(controlPanel, PMChoice.createComboBox(PG_CURRENT_RELATIVES, s_Relatives));

setupToolbarComponent(controlPanel, PMOption.createToggleButton(PG_CONN_SHOW_LAYOUT_ONLY,	ICON_BTN_CONN_RELEVANT));
setupToolbarComponent(controlPanel, PMOption.createToggleButton(PG_CONN_SHOW_DERIVATION,	ICON_BTN_CONN_DERIV));
setupToolbarComponent(controlPanel, PMOption.createToggleButton(PG_CONN_SHOW_IMPLEMENTATION,ICON_BTN_CONN_IMPL));
setupToolbarComponent(controlPanel, PMOption.createToggleButton(PG_CONN_SHOW_INCLUSION,		ICON_BTN_CONN_NEST));

setupToolbarComponent(controlPanel, PMOption.createToggleButton(PM_COLORIZE_PACKAGES,		ICON_BTN_COLORIZE_PKG));
//setupToolbarComponent(controlPanel, PMPackageColors.createButton(PM_PACKAGE_COLORS,			ICON_BTN_COLPKG_DIALOG));

// create the graph panel

m_Graph = new Graph();
m_Graph.getSelectionModel().addGraphSelectionListener(new GListener());

JScrollPane		scroller = new JScrollPane(m_Graph);

scroller.getHorizontalScrollBar().setUnitIncrement(25);
scroller.getVerticalScrollBar().setUnitIncrement(25);

add(scroller, BorderLayout.CENTER);

updateSelection();	// disable history buttons
}
/*------------------------------------------------------------------------------------------------------------------------------------
	handleMessage [implements EBComponent]
------------------------------------------------------------------------------------------------------------------------------------*/
public void handleMessage(EBMessage inMessage)
{
if(inMessage instanceof PropertiesChanged)
	{
	m_Graph.repaint();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	register
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	register(Model inModel)
{
super.register(inModel);

// connect tasks to the property managers
m_Model.getProperties().getPM(PM_USE_SHORT_NAMES)			.addActionListener(m_GraphUpdater);
m_Model.getProperties().getPM(PM_SHOW_ACCESS_SYMBOLS)		.addActionListener(m_GraphUpdater);

m_Model.getProperties().getPM(PG_CENTER_SELECTION)			.addActionListener(m_GraphPositioner);

m_Model.getProperties().getPM(PG_CURRENT_LAYOUT)			.addActionListener(m_GraphBuilder);
m_Model.getProperties().getPM(PG_FILTER_SELECTION)			.addActionListener(m_GraphBuilder);
m_Model.getProperties().getPM(PG_CURRENT_RELATIVES)			.addActionListener(m_GraphBuilder);
m_Model.getProperties().getPM(PI_SHOW_CLASSES)				.addActionListener(m_GraphBuilder);
m_Model.getProperties().getPM(PI_SHOW_INTERFACES)			.addActionListener(m_GraphBuilder);
m_Model.getProperties().getPM(PI_SHOW_NESTED)				.addActionListener(m_GraphBuilder);

m_Model.getProperties().getPM(PG_CONN_SHOW_LAYOUT_ONLY)		.addActionListener(m_GraphPainter);
m_Model.getProperties().getPM(PG_CONN_SHOW_DERIVATION)		.addActionListener(m_GraphPainter);
m_Model.getProperties().getPM(PG_CONN_SHOW_IMPLEMENTATION)	.addActionListener(m_GraphPainter);
m_Model.getProperties().getPM(PG_CONN_SHOW_INCLUSION)		.addActionListener(m_GraphPainter);
m_Model.getProperties().getPM(PM_COLORIZE_PACKAGES)			.addActionListener(m_GraphPainter);
m_Model.getProperties().getPM(PM_PACKAGE_COLORS)			.addActionListener(m_GraphPainter);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	unregister
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	unregister()
{
if(m_Model != null)
	{
	// disconnect tasks from the property managers
	m_Model.getProperties().getPM(PM_USE_SHORT_NAMES)			.removeActionListener(m_GraphUpdater);
	m_Model.getProperties().getPM(PM_SHOW_ACCESS_SYMBOLS)		.removeActionListener(m_GraphUpdater);

	m_Model.getProperties().getPM(PG_CENTER_SELECTION)			.removeActionListener(m_GraphPositioner);

	m_Model.getProperties().getPM(PG_CURRENT_LAYOUT)			.removeActionListener(m_GraphBuilder);
	m_Model.getProperties().getPM(PG_FILTER_SELECTION)			.removeActionListener(m_GraphBuilder);
	m_Model.getProperties().getPM(PG_CURRENT_RELATIVES)			.removeActionListener(m_GraphBuilder);
	m_Model.getProperties().getPM(PI_SHOW_CLASSES)				.removeActionListener(m_GraphBuilder);
	m_Model.getProperties().getPM(PI_SHOW_INTERFACES)			.removeActionListener(m_GraphBuilder);
	m_Model.getProperties().getPM(PI_SHOW_NESTED)				.removeActionListener(m_GraphBuilder);

	m_Model.getProperties().getPM(PG_CONN_SHOW_LAYOUT_ONLY)		.removeActionListener(m_GraphPainter);
	m_Model.getProperties().getPM(PG_CONN_SHOW_DERIVATION)		.removeActionListener(m_GraphPainter);
	m_Model.getProperties().getPM(PG_CONN_SHOW_IMPLEMENTATION)	.removeActionListener(m_GraphPainter);
	m_Model.getProperties().getPM(PG_CONN_SHOW_INCLUSION)		.removeActionListener(m_GraphPainter);
	m_Model.getProperties().getPM(PM_COLORIZE_PACKAGES)			.removeActionListener(m_GraphPainter);
	m_Model.getProperties().getPM(PM_PACKAGE_COLORS)			.removeActionListener(m_GraphPainter);
	}

super.unregister();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	selectionChanged [implements SelectionListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void	selectionChanged(SelectionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "selection changed, source: " + inEvt.getSource());

if(inEvt.isElementSelection())	// if the selected element changed, do a full update
	updateSelection();
else							// otherwise simply update the history buttons
	super.updateSelection();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	updateContent
------------------------------------------------------------------------------------------------------------------------------------*/
public void		updateContent()
{
buildGraph();
m_Graph.scrollRectToVisible(UNIT_RECTANGLE);
updateSelection();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	updateSelection
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	updateSelection()
{
super.updateSelection();
updateGraphSelection();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	buildGraph
------------------------------------------------------------------------------------------------------------------------------------*/
public void		buildGraph()
{
GraphModel	gm = (GraphModel)m_Graph.getModel();

if(m_Model != null)	gm.setContent(m_Model.getWorkSet());
else				gm.reset();

updateGraphSelection();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	updateGraphSelection
------------------------------------------------------------------------------------------------------------------------------------*/
public void		updateGraphSelection()
{
GraphModel	gm	= (GraphModel)m_Graph.getModel();
MElement	e	= m_Model != null ? m_Model.getSelectionModel().getElement() : null;

// if selection filtering is on, we need to rebuild the graph
if(	m_Model != null &&
	m_Model.getProperties().getOption(PG_FILTER_SELECTION))
	{
	m_Graph.scrollRectToVisible(UNIT_RECTANGLE);
	m_Graph.contentChanged();
	}

if(gm.hasElement(e))
	{
	if(e != m_Graph.getSelectedValue())
		{
		m_IsGraphAutoSelection = true;
		m_Graph.setSelectedValue(e);
		}
	}
else
	{
	if(!m_Graph.isSelectionEmpty())
		{
		m_IsGraphAutoSelection = true;
		m_Graph.clearSelection();
		}
	}

// manage selection centring
updateGraphCentring();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	updateGraphCentring
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	updateGraphCentring()
{
if(	m_Model != null &&
	m_Model.getProperties().getOption(PG_CENTER_SELECTION))
		m_Graph.centerSelection();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	GListener [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class GListener implements GraphSelectionListener
{
public void	valueChanged(GraphSelectionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

if(m_IsGraphAutoSelection)
	{
	m_IsGraphAutoSelection = false;
	}
else if(m_Model != null)
	{
	m_Model.getSelectionModel().select(inEvt.getSelectedElement(), true);
	}
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	GBuilder [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class GBuilder implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

buildGraph();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	GUpdater [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class GUpdater implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

GraphModel	gm = (GraphModel)m_Graph.getModel();
gm.regenerate();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	GPainter [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class GPainter implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

m_Graph.repaint();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	GPositioner [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class GPositioner implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

updateGraphCentring();
}
}
}
/*
 * Graph.java
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

package jexplorer.gui.graph;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Component;
import java.awt.Dimension;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JPanel;
import javax.swing.CellRendererPane;
import javax.swing.ToolTipManager;

import java.util.Iterator;

import jexplorer.model.members.MElement;

import org.gjt.sp.util.Log;
/*------------------------------------------------------------------------------------------------------------------------------------
	Graph
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Displays a model graphically.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.08
 */
public class Graph
	extends JPanel
	implements GraphSelectionListener
{
private GraphModel				m_Model				= new GraphModel(this);
private GraphSelectionModel		m_SelectionModel	= new GraphSelectionModel();

private Rectangle				m_SelectionRect		= new Rectangle();
private Rectangle				m_ElementBounds		= new Rectangle();

private GraphElementRenderer	m_Renderer			= new GraphElementRenderer();
private	CellRendererPane		m_RendererPane		= new CellRendererPane();
/*------------------------------------------------------------------------------------------------------------------------------------
	compact methods
------------------------------------------------------------------------------------------------------------------------------------*/
public GraphModel			getModel()			{ return m_Model; }
public GraphSelectionModel	getSelectionModel()	{ return m_SelectionModel; }

public MElement	getSelectedValue()	{ return getSelectionModel().getSelectedElement(); }
public boolean	isSelectionEmpty()	{ return getSelectionModel().isSelectionEmpty(); }
public void		clearSelection()	{ getSelectionModel().clearSelection(); }
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	Graph()
{
setLayout(null);
setBackground(UIManager.getColor("Tree.background"));

addMouseListener(new MouseAdapter()
	{
	public void mousePressed(MouseEvent e)
		{
		if(SwingUtilities.isLeftMouseButton(e))
			getSelectionModel().setSelectedElement(getElementByLocation(e.getPoint()));
		}
	});

add(m_RendererPane);
getSelectionModel().addGraphSelectionListener(this);
ToolTipManager.sharedInstance().registerComponent(this);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setSelectedValue
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setSelectedValue(MElement inElement)
{
getSelectionModel().setSelectedElement(inElement);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	valueChanged [implements GraphSelectionListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		valueChanged(GraphSelectionEvent inEvt)
{
repaint();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	contentChanged
------------------------------------------------------------------------------------------------------------------------------------*/
public void		contentChanged()
{
// compute the new overall size
int		xMax = 0,
		yMax = 0;

if(getModel().getSize() > 0)
	{
	Iterator	iter = getModel().getIterator();

	while(iter.hasNext())
		{
		MElement	e = (MElement)iter.next();

		if(e.isInserted())
			{
			xMax = Math.max(xMax, e.getGraphX() + e.getWidth());
			yMax = Math.max(yMax, e.getGraphY() + e.getHeight());
			}
		}
	}

setPreferredSize(new Dimension(xMax, yMax));
revalidate();
repaint();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	centerSelection
------------------------------------------------------------------------------------------------------------------------------------*/
public void		centerSelection()
{
MElement		e = getSelectedValue();

if(e != null)
	{
	computeVisibleRect(m_SelectionRect);

	m_SelectionRect.x = e.getGraphX() + (e.getWidth() - m_SelectionRect.width) / 2;
	m_SelectionRect.y = e.getGraphY() + (e.getHeight() - m_SelectionRect.height) / 2;

	scrollRectToVisible(m_SelectionRect);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	paintComponent
------------------------------------------------------------------------------------------------------------------------------------*/
protected void		paintComponent(Graphics inG)
{
// paint background
super.paintComponent(inG);

// if we don't have data, let's stop here
if(getModel().getSize() == 0)
	return;

Graphics2D	g = (Graphics2D)inG;

// paint selection
MElement		se = getSelectedValue();

if(se != null && se.isInserted())
	{
	g.setPaint(UIManager.getColor("Tree.selectionBackground"));
	g.fillRect(	se.getGraphX(),
				se.getGraphY(),
				se.getWidth(),
				se.getHeight());
	}

// paint connections
Iterator	iter = getModel().getIterator();

while(iter.hasNext())
	{
	MElement	e = (MElement)iter.next();

	if(e.isInserted())
		e.drawAllConnections(this, g);
	}

// paint elements
Rectangle	paintBounds = g.getClipBounds();

iter = getModel().getIterator();

while(iter.hasNext())
	{
	MElement	e = (MElement)iter.next();

	if(e.isInserted())
		{
		m_ElementBounds.setBounds(	e.getGraphX(),
									e.getGraphY(),
									e.getWidth(),
									e.getHeight());

		// draw only if necessary
		if(	paintBounds.contains(m_ElementBounds) ||
			paintBounds.intersects(m_ElementBounds))
			{
			g.setClip(e.getGraphX(), e.getGraphY(), e.getWidth(), e.getHeight());
			g.clipRect(paintBounds.x, paintBounds.y, paintBounds.width, paintBounds.height);

			paintElement(g, e);
			}
		}
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	paintElement
------------------------------------------------------------------------------------------------------------------------------------*/
protected void		paintElement(Graphics g, MElement inElement)
{
Component rendererComponent =
	m_Renderer.getGraphElementRendererComponent(this, inElement, false);

m_RendererPane.paintComponent(
	g,
	rendererComponent,
	this,
	inElement.getGraphX(),
	inElement.getGraphY(),
	inElement.getWidth(),
	inElement.getHeight(),
	true);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getElementByLocation
------------------------------------------------------------------------------------------------------------------------------------*/
protected MElement	getElementByLocation(Point inP)
{
if(getModel().getSize() > 0)
	{
	Iterator	it = getModel().getIterator();

	while(it.hasNext())
		{
		MElement	e = (MElement)it.next();

		if(e.isInserted())
			{
			m_ElementBounds.setBounds(	e.getGraphX(),
										e.getGraphY(),
										e.getWidth(),
										e.getHeight());

			if(m_ElementBounds.contains(inP))
				return(e);
			}
		}
	}

return(null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getToolTipText
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getToolTipText(MouseEvent inEvt)
{
String	tip;
Point	p	= inEvt.getPoint();
MElement	e	= getElementByLocation(p);

if(e != null)	tip	= e.getToolTipText();
else			tip = getToolTipText();

return(tip);
}
}

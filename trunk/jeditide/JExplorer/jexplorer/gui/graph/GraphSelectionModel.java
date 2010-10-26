/*
 * GraphSelectionModel.java
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

import javax.swing.event.EventListenerList;

import jexplorer.model.members.MElement;
/*------------------------------------------------------------------------------------------------------------------------------------
	GraphSelectionModel
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* A selection model for the <code>Graph</code> class.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
*/
public class GraphSelectionModel
{
protected MElement			m_SelectedElement;
protected EventListenerList	m_ListenerList = new EventListenerList();
/*------------------------------------------------------------------------------------------------------------------------------------
	compact methods
------------------------------------------------------------------------------------------------------------------------------------*/
public MElement	getSelectedElement()	{ return m_SelectedElement; }
public boolean	isSelectionEmpty()		{ return m_SelectedElement == null; }
public void		clearSelection()		{ setSelectedElement(null); }
/*------------------------------------------------------------------------------------------------------------------------------------
	addGraphSelectionListener
------------------------------------------------------------------------------------------------------------------------------------*/
public void	addGraphSelectionListener(GraphSelectionListener inListener)
{
m_ListenerList.add(GraphSelectionListener.class, inListener);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	removeGraphSelectionListener
------------------------------------------------------------------------------------------------------------------------------------*/
public void	removeGraphSelectionListener(GraphSelectionListener inListener)
{
m_ListenerList.remove(GraphSelectionListener.class, inListener);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	fireValueChanged
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	fireValueChanged()
{
// Guaranteed to return a non-null array
Object[]	listeners = m_ListenerList.getListenerList();

GraphSelectionEvent	evt = null;

// Process the listeners FIRST to LAST, notifying
// those that are interested in this event
for(int i = 0; i < listeners.length; i += 2)
	if(listeners[i] == GraphSelectionListener.class)
		{
		if(evt == null)
			evt = new GraphSelectionEvent(this, m_SelectedElement);

		((GraphSelectionListener)listeners[i + 1]).valueChanged(evt);
		}
}   
/*------------------------------------------------------------------------------------------------------------------------------------
	setSelectedElement
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setSelectedElement(MElement inElement)
{
m_SelectedElement = inElement;
fireValueChanged();
}
}

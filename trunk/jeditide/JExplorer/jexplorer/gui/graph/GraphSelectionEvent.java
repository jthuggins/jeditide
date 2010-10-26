/*
 * GraphSelectionEvent.java
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

import java.util.EventObject;

import jexplorer.model.members.MElement;
/*------------------------------------------------------------------------------------------------------------------------------------
	GraphSelectionEvent
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * An event that characterizes a change in the current Graph selection.
 *
 * @author Amedeo Farello
 * @version 0.1, 2003.03.26
 */
public class GraphSelectionEvent extends EventObject
{
private MElement m_SelectedElement;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Constructor.
 * @param inSelectedElement The selected element.
 */
public GraphSelectionEvent(Object inSource, MElement inSelectedElement)
{
super(inSource);
m_SelectedElement = inSelectedElement;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getSelectedElement
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the selected element.
 * @return The selected element
 */
public MElement getSelectedElement() { return m_SelectedElement; }
}

/*
 * GraphModel.java
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

import java.util.TreeSet;
import java.util.Iterator;

import jexplorer.model.members.MElement;
/*------------------------------------------------------------------------------------------------------------------------------------
	GraphModel
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* A data model for the <code>Graph</code> class.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
*/
public class GraphModel
{
private Graph		m_Graph;
private TreeSet		m_WorkSet;
/*------------------------------------------------------------------------------------------------------------------------------------
	compact methods
------------------------------------------------------------------------------------------------------------------------------------*/
public int		getSize()		{ return m_WorkSet != null ? m_WorkSet.size() : 0; }
public Iterator	getIterator()	{ return m_WorkSet != null ? m_WorkSet.iterator() : null; }
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	GraphModel(Graph inGraph)
{
m_Graph = inGraph;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	hasElement
------------------------------------------------------------------------------------------------------------------------------------*/
public boolean	hasElement(MElement inElement)
{
return	inElement != null &&
		m_WorkSet != null &&
		m_WorkSet.contains(inElement);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	regenerate
------------------------------------------------------------------------------------------------------------------------------------*/
public void		regenerate()
{
m_Graph.contentChanged();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	reset
------------------------------------------------------------------------------------------------------------------------------------*/
public void		reset()
{
m_WorkSet = null;
m_Graph.contentChanged();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setContent
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setContent(TreeSet inWorkSet)
{
m_WorkSet = inWorkSet;
m_Graph.contentChanged();
}
}

/*
 * ModelManagerEvent.java
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

package jexplorer.model;

import java.util.EventObject;
/*------------------------------------------------------------------------------------------------------------------------------------
	ModelManagerEvent
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * An event that characterizes a change in the ModelManager models.
 *
 * @author Amedeo Farello
 * @version 0.1, 2003.03.26
 */
public class ModelManagerEvent extends EventObject
{
private Model	m_Model;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>ModelManagerEvent</code>.
 *
 * @param inSource	The source of this event.
 * @param inModel	The affected model.
 */
public ModelManagerEvent(Object inSource, Model inModel)
{
super(inSource);
m_Model = inModel;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getModel
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the changed MElement.
 *
 * @return The affected Model
 */
public Model getModel()
	{ return m_Model; }
}

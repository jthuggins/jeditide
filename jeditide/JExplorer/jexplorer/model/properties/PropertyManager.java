/*
 * PropertyManager.java
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

package jexplorer.model.properties;

import java.util.Hashtable;
import java.util.Vector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

import jexplorer.model.Model;
/*------------------------------------------------------------------------------------------------------------------------------------
	PropertyManager
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A base class for all property managers.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public abstract class PropertyManager extends ActionHandler
{
protected Model					m_Model;
protected EventListenerList		m_ListenerList	= new EventListenerList();
protected Vector				m_Components	= new Vector();
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public PropertyManager(	Hashtable	inActionsTable,
						Model		inModel,
						String		inName)
{
super(inActionsTable, inName);
m_Model = inModel;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getModel
------------------------------------------------------------------------------------------------------------------------------------*/
public Model	getModel()
{
return m_Model;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addActionListener
------------------------------------------------------------------------------------------------------------------------------------*/
public void		addActionListener(ActionListener inListener)
{
m_ListenerList.add(ActionListener.class, inListener);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	removeActionListener
------------------------------------------------------------------------------------------------------------------------------------*/
public void		removeActionListener(ActionListener inListener)
{
m_ListenerList.remove(ActionListener.class, inListener);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	fireActionPerformed
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	fireActionPerformed(ActionEvent inEvt)
{
// Guaranteed to return a non-null array
Object[]	listeners = m_ListenerList.getListenerList();

// Process the listeners FIRST to LAST, notifying
// those that are interested in this event
for(int i = 0; i < listeners.length; i += 2)
	if(listeners[i] == ActionListener.class)
		((ActionListener)listeners[i + 1]).actionPerformed(inEvt);
}   
/*------------------------------------------------------------------------------------------------------------------------------------
	attachComponent
------------------------------------------------------------------------------------------------------------------------------------*/
public void		attachComponent(JComponent inComponent)
{
m_Components.add(inComponent);
inComponent.setEnabled(isEnabled());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	detachComponent
------------------------------------------------------------------------------------------------------------------------------------*/
public void		detachComponent(JComponent inComponent)
{
m_Components.remove(inComponent);
inComponent.setEnabled(false);
}
}

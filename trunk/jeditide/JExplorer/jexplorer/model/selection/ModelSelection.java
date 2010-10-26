/*
 * ModelSelection.java
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

package jexplorer.model.selection;

import java.util.Vector;

import java.awt.Component;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import javax.swing.event.EventListenerList;

import org.gjt.sp.jedit.GUIUtilities;

import jexplorer.JExplorerPlugin;

import jexplorer.model.Model;

import jexplorer.model.members.MElement;
import jexplorer.model.members.MMember;
/*------------------------------------------------------------------------------------------------------------------------------------
	ModelSelection
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Represents a selection in a <code>Model</code>.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.10
 */
public class ModelSelection
{
static protected final int	HISTORY_SIZE = 20;

private Model				m_Model;
private MElement			m_Element;
private MMember				m_Member;

private String				m_SavedElementName;
private String				m_SavedMemberName;
private String				m_SavedMemberSignature;

private Vector				m_HistoryVector			= new Vector();
private int					m_HistoryIndex			= -1;
private JPopupMenu			m_HistoryPopup			= new JPopupMenu();
private PopupListener		m_HistoryPopupListener	= new PopupListener();

protected EventListenerList	m_ListenerList			= new EventListenerList();
/*------------------------------------------------------------------------------------------------------------------------------------
	compact methods
------------------------------------------------------------------------------------------------------------------------------------*/
public MElement	getElement()	{ return m_Element; }
public MMember	getMember()		{ return m_Member; }
public Object	getObject()		{ return m_Member != null ? m_Member : (Object)m_Element; }
public boolean	canGoBack()		{ return m_HistoryIndex > 0; }
public boolean	canGoForward()	{ return m_HistoryIndex < (m_HistoryVector.size() - 1); }
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	ModelSelection(Model inModel)
{
m_Model = inModel;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addSelectionListener
------------------------------------------------------------------------------------------------------------------------------------*/
public void	addSelectionListener(SelectionListener inListener)
{
m_ListenerList.add(SelectionListener.class, inListener);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	removeSelectionListener
------------------------------------------------------------------------------------------------------------------------------------*/
public void	removeSelectionListener(SelectionListener inListener)
{
m_ListenerList.remove(SelectionListener.class, inListener);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	fireSelectionChanged
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	fireSelectionChanged(	boolean	inIsElementSelection,
										boolean	inIsMemberSelection,
										boolean inDoSourcePositioning)
{
// Guaranteed to return a non-null array
Object[]	listeners = m_ListenerList.getListenerList();

SelectionEvent	evt = null;

// Process the listeners FIRST to LAST, notifying
// those that are interested in this event
for(int i = 0; i < listeners.length; i += 2)
	if(listeners[i] == SelectionListener.class)
		{
		if(evt == null)
			evt = new SelectionEvent(
						this,
						inIsElementSelection,
						inIsMemberSelection,
						getElement(),
						getMember(),
						inDoSourcePositioning);

		((SelectionListener)listeners[i + 1]).selectionChanged(evt);
		}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	select
------------------------------------------------------------------------------------------------------------------------------------*/
public void		select(Object inObj, boolean inDoSourcePositioning)
{
if(inObj != null)
	{
	boolean	repeated = false;

	// check if this is a repeated selection
	if(	(inObj instanceof MElement && inObj == m_Element && m_Member == null) ||
		(inObj instanceof MMember  && inObj == m_Member))
			repeated = true;

	if(!repeated)
		{
		// clear the history vector beyond the current position
		// by the way, Vector has a removeRange() method, but it's declared protected!

		for(int i = m_HistoryVector.size() - 1; i > m_HistoryIndex; i--)
			m_HistoryVector.remove(i);

		// check if we have reached the maximum capacity (FIFO)

		if(m_HistoryVector.size() >= HISTORY_SIZE)
			m_HistoryVector.remove(0);

		// update the history vector and index

		m_HistoryVector.add(inObj);
		m_HistoryIndex = m_HistoryVector.size() - 1;
		}

	// apply the selection

	if(inObj instanceof MElement)
		setElement((MElement)inObj, inDoSourcePositioning, true);
	else if(inObj instanceof MMember)
		setMember((MMember)inObj, inDoSourcePositioning, true);
	}
else
	{
	setElement(null, false, true);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setElement
------------------------------------------------------------------------------------------------------------------------------------*/
private void	setElement(	MElement	inElement,
							boolean		inDoSourcePositioning,
							boolean		inSaveSelection)
{
m_Member	= null;
m_Element	= inElement;

if(inSaveSelection)
	save();

fireSelectionChanged(true, false, inDoSourcePositioning);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setMember
------------------------------------------------------------------------------------------------------------------------------------*/
private void	setMember(	MMember		inMember,
							boolean		inDoSourcePositioning,
							boolean		inSaveSelection)
{
boolean		elementChanged = (m_Element != inMember.getContainer());

m_Member	= inMember;
m_Element	= m_Member.getContainer();

if(inSaveSelection)
	save();

fireSelectionChanged(elementChanged, true, inDoSourcePositioning);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	back
------------------------------------------------------------------------------------------------------------------------------------*/
public void		back()
{
if(canGoBack())
	{
	Object		obj = m_HistoryVector.get(--m_HistoryIndex);

		 if(obj instanceof MElement)	setElement((MElement)obj, true, true);
	else if(obj instanceof MMember)		setMember((MMember)obj, true, true);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	forward
------------------------------------------------------------------------------------------------------------------------------------*/
public void		forward()
{
if(canGoForward())
	{
	Object		obj = m_HistoryVector.get(++m_HistoryIndex);

		 if(obj instanceof MElement)	setElement((MElement)obj, true, true);
	else if(obj instanceof MMember)		setMember((MMember)obj, true, true);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	clearHistory
------------------------------------------------------------------------------------------------------------------------------------*/
public void		clearHistory()
{
m_HistoryVector.clear();
m_HistoryIndex = -1;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doBackPopup
------------------------------------------------------------------------------------------------------------------------------------*/
public void		doBackPopup(Component inRefComponent)
{
if(canGoBack())
	{
	m_HistoryPopup.removeAll();
	m_HistoryPopupListener.m_IsBackward = true;

	for(int i = m_HistoryIndex - 1; i >= 0; i--)
		addHistoryPopupItem(i);

	GUIUtilities.showPopupMenu(
		m_HistoryPopup, inRefComponent, 0, inRefComponent.getHeight(), false);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doForwardPopup
------------------------------------------------------------------------------------------------------------------------------------*/
public void		doForwardPopup(Component inRefComponent)
{
if(canGoForward())
	{
	m_HistoryPopup.removeAll();
	m_HistoryPopupListener.m_IsBackward = false;

	for(int i = m_HistoryIndex + 1; i < m_HistoryVector.size(); i++)
		addHistoryPopupItem(i);

	GUIUtilities.showPopupMenu(
		m_HistoryPopup, inRefComponent, 0, inRefComponent.getHeight(), false);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addHistoryPopupItem
------------------------------------------------------------------------------------------------------------------------------------*/
private void	addHistoryPopupItem(int inObjIndex)
{
Object		obj		= m_HistoryVector.get(inObjIndex);
JMenuItem	item	= new JMenuItem();

item.setText(((MMember)obj).getIndexText());
item.setFont(JExplorerPlugin.getUIFont());
item.addActionListener(m_HistoryPopupListener);
m_HistoryPopup.add(item);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	save
------------------------------------------------------------------------------------------------------------------------------------*/
public void		save()
{
m_SavedElementName		= (m_Element != null ? m_Element.getQualifiedName() : null);
m_SavedMemberName		= (m_Member != null ? m_Member.getLocalName() : null);
m_SavedMemberSignature	= (m_Member != null ? m_Member.getSignature() : null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	restore
------------------------------------------------------------------------------------------------------------------------------------*/
public void		restore()
{
if(m_SavedElementName != null)
	{
	MElement	e = m_Model.getElement(m_SavedElementName);

	if(e != null)			// if the element still exists
		{
		if(m_SavedMemberName != null)
			{
			MMember	m = e.getMember(m_SavedMemberName, m_SavedMemberSignature);

			// if the member still exists, select it
			if(m != null)	setMember(m, false, false);
			// if the member no longer exists, select its former container
			else			setElement(e, false, false);
			}
		else
			{
			setElement(e, false, false);
			}
		}
	else					// if the element no longer exists
		{
		setElement(null, false, false);
		}
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	PopupListener [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class PopupListener implements ActionListener
{
private boolean		m_IsBackward;

public void	actionPerformed(ActionEvent inEvt)
{
int		itemIndex = m_HistoryPopup.getComponentIndex((JMenuItem)inEvt.getSource());

if(m_IsBackward)	m_HistoryIndex -= (1 + itemIndex);
else				m_HistoryIndex += (1 + itemIndex);

Object	obj = m_HistoryVector.get(m_HistoryIndex);

	 if(obj instanceof MElement)	setElement((MElement)obj, true, true);
else if(obj instanceof MMember)		setMember((MMember)obj, true, true);
}
}
}

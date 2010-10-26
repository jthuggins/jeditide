/*
 * SelectionEvent.java
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

import java.util.EventObject;

import jexplorer.model.members.MElement;
import jexplorer.model.members.MMember;
/*------------------------------------------------------------------------------------------------------------------------------------
	SelectionEvent
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * An event that characterizes a change in the current Model selection.
 *
 * @author Amedeo Farello
 * @version 0.1, 2003.03.26
 */
public class SelectionEvent extends EventObject
{
private boolean		m_ElementSelection;
private boolean		m_MemberSelection;
private MElement	m_SelectedElement;
private MMember		m_SelectedMember;
private boolean		m_SourcePositioningRequested;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>SelectionEvent</code>.
 *
 * @param inSource						The source of this event.
 * @param inElementSelection			<code>true</code> if the selected MElement changes.
 * @param inMemberSelection				<code>true</code> if the selected MMember changes.
 * @param inSelectedElement				The currently selected MElement.
 * @param inSelectedMember				The currently selected MMember.
 * @param inSourcePositioningRequested	<code>true</code> if source must be positioned.
 */
public SelectionEvent(	Object	inSource,
						boolean	inElementSelection,
						boolean	inMemberSelection,
						MElement inSelectedElement,
						MMember	inSelectedMember,
						boolean	inSourcePositioningRequested)
{
super(inSource);

m_ElementSelection				= inElementSelection;
m_MemberSelection				= inMemberSelection;
m_SelectedElement				= inSelectedElement;
m_SelectedMember				= inSelectedMember;
m_SourcePositioningRequested	= inSourcePositioningRequested;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isNullSelection
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Checks if this is a <code>null</code> selection.
 * @return <code>true</code> if this is a <code>null</code> selection
 */
public boolean isNullSelection()
	{ return m_SelectedElement == null; }
/*------------------------------------------------------------------------------------------------------------------------------------
	isElementSelection
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Checks if this is an MElement selection.
 * @return <code>true</code> if this is an MElement selection
 */
public boolean isElementSelection()
	{ return m_ElementSelection; }
/*------------------------------------------------------------------------------------------------------------------------------------
	isMemberSelection
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Checks if this is a MMember selection.
 * @return <code>true</code> if this is a MMember selection
 */
public boolean isMemberSelection()
	{ return m_MemberSelection; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getSelectedElement
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the selected MElement.
 * @return The selected MElement
 */
public MElement getSelectedElement()
	{ return m_SelectedElement; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getSelectedMember
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the selected MMember.
 * @return The selected MMember
 */
public MMember getSelectedMember()
	{ return m_SelectedMember; }
/*------------------------------------------------------------------------------------------------------------------------------------
	isSourcePositioningRequested
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Checks if this source positioning is requested.
 * @return <code>true</code> if source positioning is requested
 */
public boolean isSourcePositioningRequested()
	{ return m_SourcePositioningRequested; }
}

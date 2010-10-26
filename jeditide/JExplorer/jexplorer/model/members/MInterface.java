/*
 * MInterface.java
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

package jexplorer.model.members;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Insets;
import java.awt.Component;
import java.awt.Color;

import java.util.TreeSet;
import java.util.Iterator;

import jexplorer.JExplorerPlugin;
import jexplorer.model.Model;
import jexplorer.gui.graph.Graph;

import jexplorer.parser.source.SourcePos;
import jexplorer.parser.source.Comment;
/*------------------------------------------------------------------------------------------------------------------------------------
	MInterface
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Represents a java interface.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 */
public final class MInterface extends MElement
{
private TreeSet		m_Superinterfaces;
private TreeSet		m_Subinterfaces;
private TreeSet		m_Implementors;
/*------------------------------------------------------------------------------------------------------------------------------------
	compact methods
------------------------------------------------------------------------------------------------------------------------------------*/
		String	getTypeName()	{ return "interface"; }
public	Color	getTextColor()	{ return JExplorerPlugin.getColor(COLOR_INTERFACE); }
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	MInterface(	Model		inModel,			// reference model
					byte		inAccess,			// access specifier
					boolean		inIsFinal,			// final flag
					boolean		inIsDeprecated,		// deprecated flag
					String		inQualifiedName,	// qualified name
					Comment		inComment,			// comment
					SourcePos	inSourcePos)		// reference to source
{
super(inModel, inAccess, true, inIsFinal, false, inIsDeprecated, inQualifiedName, inComment, inSourcePos);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addSuperinterface
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds a superinterface to this interface.
 * @param inSuperinterface	The superinterface to add.
 */
public void	addSuperinterface(MInterface inSuperinterface)
{
if(m_Superinterfaces == null)
	m_Superinterfaces = new TreeSet(MElementComparator.getInstance());

m_Superinterfaces.add(inSuperinterface);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	hasSuperinterfaces
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true if this interface has superinterfaces.
 */
public boolean	hasSuperinterfaces()
{
return(m_Superinterfaces != null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getSuperinterfacesIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over this interface's superinterfaces
 * or <code>null</code> if this interface has no superinterfaces.
 */
public Iterator	getSuperinterfacesIterator()
{
return m_Superinterfaces != null ? m_Superinterfaces.iterator() : null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addSubinterface
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds a subinterface to this interface.
 * @param inSubinterface	The subinterface to add.
 */
public void	addSubinterface(MInterface inSubinterface)
{
if(m_Subinterfaces == null)
	m_Subinterfaces = new TreeSet(MElementComparator.getInstance());

m_Subinterfaces.add(inSubinterface);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	hasSubinterfaces
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true if this interface has subinterfaces.
 */
public boolean	hasSubinterfaces()
{
return(m_Subinterfaces != null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getSubinterfacesIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over this interface's subinterfaces
 * or <code>null</code> if this interface has no subinterfaces.
 */
public Iterator	getSubinterfacesIterator()
{
return m_Subinterfaces != null ? m_Subinterfaces.iterator() : null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addImplementor
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds an implementor to this interface.
 * @param inImplementor	The implementor to add.
 */
public void	addImplementor(MElement inImplementor)
{
if(m_Implementors == null)
	m_Implementors = new TreeSet(MElementComparator.getInstance());

m_Implementors.add(inImplementor);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	hasImplementors
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true if this interface has implementors.
 */
public boolean	hasImplementors()
{
return(m_Implementors != null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getImplementorsIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over this interface's implementors
 * or <code>null</code> if this interface has no implementors.
 */
public Iterator	getImplementorsIterator()
{
return m_Implementors != null ? m_Implementors.iterator() : null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getLayoutDescendants
------------------------------------------------------------------------------------------------------------------------------------*/
TreeSet		getLayoutDescendants()
{
switch(getProperties().getChoice(PG_CURRENT_LAYOUT))
	{
	case LAYOUT_DERIVATION:		return(m_Subinterfaces);
	case LAYOUT_IMPLEMENTATION:	return(m_Implementors);
	case LAYOUT_INCLUSION:		return(m_Innerclasses);
	}

return(null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeInheritanceLevel
------------------------------------------------------------------------------------------------------------------------------------*/
int			computeInheritanceLevel(int inCurrLevel)
{
int			level	= inCurrLevel;

for(Iterator it = getSuperinterfacesIterator();
	it != null && it.hasNext();)
	{
	MInterface	i = (MInterface)it.next();
	level = Math.max(level, i.computeInheritanceLevel(inCurrLevel + 1));
	}

return(level);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeImplementationLevel
------------------------------------------------------------------------------------------------------------------------------------*/
int		computeImplementationLevel(int inCurrLevel)
{
return(computeInheritanceLevel(inCurrLevel));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isInsertable
------------------------------------------------------------------------------------------------------------------------------------*/
boolean		isInsertable()
{
if(!getProperties().getOption(PI_SHOW_INTERFACES))
	return(false);

return(super.isInsertable());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	buildAncestorsTable
------------------------------------------------------------------------------------------------------------------------------------*/
public void		buildAncestorsTable(TreeSet inTable)
{
switch(getProperties().getChoice(PG_CURRENT_LAYOUT))
	{
	case LAYOUT_DERIVATION:
	case LAYOUT_IMPLEMENTATION:

		// superinterfaces
		for(Iterator it = getSuperinterfacesIterator();
			it != null && it.hasNext();)
			{
			MInterface	i = (MInterface)it.next();

			inTable.add(i);
			i.buildAncestorsTable(inTable);
			}

		break;

	default:
		super.buildAncestorsTable(inTable);
		break;
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	buildDescendantsTable
------------------------------------------------------------------------------------------------------------------------------------*/
public void		buildDescendantsTable(TreeSet inTable)
{
Iterator	it;

switch(getProperties().getChoice(PG_CURRENT_LAYOUT))
	{
/*	case LAYOUT_DERIVATION:
	case LAYOUT_IMPLEMENTATION:

		// subinterfaces
		for(it = getSubinterfacesIterator();
			it != null && it.hasNext();)
			{
			MInterface	i = (MInterface)it.next();

			inTable.add(i);
			i.buildDescendantsTable(inTable);
			}

		// implementors
		for(it = getImplementorsIterator();
			it != null && it.hasNext();)
			{
			MElement	e = (MElement)it.next();

			inTable.add(e);
			e.buildDescendantsTable(inTable);
			}

		break;
*/
	case LAYOUT_DERIVATION:

		// subinterfaces
		for(it = getSubinterfacesIterator();
			it != null && it.hasNext();)
			{
			MInterface	i = (MInterface)it.next();

			inTable.add(i);
			i.buildDescendantsTable(inTable);
			}

		break;

	case LAYOUT_IMPLEMENTATION:

		// implementors
		for(it = getImplementorsIterator();
			it != null && it.hasNext();)
			{
			MElement	e = (MElement)it.next();

			inTable.add(e);
			e.buildDescendantsTable(inTable);
			}

		break;

	default:
		super.buildDescendantsTable(inTable);
		break;
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	draw
------------------------------------------------------------------------------------------------------------------------------------*/
public void		draw(Graphics2D g, Component inComponent)
{
Insets	insets = getInsets();

// draw the superinterfaces input plug
if((getProperties().isDerivationVisible() || getProperties().isImplementationVisible()) &&
	hasSuperinterfaces())
	{
	Point	p = getInputMidPlug(LOCAL_COORDS);
	g.setPaint(JExplorerPlugin.getColor(COLOR_INTERFACE));
	g.drawLine(p.x, p.y, p.x + insets.left, p.y);
	}

// draw the subinterfaces output plug
if((getProperties().isDerivationVisible() || getProperties().isImplementationVisible()) &&
	hasSubinterfaces())
	{
	Point	p = getOutputMidPlug(LOCAL_COORDS);
	g.setPaint(JExplorerPlugin.getColor(COLOR_INTERFACE));
	g.drawLine(p.x - insets.right, p.y, p.x, p.y);
	}

// draw the implementors output plug
if(	getProperties().isImplementationVisible() &&
	hasImplementors())
	{
	Point	p = getOutputBtmPlug(LOCAL_COORDS);
	g.setPaint(JExplorerPlugin.getColor(COLOR_INTERFACE));
	g.drawLine(p.x - insets.right, p.y, p.x, p.y);
	}

super.draw(g, inComponent);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	drawAllConnections
------------------------------------------------------------------------------------------------------------------------------------*/
public	void	drawAllConnections(Graph inGraph, Graphics2D inGraphics)
{
// draw connections to superinterfaces (if any)

if((getProperties().isDerivationVisible() || getProperties().isImplementationVisible()) &&
	getProperties().getOption(PI_SHOW_INTERFACES))
	{
	for(Iterator it = getSuperinterfacesIterator();
		it != null && it.hasNext();)
		{
		MInterface	i = (MInterface)it.next();

		if(i.isInserted())
			drawConnection(	getInputMidPlug(GLOBAL_COORDS),
							i.getOutputMidPlug(GLOBAL_COORDS),
							JExplorerPlugin.getColor(COLOR_INTERFACE),
							inGraphics);
		}
	}

super.drawAllConnections(inGraph, inGraphics);
}
}


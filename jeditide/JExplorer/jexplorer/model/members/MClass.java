/*
 * MClass.java
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

import jexplorer.parser.source.Comment;
import jexplorer.parser.source.SourcePos;
/*------------------------------------------------------------------------------------------------------------------------------------
	MClass
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Represents a java class.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 */
public final class MClass extends MElement
{
private MElement	m_Superclass;
private TreeSet	m_Interfaces;
private TreeSet	m_Subclasses;
/*------------------------------------------------------------------------------------------------------------------------------------
	compact methods
------------------------------------------------------------------------------------------------------------------------------------*/
public void	setSuperclass(MClass inSuperClass)	{ m_Superclass = inSuperClass; }

		String	getTypeName()	{ return "class"; }
public	Color	getTextColor()	{ return JExplorerPlugin.getColor(COLOR_CLASS); }
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	MClass(	Model		inModel,			// reference model
				byte		inAccess,			// access specifier
				boolean		inIsAbstract,		// abstract flag
				boolean		inIsFinal,			// final flag
				boolean		inIsStatic,			// static flag
				boolean		inIsDeprecated,		// deprecated flag
				String		inQualifiedName,	// qualified name
				Comment		inComment,			// comment
				SourcePos	inSourcePos)		// reference to source
{
super(inModel, inAccess, inIsAbstract, inIsFinal, inIsStatic, inIsDeprecated, inQualifiedName, inComment, inSourcePos);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addInterface
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds an interface to this class.
 * @param inInterface	The interface to add.
 */
public void	addInterface(MInterface inInterface)
{
if(m_Interfaces == null)
	m_Interfaces = new TreeSet(MElementComparator.getInstance());

m_Interfaces.add(inInterface);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	hasInterfaces
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true if this class has interfaces.
 */
public boolean	hasInterfaces()
{
return(m_Interfaces != null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getInterfacesIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over this class' interfaces
 * or <code>null</code> if this class has no interfaces.
 */
public Iterator	getInterfacesIterator()
{
return m_Interfaces != null ? m_Interfaces.iterator() : null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addSubclass
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds a subclass to this class.
 * @param inSubClass	The subclass to add.
 */
public void	addSubclass(MClass inSubClass)
{
if(m_Subclasses == null)
	m_Subclasses = new TreeSet(MElementComparator.getInstance());

m_Subclasses.add(inSubClass);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	hasSubclasses
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true if this class has subclasses.
 */
public boolean	hasSubclasses()
{
return(m_Subclasses != null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getSubclassesIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over this class' subclasses
 * or <code>null</code> if this class has no subclasses.
 */
public Iterator	getSubclassesIterator()
{
return m_Subclasses != null ? m_Subclasses.iterator() : null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getLayoutDescendants
------------------------------------------------------------------------------------------------------------------------------------*/
TreeSet		getLayoutDescendants()
{
switch(getProperties().getChoice(PG_CURRENT_LAYOUT))
	{
	case LAYOUT_DERIVATION:		return(m_Subclasses);
	case LAYOUT_IMPLEMENTATION:	return(null);
	case LAYOUT_INCLUSION:		return(m_Innerclasses);
	}

return(null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isInsertable
------------------------------------------------------------------------------------------------------------------------------------*/
boolean		isInsertable()
{
if(!getProperties().getOption(PI_SHOW_CLASSES))
	return(false);

return(super.isInsertable());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeInheritanceLevel
------------------------------------------------------------------------------------------------------------------------------------*/
int			computeInheritanceLevel(int inCurrLevel)
{
if(m_Superclass != null)
	return(m_Superclass.computeInheritanceLevel(inCurrLevel + 1));

return(inCurrLevel);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeImplementationLevel
------------------------------------------------------------------------------------------------------------------------------------*/
int			computeImplementationLevel(int inCurrLevel)
{
int			level	= inCurrLevel;

for(Iterator it = getInterfacesIterator();
	it != null && it.hasNext();)
	{
	MInterface	i = (MInterface)it.next();
	level = Math.max(level, i.computeImplementationLevel(inCurrLevel + 1));
	}

return(level);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	buildAncestorsTable
------------------------------------------------------------------------------------------------------------------------------------*/
public void		buildAncestorsTable(TreeSet inTable)
{
Iterator	it;

switch(getProperties().getChoice(PG_CURRENT_LAYOUT))
	{
	case LAYOUT_DERIVATION:

		// superclass
		if(m_Superclass != null)
			{
			inTable.add(m_Superclass);
			m_Superclass.buildAncestorsTable(inTable);
			}

		// interfaces
		for(it = getInterfacesIterator();
			it != null && it.hasNext();)
			{
			MInterface	i = (MInterface)it.next();

			inTable.add(i);
			i.buildAncestorsTable(inTable);
			}

		break;

	case LAYOUT_IMPLEMENTATION:

		// interfaces
		for(it = getInterfacesIterator();
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
	case LAYOUT_DERIVATION:

		// subclasses
		for(it = getSubclassesIterator();
			it != null && it.hasNext();)
			{
			MClass	c = (MClass)it.next();

			inTable.add(c);
			c.buildDescendantsTable(inTable);
			}

		break;

	case LAYOUT_IMPLEMENTATION:
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

// draw the superclass input plug
if(	getProperties().getOption(PI_SHOW_CLASSES) &&
	getProperties().isDerivationVisible() &&
	m_Superclass != null)
	{
	Point	p = getInputMidPlug(LOCAL_COORDS);
	g.setPaint(JExplorerPlugin.getColor(COLOR_CLASS));
	g.drawLine(p.x, p.y, p.x + insets.left, p.y);
	}

// draw the interfaces input plug
if(	getProperties().getOption(PI_SHOW_INTERFACES) &&
	getProperties().isImplementationVisible() &&
	hasInterfaces())
	{
	Point	p = getInputBtmPlug(LOCAL_COORDS);
	g.setPaint(JExplorerPlugin.getColor(COLOR_INTERFACE));
	g.drawLine(p.x, p.y, p.x + insets.left, p.y);
	}

// draw the subclasses output plug
if(	getProperties().getOption(PI_SHOW_CLASSES) &&
	getProperties().isDerivationVisible() &&
	hasSubclasses())
	{
	Point	p = getOutputMidPlug(LOCAL_COORDS);
	g.setPaint(JExplorerPlugin.getColor(COLOR_CLASS));
	g.drawLine(p.x - insets.right, p.y, p.x, p.y);
	}

super.draw(g, inComponent);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	drawAllConnections
------------------------------------------------------------------------------------------------------------------------------------*/
public	void	drawAllConnections(Graph inGraph, Graphics2D inGraphics)
{
// draw connection to superclass (if any)

if(	getProperties().isDerivationVisible() &&
	getProperties().getOption(PI_SHOW_CLASSES) &&
	m_Superclass != null &&
	m_Superclass.isInserted())
	{
	drawConnection(	getInputMidPlug(GLOBAL_COORDS),
					m_Superclass.getOutputMidPlug(GLOBAL_COORDS),
					JExplorerPlugin.getColor(COLOR_CLASS),
					inGraphics);
	}

// draw connection to interfaces (if any)

if(	getProperties().isImplementationVisible() &&
	getProperties().getOption(PI_SHOW_INTERFACES))
	{
	for(Iterator it = getInterfacesIterator();
		it != null && it.hasNext();)
		{
		MInterface	i = (MInterface)it.next();

		if(i.isInserted())
			drawConnection(	getInputBtmPlug(GLOBAL_COORDS),
							i.getOutputBtmPlug(GLOBAL_COORDS),
							JExplorerPlugin.getColor(COLOR_INTERFACE),
							inGraphics);
		}
	}

super.drawAllConnections(inGraph, inGraphics);
}
}


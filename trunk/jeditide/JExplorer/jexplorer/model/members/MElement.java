/*
 * MElement.java
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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.BasicStroke;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.UIManager;

import java.util.TreeSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;

import org.gjt.sp.jedit.Buffer;

import jexplorer.JExplorerPlugin;
import jexplorer.JExplorerConstants;

import jexplorer.gui.graph.Graph;

import jexplorer.model.Model;

import jexplorer.model.properties.ModelProperties;

import jexplorer.parser.source.Comment;
import jexplorer.parser.source.SourcePos;
/*------------------------------------------------------------------------------------------------------------------------------------
	MElement
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Base class for classes and interfaces.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 */
public abstract class MElement extends MMember
{
private Model		m_Model;

private Vector		m_Members		= new Vector();
protected TreeSet	m_Innerclasses;

private String		m_IndexName		= "";
private String		m_DetailsName	= "";

// graph related fields

private Dimension	m_Size			= new Dimension();
private Rectangle	m_Frame			= new Rectangle();
private Insets		m_Insets		= new Insets(DRAWING_BOX_SPACING, DRAWING_BOX_SPACING, DRAWING_BOX_SPACING, DRAWING_BOX_SPACING);
private int			m_HLocation		= LOCATION_NULL;
private int			m_SubVSpan		= VSPAN_NULL;
private	boolean		m_IsInserted	= false;
private Point		m_GraphLocation	= new Point();

// "plugs" coordinates selectors
static protected final int	LOCAL_COORDS	= 1;
static protected final int	GLOBAL_COORDS	= 2;

// common stroke for inner classes contour
static private	Stroke		s_InnersStroke = new BasicStroke(
	1.0f,					// line width
	BasicStroke.CAP_BUTT,	// cap type
	BasicStroke.JOIN_BEVEL,	// join type
	0,						// miter level
	new float[] {1.0f},		// dash pattern
	0.0f);					// starting dash phase
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	MElement(Model		inModel,			// reference model
				byte		inAccess,			// access specifier
				boolean		inIsAbstract,		// abstract flag
				boolean		inIsFinal,			// final flag
				boolean		inIsStatic,			// static flag
				boolean		inIsDeprecated,		// deprecated flag
				String		inQualifiedName,	// qualified name
				Comment		inComment,			// comment
				SourcePos	inSourcePos)		// reference to source
{
super(null, inQualifiedName, inAccess, inComment, inSourcePos);

m_Model = inModel;

if(inIsAbstract)	m_Modifiers |= MOD_ABSTRACT;
if(inIsFinal)		m_Modifiers |= MOD_FINAL;
if(inIsStatic)		m_Modifiers |= MOD_STATIC;
if(inIsDeprecated)	m_Modifiers |= MOD_DEPRECATED;

computeToolTipText();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	compact methods
------------------------------------------------------------------------------------------------------------------------------------*/
public	String	getCanonicalText()	{ return getIndexText(); }
public	String	getDetailsText	()	{ return m_DetailsName; }
public	String	getIndexText	()	{ return m_IndexName; }
public	Color	getPackageColor	()	{ return getProperties().getPackageColor(getQualifiedName()); }

public void		setOuterclass(MElement inObj)	{ m_Container = inObj; }
public boolean	isNestedElement()				{ return m_Container != null; }

abstract 		String	getTypeName();

public	ModelProperties	getProperties()		{ return m_Model.getProperties(); }

// graph related methods

public 	void		setHLocation	(int inValue)	{ m_HLocation = inValue; }
public 	int			getHLocation	()				{ return m_HLocation; }

public	void		clearSubVSpan	()				{ m_SubVSpan = VSPAN_NULL; }
public 	int			getMaxVSpan		()				{ return isInsertable() ? Math.max(m_SubVSpan, getHeight()) : 0; }
private	int			getSubVSpan		()				{ return m_SubVSpan; }

public	boolean		isInserted		()				{ return m_IsInserted; }
public	void		clearInserted	()				{ m_IsInserted = false; }

public	int			getGraphX		()				{ return m_GraphLocation.x; }
public	int			getGraphY		()				{ return m_GraphLocation.y; }

public	int			getWidth		()				{ return m_Size.width; }
public	int			getHeight		()				{ return m_Size.height; }

public	Insets		getInsets		()				{ return m_Insets; }

abstract TreeSet	getLayoutDescendants		();
abstract int		computeInheritanceLevel		(int inCurrLevel);
abstract int		computeImplementationLevel	(int inCurrLevel);
/*------------------------------------------------------------------------------------------------------------------------------------
	debugStatus
------------------------------------------------------------------------------------------------------------------------------------*/
public String	toString()
{
StringBuffer	buf = new StringBuffer();

buf.append("\nQualifiedName: ");
buf.append(getQualifiedName());

buf.append("\nDisplayName:   ");
buf.append(m_IndexName);

buf.append("\n\tHLocation:   ");
buf.append(m_HLocation);

buf.append("\n\tVSpan:       ");
buf.append(m_SubVSpan);

buf.append("\n\tINSERTABLE:  ");
buf.append(isInsertable());

buf.append("\n\tINSERTED:    ");
buf.append(isInserted());

return(buf.toString());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	insert
------------------------------------------------------------------------------------------------------------------------------------*/
public	boolean		insert(int inCurrHPos, int inCurrVPos)
{
if(!isInsertable())
	return(false);

// interfaces can have multiple inheritance, we must avoid multiple insertions
if(m_IsInserted)
	return(false);

// insert this element

m_IsInserted = true;
m_GraphLocation.x = inCurrHPos;
m_GraphLocation.y = inCurrVPos + getMaxVSpan() / 2 - getHeight() / 2;

// insert the descendants

if(	getLayoutDescendants() != null &&
	getLayoutDescendants().size() > 0)
	{
	Iterator	iter	= getLayoutDescendants().iterator();
	int			y		= inCurrVPos + Math.max(0, getHeight() - m_SubVSpan) / 2;

	while(iter.hasNext())
		{
		MElement	subclass = (MElement)iter.next();

		if(m_Model.isElementWorking(subclass))
			if(subclass.insert(inCurrHPos + getWidth(), y))
				y += subclass.getMaxVSpan();
		}
	}

return(true);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isInsertable
------------------------------------------------------------------------------------------------------------------------------------*/
boolean		isInsertable()
{
if(!m_Model.isElementWorking(this))
	return(false);

if(isNestedElement() && !getProperties().getOption(PI_SHOW_NESTED))
	return(false);

return(true);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getLocalName
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the local name of this element.
 */
public String	getLocalName()
{
int		lastDotIndex = m_Name.lastIndexOf('.');
return(m_Name.substring(lastDotIndex + 1));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getQualifiedName
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the qualified name of this element.
 */
public String	getQualifiedName()
{
return(m_Name);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getShortName
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getShortName()
{
if(isNestedElement())	return(m_Container.getShortName() + "." + getLocalName());
else					return(getLocalName());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getPackage
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the package this element belongs to.
 */
public String	getPackage()
{
if(isNestedElement())
	{
	return(m_Container.getPackage());
	}
else
	{
	int		lastDotIndex = m_Name.lastIndexOf('.');
	return(m_Name.substring(0, lastDotIndex));
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addMember
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds a member to this element.
 * @param inMember	The member to add.
 */
public void	addMember(MMember inMember)
{
m_Members.add(inMember);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getMember
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Retrieve a member from this element or <code>null</code> if
 * this element does not have such a member.
 * @param inLocalName	The member name.
 * @param inSignature	The member signature.
 */
public MMember	getMember(String inLocalName, String inSignature)
{
for(Enumeration e = getMembersEnumeration(); e.hasMoreElements();)
	{
	MMember	m = (MMember)e.nextElement();

	if(	m.getLocalName().equals(inLocalName) &&
		m.getSignature().equals(inSignature))
			return(m);
	}

return(null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getMembersEnumeration
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Enumeration</code> over this element's members.
 */
public Enumeration	getMembersEnumeration()
{
return m_Members.elements();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addInnerclass
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds a nested element (<code>MClass</code> or <code>MInterface</code>) to this element.
 * @param inInnerClass	The element to add.
 */
public void	addInnerclass(MElement inInnerClass)
{
if(m_Innerclasses == null)
	m_Innerclasses = new TreeSet(MElementComparator.getInstance());

m_Innerclasses.add(inInnerClass);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	hasNestedElements
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true if this element has nested elements.
 */
public boolean	hasNestedElements()
{
return(m_Innerclasses != null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getNestedElementsIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over this element's nested elements
 * or <code>null</code> if this element has no nested elements.
 */
public Iterator	getNestedElementsIterator()
{
return m_Innerclasses != null ? m_Innerclasses.iterator() : null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeDisplayNames
------------------------------------------------------------------------------------------------------------------------------------*/
void		computeDisplayNames()
{
if(getProperties().getOption(PM_USE_SHORT_NAMES))
	{
	m_IndexName = getShortName();
	m_DetailsName = getLocalName();
	}
else
	{
	m_IndexName = getQualifiedName();
	m_DetailsName = getQualifiedName();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeSize
------------------------------------------------------------------------------------------------------------------------------------*/
void		computeSize()
{
FontMetrics fm  = JExplorerPlugin.getFontMetrics(getFont());
Insets      m   = getInsets();
int         w   = m.left + fm.stringWidth(m_IndexName) + m.right + 2 * DRAWING_TEXT_HMARGIN,
			h   = m.top + fm.getHeight() + m.bottom + 2 * DRAWING_TEXT_VMARGIN;

if(getProperties().getOption(PM_SHOW_ACCESS_SYMBOLS))
	{
	ImageIcon	icon = null;

	switch(m_Access)
		{
		case ACCESS_PUBLIC:		icon = ICON_PUBLIC;		break;
		case ACCESS_PROTECTED:	icon = ICON_PROTECTED;	break;
		case ACCESS_PRIVATE:	icon = ICON_PRIVATE;	break;
		case ACCESS_PACKAGE:	icon = ICON_PACKAGE;	break;
		}

	w += (icon.getIconWidth() + DRAWING_TEXT_HMARGIN);
	}

m_Size.setSize(w, h);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeFrame
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* Computes the element frame in local coordinates.
*/
void		computeFrame()
{
Insets      i = getInsets();

m_Frame.setBounds(	i.left,
					i.top,
					m_Size.width - (i.left + i.right),
					m_Size.height - (i.top + i.bottom));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeToolTipText
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	computeToolTipText()
{
StringBuffer	buf = new StringBuffer(getTooltipPrefix());

buf.append(getTypeName());
buf.append(" ");
buf.append(getQualifiedName());

m_ToolTipText = buf.toString();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeLocationUpward
------------------------------------------------------------------------------------------------------------------------------------*/
public int	computeLocationUpward(	int	inLayoutType,
									int inCurrLevel)
{
switch(inLayoutType)
	{
	case LAYOUT_DERIVATION:		return(computeInheritanceLevel		(inCurrLevel));
	case LAYOUT_IMPLEMENTATION:	return(computeImplementationLevel	(inCurrLevel));
	case LAYOUT_INCLUSION:		return(computeInclusionLevel		(inCurrLevel));
	}

return(-1);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeInclusionLevel
------------------------------------------------------------------------------------------------------------------------------------*/
int			computeInclusionLevel(int inCurrLevel)
{
if(m_Container != null)
	return(m_Container.computeInclusionLevel(inCurrLevel + 1));

return(inCurrLevel);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeLocation
------------------------------------------------------------------------------------------------------------------------------------*/
public void		computeLocation(int inCurrLevel)
{
if(	isInsertable() &&					// is visualization allowed?
	m_HLocation == LOCATION_NULL)		// is yet to be computed?
	{
	if(getLayoutDescendants() != null)	// non terminal element
		{
		Iterator	iter = getLayoutDescendants().iterator();

		while(iter.hasNext())
			{
			MElement	obj = (MElement)iter.next();

			obj.computeLocation(inCurrLevel + 1);
			}
		}

	m_HLocation = inCurrLevel;
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeSubVSpan
------------------------------------------------------------------------------------------------------------------------------------*/
public void		computeSubVSpan()
{
if(	isInsertable() &&							// is visualization allowed?
	m_SubVSpan == VSPAN_NULL)					// still to be computed?
	{
	if(	getLayoutDescendants() == null ||
		getLayoutDescendants().isEmpty())		// terminal element
		{
		m_SubVSpan = 0;
		}
	else										// non terminal element
		{
		int			span = 0;
		Iterator	iter = getLayoutDescendants().iterator();

		while(iter.hasNext())
			{
			MElement	obj = (MElement)iter.next();

			if(obj.getSubVSpan() == VSPAN_NULL)	// avoid multiple inclusion
				{
				obj.computeSubVSpan();
				span += obj.getMaxVSpan();
				}
			}

		m_SubVSpan = span;
		}
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeAttributes
------------------------------------------------------------------------------------------------------------------------------------*/
public void		computeAttributes()
{
computeDisplayNames();
computeSize();
computeFrame();

for(Enumeration e = getMembersEnumeration(); e.hasMoreElements();)
	{
	MMember	m = (MMember)e.nextElement();

	if(m instanceof MElement)
		((MElement)m).computeAttributes();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeMembers
------------------------------------------------------------------------------------------------------------------------------------*/
public void		computeMembers()
{
m_TreeNode.removeAllChildren();

for(Enumeration e = getMembersEnumeration(); e.hasMoreElements();)
	{
	MMember		m = (MMember)e.nextElement();
	boolean		canAddMember = true;

	if(	m instanceof MElement &&
		!getProperties().getOption(PD_SHOW_NESTED))
			canAddMember = false;

	if(	m instanceof MField &&
		!getProperties().getOption(PD_SHOW_FIELDS))
			canAddMember = false;

	if(	m instanceof MConstructor &&
		!getProperties().getOption(PD_SHOW_CONSTRUCTORS))
			canAddMember = false;

	if(	m instanceof MMethod &&
		!getProperties().getOption(PD_SHOW_METHODS))
			canAddMember = false;

	if(	m.isDeprecated() &&
		!getProperties().getOption(PM_SHOW_DEPRECATED))
			canAddMember = false;

	switch(m.getAccess())
		{
		case ACCESS_PUBLIC:	
			if(!getProperties().getOption(PD_SHOW_PUBLIC))
				canAddMember = false;
			break;

		case ACCESS_PROTECTED:
			if(!getProperties().getOption(PD_SHOW_PROTECTED))
				canAddMember = false;
			break;

		case ACCESS_PRIVATE:
			if(!getProperties().getOption(PD_SHOW_PRIVATE))
				canAddMember = false;
			break;

		case ACCESS_PACKAGE:
			if(!getProperties().getOption(PD_SHOW_PACKAGE))
				canAddMember = false;
			break;
		}

	if(canAddMember)
		m_TreeNode.add(m.getTreeNode());

	if(m instanceof MElement)
		((MElement)m).computeMembers();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setIndexModelContent
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setIndexModelContent(Vector inVector)
{
boolean		canAddItself = true;

if( isNestedElement() &&
	!getProperties().getOption(PI_SHOW_NESTED))
		canAddItself = false;

if(	this instanceof MClass &&
	!getProperties().getOption(PI_SHOW_CLASSES))
		canAddItself = false;

if(	this instanceof MInterface &&
	!getProperties().getOption(PI_SHOW_INTERFACES))
		canAddItself = false;

if(	isDeprecated() &&
	!getProperties().getOption(PM_SHOW_DEPRECATED))
		canAddItself = false;

switch(getAccess())
	{
	case ACCESS_PUBLIC:	
		if(!getProperties().getOption(PI_SHOW_PUBLIC))
			canAddItself = false;
		break;

	case ACCESS_PROTECTED:
		if(!getProperties().getOption(PI_SHOW_PROTECTED))
			canAddItself = false;
		break;

	case ACCESS_PRIVATE:
		if(!getProperties().getOption(PI_SHOW_PRIVATE))
			canAddItself = false;
		break;

	case ACCESS_PACKAGE:
		if(!getProperties().getOption(PI_SHOW_PACKAGE))
			canAddItself = false;
		break;
	}

if(canAddItself)
	inVector.add(this);

for(Enumeration e = getMembersEnumeration(); e.hasMoreElements();)
	{
	MMember		m = (MMember)e.nextElement();
	boolean		canAddMember = true;

	if(m instanceof MElement)
		{
		((MElement)m).setIndexModelContent(inVector);
		canAddMember = false;
		}

	if(	m instanceof MField &&
		!getProperties().getOption(PI_SHOW_FIELDS))
			canAddMember = false;

	if(	m instanceof MConstructor &&
		!getProperties().getOption(PI_SHOW_CONSTRUCTORS))
			canAddMember = false;

	if(	m instanceof MMethod &&
		!getProperties().getOption(PI_SHOW_METHODS))
			canAddMember = false;

	if(	m.isDeprecated() &&
		!getProperties().getOption(PM_SHOW_DEPRECATED))
			canAddMember = false;

	switch(m.getAccess())
		{
		case ACCESS_PUBLIC:	
			if(!getProperties().getOption(PI_SHOW_PUBLIC))
				canAddMember = false;
			break;

		case ACCESS_PROTECTED:
			if(!getProperties().getOption(PI_SHOW_PROTECTED))
				canAddMember = false;
			break;

		case ACCESS_PRIVATE:
			if(!getProperties().getOption(PI_SHOW_PRIVATE))
				canAddMember = false;
			break;

		case ACCESS_PACKAGE:
			if(!getProperties().getOption(PI_SHOW_PACKAGE))
				canAddMember = false;
			break;
		}

	if(canAddMember)
		inVector.add(m);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	buildAncestorsTable
------------------------------------------------------------------------------------------------------------------------------------*/
public void	buildAncestorsTable(TreeSet inTable)
{
switch(getProperties().getChoice(PG_CURRENT_LAYOUT))
	{
	case LAYOUT_INCLUSION:
		// outer classes
		if(m_Container != null)
			{
			inTable.add(m_Container);
			m_Container.buildAncestorsTable(inTable);
			}
		break;
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	buildDescendantsTable
------------------------------------------------------------------------------------------------------------------------------------*/
public void	buildDescendantsTable(TreeSet inTable)
{
switch(getProperties().getChoice(PG_CURRENT_LAYOUT))
	{
	case LAYOUT_INCLUSION:
		// inner classes
		for(Iterator it = getNestedElementsIterator();
			it != null && it.hasNext();)
			{
			MElement		e = (MElement)it.next();

			inTable.add(e);
			e.buildDescendantsTable(inTable);
			}
		break;
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getInputTopPlug
------------------------------------------------------------------------------------------------------------------------------------*/
protected Point		getInputTopPlug(int inSpace)
{
Point		p = new Point(0, m_Frame.y + (int)(m_Frame.height * .25));

if(inSpace == GLOBAL_COORDS)
	p.translate(m_GraphLocation.x, m_GraphLocation.y);

return(p);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getInputMidPlug
------------------------------------------------------------------------------------------------------------------------------------*/
protected Point		getInputMidPlug(int inSpace)
{
Point		p = new Point(0, m_Frame.y + (int)(m_Frame.height * .5));

if(inSpace == GLOBAL_COORDS)
	p.translate(m_GraphLocation.x, m_GraphLocation.y);

return(p);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getInputBtmPlug
------------------------------------------------------------------------------------------------------------------------------------*/
protected Point		getInputBtmPlug(int inSpace)
{
Point		p = new Point(0, m_Frame.y + (int)(m_Frame.height * .75));

if(inSpace == GLOBAL_COORDS)
	p.translate(m_GraphLocation.x, m_GraphLocation.y);

return(p);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getOutputTopPlug
------------------------------------------------------------------------------------------------------------------------------------*/
protected Point		getOutputTopPlug(int inSpace)
{
Point		p = new Point(getWidth(), m_Frame.y + (int)(m_Frame.height * .25));

if(inSpace == GLOBAL_COORDS)
	p.translate(m_GraphLocation.x, m_GraphLocation.y);

return(p);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getOutputMidPlug
------------------------------------------------------------------------------------------------------------------------------------*/
protected Point		getOutputMidPlug(int inSpace)
{
Point		p = new Point(getWidth(), m_Frame.y + (int)(m_Frame.height * .5));

if(inSpace == GLOBAL_COORDS)
	p.translate(m_GraphLocation.x, m_GraphLocation.y);

return(p);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getOutputBtmPlug
------------------------------------------------------------------------------------------------------------------------------------*/
protected Point		getOutputBtmPlug(int inSpace)
{
Point		p = new Point(getWidth(), m_Frame.y + (int)(m_Frame.height * .75));

if(inSpace == GLOBAL_COORDS)
	p.translate(m_GraphLocation.x, m_GraphLocation.y);

return(p);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	draw
------------------------------------------------------------------------------------------------------------------------------------*/
public void		draw(Graphics2D g, Component inComponent)
{
Insets	insets = getInsets();

// draw the outerclass input plug
if(	isNestedElement() &&
	getProperties().getOption(PI_SHOW_NESTED) &&
	getProperties().isInclusionVisible())
	{
	Point	p = getInputTopPlug(LOCAL_COORDS);
	g.setPaint(JExplorerPlugin.getColor(COLOR_INCLUSION));
	g.drawLine(p.x, p.y, p.x + insets.left, p.y);
	}

// draw the inner classes output plug
if(	getProperties().getOption(PI_SHOW_NESTED) &&
	getProperties().isInclusionVisible() &&
	hasNestedElements())
	{
	Point	p = getOutputTopPlug(LOCAL_COORDS);
	g.setPaint(JExplorerPlugin.getColor(COLOR_INCLUSION));
	g.drawLine(p.x - insets.right, p.y, p.x, p.y);
	}

// paint this element
drawElement(g, inComponent);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	drawElement
------------------------------------------------------------------------------------------------------------------------------------*/
private void	drawElement(Graphics2D g, Component inComponent)
{
int			xPos = m_Frame.x + DRAWING_TEXT_HMARGIN;
ImageIcon	icon = null;

// paint the draw area

if(getProperties().getOption(PM_COLORIZE_PACKAGES))
	g.setPaint(getPackageColor());
else
	g.setPaint(UIManager.getColor("Tree.background"));

g.fillRect(m_Frame.x, m_Frame.y, m_Frame.width, m_Frame.height);

// draw the access specifier icon

if(getProperties().getOption(PM_SHOW_ACCESS_SYMBOLS))
	{
	switch(m_Access)
		{
		case ACCESS_PUBLIC:		icon = ICON_PUBLIC;		break;
		case ACCESS_PROTECTED:	icon = ICON_PROTECTED;	break;
		case ACCESS_PRIVATE:	icon = ICON_PRIVATE;	break;
		case ACCESS_PACKAGE:	icon = ICON_PACKAGE;	break;
		}

	icon.paintIcon(
		inComponent, g, xPos, (getHeight() - icon.getIconHeight()) / 2);

	xPos += (icon.getIconWidth() + DRAWING_TEXT_HMARGIN);
	}

// draw the element's name

g.setPaint(getTextColor());

int		baseLine, underLine;

	 if(isAbstract())
	{
	baseLine	= JExplorerPlugin.getFontAttribute(FONT_ABSTRACT_BASELINE);
	underLine	= JExplorerPlugin.getFontAttribute(FONT_ABSTRACT_UNDERLINE);
	}
else if(isFinal())
	{
	baseLine	= JExplorerPlugin.getFontAttribute(FONT_FINAL_BASELINE);
	underLine	= JExplorerPlugin.getFontAttribute(FONT_FINAL_UNDERLINE);
	}
else
	{
	baseLine	= JExplorerPlugin.getFontAttribute(FONT_NORMAL_BASELINE);
	underLine	= JExplorerPlugin.getFontAttribute(FONT_NORMAL_UNDERLINE);
	}

g.drawString(	m_IndexName,
				xPos,
				m_Frame.y + DRAWING_TEXT_VMARGIN + baseLine);

// underline the name if necessary

if(	getProperties().getOption(PM_SHOW_ACCESS_SYMBOLS) &&
	isStatic())
	{
	int		width = m_Frame.width - 2 * DRAWING_TEXT_HMARGIN;

	if(icon != null)
		width -= (icon.getIconWidth() + DRAWING_TEXT_HMARGIN);

	g.drawLine(	xPos,
				m_Frame.y + underLine,
				xPos + width,
				m_Frame.y + underLine);
	}

// frame the draw area

if(isNestedElement())
	g.setStroke(s_InnersStroke);

g.setPaint(getTextColor());
g.drawRect(m_Frame.x, m_Frame.y, m_Frame.width, m_Frame.height);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	drawConnection
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	drawConnection(	Point		p,
								Point		q,
								Color		inColor,
								Graphics	inGraphics)
{
inGraphics.setColor(inColor);
inGraphics.drawLine(p.x, p.y, q.x, q.y);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	drawAllConnections
------------------------------------------------------------------------------------------------------------------------------------*/
public	void		drawAllConnections(Graph inGraph, Graphics2D inGraphics)
{
// draw connection to outerclass (if any)

if(	isNestedElement() &&
	getProperties().isInclusionVisible() &&
	getProperties().getOption(PI_SHOW_NESTED) &&
	m_Container.isInserted())
	{
	drawConnection(	getInputTopPlug(GLOBAL_COORDS),
					m_Container.getOutputTopPlug(GLOBAL_COORDS),
					JExplorerPlugin.getColor(COLOR_INCLUSION),
					inGraphics);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getObjectAtCaret
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the <code>java.lang.Object</code> that contains the caret
 * or <code>null</code> if nor members neither this element contain the caret.
 */
public Object	getObjectAtCaret(int inCaret, Buffer inBuffer)
{
if(m_SourcePos.getPath().equals(inBuffer.getPath()))
	{
	for(Enumeration e = getMembersEnumeration(); e.hasMoreElements();)
		{
		MMember	m = (MMember)e.nextElement();

		if(m instanceof MElement)
			{
			Object	obj = ((MElement)m).getObjectAtCaret(inCaret, inBuffer);

			if(obj != null)
				return(obj);
			}
		else
			{
			if(m.containsCaret(inCaret, inBuffer))
				return(m);
			}
		}

	if(containsCaret(inCaret, inBuffer))
		return(this);
	}

return(null);
}
}
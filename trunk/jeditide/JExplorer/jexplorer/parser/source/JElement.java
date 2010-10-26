/*
 * JElement.java
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

package jexplorer.parser.source;

import java.util.TreeSet;
import java.util.Iterator;
import java.util.Comparator;
/*------------------------------------------------------------------------------------------------------------------------------------
	JElement
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Base class for classes and interfaces.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 */
public abstract class JElement extends JMember
{
private final static ElementsComparator		s_ElementsComparator	= new ElementsComparator();
private final static FieldsComparator		s_FieldComparator		= new FieldsComparator();
private final static ExecutableComparator	s_ExecutableComparator	= new ExecutableComparator();

protected TreeSet	m_Fields;
protected TreeSet	m_Constructors;
protected TreeSet	m_Methods;
protected TreeSet	m_Elements;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>JElement</code>.
 *
 * @param inQualifiedName	The qualified name of this element.
 * @param inModifiers		The modifiers mask, decodable with <code>java.lang.reflect.Modifier</code>.
 * @param inComment			The related Javadoc comment or <code>null</code>.
 * @param inSourcePos		The reference to the source file.
 * @param inContainer		The reference to the containing element or <code>null</code>.
 */
public	JElement(	String		inQualifiedName,
					int			inModifiers,
					Comment		inComment,
					SourcePos	inSourcePos,
					JElement	inContainer)
{
super(inQualifiedName, inModifiers, inComment, inSourcePos, inContainer);
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
	getFieldsIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over this element's fields
 * or <code>null</code> if this element has no fields.
 */
public Iterator		getFieldsIterator()
{
return m_Fields != null ? m_Fields.iterator() : null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getConstructorsIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over this element's constructors
 * or <code>null</code> if this element has no constructors.
 */
public Iterator		getConstructorsIterator()
{
return m_Constructors != null ? m_Constructors.iterator() : null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getMethodsIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over this element's methods
 * or <code>null</code> if this element has no methods.
 */
public Iterator		getMethodsIterator()
{
return m_Methods != null ? m_Methods.iterator() : null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getElementsIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over this element's elements
 * or <code>null</code> if this element has no elements.
 */
public Iterator		getElementsIterator()
{
return m_Elements != null ? m_Elements.iterator() : null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addField
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds a field to this element.
 */
public void		addField(JField inField)
{
if(m_Fields == null)
	m_Fields = new TreeSet(s_FieldComparator);

m_Fields.add(inField);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addConstructor
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds a constructor to this element.
 */
public void		addConstructor(JConstructor inConstructor)
{
if(m_Constructors == null)
	m_Constructors = new TreeSet(s_ExecutableComparator);

m_Constructors.add(inConstructor);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addMethod
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds a method to this element.
 */
public void		addMethod(JMethod inMethod)
{
if(m_Methods == null)
	m_Methods = new TreeSet(s_ExecutableComparator);

m_Methods.add(inMethod);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addElement
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds an element to this element.
 */
public void		addElement(JElement inElement)
{
if(m_Elements == null)
	m_Elements = new TreeSet(s_ElementsComparator);

m_Elements.add(inElement);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	putElementsIntoFinder
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Puts this element's elements into a <code>Finder</code>.
 */
public void		putElementsIntoFinder(Finder inFinder)
{
for(Iterator eIt = getElementsIterator();
	eIt != null && eIt.hasNext();)
	{
	JElement	je = (JElement)eIt.next();

	inFinder.addElement(je);			// put the contained element
	je.putElementsIntoFinder(inFinder);	// tell the element to put its contained elements
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	resolveTypes
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Resolve this class' type references.
 */
public void		resolveTypes(Finder inFinder, JSource inSource)
{
// resolve nested elements
for(Iterator eIt = getElementsIterator();
	eIt != null && eIt.hasNext();)
	{
	JElement	je = (JElement)eIt.next();
	je.resolveTypes(inFinder, inSource);
	}

// resolve fields
for(Iterator fIt = getFieldsIterator();
	fIt != null && fIt.hasNext();)
	{
	JField	jf = (JField)fIt.next();
	jf.resolveTypes(inFinder, inSource);
	}

// resolve constructors
for(Iterator cIt = getConstructorsIterator();
	cIt != null && cIt.hasNext();)
	{
	JConstructor	jc = (JConstructor)cIt.next();
	jc.resolveTypes(inFinder, inSource);
	}

// resolve methods
for(Iterator mIt = getMethodsIterator();
	mIt != null && mIt.hasNext();)
	{
	JMethod		jm = (JMethod)mIt.next();
	jm.resolveTypes(inFinder, inSource);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	ElementsComparator [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Compares elements by type and name.
 */
static private class ElementsComparator implements Comparator
{
public int	compare(Object inKey1, Object inKey2)
{
JElement	e1 = (JElement)inKey1,
			e2 = (JElement)inKey2;

if(e1 instanceof JInterface && e2 instanceof JClass)
	return -1;
else if(e1 instanceof JClass && e2 instanceof JInterface)
	return  1;
else
	return e1.getQualifiedName().compareToIgnoreCase(e2.getQualifiedName());
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	FieldsComparator [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Compares fields by name.
 */
static private class FieldsComparator implements Comparator
{
public int	compare(Object inKey1, Object inKey2)
{
JField	f1 = (JField)inKey1,
		f2 = (JField)inKey2;

return f1.getLocalName().compareToIgnoreCase(f2.getLocalName());
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	ExecutableComparator [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Compares executable by name, then by parameters count,
 * then by parameters type names.
 */
static private class ExecutableComparator implements Comparator
{
public int	compare(Object inKey1, Object inKey2)
{
int				r	= 0;
JExecutable		x1	= (JExecutable)inKey1,
				x2	= (JExecutable)inKey2;

// compare by name
r = x1.getLocalName().compareToIgnoreCase(x2.getLocalName());
if(r != 0)
	return r;

JParameter[]	p1 = x1.getParameters(),
				p2 = x2.getParameters();

// compare by number of parameters
r = p1.length - p2.length;
if(r != 0)
	return r;

// compare by names of parameters
for(int p = 0; p < p1.length; p++)
	{
	r = p1[p].getType().getNameAndDimension()
		.compareToIgnoreCase(p2[p].getType().getNameAndDimension());
	if(r != 0)
		return r;
	}

// we can consider them equal, but we should not arrive here
return(r);
}
}
}


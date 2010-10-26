/*
 * JClass.java
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
/*------------------------------------------------------------------------------------------------------------------------------------
	JClass
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Represents a java class.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 */
public final class JClass extends JElement
{
protected JType		m_SuperClass;
protected JType[]	m_Interfaces;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>JClass</code>.
 *
 * @param inQualifiedName	The qualified name of this class.
 * @param inModifiers		The modifiers mask, decodable with <code>java.lang.reflect.Modifier</code>.
 * @param inComment			The related Javadoc comment or <code>null</code>.
 * @param inSourcePos		The reference to the source file.
 * @param inContainer		The reference to the containing element or <code>null</code>.
 * @param inSuperClass		This class' superclass.
 * @param inInterfaces		This class' interfaces.
 */
public	JClass(	String		inQualifiedName,
				int			inModifiers,
				Comment		inComment,
				SourcePos	inSourcePos,
				JElement	inContainer,
				JType		inSuperClass,
				JType[]		inInterfaces)
{
super(inQualifiedName, inModifiers, inComment, inSourcePos, inContainer);
m_SuperClass = inSuperClass;
m_Interfaces = inInterfaces;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getSuperClass
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns this class' superclass.
 */
public JType	getSuperClass()
{
return m_SuperClass;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getInterfaces
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns this class' interfaces.
 */
public JType[]	getInterfaces()
{
return m_Interfaces != null ? m_Interfaces : JSource.EMPTY_TYPES_ARRAY;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	resolveTypes
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Resolve this class' type references.
 */
public void		resolveTypes(Finder inFinder, JSource inSource)
{
super.resolveTypes(inFinder, inSource);

// resolve superclass
m_SuperClass.resolve(inFinder, inSource, this);

// resolve interfaces
if(m_Interfaces != null)
	for(int i = 0; i < m_Interfaces.length; i++)
		m_Interfaces[i].resolve(inFinder, inSource, this);
}
}

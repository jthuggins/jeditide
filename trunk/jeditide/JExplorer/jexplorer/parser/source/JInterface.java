/*
 * JInterface.java
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
	JInterface
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Represents a java interface.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 */
public final class JInterface extends JElement
{
protected JType[]	m_SuperInterfaces;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>JInterface</code>.
 * @param inQualifiedName	The qualified name of this interface.
 * @param inModifiers		The modifiers mask, decodable with <code>java.lang.reflect.Modifier</code>.
 * @param inComment			The related Javadoc comment or <code>null</code>.
 * @param inSourcePos		The reference to the source file.
 * @param inContainer		The reference to the containing element or <code>null</code>.
 * @param inSuperInterfaces	This interface's superinterfaces.
 */
public	JInterface(	String		inQualifiedName,
					int			inModifiers,
					Comment		inComment,
					SourcePos	inSourcePos,
					JElement	inContainer,
					JType[]		inSuperInterfaces)
{
super(inQualifiedName, inModifiers, inComment, inSourcePos, inContainer);
m_SuperInterfaces = inSuperInterfaces;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getSuperInterfaces
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns this interface's superinterfaces.
 */
public JType[]	getSuperInterfaces()
{
return m_SuperInterfaces != null ? m_SuperInterfaces : JSource.EMPTY_TYPES_ARRAY;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	resolveTypes
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Resolve this interface's type references.
 */
public void		resolveTypes(Finder inFinder, JSource inSource)
{
super.resolveTypes(inFinder, inSource);

// resolve superinterfaces
if(m_SuperInterfaces != null)
	for(int i = 0; i < m_SuperInterfaces.length; i++)
		m_SuperInterfaces[i].resolve(inFinder, inSource, this);
}
}


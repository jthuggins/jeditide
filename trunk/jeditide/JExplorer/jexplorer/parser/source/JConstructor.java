/*
 * JConstructor.java
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
	JConstructor
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Represents a constructor of a java class.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 */
public final class JConstructor extends JExecutable
{
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>JConstructor</code>.
 * @param inName		The local name of this constructor.
 * @param inModifiers	The modifiers mask, decodable with <code>java.lang.reflect.Modifier</code>.
 * @param inComment		The related Javadoc comment or <code>null</code>.
 * @param inSourcePos	The reference to the source file.
 * @param inContainer	The reference to the containing element or <code>null</code>.
 * @param inParameters	This constructor's parameters.
 * @param inThrowables	This constructor's throwables.
 */
public	JConstructor(	String			inName,
						int				inModifiers,
						Comment			inComment,
						SourcePos		inSourcePos,
						JElement		inContainer,
						JParameter[]	inParameters,
						JType[]			inThrowables)
{
super(inName, inModifiers, inComment, inSourcePos, inContainer, inParameters, inThrowables);
}
}


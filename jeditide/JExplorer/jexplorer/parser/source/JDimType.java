/*
 * JDimType.java
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
	JDimType
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Represents a java array type.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public final class JDimType extends JType
{
protected String	m_Dimension;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>JDimType</code>.
 * @param inName		The name of this type.
 * @param inDimension	The dimension of this type or <code>null</code>.
 */
public	JDimType(String inName, String	inDimension)
{
super(inName);
m_Dimension = inDimension;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getNameAndDimension
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the name of this type.
 */
public String	getNameAndDimension()
{
if(m_Dimension != null)
	return m_QualifiedName + m_Dimension;
else
	return m_QualifiedName;
}
}

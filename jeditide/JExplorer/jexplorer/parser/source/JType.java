/*
 * JType.java
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
	JType
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Represents a java type.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public class JType
{
private		String	m_Name;
protected	String	m_QualifiedName;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>JType</code>.
 * @param inName	The name of this type.
 */
public	JType(String inName)
{
m_Name			= inName;
m_QualifiedName	= m_Name;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getQualifiedName
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the qualified name of this type.
 */
public String	getQualifiedName()
{
return m_QualifiedName;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	resolve
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Resolve this type reference.
 */
public void		resolve(Finder inFinder, JSource inSource, JElement inContainer)
{
JSource.LookupResult	r = inSource.lookupType(inFinder, inContainer, m_Name);

if(r.isSuccesful())
	m_QualifiedName = r.getResolvedType();
else
	m_QualifiedName = m_Name;
}
}

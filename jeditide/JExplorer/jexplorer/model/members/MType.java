/*
 * MType.java
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
/*------------------------------------------------------------------------------------------------------------------------------------
	MType
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Represents a java type.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.14
 */
public final class MType
{
private String	m_QualifiedName;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	MType(String inQualifiedName)
{
m_QualifiedName	= inQualifiedName;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getQualifiedName
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getQualifiedName()
{
return m_QualifiedName;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getShortName
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getShortName()
{
return m_QualifiedName.substring(m_QualifiedName.lastIndexOf('.') + 1);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getDescriptor
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getDescriptor(boolean inUseShortName)
{
if(inUseShortName)	return(getShortName());
else				return(m_QualifiedName);
}
}

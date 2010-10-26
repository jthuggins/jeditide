/*
 * MParameter.java
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
	MParameter
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Parameter information.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.14
 */
public final class MParameter
{
private MType		m_Type;
private String		m_Name;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	MParameter(MType inType, String inName)
{
m_Type = inType;
m_Name = inName;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getDescriptor
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getDescriptor(boolean inUseShortName)
{
return(m_Type.getDescriptor(inUseShortName));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getName
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getName()
{
return(m_Name);
}
}


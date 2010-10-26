/*
 * JParameter.java
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
	JParameter
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Parameter information.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public final class JParameter
{
protected JDimType	m_Type;
protected String	m_Name;
protected boolean	m_IsFinal;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>JParameter</code>.
 * @param inType	The type of this parameter.
 * @param inName	The name of this parameter.
 * @param inIsFinal	true if this parameter is final.
 */
public	JParameter(	String		inName,
					JDimType	inType,
					boolean		inIsFinal)
{
m_Name		= inName;
m_Type		= inType;
m_IsFinal	= inIsFinal;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isFinal
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true if this parameter is final.
 */
public boolean	isFinal()
{
return m_IsFinal;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getName
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns this parameter's name.
 */
public String	getName()
{
return m_Name;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getType
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns this parameter's type.
 */
public JDimType	getType()
{
return m_Type;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	resolveTypes
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Resolve this parameter's type references.
 */
public void		resolveTypes(Finder inFinder, JElement inContainer, JSource inSource)
{
m_Type.resolve(inFinder, inSource, inContainer);
}
}

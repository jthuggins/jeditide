/*
 * MExecutable.java
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

import java.util.Vector;

import jexplorer.parser.source.JParameter;
import jexplorer.parser.source.JType;
import jexplorer.parser.source.Comment;
import jexplorer.parser.source.SourcePos;
/*------------------------------------------------------------------------------------------------------------------------------------
	MExecutable
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Base class for constructors and methods.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.14
 */
public abstract class MExecutable extends MMember
{
protected MParameter[]	m_Parameters;
protected MType[]		m_Throwables;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	MExecutable(MElement		inContainer,
					String			inName,
					byte			inAccess,		// access specifier
					JParameter[]	inParameters,	// parameters
					JType[]			inThrowables,	// exceptions
					Comment			inComment,		// comment
					SourcePos		inSourcePos)	// reference to source
{
super(inContainer, inName, inAccess, inComment, inSourcePos);

// build parameters array

m_Parameters = new MParameter[inParameters.length];

for(int p = 0; p < inParameters.length; p++)
	m_Parameters[p] = new MParameter(
		new MType(inParameters[p].getType().getNameAndDimension()),
		inParameters[p].getName());

// build throwables array

m_Throwables = new MType[inThrowables.length];

for(int t = 0; t < inThrowables.length; t++)
	m_Throwables[t] = new MType(inThrowables[t].getQualifiedName());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getParametersListText
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getParametersListText(boolean inUseShortName)
{
StringBuffer	buf = new StringBuffer();

for(int k = 0; k < m_Parameters.length; k++)
	{
	if(k > 0)	buf.append(", ");
	buf.append(m_Parameters[k].getDescriptor(inUseShortName));
	}

return(buf.toString());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getParameters
------------------------------------------------------------------------------------------------------------------------------------*/
public MParameter[]	getParameters()
{
return m_Parameters;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getThrowables
------------------------------------------------------------------------------------------------------------------------------------*/
public MType[]		getThrowables()
{
return m_Throwables;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getSignature
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getSignature()
{
return(getParametersListText(true));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getCanonicalText
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getCanonicalText()
{
StringBuffer	buf = new StringBuffer();

buf.append(m_Container.getCanonicalText());
buf.append(".");
buf.append(m_Name);
buf.append("(");
buf.append(getSignature());
buf.append(")");

return(buf.toString());
}
}

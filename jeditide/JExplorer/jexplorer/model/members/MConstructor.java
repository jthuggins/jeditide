/*
 * MConstructor.java
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

import java.awt.Color;

import jexplorer.JExplorerConstants;
import jexplorer.JExplorerPlugin;

import jexplorer.model.properties.ModelProperties;

import jexplorer.parser.source.JParameter;
import jexplorer.parser.source.JType;
import jexplorer.parser.source.Comment;
import jexplorer.parser.source.SourcePos;
/*------------------------------------------------------------------------------------------------------------------------------------
	MConstructor
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Represents a constructor of a java class.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 */
public final class MConstructor
	extends MExecutable
	implements JExplorerConstants
{
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	MConstructor(	MElement		inContainer,
						String			inName,
						byte			inAccess,			// access specifier
						JParameter[]	inParameters,		// parameters
						JType[]			inThrowables,		// exceptions
						boolean			inIsDeprecated,		// deprecated flag
						Comment			inComment,			// comment
						SourcePos		inSourcePos)		// reference to source
{
super(inContainer, inName, inAccess, inParameters, inThrowables, inComment, inSourcePos);

if(inThrowables.length > 0)	m_Modifiers |= MOD_THROWS;
if(inIsDeprecated)			m_Modifiers |= MOD_DEPRECATED;

computeToolTipText();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getTextColor
------------------------------------------------------------------------------------------------------------------------------------*/
public Color	getTextColor()
{
return(JExplorerPlugin.getColor(COLOR_CONSTRUCTOR));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getDetailsText
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getDetailsText()
{
StringBuffer	buf				= new StringBuffer();
boolean			useShortNames	= m_Container.getProperties().getOption(PM_USE_SHORT_NAMES);

// constructor name
buf.append(m_Name);

// parameters list
buf.append("(");
buf.append(getParametersListText(useShortNames));
buf.append(")");

return(buf.toString());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getIndexText
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getIndexText()
{
StringBuffer	buf				= new StringBuffer();
boolean			useShortNames	= m_Container.getProperties().getOption(PM_USE_SHORT_NAMES);

// constructor name
buf.append(m_Name);

// parameters list
buf.append("(");
buf.append(getParametersListText(useShortNames));
buf.append(")");

// element name
buf.append(" [");
buf.append(getContainer().getIndexText());
buf.append("]");

return(buf.toString());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeToolTipText
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	computeToolTipText()
{
StringBuffer	buf = new StringBuffer(getTooltipPrefix());

// constructor name
buf.append(m_Name);

// parameters list (we intentionally always use the fully qualified name)
buf.append("(");
buf.append(getParametersListText(false));
buf.append(")");

m_ToolTipText = buf.toString();
}
}


/*
 * MMethod.java
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
	MMethod
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Represents a method of a java class.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 */
public final class MMethod
	extends MExecutable
	implements JExplorerConstants
{
private MType	m_ReturnType;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public 	MMethod(MElement			inContainer,
				String			inName,
				byte			inAccess,			// access specifier
				JParameter[]	inParameters,		// parameters
				JType[]			inThrowables,		// exceptions
				MType			inReturnType,		// return type
				boolean			inIsAbstract,		// abstract flag
				boolean			inIsFinal,			// final flag
				boolean			inIsStatic,			// static flag
				boolean			inIsNative,			// native flag
				boolean			inIsSynchronized,	// synchronized flag
				boolean			inIsOverriding,		// overriding flag
				boolean			inIsDeprecated,		// deprecated flag
				Comment			inComment,			// comment
				SourcePos		inSourcePos)		// reference to source
{
super(inContainer, inName, inAccess, inParameters, inThrowables, inComment, inSourcePos);

m_ReturnType = inReturnType;

if(inIsAbstract)			m_Modifiers |= MOD_ABSTRACT;
if(inIsFinal)				m_Modifiers |= MOD_FINAL;
if(inIsStatic)				m_Modifiers |= MOD_STATIC;
if(inIsNative)				m_Modifiers |= MOD_NATIVE;
if(inIsSynchronized)		m_Modifiers |= MOD_SYNCHRONIZED;
if(inIsOverriding)			m_Modifiers |= MOD_OVERRIDES;
if(inThrowables.length > 0)	m_Modifiers |= MOD_THROWS;
if(inIsDeprecated)			m_Modifiers |= MOD_DEPRECATED;

computeToolTipText();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getTextColor
------------------------------------------------------------------------------------------------------------------------------------*/
public Color	getTextColor()
{
// NOT IMPLEMENTED return(inProperties.getOption(PM_HILITE_OVERR_METHODS) && this.isOverriding() ?
// NOT IMPLEMENTED 	JExplorerPlugin.getColor(COLOR_OVERRIDING) : JExplorerPlugin.getColor(COLOR_METHOD));
return(JExplorerPlugin.getColor(COLOR_METHOD));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getDetailsText
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getDetailsText()
{
StringBuffer	buf				= new StringBuffer();
boolean			useShortNames	= m_Container.getProperties().getOption(PM_USE_SHORT_NAMES);

// method name
buf.append(m_Name);

// parameters list
buf.append("(");
buf.append(getParametersListText(useShortNames));
buf.append(")");

// return type descriptor
buf.append(" : ");
buf.append(m_ReturnType.getDescriptor(useShortNames));

return(buf.toString());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getIndexText
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getIndexText()
{
StringBuffer	buf				= new StringBuffer();
boolean			useShortNames	= m_Container.getProperties().getOption(PM_USE_SHORT_NAMES);

// method name
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

// return type descriptor (we intentionally always use the fully qualified name)
buf.append(m_ReturnType.getDescriptor(false));

// method name
buf.append(" ");
buf.append(m_Name);

// parameters list (we intentionally always use the fully qualified name)
buf.append("(");
buf.append(getParametersListText(false));
buf.append(")");

m_ToolTipText = buf.toString();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getReturnType
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the return type of this method.
 */
public MType	getReturnType()
{
return m_ReturnType;
}
}

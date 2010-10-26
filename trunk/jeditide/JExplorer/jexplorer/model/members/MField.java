/*
 * MField.java
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
import jexplorer.parser.source.Comment;
import jexplorer.parser.source.SourcePos;
/*------------------------------------------------------------------------------------------------------------------------------------
	MField
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Represents a field in a java class.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 */
public final class MField
	extends MMember
	implements JExplorerConstants
{
private MType	m_Type;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	MField(	MElement	inContainer,
				String		inName,
				byte		inAccess,			// access specifier
				MType		inType,				// type
				boolean		inIsFinal,			// final flag
				boolean		inIsStatic,			// static flag
				boolean		inIsTransient,		// transient flag
				boolean		inIsVolatile,		// volatile flag
				boolean		inIsDeprecated,		// deprecated flag
				Comment		inComment,			// comment
				SourcePos	inSourcePos)		// reference to source
{
super(inContainer, inName, inAccess, inComment, inSourcePos);

m_Type = inType;

if(inIsFinal)		m_Modifiers |= MOD_FINAL;
if(inIsStatic)		m_Modifiers |= MOD_STATIC;
if(inIsTransient)	m_Modifiers |= MOD_TRANSIENT;
if(inIsVolatile)	m_Modifiers |= MOD_VOLATILE;
if(inIsDeprecated)	m_Modifiers |= MOD_DEPRECATED;

computeToolTipText();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getTextColor
------------------------------------------------------------------------------------------------------------------------------------*/
public Color	getTextColor()
{
return(JExplorerPlugin.getColor(COLOR_FIELD));
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

return(buf.toString());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getDetailsText
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getDetailsText()
{
StringBuffer	buf = new StringBuffer();

// field name
buf.append(m_Name);

// type descriptor
buf.append(" : ");
buf.append(m_Type.getDescriptor(m_Container.getProperties().getOption(PM_USE_SHORT_NAMES)));

return(buf.toString());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getIndexText
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getIndexText()
{
StringBuffer	buf = new StringBuffer();

// field name
buf.append(m_Name);

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

// type descriptor (we intentionally always use the fully qualified name)
buf.append(m_Type.getDescriptor(false));

// field name
buf.append(" ");
buf.append(m_Name);

m_ToolTipText = buf.toString();
}
}

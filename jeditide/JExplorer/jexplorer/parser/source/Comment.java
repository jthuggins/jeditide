/*
 * Comment.java
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

import jexplorer.parser.Token;
/*------------------------------------------------------------------------------------------------------------------------------------
	Comment
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Describes a comment in a java source file.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 * @since	0.2
 */
public class Comment
{
protected String	m_Text;
protected int		m_StartLine;
protected int		m_StartColumn;
protected int		m_EndLine;
protected int		m_EndColumn;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>Comment</code>.
 *
 * @param inToken	The parser token to provide information.
 */
public	Comment(Token inToken)
{
m_Text			= inToken.image;
m_StartLine		= inToken.beginLine;
m_StartColumn	= inToken.beginColumn;
m_EndLine		= inToken.endLine;
m_EndColumn		= inToken.endColumn;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getText
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the text of the comment. */
public String	getText()			{ return m_Text; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getStartLine
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the starting line in the source file. */
public int		getStartLine()		{ return m_StartLine; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getStartColumn
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the starting column in the source file. */
public int		getStartColumn()	{ return m_StartColumn; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getEndLine
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the ending line in the source file. */
public int		getEndLine()		{ return m_EndLine; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getEndColumn
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the ending column in the source file. */
public int		getEndColumn()		{ return m_EndColumn; }
}

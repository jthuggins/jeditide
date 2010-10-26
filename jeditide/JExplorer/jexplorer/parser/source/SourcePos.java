/*
 * SourcePos.java
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
	SourcePos
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Describes a member location and extension in a java source file.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.08
 */
public class SourcePos
{
protected String	m_Path;

protected int		m_StartLine;
protected int		m_StartColumn;
protected int		m_EndLine;
protected int		m_EndColumn;

protected int		m_BlockStartLine;
protected int		m_BlockStartColumn;
protected int		m_BlockEndLine;
protected int		m_BlockEndColumn;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>SourcePos</code>.
 * @param inPath	The pathname to the source file, in the current OS style.
 * @param inToken	The parser token to provide positional information.
 */
public	SourcePos(	String	inPath,
					Token	inToken)
{
m_Path				= inPath;
m_StartLine			= inToken.beginLine;
m_StartColumn		= inToken.beginColumn;
m_EndLine			= inToken.endLine;
m_EndColumn			= inToken.endColumn;
m_BlockStartLine	= m_StartLine;
m_BlockStartColumn	= m_StartColumn;
m_BlockEndLine		= m_EndLine;
m_BlockEndColumn	= m_EndColumn;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getPath
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the pathname to the source file, in the current OS style. */
public String	getPath()			{ return m_Path; }
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
/*------------------------------------------------------------------------------------------------------------------------------------
	setBlockStart
------------------------------------------------------------------------------------------------------------------------------------*/
/** Sets the beginning of the block in the source file. */
public void		setBlockStart(Token inToken)
{
m_BlockStartLine	= inToken.beginLine;
m_BlockStartColumn	= inToken.beginColumn;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getBlockStartLine
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the last line of the block in the source file. */
public int		getBlockStartLine()		{ return m_BlockStartLine; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getBlockStartColumn
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the last column of the block in the source file. */
public int		getBlockStartColumn()	{ return m_BlockStartColumn; }
/*------------------------------------------------------------------------------------------------------------------------------------
	setBlockEnd
------------------------------------------------------------------------------------------------------------------------------------*/
/** Sets the end of the block in the source file. */
public void		setBlockEnd(Token inToken)
{
m_BlockEndLine		= inToken.endLine;
m_BlockEndColumn	= inToken.endColumn;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getBlockEndLine
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the last line of the block in the source file. */
public int		getBlockEndLine()	{ return m_BlockEndLine; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getBlockEndColumn
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the last column of the block in the source file. */
public int		getBlockEndColumn()	{ return m_BlockEndColumn; }
}

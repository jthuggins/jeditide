/*
 * JMember.java
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
	JMember
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Base class for all items.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 */
public abstract class JMember
{
protected String		m_Name;
protected int			m_Modifiers;
protected Comment		m_Comment;
protected SourcePos		m_SourcePos;
protected JElement		m_Container;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>JMember</code>.
 * @param inName		The name of this member. Can be either local or qualified, depending on the real type of this member.
 * @param inModifiers	The modifiers mask, decodable with <code>java.lang.reflect.Modifier</code>.
 * @param inComment		The related Javadoc comment or <code>null</code>.
 * @param inSourcePos	The reference to the source file.
 * @param inContainer	The reference to the containing element or <code>null</code>.
 */
public	JMember(String		inName,
				int			inModifiers,
				Comment		inComment,
				SourcePos	inSourcePos,
				JElement	inContainer)
{
m_Name		= inName;
m_Modifiers	= inModifiers;
m_Comment	= inComment;
m_SourcePos	= inSourcePos;
m_Container	= inContainer;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getLocalName
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the local name of this member. */
public String		getLocalName()	{ return m_Name; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getModifiers
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the modifiers mask, decodable with <code>java.lang.reflect.Modifier</code>. */
public int			getModifiers()	{ return m_Modifiers; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getComment
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the related Javadoc comment or <code>null</code>. */
public Comment		getComment()	{ return m_Comment; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getSourcePos
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the reference to the source file. */
public SourcePos	getSourcePos()	{ return m_SourcePos; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getContainer
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the reference to the containing element. */
public JElement		getContainer()	{ return m_Container; }
/*------------------------------------------------------------------------------------------------------------------------------------
	setBlockStart
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Sets the position of the first character pertaining to this member.
 */
public void		setBlockStart(Token inToken)
{
m_SourcePos.setBlockStart(inToken);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setBlockEnd
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Sets the position of the last character pertaining to this member.
 */
public void		setBlockEnd(Token inToken)
{
m_SourcePos.setBlockEnd(inToken);
}
}

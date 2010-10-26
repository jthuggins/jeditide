/*
 * MMember.java
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

import java.awt.Font;
import java.awt.Color;

import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;

import jexplorer.JExplorerPlugin;
import jexplorer.JExplorerConstants;

import jexplorer.parser.source.Comment;
import jexplorer.parser.source.SourcePos;
/*------------------------------------------------------------------------------------------------------------------------------------
	MMember
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Base class for all items.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 */
public abstract class MMember implements JExplorerConstants
{
static private final String EMPTY_SIGNATURE	= "";

protected String					m_Name;
protected byte						m_Access	= ACCESS_UNKNOWN;
protected short						m_Modifiers	= MOD_NONE;
protected Comment					m_Comment;
protected SourcePos					m_SourcePos;
protected MElement					m_Container;
protected DefaultMutableTreeNode	m_TreeNode	= new DefaultMutableTreeNode(this);
protected String					m_ToolTipText;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>MMember</code>.
 *
 * @param inContainer	The reference to the containing element or <code>null</code>.
 * @param inName		The name of this member. Can be either local or qualified, depending on the real type of this member.
 * @param inAccess		The accessibility of this member.
 * @param inSourcePos	The reference to the source file.
 */
public	MMember(MElement	inContainer,
				String		inName,
				byte		inAccess,
				Comment		inComment,
				SourcePos	inSourcePos)
{
m_Container	= inContainer;
m_Name		= inName;
m_Access	= inAccess;
m_Comment	= inComment;
m_SourcePos	= inSourcePos;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setName
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Sets the name of this member.
 
 * @param inName	The name of this member. Can be either local or qualified, depending on the real type of this member.
 */
public void			setName(String inName)	{ m_Name = inName; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getLocalName
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the local name of this member. */
public String		getLocalName()	{ return m_Name; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getAccess
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the accessibility. */
public byte			getAccess()		{ return m_Access; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getComment
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the comment. */
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
public MElement		getContainer()	{ return m_Container; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getTreeNode
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the UI TreeNode. */
public	DefaultMutableTreeNode	getTreeNode()	{ return m_TreeNode; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getToolTipText
------------------------------------------------------------------------------------------------------------------------------------*/
/** Returns the tooltip text. */
public	String		getToolTipText()	{ return m_ToolTipText; }
/*------------------------------------------------------------------------------------------------------------------------------------
	compact methods
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getSignature()	{ return EMPTY_SIGNATURE; }

abstract protected	void	computeToolTipText	();
abstract public		Color	getTextColor		();
abstract public		String	getCanonicalText	();
abstract public		String	getDetailsText		();
abstract public		String	getIndexText		();

public boolean isAbstract	 ()	{ return (m_Modifiers & MOD_ABSTRACT) == MOD_ABSTRACT; }
public boolean isFinal		 ()	{ return (m_Modifiers & MOD_FINAL) == MOD_FINAL; }
public boolean isNative		 ()	{ return (m_Modifiers & MOD_NATIVE) == MOD_NATIVE; }
public boolean isStatic		 ()	{ return (m_Modifiers & MOD_STATIC) == MOD_STATIC; }
public boolean isSynchronized()	{ return (m_Modifiers & MOD_SYNCHRONIZED) == MOD_SYNCHRONIZED; }
public boolean isTransient	 ()	{ return (m_Modifiers & MOD_TRANSIENT) == MOD_TRANSIENT; }
public boolean isVolatile	 ()	{ return (m_Modifiers & MOD_VOLATILE) == MOD_VOLATILE; }
public boolean isOverriding	 ()	{ return (m_Modifiers & MOD_OVERRIDES) == MOD_OVERRIDES; }
public boolean isThrowing	 ()	{ return (m_Modifiers & MOD_THROWS) == MOD_THROWS; }
public boolean isDeprecated	 ()	{ return (m_Modifiers & MOD_DEPRECATED) == MOD_DEPRECATED; }
/*------------------------------------------------------------------------------------------------------------------------------------
	getPackage
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the package this member belongs to.
 */
public String	getPackage()
{
return(m_Container.getPackage());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getTooltipPrefix
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getTooltipPrefix()
{
StringBuffer	buf = new StringBuffer();

switch(m_Access)
	{
	case ACCESS_PUBLIC:		buf.append("public ");		break;
	case ACCESS_PROTECTED:	buf.append("protected ");	break;
	case ACCESS_PRIVATE:	buf.append("private ");		break;
	}

if(isAbstract())		buf.append("abstract ");
if(isFinal())			buf.append("final ");
if(isNative())			buf.append("native ");
if(isStatic())			buf.append("static ");
if(isSynchronized())	buf.append("synchronized ");
if(isTransient())		buf.append("transient ");
if(isVolatile())		buf.append("volatile ");

return(buf.toString());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getFont
------------------------------------------------------------------------------------------------------------------------------------*/
public Font		getFont()
{
	 if(isAbstract())	return(JExplorerPlugin.getFont(FONT_ABSTRACT));
else if(isFinal())		return(JExplorerPlugin.getFont(FONT_FINAL));
else					return(JExplorerPlugin.getFont(FONT_STANDARD));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	containsCaret
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true if this member contains the caret.
 * For performance reasons, the buffer path is not checked to match the SourcePos path.
 */
protected boolean	containsCaret(int inCaret, Buffer inBuffer)
{
int		startLine	= m_SourcePos.getBlockStartLine() - 1,
		endLine		= m_SourcePos.getBlockEndLine() - 1;

// if the model isn't kept up-to-date and the user deleted some text,
// line references could point beyond the end of the buffer, so calling
// getLineStartOffset() would throw an ArrayIndexOutOfBoundsException
if(	startLine >= inBuffer.getLineCount() ||
	endLine >= inBuffer.getLineCount())
		return(false);

return(	inCaret >=
		inBuffer.getLineStartOffset(startLine) + m_SourcePos.getBlockStartColumn() - 1
		&&
		inCaret <=
		inBuffer.getLineStartOffset(endLine) + m_SourcePos.getBlockEndColumn());
}
}

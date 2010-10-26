/*
 * ViewConnector.java
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

package jexplorer.gui;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.buffer.BufferChangeListener;

import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.BufferUpdate;

import jexplorer.model.ModelManager;
import jexplorer.model.selection.SelectionListener;
import jexplorer.model.selection.SelectionEvent;

import jexplorer.parser.source.SourcePos;
/*------------------------------------------------------------------------------------------------------------------------------------
	ViewConnector
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Realizes a connection between a <code>ModelSelection</code> and
 * a jEdit <code>View</code>.
 *
 * @author	Amedeo Farello
 * @version	0.3, 2003.05.24
 */
public class ViewConnector
	implements EBComponent, SelectionListener, BufferChangeListener
{
private	View	m_View;
private Buffer	m_Buffer;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public ViewConnector(View inView)
{
m_View = inView;
setupBufferLink();
EditBus.addToBus(this);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	terminate
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Clean up the jEdit environment.
 *
 * @since 0.3
 */
public void	terminate()
{
EditBus.removeFromBus(this);

if(m_Buffer != null)
	m_Buffer.removeBufferChangeListener(this);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getView
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the jEdit View this connector refers to.
 */
public View	getView()
{
return m_View;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setupBufferLink
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Listen to the current Buffer of the reference View.
 *
 * @since 0.3
 */
public void	setupBufferLink()
{
if(m_Buffer != null)
	m_Buffer.removeBufferChangeListener(this);

m_Buffer = m_View.getBuffer();
m_Buffer.addBufferChangeListener(this);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	handleMessage [implements EBComponent]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * @since 0.3
 */
public void		handleMessage(EBMessage inMessage)
{
// when the active buffer of the reference View changes,
// detach from the old buffer and link to the new

if(inMessage instanceof ViewUpdate)
	{
	ViewUpdate		vu = (ViewUpdate)inMessage;

	if(	vu.getWhat() == ViewUpdate.EDIT_PANE_CHANGED &&
		vu.getView() == m_View)
		setupBufferLink();
	}
else if(inMessage instanceof EditPaneUpdate)
	{
	EditPaneUpdate	epu = (EditPaneUpdate)inMessage;

	if(	epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED &&
		epu.getEditPane() == m_View.getEditPane())
		setupBufferLink();
	}
else if(inMessage instanceof BufferUpdate)
	{
	BufferUpdate	bu = (BufferUpdate)inMessage;

	if(	bu.getWhat() == BufferUpdate.SAVED &&
		bu.getView() == m_View &&
		bu.getBuffer().getName().endsWith(".java"))
		ModelManager.getInstance().bufferSaved(bu.getBuffer());
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	selectionChanged [implements SelectionListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void	selectionChanged(SelectionEvent inEvt)
{
if(	!inEvt.isNullSelection() &&
	inEvt.isSourcePositioningRequested())
	{
	final SourcePos	loc;

	if(inEvt.isMemberSelection())
		loc = inEvt.getSelectedMember().getSourcePos();
	else
		loc = inEvt.getSelectedElement().getSourcePos();

	final Buffer	buffer = jEdit.openFile(m_View, loc.getPath());

	VFSManager.runInAWTThread(new Runnable()
		{
		public void run()
			{
			if(buffer != null && buffer.isLoaded())
				{
				m_View.setBuffer(buffer);

				int		startLine	= loc.getStartLine() - 1,
						endLine		= loc.getEndLine() - 1;

				// if the model isn't kept up-to-date and the user deleted some text,
				// line references could point beyond the end of the buffer, so calling
				// getLineStartOffset() would throw an ArrayIndexOutOfBoundsException
				if(	startLine >= buffer.getLineCount() ||
					endLine >= buffer.getLineCount())
						return;

				m_View.getTextArea().setSelection(
					new Selection.Range(
							buffer.getLineStartOffset(startLine) + loc.getStartColumn() - 1,
							buffer.getLineStartOffset(endLine) + loc.getEndColumn()));

				m_View.getTextArea().moveCaretPosition(
						buffer.getLineStartOffset(startLine));
				}
			}
		});
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	foldLevelChanged [implements BufferChangeListener]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Called when line fold levels change.
 *
 * @param inBuffer The buffer in question
 * @param inStart The start line number
 * @param inEnd The end line number
 * @since 0.3
 */
public void		foldLevelChanged(Buffer inBuffer, int inStart, int inEnd)
{
}
/*------------------------------------------------------------------------------------------------------------------------------------
	transactionComplete [implements BufferChangeListener]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Called after an undo or compound edit has finished.
 *
 * @param inBuffer The buffer in question
 * @since 0.3
 */
public void		transactionComplete(Buffer inBuffer)
{
}
/*------------------------------------------------------------------------------------------------------------------------------------
	contentInserted [implements BufferChangeListener]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Called when text is inserted into the buffer.
 *
 * @param inBuffer		The buffer in question
 * @param inStartLine	The first line
 * @param inOffset		The start offset, from the beginning of the buffer
 * @param inNumLines	The number of lines inserted
 * @param inLength		The number of characters inserted
 * @since 0.3
 */
public void		contentInserted(Buffer	inBuffer,
								int		inStartLine,
								int		inOffset,
								int		inNumLines,
								int		inLength)
{
if(	inBuffer.isLoaded() &&
	inBuffer.getName().endsWith(".java"))
		ModelManager.getInstance().bufferChanged(inBuffer);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	contentRemoved [implements BufferChangeListener]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Called when text is removed from the buffer.
 *
 * @param inBuffer		The buffer in question
 * @param inStartLine	The first line
 * @param inOffset		The start offset, from the beginning of the buffer
 * @param inNumLines	The number of lines removed
 * @param inLength		The number of characters removed
 * @since 0.3
 */
public void		contentRemoved(	Buffer	inBuffer,
								int		inStartLine,
								int		inOffset,
								int		inNumLines,
								int		inLength)
{
if(	inBuffer.isLoaded() &&
	inBuffer.getName().endsWith(".java"))
		ModelManager.getInstance().bufferChanged(inBuffer);
}
}
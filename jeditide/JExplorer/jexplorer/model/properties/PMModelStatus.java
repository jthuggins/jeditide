/*
 * PMModelStatus.java
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

package jexplorer.model.properties;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.Icon;

import org.gjt.sp.jedit.jEdit;

import errorlist.ErrorSource;
//import errorlist.DefaultErrorSource;
import jexplorer.JExplorerErrorSource;

import jexplorer.JExplorerConstants;
import jexplorer.model.Model;
/*------------------------------------------------------------------------------------------------------------------------------------
	PMModelStatus
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Manages the status of a <code>Model</code>.
 *
 * @author	Amedeo Farello
 * @version	0.3, 2003.05.26
 */
public final class PMModelStatus extends PropertyManager implements JExplorerConstants
{
final static public int REBUILD_NEVER		= 0;
final static public int REBUILD_WHEN_SAVED	= 1;
final static public int REBUILD_ON_CHANGES	= 2;

protected int					m_Rebuild		= REBUILD_ON_CHANGES;
protected int					m_ParsersCount	= 0;
//protected DefaultErrorSource	m_ParserErrors;
protected JExplorerErrorSource	m_ParserErrors;
protected JExplorerErrorSource	m_UpdateErrors;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public PMModelStatus(	Hashtable	inActionsTable,
						Model		inModel,
						String		inName)
{
super(inActionsTable, inModel, inName);

//m_ParserErrors = new DefaultErrorSource(
m_ParserErrors = new JExplorerErrorSource(
	"(JExplorer model '" + m_Model.getProject().getName() + "')");

m_UpdateErrors = new JExplorerErrorSource(
	"(JExplorer model '" + m_Model.getProject().getName() + "')");
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setRebuildOption
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setRebuildOption(int inValue)
{
m_Rebuild = inValue;
actionPerformed(null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getRebuildOption
------------------------------------------------------------------------------------------------------------------------------------*/
public int		getRebuildOption()
{
return m_Rebuild;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getRebuildOptionIcon
------------------------------------------------------------------------------------------------------------------------------------*/
public Icon		getRebuildOptionIcon()
{
switch(m_Rebuild)
	{
	case REBUILD_ON_CHANGES:	return(ICON_MODEL_RB_CHANGE);
	case REBUILD_WHEN_SAVED:	return(ICON_MODEL_RB_SAVE);
	case REBUILD_NEVER:			return(ICON_MODEL_RB_NEVER);
	default:					return(ICON_BLANK);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isUpToDate
------------------------------------------------------------------------------------------------------------------------------------*/
public boolean	isUpToDate()
{
return(m_UpdateErrors.getErrorCount() == 0);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getStatusIcon
------------------------------------------------------------------------------------------------------------------------------------*/
public Icon		getStatusIcon()
{
if(isParsing())
	return(ICON_MODEL_PARSING);
else if(m_UpdateErrors.getErrorCount() > 0)
	return(ICON_MODEL_OBSOLETE);
else
	return(m_ParserErrors.getErrorCount() > 0 ?
		ICON_MODEL_DEFECTIVE : ICON_MODEL_READY);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	register
------------------------------------------------------------------------------------------------------------------------------------*/
public void		register()
{
ErrorSource.registerErrorSource(m_ParserErrors);
ErrorSource.registerErrorSource(m_UpdateErrors);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	unregister
------------------------------------------------------------------------------------------------------------------------------------*/
public void		unregister()
{
removeAllErrors();
ErrorSource.unregisterErrorSource(m_ParserErrors);
ErrorSource.unregisterErrorSource(m_UpdateErrors);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addParserError
------------------------------------------------------------------------------------------------------------------------------------*/
public void		addParserError(	int		inType,
								String	inPath,
								int		inLineIndex,
								int		inStart,
								int		inEnd,
								String	inError)
{
m_ParserErrors.addError(inType, inPath, inLineIndex, inStart, inEnd, inError);
actionPerformed(null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addUpdateError
------------------------------------------------------------------------------------------------------------------------------------*/
public void		addUpdateError(	int		inType,
								String	inPath,
								int		inLineIndex,
								int		inStart,
								int		inEnd,
								String	inError)
{
m_UpdateErrors.addError(inType, inPath, inLineIndex, inStart, inEnd, inError);
actionPerformed(null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	removeAllErrors
------------------------------------------------------------------------------------------------------------------------------------*/
public void		removeAllErrors()
{
m_ParserErrors.clear();
m_UpdateErrors.clear();
actionPerformed(null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	removeFileErrors
------------------------------------------------------------------------------------------------------------------------------------*/
public void		removeFileErrors(String inPath)
{
m_ParserErrors.removeFileErrors(inPath);
m_UpdateErrors.removeFileErrors(inPath);
actionPerformed(null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	incrementParsers
------------------------------------------------------------------------------------------------------------------------------------*/
synchronized public void	incrementParsers()
{
m_ParsersCount++;

if(m_ParsersCount == 1)
	actionPerformed(null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	decrementParsers
------------------------------------------------------------------------------------------------------------------------------------*/
synchronized public void	decrementParsers()
{
m_ParsersCount--;

if(m_ParsersCount < 1)
	actionPerformed(null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isParsing
------------------------------------------------------------------------------------------------------------------------------------*/
synchronized public boolean	isParsing()
{
return(m_ParsersCount > 0);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setEnabled
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setEnabled(boolean inEnabled)
{
super.setEnabled(inEnabled);

for(Enumeration e = m_Components.elements(); e.hasMoreElements(); )
	((JLabel)e.nextElement()).setEnabled(isEnabled());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	actionPerformed
------------------------------------------------------------------------------------------------------------------------------------*/
public void		actionPerformed(ActionEvent inEvt)
{
if(inEvt == null)	// value was programatically set
	{
	for(Enumeration e = m_Components.elements(); e.hasMoreElements(); )
		((JLabel)e.nextElement()).setIcon(getStatusIcon());

	fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	createLabel
------------------------------------------------------------------------------------------------------------------------------------*/
static public JLabel	createLabel(String inPropertyName)
{
JLabel	label = new JLabel();

label.setName(inPropertyName);
label.setEnabled(false);
label.setRequestFocusEnabled(false);
label.setToolTipText(jEdit.getProperty(inPropertyName + ".tooltip"));

label.setIcon(ICON_MODEL_OBSOLETE);

return(label);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	attachComponent
------------------------------------------------------------------------------------------------------------------------------------*/
public void		attachComponent(JComponent inComponent)
{
super.attachComponent(inComponent);
((JLabel)inComponent).setIcon(getStatusIcon());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	detachComponent
------------------------------------------------------------------------------------------------------------------------------------*/
public void		detachComponent(JComponent inComponent)
{
super.detachComponent(inComponent);
((JLabel)inComponent).setIcon(ICON_MODEL_OBSOLETE);
}
}

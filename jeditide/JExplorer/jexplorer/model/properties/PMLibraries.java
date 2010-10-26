/*
 * PMLibraries.java
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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

import jexplorer.JExplorerConstants;
import jexplorer.model.Model;
import jexplorer.gui.lib.LibrariesDialog;
/*------------------------------------------------------------------------------------------------------------------------------------
	PMLibraries
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Manages the libraries of a <code>Model</code>.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public final class PMLibraries extends PropertyManager implements JExplorerConstants
{
protected HashSet	m_Values = new HashSet();
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public PMLibraries(	Hashtable	inActionsTable,
					Model		inModel,
					String		inName)
{
super(inActionsTable, inModel, inName);
getProperties();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getIterator
------------------------------------------------------------------------------------------------------------------------------------*/
public Iterator	getIterator()
{
return m_Values.iterator();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setValues
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setValues(Enumeration inEnum)
{
m_Values.clear();

while(inEnum.hasMoreElements())
	m_Values.add(inEnum.nextElement());

setProperties();
fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getProperties
------------------------------------------------------------------------------------------------------------------------------------*/
private void	getProperties()
{
for(int index = 0; /* no condition */; index++)
	{
	String	libPath	= m_Model.getProject().getProperty(m_Name + "." + index);

	if(libPath == null)	break;

	m_Values.add(libPath);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setProperties
------------------------------------------------------------------------------------------------------------------------------------*/
private void	setProperties()
{
int			index;
Iterator	it;
Properties	props = m_Model.getProject().getProperties();

// remove all existing properties

for(index = 0; /* no condition */; index++)
	{
	if(props.remove(m_Name + "." + index) == null)	break;
	}

// create the new set of properties

for(it = m_Values.iterator(), index = 0;
	it.hasNext();
	index++)
	{
	String	libPath	= (String)it.next();

	props.setProperty(m_Name + "." + index, libPath);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setEnabled
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setEnabled(boolean inEnabled)
{
super.setEnabled(inEnabled);

for(Enumeration en = m_Components.elements(); en.hasMoreElements(); )
	((JButton)en.nextElement()).setEnabled(isEnabled());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	actionPerformed
------------------------------------------------------------------------------------------------------------------------------------*/
public void		actionPerformed(ActionEvent inEvt)
{
new LibrariesDialog((JButton)inEvt.getSource(), this).show();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	createButton
------------------------------------------------------------------------------------------------------------------------------------*/
static public JButton	createButton(String inPropertyName, Icon inIcon)
{
JButton	button = new JButton();

button.setName(inPropertyName);
button.setEnabled(false);
button.setRequestFocusEnabled(false);
button.setToolTipText(jEdit.getProperty(inPropertyName + ".tooltip"));

button.setMargin(BUTTONS_MARGINS);
button.setIcon(inIcon);

return(button);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	attachComponent
------------------------------------------------------------------------------------------------------------------------------------*/
public void		attachComponent(JComponent inComponent)
{
super.attachComponent(inComponent);
((JButton)inComponent).addActionListener(this);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	detachComponent
------------------------------------------------------------------------------------------------------------------------------------*/
public void		detachComponent(JComponent inComponent)
{
super.detachComponent(inComponent);
((JButton)inComponent).removeActionListener(this);
}
}

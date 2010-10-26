/*
 * PMChoice.java
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
import javax.swing.JComboBox;

import org.gjt.sp.jedit.jEdit;

import jexplorer.JExplorerPlugin;
import jexplorer.model.Model;
/*------------------------------------------------------------------------------------------------------------------------------------
	PMChoice
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Manages an <code>int</code> property value.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public final class PMChoice extends PropertyManager
{
protected int		m_Value = -1;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public PMChoice(Hashtable	inActionsTable,
				Model		inModel,
				String		inName)
{
super(inActionsTable, inModel, inName);
m_Value = getProperty();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getProperty
------------------------------------------------------------------------------------------------------------------------------------*/
private int		getProperty()
{
String	value = m_Model.getProject().getProperty(m_Name);

if(value == null)
	return jEdit.getIntegerProperty(m_Name, -1);
else
	try
		{ return Integer.parseInt(value.trim()); }
	catch(NumberFormatException nfe)
		{ return -1; }
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setProperty
------------------------------------------------------------------------------------------------------------------------------------*/
private void	setProperty(int inValue)
{
m_Model.getProject().setProperty(m_Name, String.valueOf(inValue));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getValue
------------------------------------------------------------------------------------------------------------------------------------*/
public int		getValue()
{
return m_Value;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setValue
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setValue(int inValue)
{
m_Value = inValue;
actionPerformed(null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setEnabled
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setEnabled(boolean inEnabled)
{
super.setEnabled(inEnabled);

for(Enumeration e = m_Components.elements(); e.hasMoreElements(); )
	((JComboBox)e.nextElement()).setEnabled(isEnabled());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	actionPerformed
------------------------------------------------------------------------------------------------------------------------------------*/
public void		actionPerformed(ActionEvent inEvt)
{
if(inEvt == null)	// value was programatically set
	{
	for(Enumeration e = m_Components.elements(); e.hasMoreElements(); )
		((JComboBox)e.nextElement()).setSelectedIndex(m_Value);
	}
else				// the event is coming from a JComboBox
	{
	JComboBox	srcCombo = (JComboBox)inEvt.getSource();

	m_Value = srcCombo.getSelectedIndex();

	for(Enumeration e = m_Components.elements(); e.hasMoreElements(); )
		{
		JComboBox	combo = (JComboBox)e.nextElement();

		if(!combo.equals(srcCombo))
			combo.setSelectedIndex(m_Value);
		}
	}

setProperty(m_Value);
fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	createComboBox
------------------------------------------------------------------------------------------------------------------------------------*/
static public JComboBox	createComboBox(String inPropertyName, String[] inItems)
{
JComboBox	combo = new JComboBox();

// build dummy data model for initial combobox setup
for(int i = 0; i < inItems.length; i++)
	combo.addItem(inItems[i]);

combo.setName(inPropertyName);
combo.setEnabled(false);
combo.setRequestFocusEnabled(false);	// is this working?
combo.setToolTipText(jEdit.getProperty(inPropertyName + ".tooltip"));

combo.setFont(JExplorerPlugin.getUIFont());

return(combo);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	attachComponent
------------------------------------------------------------------------------------------------------------------------------------*/
public void		attachComponent(JComponent inComponent)
{
super.attachComponent(inComponent);
((JComboBox)inComponent).setSelectedIndex(m_Value);		// to avoid an unwanted message,
((JComboBox)inComponent).addActionListener(this);		// set selection before attaching listener
}
/*------------------------------------------------------------------------------------------------------------------------------------
	detachComponent
------------------------------------------------------------------------------------------------------------------------------------*/
public void		detachComponent(JComponent inComponent)
{
super.detachComponent(inComponent);
((JComboBox)inComponent).removeActionListener(this);	// to avoid an unwanted message,
((JComboBox)inComponent).setSelectedIndex(0);			// detach listener before setting selection
}
}

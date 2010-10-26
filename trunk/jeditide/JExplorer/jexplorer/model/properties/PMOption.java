/*
 * PMOption.java
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
import javax.swing.JToggleButton;
import javax.swing.Icon;

import org.gjt.sp.jedit.jEdit;

import jexplorer.JExplorerConstants;
import jexplorer.model.Model;
/*------------------------------------------------------------------------------------------------------------------------------------
	PMOption
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Manages a <code>boolean</code> property value.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public final class PMOption extends PropertyManager implements JExplorerConstants
{
protected boolean	m_Value = false;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public PMOption(Hashtable	inActionsTable,
				Model		inModel,
				String		inName)
{
super(inActionsTable, inModel, inName);
m_Value	= getProperty();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getProperty
------------------------------------------------------------------------------------------------------------------------------------*/
private boolean	getProperty()
{
String	value = m_Model.getProject().getProperty(m_Name);

if(value == null)				return jEdit.getBooleanProperty(m_Name);
else if(value.equals("true"))	return true;
else if(value.equals("false"))	return false;
else							return jEdit.getBooleanProperty(m_Name);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setProperty
------------------------------------------------------------------------------------------------------------------------------------*/
private void	setProperty(boolean inValue)
{
if(m_Value)	m_Model.getProject().setProperty(m_Name, "true");
else		m_Model.getProject().setProperty(m_Name, "false");
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getValue
------------------------------------------------------------------------------------------------------------------------------------*/
public boolean	getValue()
{
return m_Value;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setValue
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setValue(boolean inValue)
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
	((JToggleButton)e.nextElement()).setEnabled(isEnabled());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	actionPerformed
------------------------------------------------------------------------------------------------------------------------------------*/
public void		actionPerformed(ActionEvent inEvt)
{
if(inEvt == null)	// value was programatically set
	{
	for(Enumeration e = m_Components.elements(); e.hasMoreElements(); )
		((JToggleButton)e.nextElement()).setSelected(m_Value);
	}
else				// the event is coming from a JToggleButton
	{
	JToggleButton	srcBtn = (JToggleButton)inEvt.getSource();

	m_Value = srcBtn.isSelected();

	for(Enumeration e = m_Components.elements(); e.hasMoreElements(); )
		{
		JToggleButton	button = (JToggleButton)e.nextElement();

		if(!button.equals(srcBtn))
			button.setSelected(m_Value);
		}
	}

setProperty(m_Value);
fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	createToggleButton
------------------------------------------------------------------------------------------------------------------------------------*/
static public JToggleButton	createToggleButton(String inPropertyName, Icon inIcon)
{
JToggleButton	button = new JToggleButton();

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
((JToggleButton)inComponent).setSelected(m_Value);
((JToggleButton)inComponent).addActionListener(this);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	detachComponent
------------------------------------------------------------------------------------------------------------------------------------*/
public void		detachComponent(JComponent inComponent)
{
super.detachComponent(inComponent);
((JToggleButton)inComponent).removeActionListener(this);
((JToggleButton)inComponent).setSelected(false);
}
}

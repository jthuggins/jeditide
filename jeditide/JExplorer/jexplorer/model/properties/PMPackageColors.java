/*
 * PMPackageColors.java
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
import java.util.TreeMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import java.awt.Color;
import java.awt.Component;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.UIManager;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

import jexplorer.JExplorerConstants;
import jexplorer.model.Model;
import jexplorer.gui.pkgcol.PkgColorsDialog;
/*------------------------------------------------------------------------------------------------------------------------------------
	PMPackageColors
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Manages the package-color definitions of a <code>Model</code>.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.08
 */
public final class PMPackageColors extends PropertyManager implements JExplorerConstants
{
protected TreeMap	m_Values = new TreeMap(Collections.reverseOrder());
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public PMPackageColors(	Hashtable	inActionsTable,
						Model		inModel,
						String		inName)
{
super(inActionsTable, inModel, inName);
getProperties();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getValues
------------------------------------------------------------------------------------------------------------------------------------*/
public TreeMap	getValues()
{
return m_Values;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setValues
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setValues(TreeMap inValues)
{
m_Values.clear();
m_Values.putAll(inValues);
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
	String	packageName	= m_Model.getProject().getProperty(m_Name + "." + index + ".package"),
			colorString	= m_Model.getProject().getProperty(m_Name + "." + index + ".color");

	if(packageName == null || colorString == null)	break;

	m_Values.put(packageName, GUIUtilities.parseColor(colorString, Color.white));
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
	if(	props.remove(m_Name + "." + index + ".package") == null	&&
		props.remove(m_Name + "." + index + ".color") == null)	break;
	}

// create the new set of properties

for(it = m_Values.keySet().iterator(), index = 0;
	it.hasNext();
	index++)
	{
	String	packageName	= (String)it.next();
	Color	color		= (Color)m_Values.get(packageName);
	String	colorString	= GUIUtilities.getColorHexString(color);

	props.setProperty(m_Name + "." + index + ".package", packageName);
	props.setProperty(m_Name + "." + index + ".color", colorString);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setEnabled
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setEnabled(boolean inEnabled)
{
super.setEnabled(inEnabled);

for(Enumeration e = m_Components.elements(); e.hasMoreElements(); )
	((JButton)e.nextElement()).setEnabled(isEnabled());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	actionPerformed
------------------------------------------------------------------------------------------------------------------------------------*/
public void		actionPerformed(ActionEvent inEvt)
{
new PkgColorsDialog((JButton)inEvt.getSource(), this).show();
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
/*------------------------------------------------------------------------------------------------------------------------------------
	getPackageColor
------------------------------------------------------------------------------------------------------------------------------------*/
public Color	getPackageColor(String inPackageName)
{
return((Color)m_Values.get(inPackageName));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getElementColor
------------------------------------------------------------------------------------------------------------------------------------*/
public Color	getElementColor(String inElementName)
{
// check the application list first

Iterator	it = m_Values.keySet().iterator();

while(it.hasNext())
	{
	String	key = (String)it.next();

	if(inElementName.startsWith(key.concat(".")))
		return((Color)m_Values.get(key));
	}

return(UIManager.getColor("Tree.background"));
}
}

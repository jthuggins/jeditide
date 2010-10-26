/*
 * ActionHandler.java
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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
/*------------------------------------------------------------------------------------------------------------------------------------
	ActionHandler
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A base class for all actions and property managers.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
abstract public class ActionHandler implements ActionListener
{
private		boolean		m_Enabled = true;
protected	String		m_Name;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public ActionHandler(	Hashtable	inActionsTable,
						String		inName)
{
m_Name = inName;
inActionsTable.put(m_Name, this);	// add self to the property manager table
}
/*------------------------------------------------------------------------------------------------------------------------------------
	Compact Methods
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setEnabled	(boolean inValue)	{ m_Enabled = inValue; }
public boolean	isEnabled	()					{ return m_Enabled; }
public String	getName		()					{ return m_Name; }
/*------------------------------------------------------------------------------------------------------------------------------------
	[ActionListener] actionPerformed
------------------------------------------------------------------------------------------------------------------------------------*/
public void			actionPerformed(ActionEvent inEvt)
{
JOptionPane.showMessageDialog(null, "Not implemented.");
}
}


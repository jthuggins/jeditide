/*
 * PkgColorsModel.java
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

package jexplorer.gui.pkgcol;

import java.util.Vector;
import java.util.Iterator;
import java.util.TreeMap;

import java.awt.Color;

import javax.swing.AbstractListModel;
import javax.swing.UIManager;

import jexplorer.model.properties.PMPackageColors;
/*------------------------------------------------------------------------------------------------------------------------------------
	PkgColorsModel
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A ListModel for the <code>PkgColorsDialog</code>.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.08
 */
public class PkgColorsModel extends AbstractListModel
{
private TreeMap	m_ValuesMap		= new TreeMap();
private	Vector	m_ListVector	= new Vector();
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	PkgColorsModel(PMPackageColors inPropertiesMgr)
{
m_ValuesMap.putAll(inPropertiesMgr.getValues());
buildInterface();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	compact methods
------------------------------------------------------------------------------------------------------------------------------------*/
public int		getSize()					{ return m_ListVector.size(); }
public Object	getElementAt(int inIndex)	{ return m_ListVector.get(inIndex); }
public TreeMap	getValues()					{ return(m_ValuesMap); }
/*------------------------------------------------------------------------------------------------------------------------------------
	getColor
------------------------------------------------------------------------------------------------------------------------------------*/
public Color		getColor(String inPackageName)
{
Color	color = (Color)m_ValuesMap.get(inPackageName);

if(color == null)
	color = UIManager.getColor("Tree.background");

return(color);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	clearInterface
------------------------------------------------------------------------------------------------------------------------------------*/
private void		clearInterface()
{
m_ListVector.clear();	// clear the list support vector
}
/*------------------------------------------------------------------------------------------------------------------------------------
	buildInterface
------------------------------------------------------------------------------------------------------------------------------------*/
private void		buildInterface()
{
Iterator	it = m_ValuesMap.keySet().iterator();

while(it.hasNext())
	m_ListVector.add((String)it.next());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addDefinition
------------------------------------------------------------------------------------------------------------------------------------*/
void		addDefinition(String inPackageName, Color inColor)
{
boolean		isNewEntry = !m_ValuesMap.containsKey(inPackageName);

clearInterface();
m_ValuesMap.put(inPackageName, inColor);
buildInterface();

int			index = m_ListVector.indexOf(inPackageName);

if(isNewEntry)	fireIntervalAdded(this, index, index);
else			fireContentsChanged(this, index, index);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	removeDefinition
------------------------------------------------------------------------------------------------------------------------------------*/
void		removeDefinition(String inPackageName)
{
int		index = m_ListVector.indexOf(inPackageName);

clearInterface();
m_ValuesMap.remove(inPackageName);
buildInterface();

fireIntervalRemoved(this, index, index);
}
}

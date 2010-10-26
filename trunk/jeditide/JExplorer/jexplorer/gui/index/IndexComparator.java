/*
 * IndexComparator.java
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

package jexplorer.gui.index;

import java.util.Comparator;

import jexplorer.model.members.MElement;
import jexplorer.model.members.MMember;
/*------------------------------------------------------------------------------------------------------------------------------------
	IndexComparator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A singleton to alphabetically compare objects for <code>IndexModel</code>.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public class IndexComparator implements Comparator
{
private static IndexComparator	s_Instance;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
private IndexComparator()
{
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getInstance
------------------------------------------------------------------------------------------------------------------------------------*/
static public IndexComparator	getInstance()
{
if(s_Instance == null)
	s_Instance = new IndexComparator();

return(s_Instance);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getKeyName
------------------------------------------------------------------------------------------------------------------------------------*/
public String	getKeyName(Object inKey)
{
return (inKey instanceof MElement) ?
			((MElement)inKey).getIndexText() :
			((MMember)inKey).getLocalName();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	compare [Comparator]
------------------------------------------------------------------------------------------------------------------------------------*/
public int		compare(Object inKey1, Object inKey2)
{
return getKeyName(inKey1).compareToIgnoreCase(getKeyName(inKey2));
}
}


/*
 * MElementComparator.java
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

import java.util.Comparator;

import jexplorer.model.Model;

import jexplorer.model.members.MElement;
import jexplorer.model.members.MClass;
import jexplorer.model.members.MInterface;
/*------------------------------------------------------------------------------------------------------------------------------------
	MElementComparator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A singleton to compare <code>MElement</code> objects.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public class MElementComparator implements Comparator
{
private static MElementComparator	s_Instance;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
private MElementComparator()
{
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getInstance
------------------------------------------------------------------------------------------------------------------------------------*/
static public MElementComparator	getInstance()
{
if(s_Instance == null)
	s_Instance = new MElementComparator();

return(s_Instance);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	compare [Comparator]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Compares two <code>MElement</code> objects.
 * The order is: first interfaces, then classes; between these two groups,
 * the ordering follows alphabetical order of full qualified names.
 */
public int		compare(Object inKey1, Object inKey2)
{
if(inKey1 instanceof MInterface && inKey2 instanceof MClass)
	return -1;
else if(inKey1 instanceof MClass && inKey2 instanceof MInterface)
	return  1;
else
	return ((MElement)inKey1).getQualifiedName()
				.compareToIgnoreCase(((MElement)inKey2).getQualifiedName());
}
}


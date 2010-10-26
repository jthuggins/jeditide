/*
 * IndexModel.java
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

import java.util.Vector;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Enumeration;

import javax.swing.AbstractListModel;

import jexplorer.JExplorerConstants;

import jexplorer.model.members.MElement;
import jexplorer.model.members.MClass;
import jexplorer.model.members.MInterface;
import jexplorer.model.members.MMember;
import jexplorer.model.members.MField;
import jexplorer.model.members.MConstructor;
import jexplorer.model.members.MMethod;

import jexplorer.model.properties.ModelProperties;

import jexplorer.parser.source.SourcePos;
import jexplorer.parser.source.JExecutable;
import jexplorer.parser.source.JSource;
/*------------------------------------------------------------------------------------------------------------------------------------
	IndexModel
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A ListModel for IndexDockable's global index.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.09
 */
public class IndexModel extends AbstractListModel implements JExplorerConstants
{
private	Vector			m_Vector = new Vector();
private MConstructor	m_SearchDummy = new MConstructor(
	null,
	"",
	ACCESS_UNKNOWN,
	JExecutable.EMPTY_PARAMETERS_ARRAY,
	JSource.EMPTY_TYPES_ARRAY,
	false,
	null,
	null);
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public IndexModel()
{
}
/*------------------------------------------------------------------------------------------------------------------------------------
	compact methods [ListModel]
------------------------------------------------------------------------------------------------------------------------------------*/
public int		getSize()					{ return m_Vector.size(); }
public Object	getElementAt(int inIndex)	{ return m_Vector.get(inIndex); }
/*------------------------------------------------------------------------------------------------------------------------------------
	compact methods
------------------------------------------------------------------------------------------------------------------------------------*/
public boolean	hasElement(Object inObj)	{ return m_Vector.contains(inObj); }
/*------------------------------------------------------------------------------------------------------------------------------------
	indexOfItem
------------------------------------------------------------------------------------------------------------------------------------*/
public int		indexOfItem(String inText)
{
int			index;

// binary search the requested text using
// a fake member to allow for comparisons

m_SearchDummy.setName(inText);

index = Collections.binarySearch(m_Vector, m_SearchDummy, IndexComparator.getInstance());

// if a perfect match couldn't be found, binary search returns
// a negative value (see the java.util.Collections documentation)

if(index < 0)
	{
	index = Math.abs(index + 1);
	index = Math.max(index, 0);
	index = Math.min(index, m_Vector.size() - 1);
	}

// since the list may contain more than one element with identical
// displayed text, the binary search cannot guarantee to position upon
// the first element of the group; thus let's proceed backwards until
// we find an element with a different text

while(	index > 0 &&
		IndexComparator.getInstance().compare(getElementAt(index), getElementAt(index - 1)) == 0)
	index--;

return(index);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	regenerate
------------------------------------------------------------------------------------------------------------------------------------*/
public void		regenerate()
{
int		lastIndex = Math.max(0, m_Vector.size() - 1);

fireIntervalRemoved(this, 0, lastIndex);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	reset
------------------------------------------------------------------------------------------------------------------------------------*/
public void		reset()
{
int		lastIndex = Math.max(0, m_Vector.size() - 1);

m_Vector.clear();
fireIntervalRemoved(this, 0, lastIndex);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	sort
------------------------------------------------------------------------------------------------------------------------------------*/
public void		sort()
{
int		lastIndex = Math.max(0, m_Vector.size() - 1);

Collections.sort(m_Vector, IndexComparator.getInstance());
fireContentsChanged(this, 0, lastIndex);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setContent
------------------------------------------------------------------------------------------------------------------------------------*/
public void		setContent(	Iterator		inModelElementsIterator,
							ModelProperties	inProperties)
{
m_Vector.clear();

while(inModelElementsIterator.hasNext())
	{
	MElement	e = (MElement)inModelElementsIterator.next();

	// nested elements are managed by containing elements directly
	if(!e.isNestedElement())
		e.setIndexModelContent(m_Vector);
	}

Collections.sort(m_Vector, IndexComparator.getInstance());
fireContentsChanged(this, 0, Math.max(0, m_Vector.size() - 1));
}
}

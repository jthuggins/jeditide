/*
 * Finder.java
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

package jexplorer.parser.source;

import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;

import java.util.zip.ZipFile;
/*------------------------------------------------------------------------------------------------------------------------------------
	Finder
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Holds information about all source files and defined items in a project.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public class Finder
{
protected HashMap	m_Sources	= new HashMap();
protected HashMap	m_Elements	= new HashMap();
protected HashMap	m_Libraries	= new HashMap();
/*------------------------------------------------------------------------------------------------------------------------------------
	clear
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Clears all source files and elements references.
 */
public void		clear()
{
m_Sources.clear();
m_Elements.clear();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addLibrary
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds a library reference for type resolution.
 */
public void		addLibrary(String inPath)
	throws IOException
{
m_Libraries.put(inPath, new ZipFile(inPath));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	removeLibraries
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Clears all library references.
 */
public void		removeLibraries()
{
m_Libraries.clear();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getLibrariesIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over the library references.
 */
public Iterator	getLibrariesIterator()
{
return m_Libraries.values().iterator();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addSource
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds a <code>JSource</code> to this finder.
 */
public void		addSource(JSource inSource)
{
m_Sources.put(inSource.getFile().getAbsolutePath(), inSource);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	removeSource
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds a <code>JSource</code> to this finder.
 */
public void		removeSource(String inPath)
{
m_Sources.remove(inPath);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getSourcesIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over the sources.
 */
public Iterator	getSourcesIterator()
{
return m_Sources.values().iterator();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addElement
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds an elements to this finder.
 */
public void		addElement(JElement inElement)
{
m_Elements.put(inElement.getQualifiedName(), inElement);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	hasElement
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true if this finder knows the requested type.
 */
public boolean	hasElement(String inQualifiedType)
{
return m_Elements.get(inQualifiedType) != null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getElementsIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over the elements.
 */
public Iterator	getElementsIterator()
{
return m_Elements.values().iterator();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	build
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Build the global elements set and resolve types for all
 * elements and members.
 */
public void		build()
{
boolean		validData = true;
Iterator	it;

m_Elements.clear();

// check if all sources are up-to-date and valid
for(it = m_Sources.values().iterator(); it.hasNext() && validData;)
	{
	JSource		source = (JSource)it.next();

	if(!source.isParsed() || !source.isValid())
		validData = false;
	}

if(validData)
	{				
	// pass 1: collect all the defined elements
	for(it = m_Sources.values().iterator(); it.hasNext();)
		((JSource)it.next()).putElementsIntoFinder(this);

	// pass 2: resolve types
	for(it = m_Sources.values().iterator(); it.hasNext();)
		((JSource)it.next()).resolveTypes(this);
	}
}
}


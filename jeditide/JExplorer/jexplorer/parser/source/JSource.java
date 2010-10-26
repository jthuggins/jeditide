/*
 * JSource.java
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

import java.io.File;

import java.util.HashSet;
import java.util.Iterator;

import java.util.zip.ZipFile;
/*------------------------------------------------------------------------------------------------------------------------------------
	JSource
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Represents a java source file.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.14
 */
public class JSource
{
			final static String[]	EMPTY_STRINGS_ARRAY		= new String[0];
			final static JElement[]	EMPTY_ELEMENTS_ARRAY	= new JElement[0];
protected	final static String		DEFAULT_PACKAGE			= "<default package>";
public		final static JType[]	EMPTY_TYPES_ARRAY		= new JType[0];

static	LookupResult	s_LookupResult = new LookupResult();

protected boolean	m_IsParsed	= false;
protected boolean	m_IsValid	= true;
protected File		m_File;
protected String	m_Package;
protected HashSet	m_SingleTypeImports;
protected HashSet	m_TypeImportOnDemands;
protected HashSet	m_Elements;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>JSource</code>.
 *
 * @param inFile	The reference <code>java.io.File</code>.
 * @param inPackage	The package this source belongs to or <code>null</code>.
 */
public	JSource(File inFile)
{
m_File = inFile;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getFile
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the reference <code>java.io.File</code>.
 */
public File		getFile()
{
return m_File;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setParsed
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Sets if this source has been parsed.
 */
public void		setParsed(boolean inIsParsed)
{
m_IsParsed = inIsParsed;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isParsed
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true this source has been parsed.
 */
public boolean	isParsed()
{
return m_IsParsed;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setValid
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Sets the validation status for this source.
 */
public void		setValid(boolean inIsValid)
{
m_IsValid = inIsValid;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isValid
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the validation status for this source.
 */
public boolean	isValid()
{
return m_IsValid;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	setPackage
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Sets the package this source belongs to.
 */
public void		setPackage(String inPackage)
{
m_Package = inPackage;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getPackage
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the package this source belongs to.
 */
public String	getPackage()
{
if(m_Package != null)
	return(m_Package);
else
	return(DEFAULT_PACKAGE);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addSingleTypeImport
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds a single-type-import to this source.
 */
public void		addSingleTypeImport(String inImport)
{
if(m_SingleTypeImports == null)
	m_SingleTypeImports = new HashSet();

m_SingleTypeImports.add(inImport);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getSingleTypeImportsIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over this source's single-type-imports
 * or <code>null</code> if this source has no single-type-imports.
 */
public Iterator	getSingleTypeImportsIterator()
{
return m_SingleTypeImports != null ? m_SingleTypeImports.iterator() : null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addTypeImportOnDemand
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds a type-import-on-demand to this source.
 */
public void		addTypeImportOnDemand(String inImport)
{
if(m_TypeImportOnDemands == null)
	m_TypeImportOnDemands = new HashSet();

m_TypeImportOnDemands.add(inImport);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getTypeImportOnDemandsIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over this source's type-import-on-demands
 * or <code>null</code> if this source has no type-import-on-demands.
 */
public Iterator	getTypeImportOnDemandsIterator()
{
return m_TypeImportOnDemands != null ? m_TypeImportOnDemands.iterator() : null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addElement
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds an element to this source.
 */
public void		addElement(JElement inElement)
{
if(m_Elements == null)
	m_Elements = new HashSet();

m_Elements.add(inElement);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getElementsIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over this source's elements
 * or <code>null</code> if this source has no elements.
 */
public Iterator	getElementsIterator()
{
return m_Elements != null ? m_Elements.iterator() : null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	putElementsIntoFinder
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Puts this source's elements into a <code>Finder</code>.
 */
void	putElementsIntoFinder(Finder inFinder)
{
for(Iterator eIt = getElementsIterator();
	eIt != null && eIt.hasNext();)
	{
	JElement	je = (JElement)eIt.next();

	inFinder.addElement(je);			// put the contained element
	je.putElementsIntoFinder(inFinder);	// tell the element to put its contained elements
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	resolveTypes
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Resolve this source's type references.
 */
void	resolveTypes(Finder inFinder)
{
for(Iterator eIt = getElementsIterator();
	eIt != null && eIt.hasNext();)
	{
	JElement	je = (JElement)eIt.next();
	je.resolveTypes(inFinder, this);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	lookupType
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Try to resolve a simple or partial type name into a fully qualified name.
 *
 * @param inFinder			The global elements finder.
 * @param inScopeElement	The element defining the search scope.
 * @param inType			The type to resolve.
 */
public LookupResult	lookupType(Finder inFinder, JElement inScopeElement, String inType)
{
lookupTypeAsMemberClass(inScopeElement, inType);
if(s_LookupResult.isSuccesful())
	return(s_LookupResult);

lookupTypeAsSingleTypeImport(inType);
if(s_LookupResult.isSuccesful())
	return(s_LookupResult);

lookupTypeAsTopLevelClass(inType);
if(s_LookupResult.isSuccesful())
	return(s_LookupResult);

lookupTypeAsPackageSibling(inFinder, inType);
if(s_LookupResult.isSuccesful())
	return(s_LookupResult);

lookupTypeAsTypeImportOnDemand(inFinder, inType);
if(s_LookupResult.isSuccesful())
	return(s_LookupResult);

s_LookupResult.init(false, null);
return(s_LookupResult);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	lookupTypeAsMemberClass
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true if the requested type is a member class.
 *
 * @param inScopeElement	The element defining the search scope.
 * @param inType			The type to resolve.
 */
private LookupResult	lookupTypeAsMemberClass(JElement inScopeElement, String inType)
{
for(Iterator it = inScopeElement.getElementsIterator();
	it != null && it.hasNext();)
	{
	JElement	je = (JElement)it.next();

	if(je.getQualifiedName().endsWith("." + inType))
		{
		s_LookupResult.init(true, je.getQualifiedName());
		return(s_LookupResult);
		}
	else
		{
		LookupResult	r = lookupTypeAsMemberClass(je, inType);
		if(r.isSuccesful())
			return(s_LookupResult);
		}
	}

s_LookupResult.init(false, null);
return(s_LookupResult);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	lookupTypeAsSingleTypeImport
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true if the requested type is defined as a single-type-import.
 *
 * @param inType	The type to resolve.
 */
private LookupResult	lookupTypeAsSingleTypeImport(String inType)
{
for(Iterator it = getSingleTypeImportsIterator();
	it != null && it.hasNext();)
	{
	String	importString = (String)it.next();

	if(importString.endsWith("." + inType))
		{
		s_LookupResult.init(true, importString);
		return(s_LookupResult);
		}
	}

s_LookupResult.init(false, null);
return(s_LookupResult);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	lookupTypeAsTopLevelClass
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true if the requested type is a top-level class.
 *
 * @param inType	The type to resolve.
 */
private LookupResult	lookupTypeAsTopLevelClass(String inType)
{
for(Iterator it = getElementsIterator();
	it != null && it.hasNext();)
	{
	JElement	je = (JElement)it.next();

	if(je.getQualifiedName().endsWith("." + inType))
		{
		s_LookupResult.init(true, je.getQualifiedName());
		return(s_LookupResult);
		}
	}

s_LookupResult.init(false, null);
return(s_LookupResult);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	lookupTypeAsPackageSibling
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true if the requested type is defined within the package.
 *
 * @param inFinder	The global elements finder.
 * @param inType	The type to resolve.
 */
private LookupResult	lookupTypeAsPackageSibling(Finder inFinder, String inType)
{
String	qualifiedType = getPackage() + "." + inType;

if(inFinder.hasElement(qualifiedType))
	s_LookupResult.init(true, qualifiedType);
else
	s_LookupResult.init(false, null);

return(s_LookupResult);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	lookupTypeAsTypeImportOnDemand
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns true if the requested type is defined as a type-import-on-demand.
 *
 * @param inType	The type to resolve.
 */
private LookupResult	lookupTypeAsTypeImportOnDemand(Finder inFinder, String inType)
{
for(Iterator it = getTypeImportOnDemandsIterator();
	it != null && it.hasNext();)
	{
	String	importStr	= (String)it.next(),				// package
			typeString	= importStr + "." + inType;			// package.Type

	// search into sources

	if(inFinder.hasElement(typeString))
		{
		s_LookupResult.init(true, typeString);
		return(s_LookupResult);
		}

	// search into libraries

	String	partialType	= inType.replace('.', '$'),			// Top.Inner -> Top$Inner
			fullString	= importStr + "." + partialType,	// package.Top$Inner
			zipString	= fullString.replace('.', '/'),		// package/Top$Inner
			classFile	= zipString + ".class";				// package/Top$Inner.class

	for(Iterator libIt = inFinder.getLibrariesIterator(); libIt.hasNext();)
		{
		ZipFile		zipFile = (ZipFile)libIt.next();

		if(zipFile.getEntry(classFile) != null)
			{
			s_LookupResult.init(true, typeString);
			return(s_LookupResult);
			}
		}
	}

s_LookupResult.init(false, null);
return(s_LookupResult);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	lookupResult [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Represents a lookup result.
 */
static class LookupResult
{
boolean		m_Succesful = false;
String		m_ResolvedType;

public boolean	isSuccesful()		{ return m_Succesful; }
public String	getResolvedType()	{ return m_ResolvedType; }

/*private*/ void	init(boolean inSuccesful, String inResolvedType)
{
m_Succesful		= inSuccesful;
m_ResolvedType	= inResolvedType;
}
}
}

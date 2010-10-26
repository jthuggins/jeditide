/*
 * Model.java
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

package jexplorer.model;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.lang.reflect.Modifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.TreeSet;
import java.util.EventObject;
import java.util.Collection;
import java.util.ArrayList;

import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import javax.swing.event.EventListenerList;

import projectviewer.PVActions;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTFile;
import projectviewer.event.ProjectListener;
import projectviewer.event.ProjectEvent;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;

import errorlist.ErrorSource;

import jexplorer.JExplorerPlugin;
import jexplorer.JExplorerConstants;

import jexplorer.model.members.MElement;
import jexplorer.model.members.MClass;
import jexplorer.model.members.MInterface;
import jexplorer.model.members.MType;
import jexplorer.model.members.MField;
import jexplorer.model.members.MConstructor;
import jexplorer.model.members.MMethod;
import jexplorer.model.members.MParameter;
import jexplorer.model.members.MElementComparator;

import jexplorer.model.properties.ModelProperties;
import jexplorer.model.properties.PMModelStatus;
import jexplorer.model.properties.PMLibraries;

import jexplorer.model.selection.ModelSelection;
import jexplorer.model.selection.SelectionListener;
import jexplorer.model.selection.SelectionEvent;

import jexplorer.gui.JExplorerDockable;
import jexplorer.gui.model.ModelDialog;

import jexplorer.parser.JavaParser;
import jexplorer.parser.ParseException;
import jexplorer.parser.TokenMgrError;

import jexplorer.parser.source.Finder;
import jexplorer.parser.source.Comment;
import jexplorer.parser.source.JSource;
import jexplorer.parser.source.JMember;
import jexplorer.parser.source.JElement;
import jexplorer.parser.source.JClass;
import jexplorer.parser.source.JInterface;
import jexplorer.parser.source.JField;
import jexplorer.parser.source.JConstructor;
import jexplorer.parser.source.JMethod;
import jexplorer.parser.source.JType;

import jexplorer.parser.grammar.JExplorerParseException;
/*------------------------------------------------------------------------------------------------------------------------------------
	Model
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A model for the JExplorer plugin.
 *
 * @author Amedeo Farello
 * @version 0.3, 2003.05.24
 */
public class Model
	implements ProjectListener, SelectionListener, JExplorerConstants
{
static private final boolean	DEBUG = false;

private VPTProject			m_Project;
private ModelProperties		m_Properties;
private ModelSelection		m_Selection;
private Hashtable			m_Files				= new Hashtable();
private Finder				m_Finder			= new Finder();
private HashMap				m_ElementsMap		= new HashMap();
private TreeSet				m_WorkSet			= new TreeSet(MElementComparator.getInstance());
private EventListenerList	m_ListenerList		= new EventListenerList();
private ParsingManager		m_PendingParsingMgr	= null;
private Timer				m_ParseOnKeystrokeTimer;

private LibrariesUpdater	m_LibrariesUpdater	= new LibrariesUpdater();
private ElementsUpdater		m_ElementsUpdater	= new ElementsUpdater();
private MembersBuilder		m_MembersBuilder	= new MembersBuilder();
private WorkSetBuilder		m_WorkSetBuilder	= new WorkSetBuilder();
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public Model(VPTProject inProject)
{
// attention: the creation order is fundamental
m_Project		= inProject;					// 1: save Project reference
m_Selection		= new ModelSelection(this);		// 2: create the selection model
m_Properties	= new ModelProperties(this);	// 3: create the properties manager

// creates the parse-on-keystroke timer
m_ParseOnKeystrokeTimer = new Timer(0, new ActionListener() {
	public void actionPerformed(ActionEvent evt)
		{
		VFSManager.runInAWTThread(new SavedBuffersParseTrigger());
		}
	});

m_ParseOnKeystrokeTimer.setRepeats(false);

// register the error source
getProperties().getModelStatus().register();

getSelectionModel().addSelectionListener(this);

getProperties().getPM(PM_LIBRARIES)			.addActionListener(m_LibrariesUpdater);

getProperties().getPM(PM_USE_SHORT_NAMES)		.addActionListener(m_ElementsUpdater);
getProperties().getPM(PM_SHOW_ACCESS_SYMBOLS)	.addActionListener(m_ElementsUpdater);

getProperties().getPM(PD_SHOW_NESTED)		.addActionListener(m_MembersBuilder);
getProperties().getPM(PD_SHOW_FIELDS)		.addActionListener(m_MembersBuilder);
getProperties().getPM(PD_SHOW_CONSTRUCTORS)	.addActionListener(m_MembersBuilder);
getProperties().getPM(PD_SHOW_METHODS)		.addActionListener(m_MembersBuilder);
getProperties().getPM(PD_SHOW_PUBLIC)		.addActionListener(m_MembersBuilder);
getProperties().getPM(PD_SHOW_PROTECTED)	.addActionListener(m_MembersBuilder);
getProperties().getPM(PD_SHOW_PRIVATE)		.addActionListener(m_MembersBuilder);
getProperties().getPM(PD_SHOW_PACKAGE)		.addActionListener(m_MembersBuilder);
getProperties().getPM(PM_SHOW_DEPRECATED)	.addActionListener(m_MembersBuilder);

getProperties().getPM(PG_CURRENT_LAYOUT)	.addActionListener(m_WorkSetBuilder);
getProperties().getPM(PI_SHOW_CLASSES)		.addActionListener(m_WorkSetBuilder);
getProperties().getPM(PI_SHOW_INTERFACES)	.addActionListener(m_WorkSetBuilder);
getProperties().getPM(PI_SHOW_NESTED)		.addActionListener(m_WorkSetBuilder);
getProperties().getPM(PG_FILTER_SELECTION)	.addActionListener(m_WorkSetBuilder);
getProperties().getPM(PG_CURRENT_RELATIVES)	.addActionListener(m_WorkSetBuilder);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	terminate
------------------------------------------------------------------------------------------------------------------------------------*/
public void		terminate()
{
// clear and unregister the error source
getProperties().getModelStatus().unregister();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	compact methods
------------------------------------------------------------------------------------------------------------------------------------*/
public	VPTProject	getProject()			{ return m_Project; }
public	boolean		hasFile(String inPath)	{ return (m_Files.get(inPath) != null); }

protected void	addElement(MElement e)	{ m_ElementsMap.put(e.getQualifiedName(), e); }

public MElement	getElement(String inQualifName)	{ return (MElement)m_ElementsMap.get(inQualifName); }
public Iterator	getElementsIterator()			{ return m_ElementsMap.values().iterator(); }

public ModelSelection	getSelectionModel()		{ return m_Selection; }
public ModelProperties	getProperties()			{ return m_Properties; }

public TreeSet	getWorkSet()							{ return m_WorkSet; }
public boolean	isElementWorking(MElement inElement)	{ return m_WorkSet.contains(inElement); }
/*------------------------------------------------------------------------------------------------------------------------------------
	addListener
------------------------------------------------------------------------------------------------------------------------------------*/
public void	addListener(ModelListener inListener)
{
m_ListenerList.add(ModelListener.class, inListener);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	removeListener
------------------------------------------------------------------------------------------------------------------------------------*/
public void	removeListener(ModelListener inListener)
{
m_ListenerList.remove(ModelListener.class, inListener);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	fireModelEvent
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	fireModelEvent(boolean inIsUpdateEvt)
{
// Guaranteed to return a non-null array
Object[]	listeners = m_ListenerList.getListenerList();

EventObject	evt = null;

// Process the listeners FIRST to LAST, notifying
// those that are interested in this event
for(int i = 0; i < listeners.length; i += 2)
	if(listeners[i] == ModelListener.class)
		{
		if(evt == null)
			evt = new EventObject(this);

		if(inIsUpdateEvt)
			((ModelListener)listeners[i + 1]).modelUpdated(evt);
		else
			((ModelListener)listeners[i + 1]).modelParsing(evt);
		}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addFile
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Adds a ProjectViewer file to the model.
 * Accepts only files with .java extension.
 *
 * @param	inVPTFile the ProjectViewer file
 * @return	<code>true</code> if the file was added. 
 */
public boolean		addFile(VPTFile inVPTFile)
{
File	file = inVPTFile.getFile();

if(file.getName().endsWith(".java"))
	{
	m_Files.put(file.getAbsolutePath(), file);
	return(true);
	}

return(false);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	loadProject
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* Parses the project files and builds the model.
*/
public void		loadProject()
{
m_Files.clear();
m_ElementsMap.clear();
m_Finder.clear();

librariesChanged();

for(Iterator it = m_Project.getFiles().iterator(); it.hasNext();)
	addFile((VPTFile)it.next());

if(m_Files.size() > 0)
	{
	ParsingManager	pm = new ParsingManager();

	pm.addFiles(m_Files.values(), true);
	VFSManager.runInWorkThread(pm);
	}
else	// the project is empty
	{
	buildModel();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	fileAdded [implements ProjectListener]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* Method called when a single file has been added to a project.
* If the file is a Java file, records the File reference and parses the file.
*/
public void		fileAdded(ProjectEvent inEvt)
{
VPTFile		vptf = inEvt.getAddedFile();

if(addFile(vptf))
	{
	ParsingManager	pm = new ParsingManager();

	pm.addFile(vptf.getFile(), true);
	VFSManager.runInWorkThread(pm);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	filesAdded [implements ProjectListener]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* Method called when several files have been added to a project.
* If there are Java files among them, records their File references and parses them.
*/
public void		filesAdded(ProjectEvent inEvt)
{
ArrayList		vptfList	= inEvt.getAddedFiles();
ParsingManager	pm			= null;

for(Iterator it = vptfList.iterator(); it.hasNext();)
	{
	VPTFile		vptf = (VPTFile)it.next();

	if(addFile(vptf))
		{
		if(pm == null)	pm = new ParsingManager();
		pm.addFile(vptf.getFile(), true);
		}
	}

if(pm != null)
	VFSManager.runInWorkThread(pm);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	fileRemoved [implements ProjectListener]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* Method called when a single file has been removed from a project.
* If the file was a Java file, discards it and rebuilds the model.
*/
public void		fileRemoved(ProjectEvent inEvt)
{
String	path = inEvt.getRemovedFile().getFile().getAbsolutePath();

if(m_Files.remove(path) != null)	// remove reference to file, using path as key
	{
	m_Finder.removeSource(path);	// remove the related source from the Finder
	buildModel();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	filesRemoved [implements ProjectListener]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* Method called when more than one file have been removed from a project.
* If there were Java files among them, discards them and rebuilds the model.
*/
public void		filesRemoved(ProjectEvent inEvt)
{
ArrayList	vptfList		= inEvt.getRemovedFiles();
boolean		needsRebuilding	= false;

for(Iterator it = vptfList.iterator(); it.hasNext();)
	{
	String	path = ((VPTFile)it.next()).getFile().getAbsolutePath();

	if(m_Files.remove(path) != null)	// remove reference to file, using path as key
		{
		m_Finder.removeSource(path);	// remove the related source from the Finder
		needsRebuilding	= true;
		}
	}

if(needsRebuilding)
	buildModel();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	propertiesChanged [implements ProjectListener]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* Method called when project properties (such as name and root) have changed.
*/
public void		propertiesChanged(ProjectEvent inEvt)
{
ModelManager.getInstance().modelRenamed(this);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	bufferSaved
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Called by <code>ModelManager</code> when a buffer is saved in jEdit.
 * Checks if the path refers to a ModelFile the files table.
 * If this is the case, it parses the reference file and rebuilds the model.
 * Multiple events generated by a possible "Save All" command are gathered
 * togheter to rebuild the model only once.
 *
 * @param inBuffer	The buffer in question
 */
public void		bufferSaved(Buffer inBuffer)
{
int		rebuildOption = getProperties().getModelStatus().getRebuildOption();

if(rebuildOption == PMModelStatus.REBUILD_WHEN_SAVED)
	{
	File	file = (File)m_Files.get(inBuffer.getPath());

	if(file != null)
		{
		if(m_PendingParsingMgr == null)
			{
			m_PendingParsingMgr = new ParsingManager();
			VFSManager.runInAWTThread(new SavedBuffersParseTrigger());
			}

		m_PendingParsingMgr.addFile(file, true);
		}
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	bufferChanged
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Called by <code>ModelManager</code> when a buffer changes in jEdit.
 * Checks if the path refers to a ModelFile the files table.
 * If this is the case, it parses the reference file and rebuilds the model.
 *
 * @param inBuffer	The buffer in question
 * @since 0.3
 */
public void		bufferChanged(Buffer inBuffer)
{
int		rebuildOption	= getProperties().getModelStatus().getRebuildOption();
File	file			= (File)m_Files.get(inBuffer.getPath());

if(file != null)
	{
	if(m_PendingParsingMgr == null)
		m_PendingParsingMgr = new ParsingManager();

	m_PendingParsingMgr.addBuffer(inBuffer, rebuildOption == PMModelStatus.REBUILD_ON_CHANGES);

	if(m_ParseOnKeystrokeTimer.isRunning())
		m_ParseOnKeystrokeTimer.stop();

	m_ParseOnKeystrokeTimer.setInitialDelay(JExplorerPlugin.getParseOnKeystrokeDelay());
	m_ParseOnKeystrokeTimer.start();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	buildModel
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	buildModel()
{
// build the model only if there is no ParsingManager at work
if(getProperties().getModelStatus().isParsing())
	return;

Iterator	it;

m_ElementsMap.clear();
m_Selection.clearHistory();
m_Finder.build();				// build the global finder

// Pass 1: build elements
// after this pass m_ElementsMap will contain an MElement object for each
// JElement in the Finder; we discard nested elements since they are
// created by the containing elements - see buildElement()
for(it = m_Finder.getElementsIterator(); it.hasNext();)
	{
	JElement	je = (JElement)it.next();

	if(je.getContainer() == null)
		addElement(buildElement(je));
	}

// Pass 2: resolve links
// after this pass all superclasses and subclasses, interfaces and implementors,
// superinterfaces and subinterfaces will be appropriately linked
for(it = m_Finder.getElementsIterator(); it.hasNext();)
	linkElement((JElement)it.next());

gBuildWorkSet();				// build the working elements subset
computeElementsAttributes();	// compute element attributes
computeElementsMembers();		// compute elements members

// tell the registered dockables to update
fireModelEvent(true);

m_Selection.restore();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	buildElement
------------------------------------------------------------------------------------------------------------------------------------*/
protected MElement		buildElement(JElement inJE)
{
MElement		e;

if(inJE instanceof JInterface)
	e = new MInterface(
				this,
				modifiersToAccess	(inJE.getModifiers()),	// access specifier
				Modifier.isFinal	(inJE.getModifiers()),	// final flag
				isDeprecated(inJE),							// deprecated flag
				inJE.getQualifiedName(),					// qualified name
				inJE.getComment(),							// comment
				inJE.getSourcePos());						// reference to source
else
	e = new MClass(
				this,
				modifiersToAccess	(inJE.getModifiers()),	// access specifier
				Modifier.isAbstract	(inJE.getModifiers()),	// abstract flag
				Modifier.isFinal	(inJE.getModifiers()),	// final flag
				Modifier.isStatic	(inJE.getModifiers()),	// static flag
				isDeprecated(inJE),							// deprecated flag
				inJE.getQualifiedName(),					// qualified name
				inJE.getComment(),							// comment
				inJE.getSourcePos());						// reference to source

// read nested elements
// we could avoid discarding nested elements in buildModel(), do nothing here and
// finally simply link outer and inner elements later in linkElement(), but this
// would result in having fields, constructors and methods appearing before nested
// elements in the details panel

for(Iterator eIt = inJE.getElementsIterator();
	eIt != null && eIt.hasNext();)
	{
	JElement	je = (JElement)eIt.next();
	MElement		ne = buildElement(je);

	addElement(ne);
	e.addMember(ne);
	e.addInnerclass(ne);
	ne.setOuterclass(e);
	}

// read fields

for(Iterator fIt = inJE.getFieldsIterator();
	fIt != null && fIt.hasNext();)
	{
	JField	jf = (JField)fIt.next();

	e.addMember(
		new MField(
			e,												// container element
			jf.getLocalName(),								// name
			modifiersToAccess	(jf.getModifiers()),		// access specifier
			new MType(jf.getType().getNameAndDimension()),	// type and dimension
			Modifier.isFinal	(jf.getModifiers()),		// final flag
			Modifier.isStatic	(jf.getModifiers()),		// static flag
			Modifier.isTransient(jf.getModifiers()),		// transient flag
			Modifier.isVolatile	(jf.getModifiers()),		// volatile flag
			isDeprecated(jf),								// deprecated flag
			jf.getComment(),								// comment
			jf.getSourcePos()));							// reference to source
	}

// read constructors

for(Iterator cIt = inJE.getConstructorsIterator();
	cIt != null && cIt.hasNext();)
	{
	JConstructor	jc = (JConstructor)cIt.next();

	e.addMember(
		new MConstructor(
			e,										// container element
			jc.getLocalName(),						// name
			modifiersToAccess(jc.getModifiers()),	// access specifier
			jc.getParameters(),						// parameters
			jc.getThrowables(),						// exceptions
			isDeprecated(jc),						// deprecated flag
			jc.getComment(),						// comment
			jc.getSourcePos()));					// reference to source
	}

// read methods

for(Iterator mIt = inJE.getMethodsIterator();
	mIt != null && mIt.hasNext();)
	{
	JMethod		jm = (JMethod)mIt.next();

	e.addMember(
		new MMethod(
			e,														// container element
			jm.getLocalName(),										// name
			modifiersToAccess		(jm.getModifiers()),			// access specifier
			jm.getParameters(),										// parameters
			jm.getThrowables(),										// exceptions
			new MType(jm.getReturnType().getNameAndDimension()),	// return type
			Modifier.isAbstract		(jm.getModifiers()),			// abstract flag
			Modifier.isFinal		(jm.getModifiers()),			// final flag
			Modifier.isStatic		(jm.getModifiers()),			// static flag
			Modifier.isNative		(jm.getModifiers()),			// native flag
			Modifier.isSynchronized	(jm.getModifiers()),			// synchronized flag
			false,													// overriding flag
			isDeprecated(jm),										// deprecated flag
			jm.getComment(),										// comment
			jm.getSourcePos()));									// reference to source
	}

return(e);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	modifiersToAccess
------------------------------------------------------------------------------------------------------------------------------------*/
protected byte		modifiersToAccess(int inModifiers)
{
	 if(Modifier.isPublic(inModifiers))		return(ACCESS_PUBLIC);
else if(Modifier.isProtected(inModifiers))	return(ACCESS_PROTECTED);
else if(Modifier.isPrivate(inModifiers))	return(ACCESS_PRIVATE);
else										return(ACCESS_PACKAGE);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isDeprecated
------------------------------------------------------------------------------------------------------------------------------------*/
protected boolean	isDeprecated(JMember inJM)
{
Comment		comment = inJM.getComment();

if(	comment != null &&
	comment.getText().indexOf("@deprecated") != -1)
		return(true);

return(false);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	linkElement
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	linkElement(JElement inJE)
{
MElement		e = getElement(inJE.getQualifiedName());

if(e == null)
	{
	Log.log(Log.ERROR, JExplorerPlugin.class,
		"linkElement(): cannot find MElement " + inJE.getQualifiedName());
	return;
	}

// link superclasses and subclasses

if(inJE instanceof JClass)
	{
	MClass	superclass = (MClass)getElement(((JClass)inJE).getSuperClass().getQualifiedName());

	if(superclass != null)
		{
		((MClass)e).setSuperclass(superclass);
		superclass.addSubclass((MClass)e);
		}
	}

// link interfaces and implementors/subinterfaces

if(inJE instanceof JClass)
	{
	JType	interfaces[] = ((JClass)inJE).getInterfaces();

	for(int i = 0; i < interfaces.length; i++)
		{
		MInterface	interf = (MInterface)getElement(interfaces[i].getQualifiedName());

		if(interf != null)
			{
			((MClass)e).addInterface(interf);
			interf.addImplementor(e);
			}
		}
	}
else if(inJE instanceof JInterface)
	{
	JType	interfaces[] = ((JInterface)inJE).getSuperInterfaces();

	for(int i = 0; i < interfaces.length; i++)
		{
		MInterface	interf = (MInterface)getElement(interfaces[i].getQualifiedName());

		if(interf != null)
			{
			((MInterface)e).addSuperinterface(interf);
			interf.addSubinterface((MInterface)e);
			interf.addImplementor(e);
			}
		}
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	librariesChanged
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	librariesChanged()
{
m_Finder.removeLibraries();

for(Iterator it = m_Properties.getLibraries().getIterator(); it.hasNext();)
	{
	String	libPath = (String)it.next();

	try	{
		m_Finder.addLibrary(libPath);
		}
	catch(IOException ioe)
		{
		Log.log(Log.ERROR, JExplorerPlugin.class,
				ioe.getClass().getName() + " while adding library: " + libPath +
				" , reason: " + ioe.getMessage());
		}
	}

buildModel();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeElementsAttributes
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	computeElementsAttributes()
{
for(Iterator it = getElementsIterator(); it.hasNext();)
	((MElement)it.next()).computeAttributes();

gComputeGraph();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	computeElementsMembers
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	computeElementsMembers()
{
for(Iterator it = getElementsIterator(); it.hasNext();)
	{
	MElement	e = (MElement)it.next();

	if(!e.isNestedElement())
		e.computeMembers();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	gBuildWorkSet
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	gBuildWorkSet()
{
synchronized(m_WorkSet)
	{
	MElement	selected = m_Selection.getElement();

	m_WorkSet.clear();

	if(	m_Properties.getOption(PG_FILTER_SELECTION) &&
		selected != null)
		{
		// must obviously include the selected element
		m_WorkSet.add(selected);

		switch(m_Properties.getChoice(PG_CURRENT_RELATIVES))
			{
			case RELATIVES_SUPER:
				selected.buildAncestorsTable(m_WorkSet);
				break;

			case RELATIVES_SUB:
				selected.buildDescendantsTable(m_WorkSet);
				break;

			case RELATIVES_ALL:
				selected.buildAncestorsTable(m_WorkSet);
				selected.buildDescendantsTable(m_WorkSet);
				break;
			}
		}
	else
		{
		m_WorkSet.addAll(m_ElementsMap.values());
		}
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	gComputeGraph
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	gComputeGraph()
{
gClearLocationsAndSpans();			// reset elements horizontal location and vertical span

// define the initial column

int			startLocation	= 0;
MElement	selected		= getSelectionModel().getElement();

if(	getProperties().getOption(PG_FILTER_SELECTION) &&
	selected != null &&
	getProperties().getChoice(PG_CURRENT_RELATIVES) == RELATIVES_SUB)
		startLocation = selected.computeLocationUpward(
			getProperties().getChoice(PG_CURRENT_LAYOUT),
			0);

gComputeLocations(startLocation);	// compute elements horizontal location
gComputeVSpans(startLocation);		// compute vertical spans
gInsertElements(startLocation);		// insert the elements into the columns
}
/*------------------------------------------------------------------------------------------------------------------------------------
	gClearLocationsAndSpans
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	gClearLocationsAndSpans()
{
for(Iterator it = getElementsIterator(); it.hasNext();)
	{
	MElement	e = (MElement)it.next();

	e.clearInserted();
	e.setHLocation(LOCATION_NULL);
	e.clearSubVSpan();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	gComputeLocations
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	gComputeLocations(int inStartLocation)
{
Iterator	iter;
int			layout = m_Properties.getChoice(PG_CURRENT_LAYOUT);

// first pass: discover elements with location == inStartLocation

iter = m_WorkSet.iterator();
while(iter.hasNext())
	{
	MElement	elem	= (MElement)iter.next();
	int			level	= elem.computeLocationUpward(layout, 0);

	if(level == inStartLocation)
		elem.setHLocation(level);
	}

// second pass: compute actual locations

iter = m_WorkSet.iterator();
while(iter.hasNext())
	{
	MElement	elem = (MElement)iter.next();

	// recursively compute location for the entire element's inheritance tree
	if(elem.getHLocation() == inStartLocation)
		{
		elem.setHLocation(LOCATION_NULL);
		elem.computeLocation(inStartLocation);
		}
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	gComputeVSpans
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	gComputeVSpans(int inStartLocation)
{
Iterator	iter = m_WorkSet.iterator();

while(iter.hasNext())
	{
	MElement	elem = (MElement)iter.next();

	// recursively compute span for the entire element's inheritance tree
	if(elem.getHLocation() == inStartLocation)
		elem.computeSubVSpan();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	gInsertElements
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	gInsertElements(int inStartLocation)
{
Iterator	iter = m_WorkSet.iterator();
int			yPos = 0;

while(iter.hasNext())
	{
	MElement	elem = (MElement)iter.next();

	// recursively insert the entire element's inheritance tree
	if(elem.getHLocation() == inStartLocation)
		{
		elem.insert(0, yPos);
		yPos += elem.getMaxVSpan();
		}
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	selectObjectAtCursor
------------------------------------------------------------------------------------------------------------------------------------*/
public void	selectObjectAtCursor(	int		inCaret,
									Buffer	inBuffer,
									boolean	inSourcePositioningRequested)
{
for(Iterator it = getElementsIterator(); it.hasNext();)
	{
	MElement	e = (MElement)it.next();
	Object		obj = e.getObjectAtCaret(inCaret, inBuffer);

	if(obj != null)
		getSelectionModel().select(obj, inSourcePositioningRequested);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	selectionChanged [implements SelectionListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void	selectionChanged(SelectionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "selection changed, source: " + inEvt.getSource());

if(inEvt.isElementSelection())
	{
	gBuildWorkSet();	// build the selection-filtered subset
	gComputeGraph();	// rebuild element attributes
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	updateModel
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Updates the model as needed.
 * For each JSource marked as "not parsed", first a corresponding jEdit
 * Buffer is searched. If this is not found, the real file is used
 */
public void	updateModel()
{
ParsingManager	pm = null;

for(Iterator it = m_Finder.getSourcesIterator(); it.hasNext();)
	{
	JSource		source = (JSource)it.next();

	if(!source.isParsed())
		{
		if(pm == null)
			pm = new ParsingManager();

		String	path	= source.getFile().getAbsolutePath();
		Buffer	buffer	= jEdit.getBuffer(path);

		if(buffer != null)
			pm.addBuffer(buffer, true);
		else
			pm.addFile(source.getFile(), true);
		}
	}

if(pm != null)
	VFSManager.runInWorkThread(pm);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	LibrariesUpdater [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class LibrariesUpdater implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

librariesChanged();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	ElementsUpdater [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class ElementsUpdater implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

computeElementsAttributes();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	MembersBuilder [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class MembersBuilder implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

computeElementsMembers();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	WorkSetBuilder [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class WorkSetBuilder implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

gBuildWorkSet();
gComputeGraph();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	SavedBuffersParseTrigger [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
/** 
 * Manages model rebuilding in a deferred way to respond once to multiple
 * BufferUpdate events.
 * 
 * @author	Amedeo Farello
 * @version 0.3, 2003.05.25
 * @since	0.2
 */
private class SavedBuffersParseTrigger implements Runnable
{
/*------------------------------------------------------------------------------------------------------------------------------------
	run
------------------------------------------------------------------------------------------------------------------------------------*/
public void		run()
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "running...");

if(m_PendingParsingMgr != null)
	{
	ParsingManager	pm = m_PendingParsingMgr;

	m_PendingParsingMgr = null;
	VFSManager.runInWorkThread(pm);
	}
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	ParsingManager [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
/** 
 * Parses a group of files and/or buffers running in a thread.
 * It is used in the following cases:
 *  - At initial model creation
 *  - At project re-import
 *  - When a Buffer is saved
 *  - When a Buffer changes
 * 
 * @author	Amedeo Farello
 * @version 0.3, 2003.05.25
 */
private class ParsingManager implements Runnable
{
HashMap		m_SourcesToParse = null;
/*------------------------------------------------------------------------------------------------------------------------------------
	addFile
------------------------------------------------------------------------------------------------------------------------------------*/
public void		addFile(File inFile, boolean inNeedParsing)
{
if(m_SourcesToParse == null)
	m_SourcesToParse = new HashMap();

String	path = inFile.getAbsolutePath();

m_SourcesToParse.put(path, new PMSource(inFile, path, inNeedParsing));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addBuffer
------------------------------------------------------------------------------------------------------------------------------------*/
public void		addBuffer(Buffer inBuffer, boolean inNeedParsing)
{
if(m_SourcesToParse == null)
	m_SourcesToParse = new HashMap();

String	path = inBuffer.getPath();

m_SourcesToParse.put(path, new PMSource(inBuffer, path, inNeedParsing));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addFiles
------------------------------------------------------------------------------------------------------------------------------------*/
public void		addFiles(Collection inFiles, boolean inNeedParsing)
{
for(Iterator it = inFiles.iterator(); it.hasNext();)
	addFile((File)it.next(), inNeedParsing);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	run
------------------------------------------------------------------------------------------------------------------------------------*/
public void		run()
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "running...");

if(m_SourcesToParse.size() > 0)
	{
	synchronized(Model.this)
		{
		PMModelStatus	status = getProperties().getModelStatus();

		status.incrementParsers();
		fireModelEvent(false);

		for(Iterator it = m_SourcesToParse.values().iterator(); it.hasNext();)
			parseSource(status, (PMSource)it.next());

		status.decrementParsers();

		if(status.isUpToDate())
			buildModel();
		}
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	parseSource
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	parseSource(PMModelStatus inStatus, PMSource inPMS)
{
inStatus.removeFileErrors(inPMS.getPath());		// clear ErrorSource

JSource		source = new JSource((File)m_Files.get(inPMS.getPath()));

m_Finder.addSource(source);		// renew the source in the Finder

if(!inPMS.needParsing())
	{
	getProperties().getModelStatus().addUpdateError(
		ErrorSource.WARNING,
		inPMS.getPath(),
		0,
		0,
		0,
		jEdit.getProperty("jexplorer.ui.err.notParsed") +
			" (JExplorer model '" + m_Project.getName() + "')");

	return;
	}

try
	{
	JavaParser.parse(source, inPMS.getInputStream());
	}
catch(NullPointerException ioe)
	{
	reportError(
		ioe, 
		inPMS.getPath(),
		0,
		0,
		0,
		"jexplorer.ui.err.unexpected",
		true,
		null);
	}
catch(FileNotFoundException fnfe)
	{
	reportError(
		fnfe,
		inPMS.getPath(),
		0,
		0,
		0,
		"jexplorer.ui.err.unexpected",
		false,
		null);
	}
catch(TokenMgrError tme)
	{
	reportError(
		tme,
		inPMS.getPath(),
		tme.getErrorLine() - 1,
		0,
		0,
		"jexplorer.ui.err.lexical",
		false,
		null);
	}
catch(ParseException pe)
	{
	reportError(
		pe,
		inPMS.getPath(),
		pe.currentToken.next.beginLine - 1,
		pe.currentToken.next.beginColumn - 1,
		pe.currentToken.next.endColumn,
		"jexplorer.ui.err.syntax",
		false,
		null);
	}
catch(JExplorerParseException jpe)
	{
	reportError(
		jpe,
		inPMS.getPath(),
		jpe.getToken().beginLine - 1,
		jpe.getToken().beginColumn - 1,
		jpe.getToken().endColumn,
		"jexplorer.ui.err.unexpected",
		true,
		jpe.getThrowable());
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	reportError
------------------------------------------------------------------------------------------------------------------------------------*/
/** 
 * Reports an error occurred while parsing.
 * 
 * @param	inThrowable				the received <code>Throwable</code>
 * @param	inSourcePath			the <code>File</code> or <code>Buffer</code> absolute path
 * @param	inLine					the line where the error occurred
 * @param	inStartColumn			the initial column
 * @param	inEndColumn				the final column
 * @param	inErrorPropertyName		error description property name
 * @param	inWriteLog				true if logging is requested
 * @param	inStackTraceThrowable	optional <code>Throwable</code> to be stack-traced, may be <code>null</code>
 * @since	0.3
 */
protected void	reportError(Throwable	inThrowable,
							String		inSourcePath,
							int			inLine,
							int			inStartColumn,
							int			inEndColumn,
							String		inErrorPropertyName,
							boolean		inWriteLog,
							Throwable	inStackTraceThrowable)
{
getProperties().getModelStatus().addParserError(
	ErrorSource.ERROR,
	inSourcePath,
	inLine,
	inStartColumn,
	inEndColumn,
	jEdit.getProperty(inErrorPropertyName) +
		" (JExplorer model '" + m_Project.getName() + "')");

if(inWriteLog)
	Log.log(Log.ERROR, JExplorerPlugin.class,
		inThrowable.getMessage() +
		"\nwhile parsing file: " + inSourcePath);

if(inStackTraceThrowable != null)
	inStackTraceThrowable.printStackTrace();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	PMSource [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
/** 
 * Describes a "source" (a File or a Buffer) for the ParsingManager.
 * 
 * @author	Amedeo Farello
 * @version 0.3, 2003.05.31
 * @since	0.3
 */
private class PMSource
{
private Object		m_Origin;
private String		m_Path;
private boolean		m_NeedParsing;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public PMSource(Object inOrigin, String inPath, boolean inNeedParsing)
{
m_Origin		= inOrigin;
m_Path			= inPath;
m_NeedParsing	= inNeedParsing;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	needParsing
------------------------------------------------------------------------------------------------------------------------------------*/
public boolean		needParsing()
{
return(m_NeedParsing);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getPath
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the absolute path for the origin.
 *
 * @return	a String representing the path. 
 */
public String		getPath()
{
return(m_Path);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getInputStream
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Builds an <code>InputStream</code> for the origin.
 *
 * @return	the <code>InputStream</code> or <code>null</code>, if
 *			origin is invalid. 
 */
public InputStream	getInputStream()
	throws FileNotFoundException, NullPointerException
{
InputStream		is = null;

if(m_Origin instanceof File)
	{
	is = new FileInputStream((File)m_Origin);
	}
else if(m_Origin instanceof Buffer)
	{
	Buffer	buffer = (Buffer)m_Origin;
	is = new ByteArrayInputStream(buffer.getText(0, buffer.getLength()).getBytes());
	}

if(is == null)
	throw new NullPointerException("Origin must be a File or a Buffer");

return(is);
}
}
}
}
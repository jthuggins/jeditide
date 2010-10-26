/*
 * ModelManager.java
 * Copyright (c) 2003 Amedeo Farello
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

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.event.EventListenerList;

import org.gjt.sp.util.Log;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;

import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;
import projectviewer.event.ProjectViewerListener;
import projectviewer.event.ProjectViewerEvent;

import jexplorer.JExplorerPlugin;

import jexplorer.model.Model;

import jexplorer.gui.JExplorerDockable;
import jexplorer.gui.ViewConnector;

import jexplorer.gui.model.ModelManagerDialog;
/*------------------------------------------------------------------------------------------------------------------------------------
	ModelManager
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A singleton to manage <code>Model</code> objects.
 *	The basic rule is:
 *		1 Project,
 *		1 Model,
 *		1 SelectionModel,
 *		1 ModelProperties,
 *		n Dockables
 *
 * Each registered JExplorerDockable has a ModelViewConnection entry in the
 * m_MVConnections map. Each ModelViewConnection links a Model with a ViewConnector.
 * There is at most a single ViewConnector for each jEdit View.
 *
 * @author Amedeo Farello
 * @version 0.3, 2003.05.24
 */
public class ModelManager
	implements ProjectViewerListener
{
private static ModelManager	s_Instance;

static private final byte	MODEL_ADDED		= 1;
static private final byte	MODEL_REMOVED	= 2;
static private final byte	MODEL_RENAMED	= 3;

private HashMap				m_Models			= new HashMap();
private HashMap				m_ViewConnectors	= new HashMap();
private HashMap				m_MVConnections		= new HashMap();
private EventListenerList	m_ListenerList		= new EventListenerList();
private ModelManagerDialog	m_Dialog;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>ModelManager</code>.
 */
private ModelManager()
{
ProjectViewer.addProjectViewerListener(this, null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	terminate
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Terminates this <code>ModelManager</code>.
 */
public void	terminate()
{
ProjectViewer.removeProjectViewerListener(this, null);
s_Instance = null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getInstance
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the unique <code>ModelManager</code> instance.
 */
static public ModelManager	getInstance()
{
if(s_Instance == null)
	s_Instance = new ModelManager();

return(s_Instance);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getModel
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the <code>Model</code> referring to the requested <code>VPTProject</code>
 * or <code>null</code> if there is no such <code>Model</code>.
 */
public Model	getModel(VPTProject inProject)
{
return (Model)m_Models.get(inProject);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getModelsIterator
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns a <code>java.util.Iterator</code> over the models.
 */
public Iterator	getModelsIterator()
{
return m_Models.values().iterator();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	bufferSaved
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Called by a <code>ViewConnector</code> when a .java <code>Buffer</code> is saved.
 *
 * @since 0.3
 */
public void		bufferSaved(Buffer inBuffer)
{
for(Iterator it = m_Models.values().iterator(); it.hasNext();)
	((Model)it.next()).bufferSaved(inBuffer);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	bufferChanged
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Called by a <code>ViewConnector</code> when a .java <code>Buffer</code> changes.
 *
 * @since 0.3
 */
public void		bufferChanged(Buffer inBuffer)
{
for(Iterator it = m_Models.values().iterator(); it.hasNext();)
	((Model)it.next()).bufferChanged(inBuffer);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	projectLoaded [implements ProjectViewerListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		projectLoaded(ProjectViewerEvent inEvt)
{
if(m_Dialog != null)
	m_Dialog.repaint();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	projectAdded [implements ProjectViewerListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		projectAdded(ProjectViewerEvent inEvt)
{
if(m_Dialog != null)
	((DefaultListModel)m_Dialog.getListModel()).addElement(inEvt.getProject());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	projectRemoved [implements ProjectViewerListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		projectRemoved(ProjectViewerEvent inEvt)
{
if(m_Dialog != null)
	((DefaultListModel)m_Dialog.getListModel()).removeElement(inEvt.getProject());

removeModel(inEvt.getProject());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	modelRenamed
------------------------------------------------------------------------------------------------------------------------------------*/
/** 
 * Called from a Model to inform that it has been renamed.
 *
 * @param inModel the model that was renamed.
 */
public void		modelRenamed(Model inModel)
{
fireModelManagerEvent(inModel, MODEL_RENAMED);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addListener
------------------------------------------------------------------------------------------------------------------------------------*/
public void	addListener(ModelManagerListener inListener)
{
m_ListenerList.add(ModelManagerListener.class, inListener);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	removeListener
------------------------------------------------------------------------------------------------------------------------------------*/
public void	removeListener(ModelManagerListener inListener)
{
m_ListenerList.remove(ModelManagerListener.class, inListener);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	fireModelManagerEvent
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	fireModelManagerEvent(Model inModel, byte inWhat)
{
// Guaranteed to return a non-null array
Object[]	listeners = m_ListenerList.getListenerList();

ModelManagerEvent	evt = null;

// Process the listeners FIRST to LAST, notifying
// those that are interested in this event
for(int i = 0; i < listeners.length; i += 2)
	if(listeners[i] == ModelManagerListener.class)
		{
		if(evt == null)
			evt = new ModelManagerEvent(this, inModel);

		switch(inWhat)
			{
			case MODEL_ADDED:
				((ModelManagerListener)listeners[i + 1]).modelAdded(evt);
				break;

			case MODEL_REMOVED:
				((ModelManagerListener)listeners[i + 1]).modelRemoved(evt);
				break;

			case MODEL_RENAMED:
				((ModelManagerListener)listeners[i + 1]).modelRenamed(evt);
				break;
			}
		}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	addModel
------------------------------------------------------------------------------------------------------------------------------------*/
public void		addModel(VPTProject inProject)
{
// ensure that the project is loaded
inProject = ProjectManager.getInstance().getProject(inProject.getName());

Model	model = new Model(inProject);

m_Models.put(inProject, model);				// 1. update the models list
fireModelManagerEvent(model, MODEL_ADDED);	// 2. inform listeners
model.loadProject();						// 3. do initial build
inProject.addProjectListener(model);		// 4. attach the model to the project
}
/*------------------------------------------------------------------------------------------------------------------------------------
	removeModel
------------------------------------------------------------------------------------------------------------------------------------*/
public void		removeModel(VPTProject inProject)
{
Model	model = getModel(inProject);

if(model != null)
	{
	inProject.removeProjectListener(model);			// 1. detach the model from the project
	m_Models.remove(inProject);						// 2. update the models list
	fireModelManagerEvent(model, MODEL_REMOVED);	// 3. inform listeners
	model.terminate();								// 4. terminate model
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getViewConnector
------------------------------------------------------------------------------------------------------------------------------------*/
private ViewConnector	getViewConnector(View inView)
{
ViewConnector	viewConn = (ViewConnector)m_ViewConnectors.get(inView);

if(viewConn == null)
	{
	viewConn = new ViewConnector(inView);
	m_ViewConnectors.put(inView, viewConn);
	}

return(viewConn);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	releaseViewConnector
------------------------------------------------------------------------------------------------------------------------------------*/
private void	releaseViewConnector(View inView)
{
ViewConnector	viewConn = (ViewConnector)m_ViewConnectors.get(inView);

if(viewConn == null)
	{
	Log.log(Log.ERROR, JExplorerPlugin.class, "Trying to release an unexisting ViewConnector");
	return;
	}

// before removing a ViewConnector, check if there is some other
// ModelViewConnection using it
for(Iterator it = m_MVConnections.values().iterator(); it.hasNext();)
	{
	ModelViewConnection		mvc = (ModelViewConnection)it.next();

	if(mvc.getViewConnector() == viewConn)
		return;
	}

// if we are here, we can safely release the ViewConnector
viewConn.terminate();
m_ViewConnectors.remove(inView);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	registerDockable
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Establishes a link between a dockable window and a model.
 *
 * @param inDockable	The dockable window to register.
 * @param inView		The jEdit View containing the dockable window.
 * @param inModel		The model to connect to the dockable window.
 */
public void	registerDockable(JExplorerDockable inDockable, View inView, Model inModel)
{
ViewConnector			vConn	= getViewConnector(inView);
ModelViewConnection		mvc		= new ModelViewConnection(inModel, vConn);

mvc.link();
m_MVConnections.put(inDockable, mvc);
inModel.addListener(inDockable);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	unregisterDockable
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Breaks the link between a dockable window and a model.
 *
 * @param inDockable	The dockable window to unregister.
 * @param inView		The jEdit View containing the dockable window.
 * @param inModel		The model to disconnect from the dockable window.
 */
public void	unregisterDockable(JExplorerDockable inDockable)
{
ModelViewConnection		mvc = (ModelViewConnection)m_MVConnections.get(inDockable);

if(mvc == null)
	{
	Log.log(Log.ERROR, JExplorerPlugin.class, "Trying to unregister an unregistered dockable");
	return;
	}

m_MVConnections.remove(inDockable);
mvc.unlink();
releaseViewConnector(mvc.getViewConnector().getView());
mvc.getModel().removeListener(inDockable);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	selectObjectAtCursor
------------------------------------------------------------------------------------------------------------------------------------*/
public void		selectObjectAtCursor(View inView, boolean inSourcePositioningRequested)
{
int		caret	= inView.getTextArea().getCaretPosition();
String	path	= inView.getBuffer().getPath();

for(Iterator it = m_Models.values().iterator(); it.hasNext();)
	{
	Model	model = (Model)it.next();

	if(model.hasFile(path))
		model.selectObjectAtCursor(caret, inView.getBuffer(), inSourcePositioningRequested);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	openModelManagerDialog
------------------------------------------------------------------------------------------------------------------------------------*/
public void		openModelManagerDialog()
{
if(m_Dialog == null)
	{
	m_Dialog = new ModelManagerDialog(this);
	m_Dialog.show();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	modelsDialogCanceled
------------------------------------------------------------------------------------------------------------------------------------*/
public void		modelsDialogCanceled()
{
m_Dialog = null;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	ModelViewConnection [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Mantains a Model-View (through a ViewConnector) connection.
 * It is used to allow proper detachment of Models and ViewConnectors when
 * a <code>JExplorerDockable</code> is unregistered or a <code>Model</code>
 * or a <code>View</code> is deleted.
 */
private class ModelViewConnection
{
private Model			m_Model;
private ViewConnector	m_ViewConnector;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Create a new <code>ModelViewConnection</code>.
 *
 * @param inModel			The <code>Model</code> to record.
 * @param inViewConnector	The <code>ViewConnector</code> to record.
 */
public ModelViewConnection(Model inModel, ViewConnector inViewConnector)
{
m_Model			= inModel;
m_ViewConnector	= inViewConnector;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getModel
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the recorded <code>Model</code>.
 */
public Model	getModel()
{
return m_Model;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getViewConnector
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Returns the recorded <code>ViewConnector</code>.
 */
public ViewConnector	getViewConnector()
{
return m_ViewConnector;
}
/*------------------------------------------------------------------------------------------------------------------------------------
	link
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Makes the ViewConnector a listener to the model selection.
 */
public void		link()
{
m_Model.getSelectionModel().addSelectionListener(m_ViewConnector);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	unlink
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Detaches the ViewConnector from the model selection.
 */
public void		unlink()
{
m_Model.getSelectionModel().removeSelectionListener(m_ViewConnector);
}
}
}

/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package projectviewer.importer;

//{{{ Imports
import java.io.IOException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import java.awt.Component;

import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.util.Log;

import projectviewer.ProjectViewer;
import projectviewer.VFSHelper;
import projectviewer.gui.ImportDialog;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTDirectory;
//}}}

/**
 *	Imports files and/or directories from the project root. Optionally, can
 *	remove all existing files under the root before doing a fresh import.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class RootImporter extends FileImporter {

	//{{{ Private members
	private boolean clean;
	protected Component parent;
	protected String oldRoot;
	protected boolean addHidden;
	//}}}

	//{{{ +RootImporter(VPTNode, String, ProjectViewer, Component) : <init>
	/**
	 *	Creates an Importer that uses a component other than the ProjectViewer
	 *	as the parent of the dialogs shown to the user. If "oldRoot" is not null,
	 *	files under this directory will be removed from the root node of the
	 *	project.
	 */
	public RootImporter(VPTNode node, String oldRoot, ProjectViewer viewer, Component parent) {
		super(node, viewer);
		if (parent != null) {
			this.parent = parent;
		} else if (viewer != null) {
			this.parent = viewer;
		} else {
			this.parent = jEdit.getActiveView();
		}
		clean = (oldRoot != null);
		this.oldRoot = oldRoot;
	} //}}}

	//{{{ +RootImporter(VPTNode, ProjectViewer, boolean) : <init>
	/**
	 *	Imports files from the root of the project. If "clean" is "true", the
	 *	existing nodes that are below the root of the project will be removed
	 *	before the importing.
	 */
	public RootImporter(VPTNode node, ProjectViewer viewer, boolean clean) {
		this(node, null, viewer, null);
		this.clean = clean;
	} //}}}


	/** Asks if the user wants to import files from the chosen project root. */
	protected void internalDoImport()
	{
		boolean flatten = false;
		if (fnf == null) {
			String dlgTitle = (project.getChildCount() == 0)
							? "projectviewer.import.msg_proj_root.title"
							: "projectviewer.import.msg_reimport.title";
			ImportDialog id = showImportDialog(dlgTitle, FILTER_CONF_PROJECT);
			if (!id.isApproved()) {
				return;
			}

			fnf = id.getImportFilter();
			flatten = id.getFlattenFilePaths();
			addHidden = id.getAddHiddenFiles();
			saveImportFilterStatus(project, id, FILTER_CONF_PROJECT);
		}

		String state = null;
		if (viewer != null) {
			state = viewer.getTreePanel().getFolderTreeState(project);
		}

		if (clean) {
			if (oldRoot == null) {
				oldRoot = project.getRootPath();
			}
			Enumeration e = project.children();
			List<VPTNode> toRemove = new ArrayList<VPTNode>();
			while (e.hasMoreElements()) {
				VPTNode n = (VPTNode) e.nextElement();
				// need to handle "virtual directories", which mess up the
				// value of getNodePath() when the path doesn't exist anymore.
				if (n.getNodePath().startsWith(oldRoot)
					|| (n.isDirectory()
						&& ((VPTDirectory)n).getURL().startsWith(oldRoot)))
				{
					toRemove.add(n);
				}
			}
			if (toRemove.size() > 0) {
				for (VPTNode n : toRemove) {
					if (n.isDirectory()) {
						unregisterFiles((VPTDirectory)n);
						removeDirectory((VPTDirectory)n);
					} else if (n.isFile()) {
						removeFile((VPTFile)n);
					}
				}
			}
		}

		try {
			importFiles(project, fnf, true, flatten, addHidden);
		} catch (IOException ioe) {
			Log.log(Log.ERROR, this, "VFS exception while importing", ioe);
		}

		postAction = new NodeStructureChange(project, state);
	}

	//{{{ #unregisterFiles(VPTDirectory) : void
	/** Unregisters all files in the directory from the project, recursively. */
	protected void unregisterFiles(VPTDirectory dir) {
		for (int i = 0; i < dir.getChildCount(); i++) {
			VPTNode n = (VPTNode) dir.getChildAt(i);
			if (n.isDirectory()) {
				unregisterFiles((VPTDirectory)n);
				removeDirectory((VPTDirectory)n);
			} else if (n.isFile()) {
				removeFile((VPTFile)n);
			}
		}
	} //}}}

	//{{{ #getImportDialog() : ImportDialog
	protected ImportDialog getImportDialog() {
		ImportDialog dlg = super.getImportDialog();
		dlg.hideFileChooser();
		dlg.lockTraverse();
		dlg.hideNewNode();
		return dlg;
	} //}}}

}


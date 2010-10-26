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
package projectviewer.vpt;

//{{{ Imports
import java.io.IOException;
import java.lang.ref.WeakReference;

import java.awt.Color;
import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.util.Log;

import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;

import common.threads.WorkerThreadPool;

import projectviewer.PVActions;
import projectviewer.ProjectViewer;
import projectviewer.VFSHelper;
import projectviewer.config.AppLauncher;
import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *	Models a file that is part of a project.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTFile extends VPTNode
{

	//{{{ Constants

	private final static Icon fileClosedIcon 	= GUIUtilities.loadIcon("File.png");
	private final static Icon fileOpenedIcon 	= GUIUtilities.loadIcon("OpenFile.png");
	private static Icon noFileIcon				= null;

	private static final AppLauncher appList  = AppLauncher.getInstance();
	private static FileSystemView fsView;

	//}}}

	//{{{ Attributes

	private String	url;
	private Color	fileTypeColor;

	private Icon	fileIcon;
	private boolean exists;
	private boolean	loadedIcon;
	private boolean openLater;
	private boolean retrieving;
	private VFSFile vfsfile;

	private String tsCache;
	//}}}

	public VPTFile(String url)
	{
		super(VFSManager.getVFSForPath(url).getFileName(url), false);
		this.url = url;
		this.fileTypeColor = VFS.getDefaultColorFor(getName());
		this.fileIcon = null;
		this.loadedIcon = false;
		this.vfsfile = null;
		this.exists = true;
	}


	/** Returns is the underlying file is writable. */
	public boolean canWrite()
	{
		VFSFile f = getFile();
		return (f != null && f.isWriteable());
	}


	/**
	 * Deletes the file from the VFS. Before deleting, try to close
	 * the file.
	 *
	 * @return Whether the file was successfully deleted.
	 */
	public boolean delete()
	{
		close();
		try {
			VFSHelper.deleteFile(url);
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}


	/**
	 * Return the VFS file associated with this node. May return null
	 * if an I/O error occurs.
	 */
	public VFSFile getFile()
	{
		return getFile(false);
	}


	/**
	 * Returns the VFS file associated with this node. Optionally,
	 * does lazy initialization of the internal cached file node.
	 *
	 * The "lazy" flag is for use by code that cannot cope with the
	 * possibility of instantiating the VFS file causing any interaction
	 * with the UI (e.g., an FTP login dialog).
	 *
	 * For VFS instances that have the LOW_LATENCY_CAP capability, the
	 * file is always retrieved from the VFS so that updates to the
	 * filesystem are reflected in the project tree.
	 *
	 * @param	lazy	Whether to use lazy initialization of the file.
	 *
	 * @return The VFSFile backing this node. May be null if doing
	 *         lazy initialization, the file doesn't exist, or an I/O
	 *         error occurs.
	 *
	 * @since PV 3.0.0
	 */
	protected VFSFile getFile(boolean lazy)
	{
		if (vfsfile != null) {
			VFS vfs = VFSManager.getVFSForPath(url);
			assert vfs != null : "Unloaded VFS?";

			int caps = vfs.getCapabilities();
			if ((caps & VFS.LOW_LATENCY_CAP) != VFS.LOW_LATENCY_CAP) {
				return vfsfile;
			}
		}
		if (lazy) {
			FileGetter.queue(this);
		} else {
			try {
				vfsfile = VFSHelper.getFile(url);
				exists = vfsfile != null;
			} catch (IOException ioe) {
				Log.log(Log.WARNING, this, ioe);
			}
		}
		return (vfsfile != null || !exists) ? vfsfile : new DummyVFSFile();
	}


	/**
	 *	Returns "true" if the node is a file and is currently opened
	 *	in jEdit.
	 */
	public boolean isOpened()
	{
		return (jEdit.getBuffer(url) != null);
	}


	/**
	 *	Returns the VFS URL for this file.
	 *
	 *	@since PV 3.0.0
	 */
	public String getURL()
	{
		return url;
	}


	/**
	 *	Changes this file's URL.
	 *
	 *	@since PV 3.0.0
	 */
	public void setURL(String url)
	{
		this.url = url;
		this.tsCache = null;
		setName(VFSManager.getVFSForPath(url).getFileName(url));
	}


	/**
	 *	Returns the icon to be shown on the tree next to the node name.
	 *
	 *	@param	expanded	If the node is currently expanded or not.
	 */
	public Icon getIcon(boolean expanded)
	{
		Icon baseIcon = fileClosedIcon;
		if (isOpened()) {
			baseIcon = fileOpenedIcon;
		} else {
			if (VFSManager.getVFSForPath(url) == VFSManager.getFileVFS() &&
				ProjectViewerConfig.getInstance().getUseSystemIcons()) {
				File file = new File(url);
				if (!loadedIcon) {
					if (file.exists()) {
						if (fsView == null) {
							fsView = FileSystemView.getFileSystemView();
						}
						fileIcon = fsView.getSystemIcon(file);
						loadedIcon = true;
						baseIcon = fileIcon;
					}
				} else {
					baseIcon = (fileIcon != null) ? fileIcon : fileClosedIcon;
				}
			} else {
				baseIcon = fileClosedIcon;
			}
		}
		return baseIcon;
	}


	/**
	 *	Returns the node's foreground color.
	 *
	 *	@param	sel		If the node is currently selected.
	 */
	public Color getForegroundColor(boolean sel) {
		if (sel) return super.getForegroundColor(sel);
		if (fileTypeColor == null) {
			return super.getForegroundColor(sel);
		} else {
			return fileTypeColor;
		}
	}


	/** Returns a string representation of the current node. */
	public String toString()
	{
		if (tsCache == null) {
			tsCache = "File [" + url + "]";
		}
		return tsCache;
	}


	/**	File nodes can be opened, so return true. */
	public boolean canOpen()
	{
		return true;
	}


	/**
	 *	Returns whether the VFS of the file allows renaming.
	 *
	 *	@since PV 3.0.0
	 */
	public boolean canRename()
	{
		int caps = VFSManager.getVFSForPath(url).getCapabilities();
		return (caps & VFS.RENAME_CAP) != 0;
	}


	/**
	 *	Opens a new buffer in jEdit with the file pointed by this node. The
	 *	buffer is loaded in the currently active view.
	 */
	public void open() {
		openLater = true;
		if (getFile(true) != null) {
			openLater = false;
			if (ProjectViewerConfig.getInstance().getUseExternalApps()
					&& appList.getAppName(url) != null) {
				appList.launchApp(url, jEdit.getActiveView());
			} else {
				jEdit.openFile(jEdit.getActiveView(), getNodePath());
				PVActions.requestFocus(jEdit.getActiveView().getTextArea());
			}
		}
	}


	/** "Closes" the jEdit buffer that contains the file. */
	public void close()
	{
		Buffer b = jEdit.getBuffer(url);
		if (b != null) {
			jEdit.closeBuffer(jEdit.getActiveView(), b);
		}
	}


	/**	Returns the path to the file represented by this node. */
	public String getNodePath()
	{
		return url;
	}


	/** Files are at the same level of every leaf node. */
	public int compareTo(VPTNode node)
	{
		if (!node.getAllowsChildren()) {
			return compareName(node);
		} else {
			return 1;
		}
	}


	/** Flush the internal VFSFile instance, and force retrieval of a new one. */
	protected void changed()
	{
		if (!retrieving) {
			this.vfsfile = null;
			getFile(true);
		}
	}


	/**
	 * A simple "task queue" for initializing VFSFile instances.
	 * Initialization will be done in the AWT thread (since starting
	 * VFS sessions may require UI interaction).
	 */
	private static class FileGetter implements Runnable
	{
		private static Queue<VPTFile> queue;

		private static final int QUEUED = 0;
		private static final int FETCHING = 1;
		private static final int UPDATING = 3;
		private static final int DONE = 4;

		private int state = QUEUED;
		private Map<VPTFile, VFSFile> updates;

		public static void queue(VPTFile f)
		{
			if (queue == null) {
				queue = new ConcurrentLinkedQueue<VPTFile>();
			}
			queue.offer(f);
			if (!queue.isEmpty()) {
				SwingUtilities.invokeLater(new FileGetter());
			}
		}


		public void run()
		{
			/*
			 * The code uses the AWT thread to define a "batch" of
			 * requests. The JTree code will try to paint a bunch of
			 * different cells, triggering calls to getFile(), which
			 * will populate the queue. Once the AWT thread is idle,
			 * this task will execute, and just re-schedule itself to
			 * run in an I/O thread to do the hard work. Once all files
			 * are retrieved, the task re-schedules itself in the AWT
			 * thread to update the nodes in the tree.
			 */
			switch (state) {
			case QUEUED:
				state = FETCHING;
				WorkerThreadPool.getSharedInstance().addRequest(this);
				break;

			case FETCHING:
				fetch();
				state = UPDATING;
				SwingUtilities.invokeLater(this);
				break;

			case UPDATING:
				update();
				state = DONE;
				break;

			default:
				assert false : "should not reach this";
			}
		}


		private void fetch()
		{
			updates = new HashMap<VPTFile, VFSFile>();
			while (!queue.isEmpty()) {
				VPTFile f = queue.poll();
				if (f == null) {
					break;
				}
				try {
					VFSFile newfile = VFSHelper.getFile(f.url);
					f.retrieving = true;
					if (needRefresh(f, newfile)) {
						updates.put(f, newfile);
					}
				} catch (IOException ioe) {
					Log.log(Log.WARNING, this, ioe);
				}
			}
		}


		private void update()
		{
			for (VPTFile f : updates.keySet()) {
				VFSFile newfile = updates.get(f);
				f.vfsfile = newfile;
				f.exists = newfile != null;
				ProjectViewer.nodeChanged(f);
				if (f.openLater && newfile != null) {
					f.open();
				}
				f.retrieving = false;
				f.openLater = false;
			}
		}


		private boolean needRefresh(VPTFile f,
									VFSFile newfile)
		{
			if (newfile == null || f.vfsfile == null) {
				return f.exists || (newfile != f.vfsfile);
			}

			return (f.vfsfile.isReadable() != newfile.isReadable() ||
					f.vfsfile.isWriteable() != newfile.isWriteable());
		}

	}


	/**
	 * Dummy VFSFile implementation to trick IconComposer into showing
	 * a clean icon while we retrieve the actual VFSFile for the node.
	 * This avoids some ugly flickering in the trees as the VFSFile
	 * instances are collected.
	 */
	private static class DummyVFSFile extends VFSFile
	{

		public boolean isReadable()
		{
			return true;
		}

		public boolean isWriteable()
		{
			return true;
		}

	}

}


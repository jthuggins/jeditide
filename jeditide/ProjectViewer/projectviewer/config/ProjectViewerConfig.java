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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package projectviewer.config;

//{{{ Imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;

import projectviewer.ProjectManager;
import projectviewer.ProjectPlugin;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTRoot;
//}}}

/**
 *	<p>Class to hold configuration information for the plugin.</p>
 *
 *	<p>Note about property changing events: currently, these events are only
 *	generated for the properties regarding the ProjectViewer GUI (that is,
 *	SHOW_FOLDERS_OPT, SHOW_FILES_OPT, SHOW_WFILES_OPT, USER_CONTEXT_MENU
 *	and USER_MENU_FIRST).If the change of another property needs to be
 *	notified to someone, please include the call to the appropriate
 *	"firePropertyChanged" method is the setter methods of the property.</p>
 *
 *	<p>Also of note is that these events are for internal ProjectViewer use
 *	and are not meant to be used by other plugins interfacing with PV.</p>
 *
 *	@author		Marcelo Vanzin
 */
public final class ProjectViewerConfig {

	//{{{ Static attributes

	public static final String CONFIG_FILE = "config.properties";
	public static final String ERRORLIST_PLUGIN = "errorlist.ErrorListPlugin";
	public static final String INFOVIEWER_PLUGIN = "infoviewer.InfoViewerPlugin";

	public static final String ASK_IMPORT_OPT			  = "projectviewer.ask-import";
	public static final String BROWSER_PATH_OPT			  = "browser-path";
	public static final String BROWSER_USE_INFOVIEWER	  = "projectviewer.browser.use_infoviewer";
	public static final String CASE_INSENSITIVE_SORT_OPT  = "projectviewer.case_insensitive_sort";
	public static final String CLOSE_FILES_OPT			  = "projectviewer.close_files";
	public static final String DELETE_NOT_FOUND_FILES_OPT = "projectviewer.delete_files";
	public static final String EXCLUDE_DIRS_OPT			  = "exclude-dirs";
	public static final String FOLLOW_BUFFER_OPT		  = "projectviewer.follow_buffer";
	public static final String IMPORT_EXTS_OPT			  = "include-extensions";
	public static final String IMPORT_GLOBS_OPT			  = "import-globs";
	public static final String INCLUDE_FILES_OPT		  = "include-files";
	public static final String LAST_NODE_OPT			  = "projectviewer.last-node.";
	public static final String LAST_PROJECT_OPT			  = "projectviewer.last-project";
	public static final String LAST_INIT_VERSION_OPT	  = "projectviewer.last-init-version";
	public static final String REMEBER_OPEN_FILES_OPT	  = "projectviewer.remeber_open";
	public static final String SHOW_PROJECT_TITLE_OPT	  = "projectviewer.show_project_in_title";
	public static final String USE_EXTERNAL_APPS_OPT	  = "projectviewer.use_external_apps";
	public static final String USE_SYSTEM_ICONS_OPT		  = "projectviewer.use_system_icons";

	public static final String SHOW_COMPACT_OPT			  = "projectviewer.show_compact_tree";
	public static final String SHOW_FILES_OPT			  = "projectviewer.show_files_tree";
	public static final String SHOW_FILTERED_OPT		  = "projectviewer.show_filtered_tree";
	public static final String SHOW_FOLDERS_OPT			  = "projectviewer.show_folder_tree";
	public static final String SHOW_WFILES_OPT			  = "projectviewer.show_working_files_tree";
	public static final String SHOW_ALLWFILES_OPT		  = "projectviewer.show_all_working_files";

	public static final String USER_MENU_FIRST			  = "projectviewer.contextmenu.userfirst";
	public static final String USER_CONTEXT_MENU		  = "projectviewer.user_context_menu";

	public static final String EXTENSIONS_PREFIX		  = "projectviewer.extensions.disable.";

	public static final int ASK_ALWAYS	= 0;
	public static final int ASK_ONCE	= 1;
	public static final int ASK_NEVER	= 2;
	public static final int AUTO_IMPORT = 3;

	private static ProjectViewerConfig config = new ProjectViewerConfig();

	//}}}

	//{{{ Static methods

	/** Returns the config. */
	public static ProjectViewerConfig getInstance() {
		return config;
	}

	//}}}

	//{{{ Instance variables

	private boolean caseInsensitiveSort		= false;
	private boolean closeFiles				= true;
	private boolean rememberOpen			= true;
	private boolean deleteNotFoundFiles		= true;
	private boolean followCurrentBuffer		= true;
	private int		askImport				= ASK_ONCE;

	private boolean showFoldersTree			= true;
	private boolean showFilesTree			= true;
	private boolean showWorkingFilesTree	= true;
	private boolean showCompactTree			= true;
	private boolean showFilteredTree		= true;
	private boolean showAllWorkingFiles		= false;
	private boolean showProjectInTitle		= true;
	private boolean useInfoViewer			= false;
	private boolean useExternalApps			= false;
	private boolean useSystemIcons			= false;

	private String importGlobs				= null;
	private String excludeDirs				= null;
	private String lastProject				= null;
	private String browserPath				= "mozilla";

	private boolean userMenuFirst			= false;
	private String userContextMenu			= null;
	private String lastInitVersion			= null;

	private List<PropertyChangeListener>	listeners;
	private Stack<Object>					lastNodes;
	private Map<String, Set<String>>		extensions;

	//}}}

	//{{{ ProjectViewerConfig()
	/**
	 *	<p>Initializes the configuration using the properties available
	 *	in the object passed.</p>
	 *
	 *	<p>This procedure cannot be executed in the constructor because of
	 *	some circular references due to the use of VPTNode.</p>
	 *
	 *	@param	props	An object containing the configuration of the plugin.
	 */
	private ProjectViewerConfig() {
		listeners = new ArrayList<PropertyChangeListener>();

		// loads the properties
		Properties props = new Properties();
		InputStream is = null;
		try {
			is = ProjectPlugin.getResourceAsStream("config.properties");
			props.load(is);
		} catch (Exception e) {
			// Ignores errors
			Log.log(Log.WARNING, ProjectViewerConfig.class, "Cannot read config file.");
		} finally {
			if (is != null) try { is.close(); } catch (Exception e) { }
		}

		String tmp;

		// close_files options

		tmp = props.getProperty(CLOSE_FILES_OPT);
		if (tmp != null) {
			setCloseFiles("true".equalsIgnoreCase(tmp));
		}

		// remember_open options
		tmp = props.getProperty(REMEBER_OPEN_FILES_OPT);
		if (tmp != null) {
			setRememberOpen("true".equalsIgnoreCase(tmp));
		}

		// delete_files option
		tmp = props.getProperty(DELETE_NOT_FOUND_FILES_OPT);
		if (tmp != null) {
			setDeleteNotFoundFiles("true".equalsIgnoreCase(tmp));
		}

		// show_folders_tree
		tmp = props.getProperty(SHOW_FOLDERS_OPT);
		if (tmp != null) {
			setShowFoldersTree("true".equalsIgnoreCase(tmp));
		}

		// show_files_tree
		tmp = props.getProperty(SHOW_FILES_OPT);
		if (tmp != null) {
			setShowFilesTree("true".equalsIgnoreCase(tmp));
		}

		// show_working_files_tree
		tmp = props.getProperty(SHOW_WFILES_OPT);
		if (tmp != null) {
			setShowWorkingFilesTree("true".equalsIgnoreCase(tmp));
		}

		tmp = props.getProperty(SHOW_ALLWFILES_OPT);
		if (tmp != null) {
			setShowAllWorkingFiles("true".equalsIgnoreCase(tmp));
		}

		// show_compact_tree
		tmp = props.getProperty(SHOW_COMPACT_OPT);
			setShowCompactTree("true".equalsIgnoreCase(tmp));

		// show_filtered_tree
		tmp = props.getProperty(SHOW_FILTERED_OPT);
			setShowFilteredTree("true".equalsIgnoreCase(tmp));

		// ask_import
		tmp = props.getProperty(ASK_IMPORT_OPT);
		if (tmp != null) {
			try {
				setAskImport(Integer.parseInt(tmp));
			} catch (NumberFormatException nfe) {
				// ignore
			}
		}

		// use_system_icons
		tmp = props.getProperty(USE_SYSTEM_ICONS_OPT);
		if (tmp != null) {
			setUseSystemIcons("true".equalsIgnoreCase(tmp));
		}

		// show_project_title
		tmp = props.getProperty(SHOW_PROJECT_TITLE_OPT);
		if (tmp != null) {
			setShowProjectInTitle("true".equalsIgnoreCase(tmp));
		}

		// case_insensitive_sort
		tmp = props.getProperty(CASE_INSENSITIVE_SORT_OPT);
		if (tmp != null) {
			setCaseInsensitiveSort("true".equalsIgnoreCase(tmp));
		}

		// Importing options
		importGlobs	 = props.getProperty(IMPORT_GLOBS_OPT);
		excludeDirs	 = props.getProperty(EXCLUDE_DIRS_OPT);

		// Last path
		lastNodes = new Stack<Object>();
		int cnt = 0;
		boolean foundPath = false;
		tmp = props.getProperty(LAST_NODE_OPT + "count");
		if (tmp != null) {
			// new style node path list
			foundPath = true;
			cnt = Integer.parseInt(tmp);
			for (int i = 0; i < cnt; i++) {
				Stack<String> path = new Stack<String>();
				int j = 0;
				do {
					tmp = props.getProperty(LAST_NODE_OPT + i + "." + j);
					if (tmp != null) {
						path.push(tmp);
						j++;
					}
				} while (tmp != null);
				if (!path.isEmpty()) {
					lastNodes.push(path);
				}
			}
		} else {
			// old style single node path
			Stack<String> lastPath = new Stack<String>();
			while (props.getProperty(LAST_NODE_OPT + cnt) != null) {
				lastPath.push(props.getProperty(LAST_NODE_OPT + cnt));
				cnt++;
				foundPath = true;
			}
			if (!lastPath.isEmpty()) {
				lastNodes.push(lastPath);
			}
		}

		// Last opened project
		lastProject	 = props.getProperty(LAST_PROJECT_OPT);
		if (lastProject != null && !foundPath) {
			VPTNode c = getLastNode().getChildWithName(lastProject);
			if (c != null)
				lastNodes.push(c);
		}

		// External apps by default
		tmp = props.getProperty(USE_EXTERNAL_APPS_OPT);
		if (tmp != null) {
			setUseExternalApps("true".equalsIgnoreCase(tmp));
		}

		// BrowserPath
		tmp = props.getProperty(BROWSER_PATH_OPT);
		if (tmp != null) {
			browserPath = tmp;
		}

		// browser.use_infoviewer
		tmp = props.getProperty(BROWSER_USE_INFOVIEWER);
		if (tmp != null && jEdit.getPlugin(INFOVIEWER_PLUGIN) != null) {
			setUseInfoViewer("true".equalsIgnoreCase(tmp));
		}

		// projectviewer.contextmenu.userfirst
		tmp = props.getProperty(USER_MENU_FIRST);
		userMenuFirst = "true".equalsIgnoreCase(tmp);

		// projectviewer.user_context_menu
		tmp = props.getProperty(USER_CONTEXT_MENU);
		if (tmp != null) {
			setUserContextMenu(tmp);
		}

		// follow current buffer
		tmp = props.getProperty(FOLLOW_BUFFER_OPT);
		if (tmp != null) {
			setFollowCurrentBuffer("true".equalsIgnoreCase(tmp));
		}

		// disabled extensions
		extensions = new HashMap<String, Set<String>>();
		for (Object _key : props.keySet()) {
			String key = (String) _key;
			if (key.startsWith(EXTENSIONS_PREFIX)) {
				String ext = key.substring(EXTENSIONS_PREFIX.length());
				StringTokenizer vals = new StringTokenizer(props.getProperty(key),
														   ",");
				Set<String> lst = new HashSet<String>();
				while (vals.hasMoreTokens()) {
					lst.add(vals.nextToken());
				}
				extensions.put(ext, lst);
			}
		}

		//{{{ Incremental updates to the config file
		// last init version
		lastInitVersion = props.getProperty(LAST_INIT_VERSION_OPT);

		// check if import settings mods were applied (version: PV 2.1.0)
		if (lastInitVersion == null ||
				StandardUtilities.compareStrings(lastInitVersion,
					"2.1.0", true) < 0) {
			updateImportSettings(props);
		}

		// only for 2.1.0.1 or below, update the AppLauncher config
		// I had to do a "2.1.0.1" thing because I messed up when
		// releasing the beta for 2.1.0 and numbering it "2.1.0"; so
		// this avoids messing up the configuration for those people
		// that were using the beta.
		if (lastInitVersion == null ||
				StandardUtilities.compareStrings(lastInitVersion,
					"2.1.0.1", true) < 0) {
			updateAppLauncherSettings(props);
		}

		// checks for incremental updates to import settings
		String thisVersion = jEdit.getProperty("plugin.projectviewer.ProjectPlugin.version");
		if (lastInitVersion == null ||
				StandardUtilities.compareStrings(lastInitVersion,
					thisVersion, true) < 0) {
			Properties importProps = loadDefaultImportProps();
			// check again, since we may have had IO problems.
			if (importProps != null) {
				// ok, last version loaded was older than the current plugins,
				// so let's update the entries if any updates exist.
				updateLists(importProps);
			}
		}

		lastInitVersion = thisVersion;
		//}}}

	} //}}}

	//{{{ Properties (Getters and Setters)

	public void setCloseFiles(boolean closeFiles) {
		this.closeFiles = closeFiles;
	}

	public void setDeleteNotFoundFiles(boolean deleteNotFoundFiles) {
		this.deleteNotFoundFiles = deleteNotFoundFiles;
	}

	public void setRememberOpen(boolean newRememberOpen) {
		this.rememberOpen = newRememberOpen;
	}

	public void setAskImport(int newAskImport) {
		int old = this.askImport;
		if (newAskImport > AUTO_IMPORT || newAskImport < ASK_ALWAYS) {
			askImport = ASK_ALWAYS;
		} else {
			this.askImport = newAskImport;
		}
		this.firePropertyChanged(ASK_IMPORT_OPT, new Integer(old),
			new Integer(askImport));
	}

	public void setExcludeDirs(String newExcludeDirs) {
		this.excludeDirs = newExcludeDirs;
	}

	public void setBrowserpath(String newBrowserPath) {
	  this.browserPath = newBrowserPath;
	}

	public void setShowFoldersTree(boolean newShowFoldersTree) {
		boolean old = this.showFoldersTree;
		this.showFoldersTree = newShowFoldersTree;
		firePropertyChanged(SHOW_FOLDERS_OPT, old, newShowFoldersTree);
	}

	public void setShowFilesTree(boolean newShowFilesTree) {
		boolean old = this.showFilesTree;
		this.showFilesTree = newShowFilesTree;
		firePropertyChanged(SHOW_FILES_OPT, old, newShowFilesTree);
	}

	public void setShowWorkingFilesTree(boolean newShowWorkingFilesTree) {
		boolean old = this.showWorkingFilesTree;
		this.showWorkingFilesTree = newShowWorkingFilesTree;
		firePropertyChanged(SHOW_WFILES_OPT, old, newShowWorkingFilesTree);
	}

	public void setShowAllWorkingFiles(boolean newShowAllWorkingFiles) {
		boolean old = this.showAllWorkingFiles;
		this.showAllWorkingFiles = newShowAllWorkingFiles;
		firePropertyChanged(SHOW_ALLWFILES_OPT, old, newShowAllWorkingFiles);
	}

	public void setShowCompactTree(boolean newValue) {
		boolean old = this.showCompactTree;
		this.showCompactTree = newValue;
		firePropertyChanged(SHOW_COMPACT_OPT, old, newValue);
	}

	public void setShowFilteredTree(boolean newValue) {
		boolean old = this.showFilteredTree;
		this.showFilteredTree = newValue;
		firePropertyChanged(SHOW_FILTERED_OPT, old, newValue);
	}

	public boolean getCloseFiles() {
		return closeFiles;
	}

	public boolean getDeleteNotFoundFiles() {
		return deleteNotFoundFiles;
	}

	public boolean getRememberOpen() {
		return rememberOpen;
	}

	public int getAskImport() {
		return askImport;
	}

	public String getExcludeDirs() {
		return excludeDirs;
	}

	public String getBrowserPath() {
		return browserPath;
	}

	public boolean getShowFoldersTree() {
		return showFoldersTree;
	}

	public boolean getShowFilesTree() {
		return showFilesTree;
	}

	public boolean getShowWorkingFilesTree() {
		return showWorkingFilesTree;
	}

	public boolean getShowAllWorkingFiles() {
		return showAllWorkingFiles;
	}

	public boolean getShowCompactTree() {
		return showCompactTree;
	}

	public boolean getShowFilteredTree() {
		return showFilteredTree;
	}

	//{{{ property useInfoViewer
	public void setUseInfoViewer(boolean useInfoViewer) {
		this.useInfoViewer = useInfoViewer;
	}

	public boolean getUseInfoViewer() {
		return (useInfoViewer) ? isInfoViewerAvailable() : false;
	}
	//}}}

	//{{{ property userMenuFirst
	public void setUserMenuFirst(boolean userMenuFirst) {
		boolean oldValue = this.userMenuFirst;
		this.userMenuFirst = userMenuFirst;
		firePropertyChanged(USER_CONTEXT_MENU, oldValue, userMenuFirst);
	}

	public boolean getUserMenuFirst() {
		return userMenuFirst;
	}
	//}}}

	//{{{ property userContextMenu
	public void setUserContextMenu(String userContextMenu) {
		String oldMenu = this.userContextMenu;
		this.userContextMenu = userContextMenu;
		firePropertyChanged(USER_CONTEXT_MENU, oldMenu, userContextMenu);
	}

	public String getUserContextMenu() {
		return (userContextMenu != null) ? userContextMenu : "";
	}
	//}}}

	//{{{ property useSystemIcons
	public void setUseSystemIcons(boolean useSystemIcons) {
		this.useSystemIcons = useSystemIcons;
	}

	public boolean getUseSystemIcons() {
		return useSystemIcons;
	}
	//}}}

	//{{{ property lastNode
	/**
	 *	Sets the path to the given node as the "last active path" used by the
	 *	user. This makes it possible to reload the exact node that was active
	 *	before next time PV starts, be it a project or a group.
	 *
	 *	@since	PV 2.1.0
	 */
	public void setLastNode(VPTNode node) {
		this.lastNodes.push(node);
	}

	/**
	 *	Returns the path to the last active node as a list. Each item is
	 *	the name of a node in the path, starting with the first child of
	 *	the root node at index 0. This method will never return null; at
	 *	least the root of the tree will be returned.
	 *
	 *	<p>Note: DON'T CALL THIS METHOD; it's for internal PV use ONLY,
	 *	and having other callers might mess some things up.</p>
	 *
	 *	@since	PV 2.1.0
	 */
	public VPTNode getLastNode() {
		ProjectManager.getInstance(); // make sure config is loaded
		if (lastNodes.isEmpty()) {
			return VPTRoot.getInstance();
		} else {
			Object lastNode = lastNodes.pop();
			if (lastNode instanceof VPTNode) {
				return (VPTNode) lastNode;
			} else {
				Stack lastPath = (Stack) lastNode;
				VPTNode n = VPTRoot.getInstance();
				while (!lastPath.isEmpty()) {
					VPTNode c = n.getChildWithName((String)lastPath.pop());
					if (c != null)
						n = c;
				}
				return n;
			}
		}
	}

	//}}}

	//{{{ property useExternalApps
	public void setUseExternalApps(boolean useExternalApps) {
		this.useExternalApps = useExternalApps;
	}

	public boolean getUseExternalApps() {
		return useExternalApps;
	}
	//}}}

	//{{{ property importGlobs
	public void setImportGlobs(String importGlobs) {
		this.importGlobs = importGlobs;
	}

	public String getImportGlobs() {
		return importGlobs;
	}
	//}}}

	//{{{ property showProjectInTitle
	public void setShowProjectInTitle(boolean flag) {
		this.showProjectInTitle = flag;
	}

	public boolean getShowProjectInTitle() {
		return showProjectInTitle;
	}
	//}}}

	//{{{ property caseInsensitiveSort
	public void setCaseInsensitiveSort(boolean flag) {
		boolean old = caseInsensitiveSort;
		this.caseInsensitiveSort = flag;
		this.firePropertyChanged(CASE_INSENSITIVE_SORT_OPT, Boolean.valueOf(old),
			Boolean.valueOf(flag));
	}

	public boolean getCaseInsensitiveSort() {
		return caseInsensitiveSort;
	}
	//}}}

	//{{{ property followCurrentBuffer
	public void setFollowCurrentBuffer(boolean flag) {
		this.followCurrentBuffer = flag;
	}

	public boolean getFollowCurrentBuffer() {
		return followCurrentBuffer;
	}
	//}}}

	//{{{ disabled extensions

	public boolean isExtensionEnabled(String type,
	                                  String ext)
	{
		Set<String> lst = extensions.get(type);
		return (lst == null || !lst.contains(ext));
	}


	public void enableExtension(String type,
	                            String ext)
	{
		Set<String> lst = extensions.get(type);
		if (lst != null) {
			lst.remove(ext);
			if (lst.size() == 0) {
				extensions.remove(type);
			}
		}
	}

	public void disableExtension(String type,
	                             String ext)
	{
		Set<String> lst = extensions.get(type);
		if (lst == null) {
			lst = new HashSet<String>();
			extensions.put(type, lst);
		}
		lst.add(ext);
	}

	//}}}

	//}}}

	//{{{ Public Methods

	/**
	 *	Adds a new property change listener to the list.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 *	Removes a property change listener to the list.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.remove(listener);
	}

	//{{{ update(Properties) method
	/**
	 *	<p>Updates the properties in the properties object passed to
	 *	reflect the current state of the config.</p>
	 */
	public void update(Properties props) {
		props.setProperty(CLOSE_FILES_OPT, String.valueOf(closeFiles));
		props.setProperty(REMEBER_OPEN_FILES_OPT, String.valueOf(rememberOpen));
		props.setProperty(DELETE_NOT_FOUND_FILES_OPT, String.valueOf(deleteNotFoundFiles));
		props.setProperty(ASK_IMPORT_OPT, String.valueOf(askImport));
		props.setProperty(USE_EXTERNAL_APPS_OPT, String.valueOf(useExternalApps));
		props.setProperty(USE_SYSTEM_ICONS_OPT, String.valueOf(useSystemIcons));
		props.setProperty(SHOW_PROJECT_TITLE_OPT, String.valueOf(showProjectInTitle));
		props.setProperty(CASE_INSENSITIVE_SORT_OPT, String.valueOf(caseInsensitiveSort));
		props.setProperty(FOLLOW_BUFFER_OPT, String.valueOf(followCurrentBuffer));

		props.setProperty(SHOW_FOLDERS_OPT, String.valueOf(showFoldersTree));
		props.setProperty(SHOW_FILES_OPT, String.valueOf(showFilesTree));
		props.setProperty(SHOW_WFILES_OPT, String.valueOf(showWorkingFilesTree));
		props.setProperty(SHOW_ALLWFILES_OPT, String.valueOf(showAllWorkingFiles));
		props.setProperty(SHOW_COMPACT_OPT, String.valueOf(showCompactTree));
		props.setProperty(SHOW_FILTERED_OPT, String.valueOf(showFilteredTree));

		props.setProperty(IMPORT_GLOBS_OPT, importGlobs);
		props.setProperty(EXCLUDE_DIRS_OPT, excludeDirs);
		props.setProperty(LAST_INIT_VERSION_OPT, lastInitVersion);

		props.setProperty(BROWSER_PATH_OPT, String.valueOf(browserPath));
		props.setProperty(BROWSER_USE_INFOVIEWER, String.valueOf(useInfoViewer));

		props.setProperty(USER_MENU_FIRST, String.valueOf(userMenuFirst));
		if (userContextMenu != null) {
			props.setProperty(USER_CONTEXT_MENU, userContextMenu);
		}

		// last path
		int ncnt = 0;
		int total = lastNodes.size();
		while (!lastNodes.isEmpty()) {
			Object node = lastNodes.pop();
			int pcnt = 0;
			if (node instanceof Stack) {
				Stack nodePaths = (Stack) node;
				for (Object path : nodePaths) {
					props.setProperty(LAST_NODE_OPT + ncnt + "." + pcnt,
									  path.toString());
					pcnt++;
				}
			} else {
				VPTNode n = (VPTNode) node;
				while (!n.isRoot()) {
					props.setProperty(LAST_NODE_OPT + ncnt + "." + pcnt,
									  n.getName());
					n = (VPTNode) n.getParent();
					pcnt++;
				}
			}
			ncnt++;
		}
		props.setProperty(LAST_NODE_OPT + "count", String.valueOf(total));

		// disabled extensions
		for (String ext : extensions.keySet()) {
			StringBuilder val = new StringBuilder();
			Set<String> lst = extensions.get(ext);
			for (Iterator<String> i = lst.iterator(); i.hasNext(); ) {
				val.append(i.next());
				if (i.hasNext()) {
					val.append(",");
				}
			}
			props.setProperty(EXTENSIONS_PREFIX + ext, val.toString());
		}
	} //}}}

	//{{{ save() method
	/** Save the configuration to the plugin's config file on disk. */
	public void save() {
		Properties p = new Properties();
		update(p);

		OutputStream out = ProjectPlugin.getResourceAsOutputStream(CONFIG_FILE);
		if (out != null) {
			try {
				p.store(out, "Project Viewer Config File");
			} catch (Exception e) {
				// Ignore errors?
				Log.log(Log.ERROR, this, "Cannot write to config file!");
			} finally {
				try { out.close(); } catch (Exception e) {}
			}
		} else {
			Log.log(Log.ERROR, this, "Cannot write to config file!");
		}

	} //}}}

	//{{{ isInfoViewerAvailable()
	public boolean isInfoViewerAvailable() {
		return (jEdit.getPlugin(INFOVIEWER_PLUGIN) != null);
	} //}}}

	//{{{ isErrorListAvailable()
	public boolean isErrorListAvailable() {
		return (jEdit.getPlugin(ERRORLIST_PLUGIN) != null);
	} //}}}

	//}}}

	//{{{ Private Methods

	/** Fires and event when a boolean property is changed. */
	private void firePropertyChanged(String property, boolean oldValue, boolean newValue) {
		if (oldValue != newValue) {
			firePropertyChanged(property, new Boolean(oldValue), new Boolean(newValue));
		}
	}

	/** Fires and event when a property is changed. */
	private void firePropertyChanged(String property, Object oldValue, Object newValue) {
		if (((oldValue == null && newValue != null) ||
			 (oldValue != null && newValue != null) ||
			 !oldValue.equals(newValue)) && listeners.size() > 0) {
			PropertyChangeEvent evt =
				new PropertyChangeEvent(this,property,oldValue,newValue);
			for (PropertyChangeListener lsnr : listeners) {
				lsnr.propertyChange(evt);
			}
		}
	}

	/**
	 *	Loads the "import.properties" (or "import-sample.properties") that
	 *	contains information about the file importing options and updates
	 *	to the import lists.
	 *
	 *	@since	PV 2.1.0
	 */
	private Properties loadDefaultImportProps() {
		InputStream is = null;
		Properties props = new Properties();
		try {
			is = ProjectViewerConfig.class.getResourceAsStream("/projectviewer/import-sample.properties");
			if (is != null) {
				props.load(is);
				try { is.close(); } catch (Exception e) { }
			}
			is = ProjectPlugin.getResourceAsStream("import.properties");
			if (is != null) {
				props.load(is);
			}
		} catch (IOException ioe) {
			props.setProperty(IMPORT_EXTS_OPT, "");
			props.setProperty(EXCLUDE_DIRS_OPT, "");
			props.setProperty(INCLUDE_FILES_OPT, "");
		} finally {
			if (is != null) try { is.close(); } catch (Exception e) { }
		}
		return props;
	}

	/**
	 *	Merges the two lists of strings, adding all the strings in "add" that
	 *	are not already in "src", returning the result.
	 *
	 *	@since	PV 2.1.0
	 */
	private String mergeList(String src, String add) {
		StringBuffer res = new StringBuffer(src);
		StringTokenizer st = new StringTokenizer(add, " ");
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (!src.startsWith(s) && !src.endsWith(s)
					&& src.indexOf(" " + s + " ") == -1) {
				res.append(" ").append(s);
			}
		}
		return res.toString();
	}

	/**
	 *	Check the import properties provided for the list of updated
	 *	properties since the "lastInitVersion" detected in the config.
	 *	If the version string is "null", all the updates are applied.
	 *
	 *	@since	PV 2.1.0
	 */
	private void updateLists(Properties importProps) {
		for (Object _key : importProps.keySet()) {
			String key = (String) _key;
			if (key.startsWith(EXCLUDE_DIRS_OPT + "-")) {
				if (needUpdate(key.substring(EXCLUDE_DIRS_OPT.length() + 1))) {
					excludeDirs = mergeList(excludeDirs, importProps.getProperty(key));
				}
			}
			if (key.startsWith(IMPORT_GLOBS_OPT + "-")) {
				if (needUpdate(key.substring(IMPORT_GLOBS_OPT.length() + 1))) {
					importGlobs = mergeList(importGlobs, importProps.getProperty(key));
				}
			}
		}
	}

	/**
	 *	Returns whether we need to update the lists for the given version,
	 *	depending on the lastInitVersion detected from the user's config file.
	 */
	private boolean needUpdate(String updateVersion) {
		return (lastInitVersion == null
				|| StandardUtilities.compareStrings(lastInitVersion, updateVersion, true) < 0);
	}

	/**
	 *	Translates old import settings (< PV 2.1.0) into the 2.1-style globs.
	 *	This also merges the "extensions to include" and "files to include"
	 *	settings into the "include globs" list, since it now can handle both
	 *	cases. The "directories to ignore" doesn't change, since the current
	 *	syntax corresponds to valid globs; the only change is semantic (it's
	 *	now a glob, not just a string match).
	 *
	 *	@since	PV 2.1.0
	 */
	private void updateImportSettings(Properties config) {
		// translates the "extensions to include" into a list of globs.
		String includeExts = config.getProperty(IMPORT_EXTS_OPT);
		if (includeExts == null) {
			// don't have a configuration; just read the defaults and
			// return.
			Properties defaults = loadDefaultImportProps();
			importGlobs = defaults.getProperty(IMPORT_GLOBS_OPT);
			excludeDirs = defaults.getProperty(EXCLUDE_DIRS_OPT);
			return;
		}

		StringTokenizer st = new StringTokenizer(includeExts, " ");
		StringBuffer globs = new StringBuffer();
		while(st.hasMoreTokens()) {
			globs.append("*.").append(st.nextToken()).append(" ");
		}

		// appends the "files to include" list as is
		if (config.get(INCLUDE_FILES_OPT) != null)
			globs.append(config.getProperty(INCLUDE_FILES_OPT));

		setImportGlobs(globs.toString().trim());
	}

	/**
	 *	Translates the old AppLauncher cofiguration into globs.
	 *
	 *	@since	PV 2.1.0.1
	 */
	private void updateAppLauncherSettings(Properties config) {
		InputStream inprops =
			ProjectPlugin.getResourceAsStream("fileassocs.properties");
		Properties p = new Properties();

		// loads the properties from the file and translates the extensions
		// into globs
		if (inprops != null) {
			try {
				p.load(inprops);

				Properties newp = new Properties();
				for (Object _key : p.keySet()) {
					String key = (String) _key;
					newp.put("*." + key, p.get(key));
				}
				p = newp;
			} catch (IOException ioe) {
				Log.log(Log.WARNING, this, ioe);
			} finally {
				try { inprops.close(); } catch (Exception e) { }
			}

		}

		// saves the properties back to the file
		OutputStream out = ProjectPlugin.getResourceAsOutputStream("fileassocs.properties");
		if (out != null) {
			try {
				p.store(out, "");
			} catch (IOException ioe) {
				Log.log(Log.WARNING, this, ioe);
			} finally {
				try { out.close(); } catch (Exception e) { }
			}
		}

	}

	//}}}

}


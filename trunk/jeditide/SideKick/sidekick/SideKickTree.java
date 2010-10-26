/*
 * SideKickTree.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
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

package sidekick;

//{{{ Imports
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Stack;
import java.util.HashSet;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.JLabel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PositionChanging;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StringList;
//}}}

/**
 * The Structure Browser dockable.  One instance is created for each View.
 */
public class SideKickTree extends JTree implements MouseMotionListener
{

    protected View view;

	//{{{ SideKickTree constructor
	public SideKickTree(TreeModel model, View view)
	{
		super(model);
        this.view = view;
	} //}}}

	//{{{ selectPath() method
	public void selectPath(TreePath path)
	{
		setSelectionPath(path);
		Rectangle r = getPathBounds(path);
		if (r != null) {
			r.width = 1;
			scrollRectToVisible(r);
		}
	} //}}}

	//{{{ expandAll() methods
	/**
	 * Expand or collapse all nodes in the tree.
	 * @param expand if true, expand all nodes, if false, collapse all nodes
	 */
	public void expandAll( boolean expand ) {
		TreeNode root = ( TreeNode ) getModel().getRoot();
		expandAll( new TreePath( root ), expand );
	}

	// recursive method to traverse children
	private void expandAll( TreePath parent, boolean expand ) {
		TreeNode node = ( TreeNode ) parent.getLastPathComponent();
		if ( node.getChildCount() >= 0 ) {
		    for ( Enumeration e = node.children(); e.hasMoreElements(); ) {
			TreeNode n = ( TreeNode ) e.nextElement();
			TreePath path = parent.pathByAddingChild( n );
			expandAll( path, expand );
		    }
		}

		// expansion or collapse must be done from the bottom up
		if ( expand ) {
		    expandPath( parent );
		}
		else {
		    collapsePath( parent );
		}
	}//}}}

	//{{{ expandTreeAt() method
	public void expandTreeAt(TreePath treePath)
	{
		if(treePath != null)
		{
			expandPath(treePath);
			setSelectionPath(treePath);
			if (jEdit.getBooleanProperty("sidekick.scrollToVisible")) {
				Rectangle r = getPathBounds(treePath);
				if (r != null) {
					r.width = 1;
					scrollRectToVisible(r);
				} else
			    scrollPathToVisible(treePath);
			}
		}
	} //}}}
	
	public void expandCurrentNode() {
		DefaultMutableTreeNode node =  (DefaultMutableTreeNode)getLastSelectedPathComponent();	
		TreePath path = new TreePath(node.getPath());
		expandPath(path);		
	}
	
	public void collapseCurrentNode() {
		DefaultMutableTreeNode node =  (DefaultMutableTreeNode)getLastSelectedPathComponent();	
		TreePath path = new TreePath(node.getPath());
		collapsePath(path);		
	}

	public void find_visible_nodes(HashSet<TreePath> set, DefaultMutableTreeNode node)
	{
		TreePath path = new TreePath(node.getPath());
		if (isVisible(path) && isExpanded(path)) {
			set.add(new TreePath(node.getPath()));
			for (Enumeration e = node.children(); e.hasMoreElements(); )
				find_visible_nodes(set, (DefaultMutableTreeNode)e.nextElement());
		}
	}
	
	public void filter_visible_nodes(FilteredTreeModel model, 
					    HashSet<TreePath> visible, 
					    DefaultMutableTreeNode node)
	{
		TreePath path = new TreePath(node.getPath());
		if (!visible.contains(path)) {
			return;
		}
		
		expandPath(path);
		for (Enumeration e = node.children(); e.hasMoreElements(); )
		{
			filter_visible_nodes(model, visible, (DefaultMutableTreeNode)e.nextElement());
		}
	}
	
	public void mouseDragged(MouseEvent evt) {
	}


	public void mouseMoved(MouseEvent evt)
	{
		TreePath path = getPathForLocation(
			evt.getX(),evt.getY());
		if(path == null)
			view.getStatus().setMessage(null);
		else
		{
			Object value = ((DefaultMutableTreeNode)path
				.getLastPathComponent()).getUserObject();

			if(value instanceof IAsset)
			{
				String info = ((IAsset)value).getShortString();
				view.getStatus().setMessage(info);
			}
		}
	}
}


/*
 * QuickSourceBrowser.java
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
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.lang.RuntimeException;
import java.lang.StringBuffer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Stack;
import java.util.HashSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
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

import sidekick.SideKickTree;
//}}}

/**
 * The Structure Browser dockable.  One instance is created for each View.
 */
public class QuickSourceBrowser extends JFrame implements DefaultFocusComponent
{


	protected SideKickTree tree;
	protected JTextArea status;
	private JPanel treePanel;
	private boolean statusShowing = false;
	private Buffer lastParsedBuffer = null;
	private JToolBar filterBox;

	protected View view;
	private Timer caretTimer;

	protected SideKickParsedData data;

	private int autoExpandTree = 0;
	private JPanel toolBox;

	private JTextField searchField;
	//}}}

	//{{{ QuickSourceBrowser constructor
	public QuickSourceBrowser(View view)
	{
	    super(new String());
		int preferredWidth = view.getWidth() / 4;
		int preferredHeight = (3 * preferredWidth)/2;

		int x = view.getX() + (view.getWidth() - preferredWidth )/2;
		int y = view.getY() + view.getJMenuBar().getHeight();

		setBounds(x, y, preferredWidth, preferredHeight);
		setIconImage(view.getIconImage());

		if(view.getTextArea() == null) {
		    throw new RuntimeException("No text area define");
		}
	
		treePanel = new JPanel(new BorderLayout());

		this.view = view;

		treePanel = new JPanel(new BorderLayout());

		filterBox = new JToolBar();
		filterBox.setLayout(new BorderLayout());
		filterBox.setFloatable(false);

		searchField = new JTextField();
		searchField.setToolTipText(jEdit.getProperty("sidekick-tree.filter.tooltip"));

		filterBox.add(searchField, BorderLayout.CENTER);

		toolBox = new JPanel(new BorderLayout());
		toolBox.add(BorderLayout.SOUTH, filterBox);

		treePanel.add(BorderLayout.NORTH,toolBox);

		// create a faux model that will do until a real one arrives
		TreeModel emptyModel = new DefaultTreeModel(new DefaultMutableTreeNode(null));
		emptyModel = new FilteredTreeModel((DefaultTreeModel)emptyModel, true);
		tree = new SideKickTree(emptyModel, view);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		KeyHandler kh = new KeyHandler();
		tree.addKeyListener(kh);
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                processMousePress(evt);
            }

            public void mouseExited(MouseEvent evt) {
                processMouseExited(evt);
            }
        };
        tree.addMouseListener(ml);

		tree.addMouseMotionListener(tree);
		searchField.addKeyListener(kh);

		// looks bad with the OS X L&F, apparently...
		if(!OperatingSystem.isMacOSLF())
			tree.putClientProperty("JTree.lineStyle", "Angled");

		tree.setVisibleRowCount(10);
		tree.setCellRenderer(new Renderer());

		treePanel.add(BorderLayout.CENTER,new JScrollPane(tree));

		add(treePanel);
		setVisible(true);

        WindowFocusListener wfl = new WindowAdapter() {
            public void windowLostFocus(WindowEvent evt) {
                setVisible(false);
                dispose();
            }
        };
        addWindowFocusListener(wfl);

		update();
	} //}}}

	//{{{ focusOnDefaultComponent() method
	public void focusOnDefaultComponent()
	{
		searchField.requestFocusInWindow();
	} //}}}

	//{{{ addNotify() method
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
	} //}}}

	//{{{ removeNotify() method
	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
	} //}}}
	
	//{{{ CaretHandler class
	/*class CaretHandler implements CaretListener
	{
		public void caretUpdate(CaretEvent evt)
		{
			if (!view.getBuffer().equals( lastParsedBuffer)) {
				return;
			}
			if(evt.getSource() == view.getTextArea())
			{
				expandTreeWithDelay();
			}
		}
	}*/ //}}}

	//{{{ handleEditPaneUpdate() method
	@EBHandler
	public void handleEditPaneUpdate(EditPaneUpdate epu)
	{
		//EditPane editPane = epu.getEditPane();
		//if(epu.getWhat() == EditPaneUpdate.CREATED)
		//	editPane.getTextArea().addCaretListener(new CaretHandler());
	} //}}}

	//{{{ handleSideKickUpdate() method
	@EBHandler
	public void handleSideKickUpdate(SideKickUpdate msg)
	{
		if(msg.getView() == view)
			update();
	} //}}}

	//{{{ addData method
	protected void addData(Object obj, Stack<String> keys)
	{
		if (obj instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)obj;
			String cur_key = "";
			FilteredTreeModel model = (FilteredTreeModel)tree.getModel();
			try {
				Asset a = (Asset)node.getUserObject();
				if (a != null)
					cur_key = a.getName();
			} catch (ClassCastException ex) {
				if (node.toString() != null)
					cur_key = node.toString();
			}
			keys.push(cur_key);
			if (model.isLeaf(node)) {
				for (String key : keys) {
					model.addSearchKey(node, key);
				}
			}

			Enumeration<DefaultMutableTreeNode> e;
			for (e = node.children(); e.hasMoreElements(); ) {
				addData(e.nextElement(), keys);
			}

			keys.pop();
		} else {
			Log.log(Log.DEBUG, this, "addData called on a node that isn't a treenode!!!!!!!!!"); // how exciting!
		}
	} //}}}

	//{{{ expandTreeWithDelay() method
	/**
	 * Expands the tree after a delay.
	 * The delay timer is restarted each time this method is called.
	 */
	protected void expandTreeWithDelay()
	{
		if(caretTimer != null) {
			caretTimer.stop();
		}
		else
		{
			caretTimer = new Timer(0,new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					if (searchField.getText().length() > 0) {
						searchField.setText("");
						updateFilter(false);
					}
					TextArea textArea = view.getTextArea();
					int caret = textArea.getCaretPosition();
					Selection s = textArea.getSelectionAtOffset(caret);
                    if(data != null) {
                       TreePath p = data.getTreePathForPosition(s == null ? caret : s.getStart());
					   tree.expandTreeAt(p);
                    }
				}
			});
			caretTimer.setInitialDelay(500);
			caretTimer.setRepeats(false);
		}
		caretTimer.start();
	} //}}}

	//{{{ updateSearchData() method
	protected void updateSearchData()
	{
		DefaultMutableTreeNode root;
		FilteredTreeModel model = (FilteredTreeModel)tree.getModel();
		root = (DefaultMutableTreeNode)model.getRoot();
		addData(root, new Stack<String>());
	} //}}}

	//{{{ update() method
	protected void update()
	{
		Buffer parsedBuffer = view.getBuffer();
		SideKickParser parser = SideKickPlugin.getParserForBuffer(parsedBuffer);

		data = SideKickParsedData.getParsedData(view);
		if(parser == null || data == null)
		{
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(parsedBuffer.getName());
			root.insert(new DefaultMutableTreeNode(
				jEdit.getProperty("sidekick-tree.not-parsed")),0);
			tree.setModel(new FilteredTreeModel(new DefaultTreeModel(root), true));
			lastParsedBuffer = null;
		}
		else
		{
			tree.setModel(new FilteredTreeModel(data.tree, true));
			lastParsedBuffer = parsedBuffer;
		}
		updateSearchData();

		if (data != null && data.expansionModel != null) 
		{
			for (Integer row : data.expansionModel) 
			{
				tree.expandRow(row);	
			}
		}
		else 
		{
			if (autoExpandTree == -1)
				tree.expandAll(true);
			else if (autoExpandTree == 0)
				tree.expandAll(false);
			else if (autoExpandTree > 0) {
				tree.expandRow( 0 );
				for (int i = 1; i < autoExpandTree; i++) {
					for ( int j = tree.getRowCount() - 1; j > 0; j-- )
					    tree.expandRow( j );
				}
			}
		}

		updateFilter();

	} //}}}

	protected void processMousePress(MouseEvent evt)
	{
		TreePath path = tree.getPathForLocation(
			evt.getX(),evt.getY());
		if(path != null)
		{
			Object value = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();

			if(value instanceof IAsset)
			{
				IAsset asset = (IAsset)value;

				JEditTextArea textArea = view.getTextArea();
				EditPane editPane = view.getEditPane();

				if(evt.getClickCount() == 2)
				{
					doubleClicked(view,asset,path);
				}
				else if(evt.isShiftDown())
				{
					shiftClick(view,asset,path);
				}
				else if(evt.isControlDown())
				{
					controlClick(view,asset,path);
				}
				else {
					EditBus.send(new PositionChanging(editPane));
					textArea.setCaretPosition(asset.getStart().getOffset());
				}
				setVisible(false);
				dispose();

			}
		}
			
    }

    protected void processMouseExited(MouseEvent evt)
    {
		view.getStatus().setMessage(null);
        super.processMouseEvent(evt);
	}

	private void doubleClicked(View view, IAsset asset, TreePath path)
	{
	}

	private void shiftClick(View view, IAsset asset, TreePath path)
	{
		JEditTextArea textArea = view.getTextArea();
		textArea.setCaretPosition(asset.getEnd().getOffset());
		Selection.Range range = new Selection.Range(
			asset.getStart().getOffset(),
			asset.getEnd().getOffset() );
		textArea.addToSelection(range);
	}

	private void controlClick(View view, IAsset asset, TreePath path)
    {
		JEditTextArea textArea = view.getTextArea();
		textArea.getDisplayManager().narrow(
			textArea.getLineOfOffset(asset.getStart().getOffset()),
			textArea.getLineOfOffset(asset.getEnd().getOffset()));
	}

	public void updateFilter()
	{
		updateFilter(true);
	}

	public void updateFilter(boolean with_delay)
	{
		FilteredTreeModel ftm = (FilteredTreeModel)tree.getModel();

		if (searchField.getText().length() == 0) {
			ftm.clearFilter();
			ftm.reset();
			if (autoExpandTree == -1)
				tree.expandAll(true);
			else if (autoExpandTree == 0)
				tree.expandAll(false);
			else if (autoExpandTree > 0) {
				tree.expandRow( 0 );
				for (int i = 1; i < autoExpandTree; i++) {
					for ( int j = tree.getRowCount() - 1; j > 0; j-- )
						tree.expandRow( j );
				}
			}
			if(with_delay){
			    expandTreeWithDelay();
			}

		} else {
			HashSet<TreePath> visible = new HashSet<TreePath>();
			tree.find_visible_nodes(visible, (DefaultMutableTreeNode)ftm.getRoot());
			ftm.filterByText(searchField.getText());
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)ftm.getRoot();
			tree.expandAll(true);
		}
	}

	public void setSearchFilter(String text)
	{
		searchField.setText(text);
		updateFilter();
	}
	public String getSearchFilter()
	{
		return searchField.getText();
	}

	//{{{ KeyHandler class
	class KeyHandler extends KeyAdapter
	{

		protected void next() 
		{
			DefaultMutableTreeNode node =  (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			FilteredTreeModel model = (FilteredTreeModel)tree.getModel();
			if (node == null) {
				node = (DefaultMutableTreeNode)model.getRoot();
			}
			
			// standard tree movement for next:
			// If selected node has children and selected node is expanded,
			// then next is the first child of the selected node, otherwise,
			// next is the next sibling.
			// If selected node is a leaf, then next is next sibling.
			// If next sibling is null, that means selected node is the
			// last child of the parent node, so next is parent.nextSibling
			if (node.getChildCount() > 0) {
				DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode)node.getFirstChild();
				TreePath path = new TreePath(firstChild.getPath());
				if (tree.isVisible(path)) {
					node = firstChild;
				}
				else {
					node = (DefaultMutableTreeNode)node.getNextSibling();
				}
			}
			else {	
				// node is a leaf
				DefaultMutableTreeNode next = (DefaultMutableTreeNode)node.getNextSibling();
				if (next == null) {
					// must be last child of parent
					next = (DefaultMutableTreeNode)((DefaultMutableTreeNode)node.getParent()).getNextSibling();
				}
				node = next;
			}
			
			if (node != null) {
				TreePath p = new TreePath(node.getPath());
				tree.selectPath(p);
			}
		}
		
		protected void nextLeaf()
		{
			DefaultMutableTreeNode node =  (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			FilteredTreeModel model = (FilteredTreeModel)tree.getModel();
			if (node == null) {
				node = (DefaultMutableTreeNode)model.getRoot();
			}
			if (model.isLeaf(node)) {
				node = node.getNextLeaf();	
			} else {
				Enumeration<DefaultMutableTreeNode> e = node.depthFirstEnumeration();
				node = e.nextElement();
			}
			if (node != null) {
				while ((node != null) && !(model.isVisible(node) && tree.isVisible(new TreePath(node.getPath())))) {
					node = node.getNextLeaf();
				}
				if (node != null) {
					TreePath p = new TreePath(node.getPath());
					tree.selectPath(p);
				}
			}
		}
		
		protected void prev() 
		{
			DefaultMutableTreeNode node =  (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			FilteredTreeModel model = (FilteredTreeModel)tree.getModel();
			if (node == null) {
				node = (DefaultMutableTreeNode)model.getRoot();
			}
			
			// standard movement for previous:
			// Initially, previous is the previous sibling.
			// If previous sibling is null, then that means the current node
			// is the first child of the parent node, so previous is the
			// parent node.
			// If previous sibling has children and is expanded, then previous
			// is the last child of the previous sibling.
			DefaultMutableTreeNode prev = node.getPreviousSibling();
			if (prev == null) {	// could be first child
				node = (DefaultMutableTreeNode)node.getParent();
			}
			else if (prev.getChildCount() > 0) {
				DefaultMutableTreeNode lastChild = (DefaultMutableTreeNode)prev.getLastChild();
				TreePath path = new TreePath(lastChild.getPath());
				if (tree.isVisible(path)) {
					node = lastChild;
				}
				else {
					node = prev;	
				}
			}
			else {
				node = prev;	
			}
			
			if (node != null) {
				TreePath p = new TreePath(node.getPath());
				tree.selectPath(p);
			}
		}

		protected void prevLeaf()
		{
			DefaultMutableTreeNode node =  (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			FilteredTreeModel model = (FilteredTreeModel)tree.getModel();
			if (node == null) {
				node = (DefaultMutableTreeNode)model.getRoot();
			}
			// If the node isn't a leaf, use depthFirstEnumeration to find the next
			// leaf (which moves us forward), then get the previous (to get the previous
			// node from where we started).
			if (!model.isLeaf(node)) {
				Enumeration<DefaultMutableTreeNode> e = node.depthFirstEnumeration();
				node = e.nextElement();
			}
			node = node.getPreviousLeaf();	
			if (node != null) {
				while ((node != null) && (!model.isVisible(node))) {
					node = node.getPreviousLeaf();
				}
				if (node != null) {
					TreePath p = new TreePath(node.getPath());
					tree.selectPath(p);
				}
			}
		}

		public void keyPressed(KeyEvent evt)
		{
			if(caretTimer != null)
				caretTimer.stop();


			switch (evt.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:
					evt.consume();
                    setVisible(false);
                    dispose();
					break;
				case KeyEvent.VK_ENTER:
					evt.consume();

					TreePath path = tree.getSelectionPath();

					if(path != null)
					{
						Object value = ((DefaultMutableTreeNode)path
							.getLastPathComponent()).getUserObject();

						if(value instanceof IAsset)
						{
							IAsset asset = (IAsset)value;

							JEditTextArea textArea = view.getTextArea();

							if(evt.isShiftDown()) {
								textArea.setCaretPosition(asset.getEnd().getOffset());
								textArea.addToSelection(
									new Selection.Range(
										asset.getStart().getOffset(),
										asset.getEnd().getOffset() + 1));
							} else {
								textArea.setCaretPosition(asset.getStart().getOffset());
							}
							textArea.requestFocus();
							setVisible(false);
							dispose();
						}
					}
					break;
				case KeyEvent.VK_BACK_SPACE:
					evt.consume();
					if (searchField.getText().length() <= 1) {
						searchField.setText("");
					} else {
						StringBuffer s = new StringBuffer(searchField.getText());
						s.setLength(s.length() - 1);
						searchField.setText(s.toString());
					}
					updateFilter();
					break;
				case KeyEvent.VK_DOWN:
					evt.consume();
					nextLeaf();
					break;
				case KeyEvent.VK_UP:
					evt.consume();
					prevLeaf();
					break;
				case KeyEvent.VK_LEFT:
					tree.collapseCurrentNode();
					break;
				case KeyEvent.VK_RIGHT:
					tree.expandCurrentNode();
					break;
				case KeyEvent.VK_PAGE_UP:
				{
					evt.consume();

					int offset = tree.getScrollableUnitIncrement(tree.getParent().getBounds(), javax.swing.SwingConstants.VERTICAL, 0);
					for (int i = 0; i < offset; ++i) {
						if (evt.isControlDown())
							prevLeaf();
						else
							prev();
					}
				}
				break;
				case KeyEvent.VK_PAGE_DOWN:
				{
					evt.consume();

					int offset = tree.getScrollableUnitIncrement(tree.getParent().getBounds(), javax.swing.SwingConstants.VERTICAL, 0);
					for (int i = 0; i < offset; ++i) {
						if (evt.isControlDown())
							nextLeaf();
						else
							next();
					}
				}
				break;
				default:
					break;
			}
		}

		public void keyTyped(KeyEvent evt)
		{
			Character c = evt.getKeyChar();
			// TODO: What is the correct combo here to filter
			// non-identifier characters?
			if (Character.isLetterOrDigit(c) ||
				(" _!@$%^&*()_+-=[]{};':\",.<>/?\\|".indexOf(c) != -1))
			{
				evt.consume();
				searchField.setText(searchField.getText() + c);
				updateFilter();
			}
		}
	} //}}}

	// {{{ SidekickProperties class
	/**
	 * This class creates an options dialog containing an optionpane
	 * for each SideKick service, as well as one for SideKick itself.
	 * This properties pane is mode-sensitive.
	 *
	 * sidekick options, and one for the specific plugin's option pane.
	 */
	class SideKickProperties implements ActionListener {

		public void actionPerformed(ActionEvent e)
		{
			try {
				new ModeOptionsDialog(view);
			}
			catch (Exception ex) {
				Log.log (Log.ERROR, this, "dialog create failed", ex);
			}

		}

	} // }}}

	//{{{ Renderer class
	class Renderer extends DefaultTreeCellRenderer
	{
		public Component getTreeCellRendererComponent(JTree tree,
			Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus)
		{
			super.getTreeCellRendererComponent(tree,value,sel,
				expanded,leaf,row,hasFocus);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			Object nodeValue = node.getUserObject();
			if(nodeValue instanceof IAsset)
			{
				IAsset asset = (IAsset)node.getUserObject();

				setText(asset.getShortString());
				setIcon(asset.getIcon());
			}
			// is root?
			else if(node.getParent() == null)
			{
				setIcon(org.gjt.sp.jedit.browser.FileCellRenderer
					.fileIcon);
			}
			else
				setIcon(null);

			return this;
		}
	} //}}}
	

	//}}}
}

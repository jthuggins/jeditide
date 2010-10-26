/*
 * IndexDockable.java
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

package jexplorer.gui;

import java.util.Enumeration;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.ToolTipManager;
import javax.swing.SwingUtilities;
import javax.swing.Box;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeNode;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import javax.swing.plaf.basic.BasicTreeUI;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;

import jexplorer.model.Model;

import jexplorer.model.members.MElement;
import jexplorer.model.members.MMember;

import jexplorer.model.properties.PMOption;
import jexplorer.model.properties.PMModelStatus;

import jexplorer.model.selection.ModelSelection;
import jexplorer.model.selection.SelectionListener;
import jexplorer.model.selection.SelectionEvent;

import jexplorer.gui.detail.DetailsCellRenderer;

import jexplorer.gui.index.IndexModel;
import jexplorer.gui.index.IndexComparator;
import jexplorer.gui.index.IndexCellRenderer;
/*------------------------------------------------------------------------------------------------------------------------------------
	IndexDockable
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* An Index window for the JExplorer plugin.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
*/
public class IndexDockable extends JExplorerDockable
{
static private final boolean	DEBUG = false;

// control panel related items
private Rectangle				m_PositionRect			= new Rectangle();
private JTextField				m_PositionField;

// index list related items
private	JList					m_List;
private boolean					m_IsListAutoSelection	= false;
private LBuilder				m_ListBuilder			= new LBuilder();
private LUpdater				m_ListUpdater			= new LUpdater();
private LPainter				m_ListPainter			= new LPainter();
private LSorter					m_ListSorter			= new LSorter();

// members tree related items
private JTree					m_Tree;
private DefaultMutableTreeNode	m_NullNode;
private boolean					m_IsTreeAutoSelection	= false;
private TBuilder				m_TreeBuilder			= new TBuilder();
private TPainter				m_TreePainter			= new TPainter();
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public IndexDockable(View inView, String inPosition)
{
super(inView, inPosition);

// COMMON TOOLBAR

JPanel	commonToolbar = new JPanel();
commonToolbar.setLayout(new BoxLayout(commonToolbar, BoxLayout.Y_AXIS));
add(commonToolbar, BorderLayout.NORTH);

// create the Common Toolbar Top Panel
JPanel	cttp = new JPanel();
cttp.setLayout(new BoxLayout(cttp, BoxLayout.X_AXIS));
commonToolbar.add(cttp);

cttp.add(m_ModelsCombo);			// add the standard Project selector
setupToolbarComponent(cttp, PMModelStatus.createLabel(PM_MODEL_STATUS));

// create the Common Toolbar Bottom Panel
JPanel	ctbp = new JPanel();
ctbp.setLayout(new BoxLayout(ctbp, BoxLayout.X_AXIS));
commonToolbar.add(ctbp);

ctbp.add(buildHistoryToolbar());	// add the standard history toolbar
ctbp.add(Box.createHorizontalGlue());
setupToolbarComponent(ctbp, PMOption.createToggleButton(PM_USE_SHORT_NAMES,		ICON_BTN_SHORT_NAMES));
setupToolbarComponent(ctbp, PMOption.createToggleButton(PM_SHOW_ACCESS_SYMBOLS,	ICON_BTN_SYMBOLS));
// NOT IMPLEMENTED setupToolbarComponent(ctbp, PMOption.createToggleButton(PM_HILITE_OVERR_METHODS,ICON_BTN_OVERRIDING));
ctbp.add(Box.createHorizontalStrut(6));
setupToolbarComponent(ctbp, PMOption.createToggleButton(PM_SHOW_DEPRECATED,		ICON_BTN_DEPRECATED));

// INDEX PANEL

JPanel	indexPanel = new JPanel(new BorderLayout());

// create the index toolbar
JPanel	idxToolsPanel = new JPanel();
idxToolsPanel.setLayout(new BoxLayout(idxToolsPanel, BoxLayout.Y_AXIS));
indexPanel.add(idxToolsPanel, BorderLayout.NORTH);

// create the Index Toolbar Top Panel
JPanel	ittp = new JPanel();
ittp.setLayout(new BoxLayout(ittp, BoxLayout.X_AXIS));
idxToolsPanel.add(ittp);

setupToolbarComponent(ittp, PMOption.createToggleButton(PI_SHOW_CLASSES,		ICON_CLASS));
setupToolbarComponent(ittp, PMOption.createToggleButton(PI_SHOW_INTERFACES,		ICON_INTERFACE));
setupToolbarComponent(ittp, PMOption.createToggleButton(PI_SHOW_NESTED,			/*ICON_NESTEDELEMENT*/ICON_NESTEDCLASS));
setupToolbarComponent(ittp, PMOption.createToggleButton(PI_SHOW_FIELDS,			ICON_FIELD));
setupToolbarComponent(ittp, PMOption.createToggleButton(PI_SHOW_CONSTRUCTORS,	ICON_CONSTRUCTOR));
setupToolbarComponent(ittp, PMOption.createToggleButton(PI_SHOW_METHODS,		ICON_METHOD));
ittp.add(Box.createHorizontalGlue());
setupToolbarComponent(ittp, PMOption.createToggleButton(PM_COLORIZE_PACKAGES,	ICON_BTN_COLORIZE_PKG));
//setupToolbarComponent(ittp, PMPackageColors.createButton(PM_PACKAGE_COLORS,		ICON_BTN_COLPKG_DIALOG));
//ittp.add(Box.createHorizontalStrut(6));
//setupToolbarComponent(ittp, PMLibraries.createButton(PM_LIBRARIES,				ICON_BTN_LIB_DIALOG));

// create the Index Toolbar Bottom Panel
JPanel	itbp = new JPanel();
itbp.setLayout(new BoxLayout(itbp, BoxLayout.X_AXIS));
idxToolsPanel.add(itbp);

setupToolbarComponent(itbp, PMOption.createToggleButton(PI_SHOW_PUBLIC,		ICON_BTN_PUBLIC));
setupToolbarComponent(itbp, PMOption.createToggleButton(PI_SHOW_PROTECTED,	ICON_BTN_PROTECTED));
setupToolbarComponent(itbp, PMOption.createToggleButton(PI_SHOW_PRIVATE,	ICON_BTN_PRIVATE));
setupToolbarComponent(itbp, PMOption.createToggleButton(PI_SHOW_PACKAGE,	ICON_BTN_PACKAGE));
itbp.add(Box.createHorizontalStrut(6));

// create the search field
m_PositionField = new JTextField();
itbp.add(m_PositionField);

m_PositionField.getDocument().addDocumentListener(new DocumentListener() {
	public void insertUpdate(DocumentEvent inEvt)	{ positionList(); }
	public void removeUpdate(DocumentEvent inEvt)	{ positionList(); }
	public void changedUpdate(DocumentEvent inEvt)	{ positionList(); }
	});

// create the JList

m_List = new JList(new IndexModel());
m_List.setCellRenderer(new IndexCellRenderer(this));
m_List.addListSelectionListener(new LSelectionListener());
m_List.addMouseListener(new LMouseListener());
// enable cell tooltips - this has no effect as of JDK 1.3.1_01
ToolTipManager.sharedInstance().registerComponent(m_List);
indexPanel.add(new JScrollPane(m_List), BorderLayout.CENTER);

// DETAILS PANEL

JPanel	detailsPanel = new JPanel(new BorderLayout());

// create the Details Toolbar
JPanel	dt = new JPanel();
dt.setLayout(new BoxLayout(dt, BoxLayout.X_AXIS));
detailsPanel.add(dt, BorderLayout.NORTH);

setupToolbarComponent(dt, PMOption.createToggleButton(PD_SHOW_NESTED,		/*ICON_NESTEDELEMENT*/ICON_NESTEDCLASS));
setupToolbarComponent(dt, PMOption.createToggleButton(PD_SHOW_FIELDS,		ICON_FIELD));
setupToolbarComponent(dt, PMOption.createToggleButton(PD_SHOW_CONSTRUCTORS,	ICON_CONSTRUCTOR));
setupToolbarComponent(dt, PMOption.createToggleButton(PD_SHOW_METHODS,		ICON_METHOD));
dt.add(Box.createHorizontalGlue());
setupToolbarComponent(dt, PMOption.createToggleButton(PD_SHOW_PUBLIC,		ICON_BTN_PUBLIC));
setupToolbarComponent(dt, PMOption.createToggleButton(PD_SHOW_PROTECTED,	ICON_BTN_PROTECTED));
setupToolbarComponent(dt, PMOption.createToggleButton(PD_SHOW_PRIVATE,		ICON_BTN_PRIVATE));
setupToolbarComponent(dt, PMOption.createToggleButton(PD_SHOW_PACKAGE,		ICON_BTN_PACKAGE));

// create the JTree
// create a default node to be shown when graph selection is null
m_NullNode = new DefaultMutableTreeNode("", false);

m_Tree = new JTree(m_NullNode);
m_Tree.putClientProperty("JTree.lineStyle", "Angled");
m_Tree.setCellRenderer(new DetailsCellRenderer(this));
m_Tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
m_Tree.addTreeSelectionListener(new TSelectionListener());
m_Tree.addMouseListener(new TMouseListener());
ToolTipManager.sharedInstance().registerComponent(m_Tree);	// enable tooltips
detailsPanel.add(new JScrollPane(m_Tree), BorderLayout.CENTER);

// create the JSplitPane

final JSplitPane	splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, indexPanel, detailsPanel);
splitPane.setOneTouchExpandable(true);
add(splitPane, BorderLayout.CENTER);

updateSelection();	// disable history buttons

SwingUtilities.invokeLater(new Runnable() {
	public void run()
		{
		splitPane.setDividerLocation(0.4);
		}
	});
}
/*------------------------------------------------------------------------------------------------------------------------------------
	handleMessage [implements EBComponent]
------------------------------------------------------------------------------------------------------------------------------------*/
public void handleMessage(EBMessage inMessage)
{
if(inMessage instanceof PropertiesChanged)
	{
	m_List.repaint();
	updateTree();
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	register
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	register(Model inModel)
{
super.register(inModel);

// connect LIST tasks to the property managers
m_Model.getProperties().getPM(PM_USE_SHORT_NAMES)		.addActionListener(m_ListSorter);

m_Model.getProperties().getPM(PI_SHOW_CLASSES)			.addActionListener(m_ListBuilder);
m_Model.getProperties().getPM(PI_SHOW_INTERFACES)		.addActionListener(m_ListBuilder);
m_Model.getProperties().getPM(PI_SHOW_NESTED)			.addActionListener(m_ListBuilder);
m_Model.getProperties().getPM(PI_SHOW_FIELDS)			.addActionListener(m_ListBuilder);
m_Model.getProperties().getPM(PI_SHOW_CONSTRUCTORS)		.addActionListener(m_ListBuilder);
m_Model.getProperties().getPM(PI_SHOW_METHODS)			.addActionListener(m_ListBuilder);
m_Model.getProperties().getPM(PI_SHOW_PUBLIC)			.addActionListener(m_ListBuilder);
m_Model.getProperties().getPM(PI_SHOW_PROTECTED)		.addActionListener(m_ListBuilder);
m_Model.getProperties().getPM(PI_SHOW_PRIVATE)			.addActionListener(m_ListBuilder);
m_Model.getProperties().getPM(PI_SHOW_PACKAGE)			.addActionListener(m_ListBuilder);
m_Model.getProperties().getPM(PM_SHOW_DEPRECATED)		.addActionListener(m_ListBuilder);

m_Model.getProperties().getPM(PM_SHOW_ACCESS_SYMBOLS)	.addActionListener(m_ListPainter);
// NOT IMPLEMENTED m_Model.getProperties().getPM(PM_HILITE_OVERR_METHODS)	.addActionListener(m_ListPainter);
m_Model.getProperties().getPM(PM_COLORIZE_PACKAGES)		.addActionListener(m_ListPainter);
m_Model.getProperties().getPM(PM_PACKAGE_COLORS)		.addActionListener(m_ListPainter);

// connect TREE tasks to the property managers
m_Model.getProperties().getPM(PM_USE_SHORT_NAMES)		.addActionListener(m_TreePainter);
m_Model.getProperties().getPM(PM_SHOW_ACCESS_SYMBOLS)	.addActionListener(m_TreePainter);
// NOT IMPLEMENTED m_Model.getProperties().getPM(PM_HILITE_OVERR_METHODS)	.addActionListener(m_TreePainter);

m_Model.getProperties().getPM(PD_SHOW_NESTED)			.addActionListener(m_TreeBuilder);
m_Model.getProperties().getPM(PD_SHOW_FIELDS)			.addActionListener(m_TreeBuilder);
m_Model.getProperties().getPM(PD_SHOW_CONSTRUCTORS)		.addActionListener(m_TreeBuilder);
m_Model.getProperties().getPM(PD_SHOW_METHODS)			.addActionListener(m_TreeBuilder);
m_Model.getProperties().getPM(PD_SHOW_PUBLIC)			.addActionListener(m_TreeBuilder);
m_Model.getProperties().getPM(PD_SHOW_PROTECTED)		.addActionListener(m_TreeBuilder);
m_Model.getProperties().getPM(PD_SHOW_PRIVATE)			.addActionListener(m_TreeBuilder);
m_Model.getProperties().getPM(PD_SHOW_PACKAGE)			.addActionListener(m_TreeBuilder);
m_Model.getProperties().getPM(PM_SHOW_DEPRECATED)		.addActionListener(m_TreeBuilder);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	unregister
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	unregister()
{
if(m_Model != null)
	{
	// disconnect LIST tasks from the property managers
	m_Model.getProperties().getPM(PM_USE_SHORT_NAMES)		.removeActionListener(m_ListSorter);

	m_Model.getProperties().getPM(PI_SHOW_CLASSES)			.removeActionListener(m_ListBuilder);
	m_Model.getProperties().getPM(PI_SHOW_INTERFACES)		.removeActionListener(m_ListBuilder);
	m_Model.getProperties().getPM(PI_SHOW_NESTED)			.removeActionListener(m_ListBuilder);
	m_Model.getProperties().getPM(PI_SHOW_FIELDS)			.removeActionListener(m_ListBuilder);
	m_Model.getProperties().getPM(PI_SHOW_CONSTRUCTORS)		.removeActionListener(m_ListBuilder);
	m_Model.getProperties().getPM(PI_SHOW_METHODS)			.removeActionListener(m_ListBuilder);
	m_Model.getProperties().getPM(PI_SHOW_PUBLIC)			.removeActionListener(m_ListBuilder);
	m_Model.getProperties().getPM(PI_SHOW_PROTECTED)		.removeActionListener(m_ListBuilder);
	m_Model.getProperties().getPM(PI_SHOW_PRIVATE)			.removeActionListener(m_ListBuilder);
	m_Model.getProperties().getPM(PI_SHOW_PACKAGE)			.removeActionListener(m_ListBuilder);
	m_Model.getProperties().getPM(PM_SHOW_DEPRECATED)		.removeActionListener(m_ListBuilder);

	m_Model.getProperties().getPM(PM_SHOW_ACCESS_SYMBOLS)	.removeActionListener(m_ListPainter);
// NOT IMPLEMENTED	m_Model.getProperties().getPM(PM_HILITE_OVERR_METHODS)	.removeActionListener(m_ListPainter);
	m_Model.getProperties().getPM(PM_COLORIZE_PACKAGES)		.removeActionListener(m_ListPainter);
	m_Model.getProperties().getPM(PM_PACKAGE_COLORS)		.removeActionListener(m_ListPainter);

	// disconnect TREE tasks from the property managers
	m_Model.getProperties().getPM(PM_USE_SHORT_NAMES)		.removeActionListener(m_TreePainter);
	m_Model.getProperties().getPM(PM_SHOW_ACCESS_SYMBOLS)	.removeActionListener(m_TreePainter);
// NOT IMPLEMENTED	m_Model.getProperties().getPM(PM_HILITE_OVERR_METHODS)	.removeActionListener(m_TreePainter);

	m_Model.getProperties().getPM(PD_SHOW_NESTED)			.removeActionListener(m_TreeBuilder);
	m_Model.getProperties().getPM(PD_SHOW_FIELDS)			.removeActionListener(m_TreeBuilder);
	m_Model.getProperties().getPM(PD_SHOW_CONSTRUCTORS)		.removeActionListener(m_TreeBuilder);
	m_Model.getProperties().getPM(PD_SHOW_METHODS)			.removeActionListener(m_TreeBuilder);
	m_Model.getProperties().getPM(PD_SHOW_PUBLIC)			.removeActionListener(m_TreeBuilder);
	m_Model.getProperties().getPM(PD_SHOW_PROTECTED)		.removeActionListener(m_TreeBuilder);
	m_Model.getProperties().getPM(PD_SHOW_PRIVATE)			.removeActionListener(m_TreeBuilder);
	m_Model.getProperties().getPM(PD_SHOW_PACKAGE)			.removeActionListener(m_TreeBuilder);
	m_Model.getProperties().getPM(PM_SHOW_DEPRECATED)		.removeActionListener(m_TreeBuilder);
	}

super.unregister();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	selectionChanged [implements SelectionListener]
------------------------------------------------------------------------------------------------------------------------------------*/
public void	selectionChanged(SelectionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "selection changed, source: " + inEvt.getSource());

updateSelection();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	updateContent
------------------------------------------------------------------------------------------------------------------------------------*/
public void		updateContent()
{
buildList();
updateSelection();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	updateSelection
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	updateSelection()
{
super.updateSelection();
updateIndexSelection();
buildTree(false);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	buildList
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	buildList()
{
IndexModel		im = (IndexModel)m_List.getModel();

if(m_Model != null)	im.setContent(m_Model.getElementsIterator(), m_Model.getProperties());
else				im.reset();

updateIndexSelection();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	buildTree
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	buildTree(boolean inForceRebuild)
{
DefaultTreeModel		tm	 = (DefaultTreeModel)m_Tree.getModel();
MElement				e	 = (m_Model != null ? m_Model.getSelectionModel().getElement() : null);
DefaultMutableTreeNode	root = (e != null ? e.getTreeNode() : m_NullNode);

// check if we need to rebuild the tree
if(inForceRebuild || tm.getRoot() != root)
	tm.setRoot(root);

// update the selection
if(tm.getRoot() != m_NullNode)
	updateMembersSelection();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	updateIndexSelection
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	updateIndexSelection()
{
IndexModel		im	= (IndexModel)m_List.getModel();
Object			obj = (m_Model != null ? m_Model.getSelectionModel().getObject() : null);

if(im.hasElement(obj))
	{
	if(obj != m_List.getSelectedValue())
		{
		m_IsListAutoSelection = true;
		m_List.setSelectedValue(obj, true);
		}
	}
else
	{
	if(!m_List.isSelectionEmpty())
		{
		m_IsListAutoSelection = true;
		m_List.clearSelection();
		}
	}

if(m_List.isSelectionEmpty())
	m_List.scrollRectToVisible(UNIT_RECTANGLE);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	updateTree
------------------------------------------------------------------------------------------------------------------------------------*/
public void		updateTree()
{
if(!isShowing())	// don't waste time
	return;

// I spent more hours than I like to remember to find out that this apparently
// useless piece of code seems to be the only simple way to update the content
// of a JTree without completely rebuilding it while avoiding incorrect redraws.
// In brief, no matter what you do to the size of the JLabels which constitute
// the cells, they are always resized and clipped to what the LayoutCache
// mantained by BasicTreeUI thinks should be their size.
// So if you increase the text length in a node, each JLabel correctly modifies
// its preferred size, but BasicTreeUI  ignores this and tells the CellRendererPane
// (which holds the cells in place of the JTree for performance reasons) to use the
// cached values, thus truncating the displayed text.

final BasicTreeUI		btui = (BasicTreeUI)m_Tree.getUI();

btui.setLeftChildIndent(btui.getLeftChildIndent());

// But wait, there's more! This is not enough to convince the tree to update also
// the root node (and still happens if you rebuild the entire tree, so I suppose
// that only a completely new Model would work). The root node gets updated the
// second time. So here it is another trick... altough the display flickers.

final Runnable pleaseUpdateTheRootNodeToo = new Runnable()
	{
	public void run()	{ btui.setLeftChildIndent(btui.getLeftChildIndent()); }
	};

SwingUtilities.invokeLater(pleaseUpdateTheRootNodeToo);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	updateMembersSelection
------------------------------------------------------------------------------------------------------------------------------------*/
protected void			updateMembersSelection()
{
DefaultTreeModel		dtm		= (DefaultTreeModel)m_Tree.getModel();
DefaultMutableTreeNode	currSel = (DefaultMutableTreeNode)m_Tree.getLastSelectedPathComponent();
MMember					member	= (m_Model != null ? m_Model.getSelectionModel().getMember() : null);

if(member != null)					// if a member is selected, select its leaf
	{
	if(member.getTreeNode() != currSel)
		{
		TreePath	tp = new TreePath(dtm.getPathToRoot(member.getTreeNode()));

		m_IsTreeAutoSelection = true;
		m_Tree.setSelectionPath(tp);
		m_Tree.scrollPathToVisible(tp);
		}
	}
else if(dtm.getRoot() != currSel)	// if no member is selected, select the root
	{
	m_IsTreeAutoSelection = true;
	m_Tree.setSelectionRow(0);
	m_Tree.scrollRowToVisible(0);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	positionList
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	positionList()
{
IndexModel		elm		= (IndexModel)m_List.getModel();
int				index	= elm.indexOfItem(m_PositionField.getText());

Rectangle		cellBounds = m_List.getCellBounds(index, index);

if(cellBounds != null)
	{
	m_List.computeVisibleRect(m_PositionRect);

	m_PositionRect.x = cellBounds.x;
	m_PositionRect.y = cellBounds.y;

	m_List.scrollRectToVisible(m_PositionRect);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	LSelectionListener [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class LSelectionListener implements ListSelectionListener
{
public void	valueChanged(ListSelectionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

if(!inEvt.getValueIsAdjusting())
	{
	if(m_IsListAutoSelection)
		{
		m_IsListAutoSelection = false;
		}
	else if(m_Model != null)
		{
		JList	list	= (JList)inEvt.getSource();
		Object	obj		= list.getSelectedValue();

		m_Model.getSelectionModel().select(obj, true);
		}
	}
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	TSelectionListener [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class TSelectionListener implements TreeSelectionListener
{
public void	valueChanged(TreeSelectionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

if(m_IsTreeAutoSelection)
	{
	m_IsTreeAutoSelection = false;
	}
else if(m_Model != null)
	{
	JTree					tree = (JTree)inEvt.getSource();
	DefaultMutableTreeNode	node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

	if(node != null)
		m_Model.getSelectionModel().select(node.getUserObject(), true);
	}
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	LMouseListener [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class LMouseListener extends MouseAdapter
{
public void	mouseClicked(MouseEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

if(m_Model != null)
	{
	int		index = m_List.locationToIndex(inEvt.getPoint());

	if(index == -1)
		return;

	if(!m_List.isSelectedIndex(index))
		m_List.setSelectedIndex(index);

	Object	obj = m_List.getSelectedValue();

	m_Model.getSelectionModel().select(obj, true);
	}
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	TMouseListener [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class TMouseListener extends MouseAdapter
{
public void	mouseClicked(MouseEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

if(m_Model != null)
	{
	TreePath	path = m_Tree.getPathForLocation(inEvt.getX(), inEvt.getY());

	if(path == null)
		return;

	if(!m_Tree.isPathSelected(path))
		m_Tree.setSelectionPath(path);

	DefaultMutableTreeNode	node = (DefaultMutableTreeNode)m_Tree.getLastSelectedPathComponent();

	if(node != null)
		m_Model.getSelectionModel().select(node.getUserObject(), true);
	}
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	LSorter [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class LSorter implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

IndexModel		im = (IndexModel)m_List.getModel();

im.sort();
updateSelection();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	LBuilder [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class LBuilder implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

buildList();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	LUpdater [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class LUpdater implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

IndexModel		im = (IndexModel)m_List.getModel();
im.regenerate();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	LPainter [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class LPainter implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

m_List.repaint();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	TBuilder [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class TBuilder implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

buildTree(true);
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	TPainter [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class TPainter implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
if(DEBUG)
	Log.log(Log.DEBUG, this, "source: " + inEvt.getSource());

updateTree();
}
}
}
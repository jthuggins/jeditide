/*
 * BufferTabsPlugin.java - Main class of the BufferTabs plugin for jEdit.
 * Copyright (C) 1999, 2000 Jason Ginchereau
 * Copyright (C) 2000, 2001, 2002, 2003 Andre Kaplan
 * Copyright (C) 2003 Kris Kopicki
 * Copyright (C) 2008 Matthieu Casanova
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


package buffertabs;


import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.visitors.JEditVisitorAdapter;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Jason Ginchereau
 * @author Andre Kaplan
 * @author Matthieu Casanova
 */
public class BufferTabsPlugin extends EBPlugin
{
	private static Map<EditPane, BufferTabs> tabsMap = new Hashtable<EditPane, BufferTabs>();

	private static JPopupMenu popupMenu;

	@Override
	public void handleMessage(EBMessage msg)
	{
		if (msg instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate) msg;

			if (epu.getWhat() == EditPaneUpdate.CREATED)
			{
				editPaneCreated(epu.getEditPane());
			}
			else if (epu.getWhat() == EditPaneUpdate.DESTROYED)
			{
				editPaneDestroyed(epu.getEditPane());
			}
		}
		else if (msg instanceof PropertiesChanged)
		{
			propertiesChanged();
		}

	}

	@Override
	public void stop()
	{
		jEdit.visit(new JEditVisitorAdapter()
		{
			@Override
			public void visit(EditPane editPane)
			{
				editPaneDestroyed(editPane);
			}
		});
	}

	@Override
	public void start()
	{
		jEdit.visit(new JEditVisitorAdapter()
		{
			@Override
			public void visit(EditPane editPane)
			{
				editPaneCreated(editPane);
			}
		});
	}

	private static void editPaneCreated(EditPane editPane)
	{
		addBufferTabsToEditPane(editPane);
	}


	private static void editPaneDestroyed(EditPane editPane)
	{
		removeBufferTabsFromEditPane(editPane);
	}

	private static void propertiesChanged()
	{
		jEdit.visit(new JEditVisitorAdapter()
		{
			@Override
			public void visit(EditPane editPane)
			{
			}
		});
	}

	public static BufferTabs getBufferTabsForEditPane(EditPane editPane)
	{
		return editPane != null ? tabsMap.get(editPane) : null;
	}

	private static void addBufferTabsToEditPane(EditPane editPane)
	{
		Component ta = editPane.getTextArea();
		Container container = ta.getParent();

		BufferTabs tabs = new BufferTabs(editPane);
		tabs.setTabPlacement(JTabbedPane.TOP);
		tabs.start();
		container.add(tabs);
		tabsMap.put(editPane, tabs);
	}

	private static void removeBufferTabsFromEditPane(EditPane editPane)
	{
		Component ta = editPane.getTextArea();
		BufferTabs tabs = getBufferTabsForEditPane(editPane);

		if (tabs == null)
		{
			return;
		}

		tabs.stop();
		Container container = tabs.getParent();
		container.remove(tabs);
		container.add(ta);
		tabsMap.remove(editPane);
		editPane.getBufferSet().removeBufferSetListener(tabs);
		while (container != null && !(container instanceof JComponent))
		{
			container = container.getParent();
		}
		if (container != null)
		{
			((JComponent) container).revalidate();
		}
	}

	protected static JPopupMenu getRightClickPopup()
	{
		if (popupMenu == null)
			return popupMenu = GUIUtilities.loadPopupMenu("buffertabs.popup");
		else
			return popupMenu;
	}
}


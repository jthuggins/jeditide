/*
 * JExplorerDialog.java
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

import java.awt.BorderLayout;
import java.awt.Component;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

import jexplorer.JExplorerPlugin;
/*------------------------------------------------------------------------------------------------------------------------------------
	JExplorerDialog
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * A base class for all the JExplorer plugin dialogs.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public abstract class JExplorerDialog extends JDialog
{
protected String		m_PropertiesPrefix;

protected JPanel		m_ContentPanel;
protected JPanel		m_ButtonsPanel;

protected JButton		m_OK_Btn;
protected JButton		m_Cancel_Btn;

private ButtonsListener	m_ButtonsListener = new ButtonsListener();
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public JExplorerDialog(	String		inPropertiesPrefix,
						Component	inParentComponent,
						boolean		inModal)
{
super(	JOptionPane.getFrameForComponent(inParentComponent),
		jEdit.getProperty(inPropertiesPrefix + ".title"),
		inModal);

m_PropertiesPrefix = inPropertiesPrefix;

addWindowListener(new WindowAdapter()
	{
	// closing the dialog with the close button is equivalent to press 'Cancel'
	public void windowClosing(WindowEvent inEvt)
		{ doCancel(); }
	});

addKeyListener(new KeyHandler());

// create content panel

m_ContentPanel = new JPanel(new BorderLayout());
m_ContentPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
getContentPane().add(BorderLayout.CENTER, m_ContentPanel);

// create buttons panel

m_ButtonsPanel = new JPanel();
m_ButtonsPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
getContentPane().add(BorderLayout.SOUTH, m_ButtonsPanel);

// create standard buttons

m_OK_Btn = new JButton();
m_OK_Btn.addActionListener(m_ButtonsListener);
m_OK_Btn.setFont(JExplorerPlugin.getUIFont());
m_OK_Btn.setText(jEdit.getProperty("common.ok"));

m_Cancel_Btn = new JButton();
m_Cancel_Btn.addActionListener(m_ButtonsListener);
m_Cancel_Btn.setFont(JExplorerPlugin.getUIFont());
m_Cancel_Btn.setText(jEdit.getProperty("common.cancel"));

getRootPane().setDefaultButton(m_OK_Btn);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doOK
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doOK()
{
GUIUtilities.saveGeometry(this, m_PropertiesPrefix);
dispose();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	doCancel
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	doCancel()
{
GUIUtilities.saveGeometry(this, m_PropertiesPrefix);
dispose();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	ButtonsListener [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class	ButtonsListener implements ActionListener
{
public void		actionPerformed(ActionEvent evt)
{
	 if(evt.getSource() == m_OK_Btn)		doOK();
else if(evt.getSource() == m_Cancel_Btn)	doCancel();
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	KeyHandler [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
private class KeyHandler extends KeyAdapter
{
public void keyPressed(KeyEvent evt)
{
if(evt.isConsumed())
	return;

if(evt.getKeyCode() == KeyEvent.VK_ENTER)
	{
	evt.consume();
	m_OK_Btn.doClick();
	}
else if(evt.getKeyCode() == KeyEvent.VK_ESCAPE)
	{
	evt.consume();
	m_Cancel_Btn.doClick();
	}
}
}
}

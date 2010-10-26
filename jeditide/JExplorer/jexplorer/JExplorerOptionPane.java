/*
 * JExplorerOptionPane.java
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

package jexplorer;

import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JSlider;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;

import org.gjt.sp.jedit.gui.FontSelector;
import org.gjt.sp.jedit.gui.ColorWellButton;
/*------------------------------------------------------------------------------------------------------------------------------------
	JExplorerOptionPane
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* This is the option pane that jEdit displays for JExplorer's options.
 *
 * @author	Amedeo Farello
 * @version	0.3, 2003.05.25
*/
public class JExplorerOptionPane
	extends AbstractOptionPane
	implements JExplorerConstants
{
private FontSelector		m_FontStandard;
private FontSelector		m_FontAbstract;
private FontSelector		m_FontFinal;

private ColorWellButton		m_ColorClasses;
private ColorWellButton		m_ColorInterfaces;
private ColorWellButton		m_ColorInclusion;
private ColorWellButton		m_ColorFields;
private ColorWellButton		m_ColorConstructors;
private ColorWellButton		m_ColorMethods;

private JSlider				m_ParserDelay;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public JExplorerOptionPane()
{
super("jexplorer");
}
/*------------------------------------------------------------------------------------------------------------------------------------
	_init
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	_init()
{
addComponent(new JLabel(jEdit.getProperty("jexplorer.ui.options.fonts.title")));
addSeparator();

// standard font
addComponent(
	jEdit.getProperty("jexplorer.ui.options.fontStandard.label"),
	m_FontStandard = new FontSelector(
		jEdit.getFontProperty(FONT_STANDARD)));

m_FontStandard.setToolTipText(
	jEdit.getProperty("jexplorer.ui.options.fontStandard.tooltip"));

// 'abstract' font
addComponent(
	jEdit.getProperty("jexplorer.ui.options.fontAbstract.label"),
	m_FontAbstract = new FontSelector(
		jEdit.getFontProperty(FONT_ABSTRACT)));

m_FontAbstract.setToolTipText(
	jEdit.getProperty("jexplorer.ui.options.fontAbstract.tooltip"));

// 'final' font
addComponent(
	jEdit.getProperty("jexplorer.ui.options.fontFinal.label"),
	m_FontFinal = new FontSelector(
		jEdit.getFontProperty(FONT_FINAL)));

m_FontFinal.setToolTipText(
	jEdit.getProperty("jexplorer.ui.options.fontFinal.tooltip"));

addComponent(Box.createVerticalStrut(20));
addComponent(new JLabel(jEdit.getProperty("jexplorer.ui.options.colors.title")));
addSeparator();

// classes and derivation relationships color
addComponent(
	jEdit.getProperty("jexplorer.ui.options.colorClasses.label"),
	m_ColorClasses = new ColorWellButton(
		jEdit.getColorProperty(COLOR_CLASS)));

m_ColorClasses.setToolTipText(
	jEdit.getProperty("jexplorer.ui.options.colorClasses.tooltip"));

// interfaces and implementation relationships color
addComponent(
	jEdit.getProperty("jexplorer.ui.options.colorInterfaces.label"),
	m_ColorInterfaces = new ColorWellButton(
		jEdit.getColorProperty(COLOR_INTERFACE)));

m_ColorInterfaces.setToolTipText(
	jEdit.getProperty("jexplorer.ui.options.colorInterfaces.tooltip"));

// nesting relationships color
addComponent(
	jEdit.getProperty("jexplorer.ui.options.colorInclusion.label"),
	m_ColorInclusion = new ColorWellButton(
		jEdit.getColorProperty(COLOR_INCLUSION)));

m_ColorInclusion.setToolTipText(
	jEdit.getProperty("jexplorer.ui.options.colorInclusion.tooltip"));

// fields color
addComponent(
	jEdit.getProperty("jexplorer.ui.options.colorFields.label"),
	m_ColorFields = new ColorWellButton(
		jEdit.getColorProperty(COLOR_FIELD)));

m_ColorFields.setToolTipText(
	jEdit.getProperty("jexplorer.ui.options.colorFields.tooltip"));

// constructors color
addComponent(
	jEdit.getProperty("jexplorer.ui.options.colorConstructors.label"),
	m_ColorConstructors = new ColorWellButton(
		jEdit.getColorProperty(COLOR_CONSTRUCTOR)));

m_ColorConstructors.setToolTipText(
	jEdit.getProperty("jexplorer.ui.options.colorConstructors.tooltip"));

// methods color
addComponent(
	jEdit.getProperty("jexplorer.ui.options.colorMethods.label"),
	m_ColorMethods = new ColorWellButton(
		jEdit.getColorProperty(COLOR_METHOD)));

m_ColorMethods.setToolTipText(
	jEdit.getProperty("jexplorer.ui.options.colorMethods.tooltip"));

addComponent(Box.createVerticalStrut(20));
addComponent(new JLabel(jEdit.getProperty("jexplorer.ui.options.models.title")));
addSeparator();

// rebuilding delay
addComponent(
	jEdit.getProperty("jexplorer.ui.options.modelsDelay.label"),
	m_ParserDelay = new JSlider(
		500, 3000, jEdit.getIntegerProperty(PARSE_ON_KEYSTROKE_DELAY, 1500)));

Hashtable	labelTable = new Hashtable();

for(int i = 500; i <= 3000; i += 500)
	labelTable.put(new Integer(i), new JLabel(String.valueOf((double)i / 1000.0)));

m_ParserDelay.setLabelTable(labelTable);
m_ParserDelay.setPaintLabels(true);
m_ParserDelay.setMajorTickSpacing(500);
m_ParserDelay.setPaintTicks(true);

m_ParserDelay.setToolTipText(
	jEdit.getProperty("jexplorer.ui.options.modelsDelay.tooltip"));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	_save
------------------------------------------------------------------------------------------------------------------------------------*/
protected void	_save()
{
jEdit.setFontProperty(FONT_STANDARD,	m_FontStandard.getFont());
jEdit.setFontProperty(FONT_ABSTRACT,	m_FontAbstract.getFont());
jEdit.setFontProperty(FONT_FINAL,		m_FontFinal.getFont());

jEdit.setColorProperty(COLOR_CLASS,			m_ColorClasses.getSelectedColor());
jEdit.setColorProperty(COLOR_INTERFACE,		m_ColorInterfaces.getSelectedColor());
jEdit.setColorProperty(COLOR_INCLUSION,		m_ColorInclusion.getSelectedColor());
jEdit.setColorProperty(COLOR_FIELD,			m_ColorFields.getSelectedColor());
jEdit.setColorProperty(COLOR_CONSTRUCTOR,	m_ColorConstructors.getSelectedColor());
jEdit.setColorProperty(COLOR_METHOD,		m_ColorMethods.getSelectedColor());

jEdit.setIntegerProperty(PARSE_ON_KEYSTROKE_DELAY, m_ParserDelay.getValue());
}
}

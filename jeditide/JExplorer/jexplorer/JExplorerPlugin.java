/*
 * JExplorerPlugin.java
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

package jexplorer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.UIManager;

import java.util.Vector;

import org.gjt.sp.util.Log;

import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.jedit.msg.PropertiesChanged;

import jexplorer.model.ModelManager;
/*------------------------------------------------------------------------------------------------------------------------------------
	JExplorerPlugin
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Main class for the JExplorerPlugin.
 *
 * @author	Amedeo Farello
 * @version	0.3, 2003.05.25
 */
public class JExplorerPlugin
	extends EBPlugin
	implements JExplorerConstants
{
static private boolean		s_PropertiesUpToDate = false;

// common colors
static private Color		s_ColorClasses,
							s_ColorInterfaces,
							s_ColorInclusion,
							s_ColorFields,
							s_ColorConstructors,
							s_ColorMethods;
// NOT IMPLEMENTED			s_ColorOverriding;

// common fonts
static private Font			s_FontStandard,
							s_FontAbstract,
							s_FontFinal,
							s_UIFontListBold;

// common font properties
static private FontMetrics	s_FontMetrics_Standard,
							s_FontMetrics_Abstract,
							s_FontMetrics_Final;

static private int			s_FontBaselineStandard	= 0,
							s_FontBaselineAbstract	= 0,
							s_FontBaselineFinal		= 0,
							s_FontUnderlineStandard	= 0,
							s_FontUnderlineAbstract	= 0,
							s_FontUnderlineFinal	= 0;

static private int			s_ParseOnKeystrokeDelay;
/*------------------------------------------------------------------------------------------------------------------------------------
	compact methods
------------------------------------------------------------------------------------------------------------------------------------*/
static public Font	getUIFont()			{ return UIManager.getFont("Tree.font"); }
static public Font	getUIFontList()		{ return UIManager.getFont("Label.font"); }
static public Font	getUIFontListBold()	{ return s_UIFontListBold; }
/*------------------------------------------------------------------------------------------------------------------------------------
	start
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Method called by jEdit to initialize the plugin.
 */
public void		start()
{
s_UIFontListBold = getUIFontList().deriveFont(Font.BOLD);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	stop
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Method called by jEdit to terminate the plugin.
 */
public void		stop()
{
ModelManager.getInstance().terminate();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	createMenuItems
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* Method called every time a view is created to set up the Plugins menu.
*/
public void		createMenuItems(Vector inMenuItems)
{
inMenuItems.addElement(GUIUtilities.loadMenu("jexplorer-menu"));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	createOptionPanes
------------------------------------------------------------------------------------------------------------------------------------*/
/**
* Method called every time the plugin options dialog box is displayed.
*/
public void		createOptionPanes(OptionsDialog inOptionsDialog)
{
inOptionsDialog.addOptionPane(new JExplorerOptionPane());
}
/*------------------------------------------------------------------------------------------------------------------------------------
	handleMessage [implements EBComponent]
------------------------------------------------------------------------------------------------------------------------------------*/
public void		handleMessage(EBMessage inMessage)
{
// when properties change, invalidate the current settings
if(inMessage instanceof PropertiesChanged)
	{
	s_PropertiesUpToDate = false;
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	propertiesChanged
------------------------------------------------------------------------------------------------------------------------------------*/
static private void		propertiesChanged()
{
// this test is necessary since we need a View to compute font properties
// but jEdit sends the first PropertiesChanged message before Views are created

if(jEdit.getFirstView() != null)
	{
	// build colors table
	s_ColorClasses		= jEdit.getColorProperty(COLOR_CLASS);
	s_ColorInterfaces	= jEdit.getColorProperty(COLOR_INTERFACE);
	s_ColorInclusion	= jEdit.getColorProperty(COLOR_INCLUSION);
	s_ColorFields		= jEdit.getColorProperty(COLOR_FIELD);
	s_ColorConstructors	= jEdit.getColorProperty(COLOR_CONSTRUCTOR);
	s_ColorMethods		= jEdit.getColorProperty(COLOR_METHOD);
// NOT IMPLEMENTED	s_ColorOverriding	= jEdit.getColorProperty(COLOR_OVERRIDING);

	// build fonts table
	s_FontStandard		= jEdit.getFontProperty(FONT_STANDARD);
	s_FontAbstract		= jEdit.getFontProperty(FONT_ABSTRACT);
	s_FontFinal			= jEdit.getFontProperty(FONT_FINAL);

	// compute font properties

	// standard font properties
	s_FontMetrics_Standard	= jEdit.getFirstView().getFontMetrics(s_FontStandard);
	s_FontBaselineStandard	= s_FontMetrics_Standard.getAscent();
	s_FontUnderlineStandard	= s_FontMetrics_Standard.getHeight() - s_FontMetrics_Standard.getLeading() / 2;

	// abstract font properties
	s_FontMetrics_Abstract	= jEdit.getFirstView().getFontMetrics(s_FontAbstract);
	s_FontBaselineAbstract	= s_FontMetrics_Abstract.getAscent();
	s_FontUnderlineAbstract	= s_FontMetrics_Abstract.getHeight() - s_FontMetrics_Abstract.getLeading() / 2;

	// final font properties
	s_FontMetrics_Final		= jEdit.getFirstView().getFontMetrics(s_FontFinal);
	s_FontBaselineFinal		= s_FontMetrics_Final.getAscent();
	s_FontUnderlineFinal	= s_FontMetrics_Final.getHeight() - s_FontMetrics_Final.getLeading() / 2;

	// parse-on-keystroke delay
	s_ParseOnKeystrokeDelay	= jEdit.getIntegerProperty(PARSE_ON_KEYSTROKE_DELAY, 1500);

	s_PropertiesUpToDate = true;
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getColor
------------------------------------------------------------------------------------------------------------------------------------*/
static public Color		getColor(String inName)
{
if(!s_PropertiesUpToDate)
	propertiesChanged();

	 if(inName.equals(COLOR_CLASS))			return(s_ColorClasses);
else if(inName.equals(COLOR_INTERFACE))		return(s_ColorInterfaces);
else if(inName.equals(COLOR_INCLUSION))		return(s_ColorInclusion);
else if(inName.equals(COLOR_FIELD))			return(s_ColorFields);
else if(inName.equals(COLOR_CONSTRUCTOR))	return(s_ColorConstructors);
else if(inName.equals(COLOR_METHOD))		return(s_ColorMethods);
// NOT IMPLEMENTED else if(inName.equals(COLOR_OVERRIDING))	return(s_ColorOverriding);

Log.log(Log.ERROR, JExplorerPlugin.class, "Bad color name request: " + inName);

return(s_ColorClasses);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getFont
------------------------------------------------------------------------------------------------------------------------------------*/
static public Font		getFont(String inName)
{
if(!s_PropertiesUpToDate)
	propertiesChanged();

	 if(inName.equals(FONT_STANDARD))	return(s_FontStandard);
else if(inName.equals(FONT_ABSTRACT))	return(s_FontAbstract);
else if(inName.equals(FONT_FINAL))		return(s_FontFinal);

Log.log(Log.ERROR, JExplorerPlugin.class, "Bad font name request: " + inName);

return(s_FontStandard);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getFontMetrics
------------------------------------------------------------------------------------------------------------------------------------*/
static public FontMetrics	getFontMetrics(Font inFont)
{
if(!s_PropertiesUpToDate)
	propertiesChanged();

	 if(inFont.equals(s_FontStandard))	return(s_FontMetrics_Standard);
else if(inFont.equals(s_FontAbstract))	return(s_FontMetrics_Abstract);
else if(inFont.equals(s_FontFinal))		return(s_FontMetrics_Final);

Log.log(Log.ERROR, JExplorerPlugin.class, "Bad font metrics request");

return(s_FontMetrics_Standard);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getFontAttribute
------------------------------------------------------------------------------------------------------------------------------------*/
static public int		getFontAttribute(int inWhat)
{
if(!s_PropertiesUpToDate)
	propertiesChanged();

switch(inWhat)
	{
	case FONT_NORMAL_BASELINE:		return(s_FontBaselineStandard);
	case FONT_ABSTRACT_BASELINE:	return(s_FontBaselineAbstract);
	case FONT_FINAL_BASELINE:		return(s_FontBaselineFinal);
	case FONT_NORMAL_UNDERLINE:		return(s_FontUnderlineStandard);
	case FONT_ABSTRACT_UNDERLINE:	return(s_FontUnderlineAbstract);
	case FONT_FINAL_UNDERLINE:		return(s_FontUnderlineFinal);

	default:
		Log.log(Log.ERROR, JExplorerPlugin.class, "Bad font property request: " + Integer.toString(inWhat));
		return(0);
	}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getParseOnKeystrokeDelay
------------------------------------------------------------------------------------------------------------------------------------*/
static public int		getParseOnKeystrokeDelay()
{
if(!s_PropertiesUpToDate)
	propertiesChanged();

return(s_ParseOnKeystrokeDelay);
}
}

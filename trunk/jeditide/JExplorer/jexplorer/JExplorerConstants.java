/*
 * JExplorerConstants.java
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

import java.awt.Rectangle;
import java.awt.Insets;

import javax.swing.ImageIcon;
/*------------------------------------------------------------------------------------------------------------------------------------
	JExplorerConstants
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Global constants for the JExplorerPlugin.
 *
 * @author	Amedeo Farello
 * @version	0.2, 2003.05.10
 */
public interface JExplorerConstants
{
// declaration modifiers

short		MOD_NONE				= 0x0000,
			MOD_ABSTRACT			= 0x0001,
			MOD_FINAL				= 0x0002,
			MOD_STATIC				= 0x0004,
			MOD_NATIVE				= 0x0008,
			MOD_SYNCHRONIZED		= 0x0010,
			MOD_TRANSIENT			= 0x0020,
			MOD_VOLATILE			= 0x0040,
			MOD_OVERRIDES			= 0x0080,
			MOD_THROWS				= 0x0100,
			MOD_DEPRECATED			= 0x0200;

// access specifiers

byte		ACCESS_UNKNOWN			= 0,
			ACCESS_PUBLIC			= 1,
			ACCESS_PROTECTED		= 2,
			ACCESS_PRIVATE			= 3,
			ACCESS_PACKAGE			= 4;

// graph layout types

int			LAYOUT_DERIVATION		= 0,
			LAYOUT_IMPLEMENTATION	= 1,
			LAYOUT_INCLUSION		= 2;

// relatives inclusion

int			RELATIVES_SUPER			= 0,
			RELATIVES_ALL			= 1,
			RELATIVES_SUB			= 2;

// horizontal location values

int			LOCATION_NULL			= -1;

// vertical span values

int			VSPAN_NULL				= -1;

// drawing constants

int			DRAWING_TEXT_HMARGIN	=  4,	// horizontal space between the element's name and frame
			DRAWING_TEXT_VMARGIN	=  1,	// vertical space between the element's name and frame
			DRAWING_BOX_SPACING		=  6;	// element border size

// font properties

int			FONT_NORMAL_BASELINE	= 0,
			FONT_ABSTRACT_BASELINE	= 1,
			FONT_FINAL_BASELINE		= 2,
			FONT_NORMAL_UNDERLINE	= 3,
			FONT_ABSTRACT_UNDERLINE	= 4,
			FONT_FINAL_UNDERLINE	= 5;

// useful constants

Rectangle	UNIT_RECTANGLE		= new Rectangle(0, 0, 1, 1);
Insets		BUTTONS_MARGINS		= new Insets(0, 0, 0, 0);

// icons

ImageIcon	ICON_BLANK			= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/blank.png")),

			ICON_CLASS			= new ImageIcon(JExplorerConstants.class.getResource("/images/class.png")),
			ICON_INTERFACE		= new ImageIcon(JExplorerConstants.class.getResource("/images/interface.png")),
			ICON_NESTEDCLASS	= new ImageIcon(JExplorerConstants.class.getResource("/images/nestedClass.png")),
			ICON_NESTEDINTERFACE= new ImageIcon(JExplorerConstants.class.getResource("/images/nestedInterface.png")),
			ICON_FIELD			= new ImageIcon(JExplorerConstants.class.getResource("/images/field.png")),
			ICON_CONSTRUCTOR	= new ImageIcon(JExplorerConstants.class.getResource("/images/constructor.png")),
			ICON_METHOD			= new ImageIcon(JExplorerConstants.class.getResource("/images/method.png")),

			ICON_PUBLIC			= new ImageIcon(JExplorerConstants.class.getResource("/images/public.png")),
			ICON_PROTECTED		= new ImageIcon(JExplorerConstants.class.getResource("/images/protected.png")),
			ICON_PRIVATE		= new ImageIcon(JExplorerConstants.class.getResource("/images/private.png")),
			ICON_PACKAGE		= new ImageIcon(JExplorerConstants.class.getResource("/images/package.png")),

			ICON_THROWS			= new ImageIcon(JExplorerConstants.class.getResource("/images/throws.png")),
			ICON_DEPRECATED		= new ImageIcon(JExplorerConstants.class.getResource("/images/deprecated.png")),
			ICON_NATIVE			= new ImageIcon(JExplorerConstants.class.getResource("/images/native.png")),
			ICON_TRANSIENT		= new ImageIcon(JExplorerConstants.class.getResource("/images/transient.png")),
			ICON_SYNCHRONIZED	= new ImageIcon(JExplorerConstants.class.getResource("/images/synchronized.png")),
			ICON_VOLATILE		= new ImageIcon(JExplorerConstants.class.getResource("/images/volatile.png")),

			ICON_MODEL				= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/model.png")),
			ICON_MODEL_RB_CHANGE	= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/rebuildOnChange.png")),
			ICON_MODEL_RB_SAVE		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/rebuildOnSave.png")),
			ICON_MODEL_RB_NEVER		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/rebuildNever.png")),
			ICON_MODEL_OBSOLETE		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/modelObsolete.png")),
			ICON_MODEL_PARSING		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/modelParsing.png")),
			ICON_MODEL_DEFECTIVE	= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/modelDefective.png")),
			ICON_MODEL_READY		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/modelReady.png")),

			ICON_BTN_SHORT_NAMES	= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/useShortNames.png")),
			ICON_BTN_SYMBOLS		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/showAccessSymbols.png")),
// NOT IMPLEMENTED			ICON_BTN_OVERRIDING		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/hiliteOverrMethods.png")),
			ICON_BTN_DEPRECATED		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/showDeprecated.png")),
			ICON_BTN_COLORIZE_PKG	= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/colorizePackages.png")),
			ICON_BTN_COLPKG_DIALOG	= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/openPackageDialog.png")),
			ICON_BTN_LIB_DIALOG		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/openLibrariesDialog.png")),

			ICON_BTN_PUBLIC			= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/showPublicAccess.png")),
			ICON_BTN_PROTECTED		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/showProtectedAccess.png")),
			ICON_BTN_PRIVATE		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/showPrivateAccess.png")),
			ICON_BTN_PACKAGE		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/showPackageAccess.png")),

			ICON_BTN_SEL_CENTER		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/selCenter.png")),
			ICON_BTN_SEL_FILTER		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/selApplyFilter.png")),
			ICON_BTN_CONN_RELEVANT	= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/connShowRelevantOnly.png")),
			ICON_BTN_CONN_DERIV		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/connShowDerivation.png")),
			ICON_BTN_CONN_IMPL		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/connShowImplementation.png")),
			ICON_BTN_CONN_NEST		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/connShowNesting.png")),

			ICON_BTN_BACK			= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/back.png")),
			ICON_BTN_FORWARD		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/forward.png")),
			ICON_BTN_OPEN_POPUP		= new ImageIcon(JExplorerConstants.class.getResource("/images/buttons/openPopup.png"));

// managed properties names

String		// global - fonts
			FONT_STANDARD				= "jexplorer.fontStandard",
			FONT_ABSTRACT				= "jexplorer.fontAbstract",
			FONT_FINAL					= "jexplorer.fontFinal",

			// global - colors
			COLOR_CLASS					= "jexplorer.colorClasses",
			COLOR_INTERFACE				= "jexplorer.colorInterfaces",
			COLOR_INCLUSION				= "jexplorer.colorInclusion",
			COLOR_FIELD					= "jexplorer.colorFields",
			COLOR_CONSTRUCTOR			= "jexplorer.colorConstructors",
			COLOR_METHOD				= "jexplorer.colorMethods",
// NOT IMPLEMENTED			COLOR_OVERRIDING			= "jexplorer.colorOverriding",

			PARSE_ON_KEYSTROKE_DELAY	= "jexplorer.parseOnKeystrokeDelay",

			// model - elements visualization (common)
			PM_USE_SHORT_NAMES			= "jexplorer.useShortNames",
			PM_SHOW_ACCESS_SYMBOLS		= "jexplorer.showAccessSymbols",
// NOT IMPLEMENTED			PM_HILITE_OVERR_METHODS		= "jexplorer.hiliteOverrMethods",
			PM_SHOW_DEPRECATED			= "jexplorer.showDeprecated",
			PM_COLORIZE_PACKAGES		= "jexplorer.colorizePackages",
			PM_PACKAGE_COLORS			= "jexplorer.pkgcol",
			PM_LIBRARIES				= "jexplorer.library",

			// model - elements visualization (index panel)
			PI_SHOW_CLASSES				= "jexplorer.index.showClasses",
			PI_SHOW_INTERFACES			= "jexplorer.index.showInterfaces",
			PI_SHOW_NESTED				= "jexplorer.index.showNested",
			PI_SHOW_FIELDS				= "jexplorer.index.showFields",
			PI_SHOW_CONSTRUCTORS		= "jexplorer.index.showConstructors",
			PI_SHOW_METHODS				= "jexplorer.index.showMethods",
			PI_SHOW_PUBLIC				= "jexplorer.index.showPublicAccess",
			PI_SHOW_PROTECTED			= "jexplorer.index.showProtectedAccess",
			PI_SHOW_PRIVATE				= "jexplorer.index.showPrivateAccess",
			PI_SHOW_PACKAGE				= "jexplorer.index.showPackageAccess",

			// model - elements visualization (details panel)
			PD_SHOW_NESTED				= "jexplorer.detail.showNested",
			PD_SHOW_FIELDS				= "jexplorer.detail.showFields",
			PD_SHOW_CONSTRUCTORS		= "jexplorer.detail.showConstructors",
			PD_SHOW_METHODS				= "jexplorer.detail.showMethods",
			PD_SHOW_PUBLIC				= "jexplorer.detail.showPublicAccess",
			PD_SHOW_PROTECTED			= "jexplorer.detail.showProtectedAccess",
			PD_SHOW_PRIVATE				= "jexplorer.detail.showPrivateAccess",
			PD_SHOW_PACKAGE				= "jexplorer.detail.showPackageAccess",

			// model - graph layout
			PG_CURRENT_LAYOUT			= "jexplorer.graph.layout",

			// model - graph selection
			PG_CENTER_SELECTION			= "jexplorer.graph.sel.center",
			PG_FILTER_SELECTION			= "jexplorer.graph.sel.applyFilter",
			PG_CURRENT_RELATIVES		= "jexplorer.graph.sel.relatives",

			// model - graph connections visualization
			PG_CONN_SHOW_LAYOUT_ONLY	= "jexplorer.graph.conn.showRelevantOnly",
			PG_CONN_SHOW_DERIVATION		= "jexplorer.graph.conn.showDerivation",
			PG_CONN_SHOW_IMPLEMENTATION	= "jexplorer.graph.conn.showImplementation",
			PG_CONN_SHOW_INCLUSION		= "jexplorer.graph.conn.showNesting",

			// model - status (used only in memory, there's no corresponding persistent property)
			PM_MODEL_STATUS				= "jexplorer.modelStatus";
}

/*
 * ModelProperties.java
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

package jexplorer.model.properties;

import java.util.Hashtable;

import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.gjt.sp.util.Log;

import jexplorer.JExplorerPlugin;
import jexplorer.JExplorerConstants;

import jexplorer.model.Model;
import jexplorer.model.selection.SelectionListener;
import jexplorer.model.selection.SelectionEvent;
/*------------------------------------------------------------------------------------------------------------------------------------
	ModelProperties
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * Holds all the properties of a <code>Model</code>.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public class ModelProperties implements JExplorerConstants
{
private Model						m_Model;
private Hashtable					m_ActionsTable = new Hashtable();

// task handlers

private RelativesChoiceEnabler		m_RelativesChoiceEnabler	= new RelativesChoiceEnabler();
private ShowInnerOptionEnabler		m_ShowInnerOptionEnabler	= new ShowInnerOptionEnabler();
private ConnectionOptionsEnabler	m_ConnectionOptionsEnabler	= new ConnectionOptionsEnabler();

// layout options

private PMChoice					m_LayoutChoice;

// selection management

private PMOption					m_UseSelectionOption;
private PMOption					m_CenterSelectionOption;
private PMChoice					m_RelativesChoice;

// elements visualization

private PMOption					m_UseShortNamesOption;
private PMOption					m_ShowAccessSymbolsOption;
// NOT IMPLEMENTED private PMOption					m_HiliteOverrMethodsOption;
private PMOption					m_ShowDeprecatedOption;

private PMOption					m_IxShowClassOption;
private PMOption					m_IxShowInterfaceOption;
private PMOption					m_IxShowNestedOption;
private PMOption					m_IxShowFieldsOption;
private PMOption					m_IxShowConstructorsOption;
private PMOption					m_IxShowMethodsOption;
private PMOption					m_IxShowPublicOption;
private PMOption					m_IxShowProtectedOption;
private PMOption					m_IxShowPrivateOption;
private PMOption					m_IxShowPackageOption;

private PMOption					m_DtShowNestedOption;
private PMOption					m_DtShowFieldsOption;
private PMOption					m_DtShowConstructorsOption;
private PMOption					m_DtShowMethodsOption;
private PMOption					m_DtShowPublicOption;
private PMOption					m_DtShowProtectedOption;
private PMOption					m_DtShowPrivateOption;
private PMOption					m_DtShowPackageOption;

// connections visualization

private PMOption					m_ShowLayoutConnOption;
private PMOption					m_ShowDerConnOption;
private PMOption					m_ShowImpConnOption;
private PMOption					m_ShowIncConnOption;

// package colorization

private PMOption					m_ColPackagesOption;
private PMPackageColors				m_ColPackagesColors;

// files and errors management

private PMModelStatus				m_ModelStatus;
private PMLibraries					m_Libraries;
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public	ModelProperties(Model inModel)
{
m_Model = inModel;

m_UseShortNamesOption		= new PMOption(m_ActionsTable, m_Model, PM_USE_SHORT_NAMES);
m_ShowAccessSymbolsOption	= new PMOption(m_ActionsTable, m_Model, PM_SHOW_ACCESS_SYMBOLS);
// NOT IMPLEMENTED m_HiliteOverrMethodsOption	= new PMOption(m_ActionsTable, m_Model, PM_HILITE_OVERR_METHODS);
m_ShowDeprecatedOption		= new PMOption(m_ActionsTable, m_Model, PM_SHOW_DEPRECATED);

m_IxShowClassOption			= new PMOption(m_ActionsTable, m_Model, PI_SHOW_CLASSES);

m_IxShowClassOption.addActionListener(m_ShowInnerOptionEnabler);
m_IxShowClassOption.addActionListener(m_ConnectionOptionsEnabler);

m_IxShowInterfaceOption		= new PMOption(m_ActionsTable, m_Model, PI_SHOW_INTERFACES);

m_IxShowInterfaceOption.addActionListener(m_ShowInnerOptionEnabler);
m_IxShowInterfaceOption.addActionListener(m_ConnectionOptionsEnabler);

m_IxShowNestedOption		= new PMOption(m_ActionsTable, m_Model, PI_SHOW_NESTED);

m_IxShowNestedOption.addActionListener(m_ConnectionOptionsEnabler);

m_IxShowFieldsOption		= new PMOption(m_ActionsTable, m_Model, PI_SHOW_FIELDS);
m_IxShowConstructorsOption	= new PMOption(m_ActionsTable, m_Model, PI_SHOW_CONSTRUCTORS);
m_IxShowMethodsOption		= new PMOption(m_ActionsTable, m_Model, PI_SHOW_METHODS);
m_IxShowPublicOption		= new PMOption(m_ActionsTable, m_Model, PI_SHOW_PUBLIC);
m_IxShowProtectedOption		= new PMOption(m_ActionsTable, m_Model, PI_SHOW_PROTECTED);
m_IxShowPrivateOption		= new PMOption(m_ActionsTable, m_Model, PI_SHOW_PRIVATE);
m_IxShowPackageOption		= new PMOption(m_ActionsTable, m_Model, PI_SHOW_PACKAGE);

m_DtShowNestedOption		= new PMOption(m_ActionsTable, m_Model, PD_SHOW_NESTED);
m_DtShowFieldsOption		= new PMOption(m_ActionsTable, m_Model, PD_SHOW_FIELDS);
m_DtShowConstructorsOption	= new PMOption(m_ActionsTable, m_Model, PD_SHOW_CONSTRUCTORS);
m_DtShowMethodsOption		= new PMOption(m_ActionsTable, m_Model, PD_SHOW_METHODS);
m_DtShowPublicOption		= new PMOption(m_ActionsTable, m_Model, PD_SHOW_PUBLIC);
m_DtShowProtectedOption		= new PMOption(m_ActionsTable, m_Model, PD_SHOW_PROTECTED);
m_DtShowPrivateOption		= new PMOption(m_ActionsTable, m_Model, PD_SHOW_PRIVATE);
m_DtShowPackageOption		= new PMOption(m_ActionsTable, m_Model, PD_SHOW_PACKAGE);

m_LayoutChoice				= new PMChoice(m_ActionsTable, m_Model, PG_CURRENT_LAYOUT);
m_CenterSelectionOption		= new PMOption(m_ActionsTable, m_Model, PG_CENTER_SELECTION);
m_UseSelectionOption		= new PMOption(m_ActionsTable, m_Model, PG_FILTER_SELECTION);

m_UseSelectionOption.addActionListener(m_RelativesChoiceEnabler);

m_RelativesChoice			= new PMChoice(m_ActionsTable, m_Model, PG_CURRENT_RELATIVES);
m_ShowLayoutConnOption		= new PMOption(m_ActionsTable, m_Model, PG_CONN_SHOW_LAYOUT_ONLY);

m_ShowLayoutConnOption.addActionListener(m_ConnectionOptionsEnabler);

m_ShowDerConnOption			= new PMOption(m_ActionsTable, m_Model, PG_CONN_SHOW_DERIVATION);
m_ShowImpConnOption			= new PMOption(m_ActionsTable, m_Model, PG_CONN_SHOW_IMPLEMENTATION);
m_ShowIncConnOption			= new PMOption(m_ActionsTable, m_Model, PG_CONN_SHOW_INCLUSION);
m_ColPackagesOption			= new PMOption(m_ActionsTable, m_Model, PM_COLORIZE_PACKAGES);
m_ColPackagesColors			= new PMPackageColors(m_ActionsTable, m_Model, PM_PACKAGE_COLORS);

m_Libraries					= new PMLibraries(m_ActionsTable, m_Model, PM_LIBRARIES);
m_ModelStatus				= new PMModelStatus(m_ActionsTable, m_Model, PM_MODEL_STATUS);

// enable relatives combo box according to current selection
m_Model.getSelectionModel().addSelectionListener(m_RelativesChoiceEnabler);

initialize();
}
/*------------------------------------------------------------------------------------------------------------------------------------
	initialize
------------------------------------------------------------------------------------------------------------------------------------*/
void		initialize()
{
// note: this must be called after all user interface has been initialized

m_RelativesChoiceEnabler	.actionPerformed(null);
m_ConnectionOptionsEnabler	.actionPerformed(null);
m_ShowInnerOptionEnabler	.actionPerformed(null);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getPM
------------------------------------------------------------------------------------------------------------------------------------*/
public PropertyManager	getPM(String inName)
{
PropertyManager		pm = (PropertyManager)m_ActionsTable.get(inName);

if(pm == null)
	Log.log(Log.ERROR, JExplorerPlugin.class, "Property Manager not found: " + inName);

return(pm);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getOption
------------------------------------------------------------------------------------------------------------------------------------*/
public boolean			getOption(String inName)
{
PMOption	pm = (PMOption)getPM(inName);

if(pm != null)
	return(pm.getValue());

return(false);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getChoice
------------------------------------------------------------------------------------------------------------------------------------*/
public int				getChoice(String inName)
{
PMChoice	pm = (PMChoice)getPM(inName);

if(pm != null)
	return(pm.getValue());

return(-1);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getModelStatus
------------------------------------------------------------------------------------------------------------------------------------*/
public PMModelStatus	getModelStatus()
{
return((PMModelStatus)getPM(PM_MODEL_STATUS));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getLibraries
------------------------------------------------------------------------------------------------------------------------------------*/
public PMLibraries		getLibraries()
{
return((PMLibraries)getPM(PM_LIBRARIES));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getPackageColors
------------------------------------------------------------------------------------------------------------------------------------*/
public PMPackageColors	getPackageColors()
{
return((PMPackageColors)getPM(PM_PACKAGE_COLORS));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	getPackageColor
------------------------------------------------------------------------------------------------------------------------------------*/
public Color	getPackageColor(String inElementName)
{
PMPackageColors		pm = (PMPackageColors)getPM(PM_PACKAGE_COLORS);

return(pm.getElementColor(inElementName));
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isDerivationVisible
------------------------------------------------------------------------------------------------------------------------------------*/
public boolean	isDerivationVisible()
{
return	getOption(PG_CONN_SHOW_LAYOUT_ONLY) &&
		getChoice(PG_CURRENT_LAYOUT) == LAYOUT_DERIVATION
		||
		!getOption(PG_CONN_SHOW_LAYOUT_ONLY) &&
		getOption(PG_CONN_SHOW_DERIVATION);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isImplementationVisible
------------------------------------------------------------------------------------------------------------------------------------*/
public boolean	isImplementationVisible()
{
return	getOption(PG_CONN_SHOW_LAYOUT_ONLY) &&
			(
			getChoice(PG_CURRENT_LAYOUT) == LAYOUT_DERIVATION ||
			getChoice(PG_CURRENT_LAYOUT) == LAYOUT_IMPLEMENTATION
			)
		||
		!getOption(PG_CONN_SHOW_LAYOUT_ONLY) &&
		getOption(PG_CONN_SHOW_IMPLEMENTATION);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	isInclusionVisible
------------------------------------------------------------------------------------------------------------------------------------*/
public boolean	isInclusionVisible()
{
return	getOption(PG_CONN_SHOW_LAYOUT_ONLY) &&
		getChoice(PG_CURRENT_LAYOUT) == LAYOUT_INCLUSION
		||
		!getOption(PG_CONN_SHOW_LAYOUT_ONLY) &&
		getOption(PG_CONN_SHOW_INCLUSION);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	RelativesChoiceEnabler [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
class RelativesChoiceEnabler implements ActionListener, SelectionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
m_RelativesChoice.setEnabled(
	m_UseSelectionOption.getValue() &&
	m_Model.getSelectionModel().getElement() != null);
}
public void	selectionChanged(SelectionEvent inEvt)
{
m_RelativesChoice.setEnabled(
	m_UseSelectionOption.getValue() &&
	inEvt.getSelectedElement() != null);
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	ShowInnerOptionEnabler [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
class ShowInnerOptionEnabler implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
m_IxShowNestedOption.setEnabled(
	m_IxShowClassOption.getValue() ||
	m_IxShowInterfaceOption.getValue());
}
}
/*------------------------------------------------------------------------------------------------------------------------------------
	ConnectionOptionsEnabler [inner class]
------------------------------------------------------------------------------------------------------------------------------------*/
class ConnectionOptionsEnabler implements ActionListener
{
public void	actionPerformed(ActionEvent inEvt)
{
m_ShowDerConnOption.setEnabled(
	!m_ShowLayoutConnOption.getValue() &&
	(m_IxShowClassOption.getValue() || m_IxShowInterfaceOption.getValue()));

m_ShowImpConnOption.setEnabled(
	!m_ShowLayoutConnOption.getValue() &&
	m_IxShowInterfaceOption.getValue());

m_ShowIncConnOption.setEnabled(
	!m_ShowLayoutConnOption.getValue() &&
	m_IxShowNestedOption.getValue() &&
	(m_IxShowClassOption.getValue() || m_IxShowInterfaceOption.getValue()));
}
}
}

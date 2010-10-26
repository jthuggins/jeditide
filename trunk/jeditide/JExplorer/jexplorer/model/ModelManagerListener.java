/*
 * ModelManagerListener.java
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

package jexplorer.model;

import java.util.EventListener;
/*------------------------------------------------------------------------------------------------------------------------------------
	ModelManagerListener
------------------------------------------------------------------------------------------------------------------------------------*/
/** 
 * The listener that's notified when a ModelManager models change.
 * 
 * @author Amedeo Farello
 * @version 0.1, 2003.03.26
 */
public interface ModelManagerListener extends EventListener
{
/*------------------------------------------------------------------------------------------------------------------------------------
	modelAdded
------------------------------------------------------------------------------------------------------------------------------------*/
/** 
 * Called whenever a model is added.
 *
 * @param inEvt the event that characterizes the change.
 */
void	modelAdded(ModelManagerEvent inEvt);
/*------------------------------------------------------------------------------------------------------------------------------------
	modelRemoved
------------------------------------------------------------------------------------------------------------------------------------*/
/** 
 * Called whenever a model is removed.
 *
 * @param inEvt the event that characterizes the change.
 */
void	modelRemoved(ModelManagerEvent inEvt);
/*------------------------------------------------------------------------------------------------------------------------------------
	modelRenamed
------------------------------------------------------------------------------------------------------------------------------------*/
/** 
 * Called whenever a model is renamed.
 *
 * @param inEvt the event that characterizes the change.
 */
void	modelRenamed(ModelManagerEvent inEvt);
}

/*
 * ModelListener.java
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
import java.util.EventObject;
/*------------------------------------------------------------------------------------------------------------------------------------
	ModelListener
------------------------------------------------------------------------------------------------------------------------------------*/
/** 
 * The listener that's notified when a Model change.
 * 
 * @author Amedeo Farello
 * @version 0.1, 2003.03.26
 */
public interface ModelListener extends EventListener
{
/*------------------------------------------------------------------------------------------------------------------------------------
	modelParsing
------------------------------------------------------------------------------------------------------------------------------------*/
/** 
 * Called whenever a model is parsing.
 *
 * @param inEvt the event that characterizes the change.
 */
void	modelParsing(EventObject inEvt);
/*------------------------------------------------------------------------------------------------------------------------------------
	modelUpdated
------------------------------------------------------------------------------------------------------------------------------------*/
/** 
 * Called whenever a model is updated.
 *
 * @param inEvt the event that characterizes the change.
 */
void	modelUpdated(EventObject inEvt);
}

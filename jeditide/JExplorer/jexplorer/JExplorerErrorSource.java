/*
 * JExplorerErrorSource.java
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

import java.util.Vector;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.EditBus;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSourceUpdate;
/*------------------------------------------------------------------------------------------------------------------------------------
	JExplorerErrorSource
------------------------------------------------------------------------------------------------------------------------------------*/
/**
 * This class adds selective error removal to DefaultErrorSource.
 * Its code is simply copied and adapted from the upcoming ErrorList 1.3.
 *
 * @author	Amedeo Farello
 * @version	0.1, 2003.05.05
 */
public class JExplorerErrorSource extends DefaultErrorSource
{
/*------------------------------------------------------------------------------------------------------------------------------------
	<init>
------------------------------------------------------------------------------------------------------------------------------------*/
public JExplorerErrorSource(String name)
{
super(name);
}
/*------------------------------------------------------------------------------------------------------------------------------------
	removeFileErrors
------------------------------------------------------------------------------------------------------------------------------------*/
public synchronized void removeFileErrors(String path)
{
final Vector list = (Vector)errors.remove(path);

if(list == null)
	return;

errorCount -= list.size();
//removeOrAddToBus();

SwingUtilities.invokeLater(new Runnable()
	{
	public void run()
		{
		for(int i = 0; i < list.size(); i++)
			{
			DefaultError error = (DefaultError)list.get(i);

			ErrorSourceUpdate message = new ErrorSourceUpdate(
				JExplorerErrorSource.this,
				ErrorSourceUpdate.ERROR_REMOVED,
				JExplorerErrorSource.this,
				error);

			EditBus.send(message);
			}
		}
	});
}
}

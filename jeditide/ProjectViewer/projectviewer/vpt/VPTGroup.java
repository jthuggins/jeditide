/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package projectviewer.vpt;

//{{{ Imports
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.gjt.sp.jedit.GUIUtilities;
//}}}

/**
 *	A VPTGroup is a container for groups and projects.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 2.1.0
 */
public class VPTGroup extends VPTNode {

	//{{{ Constants
	private final static Icon groupIcon =
		new ImageIcon(VPTGroup.class.getResource("/projectviewer/images/vpt_group.png"));
	//}}}

	//{{{ +VPTGroup(String) : <init>
	public VPTGroup(String name) {
		super(name, true);
	} //}}}

	//{{{ +getIcon(boolean) : Icon
	/**
	 *	Returns the icon to be shown on the tree next to the node name.
	 *
	 *	@param	expanded	If the node is currently expanded or not.
	 */
	public Icon getIcon(boolean expanded) {
		return groupIcon;
	} //}}}

	//{{{ +getNodePath() : String
	/**	Returns the path to this group in the group tree. */
	public String getNodePath() {
		if (getParent() != null) {
			return ((VPTNode)getParent()).getNodePath() + getName();
		}
		return getName() + File.separator;
	} //}}}

	//{{{ +getChildPaths() : String
	/**	Returns the paths to this group's projects. */
	public String getChildPaths() {
        StringBuffer path = new StringBuffer("");
        lock(false);
        try{
            if(getAllowsChildren()) {
                for(int i = 0; i < getChildCount(); i++) {
                    VPTNode node = (VPTNode)getChildAt(i);
                    if(node.isGroup()) {
                        path.append(((VPTGroup)node).getChildPaths());
                    }else if(node.isProject()) {
                        path.append(node.getNodePath() + File.pathSeparator);
                    }
                }
            }
        } finally {
            unlock(false);
        }
        return path.toString();
	} //}}}

	//{{{ +compareTo(VPTNode) : int
	public int compareTo(VPTNode n)
	{
		if (!n.isGroup()) {
			return -1;
		} else if (n.isRoot()) {
			return 1;
		} else {
			return compareName(n);
		}
	} //}}}

	//{{{ +toString() : String
	public String toString() {
		return "VPTGroup [" + getName() + "]";
	} //}}}

}


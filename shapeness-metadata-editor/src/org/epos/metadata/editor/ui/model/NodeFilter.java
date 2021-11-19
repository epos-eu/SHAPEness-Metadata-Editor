/*******************************************************************************
 * <one line to give the program's name and a brief idea of what it does.>
 *     
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.epos.metadata.editor.ui.model;

import org.apache.commons.lang3.ClassUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.epos.metadata.editor.reflection.shapes.Node;

public class NodeFilter extends ViewerFilter {

	private Class category;
	
	public NodeFilter(Class category) {
		super();
		this.category = category;
	}

	
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {

        if (element instanceof Node) {
            Node node = (Node) element;
            
            try {
				if(ClassUtils.getClass(category.getTypeName()).isInstance(node)){
					return false;
				} else {
					return true;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

        }
        return true;
    }

}

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

import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.epos.metadata.editor.reflection.shapes.Node;

public class Connection implements Cloneable {
    
	private String id;
    private String label;
    private Node source;
    private Node destination;
    private EntityConnectionData entityConnectionData;

    public Connection(String id, String label, Node source, Node destination) {
        this.id = id;
        this.label = label;
        this.source = source;
        this.destination = destination;
    }

    public String getLabel() {
        return label;
    }

    public Node getSource() {
        return source;
    }
    public Node getDestination() {
        return destination;
    }

	public EntityConnectionData getEntityConnectionData() {
		return entityConnectionData;
	}

	public void setEntityConnectionData(EntityConnectionData entityConnectionData) {
		this.entityConnectionData = entityConnectionData;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public void setDestination(Node destination) {
		this.destination = destination;
	}
	
	public Object clone() throws CloneNotSupportedException{
	    return super.clone();
	}

}

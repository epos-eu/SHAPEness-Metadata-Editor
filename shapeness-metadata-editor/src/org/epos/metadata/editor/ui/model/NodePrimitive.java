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

import org.epos.metadata.editor.reflection.shapes.Node;

public class NodePrimitive extends Node{
	
	private Object valueRef;
	private String className;

	public NodePrimitive(String id, String name, String className, Object value) {
		super(id, name);
		this.className = className;
		this.valueRef = value;
		// TODO Auto-generated constructor stub
	}

	public Object getValueRef() {
		return valueRef;
	}

	public void setValueRef(Object value) {
		this.valueRef = value;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}

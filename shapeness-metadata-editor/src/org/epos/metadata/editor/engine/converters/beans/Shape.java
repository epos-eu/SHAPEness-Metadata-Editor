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
package org.epos.metadata.editor.engine.converters.beans;

import java.util.ArrayList;
import java.util.HashMap;

public class Shape {
	
	private String shapeID;
	private String shapeName;
	private ArrayList<_Class> types;
	private HashMap<String, ArrayList<Pair<String,Object>>> properties;
	
	public Shape() {
		this.types = new ArrayList<_Class>();
		this.properties = new HashMap<String, ArrayList<Pair<String,Object>>>();
	}
	
	
	public String getShapeID() {
		return shapeID;
	}


	public void setShapeID(String shapeID) {
		this.shapeID = shapeID;
	}


	public String getShapeName() {
		return shapeName;
	}
	public void setShapeName(String shapeName) {
		this.shapeName = shapeName;
	}
	public ArrayList<_Class> getTypes() {
		return types;
	}
	public void setTypes(ArrayList<_Class> types) {
		this.types = types;
	}
	public HashMap<String, ArrayList<Pair<String,Object>>> getProperties() {
		return properties;
	}


	public void setProperties(HashMap<String, ArrayList<Pair<String,Object>>> properties) {
		this.properties = properties;
	}


	@Override
	public String toString() {
		return "Shape [shapeName=" + shapeName + ", types=" + types + ", properties=" + properties + "]";
	}

	
}

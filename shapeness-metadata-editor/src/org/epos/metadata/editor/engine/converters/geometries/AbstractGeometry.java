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
package org.epos.metadata.editor.engine.converters.geometries;

import org.apache.jena.rdf.model.Literal;

public abstract class AbstractGeometry {

	private String geometryString;
	
	public AbstractGeometry(String geometryString) {
		this.geometryString = geometryString;
	}

	public String getGeometryString() {
		return geometryString;
	}

	public void setGeometryString(String geometryString) {
		this.geometryString = geometryString;
	}
	
	public abstract Literal toLiteral();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((geometryString == null) ? 0 : geometryString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractGeometry other = (AbstractGeometry) obj;
		if (geometryString == null) {
			if (other.geometryString != null)
				return false;
		} else if (!geometryString.equals(other.geometryString))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return geometryString;
	}
	
	
}

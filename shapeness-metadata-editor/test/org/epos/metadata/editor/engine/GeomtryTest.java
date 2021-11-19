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
package org.epos.metadata.editor.engine;

import org.epos.metadata.editor.engine.converters.geometries.GeometryWKT;

public class GeomtryTest {
	public static void main(String[] args) {
		GeometryWKT lwi = new GeometryWKT("POLYGON((10 10, 10 20, 20 10, 10 10))");
		//System.out.println(lwi.toLiteral().getLexicalForm());
		//System.out.println(lwi.toString());
	}
}

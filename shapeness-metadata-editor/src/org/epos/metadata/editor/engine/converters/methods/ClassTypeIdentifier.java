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
package org.epos.metadata.editor.engine.converters.methods;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.engine.converters.beans.ModelClass;
import org.epos.metadata.editor.engine.converters.beans.Shape;
import org.epos.metadata.editor.engine.converters.geometries.GeometryWKT;
import org.epos.metadata.editor.ui.utils.Variables;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

/**
 * 
 * This class provide a static method to catch a CtClass to be used in the field generation
 * 
 * @author valerio
 *
 */
public class ClassTypeIdentifier {

	private static Logger logger = Logger.getGlobal();

	public static CtClass getType(String type, ClassPool pool, ArrayList<CtClass> subClasses) throws NotFoundException {

		if(pool==null) pool = ClassPool.getDefault();
		CtClass typef = null;

		switch(type) {
		case "integer":
			typef = pool.get("java.lang.Integer");
			break;
		case "double":
			typef = pool.get("java.lang.Double");
			break;
		case "decimal":
			typef = pool.get("java.lang.Double");
			break;
		case "float":
			typef = pool.get("java.lang.Float");
			break;
		case "boolean":
			typef = pool.get("java.lang.Boolean");
			break;
		case "string":
			typef = pool.get("java.lang.String");
			break;
		case "anyURI":
			typef = pool.get("java.net.URI");
			break;
		case "date":
			typef = pool.get("org.apache.jena.datatypes.xsd.XSDDateTime");
			break;
		case "dateTime":
			typef = pool.get("org.apache.jena.datatypes.xsd.XSDDateTime");
			break;
		case "wktLiteral":
			typef = pool.get("org.epos.metadata.editor.engine.converters.geometries.GeometryWKT");
			break;
		default:
			if(type.equals("")) {
				typef = pool.get("java.lang.String");
			}else{
				if(pool.getOrNull(Variables.MODEL_PATH.getValue()+type)!=null) {
					typef = pool.get(Variables.MODEL_PATH.getValue()+type);
					return typef;
				}if(Character.isLowerCase(type.substring(0,1).charAt(0)) && pool.getOrNull(Variables.MODEL_PATH.getValue()+type.substring(0, 1).toUpperCase() + type.substring(1))!=null) {
					typef = pool.get(Variables.MODEL_PATH.getValue()+type.substring(0, 1).toUpperCase() + type.substring(1));
					return typef;
				}else {
					for(Shape sh : Engine.getIstance().getM2c().getSh()){
						if(sh.getShapeName().equals(type)) {
							pool.makeClass(Variables.MODEL_PATH.getValue()+type);
						}
					}
					if(pool.getOrNull(Variables.MODEL_PATH.getValue()+type)!=null) {
						typef = pool.get(Variables.MODEL_PATH.getValue()+type);
					}else {
						logger.info("Not found "+Variables.MODEL_PATH.getValue()+type+", trying to put String");
						typef = pool.get("java.lang.String");
					}
					return typef;
				}
			}
			break;
		}
		return typef;
	}


	public static Class<?> getClassType(String type) throws NotFoundException {

		switch(type) {
		case "integer":
			return Integer.class;
		case "double":
			return Double.class;
		case "decimal":
			return Double.class;
		case "float":
			return Float.class;
		case "boolean":
			return Boolean.class;
		case "string":
			return String.class;
		case "anyURI":
			return URI.class;
		case "date":
			return XSDDateTime.class;
		case "dateTime":
			return XSDDateTime.class;
		case "wktLiteral":
			return GeometryWKT.class;
		default:
			return String.class;
		} 
	}
}

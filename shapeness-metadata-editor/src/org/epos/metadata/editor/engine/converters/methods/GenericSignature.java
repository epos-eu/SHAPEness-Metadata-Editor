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

import java.util.List;

import javassist.CtClass;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.SignatureAttribute;

/**
 * 
 * This class implements a static method to generate a list of CtClasses
 * 
 * @author valerio
 *
 */
public class GenericSignature {

	public static String getGenericSignature(CtClass relatedClass) throws BadBytecode {
		String fieldSignature = "L" + List.class.getCanonicalName().replace(".", "/") + "<L" + relatedClass.getName().replace(".", "/") + ";>;";
		return  SignatureAttribute.toClassSignature(fieldSignature).encode();
	}

}

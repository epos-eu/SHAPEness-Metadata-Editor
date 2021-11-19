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

import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;

/**
 * 
 * This class provide a static method to generate a toString method to be injected into generated class.
 * The toString method is an override of the superclass toString and provide all fields information of the class
 * 
 * @author valerio
 *
 */
public class ToString {

	private static Logger logger = Logger.getGlobal();
	
	public static void generateToString(CtClass cc) {

		CtMethod m;
		try {
			m = CtNewMethod.make("public java.lang.String toString() {\n" + 
					"        // Generate toString including transient and static fields.\n" + 
					"        return org.apache.commons.lang3.builder.ReflectionToStringBuilder.toString(this,\n" + 
					"            org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE, true, true);\n" + 
					"    }", cc);

			MethodInfo methodInfoGetEid = m.getMethodInfo();
			ConstPool cp = methodInfoGetEid.getConstPool();
			Annotation annotationNew = new Annotation("Override", cp);
			AnnotationsAttribute attributeNew = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);  
			attributeNew.addAnnotation(annotationNew);
			m.getMethodInfo().addAttribute(attributeNew);
			cc.addMethod(m);

		} catch (CannotCompileException e) {
			logger.severe(e.getLocalizedMessage());
		}
	}
}

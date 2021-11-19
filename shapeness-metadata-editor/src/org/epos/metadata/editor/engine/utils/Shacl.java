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
package org.epos.metadata.editor.engine.utils;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 
 * Interface Shacl used for shacl annotation on classes
 * 
 * @author valerio
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ShaclAnnotationValidator.class)
@Documented
public @interface Shacl {
	public String term();
    public String vocabulary();
    public String shortVocabulary();
	public String constraint() default "Optional";
	public String altVocabulary();	
	
	String message() default "{org.epos.metadata.editor.engine.utils.Shacl}"; 
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

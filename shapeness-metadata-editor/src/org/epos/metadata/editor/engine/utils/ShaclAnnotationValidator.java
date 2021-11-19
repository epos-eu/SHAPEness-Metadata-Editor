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

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ShaclAnnotationValidator implements ConstraintValidator<Shacl, Object> {

	private String constraint;

	@Override
	public void initialize(Shacl shacl) {
		constraint = shacl.constraint();
	}

	@Override
	public boolean isValid(Object contactField, ConstraintValidatorContext cxt) {
	/*	//System.out.println(contactField+" "+constraint);
		//System.out.println(contactField+" "+Optional.ofNullable(contactField));
		//System.out.println(contactField+" "+Optional.ofNullable(contactField).isEmpty());
		//System.out.println(contactField+" "+Optional.ofNullable(contactField).isEmpty());*/
		if(contactField!=null) //System.out.println(contactField.toString());

		if(constraint.equals("Mandatory")) {
			if(Optional.ofNullable(contactField).isEmpty() 
					|| ( !Optional.ofNullable(contactField).isEmpty() && contactField instanceof List &&  ((List) contactField).size()==0)) 
			{
				cxt.disableDefaultConstraintViolation();
				cxt
				.buildConstraintViolationWithTemplate( "{Invalid.Mandatory}"  )
				.addConstraintViolation();
				return false;
			}
		}

		if(constraint.equals("Recommended")) {
			if(Optional.ofNullable(contactField).isEmpty()
					|| ( !Optional.ofNullable(contactField).isEmpty() && contactField instanceof List &&  ((List) contactField).size()==0)) {
				cxt.disableDefaultConstraintViolation();
				cxt
				.buildConstraintViolationWithTemplate( "{Invalid.Recommended}"  )
				.addConstraintViolation();
				return false;
			}
		}
		

		return true;
	}

}

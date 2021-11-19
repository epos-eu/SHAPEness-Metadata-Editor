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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.RIOT;
import org.epos.metadata.editor.engine.converters.Classes2Model;
import org.epos.metadata.editor.engine.converters.Model2Classes;
import org.epos.metadata.editor.engine.converters.beans.ModelClass;
import org.epos.metadata.editor.engine.io.ModelReader;
import org.epos.metadata.editor.engine.io.FileReader;
import org.epos.metadata.editor.engine.io.FileWriter;
import org.epos.metadata.editor.engine.model.ModelStored;
import org.epos.metadata.editor.engine.utils.ClassWriter;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.DataManager;

/**
 * Hello world!
 *
 */
public class ReadFileTest{
	public static void main(String[] args) throws IOException {
		String shapes = "https://raw.githubusercontent.com/epos-eu/EPOS-DCAT-AP/EPOS-DCAT-AP-shapes/epos-dcat-ap_shapes.ttl";

		new Engine.EngineBuilder()
					.setModelReader(new ModelReader(shapes))
					.setModel2Classes(new Model2Classes())
					.setClasses2Model(new Classes2Model())
					.setModelStored(new ModelStored())
					.setFileWriter(new FileWriter())
					.setFileReader(new FileReader())
					.build();
		
	/*	Engine.getIstance().getM2c().getClassesGenerated().forEach(cg -> {
			ClassWriter.writerClassOnFile(cg);
		});
		Engine.getIstance().getM2c().getSubClassesGenerated().forEach(cg -> {
			ClassWriter.writerClassOnFile(cg);
		});
*/
		Engine.getIstance().getFreader().readFromURL("https://raw.githubusercontent.com/epos-eu/EPOS-DCAT-AP/EPOS-DCAT-AP-shapes/examples/EPOS-DCAT-AP_metadata_template.ttl");
		Engine.getIstance().getC2m().generateResources();
		//Engine.getIstance().getWriter().writeModelToFile("pippo", Lang.TTL, Engine.getIstance().getC2m().getOutputOntModel());
		//System.out.println(Engine.getIstance().getWriter().writeModelToString(Lang.TURTLE, Engine.getIstance().getC2m().getOutputOntModel()));
		RDFWriter.create()
	     .set(RIOT.symTurtleDirectiveStyle, "sparql")
	     .lang(Lang.TTL)
	     .source(Engine.getIstance().getC2m().getOutputOntModel())
	     .output(System.out);
		Engine.getIstance().getC2m().getOutputOntModel().write(System.out, "TTL");
		
	}

}

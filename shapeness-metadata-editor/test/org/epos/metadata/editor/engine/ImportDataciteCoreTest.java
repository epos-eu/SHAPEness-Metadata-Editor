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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.jena.riot.Lang;
import org.epos.metadata.editor.engine.converters.Classes2Model;
import org.epos.metadata.editor.engine.converters.DataciteConverter;
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
public class ImportDataciteCoreTest{
	public static void main(String[] args) throws URISyntaxException {
		String shapes = "https://raw.githubusercontent.com/epos-eu/EPOS-DCAT-AP/EPOS-DCAT-AP-shapes/epos-dcat-ap_draft_update.ttl";

		Engine e = null;
		try {
			e = new Engine.EngineBuilder()
					.setModelReader(new ModelReader(shapes))
					.setModel2Classes(new Model2Classes())
					.setClasses2Model(new Classes2Model())
					.setModelStored(new ModelStored())
					.setFileWriter(new FileWriter())
					.setFileReader(new FileReader())
					.build();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		List<Class<?>> list = e.getM2c().getClasses();
		list.addAll(e.getM2c().getSubClasses());

		Node node = null;

		for(Class<?> c : list) {
			if(c.getName().contains("Dataset")) {
				try {					
					Constructor<?> constructor = c.getDeclaredConstructor(String.class, String.class);
					node = (Node) constructor.newInstance(UUID.randomUUID().toString(), c.getSimpleName());
					DataManager.getIstance().getNodes().add(node);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		//System.out.println("USING DOI: 10.6092/ingv.it-ahead");
		DataciteConverter.getInstance().setFolderLocation(".");
		DataciteConverter.getInstance().setSchemaLocation(new File("resources/datacite-to-epos-dcat-ap.xsl").toURI());
		try {
			DataciteConverter.getInstance().generateFromDataciteAddress("	");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
		//System.out.println("Try to get fields");

		DataManager.getIstance().getNodes().forEach(n->{
			//System.out.println(n.toString());
		});

		

		
	}

}

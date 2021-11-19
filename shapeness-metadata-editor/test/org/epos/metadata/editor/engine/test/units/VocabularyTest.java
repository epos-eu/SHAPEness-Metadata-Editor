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
/**
 * 
 */
package org.epos.metadata.editor.engine.test.units;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.engine.VocabularyStore;
import org.epos.metadata.editor.engine.converters.Classes2Model;
import org.epos.metadata.editor.engine.converters.Model2Classes;
import org.epos.metadata.editor.engine.io.FileReader;
import org.epos.metadata.editor.engine.io.FileWriter;
import org.epos.metadata.editor.engine.io.ModelReader;
import org.epos.metadata.editor.engine.model.ModelStored;
import org.epos.metadata.editor.engine.test.beans.Person;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.DataManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



/**
 * @author valerio
 *
 */
public class VocabularyTest {

	private static List<Class<?>> list;
	static Logger log = Logger.getLogger(VocabularyTest.class.getName());

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		log.setUseParentHandlers(false);

		ConsoleHandler handler = new ConsoleHandler();

		Formatter formatter = new LogFormatter();
		handler.setFormatter(formatter);        

		log.addHandler(handler);



		String shapes = "https://raw.githubusercontent.com/epos-eu/EPOS-DCAT-AP/EPOS-DCAT-AP-shapes/epos-dcat-ap_draft_update.ttl";
		log.info("Reading file "+shapes);

		Engine e = new Engine.EngineBuilder()
				.setModelReader(new ModelReader(shapes))
				.setModel2Classes(new Model2Classes())
				.setClasses2Model(new Classes2Model())
				.setModelStored(new ModelStored())
				.setFileWriter(new FileWriter())
				.setFileReader(new FileReader())
				.setVocabularyStore(new VocabularyStore(null))
				.build();


		log.info("File read correctly");

		list = Engine.getIstance().getM2c().getClasses();
		list.addAll(Engine.getIstance().getM2c().getSubClasses());


		log.info("List of classes created correctly");
		Engine.getIstance().getVocStore().setFolder("vocabularies/");
		Engine.getIstance().getVocStore().initializeStore(Engine.getIstance().getMs().getShaclModel().getNsPrefixMap());
	}

	/***
	 * 
	 * 
	 * TEST ON EPOS-DCAT-AP -> PERSON
	 * 
	 * 
	 */

	@Test
	public void CheckSchemaAddressDefinition() {//schema:address
		
		String schema_address = "schema:address";
		String class_ = "Person";
		log.info("Result for address in Person: "+Engine.getIstance().getVocStore().searchDescriptionOrCommentByString(class_, schema_address));
	}

	
	@Test
	public void CheckContactPointDefinition() {//schema:address
		
		String schema_address = "contactPoint";
		String class_ = "Organization";
		log.info("Result for contactPoint in Organization: "+Engine.getIstance().getVocStore().searchDescriptionOrCommentByString(class_, schema_address));
	}
	
	@Test
	public void CheckLeiCodeDefinition() {//schema:address
		
		String schema_address = "schema:leiCode";
		String class_ = "Organization";
		log.info("Result for LeiCode in Organization: "+Engine.getIstance().getVocStore().searchDescriptionOrCommentByString(class_, schema_address));
	}
	
	@Test
	public void CheckContactTypeDefinition() {//schema:address
		
		String schema_address = "schema:contactType";
		String class_ = "ContactPoint";
		log.info("Result for contactType in ContactPoint: "+Engine.getIstance().getVocStore().searchDescriptionOrCommentByString(class_, schema_address));
	}
	
	@Test
	public void CheckTypeDistributionDefinition() {//schema:address
		
		String schema_address = "dct:type";
		String class_ = "Distribution";
		log.info("Result for type in Distribution: "+Engine.getIstance().getVocStore().searchDescriptionOrCommentByString(class_, schema_address));
	}
	
	@Test
	public void CheckTypeEquipmentDefinition() {//schema:address
		
		String schema_address = "dct:type";
		String class_ = "Equipment";
		log.info("Result for type in Equipment: "+Engine.getIstance().getVocStore().searchDescriptionOrCommentByString(class_, schema_address));
	}
	
	@Test
	public void CheckSerialNumberDefinition() {//schema:address
		
		String schema_address = "schema:serialNumber";
		String class_ = "Equipment";
		log.info("Result for serialNumber in Equipment: "+Engine.getIstance().getVocStore().searchDescriptionOrCommentByString(class_, schema_address));
	}
	
	@Test
	public void CheckManufacturerDefinition() {//schema:address
		
		String schema_address = "schema:manufacturer";
		String class_ = "Equipment";
		log.info("Result for manufacturer in Equipment: "+Engine.getIstance().getVocStore().searchDescriptionOrCommentByString(class_, schema_address));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDown() throws Exception {

	}

}

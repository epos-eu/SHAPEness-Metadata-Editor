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
public class ReadPersonTest {

	private static List<Class<?>> list;
	static Logger log = Logger.getLogger(ReadPersonTest.class.getName());



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
				.build();


		log.info("File read correctly");

		list = Engine.getIstance().getM2c().getClasses();
		list.addAll(Engine.getIstance().getM2c().getSubClasses());


		log.info("List of classes created correctly");
	}

	/***
	 * 
	 * 
	 * TEST ON EPOS-DCAT-AP -> PERSON
	 * 
	 * 
	 */

	@Test
	public void PersonFieldNumber() {
		for(Class<?> c : list) {
			if(c.getName().contains("Person")) {
				assertEquals(Person.class.getDeclaredFields().length,c.getDeclaredFields().length);
			}
		}
	}


	@Test
	public void PersonFieldName() throws NoSuchFieldException, SecurityException {
		for(Class<?> c : list) {
			if(c.getName().contains("Person")) {
				for(Field f : c.getDeclaredFields()) {
					assertNotNull(Person.class.getDeclaredField(f.getName()));
				}
			}
		}
	}

	@Test
	public void PersonFieldType() throws NoSuchFieldException, SecurityException  {
		for(Class<?> c : list) {
			if(c.getName().contains("Person")) {
				for(Field f : c.getDeclaredFields()) {
					assertEquals(Person.class.getDeclaredField(f.getName()).getType().getSimpleName(), f.getType().getSimpleName());
				}
			}
		}
	}

	@Test
	public void PersonFieldAnnotation() throws NoSuchFieldException, SecurityException {
		for(Class<?> c : list) {
			if(c.getName().contains("Person")) {
				for(Field f : c.getDeclaredFields()) {
					assertEquals(Person.class.getDeclaredField(f.getName()).getDeclaredAnnotations().length, f.getDeclaredAnnotations().length);
					for(Annotation n : f.getDeclaredAnnotations()) {
						assertTrue(Arrays.asList(Person.class.getDeclaredField(f.getName()).getDeclaredAnnotations()).contains(n));
					}

				}
			}
		}
	}

	@Test
	public void PersonFieldGettersName() throws NoSuchMethodException, SecurityException {
		for(Class<?> c : list) {
			if(c.getName().contains("Person")) {
				for(Method f : c.getDeclaredMethods()) {
					if(f.getName().startsWith("get"))
						assertEquals(Person.class.getDeclaredMethod(f.getName()).getName(), f.getName());
				}
			}
		}
	}

	@Test
	public void PersonFieldGettersType() throws NoSuchMethodException, SecurityException {
		for(Class<?> c : list) {
			if(c.getName().contains("Person")) {
				for(Method f : c.getDeclaredMethods()) {
					if(f.getName().startsWith("get"))
						assertEquals(Person.class.getDeclaredMethod(f.getName()).getDeclaringClass().getSimpleName(), f.getDeclaringClass().getSimpleName());
				}
			}
		}
	}


	@Test
	public void PersonFieldSettersName()throws NoSuchMethodException, SecurityException {
		for(Class<?> c : list) {
			if(c.getName().contains("Person")) {
				for(Method f : c.getDeclaredMethods()) {
					if(f.getName().startsWith("set"))
						assertTrue(Arrays.asList(Person.class.getDeclaredMethods()).stream().map(Method::getName).collect(Collectors.toList()).contains(f.getName()));
				}
			}
		}
	}

	@Test
	public void PersonFieldAddersType() throws NoSuchMethodException, SecurityException {
		for(Class<?> c : list) {
			if(c.getName().contains("Person")) {
				for(Method f : c.getDeclaredMethods()) {
					if(f.getName().startsWith("add")) {
						List<Class<?>> list = Arrays.asList(Person.class.getDeclaredMethods()).stream()
						.map(Method::getReturnType)
						.collect(Collectors.toList());
						assertTrue(list.stream().map(Class::getSimpleName).collect(Collectors.toList()).contains(f.getReturnType().getSimpleName()));
					}
				}
			}
		}
	}

	@Test
	public void PersonFieldAddersName() throws NoSuchMethodException, SecurityException {
		for(Class<?> c : list) {
			if(c.getName().contains("Person")) {
				for(Method f : c.getDeclaredMethods()) {
					if(f.getName().startsWith("add")) {
						assertTrue(Arrays.asList(Person.class.getDeclaredMethods()).stream().map(Method::getName).collect(Collectors.toList()).contains(f.getName()));
					}
				}
			}
		}
	}

	@Test
	public void PersonFieldSettersType() throws NoSuchMethodException, SecurityException {
		for(Class<?> c : list) {
			if(c.getName().contains("Person")) {
				for(Method f : c.getDeclaredMethods()) {
					if(f.getName().startsWith("set")) {
						List<Class<?>> list = Arrays.asList(Person.class.getDeclaredMethods()).stream()
						.map(Method::getReturnType)
						.collect(Collectors.toList());
						assertTrue(list.stream().map(Class::getSimpleName).collect(Collectors.toList()).contains(f.getReturnType().getSimpleName()));
					}
				}
			}
		}
	}

	@Test
	public void PersonMethodsParametersType() throws NoSuchMethodException, SecurityException {
		for(Class<?> c : list) {
			if(c.getName().contains("Person")) {
				Map<String,ArrayList<String>> methodParameters = new HashMap<String,ArrayList<String>>();
				for(Method f : c.getDeclaredMethods()) {
					List<String> received = Arrays.asList(f.getParameterTypes())
							.stream()
							.map(Class::getSimpleName)
							.collect(Collectors.toList());
					if(methodParameters.containsKey(f.getName())) methodParameters.get(f.getName()).addAll(received);
					else methodParameters.put(f.getName(), (ArrayList<String>) received);
				}
				Map<String,ArrayList<String>> methodParametersExpected = new HashMap<String,ArrayList<String>>();
						
				Arrays.asList(Person.class.getDeclaredMethods()).forEach(e->{
					List<String> expected = Arrays.asList(e.getParameterTypes())
					.stream()
					.map(Class::getSimpleName)
					.collect(Collectors.toList());

					if(methodParametersExpected.containsKey(e.getName())) methodParametersExpected.get(e.getName()).addAll(expected);
					else methodParametersExpected.put(e.getName(), (ArrayList<String>) expected);
				});
				
				for(Entry<String, ArrayList<String>> m : methodParameters.entrySet()){
					assertTrue(methodParametersExpected.containsKey(m.getKey()));
					assertTrue(methodParametersExpected.get(m.getKey()).containsAll(methodParameters.get(m.getKey())));
			    }
					

			}
		}
	}


	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDown() throws Exception {
		
		log.info("Printing object fields: ");
		for(Class<?> c : list) {
			if(c.getName().contains("Person")) {
				for(Field f : c.getDeclaredFields()) {
						log.warning(f.toString());
				}
			}
		}
		log.info("Printing object methods: ");
		for(Class<?> c : list) {
			if(c.getName().contains("Person")) {
				for(Method f : c.getDeclaredMethods()) {
						log.warning(f.toString()+"\n"+f.getName()+"\n"+Arrays.toString(f.getParameterTypes()));
				}
			}
		}
	}

}
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
package org.epos.metadata.editor.engine.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.Map.Entry;

import org.apache.commons.lang3.ClassUtils;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.engine.checkers.ShaclChecker;
import org.epos.metadata.editor.engine.converters.geometries.GeometryWKT;
import org.epos.metadata.editor.engine.converters.methods.ClassTypeIdentifier;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.Connection;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.utils.Variables;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.vocabulary.SH;

import javassist.NotFoundException;

public class FileReader {

	Logger logger = Logger.getGlobal();

	private ShaclChecker schk;

	public FileReader() {

		schk = new ShaclChecker();
	}


	public void read(String fileURI) throws IOException {

		logger.info("File reader initialization");
		try {
			readFromURL(fileURI);
		}catch(MalformedURLException mue) {
			readFromFile(fileURI);
		} 
	}

	//TODO: add generic read

	public void readFromURL(String fileURL) throws IOException {
		URL url = new URL(fileURL);
		InputStream in = null;
		try {
			in = url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Model inputFile = JenaUtil.createMemoryModel(); //MEMORY MODEL

		inputFile.read(in, SH.BASE_URI, FileUtils.langTurtle);


		if(schk.Validate(Engine.getIstance().getMs().getShaclModel() , inputFile)==true)
			createFilledClasses(inputFile);
		else {
			new Exception("File not conform against shacl model");
			createFilledClasses(inputFile);
		}
	}

	public void readFromFile(String filePath) throws FileNotFoundException {
		InputStream in = new FileInputStream(filePath);

		Model inputFile = JenaUtil.createMemoryModel(); //MEMORY MODEL

		inputFile.read(in, SH.BASE_URI, FileUtils.langTurtle);

		if(schk.Validate(Engine.getIstance().getMs().getShaclModel() , inputFile)==true)
			createFilledClasses(inputFile);
		else {
			new Exception("File not conform against shacl model");
			createFilledClasses(inputFile);
		}

	}
	
	public void readFromInputStream(InputStream filePath) throws FileNotFoundException {
		//InputStream in = new FileInputStream(filePath);

		Model inputFile = JenaUtil.createMemoryModel(); //MEMORY MODEL

		inputFile.read(filePath, SH.BASE_URI, FileUtils.langTurtle);

		if(schk.Validate(Engine.getIstance().getMs().getShaclModel() , inputFile)==true)
			createFilledClasses(inputFile);
		else {
			new Exception("File not conform against shacl model");
			createFilledClasses(inputFile);
		}

	}


	private void createFilledClasses(Model inputFile){

		HashMap<String,ArrayList<String[]>> compactNodes = new HashMap<String, ArrayList<String[]>>();
		inputFile.getGraph().find().forEachRemaining(n->{
			if(compactNodes.containsKey(n.getSubject().toString())) {
				compactNodes.get(n.getSubject().toString()).add(new String[]{n.getPredicate().toString(),n.getObject().toString()});
			}
			else {
				ArrayList<String[]> temp = new ArrayList<String[]>();
				temp.add(new String[]{n.getPredicate().toString(),n.getObject().toString()});
				compactNodes.put(n.getSubject().toString(), temp);
			}
		});

		try {
			createNodeObjects(compactNodes, inputFile.getNsPrefixMap());
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | URISyntaxException e1) {
			e1.printStackTrace();
		}
	}

	int i = 0;

	private String getClassType(HashMap<String,ArrayList<String[]>> compactNodes, Map<String, String> map, String key){

		////System.out.println("Expecting type: "+key+" on keyset: \n"+compactNodes.keySet().toString());
		String type = null;
		for(String[] s : compactNodes.get(key)) {
			////System.out.println("Item: "+Arrays.toString(s));
			if(s[0].contains(RDF.type.toString())) {
				for(String v : map.values()) {
					String temp = s[1].replace(v, "");
					for(org.topbraid.shacl.engine.Shape rs : Engine.getIstance().getMs().getShapegraph().getRootShapes()){
						if(rs.getShapeResource().getLocalName().equals(temp+"Shape"))
							type = temp;
					}
				}
			}
		}
		////System.out.println("Resulting type: "+type);
		return type;
	}

	public void createNodeObjects(HashMap<String,ArrayList<String[]>> compactNodes, Map<String, String> map) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException, InstantiationException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
		for(Entry<String, ArrayList<String[]>> e : compactNodes.entrySet()) {
			String id = e.getKey();
			ArrayList<String[]> props = e.getValue();


			String type = getClassType(compactNodes, map, id);
			i++;

			////System.out.println("NODE ID: "+id+" TYPE "+type);

			Node currentNode = null;
			if(DataManager.getIstance().getNodeById(id)!=null){
				currentNode = DataManager.getIstance().getNodeById(id);
			}
			else {
				try {
					Class<?> classItem = ClassUtils.getClass(Variables.MODEL_PATH.getValue()+type);
					Object[] intArgs = new Object[] { id, type};
					Class<?>[] intArgsClass = new Class[] { String.class, String.class };
					Constructor<?> constructor = classItem.getConstructor(intArgsClass);
					currentNode = (Node) constructor.newInstance(intArgs);

					DataManager.getIstance().getNodes().add(currentNode);
				}catch(ClassNotFoundException cnfe) {
					logger.info("Class not found: "+cnfe.getLocalizedMessage()+" on class "+type+" and "+cnfe.getMessage());
				}
			}
			System.out.println(currentNode.toString());

			//http://purl.org/dc/terms/type

			/*if(currentNode!=null) //System.out.println(currentNode.toString());
			else //System.out.println("missing "+e.getKey());*/

			if(currentNode!=null) {
				//call methods
				for(String[] s : props) {
					String[] values = s;
					String name = null;
					String value = null;
					Class<?> valueType = null;
					String ns = null;
					boolean modified = false;



					// CLEAN STRING
					for(String v : map.values()) {
						if(values[0].contains(v)) {
							ns = v;
							name = values[0].replace(v, "");
						}
						if(values[1].contains(v) && values[0].contains(RDF.type.toString())) {
							value = values[1].replace(v, "");
							modified = true;
						}
					}
					if(!modified) value = values[1];


					valueType = String.class;

					//VALUE CLEANUP
					value = value.replace("\"", "");

					name = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9]", "");

					//if(s[0].equals("http://purl.org/dc/terms/identifier"))
					//	//System.out.println("found identifier BEFORE !!!***** "+s[1]+" "+s[0]);
					//System.out.println("*** Checking what is wrong with that\nCompactNodes: "+compactNodes+"\nValue: "+value+"\nID:"+id);
					//CHECK VALUETYPE
					if(!id.equals(value) && compactNodes.containsKey(value)) {
						try {
							valueType = ClassUtils.getClass(Variables.MODEL_PATH.getValue()+getClassType(compactNodes, map, value));
							//System.out.println("After try, valueType: "+valueType);
						} catch (ClassNotFoundException e1) {
							valueType = ClassUtils.getClass("java.lang.String");
						}
					}

					if(value.contains("^^")) {
						System.out.println("VALUE: "+value);
						String[] subValues = value.split("\\^");
						System.out.println(subValues.length);
						System.out.println(Arrays.asList(subValues).toString());
						value = subValues[0];
						System.out.println("VALUE SPLIT: "+value);
						System.out.println("NAME VALUE: "+name);
						try {
							String valueTemp = subValues[2];
							for(String v : map.values()) {
								if(subValues[2].contains(v)) {
									valueTemp = subValues[2].replace(v, "");
								}
							}
							if(subValues[2].contains("http://www.w3.org/2001/XMLSchema"))
								valueTemp = subValues[2].split("#")[1];
							valueType = ClassTypeIdentifier.getClassType(valueTemp);
							//System.out.println("PREDICTED: "+valueType);
						} catch (NotFoundException e1) {
							e1.printStackTrace();
						}

					}

					////System.out.println(valueType+" "+value+" "+ ns);

					//System.out.println("VALUE OPS: "+value);
					//System.out.println("NAME OPS: "+name);

					if(!ns.equals(RDF.type.toString())) {
						for(Method m : currentNode.getClass().getDeclaredMethods()) {
							if((m.getName().contains("set") || m.getName().contains("add")) && m.getName().contains(name)) {
								m.setAccessible(true);
								Field f = currentNode.getClass().getDeclaredField(m.getName().substring(3,m.getName().length()));
								f.setAccessible(true);
								if(f.getType().equals(List.class)) {
									if(f.get(currentNode)==null) {
										f.set(currentNode, new ArrayList());
									}
								}
								boolean inserted = false;

								for(Class<?> p : m.getParameterTypes()) {
									//try {
										//System.out.println("Preparing:\n-  ValueType: "+valueType+"\n- Value: "+value+"\n-  ID: "+id+"\n- Item: "+Engine.getIstance().getMs().getShortNS(ns)+"_"+name);
										Object val = toObject(valueType,value,id, Engine.getIstance().getMs().getShortNS(ns)+"_"+name);
										//System.out.println("Method: "+m.toString()+" Field: "+f.toGenericString()+" value "+val.toString());
										//System.out.println("P: "+p.getName()+" valuetype: "+valueType.getName());
										if(p.equals(valueType)) {
											m.invoke(currentNode, val);
											inserted = true;
											//System.out.println("Printing method value: "+f.get(currentNode)+"\n\n\n");
										}
									/*}catch(Exception ex) {
										logger.severe(ex.getMessage());
										ex.printStackTrace();
									}*/
								}

								//System.out.println("*** checking current node value: "+f.get(currentNode)+"\n\n");
								if(!inserted && f.get(currentNode)==null) {
									//System.out.println("Not Inserted!!! ***");
									for(Class<?> p : m.getParameterTypes()) {
										if(p.equals(String.class)) {
											m.invoke(currentNode, value);
											//System.out.println("Printing method value: "+f.get(currentNode)+"\n\n\n");
										}
									}
								}
							}
						}
					}
				}
			}

			////System.out.println("MY BEAUTIFUL NODE: \n"+currentNode.toString());

		}
	}

	public static Object toObject( Class<?> clazz, String value, String nodeid, String prop ) throws ClassNotFoundException, URISyntaxException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		if( Boolean.class == clazz ) return Boolean.parseBoolean( value );
		if( Byte.class == clazz ) return Byte.parseByte( value );
		if( Short.class == clazz ) return Short.parseShort( value );
		if( Integer.class == clazz ) return Integer.parseInt( value );
		if( Long.class == clazz ) return Long.parseLong( value );
		if( Float.class == clazz ) return Float.parseFloat( value );
		if( Double.class == clazz ) return Double.parseDouble( value );
		if( URI.class == clazz ) return new URI( value );
		if( XSDDateTime.class == clazz ) {
			SimpleDateFormat objSDF = new SimpleDateFormat(parse(value));
			objSDF.setTimeZone(TimeZone.getTimeZone("GMT"));
			Calendar cal = null;
			try {
				cal = Calendar.getInstance();
				cal.setTime( objSDF.parse(value));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new XSDDateTime(cal);
		}
		if(GeometryWKT.class == clazz) {
			return new GeometryWKT(value);
		}
		if( clazz.getSuperclass() == Node.class ) {
			Node cx = null;
			//CHECK IF NODE EXISTS OTHERWISE CREATE A NEW ONE
			if(DataManager.getIstance().getNodeById(value)!=null){
				cx = DataManager.getIstance().getNodeById(value);
			}
			else {
				Class<?> classItem = ClassUtils.getClass(clazz.getName());
				Object[] intArgs = new Object[] { value, classItem.getSimpleName()};
				Class<?>[] intArgsClass = new Class[] { String.class, String.class };
				Constructor<?> constructor = classItem.getConstructor(intArgsClass);
				cx = (Node) constructor.newInstance(intArgs);
				DataManager.getIstance().getNodes().add(cx);
			}

			boolean connExists = false;

			for(Connection conn : DataManager.getIstance().getConnections()) {
				if(conn.getSource().getId().equals(nodeid) && conn.getDestination().getId().equals(value))
					connExists = true;
			}

			if(!connExists) {
				Connection connection = new Connection(nodeid, prop, DataManager.getIstance().getNodeById(nodeid), cx);
				DataManager.getIstance().getConnections().add(connection);
			}

			return cx;
		}
		return value;
	}

	private static final String[] formats = { 
			"yyyy-MM-dd'T'HH:mm:ss'Z'",  
			"yyyy-MM-dd'T'HH:mm:ssZ",
			"yyyy-MM-dd'T'HH:mm:ss",      
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ", 
			"yyyy-MM-dd HH:mm:ss", 
			"yyyy-MM-dd",
			"MM/dd/yyyy HH:mm:ss",        
			"MM/dd/yyyy'T'HH:mm:ss.SSS'Z'", 
			"MM/dd/yyyy'T'HH:mm:ss.SSSZ", 
			"MM/dd/yyyy'T'HH:mm:ss.SSS", 
			"MM/dd/yyyy'T'HH:mm:ssZ",     
			"MM/dd/yyyy'T'HH:mm:ss", 
			"yyyy:MM:dd HH:mm:ss",        
			"yyyyMMdd", 

	};

	public static String parse(String d) {
		if (d != null) {
			for (String parse : formats) {
				SimpleDateFormat sdf = new SimpleDateFormat(parse);
				try {
					sdf.parse(d);
					return parse;
				} catch (ParseException e) {

				}
			}
		}
		return d;
	}

}

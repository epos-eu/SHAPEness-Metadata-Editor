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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.jena.Jena;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.NsIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.thrift.wire.RDF_StreamRow;
import org.apache.jena.shared.Lock;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VCARD;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry;
import org.epos.metadata.editor.engine.utils.Shacl;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.utils.Variables;
import org.topbraid.jenax.util.JenaUtil;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;

/**
 * 
 * This class provide methods to create and manage a serialize method to be injected into generated classes.
 * The method serialize enaable the conversion from classes to model resources
 * 
 * @author valerio
 *
 */
public class Serializer {

	private Logger logger = Logger.getGlobal();

	private ClassPool pool = ClassPool.getDefault();

	private StringBuilder sb;
	private String instanceName;

	public Serializer(String instanceName) {
		this.sb = new StringBuilder();
		this.instanceName = instanceName;
	}


	/*public static void main(String[] args) {
		Model m = JenaUtil.createDefaultModel();
		Resource rstl = m.createResource("http://example/Person");
		rstl.addProperty(m.getProperty("http://example/name"), "gianni");

		Resource rstl2 = null;
		if(rstl2!=null) rstl.addProperty(m.getProperty("ciao"), rstl2);
	}*/

	public void addToSerializer(CtField field, int type, HashMap<String, ArrayList<String>> ns, String name) throws NotFoundException {
		switch(type){
		case 0:
			//SERIALIZER INIT
			sb.append("public org.apache.jena.rdf.model.Resource serialize(org.apache.jena.rdf.model.Model m, org.apache.jena.rdf.model.Model om, java.lang.String uri){\n");
			sb.append("org.apache.jena.rdf.model.Resource rstl = om.createResource(uri);\n");
			//sb.append("org.apache.jena.ontology.Individual individual = om.createIndividual(super.getId(),rstl);\n");
			sb.append("try{\n");
			break;
		case 1:
			//SERIALIZER SIMPLE
			sb.append("if(java.util.Optional.ofNullable("+field.getName()+").isPresent()){\n");
			if(field.getDeclaringClass() == pool.get("java.lang.Object") || field.getDeclaringClass().getSuperclass().equals(pool.get(Variables.MODEL_PATH_NODE.getValue()))) {
				sb.append("if("+instanceName+".class.isAssignableFrom("+field.getName()+".getClass())){ "
						+ "System.out.println(uri);"
						+ "System.out.println(\"Imma here 1\");"
						+ "rstl.addProperty(m.getProperty(\""+name+"\"),(("+instanceName+") "+field.getName()+").serialize(m, om, \""+name+"\"));"
						+ "System.out.println(\"Imma here 1 stop\");"
						+ "}\n");
				sb.append("else if("+AbstractGeometry.class.getName()+".class.isAssignableFrom("+field.getName()+".getClass())){ "
						+ "System.out.println(\"Imma here 2\");"
						+ "rstl.addProperty(m.getProperty(\""+name+"\"),(("+AbstractGeometry.class.getName()+") "+field.getName()+").toLiteral());"
						+ "System.out.println(\"Imma here 2 stop\");"
						+ "}\n");
				sb.append("else { rstl.addProperty(m.getProperty(\""+name+"\"),m.createTypedLiteral("+field.getName()+"));}");
			}
			else sb.append("rstl.addProperty(m.getProperty(\""+name+"\"),m.createTypedLiteral("+field.getName()+"));");
			sb.append("}\n");
			break;
		case 2:
			//SERIALIZER LIST
			sb.append("if(java.util.Optional.ofNullable("+field.getName()+").isPresent()){\n");
			sb.append("for(int i=0; i<"+field.getName()+".size();i++){\n");
			sb.append("if("+instanceName+".class.isAssignableFrom("+field.getName()+".get(i).getClass())){ "
					+ "System.out.println(uri);"
					+ "System.out.println(\"Imma here 3\");"
					+ "rstl.addProperty(m.getProperty(\""+name+"\"),(("+instanceName+") "+field.getName()+".get(i)).serialize(m, om, \""+name+"\"));"
					+ "System.out.println(\"Imma here 3 stop\");"
					+ "}\n");
			sb.append("else if("+AbstractGeometry.class.getName()+".class.isAssignableFrom("+field.getName()+".get(i).getClass())){ "
					+ "System.out.println(\"Imma here 4\");"
					+ "rstl.addProperty(m.getProperty(\""+name+"\"),(("+AbstractGeometry.class.getName()+") "+field.getName()+".get(i)).toLiteral());"
					+ "System.out.println(\"Imma here 4 stop\");"
					+ "}\n");
			sb.append("else rstl.addProperty(m.getProperty(\""+name+"\"),m.createTypedLiteral("+field.getName()+".get(i)));");
			sb.append("}\n");
			sb.append("}\n");
			break;

		case 3:
			sb.append("if(java.util.Optional.ofNullable("+field.getName()+").isPresent()){\n");
			if(field.getDeclaringClass() == pool.get("java.lang.Object") || field.getDeclaringClass().getSuperclass().equals(pool.get(Variables.MODEL_PATH_NODE.getValue()))) {
				sb.append("if("+instanceName+".class.isAssignableFrom("+field.getName()+".getClass())){ "
						+ "System.out.println(uri);"
						+ "System.out.println(\"Imma here 5\");"
						+ "rstl.addProperty(m.getProperty(\""+name+"\"),(("+instanceName+") "+field.getName()+").serialize(m, om, \""+name+"\"));"
						+ "System.out.println(\"Imma here 5 stop\");"
						+ "}\n");
				sb.append("else if("+AbstractGeometry.class.getName()+".class.isAssignableFrom("+field.getName()+".getClass())){ "
						+ "System.out.println(\"Imma here 6\");"
						+ "rstl.addProperty(m.getProperty(\""+name+"\"),(("+AbstractGeometry.class.getName()+") "+field.getName()+").toLiteral());"
						+ "System.out.println(\"Imma here 6 stop\");"
						+ "}\n");
				sb.append("else { rstl.addProperty(m.getProperty(\""+name+"\"),m.createTypedLiteral("+field.getName()+"));}");
			} 
			else sb.append("rstl.addProperty(m.getProperty(\""+name+"\"),m.createTypedLiteral("+field.getName()+"));");
			sb.append("}\n");
			break;
		case 4:
			// END
			sb.append("}catch(Exception e) {e.printStackTrace(); System.out.println(\"Found error during \"+uri+\" \"+e.getMessage());}");
			sb.append("System.out.println(\"returned: \"+uri);");
			sb.append("return rstl; }");
			break;
		}
	}

	public void newSerializer() {

		this.sb = new StringBuilder();
		sb.append("public org.apache.jena.ontology.Individual serialize(org.apache.jena.rdf.model.Model m, org.apache.jena.ontology.OntModel om, java.lang.String uri, java.lang.Object o) {\n"); 
		sb.append("		org.apache.jena.rdf.model.Resource toSearch = org.apache.jena.rdf.model.ResourceFactory.createResource(uri);\n"); 
		sb.append("		org.apache.jena.rdf.model.Resource rstl = null;\n"); 
		sb.append("		if(om.containsResource(toSearch)){ rstl = om.getResource(uri);}\n"); 
		sb.append("		else { rstl = om.createResource(uri); }\n"); 
		sb.append("		if(om.getIndividual(((org.epos.metadata.editor.reflection.shapes.Node) o).getId()) != null) {\n"); 
		sb.append("			return om.getIndividual(((org.epos.metadata.editor.reflection.shapes.Node) o).getId());\n"); 
		sb.append("		}\n"); 
		sb.append("		org.apache.jena.ontology.Individual individual = om.createIndividual(((org.epos.metadata.editor.reflection.shapes.Node) o).getId(),rstl);\n"); 
		sb.append("		for(java.lang.reflect.Field f : o.getClass().getDeclaredFields()) {\n"); 
		sb.append("			try {\n"); 
		sb.append("				if(java.util.Optional.ofNullable(f.get(o)).isPresent()){\n"); 
		sb.append("					if(java.util.List.class.isAssignableFrom(f.get(o).getClass())) {\n"); 
		sb.append("						for(int i=0; i<java.util.List.class.cast(f.get(o)).size();i++){\n"); 
		sb.append("							java.lang.Object item = java.util.List.class.cast(f.get(o)).get(i);\n"); 
		sb.append("							org.epos.metadata.editor.engine.utils.Shacl ta = f.getAnnotation(org.epos.metadata.editor.engine.utils.Shacl.class);\n"); 
		sb.append("							if(org.epos.metadata.editor.reflection.shapes.Node.class.isAssignableFrom(java.lang.Class.forName(item.getClass().getCanonicalName()))) {\n"); 
		sb.append("								java.lang.String ns = org.epos.metadata.editor.engine.Engine.getIstance().getMs().getResource(item.getClass().getSimpleName()).getURI();\n"); 
		sb.append("								System.out.println(\"NAMESPACE: \"+ns);\n"); 
		sb.append("								java.lang.Object returned = (org.epos.metadata.editor.reflection.shapes.Node.class.cast(item)).serialize(m, om, ns, item);\n"); 
		sb.append("								individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),org.apache.jena.ontology.Individual.class.cast(returned));\n"); 
		sb.append("							}\n"); 
		sb.append("							else if(org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry.class.isAssignableFrom(f.getClass())){\n"); 
		sb.append("								individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),((org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry) item).toLiteral());\n"); 
		sb.append("							} else	{\n"); 
		sb.append("								individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),m.createTypedLiteral(item)); }\n"); 
		sb.append("						}\n"); 
		sb.append("					}else {\n"); 
		sb.append("						org.epos.metadata.editor.engine.utils.Shacl ta = f.getAnnotation(org.epos.metadata.editor.engine.utils.Shacl.class);\n"); 
		sb.append("						if(java.lang.Class.forName(f.getClass().getCanonicalName()).isAssignableFrom(org.epos.metadata.editor.reflection.shapes.Node.class)){\n"); 
		sb.append("							java.lang.String ns = org.epos.metadata.editor.engine.Engine.getIstance().getMs().getResource(f.get(o).getClass().getSimpleName()).getURI();\n"); 
		sb.append("							System.out.println(\"NAMESPACE: \"+ns);\n"); 
		sb.append("							java.lang.Object returned = (org.epos.metadata.editor.reflection.shapes.Node.class.cast(f.get(o))).serialize(m, om, ns, f.get(o));\n"); 
		sb.append("							individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),org.apache.jena.ontology.Individual.class.cast(returned));\n"); 
		sb.append("						}\n"); 
		sb.append("						else if(org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry.class.isAssignableFrom(f.getClass())){\n"); 
		sb.append("							individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),((org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry) f.get(o)).toLiteral());\n"); 
		sb.append("						} else	{\n"); 
		sb.append("							individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),m.createTypedLiteral(f.get(o))); }\n"); 
		sb.append("					}\n"); 
		sb.append("				}\n"); 
		sb.append("			} catch (java.lang.IllegalArgumentException | java.lang.IllegalAccessException | java.lang.SecurityException | java.lang.ClassNotFoundException e) {\n"); 
		sb.append("				e.printStackTrace();\n"); 
		sb.append("			}\n"); 
		sb.append("		}\n"); 
		sb.append("		return individual;\n"); 
		sb.append("	}\n");

		System.out.println(sb.toString());
	}

	public void createSerializer(CtClass cc) {
		CtMethod serializer;
		try {
			//if(sb.toString().contains("Dataset") ) //System.out.println(sb.toString());
			serializer = CtNewMethod.make(sb.toString(), cc);

			MethodInfo methodInfoGetEid = serializer.getMethodInfo();
			ConstPool cp = methodInfoGetEid.getConstPool();
			Annotation annotationNew = new Annotation("Override", cp);
			AnnotationsAttribute attributeNew = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);  
			attributeNew.addAnnotation(annotationNew);
			serializer.getMethodInfo().addAttribute(attributeNew);
			cc.addMethod(serializer);
		} catch (CannotCompileException e) {
			e.printStackTrace();
			logger.severe(e.getLocalizedMessage()+" "+e.getCause()+" "+e.getMessage());
		}
	}

	public static synchronized org.apache.jena.ontology.Individual serialize(org.apache.jena.rdf.model.Model m, org.apache.jena.ontology.OntModel om, java.lang.String uri, org.epos.metadata.editor.reflection.shapes.Node o){

		if(uri==null) uri = Engine.getIstance().getMs().getClassVocab(o.getClass().getSimpleName());
		System.out.println("URI: "+uri);
		org.apache.jena.rdf.model.Resource toSearch = org.apache.jena.rdf.model.ResourceFactory.createResource(uri);
		org.apache.jena.rdf.model.Resource rstl = null;
		org.apache.jena.ontology.Individual individual = null;
		if(om.containsResource(toSearch)){ rstl = om.getResource(uri);}
		else { rstl = om.createResource(uri);  }
		if(om.getIndividual(o.getId()) != null) {
			return om.getIndividual(o.getId());
		}
		else {
			individual = om.createIndividual(o.getId(),rstl);
			for(java.lang.reflect.Field f : o.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				try {
					System.out.println(f.getName()+" "+f.get(o));
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					e1.printStackTrace();
				}
				try {
					if(java.util.Optional.ofNullable(f.get(o)).isPresent()){
						if(java.util.List.class.isAssignableFrom(f.get(o).getClass())) {
							for(int i=0; i<java.util.List.class.cast(f.get(o)).size();i++){
								java.lang.Object item = java.util.List.class.cast(f.get(o)).get(i);
								org.epos.metadata.editor.engine.utils.Shacl ta = f.getAnnotation(org.epos.metadata.editor.engine.utils.Shacl.class);
								if(org.epos.metadata.editor.reflection.shapes.Node.class.isAssignableFrom(item.getClass())) {
									//java.lang.String ns = org.epos.metadata.editor.engine.Engine.getIstance().getMs().getResource(item.getClass().getSimpleName()).getURI().replace("Shape", "");
									String ns = null;
									if(org.epos.metadata.editor.engine.Engine.getIstance().getMs().getResource(item.getClass().getSimpleName()).getURI().replace("Shape", "")!=null) 
										ns = Engine.getIstance().getMs().getClassVocab(f.get(o).getClass().getSimpleName());
									System.out.println("NAMESPACE 1: "+ns);
									java.lang.Object returned = Serializer.serialize(m, om, ns, org.epos.metadata.editor.reflection.shapes.Node.class.cast(item));
									individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),org.apache.jena.ontology.Individual.class.cast(returned));
								}
								else if(org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry.class.isAssignableFrom(item.getClass())){
									individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),((org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry) item).toLiteral());
								} else	{
									individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),m.createTypedLiteral(item)); }
							}
						}else {

							org.epos.metadata.editor.engine.utils.Shacl ta = f.getAnnotation(org.epos.metadata.editor.engine.utils.Shacl.class);
							if(org.epos.metadata.editor.reflection.shapes.Node.class.isAssignableFrom(f.get(o).getClass())){
								//java.lang.String ns = org.epos.metadata.editor.engine.Engine.getIstance().getMs().getResource(f.get(o).getClass().getSimpleName()).getURI().replace("Shape", "");
								String ns = null;
								if(org.epos.metadata.editor.engine.Engine.getIstance().getMs().getResource(f.get(o).getClass().getSimpleName()).getURI().replace("Shape", "")!=null) 
									ns = Engine.getIstance().getMs().getClassVocab(f.get(o).getClass().getSimpleName());
								System.out.println("NAMESPACE 2: "+ns);
								java.lang.Object returned = Serializer.serialize(m, om, ns, org.epos.metadata.editor.reflection.shapes.Node.class.cast(f.get(o)));
								individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),org.apache.jena.ontology.Individual.class.cast(returned));
							}
							else if(org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry.class.isAssignableFrom(f.getClass())){
								individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),((org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry) f.get(o)).toLiteral());
							} else	{
								individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),m.createTypedLiteral(f.get(o))); }
						}
					}
				} catch (java.lang.IllegalArgumentException | java.lang.IllegalAccessException | java.lang.SecurityException e) {
					e.printStackTrace();
				}
			}
		}
		return individual;
	}


	public static Resource bkp_serialize(Model m, Model om, String uri, Node o){
		Resource toSearch = ResourceFactory.createResource(o.getId());
		Resource rstl = null;
		System.out.println("ID -> "+o.getId()+" "+Engine.getIstance().getMs().getRootOntology().replace("#", ""));
		if(om.containsResource(toSearch)){ 
			rstl = om.getResource(o.getId());
		}
		else {
			boolean foundIdentifier = false;
			/*for(java.lang.reflect.Field f : o.getClass().getDeclaredFields()) {
				if(f.getName().contains("identifier")) foundIdentifier = true;
			}*/
			if(o.getId().contains(Engine.getIstance().getMs().getRootOntology().replace("#", ""))) {
				System.out.println("ID -> "+o.getId()+" "+Engine.getIstance().getMs().getRootOntology());
				foundIdentifier = true;
			}

			if(foundIdentifier) {
				rstl = om.createResource(o.getId()); 
				rstl.addProperty(RDF.type,Engine.getIstance().getMs().getResource(o.getClass().getSimpleName()).getURI().replace("Shape", ""));
			}
			else rstl = ResourceFactory.createResource(o.getId()); 
		}
		for(Field f : o.getClass().getDeclaredFields()) {
			f.setAccessible(true);
			try {
				if(java.util.Optional.ofNullable(f.get(o)).isPresent()){
					if(java.util.List.class.isAssignableFrom(f.get(o).getClass())) {
						for(int i=0; i<java.util.List.class.cast(f.get(o)).size();i++){
							java.lang.Object item = java.util.List.class.cast(f.get(o)).get(i);
							Shacl ta = f.getAnnotation(Shacl.class);
							if(Node.class.isAssignableFrom(item.getClass())) {
								String ns = Engine.getIstance().getMs().getResource(item.getClass().getSimpleName()).getURI().replace("Shape", "");
								System.out.println("NAMESPACE: "+ns);
								Resource returned = Serializer.bkp_serialize(m, om, ns, Node.class.cast(item));
								Resource rs = om.createResource();
								rstl.addProperty(om.createProperty(ta.vocabulary()+ta.term()),rs);
								for(Statement p : returned.listProperties().toList()) {
									rs.addProperty(p.getPredicate(), p.getObject());
									//if(om.getProperty(ta.vocabulary()+ta.term()) != null) {
									//}
									//else rstl.addProperty(om.createProperty(ta.vocabulary()+ta.term()),om.createResource(returned));
								}
							}
							else if(AbstractGeometry.class.isAssignableFrom(item.getClass())){
								if(om.getProperty(ta.vocabulary()+ta.term()) != null)
									rstl.addProperty(om.getProperty(ta.vocabulary()+ta.term()),((AbstractGeometry) item).toLiteral());
								else rstl.addProperty(om.createProperty(ta.vocabulary()+ta.term()),((AbstractGeometry) item).toLiteral());
							} else	{
								if(om.getProperty(ta.vocabulary()+ta.term()) != null)
									rstl.addProperty(om.getProperty(ta.vocabulary()+ta.term()),item.toString());
								else rstl.addProperty(om.createProperty(ta.vocabulary()+ta.term()),item.toString());
							}
						}
					}else {

						Shacl ta = f.getAnnotation(Shacl.class);
						if(Node.class.isAssignableFrom(f.get(o).getClass())){
							String ns = Engine.getIstance().getMs().getResource(f.get(o).getClass().getSimpleName()).getURI().replace("Shape", "");
							System.out.println("NAMESPACE: "+ns);
							Resource returned = Serializer.bkp_serialize(m, om, ns, Node.class.cast(f.get(o)));
							Resource rs = om.createResource();
							rstl.addProperty(om.createProperty(ta.vocabulary()+ta.term()),rs);
							for(Statement p : returned.listProperties().toList()) {
								rs.addProperty(p.getPredicate(), p.getObject());
							}
							/*if(om.getProperty(ta.vocabulary()+ta.term()) != null)
								rstl.addProperty(om.getProperty(ta.vocabulary()+ta.term()),om.createResource(returned));
							else rstl.addProperty(om.createProperty(ta.vocabulary()+ta.term()),om.createResource(returned));*/
						}
						else if(AbstractGeometry.class.isAssignableFrom(f.getClass())){
							if(om.getProperty(ta.vocabulary()+ta.term()) != null)
								rstl.addProperty(om.getProperty(ta.vocabulary()+ta.term()),((AbstractGeometry) f.get(o)).toLiteral());
							else rstl.addProperty(om.createProperty(ta.vocabulary()+ta.term()),((AbstractGeometry) f.get(o)).toLiteral());
						} else	{
							System.out.println("INFO --> "+f.get(o));
							if(om.getProperty(ta.vocabulary()+ta.term()) != null)
								rstl.addProperty(om.getProperty(ta.vocabulary()+ta.term()),f.get(o).toString());
							else rstl.addProperty(om.createProperty(ta.vocabulary()+ta.term()),f.get(o).toString());
						}
					}
				}
			} catch (java.lang.IllegalArgumentException | java.lang.IllegalAccessException | java.lang.SecurityException e) {
				e.printStackTrace();
			}
		}
		return rstl;
	}

}

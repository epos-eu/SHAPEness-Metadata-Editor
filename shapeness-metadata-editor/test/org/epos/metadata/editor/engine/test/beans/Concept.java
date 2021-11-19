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
package org.epos.metadata.editor.engine.test.beans;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry;
import org.epos.metadata.editor.engine.utils.Shacl;
import org.epos.metadata.editor.reflection.shapes.Node;

public class Concept  extends Node {

	@NotNull
	@Shacl(constraint = "Mandatory", term = "name", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	String schema_name;
	
	
	public Concept(String id, String name) {
		super(id, name);
	}


	public String getSchema_name() {
		return schema_name;
	}

	public void setSchema_name(String schema_name) {
		this.schema_name = schema_name;
	}

	public org.apache.jena.ontology.Individual serialize(org.apache.jena.rdf.model.Model m, org.apache.jena.ontology.OntModel om, java.lang.String uri, java.lang.Object o) {
		org.apache.jena.rdf.model.Resource toSearch = org.apache.jena.rdf.model.ResourceFactory.createResource(uri);
		org.apache.jena.rdf.model.Resource rstl = null;
		if(om.containsResource(toSearch)) rstl = om.getResource(uri);
		else rstl = om.createResource(uri);

		if(om.getIndividual(((org.epos.metadata.editor.reflection.shapes.Node) o).getId()) != null) {
			return om.getIndividual(((org.epos.metadata.editor.reflection.shapes.Node) o).getId());
		}

		org.apache.jena.ontology.Individual individual = om.createIndividual(((org.epos.metadata.editor.reflection.shapes.Node) o).getId(),rstl);
		for(java.lang.reflect.Field f : o.getClass().getDeclaredFields()) {
			try {
				if(java.util.Optional.ofNullable(f.get(o)).isPresent()){
					if(java.util.List.class.isAssignableFrom(f.get(o).getClass())) {
						for(int i=0; i<java.util.List.class.cast(f.get(o)).size();i++){
							java.lang.Object item = java.util.List.class.cast(f.get(o)).get(i);
							org.epos.metadata.editor.engine.utils.Shacl ta = f.getAnnotation(org.epos.metadata.editor.engine.utils.Shacl.class);
							if(org.epos.metadata.editor.reflection.shapes.Node.class.isAssignableFrom(java.lang.Class.forName(item.getClass().getCanonicalName()))) {
								java.lang.String ns = org.epos.metadata.editor.engine.Engine.getIstance().getMs().getResource(item.getClass().getSimpleName()).getURI();
								System.out.println("NAMESPACE: "+ns);
								java.lang.Object returned = (org.epos.metadata.editor.reflection.shapes.Node.class.cast(item)).serialize(m, om, "http://schema.org/"+item.getClass().getSimpleName(), item);
								individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),org.apache.jena.ontology.Individual.class.cast(returned));
							}
							else if(org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry.class.isAssignableFrom(f.getClass())){
								individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),((org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry) item).toLiteral());
							} else	
								individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),m.createTypedLiteral(item));
						}
					}else {
						org.epos.metadata.editor.engine.utils.Shacl ta = f.getAnnotation(org.epos.metadata.editor.engine.utils.Shacl.class);
						if(java.lang.Class.forName(f.getClass().getCanonicalName()).isAssignableFrom(org.epos.metadata.editor.reflection.shapes.Node.class)){
							java.lang.Object returned = (org.epos.metadata.editor.reflection.shapes.Node.class.cast(f.get(o))).serialize(m, om, "http://schema.org/"+f.get(o).getClass().getSimpleName(), f.get(o));
							individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),org.apache.jena.ontology.Individual.class.cast(returned));
						}
						else if(org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry.class.isAssignableFrom(f.getClass())){
							individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),((org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry) f.get(o)).toLiteral());
						} else	
							individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),m.createTypedLiteral(f.get(o)));
					}


				}
			} catch (java.lang.IllegalArgumentException | java.lang.IllegalAccessException | java.lang.SecurityException | java.lang.ClassNotFoundException e) {
				e.printStackTrace();
			}
		}		
		return individual;
	}

	
	@Override
	public String toString() {
		return "Concept [schema_name=" + schema_name + "]";
	}
	
	

}

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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotEmpty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry;
import org.epos.metadata.editor.engine.utils.Shacl;
import org.epos.metadata.editor.reflection.shapes.Node;

import com.fasterxml.jackson.databind.deser.impl.PropertyValue;

public class Person extends Node{

	@NotEmpty
	@Shacl(constraint = "Mandatory", term = "identifier", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	List<Object> schema_identifier;


	@Shacl(constraint = "Optional", term = "givenName", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	String schema_givenName;


	@Shacl(constraint = "Optional", term = "familyName", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	String schema_familyName;

	@Shacl(constraint = "Optional", term = "contactPoint", vocabulary = "http://schema.org/", altVocabulary = "http://www.w3.org/ns/dcat#", shortVocabulary = "")
	List<Object> contactPoint;

	@Shacl(constraint = "Optional", term = "address", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	Object schema_address;

	@Shacl(constraint = "Optional", term = "email", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	List<String> schema_email;

	@Shacl(constraint = "Optional", term = "qualifications", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	String schema_qualifications;

	@Shacl(constraint = "Optional", term = "telephone", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	List<String> schema_telephone;

	@Shacl(constraint = "Optional", term = "url", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	URI schema_url;

	@Shacl(constraint = "Optional", term = "affiliation", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	List<Organization> schema_affiliation;

	@Shacl(constraint = "Optional", term = "annotation", vocabulary = "https://www.epos-eu.org/epos-dcat-ap#", altVocabulary = "null", shortVocabulary = "")
	List<String> epos_annotation;


	public Person(String id, String name) {
		super(id, name);
		this.contactPoint = new ArrayList<>();
		this.epos_annotation = new ArrayList<>();
		this.schema_affiliation = new ArrayList<>();
		this.schema_email = new ArrayList<>();
		this.schema_identifier = new ArrayList<>();
		this.schema_telephone = new ArrayList<>();
	}


	public List<Object> getschema_identifier() {
		return schema_identifier;
	}

	public String getschema_givenName() {
		return schema_givenName;
	}

	public String getschema_familyName() {
		return schema_familyName;
	}

	public List<Object> getcontactPoint() {
		return contactPoint;
	}

	public Object getschema_address() {
		return schema_address;
	}

	public List<String> getschema_email() {
		return schema_email;
	}

	public String getschema_qualifications() {
		return schema_qualifications;
	}

	public List<String> getschema_telephone() {
		return schema_telephone;
	}

	public URI getschema_url() {
		return schema_url;
	}

	public List<Organization> getschema_affiliation() {
		return schema_affiliation;
	}

	public List<String> getepos_annotation() {
		return epos_annotation;
	}


	public void addschema_identifier(String arg0) {
		this.schema_identifier.add(arg0);
	}

	public void addschema_identifier(URI arg0) {
		this.schema_identifier.add(arg0);
	}

	public void addschema_identifier(PropertyValue arg0) {
		this.schema_identifier.add(arg0);
	}

	public void setschema_givenName(String arg0) {
		this.schema_givenName = arg0;
	}

	public void setschema_familyName(String arg0) {
		this.schema_familyName = arg0;
	}

	public void addcontactPoint(ContactPoint arg0) {
		this.contactPoint.add(arg0);
	}
	
	public void addcontactPoint(String arg0) {
		this.contactPoint.add(arg0);
	}

	public void setschema_address(String arg0) {
		this.schema_address = arg0;
	}

	public void setschema_address(PostalAddress arg0) {
		this.schema_address = arg0;
	}

	public void addschema_email(String arg0) {
		this.schema_email.add(arg0);
	}

	public void setschema_qualifications(String arg0) {
		this.schema_qualifications = arg0;
	}

	public void addschema_telephone(String arg0) {
		this.schema_telephone.add(arg0);
	}

	public void setschema_url(URI arg0) {
		this.schema_url = arg0;
	}

	public void addschema_affiliation(Organization arg0) {
		this.schema_affiliation.add(arg0);
	}

	public void addepos_annotation(String arg0) {
		this.epos_annotation.add(arg0);
	}

	public org.apache.jena.ontology.Individual serialize(org.apache.jena.rdf.model.Model m, org.apache.jena.ontology.OntModel om, java.lang.String uri, java.lang.Object o) {
		org.apache.jena.rdf.model.Resource toSearch = org.apache.jena.rdf.model.ResourceFactory.createResource(uri);
		org.apache.jena.rdf.model.Resource rstl = null;
		if(om.containsResource(toSearch)){ rstl = om.getResource(uri);}
		else { rstl = om.createResource(uri); }
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
								java.lang.Object returned = (org.epos.metadata.editor.reflection.shapes.Node.class.cast(item)).serialize(m, om, ns, item);
								individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),org.apache.jena.ontology.Individual.class.cast(returned));
							}
							else if(org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry.class.isAssignableFrom(f.getClass())){
								individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),((org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry) item).toLiteral());
							} else	{
								individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),m.createTypedLiteral(item)); }
						}
					}else {
						org.epos.metadata.editor.engine.utils.Shacl ta = f.getAnnotation(org.epos.metadata.editor.engine.utils.Shacl.class);
						if(java.lang.Class.forName(f.getClass().getCanonicalName()).isAssignableFrom(org.epos.metadata.editor.reflection.shapes.Node.class)){
							java.lang.String ns = org.epos.metadata.editor.engine.Engine.getIstance().getMs().getResource(f.get(o).getClass().getSimpleName()).getURI();
							System.out.println("NAMESPACE: "+ns);
							java.lang.Object returned = (org.epos.metadata.editor.reflection.shapes.Node.class.cast(f.get(o))).serialize(m, om, ns, f.get(o));
							individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),org.apache.jena.ontology.Individual.class.cast(returned));
						}
						else if(org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry.class.isAssignableFrom(f.getClass())){
							individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),((org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry) f.get(o)).toLiteral());
						} else	{
							individual.addProperty(m.getProperty(ta.vocabulary()+ta.term()),m.createTypedLiteral(f.get(o))); }
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
		return "Person [schema_identifier=" + schema_identifier + ", schema_givenName=" + schema_givenName
				+ ", schema_familyName=" + schema_familyName + ", contactPoint=" + contactPoint + ", schema_address="
				+ schema_address + ", schema_email=" + schema_email + ", schema_qualifications=" + schema_qualifications
				+ ", schema_telephone=" + schema_telephone + ", schema_url=" + schema_url + ", schema_affiliation="
				+ schema_affiliation + ", epos_annotation=" + epos_annotation + "]";
	}



}

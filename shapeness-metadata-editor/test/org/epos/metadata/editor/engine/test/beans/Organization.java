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
import java.net.URI;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry;
import org.epos.metadata.editor.engine.utils.Shacl;
import org.epos.metadata.editor.reflection.shapes.Node;

import com.fasterxml.jackson.databind.deser.impl.PropertyValue;

public class Organization extends Node{
	
	@NotEmpty
	@Shacl(constraint = "Mandatory", term = "identifier", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	List<Object> schema_identifier;
	
	@Shacl(constraint = "Recommended", term = "legalName", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	String schema_legalName;
	
	@Shacl(constraint = "Recommended", term = "leiCode", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	String schema_leiCode;
	
	@Shacl(constraint = "Optional", term = "annotation", vocabulary = "https://www.epos-eu.org/epos-dcat-ap#", altVocabulary = "null", shortVocabulary = "")
	List<String> epos_annotation;
	
	@Shacl(constraint = "Optional", term = "address", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	Object schema_address;
	
	@Shacl(constraint = "Optional", term = "logo", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	URI schema_logo;
	
	@Shacl(constraint = "Optional", term = "url", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	URI schema_url;
	
	@Shacl(constraint = "Optional", term = "email", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	List<String> schema_email;
	
	@Shacl(constraint = "Optional", term = "telephone", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	List<String> schema_telephone;
	
	@Shacl(constraint = "Optional", term = "contactPoint", vocabulary = "http://schema.org/", altVocabulary = "http://www.w3.org/ns/dcat#", shortVocabulary = "")
	List<ContactPoint> contactPoint;
	
	@Shacl(constraint = "Optional", term = "memberOf", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	List<Organization> schema_memberOf;
	
	@Shacl(constraint = "Optional", term = "owns", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	List<Object> schema_owns;
	
	@Shacl(constraint = "Optional", term = "associatedProject", vocabulary = "https://www.epos-eu.org/epos-dcat-ap#", altVocabulary = "null", shortVocabulary = "")
	List<Project> epos_associatedProject;


	public Organization(final String id, final String name) {
		super(id, name);
	}


	public List<Object> getschema_identifier() {
		return schema_identifier;
	}


	public String getschema_legalName() {
		return schema_legalName;
	}


	public String getschema_leiCode() {
		return schema_leiCode;
	}


	public List<String> getepos_annotation() {
		return epos_annotation;
	}


	public Object getschema_address() {
		return schema_address;
	}


	public URI getschema_logo() {
		return schema_logo;
	}


	public URI getschema_url() {
		return schema_url;
	}


	public List<String> getschema_email() {
		return schema_email;
	}


	public List<String> getschema_telephone() {
		return schema_telephone;
	}

	public List<ContactPoint> getcontactPoint() {
		return contactPoint;
	}
	

	public List<Organization> getschema_memberOf() {
		return schema_memberOf;
	}


	public List<Object> getschema_owns() {
		return schema_owns;
	}
	
	public List<Project> getepos_associatedProject() {
		return epos_associatedProject;
	}



	public void addschema_identifier(final String arg0) {
	}

	public void addschema_identifier(final URI arg0) {
	}

	public void addschema_identifier(final PropertyValue arg0) {
	}

	public void setschema_legalName(final String schema_legalName) {
	}

	public void setschema_leiCode(final String schema_leiCode) {
	}


	public void addepos_annotation(final String arg0) {
	}


	public void setschema_address(final String arg0) {
	}

	public void setschema_address(final PostalAddress arg0) {
	}


	public void setschema_logo(final URI schema_logo) {
	}


	public void setschema_url(final URI schema_url) {
	}


	public void addschema_email(final String schema_email) {
	}


	public void addschema_telephone(final String schema_telephone) {
	}

	public void addcontactPoint(final ContactPoint arg0) {
	}
	
	public void addcontactPoint(final String arg0) {
	}


	public void addschema_memberOf(final Organization schema_memberOf) {
	}


	public void addschema_owns(final Facility arg0) {
	}
	
	public void addschema_owns(final Equipment arg0) {
	}
	
	public void addepos_associatedProject(final Project arg0) {
	}
	
	public Resource serialize(Model m, Model om, String uri)
	{
		Resource rstl = om.createResource(uri);
		for(Field f : this.getClass().getFields()) {

			try {
				if(java.util.Optional.ofNullable(f.get(this)).isPresent()){
					System.out.println(f.getClass());
					System.out.println(this.getClass());
					if(this.getClass().isAssignableFrom(f.getClass())){
							rstl.addProperty(m.getProperty(f.getName()),(this.getClass().cast(f.get(this))).serialize(m, om, f.getName()));
					}
					else if(AbstractGeometry.class.isAssignableFrom(f.getClass())){
							rstl.addProperty(m.getProperty(f.getName()),((AbstractGeometry) f.get(this)).toLiteral());
					} else
							rstl.addProperty(m.getProperty(f.getName()),m.createTypedLiteral(f.get(this)));

				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return rstl;
	}


	public String toString() {
		return null;
	}
}

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry;
import org.epos.metadata.editor.engine.utils.Shacl;
import org.epos.metadata.editor.reflection.shapes.Node;

import com.fasterxml.jackson.databind.deser.impl.PropertyValue;

public class Publication extends Node{

	@NotEmpty
	@Shacl(constraint = "Mandatory", term = "identifier", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	List<Object> schema_identifier;

	@NotNull
	@Shacl(constraint = "Mandatory", term = "name", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	String schema_name;


	@Shacl(constraint = "Recommended", term = "datePublished", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	XSDDateTime schema_datePublished;


	@Shacl(constraint = "Recommended", term = "publisher", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	Object schema_publisher;

	@Shacl(constraint = "Recommended", term = "abstract", vocabulary = "http://purl.org/dc/terms/", altVocabulary = "null", shortVocabulary = "")
	String dct_abstract;

	@Shacl(constraint = "Recommended", term = "author", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	Person schema_author;

	@Shacl(constraint = "Recommended", term = "contributor", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	List<Person> schema_contributor;

	@Shacl(constraint = "Optional", term = "license", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	URI schema_license;

	@Shacl(constraint = "Optional", term = "keywords", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	String schema_keywords;

	@Shacl(constraint = "Optional", term = "issn", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	String schema_issn;

	@Shacl(constraint = "Optional", term = "numberOfPages", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	Integer schema_numberOfPages;

	@Shacl(constraint = "Optional", term = "volumeNumber", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	String schema_volumeNumber;

	@Shacl(constraint = "Optional", term = "theme", vocabulary = "http://www.w3.org/ns/dcat#", altVocabulary = "null", shortVocabulary = "")
	List<Concept> dcat_theme;

	public Publication(String id, String name) {
		super(id, name);
		this.dcat_theme = new ArrayList<>();
		this.schema_contributor = new ArrayList<>();
		this.schema_identifier = new ArrayList<>();
	}

	public List<Object> getschema_identifier() {
		return schema_identifier;
	}

	public String getschema_name() {
		return schema_name;
	}

	public XSDDateTime getschema_datePublished() {
		return schema_datePublished;
	}

	public Object getschema_publisher() {
		return schema_publisher;
	}

	public String getdct_abstract() {
		return dct_abstract;
	}

	public Person getschema_author() {
		return schema_author;
	}

	public List<Person> getschema_contributor() {
		return schema_contributor;
	}

	public URI getschema_license() {
		return schema_license;
	}

	public String getschema_keywords() {
		return schema_keywords;
	}

	public String getschema_issn() {
		return schema_issn;
	}

	public Integer getschema_numberOfPages() {
		return schema_numberOfPages;
	}

	public String getschema_volumeNumber() {
		return schema_volumeNumber;
	}

	public List<Concept> getdcat_theme() {
		return dcat_theme;
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

	public void setschema_name(String schema_name) {
		this.schema_name = schema_name;
	}

	public void setschema_datePublished(XSDDateTime schema_datePublished) {
		this.schema_datePublished = schema_datePublished;
	}

	public void setschema_publisher(Person schema_publisher) {
		this.schema_publisher = schema_publisher;
	}

	public void setschema_publisher(Organization schema_publisher) {
		this.schema_publisher = schema_publisher;
	}

	public void setschema_publisher(String schema_publisher) {
		this.schema_publisher = schema_publisher;
	}

	public void setdct_abstract(String dct_abstract) {
		this.dct_abstract = dct_abstract;
	}

	public void setschema_author(Person schema_author) {
		this.schema_author = schema_author;
	}

	public void addschema_contributor(Person schema_contributor) {
		this.schema_contributor.add(schema_contributor);
	}

	public void setschema_license(URI schema_license) {
		this.schema_license = schema_license;
	}

	public void setschema_keywords(String schema_keywords) {
		this.schema_keywords = schema_keywords;
	}

	public void setschema_issn(String schema_issn) {
		this.schema_issn = schema_issn;
	}

	public void setschema_numberOfPages(Integer schema_numberOfPages) {
		this.schema_numberOfPages = schema_numberOfPages;
	}

	public void setschema_volumeNumber(String schema_volumeNumber) {
		this.schema_volumeNumber = schema_volumeNumber;
	}

	public void adddcat_theme(Concept dcat_theme) {
		this.dcat_theme.add(dcat_theme);
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
		return "Publication [schema_identifier=" + schema_identifier + ", schema_name=" + schema_name
				+ ", schema_datePublished=" + schema_datePublished + ", schema_publisher=" + schema_publisher
				+ ", dct_abstract=" + dct_abstract + ", schema_author=" + schema_author + ", schema_contributor="
				+ schema_contributor + ", schema_license=" + schema_license + ", schema_keywords=" + schema_keywords
				+ ", schema_issn=" + schema_issn + ", schema_numberOfPages=" + schema_numberOfPages
				+ ", schema_volumeNumber=" + schema_volumeNumber + ", dcat_theme=" + dcat_theme + "]";
	}


}

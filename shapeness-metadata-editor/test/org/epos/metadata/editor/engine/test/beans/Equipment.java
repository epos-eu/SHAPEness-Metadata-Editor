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

import java.net.URI;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.epos.metadata.editor.engine.utils.Shacl;

public class Equipment {

	@NotNull
	@Shacl(constraint = "Mandatory", term = "description", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	String schema_description;
	
	@NotNull
	@Shacl(constraint = "Mandatory", term = "identifier", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	Object schema_identifier;

	@NotNull
	@Shacl(constraint = "Mandatory", term = "name", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	String schema_name;
	
	@Shacl(constraint = "Recommended", term = "type", vocabulary = "http://purl.org/dc/terms/", altVocabulary = "null", shortVocabulary = "")
	Object dct_type;
	
	@Shacl(constraint = "Recommended", term = "manufacturer", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	Organization schema_manufacturer;
	
	@Shacl(constraint = "Recommended", term = "serialNumber", vocabulary = "http://schema.org/", altVocabulary = "null", shortVocabulary = "")
	String schema_serialNumber;
	
	@Shacl(constraint = "Optional", term = "isPartOf", vocabulary = "http://purl.org/dc/terms/", altVocabulary = "null", shortVocabulary = "")
	List<Object> dct_isPartOf;
	
	@Shacl(constraint = "Optional", term = "filter", vocabulary = "https://www.epos-eu.org/epos-dcat-ap#", altVocabulary = "null", shortVocabulary = "")
	String epos_filter; //TODO
	
	@Shacl(constraint = "Optional", term = "dynamicRange", vocabulary = "https://www.epos-eu.org/epos-dcat-ap#", altVocabulary = "null", shortVocabulary = "")
	QuantitativeValue epos_dynamicRange;
	
	@Shacl(constraint = "Optional", term = "orientation", vocabulary = "https://www.epos-eu.org/epos-dcat-ap#", altVocabulary = "null", shortVocabulary = "")
	String epos_orientation;
	
	@Shacl(constraint = "Optional", term = "resolution", vocabulary = "https://www.epos-eu.org/epos-dcat-ap#", altVocabulary = "null", shortVocabulary = "")
	String epos_resolution;
	
	@Shacl(constraint = "Optional", term = "samplePeriod", vocabulary = "https://www.epos-eu.org/epos-dcat-ap#", altVocabulary = "null", shortVocabulary = "")
	QuantitativeValue epos_samplePeriod;
	
	@Shacl(constraint = "Optional", term = "contactPoint", vocabulary = "http://schema.org/", altVocabulary = "http://www.w3.org/ns/dcat#", shortVocabulary = "")
	List<ContactPoint> contactPoint;
	
	@Shacl(constraint = "Optional", term = "spatial", vocabulary = "http://purl.org/dc/terms/", altVocabulary = "null", shortVocabulary = "")
	Location dct_spatial;
	
	@Shacl(constraint = "Optional", term = "annotation", vocabulary = "https://www.epos-eu.org/epos-dcat-ap#", altVocabulary = "null", shortVocabulary = "")
	List<String> epos_annotation;
	
	@Shacl(constraint = "Optional", term = "temporal", vocabulary = "http://purl.org/dc/terms/", altVocabulary = "null", shortVocabulary = "")
	String dct_temporal;
	
	@Shacl(constraint = "Optional", term = "relation", vocabulary = "http://purl.org/dc/terms/", altVocabulary = "null", shortVocabulary = "")
	List<Object> dct_relation;
	
	@Shacl(constraint = "Optional", term = "theme", vocabulary = "http://www.w3.org/ns/dcat#", altVocabulary = "null", shortVocabulary = "")
	List<Concept> dcat_theme;

	public String getschema_description() {
		return schema_description;
	}

	public Object getschema_identifier() {
		return schema_identifier;
	}

	public String getschema_name() {
		return schema_name;
	}

	public Object getdct_type() {
		return dct_type;
	}

	public Organization getschema_manufacturer() {
		return schema_manufacturer;
	}

	public String getschema_serialNumber() {
		return schema_serialNumber;
	}

	public List<Object> getdct_isPartOf() {
		return dct_isPartOf;
	}

	public Object getepos_filter() {
		return epos_filter;
	}

	public QuantitativeValue getepos_dynamicRange() {
		return epos_dynamicRange;
	}

	public String getepos_orientation() {
		return epos_orientation;
	}

	public String getepos_resolution() {
		return epos_resolution;
	}

	public QuantitativeValue getepos_samplePeriod() {
		return epos_samplePeriod;
	}

	public List<ContactPoint> getcontactPoint() {
		return contactPoint;
	}

	public Location getdct_spatial() {
		return dct_spatial;
	}

	public List<String> getepos_annotation() {
		return epos_annotation;
	}

	public String getdct_temporal() {
		return dct_temporal;
	}

	public List<Object> getdct_relation() {
		return dct_relation;
	}

	public List<Concept> getdcat_theme() {
		return dcat_theme;
	}

	public void setschema_description(String schema_description) {
		this.schema_description = schema_description;
	}

	public void setschema_identifier(String schema_identifier) {
		this.schema_identifier = schema_identifier;
	}
	
	public void setschema_identifier(URI schema_identifier) {
		this.schema_identifier = schema_identifier;
	}

	public void setschema_name(String schema_name) {
		this.schema_name = schema_name;
	}

	public void setdct_type(URI dct_type) {
		this.dct_type = dct_type;
	}
	
	public void setdct_type(Concept dct_type) {
		this.dct_type = dct_type;
	}

	public void setschema_manufacturer(Organization schema_manufacturer) {
		this.schema_manufacturer = schema_manufacturer;
	}

	public void setschema_serialNumber(String schema_serialNumber) {
		this.schema_serialNumber = schema_serialNumber;
	}

	public void adddct_isPartOf(Equipment dct_isPartOf) {
		this.dct_isPartOf.add(dct_isPartOf);
	}
	
	public void adddct_isPartOf(Facility dct_isPartOf) {
		this.dct_isPartOf.add(dct_isPartOf);
	}

	public void setepos_filter(String epos_filter) {
		this.epos_filter = epos_filter;
	}

	public void setepos_dynamicRange(QuantitativeValue epos_dynamicRange) {
		this.epos_dynamicRange = epos_dynamicRange;
	}
	

	public void setepos_orientation(String epos_orientation) {
		this.epos_orientation = epos_orientation;
	}

	public void setepos_resolution(String epos_resolution) {
		this.epos_resolution = epos_resolution;
	}

	public void setepos_samplePeriod(QuantitativeValue epos_samplePeriod) {
		this.epos_samplePeriod = epos_samplePeriod;
	}

	public void addcontactPoint(ContactPoint arg0) {
	}
	
	public void addcontactPoint(String arg0) {
	}

	public void setdct_spatial(Location dct_spatial) {
		this.dct_spatial = dct_spatial;
	}

	public void addepos_annotation(String arg0) {
	}

	public void setdct_temporal(String dct_temporal) {
		this.dct_temporal = dct_temporal;
	}

	public void adddct_relation(Dataset dct_relation) {
		this.dct_relation.add(dct_relation);
	}
	
	public void adddct_relation(WebService dct_relation) {
		this.dct_relation.add(dct_relation);
	}
	
	public void adddct_relation(String dct_relation) {
		this.dct_relation.add(dct_relation);
	}

	public void adddcat_theme(Concept dcat_theme) {
		this.dcat_theme.add(dcat_theme);
	}
	
	public Resource serialize(Model m, Model om, String uri)
	{
		Equipment e = new Equipment();
		Resource rstl = om.createResource(uri);
		rstl.addProperty(m.getProperty("description"), e.serialize(m, om, "uri"));
		rstl.addProperty(m.getProperty("description"), m.createTypedLiteral(""));
		return null;
	}


	public String toString() {
		return null;
	}
	
}

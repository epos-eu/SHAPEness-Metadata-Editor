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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.VCARD;
import org.epos.metadata.editor.engine.converters.methods.Serializer;
import org.epos.metadata.editor.engine.test.beans.Concept;
import org.epos.metadata.editor.engine.test.beans.Organization;
import org.epos.metadata.editor.engine.test.beans.Person;
import org.epos.metadata.editor.engine.test.beans.Publication;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.vocabulary.SH;

public class ModelTest {

	public static void main(String[] args) {
		
		String shapes = "https://raw.githubusercontent.com/epos-eu/EPOS-DCAT-AP/EPOS-DCAT-AP-shapes/epos-dcat-ap_shapes.ttl";

		URL url = null;
		try {
			url = new URL(shapes);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		InputStream in = null;
		try {
			in = url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Model shaclModel = JenaUtil.createMemoryModel(); //MEMORY MODEL
		shaclModel.read(in, SH.BASE_URI, FileUtils.langTurtle);
		
		OntModel m = JenaUtil.createOntologyModel(OntModelSpec.OWL_DL_MEM, JenaUtil.createDefaultModel());
		m.setNsPrefixes(shaclModel.getNsPrefixMap());
		
		Person p = new Person("1", "Person");
		p.addschema_identifier("Person1");
		p.addcontactPoint("CIAONE");
		
		Publication o = new Publication("2", "Organization");
		o.addschema_identifier("Publication1");
		o.addschema_contributor(p);
		
		Concept c = new Concept("3", "Concept");
		c.setSchema_name("ciaonezzz");
		o.adddcat_theme(c);
		
		System.out.println(o.toString());
		

				
		try {
			Serializer.serialize(shaclModel, m, "http://schema.org/Publication", o);
			Serializer.serialize(shaclModel, m, "http://schema.org/Person", p);
			Serializer.serialize(shaclModel, m, "http://schema.org/Concept", c);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*String personURI    = "http://epos/JohnSmith";
		String givenName    = "John";
		String familyName   = "Smith";
		String fullName     = givenName + " " + familyName;
		Resource johnSmith
		  = m.createResource(personURI)
		         .addProperty(VCARD.FN, fullName);*/
		
		RDFDataMgr.write(System.out, m, RDFFormat.TURTLE_PRETTY) ;
		
	}

}

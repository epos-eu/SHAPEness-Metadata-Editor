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
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.epos.metadata.editor.engine.converters.Classes2Model;
import org.epos.metadata.editor.engine.converters.Model2Classes;
import org.epos.metadata.editor.engine.io.FileReader;
import org.epos.metadata.editor.engine.io.FileWriter;
import org.epos.metadata.editor.engine.io.ModelReader;
import org.epos.metadata.editor.engine.model.ModelStored;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.vocabulary.SH;

public class IndividualTest {
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		String shapes = "https://raw.githubusercontent.com/epos-eu/EPOS-DCAT-AP/EPOS-DCAT-AP-shapes/epos-dcat-ap_draft_update.ttl";
		
		Engine e = new Engine.EngineBuilder()
				.setModelReader(new ModelReader(shapes))
				.setModel2Classes(new Model2Classes())
				.setClasses2Model(new Classes2Model())
				.setModelStored(new ModelStored())
				.setFileWriter(new FileWriter())
				.setFileReader(new FileReader())
				.build();
		
		OntModel m = JenaUtil.createOntologyModel(OntModelSpec.OWL_DL_MEM, JenaUtil.createDefaultModel());
		

		m.setNsPrefixes(Engine.getIstance().getMs().getShaclModel().getNsPrefixMap());
		
		Resource rstl = m.createResource("http://schema.org/Person");
		Individual individual = m.createIndividual("myid",rstl);
		Property p  = Engine.getIstance().getMs().getShaclModel().getProperty("http://schema.org/url");
		
		//System.out.println(p.isLiteral());
		//System.out.println(p.isAnon());
		//System.out.println(p.isResource());
		//System.out.println(p.isURIResource());

		//System.out.println(p.getProperty(SH.datatype));
		URI r = new URI("http://google.com");

		Literal l1 = null;
		l1 = m.createTypedLiteral(r);

		individual.addProperty(SH.datatype, l1);
		
		RDFDataMgr.write(System.out, m, Lang.TTL) ;
		
		
		
	}

}

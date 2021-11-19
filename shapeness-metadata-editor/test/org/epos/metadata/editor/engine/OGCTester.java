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

import java.io.File;
import java.io.IOException;

import org.apache.jena.riot.Lang;
import org.epos.metadata.editor.engine.converters.Classes2Model;
import org.epos.metadata.editor.engine.converters.Model2Classes;
import org.epos.metadata.editor.engine.converters.OGCWMSConverter;
import org.epos.metadata.editor.engine.io.FileReader;
import org.epos.metadata.editor.engine.io.FileWriter;
import org.epos.metadata.editor.engine.io.ModelReader;
import org.epos.metadata.editor.engine.model.ModelStored;

public class OGCTester {


	public static void main(String[] args) {
		testOGCWFS();
	}

	public static void testOGCWFS() {
		System.out.println("OGCWFSConverter");
		try {
			File source = new File("resources/ogc-wfs-to-epos-dcat-ap.xsl");//new File(("/Users/rossana/git/shapeness-metadata-editor/resources/ogc-wms-cap-2-epos-dcat-ap.xsl"));
			OGCWMSConverter.getInstance().setSchemaLocation(source.toURI());
			OGCWMSConverter.getInstance().generateFromOGCWMSCapabilitiesAddress("https://www.emidius.eu/services/europe/ows?service=wfs&request=GetCapabilities");

			/*String shapes = "https://raw.githubusercontent.com/epos-eu/EPOS-DCAT-AP/EPOS-DCAT-AP-shapes/epos-dcat-ap_shapes.ttl";
			Engine e = new Engine.EngineBuilder()
					.setModelReader(new ModelReader(shapes))
					.setModel2Classes(new Model2Classes())
					.setClasses2Model(new Classes2Model())
					.setModelStored(new ModelStored())
					.setFileWriter(new FileWriter())
					.setFileReader(new FileReader())
					.build();

			Engine.getIstance().getC2m().generateResources();
			Engine.getIstance().getWriter().writeModelToFile("testWFS", Lang.TURTLE, Engine.getIstance().getC2m().getOutputOntModel());
			*/
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void testOGCWMS() {
		String shapes = "https://raw.githubusercontent.com/epos-eu/EPOS-DCAT-AP/EPOS-DCAT-AP-shapes/epos-dcat-ap_shapes.ttl";
		Engine e = null;
		try {
			e = new Engine.EngineBuilder()
					.setModelReader(new ModelReader(shapes))
					.setModel2Classes(new Model2Classes())
					.setClasses2Model(new Classes2Model())
					.setModelStored(new ModelStored())
					.setFileWriter(new FileWriter())
					.setFileReader(new FileReader())
					.build();

			System.out.println("OGCWMSConverter");

			File source = new File("resources/ogc-wms-to-epos-dcat-ap.xsl");//new File(("/Users/rossana/git/shapeness-metadata-editor/resources/ogc-wms-cap-2-epos-dcat-ap.xsl"));
			OGCWMSConverter.getInstance().setSchemaLocation(source.toURI());

			OGCWMSConverter.getInstance().generateFromOGCWMSCapabilitiesAddress("https://www.emidius.eu/services/europe/ows?service=wms&request=GetCapabilities");
			//OGCWMSConverter.getInstance().generateFromOGCWMSCapabilitiesAddress("https://data.geoscience.earth/api/wmsModel?service=wms&request=GetCapabilities");
			//OGCWMSConverter.getInstance().generateFromOGCWMSCapabilitiesAddress("https://maps.eu-risk.eucentre.it/mapproxy/EU_Exposure_L0/ows?request=GetCapabilities");

			//OGCWMSConverter.getInstance().generateFromOGCWMSCapabilitiesAddress("https://peridot.geo-zs.si/EGDI/ows?service=wms&request=GetCapabilities");
			Engine.getIstance().getC2m().generateResources();
			Engine.getIstance().getWriter().writeModelToFile("test", Lang.TURTLE, Engine.getIstance().getC2m().getOutputOntModel());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}

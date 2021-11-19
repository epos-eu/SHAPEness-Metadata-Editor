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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Scanner;

import javax.xml.bind.JAXBException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.transform.XSLTransformer;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.vocabulary.SH;

import com.google.gson.Gson;

public class DataciteConverterTest {

	public static void main(String[] args) throws IOException {

		try {
			DataciteConverterTest.getInstance().setDataciteAddress("https://api.datacite.org/dois/application/vnd.datacite.datacite+json/10.6092/ingv.it-ahead");
		} catch (IOException | JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String dataciteItemXML;
	private String dataciteItemJSON;
	private String dataciteItemJSONLD;

	private String resultXML;

	private static DataciteConverterTest dataciteConverter;

	public static DataciteConverterTest getInstance() {
		if(dataciteConverter==null) dataciteConverter = new DataciteConverterTest();
		return dataciteConverter;
	}

	public void setDataciteAddress(String address) throws IOException, JAXBException {
		if(address.contains("ld+json")) this.dataciteItemJSONLD = address;
		else if(address.contains("xml")) this.dataciteItemXML = address;
		else this.dataciteItemJSON = address;	

		if(dataciteItemXML==null) {

			//System.out.println("JSON DOWNLOAD");
			if(dataciteItemJSONLD!=null) convertJSONLDtoXML();
			else convertJSONtoXML();
		} else {

			//System.out.println("XML DOWNLOAD");
			HttpURLConnection request1 = (HttpURLConnection) new URL(dataciteItemXML).openConnection();
			request1.setRequestMethod("GET");
			request1.connect();
			InputStream is = request1.getInputStream();
			dataciteItemXML = new Scanner(is,"UTF-8").useDelimiter("\\A").next();

		}

		//System.out.println("BEGIN CONVERT");
		try {
			convertXMLbyXSLT();
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}

		////System.out.println(resultXML);
	}

	private void convertJSONLDtoXML() throws IOException {
		//System.out.println("JSONLD TO XML DOWNLOAD");


		InputStream is = new URL(dataciteItemJSONLD).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
	        InputStream targetStream = new ByteArrayInputStream(jsonText.getBytes());

			Model inputFile = JenaUtil.createMemoryModel(); //MEMORY MODEL
			inputFile.read(targetStream, null, "JSON-LD"); // was "in"
		} finally {
			is.close();
		}
	}

	private void convertJSONtoXML() throws IOException, JAXBException {
		//System.out.println("JSON TO XML DOWNLOAD");
		InputStream is = new URL(dataciteItemJSON).openStream();

		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			//System.out.println("JSON READING");
			String jsonText = readAll(rd);
			/*JsonToXMLConverter converter = new JsonToXMLConverter();
			dataciteItemXML = converter.convertJsonToXml(jsonText);*/
			
			org.json.JSONObject jsonFileObject = new org.json.JSONObject(jsonText);
			dataciteItemXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<resource xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://datacite.org/schema/kernel-4\" xsi:schemaLocation=\"http://datacite.org/schema/kernel-4 http://schema.datacite.org/meta/kernel-4/metadata.xsd\">" 
	                     + org.json.XML.toString(jsonFileObject) + "</resource>";
		} finally {
			is.close();
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	private void convertXMLbyXSLT() throws JDOMException, IOException {

		//System.out.println("START CONVERT");
		SAXBuilder builder = new SAXBuilder();
		builder.setExpandEntities(false);     

		File xsl = null;
		xsl = new File("resources/datacite-to-dcat-ap.xsl");


		//System.out.println("THE INPUT: \n\n"+dataciteItemXML+"\n\n");

		XSLTransformer transformer = new XSLTransformer(xsl);
		Document toTransform = builder.build(new StringReader(dataciteItemXML));//builder.build(new File(folderLocation+"/tempfile.xml"));
		Document transformed = transformer.transform(toTransform);

		XMLOutputter outputter = new XMLOutputter();

		resultXML = outputter.outputString(transformed);

		//System.out.println("THE RESULT: \n\n"+resultXML+"\n\n");


		Model inputFile = JenaUtil.createMemoryModel(); //MEMORY MODEL

		//InputStream in = new FileInputStream(folderLocation+"/resultXML.xml");
		inputFile.read(new ByteArrayInputStream(resultXML.getBytes()), SH.BASE_URI, FileUtils.langXML); // was "in"

		StringWriter sw = new StringWriter();
		RDFDataMgr.write(sw, inputFile, Lang.TURTLE);
		OutputStream out = new FileOutputStream("resultTTL.ttl");
		RDFDataMgr.write(out, inputFile, Lang.TURTLE);

		//System.out.println("END");



		////System.out.println(resultXML);
	}

	public String getDataciteItemXML() {
		return dataciteItemXML;
	}

	public void setDataciteItemXML(String dataciteItemXML) {
		this.dataciteItemXML = dataciteItemXML;
	}

	public String getDataciteItemJSON() {
		return dataciteItemJSON;
	}

	public void setDataciteItemJSON(String dataciteItemJSON) {
		this.dataciteItemJSON = dataciteItemJSON;
	}

	public String getDataciteItemJSONLD() {
		return dataciteItemJSONLD;
	}

	public void setDataciteItemJSONLD(String dataciteItemJSONLD) {
		this.dataciteItemJSONLD = dataciteItemJSONLD;
	}


}

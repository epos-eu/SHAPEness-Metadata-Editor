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
package org.epos.metadata.editor.engine.converters;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.ui.utils.Util;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.transform.XSLTransformer;
import org.osgi.framework.Bundle;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.vocabulary.SH;


public class DataciteConverter {
	Logger log = Logger.getLogger("RCP_log");
	
	private String folderLocation;
	private URI schemaLocation;

	private String dataciteItemXML;
	private String resultXML;
	
	private String dataciteDOI_URL = "https://api.datacite.org/dois/application/vnd.datacite.datacite+xml/";

	private static DataciteConverter dataciteConverter;
	
	private DataciteConverter() {
		/*FileHandler fh;
		try {
			fh = new FileHandler("datacite.log");
		    log.addHandler(fh);
		    SimpleFormatter formatter = new SimpleFormatter();  
		    fh.setFormatter(formatter);  
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}  */
	}

	public static DataciteConverter getInstance() {
		
		if(dataciteConverter==null) dataciteConverter = new DataciteConverter();
		return dataciteConverter;
	}

	public void setFolderLocation(String folderLocation) {
		this.folderLocation = folderLocation;
	}

	public void generateFromDataciteAddress(String address) throws IOException {
		
		log.log(Level.INFO, "Address: "+address); 


		this.dataciteItemXML = dataciteDOI_URL+address;

		log.log(Level.INFO, "Address completo: "+dataciteItemXML);

	/*	IRunnableWithProgress op = new IRunnableWithProgress() {
			@SuppressWarnings("resource")
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				monitor.beginTask("Import metadata from DataCite ... ", 5);*/
				try {
					HttpURLConnection request1 = (HttpURLConnection) new URL(dataciteItemXML).openConnection();
					request1.setRequestMethod("GET");
					request1.connect();
					InputStream is = request1.getInputStream();
					dataciteItemXML = new Scanner(is,"UTF-8").useDelimiter("\\A").next();
					

					log.log(Level.INFO, "XML completo: "+dataciteItemXML);

				//	monitor.worked(1);
					//convertXMLbyXSLT();
				//	monitor.worked(2);
					generateNodes(convertXMLbyXSLT());
				//	monitor.worked(2);
				//	monitor.done();
				}catch(JDOMException | IOException e) {
					e.printStackTrace();
				}
			//}
	//	};

	/*IWorkbenchWindow win  = PlatformUI.getWorkbench().getActiveWorkbenchWindow();			

		try {
			new ProgressMonitorDialog(win.getShell()).run(true, false, op);
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			MessageDialog.openError(win.getShell(), "An error occured importing metadata from Datacite!", e.toString());
			return;
		}*/
	}

	private InputStream convertXMLbyXSLT() throws JDOMException, IOException {

		System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
		System.setProperty("file.encoding","UTF-8");
		java.lang.reflect.Field charset;
		try {
			charset = Charset.class.getDeclaredField("defaultCharset");
			charset.setAccessible(true);
			charset.set(null,null);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		//Bundle bundle = Platform.getBundle("org.epos.metadata.editor");
		SAXBuilder builder = new SAXBuilder();
		builder.setExpandEntities(false);     

		log.log(Level.INFO, "Schema location: "+schemaLocation);
		File xsl = new File(schemaLocation);
		//xsl = new File(FileLocator.resolve(bundle.getEntry("resources/datacite-to-epos-dcat-ap.xsl")).toURI());
		//xsl = new File(schemaLocation);

		XSLTransformer transformer = new XSLTransformer(xsl);
		Document toTransform = builder.build(new StringReader(dataciteItemXML));
		Document transformed = transformer.transform(toTransform);

		XMLOutputter outputter = new XMLOutputter();

		resultXML = outputter.outputString(transformed);
		
		log.log(Level.INFO, "XML result: "+resultXML);
		log.log(Level.INFO, "STEP 1");
		Model inputFile = JenaUtil.createMemoryModel(); //MEMORY MODEL
		log.log(Level.INFO, "STEP 2 "+inputFile);
		InputStream is = new ByteArrayInputStream(resultXML.getBytes());
		//InputStream is = IOUtils.toInputStream(resultXML, Charset.defaultCharset());
		log.log(Level.INFO, "STEP 2-a "+is);
		inputFile.read(is, SH.BASE_URI, FileUtils.langXML); // was "in"
		log.log(Level.INFO, "STEP 3");

		StringWriter sw = new StringWriter();
		log.log(Level.INFO, "STEP 4");
		RDFDataMgr.write(sw, inputFile, Lang.TURTLE);
		log.log(Level.INFO, "STEP 5");
		

		log.log(Level.INFO, "TTL result: "+sw.toString());
		/*OutputStream out = new FileOutputStream(folderLocation+"/resultTTL.ttl");
		RDFDataMgr.write(out, inputFile, Lang.TURTLE);*/
		return IOUtils.toInputStream(sw.toString(), Charset.defaultCharset());//new ByteArrayInputStream(sw.toString().getBytes(StandardCharsets.UTF_8));
	}

	private void generateNodes(InputStream is) {
		try {
			/*if(file==null) Engine.getIstance().getFreader().readFromFile(folderLocation+"/"+"resultTTL.ttl");
			else*/
			log.log(Level.INFO, "File input stream: "+is);
				Engine.getIstance().getFreader().readFromInputStream(is);

			//CANCELLARE FILE 
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}

	public String getDataciteItemXML() {
		return dataciteItemXML;
	}

	public void setDataciteItemXML(String dataciteItemXML) {
		this.dataciteItemXML = dataciteItemXML;
	}

	public URI getSchemaLocation() {
		return schemaLocation;
	}

	public void setSchemaLocation(URI schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

}

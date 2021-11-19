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
package org.epos.metadata.editor.engine.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileUtils;
import org.epos.metadata.editor.engine.model.ModelStored;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.vocabulary.SH;

public class ModelReader {

	private static final Logger logger = Logger.getGlobal();

	private String shapes;
	
	public ModelReader(String shapes) {
		this.shapes = shapes;
	}

	public void readSHACLModel(ModelStored ms) throws IOException {
		
		logger.info("Reader initialization");

		Model shaclModel = null;
		try {
			shaclModel = readFromURL(shapes);
		}catch(MalformedURLException mue) {
			shaclModel = readFromFile(shapes);
		} 

		if(shaclModel!=null) {
			ms.build(shaclModel, FileUtils.langTurtle);
		}else {
			logger.severe("Impossible read shacl"); 
		}

	}

	private Model readFromURL(String shapeURL) throws MalformedURLException {

		logger.info("Reading model from url: "+shapeURL);
		URL url = new URL(shapeURL);
		InputStream in = null;
		try {
			in = url.openStream();
		} catch (IOException e) {
			logger.severe(e.getLocalizedMessage());
		}

		Model shaclModel = JenaUtil.createMemoryModel(); //MEMORY MODEL
		shaclModel.read(in, SH.BASE_URI, FileUtils.langTurtle);

		return shaclModel;
	}

	private Model readFromFile(String shapePath) throws FileNotFoundException {
		
		logger.info("Reading model from file: "+shapePath);
		
		InputStream in = new FileInputStream(shapePath);

		Model shaclModel = JenaUtil.createMemoryModel(); //MEMORY MODEL
		shaclModel.read(in, SH.BASE_URI, FileUtils.langTurtle);

		return shaclModel;
	}
	

	/**
	 * @return the shapes
	 */
	public String getShapes() {
		return shapes;
	}

	/**
	 * @param shapes the shapes to set
	 */
	public void setShapes(String shapes) {
		this.shapes = shapes;
	}


}

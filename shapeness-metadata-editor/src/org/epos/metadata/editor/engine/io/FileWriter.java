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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.topbraid.jenax.util.JenaUtil;

public class FileWriter {

	public FileWriter() {}

	public void writeModelToSTDOut(Lang lang, Model m) {
		RDFDataMgr.write(System.out, m, RDFFormat.TURTLE_PRETTY) ;
	}

	public void writeModelToFile(String fileName, Lang lang, Model m) {

		try {
			OutputStream out = new FileOutputStream(fileName+".ttl");
			RDFDataMgr.write(out, m, RDFFormat.TURTLE_PRETTY) ;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String writeModelToString(Lang lang, Model m) {
		StringWriter sw = new StringWriter();
		RDFDataMgr.write(sw, m, RDFFormat.TURTLE_PRETTY);
		return sw.toString();
	}
}

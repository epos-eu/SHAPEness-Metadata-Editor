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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileUtils;
import org.epos.metadata.editor.engine.converters.Classes2Model;
import org.epos.metadata.editor.engine.converters.Model2Classes;
import org.epos.metadata.editor.engine.converters.beans.ModelClass;
import org.epos.metadata.editor.engine.io.ModelReader;
import org.epos.metadata.editor.engine.io.FileReader;
import org.epos.metadata.editor.engine.io.FileWriter;
import org.epos.metadata.editor.engine.model.ModelStored;
import org.epos.metadata.editor.engine.utils.ClassWriter;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.DataManager;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.vocabulary.SH;

public class Minitest{
	public static void main(String[] args) throws IOException {
		String input_url_1 = "https://raw.githubusercontent.com/vvalerio/test/master/EPOS-DCAT-AP_metadata_template.ttl";
		//String input_url_1 = "https://raw.githubusercontent.com/epos-eu/EPOS-DCAT-AP/EPOS-DCAT-AP-shapes/examples/EPOS-DCAT-AP_metadata_template.ttl";

		String input_url_2 = "https://raw.githubusercontent.com/epos-eu/EPOS-DCAT-AP/EPOS-DCAT-AP-shapes/examples/EPOS-DCAT-AP_metadata_template.ttl";

		URL url1 = new URL(input_url_1);
		InputStream is1=url1.openStream();

		URL url2 = new URL(input_url_2);
		InputStream is2=url2.openStream();
		Model m1 = JenaUtil.createMemoryModel();
		Model m2 = JenaUtil.createMemoryModel();

		m1.read(is1, SH.BASE_URI, FileUtils.langTurtle);
		m2.read(is2, SH.BASE_URI, FileUtils.langTurtle);

		StringWriter sw = new StringWriter();
		RDFDataMgr.write(sw, m1, Lang.TRIG);
		
		System.out.println(sw.toString());
		
		OutputStream out = new FileOutputStream("prova.ttl");
		RDFDataMgr.write(out, m1, Lang.TRIG) ;


	}

}

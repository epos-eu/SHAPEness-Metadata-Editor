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
package org.epos.metadata.editor.engine.checkers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;
import org.topbraid.shacl.vocabulary.SH;

/**
 * 
 * This checker validates a turtle graph agains its model
 * 
 * 
 * @author valerio
 *
 */
public class ShaclChecker extends Checker {

	public static boolean validate(Model schema, Model file) {
	    Shapes shapes = Shapes.parse(schema.getGraph());

	    ValidationReport report = ShaclValidator.get().validate(shapes, file.getGraph());
	    ShLib.printReport(report);
	    RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
	    ////System.out.println(report.getResource().getProperty(SH.conforms).getBoolean());
		
		return report.getResource().getProperty(SH.conforms).getBoolean();
	}

	@Override
	public boolean Validate(Model schema, Model file) {
		Shapes shapes = Shapes.parse(schema.getGraph());

	    ValidationReport report = ShaclValidator.get().validate(shapes, file.getGraph());
	    ShLib.printReport(report);
	    RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
	    ////System.out.println(report.getResource().getProperty(SH.conforms).getBoolean());
		
		return report.getResource().getProperty(SH.conforms).getBoolean();
	}
	
}

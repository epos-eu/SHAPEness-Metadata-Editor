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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.Lock;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.engine.converters.methods.Serializer;
import org.epos.metadata.editor.engine.model.ModelStored;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.DataManager;
import org.hibernate.validator.internal.properties.Field;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.vocabulary.SH;

/**
 * 
 * 
 * This class converts a set of objects created by the classes stored in memory, into a new model, used for generate the output
 * 
 * 
 * @author valerio
 *
 */
public class Classes2Model {

	private static final Logger logger = Logger.getGlobal();

	private Model outputModel;
	private OntModel outputOntModel;


	private ModelStored mc;

	/**
	 * Empty constructor
	 */
	public Classes2Model() {}

	/**
	 * 
	 * set the model to be used as output, copying the namespace from the input model to the output model
	 * 
	 * @param mc
	 */
	public void setModel(ModelStored mc) {
		this.outputModel = JenaUtil.createDefaultModel();
		this.mc = mc;

		outputModel.setNsPrefixes(mc.getShaclModel().getNsPrefixMap());

	}


	/**
	 * 
	 * This method generate the outputOntModel, copying the namespace from the input model to output model
	 * 
	 */
	private void buildOutputModel() {
		//this.outputOntModel =  JenaUtil.createOntologyModel(OntModelSpec.OWL_DL_MEM, JenaUtil.createDefaultModel());
		//this.outputOntModel.setNsPrefixes(mc.getShaclModel().getNsPrefixMap());

		this.outputModel = JenaUtil.createDefaultModel();
		this.outputModel.setNsPrefixes(mc.getShaclModel().getNsPrefixMap());
		this.outputOntModel = JenaUtil.createOntologyModel(OntModelSpec.RDFS_MEM, outputModel);

	}

	/**
	 * 
	 * This method generates resources by invoking the method serialize of each object in node list
	 *
	 */
	public void generateResources(){
		buildOutputModel(); //Builds a clean output model



		DataManager.getIstance().getNodes().parallelStream().forEach(e->{
			Resource r = mc.getResource(e.getClass().getSimpleName());
			if(r!=null) {
				String resourceClassObject = r.getProperty(SH.targetClass).asTriple().getObject().getURI();

				System.out.println("starting uri: "+resourceClassObject);
				try {
				Serializer.serialize(Engine.getIstance().getMs().getShaclModel(), outputOntModel, resourceClassObject, e);
				}
				catch(Exception e1) {
					e1.printStackTrace();
				}

				/*try {
					Method m = e.getClass().getMethod("serialize", new Class[]{ Model.class, Model.class, String.class });
					m.setAccessible(true);
					outputOntModel.getLock().enterCriticalSection(Lock.WRITE);
					m.invoke(e, new Object[] {mc.getShaclModel(), outputOntModel, resourceClassObject});
					outputOntModel.getLock().leaveCriticalSection();

				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e1) {
					logger.severe("Generate resources exception "+e1.getLocalizedMessage()+" message "+ e1.getMessage());
					e1.printStackTrace();
				} */
			}
		});
	}

	/**
	 * 
	 * 
	 * This method generates a single resource by invoking the method serialize of a single node n in node list
	 * 
	 * @param n
	 */
	public void generateSingleResource(Node n){

		DataManager.getIstance().getNodes().stream()
		.filter(e->e.getId().equals(n.getId()))
		.forEach(e->{
			Resource r = mc.getResource(e.getClass().getSimpleName());
			String resourceClassObject = r.getProperty(SH.targetClass).asTriple().getObject().getURI();

			try {
				Serializer.serialize(Engine.getIstance().getMs().getShaclModel(), outputOntModel, resourceClassObject, e);
				}
				catch(Exception e1) {
					e1.printStackTrace();
				}

			/*Method m;
			try {
				m = e.getClass().getMethod("serialize", new Class[]{ Model.class, OntModel.class, String.class });
				m.setAccessible(true);
				m.invoke(e, new Object[] {Engine.getIstance().getMs().getShaclModel(), outputOntModel, resourceClassObject});
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				logger.severe("Generate single resource exception "+e1.getLocalizedMessage());
			}*/
		});
	}


	/**
	 * @return the outputOntModel
	 */
	public Model getOutputOntModel() {
		return outputOntModel;
	}
}

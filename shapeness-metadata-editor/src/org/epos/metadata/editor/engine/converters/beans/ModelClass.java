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
package org.epos.metadata.editor.engine.converters.beans;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.engine.converters.methods.ClassTypeIdentifier;
import org.epos.metadata.editor.engine.converters.methods.ToString;
import org.epos.metadata.editor.ui.utils.Variables;
import org.topbraid.shacl.vocabulary.SH;

import com.google.gson.Gson;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;


/**
 * 
 * This method creates a class from the associated model
 * 
 * 
 * @author valerio
 *
 */
public class ModelClass {

	private static final Logger logger = Logger.getGlobal();

	private ClassPool pool = ClassPool.getDefault();

	private String className;
	private org.epos.metadata.editor.engine.converters.beans.Shape item;
	private ArrayList<CtClass> classes = new ArrayList<CtClass>();
	private ArrayList<CtClass> subClasses = new ArrayList<CtClass>();

	private String instanceName = Variables.MODEL_PATH_NODE.getValue();
	/* SERIALIZER METHOD */
	//Serializer serializer = new Serializer(instanceName);

	private ModelClass(ModelClassBuilder mcb) {
		this.className = mcb.className;
		this.item = mcb.item;
		className = item.getShapeName();
	}

	private Gson gson = new Gson();

	/**
	 * 
	 * @param object
	 * @return [name, namespace]
	 */
	private String[] getIsolatedValueRemovingNamespace(String object) {
		for(String value :  Engine.getIstance().getMs().getShaclModel().getNsPrefixMap().values()) 
			if(object.contains(value)) 
				return new String[] {object.replace(value, ""), value};
		return null;
	}

	public CtClass createClassObject() {

		////System.out.println(gson.toJson(item));

		CtClass cc = null;
		if(pool.getOrNull(Variables.MODEL_PATH.getValue()+className)!=null) {
			try {
				cc = pool.get(Variables.MODEL_PATH.getValue()+className);
			} catch (NotFoundException e) {
				logger.severe(e.getLocalizedMessage()+" "+e.getCause()+" "+e.getMessage());
			}
			cc.defrost();
		}
		else {
			cc = pool.makeClass(Variables.MODEL_PATH.getValue()+className);
		}

		CtClass superClass = null;
		try {
			superClass = pool.get(instanceName);
			cc.setSuperclass(superClass);
			cc.setModifiers(superClass.getModifiers());
		} catch (NotFoundException | CannotCompileException e) {
			logger.severe(e.getLocalizedMessage()+" "+e.getCause()+" "+e.getMessage());
		}

		HashMap<String,ArrayList<String>> ns = new HashMap<String, ArrayList<String>>();

		// START SERIALIZER
		/*try {
			serializer.addToSerializer(null, 0, null, null);
		} catch (NotFoundException e) {
			logger.severe(e.getLocalizedMessage()+" "+e.getCause()+" "+e.getMessage());
		}*/

		ArrayList<FieldBean> parameters = new ArrayList<FieldBean>();

		//ITERATE AMONG PROPERTIES
		for(Entry<String, ArrayList<Pair<String, Object>>> property : item.getProperties().entrySet()) {
			//else we are talking about fields
			FieldBean parameter = new FieldBean();
			for(Pair<String,Object> pair : property.getValue()) {
				exploreAndPopulate(parameter, pair, null);
			}

			boolean merged = false;

			for(FieldBean prmt : parameters) {
				if(prmt.getName()!=null && parameter.getName()!=null && prmt.getName().equals(parameter.getName())&& prmt.getNamespace().equals(parameter.getNamespace())) {
					try {
						mergeTwo(prmt,parameter);
					} catch (IllegalAccessException | InstantiationException e) {
						logger.severe(e.getLocalizedMessage()+" "+e.getCause()+" "+e.getMessage());
					}
					parameter = prmt;
					merged = true;
				}
			}
			if(!merged) parameters.add(parameter);
		}

		
		
		// FIX 
		for(FieldBean parameter : parameters) {

			//TIDE UP NAMESPACE
			//System.out.println(parameter.getFieldName()+" "+parameter.getNamespace());
			HashSet<String> nsTemp = new HashSet<String>(parameter.getNamespace());
			parameter.setNamespace(new ArrayList<String>());
			nsTemp.forEach(e->{
				if(Engine.getIstance().getMs().getShaclModel().containsResource(ResourceFactory.createResource(e+parameter.getOrginalName())))
						parameter.getNamespace().add(e);
			});
			
			if(parameter.getName() == null) parameter.setName("none");
			if(parameter.getNamespace()!=null) {
				ns.put(parameter.getName(), parameter.getNamespace());
			}
			if(parameter.getName() == null) parameter.setName("none");
	
			if(parameter.getName().equals("ToRemove") || parameter.getName().equals("none")) parameter.setFieldName(parameter.getName());
			else {
				if(parameter.getNamespace().size()==1){
					parameter.setFieldName(Engine.getIstance()
							.getMs()
							.getShortNS(ns.get(parameter.getName()).get(0).replaceAll("_", ""))+"_"+parameter.getName());
				}
				else {
					parameter.setFieldName(parameter.getName());
				}
						
			}
			/**
			 * This section generates CtClass from types strings, then remove the not-translated classes strings from original getTypes
			 */
			HashSet<CtClass> ctClassTypes = new HashSet<CtClass>();
			
			ArrayList<String> classToRemove = new ArrayList<String>();

			parameter.getType().forEach(tp -> {
				try {
					CtClass ctType = ClassTypeIdentifier.getType(tp, pool, subClasses);
					if(ctType!=null) {
						ctClassTypes.add(ctType);
					} else classToRemove.add(tp);
				} catch (NotFoundException e) {
					logger.severe(e.getLocalizedMessage()+" "+e.getCause()+" "+e.getMessage());
				}
			});
			parameter.getType().removeAll(classToRemove);
			
			parameter.generateCtField(cc, new ArrayList<CtClass>(ctClassTypes), null, ns, subClasses);

		}
		ToString.generateToString(cc);
		/*try {
			serializer.addToSerializer(null, 4, null, null);
		} catch (NotFoundException e) {
			logger.severe(e.getLocalizedMessage()+" "+e.getCause()+" "+e.getMessage());
		}*/
		//serializer.newSerializer();
		//serializer.createSerializer(cc);



		return cc;
	}

	
	public void mergeTwo(FieldBean local, FieldBean remote) throws IllegalAccessException, InstantiationException {
	  // FieldBean merged = new FieldBean();
	   
	   if(remote.getFieldName()!=null && local.getFieldName()!=null && local.getFieldName().equals(remote.getFieldName()))
		   local.setFieldName(local.getFieldName());
	   else if(local.getFieldName()==null && remote.getFieldName()!=null)
		   local.setFieldName(remote.getFieldName());
	   else if(remote.getFieldName()==null && local.getFieldName()!=null)
		   local.setFieldName(local.getFieldName());
	   
	   if(remote.getName()!=null && local.getName()!=null && local.getName().equals(remote.getName()))
		   local.setName(local.getName());
	   else if(local.getName()==null && remote.getName()!=null)
		   local.setName(remote.getName());
	   else if(remote.getName()==null && local.getName()!=null)
		   local.setName(local.getName());
	   
	   if(remote.getMaxCount()!=null && local.getMaxCount()!=null && local.getMaxCount().equals(remote.getMaxCount()))
		   local.setMaxCount(local.getMaxCount());
	   else if(local.getMaxCount()==null && remote.getMaxCount()!=null)
		   local.setMaxCount(remote.getMaxCount());
	   else if(remote.getMaxCount()==null && local.getMaxCount()!=null)
		   local.setMaxCount(local.getMaxCount());
	   
	   if(remote.getMinCount()!=null && local.getMinCount()!=null && local.getMinCount().equals(remote.getMinCount()))
		   local.setMinCount(local.getMinCount());
	   else if(local.getMinCount()==null && remote.getMinCount()!=null)
		   local.setMinCount(remote.getMinCount());
	   else if(remote.getMinCount()==null && local.getMinCount()!=null)
		   local.setMinCount(local.getMinCount());
	   
	   if(remote.getType()!=null && local.getType()!=null && local.getType().equals(remote.getType()))
		   local.setType(local.getType());
	   else  if(remote.getType()!=null && local.getType()!=null)
		   local.getType().addAll(local.getType());
	   else if(local.getType()==null && remote.getType()!=null)
		   local.setType(remote.getType());
	   else if(remote.getType()==null && local.getType()!=null)
		   local.setType(local.getType());
	   
	   if(remote.isWarning()!=null && local.isWarning()!=null && local.isWarning().equals(remote.isWarning()))
		   local.setWarning(local.isWarning());
	   else if(local.isWarning()==null && remote.isWarning()!=null)
		   local.setWarning(remote.isWarning());
	   else if(remote.isWarning()==null && local.isWarning()!=null)
		   local.setWarning(local.isWarning());
	   
	   
	   if(remote.isList()!=null && local.isList()!=null && local.isList().equals(remote.isList()))
		   local.setList(local.isList());
	   else if(local.isList()==null && remote.isList()!=null)
		   local.setList(remote.isList());
	   else if(remote.isList()==null && local.isList()!=null)
		   local.setList(local.isList());
	   
	   if(remote.getNamespace()!=null && local.getNamespace()!=null && local.getNamespace().equals(remote.getNamespace()))
		   local.setNamespace(local.getNamespace());
	   else if(remote.getNamespace()!=null && local.getNamespace()!=null)
		   local.getNamespace().addAll(local.getNamespace());
	   else if(local.getNamespace()==null && remote.getNamespace()!=null)
		   local.setNamespace(remote.getNamespace());
	   else if(remote.isList()==null && local.getNamespace()!=null)
		   local.setNamespace(local.getNamespace());
	}

	@SuppressWarnings("unchecked")
	private void exploreAndPopulate(FieldBean parameter, Pair<String,Object> pair, String origin) {
		if(pair.getRight().getClass().equals(String.class)) {
			if(pair.getLeft().equals(SH.path.toString())){
				String[] nameAndNamespace = getIsolatedValueRemovingNamespace(pair.getRight().toString());
				parameter.setName(Normalizer.normalize(nameAndNamespace[0], Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9]", ""));
				parameter.setOrginalName(nameAndNamespace[0]);
				//parameter.getNamespace().add(nameAndNamespace[1]);
				parameter.setPath_namespace(nameAndNamespace[1]);
			}
			if(pair.getLeft().equals(RDFS.range.toString())) {
				if(parameter.getName()==null) {
					String[] nameAndNamespace = getIsolatedValueRemovingNamespace(pair.getRight().toString());
					parameter.setOrginalName(nameAndNamespace[0]);
					parameter.setName(Normalizer.normalize(nameAndNamespace[0], Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9]", ""));
					parameter.getNamespace().add(nameAndNamespace[1]);
				}
			}
			if(pair.getLeft().equals(RDF.first.toString())) {
				String[] nameAndNamespace = getIsolatedValueRemovingNamespace(pair.getRight().toString());
				if(parameter.getName()==null) {
					////System.out.println("FIRST: "+pair.getRight().toString()+" "+pair.getLeft().toString());
					parameter.setOrginalName(nameAndNamespace[0]);
					parameter.setName(Normalizer.normalize(nameAndNamespace[0], Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9]", ""));
					parameter.getNamespace().add(nameAndNamespace[1]);
					parameter.getType().add(parameter.getName().substring(0, 1).toUpperCase() + parameter.getName().substring(1));
				}
				else {
					////System.out.println("FIRST ELSES: "+pair.getRight().toString()+" "+pair.getLeft().toString());
					parameter.getNamespace().add(nameAndNamespace[1]);
					parameter.getType().add(parameter.getName().substring(0, 1).toUpperCase() + parameter.getName().substring(1));
				}
			}
			if(pair.getLeft().equals(SH.node.toString())) {
				parameter.getType().add(getIsolatedValueRemovingNamespace(pair.getRight().toString().replace(SH.node+"_", ""))[0]);
				// COMPLEX
			}
			if(pair.getLeft().equals(SH.or.toString())) {
				exploreAndPopulate(parameter,  (Pair<String, Object>) pair.getRight(), pair.getLeft());
			}
			if(pair.getLeft().equals(SH.class_.toString())) {
				String[] nameAndNamespace = getIsolatedValueRemovingNamespace(pair.getRight().toString());
				parameter.getNamespace().add(nameAndNamespace[1]);
				parameter.getType().add(getIsolatedValueRemovingNamespace(pair.getRight().toString().replace(SH.class_+"_", ""))[0]);
			}
			if(pair.getLeft().equals(SH.datatype.toString())) {
				parameter.getType()
				.add(getIsolatedValueRemovingNamespace(pair.getRight().toString().replace(SH.datatype+"_", ""))[0]);
			}
			if(pair.getLeft().equals(SH.minCount.toString())) {
				parameter.setMinCount(Integer.parseInt(pair.getRight().toString().replace(SH.minCount+"_", "").split("\\^")[0]));
			}
			if(pair.getLeft().equals(SH.maxCount.toString())) {
				parameter.setMaxCount(Integer.parseInt(pair.getRight().toString().replace(SH.maxCount+"_", "").split("\\^")[0]));
			}
			if(pair.getRight().equals(SH.Warning.toString())) {
					parameter.setWarning(true);
					////System.out.println("Ho messo il warning su "+parameter.toString());
			}

		}
		if(pair.getRight().getClass().equals(ArrayList.class)) {
			for(Pair pIter : (ArrayList<Pair>) pair.getRight())
				exploreAndPopulate(parameter, pIter, origin==null? pair.getLeft() : origin);
		}
		if(pair.getRight().getClass().equals(Pair.class))
			exploreAndPopulate(parameter, (Pair<String, Object>) pair.getRight(), origin==null? pair.getLeft() : origin);
	}

	public ArrayList<CtClass> getClasses(){
		classes.add(createClassObject());
		return classes;
	}

	public ArrayList<CtClass> getSubClasses(){
		return subClasses;
	}

	public static Class<?> getAsClass(CtClass cl){
		try {
			return cl.toClass();
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class ModelClassBuilder{
		private String className;
		private org.epos.metadata.editor.engine.converters.beans.Shape item;


		public ModelClassBuilder setClassName(String className) {
			this.className = className;
			return this;
		}
		public ModelClassBuilder setPropertiesMap(org.epos.metadata.editor.engine.converters.beans.Shape item) {
			this.item = item;
			return this;
		}

		public ModelClass build() {
			return new ModelClass(this);
		}
	}
}

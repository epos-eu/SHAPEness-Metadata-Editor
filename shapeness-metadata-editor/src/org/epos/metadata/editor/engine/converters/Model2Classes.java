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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.ClassUtils;
import org.eclipse.osgi.internal.framework.ContextFinder;
import org.epos.metadata.editor.engine.converters.beans.ModelClass;
import org.epos.metadata.editor.engine.converters.beans.Shape;
import org.epos.metadata.editor.engine.converters.methods.ItemCollect;
import org.epos.metadata.editor.engine.model.ModelStored;
import org.epos.metadata.editor.ui.utils.Variables;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.vocabulary.SH;

import com.google.gson.Gson;

import javassist.CannotCompileException;
import javassist.CtClass;


/**
 * 
 * This class reads the input model and generates the classes to be used by the metadata editor
 * 
 * @author valerio
 *
 */
public class Model2Classes {

	private static final Logger logger = Logger.getGlobal();

	private ArrayList<CtClass> classesGenerated;
	private ArrayList<CtClass> subClassesGenerated;

	private ArrayList<Class<?>> classesGenerated_class;
	private ArrayList<Class<?>> subClassesGenerated_class;

	private ArrayList<Shape> sh;



	public ArrayList<CtClass> getClassesGenerated() {
		return classesGenerated;
	}

	public void setClassesGenerated(ArrayList<CtClass> classesGenerated) {
		this.classesGenerated = classesGenerated;
	}

	private void buildClasses() {
		classesGenerated_class = new ArrayList<Class<?>>();
		subClassesGenerated_class = new ArrayList<Class<?>>();

		//CLEANER
		ArrayList<CtClass> toRemove = new ArrayList<CtClass>();


		
		for(CtClass cc : classesGenerated) {
			for(CtClass cc2 : subClassesGenerated) {
				if(cc.getName().equals(cc2.getName())) {
					toRemove.add(cc2);
				}
			}
		}

		subClassesGenerated.removeAll(toRemove);

		for(CtClass cc : subClassesGenerated) {
			try {
				Class<?> c = cc.toClass();
				subClassesGenerated_class.add(c);
			} catch (CannotCompileException e) {
				logger.info(e.getLocalizedMessage()+" "+e.getReason()+" Attempting to load subClass "+cc.getName());
				try {
					subClassesGenerated_class.add(Class.forName(cc.getName()));
				} catch (ClassNotFoundException e1) {
					logger.severe("SubClassesGenerated "+cc.getName()+": "+ e1.getLocalizedMessage());
				}
			}
		}
		for(CtClass cc : classesGenerated) {
			////System.out.println(cc.getName()+" "+Arrays.toString(cc.getMethods()));
			try {
				cc.defrost();
				Class<?> c = cc.toClass();
				classesGenerated_class.add(c);
			} catch (CannotCompileException e) {
				logger.info(e.getLocalizedMessage()+" Reason: "+e.getReason()+"\n"+e.getCause()+"\nAttempting to load class "+cc.getName());
				try {
					classesGenerated_class.add(ClassUtils.getClass(cc.getName()));
					logger.info("Class correctly loaded "+classesGenerated_class.get(classesGenerated_class.size()-1));
				} catch (ClassNotFoundException e1) {
					logger.severe("Class NOT LOADED "+cc.getName()+": "+ e1.getLocalizedMessage()+"\nReason: "+e1.getCause()+" "+e1.getException());
				}
			}
		}
	}

	public ArrayList<Class<?>> getClasses(){
		return classesGenerated_class;
	}

	public ArrayList<Class<?>> getSubClasses(){
		return subClassesGenerated_class;
	}

	public Model2Classes() {
		classesGenerated = new ArrayList<CtClass>();
		subClassesGenerated = new ArrayList<CtClass>();
	}

	public void convert(ModelStored ms) {
		logger.info("Starting conversion...");

		sh = new ArrayList<Shape>();

		for(org.topbraid.shacl.engine.Shape rs : ms.getShapegraph().getRootShapes()){
			////System.out.println(rs.getShapeResource().getLocalName());
		//if(rs.getShapeResource().getLocalName().equals("QuantitativeValueShape")) { //TODO: REMOVE THAT
			rs.getShapeResource().listProperties().forEachRemaining(p->{
				ItemCollect.collect(p.asTriple(),ms.getShaclModel().getRDFNode(p.asTriple().getObject()),sh, false,JenaUtil.getResourceProperties(rs.getShapeResource(), SH.targetClass).get(0).getLocalName());
			});
		//	}
		};

		for(Shape s : sh) {
			ModelClass mc = new ModelClass.ModelClassBuilder()
					.setClassName(s.getShapeName())
					.setPropertiesMap(s)
					.build();
			classesGenerated.addAll(mc.getClasses());
			subClassesGenerated.addAll(mc.getSubClasses());

		}

		buildClasses();
	}


	public ArrayList<CtClass> getSubClassesGenerated() {
		return subClassesGenerated;
	}

	public void setSubClassesGenerated(ArrayList<CtClass> subClassesGenerated) {
		this.subClassesGenerated = subClassesGenerated;
	}

	public ArrayList<Shape> getSh() {
		return sh;
	}

	public void setSh(ArrayList<Shape> sh) {
		this.sh = sh;
	}

}


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
package org.epos.metadata.editor.engine.converters.methods;

import java.util.ArrayList;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.engine.converters.beans.Pair;
import org.epos.metadata.editor.engine.converters.beans.Shape;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.vocabulary.SH;

public class ItemCollect {

	public static void collect(Triple t, RDFNode node, ArrayList<Shape> shapes, boolean reverse, String shapeName) {

		Shape s =null;
		boolean exists = false;
		for(Shape shape : shapes) {
			if(reverse) {
				if(shape.getShapeID().equals(t.getObject().getURI())) {
					s = shape;
					exists = true;
				}
			}else {
				if(shape.getShapeID().equals(t.getSubject().getURI())) {
					s = shape;
					exists = true;
				}
			}
		}

		if(!exists) {
			s = new Shape();
			if(reverse) s.setShapeID(t.getObject().getURI());
			else s.setShapeID(t.getSubject().getURI());
			shapes.add(s);
		}


		if(shapeName!=null) 
			s.setShapeName(shapeName);

		if(node.isResource()) {
			for(Statement p : node.asResource().listProperties().toList()){
				if(!s.getProperties().containsKey(p.getSubject().toString()))
					s.getProperties().put(p.getSubject().toString(), new ArrayList<Pair<String,Object>>());
				//CHECK IF EXISTS ITEM IN PREVIOUS
				if(p.getObject().isAnon()) {
					s.getProperties().get(p.getSubject().toString())
					.add(recursivelyAddPairs(p.getObject(), new Pair<String, Object>(p.getPredicate().toString(), p.getObject())));

				}
				else {

					
					if(node.asResource().getProperty(SH.class_)==null && node.asResource().getProperty(SH.datatype)==null &&p.getObject().toString().contains(Engine.getIstance().getMs().getRootOntology())) {
						s.getProperties().get(p.getSubject().toString())
						.add(recursivelyAddPairs(p.getObject(), new Pair<String, Object>(p.getPredicate().toString(), p.getObject())));
					}

					else s.getProperties().get(p.getSubject().toString())
					.add(new Pair<String,Object>(p.getPredicate().toString(),p.getObject().toString()));

					/*if(p.getObject().toString().contains(Engine.getIstance().getMs().getRootOntology())) {
						s.getProperties().get(p.getSubject().toString())
						.add(recursivelyAddPairs(p.getObject(), new Pair<String, Object>(p.getPredicate().toString(), p.getObject())));
					} else {*/
					//s.getProperties().get(p.getSubject().toString())
					//.add(new Pair<String,Object>(p.getPredicate().toString(),p.getObject().toString()));
					//}
				}
			}
		}
	}

	/// pair (predicate, id)
	////  				pair(predicate, id)

	/// == pair(predicate, pair(preidcate, id))

	private static Pair<String,Object> recursivelyAddPairs(RDFNode node, Pair<String,Object> pair){
		if(node.asResource().listProperties().toList().size()>1){
			ArrayList<Pair> pairs = new ArrayList<Pair>();
			for(Statement p : node.asResource().listProperties().toList()){
				if(p.getObject().isAnon()) {
					pairs.add(new Pair<String, Object>(p.getPredicate().toString(), recursivelyAddPairs(p.getObject(), new Pair<String, Object>(p.getPredicate().toString(), p.getObject().toString()))));
				}
				else {
					pairs.add(new Pair<String, Object>(p.getPredicate().toString(), p.getObject().toString()));
				}
			}
			pair = new Pair<String, Object>(pair.getLeft(),pairs);
		}
		else {
			Pair<String, Object> singlePair = null;
			for(Statement p : node.asResource().listProperties().toList()){
				if(p.getObject().isAnon()) {
					singlePair = new Pair<String, Object>(p.getPredicate().toString(),recursivelyAddPairs(p.getObject(), new Pair<String, Object>(p.getPredicate().toString(), p.getObject().toString())));
				}
				else {
					singlePair = new Pair<String, Object>(p.getPredicate().toString(), p.getObject().toString());
				}
			}
			pair = new Pair<String, Object>(pair.getLeft(),singlePair);
		}

		return pair;

	}

}

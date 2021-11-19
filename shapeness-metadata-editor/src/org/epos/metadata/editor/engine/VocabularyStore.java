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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.engine.Shape;
import org.topbraid.shacl.model.SHPropertyShape;
import org.topbraid.shacl.vocabulary.SH;

public class VocabularyStore {

	private HashMap<String, Model> store;
	private String folder;

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public VocabularyStore(String folder) {
		this.store = new HashMap<String, Model>();
		this.folder = folder;
	}

	public void initializeStore(Map<String, String> nsPrefixMap) {
		for (final File fileEntry : new File(folder).listFiles()) {
			for(String key : nsPrefixMap.keySet()) {
				//if(nsPrefixMap.keySet().contains(item.split("\\/")[1].split("\\.")[0])) {
				if(fileEntry.getName().split("\\.")[0].contains(key)) {
					InputStream in = null;
					try {
						in = new FileInputStream(folder+fileEntry.getName());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					Model vocModel = JenaUtil.createMemoryModel(); //MEMORY MODEL
					vocModel.read(in, SH.BASE_URI, FileUtils.langTurtle);
					store.put(fileEntry.getName().split("\\.")[0], vocModel);
				}
			}
		}

	}

	public String searchCommentByTermAndVocabulary(String term, String vocabulary) {

		String shortVocs = Engine.getIstance().getMs().getShortNS(vocabulary);

		for(Entry<String, Model> e : store.entrySet()) {
			if(e.getKey().contains(shortVocs)) {
				for(Statement p : e.getValue().getResource(vocabulary+term).listProperties().toList()){
					return p.getProperty(RDFS.comment).getObject().toString();
				}
			}
		}
		return null;
	}


	public String searchDescriptionOrCommentByString(String class_, String term) {
		String result = searchDescriptionByString(class_, term);
		return (result==null)? searchCommentByString(term) : result;
	}

	public String searchDescriptionByString(String class_, String term) {
		String[] splittedTerm = term.split("\\:");
		String termToSearch = splittedTerm.length>1? splittedTerm[1] : splittedTerm[0];

		String termReturn = null;
		for(Shape shp : Engine.getIstance().getMs().getShapegraph().getRootShapes()){
			if(shp.getShapeResource().getLocalName().contains(class_)){
				for(SHPropertyShape shpr : shp.getShapeResource().getPropertyShapes()){
					if(shpr.getProperty(SH.node)!=null && shpr.getProperty(SH.node).getObject().toString().toLowerCase().contains(term.toLowerCase())) {
						termReturn = shpr.getDescription();
					}
					if(shpr.getVarName()!=null && shpr.getVarName().equals(termToSearch)){
						termReturn = shpr.getDescription();
					}

				}
			}
		}
		return termReturn;
	}

	public String searchCommentByString(String term) {
		String[] splittedTerm = term.split("\\:");


		for(Entry<String, Model> e : store.entrySet()) {
			if(e.getKey().contains(splittedTerm[0])) {
				for(Statement p : e.getValue().getResource(Engine.getIstance().getMs().getShaclModel().getNsPrefixMap().get(splittedTerm[0])+splittedTerm[1]).listProperties().toList()) {
					if(p.getPredicate().equals(RDFS.comment)){
						return p.getObject().toString();
					}
				}
			}
		}
		return null;
	}

}

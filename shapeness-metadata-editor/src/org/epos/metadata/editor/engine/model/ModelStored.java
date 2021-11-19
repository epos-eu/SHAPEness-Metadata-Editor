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
package org.epos.metadata.editor.engine.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ext.xerces.xs.XSLoader;
import org.apache.jena.iri.IRI;
import org.apache.jena.irix.SetupJenaIRI;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.util.FileManager;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.ui.utils.Variables;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.engine.ShapesGraph;
import org.topbraid.shacl.vocabulary.SH;


public class ModelStored {

	private static final Logger logger = Logger.getGlobal();

	private Model shaclModel;
	private OntModel shaclOntModel;
	private String type;
	private ShapesGraph shapegraph;
	private List<OntClass> itClasses;
	private Shapes shapes;

	/**
	 * Build a ModelStored object:
	 * - import  a model
	 * - set Language of the model
	 * 
	 * 
	 * @param m
	 * @param type
	 */
	public void build(Model m, String type) {
		logger.info("Building...");
		this.shaclModel = m;
		this.type = type;

		// Create Ontology Model
		logger.info("Generate ontology model");
		this.shaclOntModel = JenaUtil.createOntologyModel(OntModelSpec.OWL_DL_MEM, shaclModel);
		this.itClasses = this.shaclOntModel.listClasses().toList();

		logger.info("Generate shapes...");
		shapegraph = new ShapesGraph(shaclModel);


	}

	/**
	 * 
	 * Returns NameSpace 
	 * 
	 * @param shapeName
	 * @param propertyName
	 * @return
	 */
	public String getNameSpaceFromShapeAndProperty(String shapeName, String propertyName) {
		for(org.topbraid.shacl.engine.Shape rs : shapegraph.getRootShapes()){
			if(rs.getShapeResource().getLocalName().contains(shapeName)) {
				return findProperty(shaclModel.getRDFNode(rs.getShapeResource().asNode()), propertyName);
			}
		};
		return null;
	}

	/**
	 * 
	 * Returns the property URI
	 * 
	 * @param node
	 * @param propertyName
	 * @return
	 */
	private String findProperty(RDFNode node, String propertyName) {
		while(node.asResource().listProperties().hasNext()) {
			Statement prop = node.asResource().listProperties().next();
			if(prop.asTriple().getObject().isURI() && prop.asTriple().getObject().getURI().contains(propertyName)) {
				return prop.asTriple().getObject().getURI();
			}
			else return findProperty(prop.getObject(), propertyName);
		}
		return null;
	}

	/**
	 * 
	 * Returns the short name of vocabulary
	 * 
	 * @param uri
	 * @return
	 */
	public String getShortNS(String uri) {
		for(String value :  shaclOntModel.getNsPrefixMap().values()) {
			if(uri.contains(value)) {
				for(Entry<String, String> e : shaclOntModel.getNsPrefixMap().entrySet()){
					if(e.getValue().equals(value) && !e.getKey().isBlank()) {
						return  e.getKey();
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * Return the root ontology
	 * 
	 * @return the root ontology
	 */
	/**
	 * @return
	 */
	public String getRootOntology() {
		String rootOntology = null;
		for(org.topbraid.shacl.engine.Shape rs : shapegraph.getRootShapes()){
			if(!rs.getShapeResource().getNameSpace().equals(rootOntology))
				rootOntology=rs.getShapeResource().getNameSpace();
		};
		return rootOntology;
	}

	
	private static ArrayList<String> dcs = new ArrayList<String>();
	
	static {
		dcs.add("https://raw.githubusercontent.com/dcmi/vocabtool/master/build/dcelements.ttl");
		dcs.add("https://raw.githubusercontent.com/dcmi/vocabtool/master/build/dcam.ttl");
		dcs.add("https://raw.githubusercontent.com/dcmi/vocabtool/master/build/dcterms.ttl");
		dcs.add("https://raw.githubusercontent.com/dcmi/vocabtool/master/build/dctype.ttl");
	}
	
	
	/**
	 * 
	 * This method is able to download vocabularies from the model PrefixMap
	 * 
	 * @param map
	 */
	public void downloadVocab(Map<String, String> map, String folder, HashMap<String,String> totNSs) {
		map.entrySet().forEach(ns->{
			String fileName = ns.getKey()+".ttl";
			File f = new File(folder+fileName);
			String rootNS = Engine.getIstance().getMs().getShortNS(Engine.getIstance().getMs().getRootOntology());
			
			totNSs.put(ns.getKey().toString(), ns.getValue().toString());

			if(fileName.endsWith("xhv.ttl") 
					|| fileName.endsWith(" .ttl") 
					|| fileName.endsWith("rdakit.ttl")
					|| fileName.endsWith("xml.ttl")
					|| fileName.endsWith("dc11.ttl")
					|| fileName.endsWith("msg0.ttl")
					|| fileName.endsWith("admin.ttl")
					|| fileName.endsWith("cc.ttl")
					|| fileName.endsWith("snomed.ttl")
					|| fileName.endsWith("status.ttl")
					|| fileName.endsWith("ex.ttl")
					|| fileName.endsWith("iana.ttl")) {
				
			} else if(fileName.endsWith("regap.ttl")) {
				Model m2 = FileManager.getInternal().loadModelInternal("https://raw.githubusercontent.com/arnau/vocabularies/master/vendor/vann.ttl");

				if(!f.exists()) {
					try (OutputStream out = new FileOutputStream(folder+fileName) ) { 
						RDFDataMgr.write(out, m2, Lang.TTL); 
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				Map<String, String> subMap =  m2.getNsPrefixMap().entrySet().stream()
						.filter(e -> !totNSs.containsKey(e.getKey()))
					      .collect(Collectors.toMap(e -> e.getKey(),
					        e -> e.getValue()));
				
				if(subMap.size()>0) downloadVocab(subMap, folder, totNSs);
			} else if(fileName.endsWith("vann.ttl")) {
				Model m2 = FileManager.getInternal().loadModelInternal("https://raw.githubusercontent.com/arnau/vocabularies/master/vendor/vann.ttl");

				if(!f.exists()) {
					try (OutputStream out = new FileOutputStream(folder+fileName) ) { 
						RDFDataMgr.write(out, m2, Lang.TTL); 
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				Map<String, String> subMap =  m2.getNsPrefixMap().entrySet().stream()
						.filter(e -> !totNSs.containsKey(e.getKey()))
					      .collect(Collectors.toMap(e -> e.getKey(),
					        e -> e.getValue()));
				if(subMap.size()>0) downloadVocab(subMap, folder, totNSs);
			} else if(fileName.endsWith("schema.ttl")) {
				Model m2 = FileManager.getInternal().loadModelInternal("https://raw.githubusercontent.com/vvalerio/test/master/model/schema.ttl");

				if(!f.exists()) {
					try (OutputStream out = new FileOutputStream(folder+fileName) ) { 
						RDFDataMgr.write(out, m2, Lang.TTL); 
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				Map<String, String> subMap =  m2.getNsPrefixMap().entrySet().stream()
						.filter(e -> !totNSs.containsKey(e.getKey()))
					      .collect(Collectors.toMap(e -> e.getKey(),
					        e -> e.getValue()));
				downloadVocab(subMap, folder, totNSs);
			} else if(fileName.endsWith("xsd.ttl")) {
				//Model m2 = FileManager.get().loadModel("https://datarefuge.s3.amazonaws.com/resources/8ac1b529-8dd6-4d94-a418-e3b705fc3837/xsd.ttl?Signature=mC%2FOOriQGtvi1AfsnipaGP45Gkw%3D&Expires=1586454167&AWSAccessKeyId=AKIAJDTJSQES6C6WZYEQ");
				Model m2 = FileManager.getInternal().loadModelInternal("https://raw.githubusercontent.com/harryhaaren/lv2/master/schemas/xsd.ttl");
				if(!f.exists()) {
					try (OutputStream out = new FileOutputStream(folder+fileName) ) { 
						RDFDataMgr.write(out, m2, Lang.TTL); 
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				Map<String, String> subMap =  m2.getNsPrefixMap().entrySet().stream()
						.filter(e -> !totNSs.containsKey(e.getKey()))
					      .collect(Collectors.toMap(e -> e.getKey(),
					        e -> e.getValue()));
				if(subMap.size()>0) downloadVocab(subMap, folder, totNSs);
			}else if(fileName.endsWith("dc.ttl")) {
				//DUBLIN CORE BUNDLE
				for(String purl : dcs) {
					Model m2 = FileManager.getInternal().loadModelInternal(purl);
					String[] fileNamePurl = purl.split("\\/");
					try (OutputStream out = new FileOutputStream(folder+fileNamePurl[fileNamePurl.length-1]) ) { 
						RDFDataMgr.write(out, m2, Lang.TTL); 
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					Map<String, String> subMap =  m2.getNsPrefixMap().entrySet().stream()
							.filter(e -> !totNSs.containsKey(e.getKey()))
						      .collect(Collectors.toMap(e -> e.getKey(),
						        e -> e.getValue()));
					if(subMap.size()>0) downloadVocab(subMap, folder, totNSs);
				}
			}else if(fileName.endsWith("spdx.ttl")) {
				Model m2 = FileManager.getInternal().loadModelInternal("https://spdx.org/rdf/spdx-2-1-rev7.owl.xml");

				if(!f.exists()) {
					try (OutputStream out = new FileOutputStream(folder+fileName) ) { 
						RDFDataMgr.write(out, m2, Lang.TTL); 
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				Map<String, String> subMap =  m2.getNsPrefixMap().entrySet().stream()
						.filter(e -> !totNSs.containsKey(e.getKey()))
					      .collect(Collectors.toMap(e -> e.getKey(),
					        e -> e.getValue()));
				if(subMap.size()>0) downloadVocab(subMap, folder, totNSs);
			}
			else {
				if(!ns.getKey().equals(rootNS)) {
					IRI irir =  SetupJenaIRI.iriCheckerFactory().create(ns.getValue().toString());
					try {
						
						Model m2 = FileManager.getInternal().loadModelInternal(irir.toURI().toString());
						if(!f.exists()) {
							try (OutputStream out = new FileOutputStream(folder+fileName) ) { 
								RDFDataMgr.write(out, m2, Lang.TTL); 
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						Map<String, String> subMap =  m2.getNsPrefixMap().entrySet().stream()
								.filter(e -> !totNSs.containsKey(e.getKey()))
							      .collect(Collectors.toMap(e -> e.getKey(),
							        e -> e.getValue()));
						if(subMap.size()>0) downloadVocab(subMap, folder, totNSs);
					}catch(Exception e) {
						//System.out.println(e.getLocalizedMessage());
					}
				}
			}
		});
	}

	/**
	 * 
	 * Return a shape as a resource
	 * 
	 * @param simpleName
	 * @return
	 */
	public Resource getResource(String simpleName) {
		for(org.topbraid.shacl.engine.Shape rs : shapegraph.getRootShapes()){
			////System.out.println("SIMPLE NAME --> "+simpleName+" SHAPERESOURCE --> "+rs.getShapeResource());
			String baseName = rs.getShapeResource().getLocalName().replace("Shape", "");
			if(baseName.equals(simpleName)) {
				////System.out.println("SIMPLE NAME SELECTED --> "+simpleName+" SHAPERESOURCE --> "+rs.getShapeResource());
				return rs.getShapeResource().asResource();
			}
		}
		return null;
	}

	/**
	 * 
	 * Return a resource that is associated to an Ontology Class, starting by a term and/or a vocabulary
	 * The vocabulary argument could be null
	 * 
	 * 
	 * @param term
	 * @param vocabulary
	 * @return
	 */
	public Resource getClassByName(String term, String vocabulary) {
		if(vocabulary == null) {
			List<Resource> result = itClasses.stream()
					.filter(e->e.getURI()!=null && StringUtils.containsIgnoreCase(e.getURI(),term))
					.collect(Collectors.toList());  

			if(result.size()>0) return result.get(0);

		}else {
			OntClass oc = this.shaclOntModel.getOntClass(this.shaclModel.getNsPrefixURI(vocabulary)+term);
			return oc.asResource();
		}
		return null;
	}

	/**
	 * 
	 * Returns the vocabulary used by a property
	 * 
	 * @param simpleName
	 * @param propName
	 * @return
	 */
	public String getPropertyVocab(String simpleName, String propName) {
		for(org.topbraid.shacl.engine.Shape rs : shapegraph.getRootShapes()){
			if(rs.getShapeResource().getLocalName().contains(simpleName+"Shape")) {
				for(Statement stm : rs.getShapeResource().listProperties().toList()) {
					if(stm.getPredicate().equals(SH.property)){
						for(Statement stm2 :stm.getObject().asResource().listProperties().toList()) {
							if(stm2.getPredicate().equals(SH.path) && stm2.getObject().toString().contains(propName)) {
								return stm2.getObject().asResource().getURI();
							}
						}
					}
				}
			}
		};
		return null;
	}

	public String getClassVocab(String simpleName) {
		for(org.topbraid.shacl.engine.Shape rs : shapegraph.getRootShapes()){
			if(rs.getShapeResource().getLocalName().contains(simpleName+"Shape")) {
				for(Statement stm : rs.getShapeResource().listProperties().toList()) {
					if(stm.getPredicate().equals(SH.targetClass)){
						return stm.getObject().toString();
					}
				}
			}
		};
		return null;
	}
	
	public List<OntClass> getItClasses() {
		return itClasses;
	}

	public void setItClasses(List<OntClass> itClasses) {
		this.itClasses = itClasses;
	}


	/**
	 * @return the shaclModel
	 */
	public Model getShaclModel() {
		return shaclModel;
	}

	/**
	 * @param shaclModel the shaclModel to set
	 */
	public void setShaclModel(Model shaclModel) {
		this.shaclModel = shaclModel;
	}

	/**
	 * @return the shaclOntModel
	 */
	public OntModel getShaclOntModel() {
		return shaclOntModel;
	}

	/**
	 * @param shaclOntModel the shaclOntModel to set
	 */
	public void setShaclOntModel(OntModel shaclOntModel) {
		this.shaclOntModel = shaclOntModel;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the shapegraph
	 */
	public ShapesGraph getShapegraph() {
		return shapegraph;
	}

	/**
	 * @param shapegraph the shapegraph to set
	 */
	public void setShapegraph(ShapesGraph shapegraph) {
		this.shapegraph = shapegraph;
	}

	public Shapes getShapes() {
		return shapes;
	}

	public void setShapes(Shapes shapes) {
		this.shapes = shapes;
	}


}

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.jena.iri.IRI;
import org.apache.jena.irix.SetupJenaIRI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.util.FileManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class CopyVocabulariesThread implements IRunnableWithProgress {

	private Map<String, String> map;
	private String folder;
	private HashMap<String,String> totNSs;

	private ArrayList<String> ignoreList = new  ArrayList<String>();
	private HashMap<String,ArrayList<String>> vocabularyList = new HashMap<String, ArrayList<String>>();

	public CopyVocabulariesThread(Map<String, String> map, String folder, HashMap<String,String> totNSs)
	{
		this.map = map;
		this.folder = folder;
		this.totNSs = totNSs;

		try  
		{  
			URL url = new URL("https://raw.githubusercontent.com/vvalerio/MDE/master/ignoreList.txt");
	        BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;  
			while((line=read.readLine())!=null)  
			{  
				ignoreList.add(line);
			}  
			read.close();    //closes the stream and release the resources  

			url = new URL("https://raw.githubusercontent.com/vvalerio/MDE/master/vocabularycsv.txt");
	        read = new BufferedReader(new InputStreamReader(url.openStream()));
			while((line=read.readLine())!=null)  
			{  
				String[] splitLine = line.split("\\,");
				if(vocabularyList.get(splitLine[0])==null) {
					vocabularyList.put(splitLine[0], new ArrayList<String>());
				}
				vocabularyList.get(splitLine[0]).add(splitLine[1]);
			}  
			read.close();    //closes the stream and release the resources  
		}  
		catch(IOException e)  
		{  
			e.printStackTrace();  
		}  
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
	{
		// Tell the user what you are doing
		int workload = map.entrySet().size();
		monitor.beginTask("", workload);
		downloadVocab(map,folder, totNSs, monitor);

		// You are done
		monitor.done();
	}

	public void downloadVocab(Map<String, String> map, String folder, HashMap<String,String> totNSs, IProgressMonitor monitor) {

		map.entrySet().forEach(ns->{

			String fileName = ns.getKey()+".ttl";
			File f = new File(folder+fileName);

			////System.out.println(ns.getKey()+" "+fileName+" "+ns.getValue());

			//monitor.subTask("Copying vocabulary " + sum.get() + " of "+ map.entrySet().size() + "...");
			monitor.subTask("Downloading vocabulary " + ns.getValue() + "...");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// Tell the monitor that you successfully finished one item of "workload"-many
			monitor.worked(1);

			// Check if the user pressed "cancel"
			if(monitor.isCanceled())
			{
				monitor.done();
				return;
			}
			
			//String rootNS = Engine.getIstance().getMs().getShortNS(Engine.getIstance().getMs().getRootOntology());

			totNSs.put(ns.getKey().toString(), ns.getValue().toString());

			if(!ignoreList.contains(fileName)) {
				if(vocabularyList.keySet().contains(fileName.replaceAll(".ttl", ""))) {
					for(String value : vocabularyList.get(fileName.replaceAll(".ttl", ""))){
						Model m2 = FileManager.getInternal().loadModelInternal(value);

						if(!f.exists()) {
							try (OutputStream out = new FileOutputStream(folder+fileName) ) { 
								RDFDataMgr.write(out, m2, Lang.TTL); 
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						/*Map<String, String> subMap =  m2.getNsPrefixMap().entrySet().stream()
						.filter(e -> !totNSs.containsKey(e.getKey()))
						.collect(Collectors.toMap(e -> e.getKey(),
								e -> e.getValue()));
				if(subMap.size()>0) downloadVocab(subMap, folder, totNSs, monitor);*/
					}
				}else {
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
						/*
						Map<String, String> subMap =  m2.getNsPrefixMap().entrySet().stream()
								.filter(e -> !totNSs.containsKey(e.getKey()))
								.collect(Collectors.toMap(e -> e.getKey(),
										e -> e.getValue()));
						if(subMap.size()>0) downloadVocab(subMap, folder, totNSs, monitor);*/
					}catch(Exception e) {
						//System.out.println(e.getLocalizedMessage());
					}
				}
			}
		});
	}


}

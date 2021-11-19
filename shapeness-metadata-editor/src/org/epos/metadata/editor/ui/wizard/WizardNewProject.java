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
package org.epos.metadata.editor.ui.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.riot.Lang;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.xtext.ui.XtextProjectHelper;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.model.NodeFilter;
import org.epos.metadata.editor.ui.model.Project;
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.utils.Variables;
import org.epos.metadata.editor.ui.views.CompositeGraphFilter;
import org.epos.metadata.editor.ui.views.ViewGraph;
import org.epos.metadata.editor.ui.views.ViewGraphPalette;
import org.epos.metadata.editor.ui.views.ViewOutline;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class WizardNewProject extends Wizard {
	


	protected WizardImportShacl one;
	protected WizardImportVocabularies two;
	Logger log = Logger.getLogger("RCP_log");
	
	private Project dataProject;

	public WizardNewProject() {
		super();
		/*FileHandler fh;
		try {
			fh = new FileHandler("MetadataLogs.log");
		    log.addHandler(fh);
		    SimpleFormatter formatter = new SimpleFormatter();  
		    fh.setFormatter(formatter);  
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}  */
		setNeedsProgressMonitor(true);
	}


	public String getWindowTitle() {
		return "New Project";
	}

	@Override
	public void addPages() {
		dataProject = new Project();
		one = new WizardImportShacl(dataProject);
		two = new WizardImportVocabularies(dataProject);

		addPage(one);
		addPage(two);

	}

	@Override
	public boolean canFinish(){
		if(getContainer().getCurrentPage() == one)
			return false;
		else if(getContainer().getCurrentPage() == two && two.isPageComplete()) {
			return true;
		} else return false;


	}


	@Override
	public boolean performFinish() {
		finishWizard();
		return true;
	}



	private void finishWizard() {
		DataManager.getIstance().getNodes().clear();
		DataManager.getIstance().getConnections().clear();

		// close editor
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editors = activePage.getEditorReferences();
		if(editors.length > 0) {
			activePage.closeEditor(editors[0].getEditor(true), true);
			activePage.setEditorAreaVisible(false);
			ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
			Command command = service.getCommand("org.epos.metadata.editor.showTurtle");
			State state = command.getState("org.eclipse.ui.commands.toggleState");
			state.setValue(false);
		}

		IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
		context.set(Project.class, dataProject);

		try {
			getContainer().run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Building project...", 10);
					if(dataProject.isNewFile()) {
						createNewTurtleProject(dataProject, monitor);
					} else {
						importTurtleFile(dataProject,monitor);
					}

					monitor.done();

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}



		try {

			ViewGraphPalette palette = (ViewGraphPalette) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewPalette");
			palette.createPaletteItems(dataProject.getShapesList());
			//IActionBars bars = view.getViewSite().getActionBars();
			//bars.getStatusLineManager().setMessage("Model loaded");

			ViewOutline outline = (ViewOutline) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewOutline");
			outline.setShapes(dataProject.getShapesList());



		} catch (PartInitException e) {
			log.log(Level.SEVERE, e.getMessage()); 

		}

		// Create filters for shapes
		DataManager.getIstance().getFilters().clear();
		CompositeGraphFilter.clearAllFilterChips();
		if(ViewGraph.getViewer() != null)
			ViewGraph.getViewer().resetFilters();
		for (Map.Entry<Class, Image> classcategory : dataProject.getShapesList().entrySet()) {
			Class classShape = classcategory.getKey();
			ViewerFilter filter = new NodeFilter(classShape);
			DataManager.getIstance().getFilters().put(classShape,filter);
		}

		ViewOutline.updateOutline();
		ViewGraph.updateGraphWithLayout();

		// close all views form
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			IViewReference[] viewReferences = page.getViewReferences();
			for (IViewReference ivr : viewReferences) {
				if (ivr.getId().startsWith("org.epos.metadata.editor.viewForm")) {
					page.hideView(ivr);
				}
			}
		}

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setText("SHAPEness Metadata Editor - " + Util.getDefaultLocation() + dataProject.getProjectName());




	}


	private IProject createNewTurtleProject(Project dataProject, IProgressMonitor monitor) {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project= root.getProject(dataProject.getProjectName());
		log.log(Level.INFO, "Default location: "+Util.getDefaultLocation()); 

		try {
			project.create(null);
			project.open(null);

			IProjectDescription description = project.getDescription(); 
			description.setNatureIds(new String[] { XtextProjectHelper.NATURE_ID});

			project.setDescription(description, null);

			// create folder for model
			IFolder modelFolder = project.getFolder ("shacl");
			modelFolder.create (false, true, null);
			IFolder vocFolder = project.getFolder ("shacl").getFolder(Variables.VOCABULARIES_FOLDER.getValue());
			vocFolder.create (false, true, null);

			String path = Variables.VOCABULARIES_FOLDER.getValue()+"/";
			Bundle bundle = Platform.getBundle("org.epos.metadata.editor");			
			URL configURL = bundle.getResource(path);
			File source = new File(FileLocator.toFileURL(configURL).getPath());
			copyLocalVocabularies(source, vocFolder);
			monitor.worked(2);

			File tmp_vocab_folder = new File(dataProject.getProjectFolder()+"/"+"tmp"+"/");

			if(dataProject.isAdditionalVocabularies()) {

				moveDowloadedVocabularies(tmp_vocab_folder, vocFolder);

			}

			// initialize vocabularies store
			Engine.getIstance().getVocStore().setFolder((vocFolder.getLocation().toOSString()+"/"));
			Engine.getIstance().getVocStore().initializeStore(Engine.getIstance().getMs().getShaclModel().getNsPrefixMap());
			monitor.worked(4);

			log.log(Level.INFO, "Project folder path: "+dataProject.getProjectFolder()); 
			log.log(Level.INFO, "Url Shacl path: "+dataProject.getUrlShacl()); 
			
			// create file from shacl
			String fileName = FilenameUtils.getName(dataProject.getUrlShacl());
			log.log(Level.INFO, "FileName loaded: "+fileName); 
			//String fileName = dataProject.getUrlShacl().substring(dataProject.getUrlShacl().lastIndexOf("/")+1, dataProject.getUrlShacl().length() );

			IFile fileModel = modelFolder.getFile(fileName);
			log.log(Level.INFO, "FileModel loaded: "+fileModel.getName()); 

			//try to download file from URL, if MalformedURLException, open FileStream

			try {
				fileModel.create(new URL(dataProject.getUrlShacl()).openStream(), IResource.NONE, null);
				log.log(Level.INFO, "Model loaded successfully from URL"); 
			}catch(MalformedURLException mue) {
				fileModel.create(new FileInputStream(dataProject.getUrlShacl()), IResource.NONE, null);
				log.log(Level.INFO, "Model loaded successfully from File");
			}

			// create folder for metadata files
			IFolder dataFolder = project.getFolder ("metadata");
			dataFolder.create (false, true, null);

			IFile fileExample = dataFolder.getFile(dataProject.getTurtleFile());
			fileExample.create(InputStream.nullInputStream(), IResource.NONE, null);
			dataProject.setTurtleFile(Platform.getLocation().makeAbsolute() + "/" + dataProject.getProjectName() +"/" +"metadata"+"/" + dataProject.getTurtleFile());

			String fExample = dataProject.getTurtleFile().replace(".ttl", "");
			monitor.worked(1);

			Engine.getIstance().getC2m().generateResources();
			Engine.getIstance().getWriter().writeModelToFile(
					fExample, Lang.TTL, 
					Engine.getIstance().getC2m().getOutputOntModel());

			monitor.worked(2);

			IScopeContext projectScope = new ProjectScope(project);
			Preferences projectNode = projectScope.getNode(project.getName());

			/*projectNode.put("projectName", project.getName());
			projectNode.put("projectFolder", Platform.getLocation().makeAbsolute().toOSString());
			projectNode.put("projectShacl", Platform.getLocation().makeAbsolute() + "/" + fileModel.getFullPath().makeAbsolute().toOSString());
			projectNode.put("projectTurtle", Platform.getLocation().makeAbsolute() + "/" +fileExample.getFullPath().makeAbsolute().toOSString());*/
			projectNode.put("projectName", project.getName());
			projectNode.put("projectFolder", Util.getDefaultLocation());
			projectNode.put("projectShacl", Util.getDefaultLocation() + "/" + fileModel.getFullPath().toPortableString());
			projectNode.put("projectTurtle", Util.getDefaultLocation() + "/" +fileExample.getFullPath().toPortableString());
			projectNode.flush();
			monitor.worked(1);

		} catch (CoreException | IOException e) {
			log.log(Level.SEVERE, e.getMessage());
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return project;

	}

	private IProject importTurtleFile(Project dataProject, IProgressMonitor monitor) {
		log.log(Level.INFO, "Default location: "+Util.getDefaultLocation()); 

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project= root.getProject(dataProject.getProjectName());

		try {
			project.create(null);
			project.open(null);

			IProjectDescription description = project.getDescription(); 
			description.setNatureIds(new String[] { XtextProjectHelper.NATURE_ID});
			project.setDescription(description, null);

			// create folder for model
			IFolder modelFolder = project.getFolder ("shacl");
			modelFolder.create (false, true, null);
			IFolder vocFolder = project.getFolder ("shacl").getFolder(Variables.VOCABULARIES_FOLDER.getValue());
			vocFolder.create (false, true, null);

			String path = Variables.VOCABULARIES_FOLDER.getValue()+"/";
			Bundle bundle = Platform.getBundle("org.epos.metadata.editor");			
			URL configURL = bundle.getResource(path);
			File source = new File(FileLocator.toFileURL(configURL).getPath());
			log.log(Level.INFO, "File locator, toFileURL: "+FileLocator.toFileURL(configURL).getPath()); 
			copyLocalVocabularies(source, vocFolder);
			monitor.worked(2);

			File tmp_vocab_folder = new File(dataProject.getProjectFolder()+"/"+"tmp"+"/");
			if(dataProject.isAdditionalVocabularies()) {
				moveDowloadedVocabularies(tmp_vocab_folder, vocFolder);

			}
			// initialize vocabularies store
			Engine.getIstance().getVocStore().setFolder((vocFolder.getLocation().toOSString()+"/"));
			Engine.getIstance().getVocStore().initializeStore(Engine.getIstance().getMs().getShaclModel().getNsPrefixMap());
			monitor.worked(4);

			
			log.log(Level.INFO, "Project folder path: "+dataProject.getProjectFolder()); 
			log.log(Level.INFO, "Url Shacl path: "+dataProject.getUrlShacl()); 
			
			// create file from shacl
			String fileShacl = FilenameUtils.getName(dataProject.getUrlShacl());
			log.log(Level.INFO, "fileShacl loaded: "+fileShacl); 
			//String fileShacl = dataProject.getUrlShacl().substring(dataProject.getUrlShacl().lastIndexOf("/")+1, dataProject.getUrlShacl().length() );

			IFile fileModel = modelFolder.getFile(fileShacl);
			log.log(Level.INFO, "fileShaclModel loaded: "+fileModel.getName()); 

			//try to download file from URL, if MalformedURLException, open FileStream
			try {
				fileModel.create(new URL(dataProject.getUrlShacl()).openStream(), IResource.NONE, null);
				log.log(Level.INFO, "Model loaded successfully from URL"); 
			}catch(MalformedURLException mue) {
				fileModel.create(new FileInputStream(dataProject.getUrlShacl()), IResource.NONE, null);
				log.log(Level.INFO, "Model loaded successfully from File");
			}
			monitor.worked(1);

			// create folder for metadata files
			IFolder dataFolder = project.getFolder ("metadata");
			dataFolder.create (false, true, null);

			log.info("dataProject.getTurtleFile() " + dataProject.getTurtleFile());

			//IFile fileExample = dataFolder.getFile(dataProject.getTurtleFile().substring(dataProject.getTurtleFile().lastIndexOf("/")+1, dataProject.getTurtleFile().length()));
			IFile fileExample = dataFolder.getFile(FilenameUtils.getName(dataProject.getTurtleFile()));
			fileExample.create(InputStream.nullInputStream(), IResource.NONE, null);

			log.info("fileExample " + fileExample);

			//String fExample = Platform.getLocation().makeAbsolute() + "/" + dataProject.getProjectName() + "/"+"metadata"+"/" + dataProject.getTurtleFile().substring(dataProject.getTurtleFile().lastIndexOf("/")+1, dataProject.getTurtleFile().length()).replace(".ttl", "");
			String fExample = Platform.getLocation().makeAbsolute() + "/" + dataProject.getProjectName() + "/"+"metadata"+"/" + FilenameUtils.removeExtension(FilenameUtils.getName(dataProject.getTurtleFile()));
			
			Engine.getIstance().getFreader().read(dataProject.getTurtleFile());


			Engine.getIstance().getC2m().generateResources();
			Engine.getIstance().getWriter().writeModelToFile(
					fExample, Lang.TTL, 
					Engine.getIstance().getC2m().getOutputOntModel());
			monitor.worked(2);

			dataProject.setTurtleFile(Platform.getLocation().makeAbsolute() + "/" + dataProject.getProjectName() + "/"+"metadata"+"/" + FilenameUtils.getName(dataProject.getTurtleFile()));

			IScopeContext projectScope = new ProjectScope(project);
			Preferences projectNode = projectScope.getNode(project.getName());

			/*projectNode.put("projectName", project.getName());
			projectNode.put("projectFolder", Platform.getLocation().makeAbsolute().toOSString());
			projectNode.put("projectShacl", Platform.getLocation().makeAbsolute() + "/" + fileModel.getFullPath().makeAbsolute().toOSString());
			projectNode.put("projectTurtle", Platform.getLocation().makeAbsolute() + "/" +fileExample.getFullPath().makeAbsolute().toOSString());*/
			projectNode.put("projectName", project.getName());
			projectNode.put("projectFolder", Util.getDefaultLocation());
			projectNode.put("projectShacl", Util.getDefaultLocation() + "/" + fileModel.getFullPath().toPortableString());
			projectNode.put("projectTurtle", Util.getDefaultLocation() + "/" +fileExample.getFullPath().toPortableString());
			projectNode.flush();
			monitor.worked(1);


		} catch (CoreException | IOException e) {
			log.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return project;

	}


	private void moveDowloadedVocabularies(File source, IFolder vocFolder) {
		for (File f: source.listFiles()) {
			f.renameTo(new File(dataProject.getProjectFolder() + "/" + vocFolder.getFullPath().toOSString() + "/" + f.getName()));

		}

	}

	private void removeExistingProject() {
		try {
			// close all views form 
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				IViewReference[] viewReferences = page.getViewReferences();
				for (IViewReference ivr : viewReferences) {
					if (ivr.getId().startsWith("org.epos.metadata.editor.viewForm")) {
						page.hideView(ivr);
					}
				}
			}

			// remove all nodes from graph

			DataManager.getIstance().getNodes().clear();

			ViewGraph.updateGraph();
			// remove project from view
			//Util.updateFileEditor();
			IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
			Project dataProject = context.get(Project.class);

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject project= root.getProject(dataProject.getProjectName());
			project.delete(true, null);
			//project.close(null);



		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void copyLocalVocabularies(File srcFolder, IContainer destFolder) {
		try {
			for (File f: srcFolder.listFiles()) {
				if (f.isDirectory()) {
					IFolder newFolder = destFolder.getFolder(new Path(f.getName()));	                
					newFolder.create(true, true, null);					
					copyLocalVocabularies(f, newFolder);
				} else {
					IFile newFile = destFolder.getFile(new Path(f.getName()));
					newFile.create(new FileInputStream(f), true, null);					
				}
			}
		} catch (CoreException | FileNotFoundException e) {
			log.log(Level.SEVERE, e.getMessage());
		}
	}


}
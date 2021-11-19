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
package org.epos.metadata.editor.ui.handlers;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import org.apache.jena.riot.Lang;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.views.navigator.ResourceSorter;
import org.eclipse.xtext.ui.XtextProjectHelper;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.engine.VocabularyStore;
import org.epos.metadata.editor.engine.checkers.ShaclChecker;
import org.epos.metadata.editor.engine.converters.Classes2Model;
import org.epos.metadata.editor.engine.converters.Model2Classes;
import org.epos.metadata.editor.engine.io.FileReader;
import org.epos.metadata.editor.engine.io.FileWriter;
import org.epos.metadata.editor.engine.io.ModelReader;
import org.epos.metadata.editor.engine.model.ModelStored;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.model.NodeFilter;
import org.epos.metadata.editor.ui.model.Project;
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.utils.Variables;
import org.epos.metadata.editor.ui.views.ViewGraph;
import org.epos.metadata.editor.ui.views.ViewGraphPalette;
import org.epos.metadata.editor.ui.views.ViewOutline;
import org.osgi.service.prefs.Preferences;

public class OpenExistingProject implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		if (ResourcesPlugin.getWorkspace().getRoot().getProjects().length == 0){
			MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", "No project has been created yet.\nPlease, create a new Project.");
			return null;
		}
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(Display.getDefault().getActiveShell(), new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		dialog.setTitle("Open Project");
		dialog.setMessage("Select a project from the workspace "+ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()+":");
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));
		dialog.setValidator(selection -> {
		    if (selection.length > 0 && selection[0] instanceof IProject)
		        return new Status(IStatus.OK, "org.epos.metadata.editor", IStatus.OK, "", null);
		    return new Status(IStatus.ERROR, "org.epos.metadata.editor", IStatus.ERROR, "", null);
		});
		dialog.setAllowMultiple(false);
		
		
		if (dialog.open() == Window.OK) {
			IResource resource = (IResource) dialog.getFirstResult();
			//System.out.println("Project selected: " + resource);

			DataManager.getIstance().getNodes().clear();
			DataManager.getIstance().getConnections().clear();
			
			IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
			Project dataProject = context.get(Project.class);

			if(dataProject == null) {
				dataProject = new Project();
			}
			if(openProject(resource.getName(), dataProject) == null) {
				return null;
			}

			context.set(Project.class, dataProject);
			
			// Create filters for shapes
			DataManager.getIstance().getFilters().clear();
			if(ViewGraph.getViewer() != null)
				ViewGraph.getViewer().resetFilters();
			
			for (Map.Entry<Class, Image> classcategory : dataProject.getShapesList().entrySet()) {
				Class classShape = classcategory.getKey();
				ViewerFilter filter = new NodeFilter(classShape);
				DataManager.getIstance().getFilters().put(classShape,filter);
			}
			
			ViewGraph.updateGraphWithLayout();
			
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setText("SHAPEness Metadata Editor - " + Util.getDefaultLocation() + dataProject.getProjectName());

			
			try {

				ViewGraphPalette palette = (ViewGraphPalette) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
						getActivePage().showView("org.epos.metadata.editor.viewPalette");
				palette.createPaletteItems(dataProject.getShapesList());
				//IActionBars bars = view.getViewSite().getActionBars();
				//bars.getStatusLineManager().setMessage("Model loaded");

				ViewOutline outline = (ViewOutline) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
						getActivePage().showView("org.epos.metadata.editor.viewOutline");
				outline.setShapes(dataProject.getShapesList());
				ViewOutline.updateOutline();
				
				// close all views form
				IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if (activePage != null) {
					IViewReference[] viewReferences = activePage.getViewReferences();
					for (IViewReference ivr : viewReferences) {
						if (ivr.getId().startsWith("org.epos.metadata.editor.viewForm")) {
							activePage.hideView(ivr);
						}
					}
				}
				// close editor
				//IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IEditorReference[] editors = activePage.getEditorReferences();
				if(editors.length > 0) {
					activePage.closeEditor(editors[0].getEditor(true), true);
					activePage.setEditorAreaVisible(false);
					
					ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
					Command command = service.getCommand("org.epos.metadata.editor.showTurtle");
					State state = command.getState("org.eclipse.ui.commands.toggleState");
					state.setValue(false);
				}

			} catch (PartInitException e) {
				//System.out.println(e.getMessage()); 

			}
			
			
		}

		return null;

	}

	private IProject openProject(String projectName, Project dataProject) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(projectName);

		

		IScopeContext projectScope = new ProjectScope(project);
		Preferences projectNode = projectScope.getNode(project.getName());
		dataProject.setProjectName(projectNode.get("projectName", null));
		dataProject.setTurtleFile(projectNode.get("projectTurtle", null));
		dataProject.setUrlShacl(projectNode.get("projectShacl", null));
		dataProject.setProjectFolder(projectNode.get("projectFolder", null));

		
		// NEED TO UPDATE PROJECT BEAN


		try {
			project.open(null);
			IProjectDescription description = project.getDescription(); 
			description.setNatureIds(new String[] { XtextProjectHelper.NATURE_ID});
			project.setDescription(description, null);

			// create folder for metadata files
			IFolder dataFolder = project.getFolder ("metadata");

			//IFile fileExample = dataFolder.getFile(dataProject.getTurtleFile().substring(dataProject.getTurtleFile().lastIndexOf('/')+1, dataProject.getTurtleFile().length()));
			//fileExample.create(InputStream.nullInputStream(), IResource.NONE, null);

			String fExample = Platform.getLocation().makeAbsolute() + "/" + 
					dataProject.getProjectName() +"/" + "metadata" +"/"+ dataProject.getTurtleFile().substring(dataProject.getTurtleFile().lastIndexOf("/")+1, dataProject.getTurtleFile().length()).replace(".ttl", "");

			IFolder vocFolder = project.getFolder ("shacl").getFolder(Variables.VOCABULARIES_FOLDER.getValue());
			
			Engine engine = new Engine.EngineBuilder()
					.setModel2Classes(new Model2Classes())
					.setModelStored(new ModelStored())
					.setModelReader(new ModelReader(dataProject.getUrlShacl()))
					.setClasses2Model(new Classes2Model())
					.setFileReader(new FileReader())
					.setFileWriter(new FileWriter())
					.setVocabularyStore(new VocabularyStore(null))
					.build();
			
			
			// initialize vocabularies store
			Engine.getIstance().getVocStore().setFolder((vocFolder.getLocation().toOSString()+"/"));
			Engine.getIstance().getVocStore().initializeStore(Engine.getIstance().getMs().getShaclModel().getNsPrefixMap());

			try {
				Engine.getIstance().getFreader().
				readFromFile(dataProject.getTurtleFile());
			} catch (org.apache.jena.riot.RiotException e) {
				//System.out.println("RDF/Turtle file not loaded");
				MessageDialog.openError(Display.getDefault().getActiveShell(), 
						"Error", "RDF/Turtle file not valid:\n" + e.getMessage());
				return null;
			}

			Engine.getIstance().getC2m().generateResources();
			Engine.getIstance().getWriter().writeModelToFile(
					fExample, Lang.TTL, 
					Engine.getIstance().getC2m().getOutputOntModel());
			
			//ShaclChecker.validate(Engine.getIstance().getMs().getShaclModel(), fExample);

			ArrayList<Class<?>> list = engine.getM2c().getClasses();
			Map<Class, Image> shapesList = new HashMap<Class, Image>();
			for (Class shape : list) {
				Image icon = Util.createImageIcon(shape.getSimpleName());
				shapesList.put(shape,icon);
			}
			

			dataProject.setShapesList(shapesList);
			return project;
		} catch (CoreException | FileNotFoundException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (org.apache.jena.riot.RiotException e) {
			//System.out.println("***************SHACL file not valid");
			MessageDialog.openError(Display.getDefault().getActiveShell(), 
					"Error", "SHACL file not valid:\n" + e.getMessage());
			
		}
		return null;

	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}

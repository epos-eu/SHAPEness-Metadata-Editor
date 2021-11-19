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
import org.eclipse.jface.dialogs.InputDialog;
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
import org.eclipse.ui.handlers.HandlerUtil;
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
import org.epos.metadata.editor.ui.model.Project;
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.utils.Variables;
import org.epos.metadata.editor.ui.views.ViewGraph;
import org.epos.metadata.editor.ui.views.ViewGraphPalette;
import org.epos.metadata.editor.ui.views.ViewOutline;
import org.osgi.service.prefs.Preferences;

public class RemoveExistingProject implements IHandler {

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
			MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", "No project has been created yet.");
			return null;
		}
		IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
		Project currentProject = context.get(Project.class);
		
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(Display.getDefault().getActiveShell(), new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		dialog.setTitle("Remove Project");
		dialog.setMessage("Select the project to remove from the workspace:");
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));
		dialog.setValidator(selection -> {
		    if (selection.length > 0 && selection[0] instanceof IProject) {
		    	IProject project = (IProject) selection[0];
		    	if(currentProject != null ) {
		    		if(!project.getName().equals(currentProject.getProjectName()))
		    			return new Status(IStatus.OK, "org.epos.metadata.editor", IStatus.OK, "", null);
		    	} else {
		    		return new Status(IStatus.OK, "org.epos.metadata.editor", IStatus.OK, "", null);
		    	}
		    }
		    return new Status(IStatus.ERROR, "org.epos.metadata.editor", IStatus.ERROR, "", null);
		});
		dialog.setAllowMultiple(false);
		
		
		if (dialog.open() == Window.OK) {
			IProject resource = (IProject) dialog.getFirstResult();
			//System.out.println("Project to rename: " + resource.getName());
			
			if(MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "Question", 
					"Do you really want to delete " + resource.getName() + " project?")) {
				try {
					resource.delete(true, null);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
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

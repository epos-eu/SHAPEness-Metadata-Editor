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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.epos.metadata.editor.engine.converters.DataciteConverter;
import org.epos.metadata.editor.engine.converters.OGCWMSConverter;
import org.epos.metadata.editor.ui.dialogs.ImportFromDOIDialog;
import org.epos.metadata.editor.ui.dialogs.ImportFromOGCWMSDialog;
import org.epos.metadata.editor.ui.model.Project;
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.utils.Variables;
import org.epos.metadata.editor.ui.views.ViewGraph;
import org.epos.metadata.editor.ui.views.ViewOutline;
import org.jdom2.JDOMException;
import org.osgi.framework.Bundle;

public class ImportMetadataFromOGCWMSHandler implements IHandler {
	Logger log = Logger.getLogger("RCP_log");

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
		
		IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
		Project dataProject = context.get(Project.class);
		
		if(dataProject != null) {
			ImportFromOGCWMSDialog dlg = new ImportFromOGCWMSDialog(Display.getDefault().getActiveShell());
			if (dlg.open() == Window.OK && !dlg.getOGCWMSURL().isEmpty()) {
				String OGCGetCapabilitiesWMS = dlg.getOGCWMSURL();
				
				IRunnableWithProgress op = new IRunnableWithProgress() {
					@SuppressWarnings("resource")
					public void run(IProgressMonitor monitor) throws InvocationTargetException {
						monitor.beginTask("Import metadata from OGC GetCapabilities... ", 5);
						try {
							Bundle bundle = Platform.getBundle("org.epos.metadata.editor");
							OGCWMSConverter.getInstance().setFolderLocation(dataProject.getProjectFolder() + dataProject.getProjectName());
							//String path = "resources/ogc-wms-to-epos-dcat-ap.xsl";		
							//URL configURL = bundle.getResource(path);
							//File source = new File(FileLocator.toFileURL(configURL).getPath());
							URL configURL = new URL("https://raw.githubusercontent.com/epos-eu/EPOS-DCAT-AP/EPOS-DCAT-AP-shapes/mappings/ogc-wms-to-epos-dcat-ap.xsl");
							//File source = new File(configURL.getFile());
							String tDir = System.getProperty("java.io.tmpdir"); 
							String path = tDir + "tmp" ; 
							File file = new File(tDir+File.separator+"ogc-wms-to-epos-dcat-ap.xsl");
							file.deleteOnExit(); 
							FileUtils.copyURLToFile(configURL, file);
							OGCWMSConverter.getInstance().setSchemaLocation(file.toURI());
							monitor.worked(1);
							OGCWMSConverter.getInstance().generateFromOGCWMSCapabilitiesAddress(OGCGetCapabilitiesWMS);
							monitor.worked(4);
							monitor.done();
						}catch(IOException e) {
							log.severe(e.getLocalizedMessage());
							e.printStackTrace();
						}
					}
				};

				IWorkbenchWindow win  = PlatformUI.getWorkbench().getActiveWorkbenchWindow();			

				try {
					new ProgressMonitorDialog(win.getShell()).run(true, false, op);
				} catch (InvocationTargetException | InterruptedException e) {
					// TODO Auto-generated catch block
					log.severe(e.getLocalizedMessage());
					e.printStackTrace();
					MessageDialog.openError(win.getShell(), "An error occured importing metadata from OGC GetCapabilities!", e.getMessage());
				}
				ViewGraph.updateGraphWithLayout();
				ViewOutline.updateOutline();
				Util.updateFileEditor();
				
				
			}
			

		} else {
			MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", "No project has been created yet.\nPlease, create a new Project.");
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

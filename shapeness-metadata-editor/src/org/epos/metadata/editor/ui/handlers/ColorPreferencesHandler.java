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
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.epos.metadata.editor.ui.dialogs.ChooseColorDialog;
import org.epos.metadata.editor.ui.model.Project;
import org.epos.metadata.editor.ui.views.CompositeGraphFilter;
import org.epos.metadata.editor.ui.views.ViewGraph;
import org.epos.metadata.editor.ui.views.ViewGraphPalette;
import org.epos.metadata.editor.ui.views.ViewOutline;

public class ColorPreferencesHandler extends AbstractHandler {

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
			Map<Class, Image> shapesList = dataProject.getShapesList();
			ChooseColorDialog dlg = new ChooseColorDialog(HandlerUtil.getActiveShell(event), shapesList);
			if(dlg.open() == Window.OK) {
				////System.out.println("Save changes of color prefs");
				dataProject.setShapesList(dlg.getShapeList());
				
				try {
					ViewOutline outline = (ViewOutline) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
							getActivePage().showView("org.epos.metadata.editor.viewOutline");
					outline.setShapes(dataProject.getShapesList());
				} catch (PartInitException e) {
					
					e.printStackTrace();
				}
				
				ViewOutline.updateOutline();
				ViewGraph.updateGraph();
				ViewGraphPalette.updatePaletteColor();
				CompositeGraphFilter.updateColorFilters();
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
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}

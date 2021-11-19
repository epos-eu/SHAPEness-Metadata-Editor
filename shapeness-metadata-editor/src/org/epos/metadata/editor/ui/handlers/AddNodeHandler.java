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
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.epos.metadata.editor.ui.dialogs.CreateNodesDialog;
import org.epos.metadata.editor.ui.dialogs.MapDialog;
import org.epos.metadata.editor.ui.model.Project;
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.views.ViewGraphPalette;
import org.epos.metadata.editor.ui.wizard.WizardNewProject;

public class AddNodeHandler implements IHandler {

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

		if(ViewGraphPalette.getDrawer() != null && ViewGraphPalette.getDrawer().getChildren().size() > 0) {
			IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
			Project dataProject = context.get(Project.class);
			if(dataProject != null) {
				Map<Class, Image> shapesList = dataProject.getShapesList();
				CreateNodesDialog dialog = new CreateNodesDialog(Display.getDefault().getActiveShell(), shapesList);
				if(dialog.open() == Window.OK) {
					List<Class> nodes = dialog.getShapesSelected();
					for (Class classNode : nodes) {
						Util.addNodeOnGraph(classNode);
					}
				}
				
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

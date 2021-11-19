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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.views.CompositeGraphFilter;
import org.epos.metadata.editor.ui.views.ViewGraph;
import org.epos.metadata.editor.ui.views.ViewOutline;

public class RemoveAllNodesAction implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {


	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(DataManager.getIstance().getNodes().isEmpty()) {
			MessageDialog.openWarning(HandlerUtil.getActiveShell(event), "Warning", "There are not nodes on graph.");
		} else {
			if(MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "Question", "Do you really want to delete all nodes?")) {

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

				DataManager.getIstance().getNodes().clear();
				DataManager.getIstance().getConnections().clear();

				ViewGraph.updateGraph();
				Util.updateFileEditor();
				ViewOutline.updateOutline();

				CompositeGraphFilter.clearAllFilterChips();



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

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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.Connection;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.views.CompositeGraphFilter;
import org.epos.metadata.editor.ui.views.ViewForm;
import org.epos.metadata.editor.ui.views.ViewGraph;
import org.epos.metadata.editor.ui.views.ViewOutline;

public class RemoveNodeAction extends AbstractHandler {

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

		if(!DataManager.getIstance().getNodes().isEmpty()) {
			try {
				ViewGraph graph = (ViewGraph) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
						getActivePage().showView("org.epos.metadata.editor.viewGraph");
				ISelection selection = graph.getViewer().getSelection();
				if(!selection.isEmpty()) {
					Object objSelected = ((IStructuredSelection) selection).getFirstElement();

					if(objSelected instanceof Node) {
						Node selectedNode = (Node) objSelected;
						if(MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "Question", "Do you really want to delete " + selectedNode.getNodeName() + " node?")) {

							Node removedNode = DataManager.getIstance().getNode(selectedNode);
							DataManager.getIstance().getNodes().remove(removedNode);

							// Check if the removed node is connected with others nodes
							DataManager.getIstance().removeConnectionWithSource(removedNode);
							List<Connection> connectionRemoved = DataManager.getIstance().removeConnectionWithDestination(removedNode);

							for (Connection connection : connectionRemoved) {
								String fieldName = connection.getLabel();
								Node source = connection.getSource();
								Util.updateBeanRemoveObj(source, fieldName, removedNode, removedNode.getClass().getTypeName());
								//System.out.println("Removed connection with node: " + source.getNodeName()+"-"+source.getId());

							}
							ViewGraph.updateGraph();

							Util.updateFileEditor();
							ViewOutline.updateOutline();

							String IDTableView = "org.epos.metadata.editor.viewForm."+ removedNode.getNodeName()+"-"+removedNode.getId().replace(":", "_"); 
							IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							IViewPart tableView = activePage.findView("org.epos.metadata.editor.viewForm" + ":" + IDTableView);
							activePage.hideView(tableView);

							if(DataManager.getIstance().getNodesByType(Util.searchClassByNode(removedNode).getTypeName()).size() == 0) {
								CompositeGraphFilter.removeFilterChips(Util.searchClassByNode(removedNode));
							}


						}

					}else {
						MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", "You should select a node to remove.");

					}
				} else {
					MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", "You should select a node to remove.");

				}

			} catch (PartInitException |   SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		} else {

			MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", "There are not nodes on the graph.");

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

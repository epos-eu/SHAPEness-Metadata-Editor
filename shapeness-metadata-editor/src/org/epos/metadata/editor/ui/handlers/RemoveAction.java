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
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
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
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.Connection;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.views.ViewForm;
import org.epos.metadata.editor.ui.views.ViewGraph;
import org.epos.metadata.editor.ui.views.ViewOutline;

public class RemoveAction extends AbstractHandler {

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
					try {
						if(objSelected instanceof Node) {
							IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
									.getService(IHandlerService.class);

							handlerService.executeCommand("org.epos.metadata.editor.removeNode", null);

						} else if(objSelected instanceof EntityConnectionData) {
							IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
									.getService(IHandlerService.class);
							handlerService.executeCommand("org.epos.metadata.editor.removeConnection", null);
						}
					} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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


	private static void updateBeanRemoveObj(Object source, String fieldName, Object fieldValue, String typeOfField) {
		Node node = DataManager.getIstance().getNode(source);
		//System.out.println("Bean source: " + node);
		try {
			Field field = node.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);

			//System.out.println("property name: " + fieldName + " type: " + field.getType());
			//System.out.println("value to be removed: " + fieldValue);

			Class<?> fieldType = field.getType();
			if(fieldType.equals(List.class)) {
				Type type = Util.getGenericTypeOfList(field);
				//System.out.println("Property is a List: " + type.getTypeName());

				try {
					Class<?> instance = ClassUtils.getClass(type.getTypeName());
					List list = (List)field.get(node);
					if(list!=null) {
						list.remove(fieldValue);
					}

				} catch (ClassNotFoundException  e) {
					e.printStackTrace();
				}

			} else {
				fieldValue = null;
				Class<?> instance = ClassUtils.getClass(typeOfField);
				//System.out.println("Remove Setting IdRef on Node: " + node);
				Method setMethod = node.getClass().getDeclaredMethod("set"+fieldName,instance);
				//System.out.println("Method invoke: " + setMethod.toGenericString() ); 
				setMethod.setAccessible(true);
				setMethod.invoke(node,fieldValue);
			}

			//System.out.println("Updated bean: " + node);


		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
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

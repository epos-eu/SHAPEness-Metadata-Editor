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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.dialogs.CloneNodeDialog;
import org.epos.metadata.editor.ui.model.Connection;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.views.CompositeGraphFilter;
import org.epos.metadata.editor.ui.views.ViewForm;
import org.epos.metadata.editor.ui.views.ViewGraph;
import org.epos.metadata.editor.ui.views.ViewOutline;



public class DuplicateNodeAction extends AbstractHandler {

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

		try {
			ViewGraph graph = (ViewGraph) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewGraph");
			ISelection selection = graph.getViewer().getSelection();
			if(!selection.isEmpty()) {
				Object objSelected = ((IStructuredSelection) selection).getFirstElement();

				if(objSelected instanceof Node) {
					Node selectedNode = (Node) objSelected;

					Node node = DataManager.getIstance().getNode(selectedNode);					
					// confirm clone
					if(MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "Question", "Do you really want to duplicate node: " + node.getNodeName()+ "-" + node.getId() + "?")) {
						
						Node duplicatedNode = null;
						try {
							duplicatedNode = node.copy();
						} catch (IllegalAccessException | InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//System.out.println("Duplicated node: " + duplicatedNode);
	
						
						List<Connection> connectionToAdd = DataManager.getIstance().getConnectionsSourceNode(node);
						for (Connection connection : connectionToAdd) {
							String fieldName = connection.getLabel();
							Node destination = connection.getDestination();
							Connection newConnection = new Connection(duplicatedNode.getId(),fieldName,duplicatedNode,destination);
							DataManager.getIstance().getConnections().add(newConnection);
	
							
						}
						
						String IDFormView = "org.epos.metadata.editor.viewForm." + 
								duplicatedNode.getNodeName()+ "-" + duplicatedNode.getId().replace(":", "_");
	
						ViewForm.createOpenFormNode(duplicatedNode, IDFormView);
						ViewGraph.updateGraph();
						Util.selectNodeOnGraph(duplicatedNode);
						Util.updateFileEditor();
						ViewOutline.updateOutline();
						CompositeGraphFilter.enableChipsByFilter(Util.searchClassByNode(duplicatedNode));

					}



				}
			}

		} catch (PartInitException |   SecurityException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public static String[] getFieldsToIgnore(Node node) { 
		ArrayList<String> listToIgnore = new ArrayList<String>();
	
		Field[] fields = node.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			
			Class<?> fieldType = field.getType();
			////System.out.println("Field type: " + fieldType);
			
			if(fieldType.equals(List.class)) {
				Type typeOfList = Util.getGenericTypeOfList(field);
				if(!Util.isPrimitiveClass(typeOfList)) {
					listToIgnore.add(field.getName());
					//System.out.println("Field to be excluded: " + field.getName() + " because is a list of: " + typeOfList);
				}
				
			} else if(!Util.isPrimitiveClass(fieldType)) {
				listToIgnore.add(field.getName());
				//System.out.println("Field to be excluded: " + field.getName() + " because is: " + fieldType);
			}
		}
		//System.out.println("Field to be excluded: " + listToIgnore.size());
		return listToIgnore.toArray(String[]::new);
		// return (String[]) listToIgnore.toArray();
	}
	
	

	public static void updateBeanRef(Object source, String fieldName, Object fieldValue) {
		Node node = DataManager.getIstance().getNode(source);
		//System.out.println("Bean source: " + node);
		try {
			Field field = node.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);

			//System.out.println("property name: " + fieldName + " type: " + field.getType());
			//System.out.println("value to be removed: " + fieldValue);

			Class<?> fieldType = field.getType();
			if(fieldType.equals(List.class)) {
				Class<?> instance = ClassUtils.getClass(fieldType.getTypeName());
				//System.out.println("Remove Setting IdRef on Node: " + node);
				Method setMethod = node.getClass().getDeclaredMethod("set"+fieldName,field.getType());
				//System.out.println("Method invoke: " + setMethod.toGenericString() ); 
				setMethod.setAccessible(true);
				setMethod.invoke(node,instance.newInstance());
				//fieldValue = new java.awt.List();
				/*Type type = Util.getGenericTypeOfList(field);
				//System.out.println("Property is a List: " + type.getTypeName());

				try {
					Class<?> instance = ClassUtils.getClass(type.getTypeName());
					List list = (List)field.get(node);
					if(list!=null) {
						list.remove(fieldValue);
					}

				} catch (ClassNotFoundException  e) {
					e.printStackTrace();
				}*/

			} else {
				//fieldValue = null;
				Class<?> instance = ClassUtils.getClass(fieldType.getTypeName());
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
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	}

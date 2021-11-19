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
package org.epos.metadata.editor.ui.utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.crypto.Data;

import org.apache.commons.lang3.ClassUtils;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.riot.Lang;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.engine.converters.geometries.GeometryWKT;
import org.epos.metadata.editor.engine.utils.Shacl;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.dialogs.CreateNewNodeDialog;
import org.epos.metadata.editor.ui.model.Connection;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.model.NodePrimitive;
import org.epos.metadata.editor.ui.model.Project;
import org.epos.metadata.editor.ui.views.CompositeGraphFilter;
import org.epos.metadata.editor.ui.views.ViewForm;
import org.epos.metadata.editor.ui.views.ViewGraph;
import org.epos.metadata.editor.ui.views.ViewOutline;

public class Util {
	
	public static String getDefaultLocation( ){
		Location defaultLocation  = Platform.getInstanceLocation();
		return defaultLocation.getURL().getPath();
		
	}
	
	public static Map<Class, Image> getShapesList() {
		IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
		Project dataProject = context.get(Project.class);
		if(dataProject != null) {
			return dataProject.getShapesList();
		} else return null;
	}
	
	public static Class searchClassByNode(Node node) {
		IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
		Project dataProject = context.get(Project.class);
		Map<Class, Image> map = dataProject.getShapesList();
		for (Map.Entry<Class, Image> classcategory : map.entrySet()) {
			Class classShape = classcategory.getKey();
			Image icon = classcategory.getValue();

			try {
				if(ClassUtils.getClass(classShape.getTypeName()).isInstance(node)){
					return classShape;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Image getImageShapeByClassName(String className) {
		IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
		Project dataProject = context.get(Project.class);
		Map<Class, Image> map = dataProject.getShapesList();
		Class classSearch = searchClassByName(className);
		return map.get(classSearch);
	}
	
	public static Color getColorShapeByClassName(Class className) {
		IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
		Project dataProject = context.get(Project.class);
		Map<Class, Image> map = dataProject.getShapesList();
		
		Image icon = map.get(className);
		int pixelValue = icon.getImageData().getPixel(5,5);

		PaletteData palette = icon.getImageData().palette; 
		RGB rgb = palette.getRGB(pixelValue);
		return new Color(Display.getDefault(), rgb);
	}
	
	public static RGB searchRGBColorForNode(Node node) {
		IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
		Project dataProject = context.get(Project.class);
		Map<Class, Image> shapes = dataProject.getShapesList();

		Image icon = shapes.get(node.getClass());
		int pixelValue = icon.getImageData().getPixel(5,5);

		PaletteData palette = icon.getImageData().palette; 
		RGB rgb = palette.getRGB(pixelValue);
		return rgb;

	}

	public static Class searchClassByName(String className) {
		IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
		Project dataProject = context.get(Project.class);
		Map<Class, Image> map = dataProject.getShapesList();
		for (Map.Entry<Class, Image> classcategory : map.entrySet()) {
			Class classShape = classcategory.getKey();
		    if(classShape.getSimpleName().equals(className))
		    	return classShape;
		    			
		}
		return null;
	}
	
	

	public static void updateFileEditor() {
		IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
		Project dataProject = context.get(Project.class);

		String fileTurtle = dataProject.getTurtleFile().substring(dataProject.getTurtleFile().lastIndexOf("/")+1, dataProject.getTurtleFile().length()).replace(".ttl", "");
		String fileName = Platform.getLocation().makeAbsolute() + "/" + dataProject.getProjectName() + "/"+"metadata"+"/" + fileTurtle;
		
		//Job job = Job.create("Update file editor", (ICoreRunnable) monitor -> {
			

			Engine.getIstance().getC2m().generateResources();
			Engine.getIstance().getWriter().writeModelToFile(
					fileName, Lang.TTL, 
					Engine.getIstance().getC2m().getOutputOntModel());

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject project= root.getProject(dataProject.getProjectName());
			IFolder dataFolder = project.getFolder ("metadata");
			IFile fileExample = dataFolder.getFile(dataProject.getTurtleFile().substring(dataProject.getTurtleFile().lastIndexOf("/")+1, dataProject.getTurtleFile().length()));
			try {
				fileExample.refreshLocal(IResource.DEPTH_ZERO, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		/*});

		// Start the Job
		job.schedule();*/
		
		//ViewGraph.updateGraph();
		
		
	}

	public static Type getGenericTypeOfList(Field fieldList) {
		Type type = fieldList.getGenericType();
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			for (Type t : pt.getActualTypeArguments()) {
				return t;
			}
		}
		return null;
	}


	public static boolean isPrimitiveClass(Type type) {
		boolean isPrimitiveOrWrapped;
		try {
			////System.out.println("TYPE: "+type.getTypeName());
			isPrimitiveOrWrapped = ClassUtils.isPrimitiveOrWrapper(ClassUtils.getClass(type.getTypeName()) );
			if(isPrimitiveOrWrapped 
					|| ClassUtils.getClass(type.getTypeName()) == String.class
					|| ClassUtils.getClass(type.getTypeName()) == java.net.URI.class
					|| ClassUtils.getClass(type.getTypeName()) == org.apache.jena.datatypes.xsd.XSDDateTime.class
					|| ClassUtils.getClass(type.getTypeName()) == GeometryWKT.class) {			 
				return true;
			} else {
				return false;
			}

		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static boolean isPrimitiveClass(String typeName) {
		boolean isPrimitiveOrWrapped;
		try {
			isPrimitiveOrWrapped = ClassUtils.isPrimitiveOrWrapper(ClassUtils.getClass(typeName) );
			if(isPrimitiveOrWrapped 
					|| ClassUtils.getClass(typeName) == String.class
					|| ClassUtils.getClass(typeName) == java.net.URI.class
					|| ClassUtils.getClass(typeName) == org.apache.jena.datatypes.xsd.XSDDateTime.class
					|| ClassUtils.getClass(typeName) == GeometryWKT.class) {			 
				return true;
			} else {
				return false;
			}

		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return false;
	}
	public static Node createNewObjClass(String className, String IDType, String IDValue) {

		try {
			String IDNode = IDType + IDValue;
			Class<?> classItem = ClassUtils.getClass(className);
			//Object[] intArgs = new Object[] {UUID.randomUUID().toString(), classItem.getSimpleName()};
			Object[] intArgs = new Object[] {IDNode, classItem.getSimpleName()};
			Class[] intArgsClass = new Class[] { String.class, String.class };
			Constructor constructor = classItem.getConstructor(intArgsClass);
			Node node = (Node) constructor.newInstance(intArgs);
			DataManager.getIstance().getNodes().add(node);

			return node;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static Object updateBeanAddObj(Object data, String fieldName, Object fieldValue) {
		Node node = DataManager.getIstance().getNode(data);
		try {
			Field field = node.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			//System.out.println("UpdateBean: " + node.getNodeName() + " with value " + fieldValue + " type: " + field.getType());

			Class<?> fieldType = field.getType();
			Object obj = null;

			if(fieldType.equals(List.class)) {
				Type type = Util.getGenericTypeOfList(field);
				//System.out.println("Property is a List: " + type.getTypeName());

				if(Util.isPrimitiveClass(type)) {
					if(ClassUtils.getClass(type.getTypeName()) == org.apache.jena.datatypes.xsd.XSDDateTime.class) {
						obj = fieldValue;
					} else {
						obj = createNewObjPrimitiveType(type,fieldValue.toString());
					}
				} else {
					obj = fieldValue;
				}

				List list = (List)field.get(node);
				if(list==null) {
					list = new ArrayList();
					list.add(obj);
					field.set(node, list);

				} else {
					Class<?> instance = ClassUtils.getClass(type.getTypeName());
					Method add = node.getClass().getDeclaredMethod("add"+fieldName,instance);
					//System.out.println("Method invoke: " + add.toGenericString()); 
					add.setAccessible(true);
					add.invoke(node,obj);
				}
				return obj;

			} else {

				//System.out.println("Property is an single object: " + field.getType());
				//System.out.println("Setting IdRef on Node: " + node);
				Method setMethod = node.getClass().getDeclaredMethod("set"+fieldName,field.getType());
				//System.out.println("Method invoke: " + setMethod.toGenericString() ); 
				setMethod.setAccessible(true);
				setMethod.invoke(node,fieldValue);
				return fieldValue;

			}


		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; 
	}

	public static Object updateBeanAddObjType(Object data, String fieldName, Object fieldValue, String typeOfField) {
		Node node = DataManager.getIstance().getNode(data);
		try {
			Field field = node.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			//System.out.println("UpdateBean: " + node + " wiht value: " + fieldValue);
			

			Class<?> fieldType = field.getType();
			Object obj = null;

			if(fieldType.equals(List.class)) {
				Type type = Util.getGenericTypeOfList(field);
				//System.out.println("Property is a List: " + type.getTypeName());

				if(Util.isPrimitiveClass(type)) {
					obj = createNewObjPrimitiveType(type,fieldValue.toString());
				} else {
					obj = fieldValue;
				}

				List list = (List)field.get(node);
				if(list==null) {
					list = new ArrayList();
					list.add(obj);
					field.set(node, list);

				} else {
					Class<?> instance = ClassUtils.getClass(typeOfField);
					Method add = node.getClass().getDeclaredMethod("add"+fieldName,instance);
					//System.out.println("Method invoke: " + add.toGenericString()); 
					add.setAccessible(true);
					add.invoke(node,obj);
				}
				return obj;

			} else {
				Class<?> instance = ClassUtils.getClass(typeOfField);
				//System.out.println("Property is an single object: " + field.getType());
				//System.out.println("Setting IdRef on Node: " + node);
				Method setMethod = node.getClass().getDeclaredMethod("set"+fieldName,instance);
				//System.out.println("Method invoke: " + setMethod.toGenericString() ); 
				setMethod.setAccessible(true);
				setMethod.invoke(node,fieldValue);
				return fieldValue;

			}


		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; 
	}




	public static void updateBeanRemoveObj(Object data, String fieldName, Object fieldValue) {
		Node node = DataManager.getIstance().getNode(data);
		try {
			Field field = node.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			//System.out.println("UpdateBean: " + node);
			//System.out.println("property name: " + fieldName + " type: " + field.getType());
			//System.out.println("value to remove: " + fieldValue);

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

			} 


		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public static void updateBeanModifyObj(Object data, String fieldName, Object oldFieldValue, Object newFieldValue) {
		Node node = DataManager.getIstance().getNode(data);
		try {
			Field field = node.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			//System.out.println("Bean: " + node);
			//System.out.println("Property name: " + fieldName + " type: " + field.getType());
			//System.out.println("value to be modified: " + oldFieldValue + " with: " + newFieldValue);

			Class<?> fieldType = field.getType();
			if(fieldType.equals(List.class)) {
				Type type = Util.getGenericTypeOfList(field);
				//System.out.println("Property is a List: " + type.getTypeName());
				Class<?> instance = ClassUtils.getClass(type.getTypeName());

				List list = (List)field.get(node);
				if(list!=null) {
					int objPosition = list.indexOf(oldFieldValue);
					list.set(objPosition, newFieldValue);
				}
			} 


		} catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}


	public static <C> Constructor<C> getAppropriateConstructor(Class<C> c, Object[] initArgs){
		if(initArgs == null)
			initArgs = new Object[0];
		for(Constructor con : c.getDeclaredConstructors()){
			Class[] types = con.getParameterTypes();
			if(types.length!=initArgs.length)
				continue;
			boolean match = true;
			for(int i = 0; i < types.length; i++){
				Class need = types[i], got = initArgs[i].getClass();
				if(!need.isAssignableFrom(got)){
					if(need.isPrimitive()){
						match = (int.class.equals(need) && Integer.class.equals(got))
								|| (long.class.equals(need) && Long.class.equals(got))
								|| (char.class.equals(need) && Character.class.equals(got))
								|| (short.class.equals(need) && Short.class.equals(got))
								|| (boolean.class.equals(need) && Boolean.class.equals(got))
								|| (byte.class.equals(need) && Byte.class.equals(got));
					}else{
						match = false;
					}
				}
				if(!match)
					break;
			}
			if(match)
				return con;
		}
		throw new IllegalArgumentException("Cannot find an appropriate constructor for class " + c + " and arguments " + Arrays.toString(initArgs));
	}
	
	public static Object createNewObjPrimitiveType(Type type, Calendar args) {
		Class<?> classField;
		try {
			classField = ClassUtils.getClass(type.getTypeName());
			Constructor constructor = getAppropriateConstructor(classField, new Object[] {args});
			return constructor.newInstance(new Object[] {args});
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static Object createNewObjPrimitiveType(Type type, String args) {
		Class<?> classField;
		try {
			classField = ClassUtils.getClass(type.getTypeName());
			Constructor constructor = getAppropriateConstructor(classField, new Object[] {args});
			return constructor.newInstance(new Object[] {args});
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static Object createNewObjPrimitiveType(String type, String args) {
		Class<?> classField;
		try {
			classField = ClassUtils.getClass(type);
			Constructor constructor = getAppropriateConstructor(classField, new Object[] {args});
			return constructor.newInstance(new Object[] {args});
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static Object getValueFromBean(Node node, String fieldName) {

		try {
			Method getMethod = node.getClass().getDeclaredMethod("get"+fieldName,null);
			getMethod.setAccessible(true);
			Object value = getMethod.invoke(node,null);
			return value;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static List<Node> getNodeListFromBean(Node node, String fieldName) {

		try {
			Method add = node.getClass().getDeclaredMethod("get"+fieldName);
			add.setAccessible(true);
			List list = (List<Node>) add.invoke(node,null);
			////System.out.println("getNodeListFromBean: " + list);
			return list;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static List getValueListFromBean(Node node, String fieldName) {

		try {
			Method add = node.getClass().getDeclaredMethod("get"+fieldName);
			add.setAccessible(true);
			List valueList = (List) add.invoke(node,null);
			return valueList;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> getAvailableClasses(String className, String field){

		List<String> availableClasses = new ArrayList<String>();

		try {
			//Class<?> c = Class.forName(className);
			Class<?> c = ClassUtils.getClass(className);
			Arrays.asList(c.getMethods()).forEach(m->{
					if(m.getName().startsWith("set") && m.getName().substring(3, m.getName().length()).equals(field)) {
						Arrays.asList(m.getParameterTypes()).forEach(e->{
							availableClasses.add(e.getName());
						});
					}
					if(m.getName().startsWith("add") && m.getName().substring(3, m.getName().length()).equals(field)) {
						Arrays.asList(m.getParameterTypes()).forEach(e->{
							availableClasses.add(e.getName());
						});
					}
			});
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		//System.out.println("AVAILABLE: "+availableClasses.toString());

		List<String> notBasic = Engine.getIstance().getM2c().getClassesGenerated().stream()
				.filter(e->availableClasses.contains(e.getName()))
				.map(e->e.getName()).collect(Collectors.toList());

		List<String> basic = availableClasses.stream().filter(e->Util.isPrimitiveClass(e)).collect(Collectors.toList());

		notBasic.addAll(basic);
		//System.out.println("FILTERED: "+notBasic);

		return notBasic;
	}

	public static void selectNodeOnGraph(Node node) {
		// set focus on graph node which has the same id

		try {
			ViewGraph graph = (ViewGraph) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewGraph");

			Iterator<?> iterator = graph.getViewer().getGraphControl().getNodes().iterator();
			while (iterator.hasNext()) {
				GraphNode item = (GraphNode) iterator.next();
				Node nodeOnGraph = (Node) item.getData("node");

				if(nodeOnGraph == node) {
					graph.getViewer().getGraphControl().setSelection(new GraphItem[]{item});
					return;
				}

			}

		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static void selectNodeInOutline(Node node) {
		// set focus node in outline

		try {
			ViewOutline outline = (ViewOutline) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewOutline");

			outline.getOutlineViewer().setSelection(new StructuredSelection ( node ));

		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static void selectConnectionInOutline(Connection connection) {
		// set focus node in outline

		try {
			ViewOutline outline = (ViewOutline) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewOutline");

			outline.getOutlineViewer().setSelection(new StructuredSelection ( connection ));

		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static void setNodePositionOnGraph(Node node, double x, double y) {
		// set position x,y on graph node
		GraphNode graphNode = getNodeOnGraph(node);
		graphNode.setLocation(x, y);
		
	}
	
	
	public static GraphNode getNodeOnGraph(Node node) {
		// set focus on graph node which has the same id

		try {
			ViewGraph graph = (ViewGraph) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewGraph");

			Iterator<?> iterator = graph.getViewer().getGraphControl().getNodes().iterator();
			while (iterator.hasNext()) {
				GraphNode item = (GraphNode) iterator.next();
				Node nodeOnGraph = (Node) item.getData("node");

				if(nodeOnGraph == node) {
					
					return item;
				}

			}

		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void selectConnectionOnGraph(Connection connection) {
		
		Node source = connection.getSource();
		Node destination = connection.getDestination();
		String connectionLabel = connection.getLabel().replace("_", ":");

		// set focus on graph connection 
		try {
			ViewGraph graph = (ViewGraph) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewGraph");

			Iterator<?> iterator = graph.getViewer().getGraphControl().getConnections().iterator();
			while (iterator.hasNext()) {
				GraphConnection item = (GraphConnection) iterator.next();

				if(item.getSource().getData("node") == source && 
						item.getDestination().getData("node") == destination &&
						item.getText().trim().equals(connectionLabel)) {
					graph.getViewer().getGraphControl().setSelection(new GraphConnection[]{item});
					return;
				}
			}

		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	

	public static void updateBeanRemoveObj(Object source, String fieldName, Object fieldValue, String typeOfField) {
		Node node = DataManager.getIstance().getNode(source);
		//System.out.println("Bean: " + node);
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
	
	public static Image createImageIcon(RGB rgb) {
		Image image = new Image(Display.getDefault(), 16, 16);
		GC gc = new GC(image);
		gc.setBackground(new Color(Display.getDefault(), rgb));
		gc.setLineWidth(1);
		
		gc.fillOval(0, 0, 16, 16);
		gc.drawOval(0, 0, 15, 15);
		gc.dispose();
		return image;


	}
	
	public static Node addNodeOnGraph(Class classNode) {
		//CreateNewNodeDialog dialog = new CreateNewNodeDialog(Display.getDefault().getActiveShell(),classNode.getSimpleName());
		//if(dialog.open() == Window.OK) {
		if(MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Question", "Do you want to create a new node " + classNode.getSimpleName() + "?")) {
			
			try {
				Class classItem = classNode;
				//Object[] intArgs = new Object[] {UUID.randomUUID().toString(), classItem.getSimpleName()};
				//String IDNode = dialog.getIDvalue();
				String IDNode = Engine.getIstance().getMs().getRootOntology() + classItem.getSimpleName() + "/" + UUID.randomUUID();
				

				if(DataManager.getIstance().checkIDNode(IDNode)) {
					MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", 
							"A node with that ID already exists in the graph.\nPlease, specify a different ID.");
					return null;
				}
				Object[] intArgs = new Object[] {IDNode, classItem.getSimpleName()};
				Class[] intArgsClass = new Class[] { String.class, String.class };
				Constructor constructor = classItem.getConstructor(intArgsClass);
				Node node = (Node) constructor.newInstance(intArgs);

				//System.out.println("Created Node class: " + node);
				DataManager.getIstance().getNodes().add(node);

				ViewGraph.updateGraph();
				

				String IDFormView = "org.epos.metadata.editor.viewForm." + 
						node.getNodeName()+ "-" + node.getId().replace(":", "_");
				
				

				ViewForm.createOpenFormNode(node, IDFormView);
				//Util.selectNodeOnGraph(node.getNodeName()+ "-" + node.getId());
				Util.selectNodeOnGraph(node);
				Util.updateFileEditor();

				ViewOutline.updateOutline();
				
				CompositeGraphFilter.enableChipsByFilter(Util.searchClassByNode(node));
				
				return node;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return null; 

	}
	
	public static Image createImageIcon(String classShape) {

		Color c = assignColor(classShape);
		Image image = new Image(Display.getDefault(), 16, 16);
		GC gc = new GC(image);

		gc.setBackground(new org.eclipse.swt.graphics.Color(Display.getDefault(),c.getRed(), c.getGreen(), c.getBlue()));
		//gc.setBackground(randomColor());
		gc.setLineWidth(1);

		gc.fillOval(0, 0, 16, 16);
		gc.drawOval(0, 0, 15, 15);
		gc.dispose();
		return image;


	}

	public static Color assignColor(String str) {

		StringBuffer hex = new StringBuffer();

		// loop chars one by one
		for (char temp : str.toCharArray()) {
			int decimal = (int) temp;
			hex.append(Integer.toHexString(decimal+str.toCharArray().length));

		}

		int r = Integer.valueOf( hex.toString().substring( 1, 3 ), 16 );
		int g = Integer.valueOf( hex.toString().substring( 3, 5 ), 16 );
		int b = Integer.valueOf( hex.toString().substring( 5, 7 ), 16 );
		////System.out.println("Color of " + str + " hex: " + hex.toString() + " RGB: " + r + " " + g + " " + b);
		return new Color(Display.getDefault(),r,g,b);

	}
	
	public static String getTermFromField(Node node, String fieldName) {

		try {
			Field field = node.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			Shacl annotation = field.getAnnotation(Shacl.class);
			return annotation.shortVocabulary()+":"+annotation.term();
		} catch (IllegalArgumentException | SecurityException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}

}

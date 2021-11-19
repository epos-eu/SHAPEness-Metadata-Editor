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
package org.epos.metadata.editor.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.Connection;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.utils.Icons;
import org.epos.metadata.editor.ui.utils.Util;

public class ViewOutline extends ViewPart {

	private TreeViewer outlineViewer;
	private transient Map<Class,Image> shapes = new HashMap<Class, Image>();
	private ArrayList<Class> categories = new ArrayList<Class>();

	public ViewOutline() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		NodeFilterdTree filteredTree = new NodeFilterdTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, 
				new NodePatternFilter(), true);


		//TreeViewer treeViewer = filteredTree.getViewer();
		//outlineViewer = new TreeViewer(parent);
		outlineViewer = filteredTree.getViewer();
		outlineViewer.setContentProvider(new OutlineContentProvider());
		outlineViewer.setLabelProvider(new OutlineLabelProvider());
		outlineViewer.setInput(DataManager.getIstance().getNodes());

		outlineViewer.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object obj1, Object obj2) {
				if(obj1 instanceof Node && obj2 instanceof Node) {
					Node node1 = (Node) obj1;
					Node node2 = (Node) obj2;
					return node1.getNodeName().compareTo(node2.getNodeName());
				} else if(obj1 instanceof Connection && obj2 instanceof Connection) {
					Connection conn1 = (Connection) obj1;
					Connection conn2 = (Connection) obj2;
					return conn1.getSource().getNodeName().compareTo(conn2.getSource().getNodeName());
				}

				return 0;
			};
		});
		
		outlineViewer.getTree().addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				if (outlineViewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection sel = (IStructuredSelection) outlineViewer.getSelection();
					Object item = sel.getFirstElement();
					if(item instanceof Node) {
						Node node = (Node) item;
						//Util.selectNodeOnGraph(node.getNodeName() + "-" + node.getId());
						Util.selectNodeOnGraph(node);
						String IDFormView = "org.epos.metadata.editor.viewForm."+ node.getNodeName() + "-" + node.getId().replace(":", "_"); 
						ViewForm.createOpenFormNode(node, IDFormView);
						CompositeGraphFilter.enableChipsByFilter(Util.searchClassByNode(node));
					} else if(item instanceof Connection) {
						Connection connection = (Connection)item;
						Util.selectConnectionOnGraph(connection);
						CompositeGraphFilter.enableChipsByFilter(Util.searchClassByNode(connection.getSource()));
						CompositeGraphFilter.enableChipsByFilter(Util.searchClassByNode(connection.getDestination()));
					}
				}
			}
		});
		
		
		outlineViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				IStructuredSelection sel = (IStructuredSelection) event.getStructuredSelection();
				Object item = sel.getFirstElement();
				if(item instanceof Node) {
					Node node = (Node) item;
					Util.selectNodeOnGraph(node);
					//String IDFormView = "org.epos.metadata.editor.viewForm."+ node.getNodeName() + "-" + node.getId().replace(":", "_"); 
					//ViewForm.createOpenFormNode(node, IDFormView);
				} else if(item instanceof Connection) {
					Connection connection = (Connection)item;
					Util.selectConnectionOnGraph(connection);
				}


			}
		});
		

		MenuManager menuMgr = new MenuManager();

		Menu menu = menuMgr.createContextMenu(outlineViewer.getControl());
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {

				if (outlineViewer.getSelection().isEmpty()) {
					return;
				}

				if (outlineViewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) outlineViewer.getSelection();
					Object item = selection.getFirstElement();
					if(item instanceof Node) {
						
						fillNodeContextMenu(menuMgr);

					} else if(item instanceof Connection)  {
						
						fillConnectionContextMenu(menuMgr);

					}
				}
			}
		});
		menuMgr.setRemoveAllWhenShown(true);
		outlineViewer.getControl().setMenu(menu);


	}

	protected void fillConnectionContextMenu(IMenuManager contextMenu) {

		Action remove = new Action("Remove") {
			@Override
			public void run() {
				try {
					IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
							.getService(IHandlerService.class);
					handlerService.executeCommand("org.epos.metadata.editor.removeConnection", null);
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotDefinedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotEnabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotHandledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		remove.setImageDescriptor(Icons.REMOVE.descriptor());
		contextMenu.add(remove);
	}

	protected void fillNodeContextMenu(IMenuManager contextMenu) {

		Action properties = new Action("Properties") {
			@Override
			public void run() {
				try {
					IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
							.getService(IHandlerService.class);
					handlerService.executeCommand("org.epos.metadata.editor.propertiesNode", null);
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotDefinedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotEnabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotHandledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		properties.setImageDescriptor(Icons.PROPERTIES.descriptor());
		contextMenu.add(properties);

		Action color = new Action("Change Color") {
			@Override
			public void run() {
				Object objSelected = ((IStructuredSelection) outlineViewer.getSelection()).getFirstElement();

				if(objSelected instanceof Node) {
					Node selectedNode = (Node) objSelected;
					Class classNode = selectedNode.getClass();
					Image icon = Util.getImageShapeByClassName(classNode.getSimpleName());
					int pixelValue = icon.getImageData().getPixel(5,5);
					ColorDialog dlg = new ColorDialog(Display.getDefault().getActiveShell());
					PaletteData pal = icon.getImageData().palette; 
					dlg.setRGB(pal.getRGB(pixelValue));

					// Change the title bar text
					dlg.setText("Choose a Color");
					// Open the dialog and retrieve the selected color
					RGB rgb = dlg.open();
					if (rgb != null) {

						// change color of treeItem and shapeList

						Image newIcon = Util.createImageIcon(rgb);
						Map<Class, Image> map = Util.getShapesList();
						map.replace(classNode, newIcon);
						ImageDescriptor descriptor = ImageDescriptor.createFromImage(newIcon);
						//toolEntry.setSmallIcon(descriptor);

						try {
							ViewOutline outline = (ViewOutline) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
									getActivePage().showView("org.epos.metadata.editor.viewOutline");
							outline.setShapes(map);

							// UPDATE color shape in palette
							ViewGraphPalette.updatePaletteColorItem(classNode.getSimpleName(), newIcon);

						} catch (PartInitException e) {

							e.printStackTrace();
						}

						ViewOutline.updateOutline();
						ViewGraph.updateGraph();


					}
				}


			}

		};
		color.setImageDescriptor(Icons.PALETTE.descriptor());
		contextMenu.add(color);


		Action rename = new Action("Rename") {
			@Override
			public void run() {
				try {
					IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
							.getService(IHandlerService.class);
					handlerService.executeCommand("org.epos.metadata.editor.renameIDnode", null);
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotDefinedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotEnabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotHandledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		rename.setImageDescriptor(Icons.RENAME_NODEID_ENABLED.descriptor());
		contextMenu.add(rename);
		
		Action duplicate = new Action("Duplicate") {
			@Override
			public void run() {
				try {
					IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
							.getService(IHandlerService.class);
					handlerService.executeCommand("org.epos.metadata.editor.duplicateNode", null);
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotDefinedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotEnabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotHandledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		duplicate.setImageDescriptor(Icons.DUPLICATE_NODE.descriptor());
		contextMenu.add(duplicate);

		Action remove = new Action("Remove") {
			@Override
			public void run() {
				try {
					IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
							.getService(IHandlerService.class);
					handlerService.executeCommand("org.epos.metadata.editor.removeNode", null);
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotDefinedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotEnabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotHandledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		remove.setImageDescriptor(Icons.REMOVE.descriptor());
		contextMenu.add(remove);


	}



	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	class OutlineContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {

		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			ArrayList<String> categories = new ArrayList<String>();
			categories.add("Nodes");
			categories.add("Relationships");
			return categories.toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {

			if(parentElement instanceof String) {
				String category = (String) parentElement;
				if(category.equals("Nodes")) {
					ArrayList<Class<?>> list = new ArrayList<Class<?>>();
					for (Class<?> shape : categories) {
						if(DataManager.getIstance().getNodesByType(shape.getName()).size() != 0){
							list.add(shape);
						}
					}
					return list.toArray();


				} else if(category.equals("Relationships")) {
					return DataManager.getIstance().getConnections().toArray();
				}
			} else if(parentElement instanceof Class) {
				Class classCategory = (Class) parentElement;
				if(DataManager.getIstance().getNodesByType(classCategory.getName()).size() == 0){
					return null;
				}
				return DataManager.getIstance().getNodesByType(classCategory.getName()).toArray();

			}
			return null;
		}

		@Override
		public Object getParent(Object element) {

			if(element instanceof Node) {
				return Util.searchClassByNode((Node)element);
			} else if(element instanceof Class) {
				return "Nodes";
			} else if(element instanceof Connection) {
				return "Relationships";
			}

			return null;
		}

		@Override
		public boolean hasChildren(Object element) {

			if(element instanceof String) {
				return true;
			} else if(element instanceof Class) {
				if(DataManager.getIstance().getNodesByType(((Class)element).getName()).size() == 0){
					return false;
				}
				return true;
			}
			return false;
		}

	}

	class OutlineLabelProvider extends LabelProvider implements IStyledLabelProvider {

		public OutlineLabelProvider() {

		}

		@Override
		public StyledString getStyledText(Object element) {
			return null;
		}

		@Override
		public String getText(Object element) {
			if(element instanceof String) {
				if(element.toString().equals("Nodes")) {
					return (String) element.toString() + " (" + DataManager.getIstance().getNodes().size() +")";
				} else if(element.toString().equals("Relationships")) {
					return (String) element.toString() + " (" + DataManager.getIstance().getConnections().size() +")";
				}

			} else if(element instanceof Node) {
				Node node = (Node) element;
				return node.getId();
			} else if (element instanceof Connection) {
				Connection connection = (Connection) element;
				Node source = connection.getSource();
				Node destination = connection.getDestination();
				return source.getNodeName() + "-" + source.getId() + " -> " +
				destination.getNodeName()  + "-" + destination.getId();

			} else if (element instanceof Class) {
				Class classCategory = (Class)element;

				if(DataManager.getIstance().getNodesByType(classCategory.getName()).size() == 0){
					return null;
				}

				return (String) classCategory.getSimpleName() + " (" + 
				DataManager.getIstance().getNodesByType(classCategory.getName()).size() +")";


			}
			return null;
		}

		@Override
		public Image getImage(Object element) {

			if(element instanceof Node) {
				Class shape = Util.searchClassByNode((Node) element);
				Image icon = shapes.get(shape);
				return icon;
				//return Icons.NODE.image();

			} else if(element instanceof Connection) {

				return Icons.EDGE.image();

			}
			return super.getImage(element);
		}

	}



	
	public TreeViewer getOutlineViewer() {
		return outlineViewer;
	}

	public static void updateOutline() {
		ViewOutline outline;
		try {
			outline = (ViewOutline) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewOutline");
			outline.getOutlineViewer().refresh();
			outline.getOutlineViewer().expandAll();
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class NodePatternFilter extends PatternFilter {

		protected boolean isLeafMatch(Viewer viewer, Object element){

			if(element instanceof Node) {
				Node node = (Node) element;
				return wordMatches(node.getNodeName()+node.getId());
			} else if(element instanceof Connection) {
				Connection connection = (Connection) element;
				Node source = connection.getSource();
				Node destination = connection.getDestination();
				if(wordMatches(source.getNodeName()+source.getId()) || 
						wordMatches(destination.getNodeName()+destination.getId())) {
					return true;
				}

			}
			return false;
		}

	}

	class NodeFilterdTree extends FilteredTree {

		public NodeFilterdTree(Composite parent, int style, NodePatternFilter nodePatternFilter, boolean b) {
			super(parent, style, nodePatternFilter, b);
		}

		@Override
		protected long getRefreshJobDelay() {
			long delay = super.getRefreshJobDelay();
			outlineViewer.getTree().redraw();
			return delay;
		}

	}


	public void setShapes(Map<Class, Image> shapes) {
		ArrayList<Class> sortedKeysList = new ArrayList<>(shapes.keySet());
		Collections.sort(sortedKeysList, Comparator.comparing(Class::getSimpleName));
		this.categories = sortedKeysList;
		this.shapes = shapes;

	}


}

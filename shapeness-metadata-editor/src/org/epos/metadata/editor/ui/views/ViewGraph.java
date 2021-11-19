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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.chips.Chips;
import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.core.viewers.internal.ZoomManager;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpaceTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.Connection;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.model.NodeContentProvider;
import org.epos.metadata.editor.ui.model.NodeFilter;
import org.epos.metadata.editor.ui.model.NodeLabelProvider;
import org.epos.metadata.editor.ui.utils.Icons;
import org.epos.metadata.editor.ui.utils.Util;




public class ViewGraph extends ViewPart implements IZoomableWorkbenchPart {

	private static GraphViewer viewer;
	private static Composite parent;
	private static Section filterSection;
	private static Composite filterArea;
	private static ArrayList<Class> chipFilters = new ArrayList<Class>();
	private static ArrayList<ViewerFilter> arrayFilters = null;


	public void createPartControl(Composite parent) {

		this.parent = parent;

		parent.setLayout(new GridLayout());

		CompositeGraphFilter compositeGraphFilter = new CompositeGraphFilter(parent);

		viewer = new GraphViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		viewer.setContentProvider(new NodeContentProvider());
		viewer.setLabelProvider(new NodeLabelProvider());        
		viewer.setInput(DataManager.getIstance().getNodes());
		LayoutAlgorithm layout = setLayout();
		viewer.setLayoutAlgorithm(layout, true);
		viewer.applyLayout();
		
		

		GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getGraphControl());

		/*SpaceTreeLayoutAlgorithm spaceTreeLayoutAlgorithm = new SpaceTreeLayoutAlgorithm(); 
		viewer.setLayoutAlgorithm(spaceTreeLayoutAlgorithm, true); 
		viewer.applyLayout();*/
		//viewer.getGraphControl().setExpandCollapseManager(spaceTreeLayoutAlgorithm.getExpandCollapseManager()); 
		//viewer.getGraphControl().setSubgraphFactory(new DefaultSubgraph.LabelSubgraphFactory()); 


		ZoomManager zoomManager = new ZoomManager(viewer.getGraphControl().getRootLayer(), viewer.getGraphControl().getViewport());
		makeAction(zoomManager);
		fillToolBar();

		DropTarget target = new DropTarget(viewer.getGraphControl(), DND.DROP_MOVE);
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		target.setTransfer(types);
		target.addDropListener(new DropTargetListener() {

			@Override
			public void dragEnter(DropTargetEvent event) {
				////System.out.println("Drag enter on graph");

			}

			@Override
			public void dragLeave(DropTargetEvent event) {
				// TODO Auto-generated method stub
				////System.out.println("dragLeaver on graph");

			}

			@Override
			public void dragOperationChanged(DropTargetEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dragOver(DropTargetEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drop(DropTargetEvent event) {
				if (ViewGraphPalette.getDrawer().getChildren().size() > 0) {
					String classShape = (String) event.data;
					Node node = Util.addNodeOnGraph(Util.searchClassByName(classShape));

					Graph g = ((GraphViewer) getViewer()).getGraphControl();
					Point p = g.getDisplay().map(null, g, event.x, event.y);

					Util.setNodePositionOnGraph(node, p.x, p.y);
				}

			}

			@Override
			public void dropAccept(DropTargetEvent event) {
				// TODO Auto-generated method stub
			}

		});


		/*we bind the zoom mechanic to a simple mouse wheel listener
		viewer.getGraphControl().addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(MouseEvent e) {
				if (e.count < 0) {
					zoomManager.zoomOut();
				} else {
					zoomManager.zoomIn();
				}
			}
		});
		 */

		viewer.getGraphControl().addKeyListener(new KeyAdapter(){	
			public void keyPressed(KeyEvent event){
				if(event.keyCode == SWT.DEL){
					ISelection selection = viewer.getSelection();
					if(!selection.isEmpty()) {
						Object objSelected = ((IStructuredSelection) selection).getFirstElement();

						if(objSelected instanceof Node) {
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
						} else if(objSelected instanceof EntityConnectionData) {
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
					}
				}
			}

		});

		/* Context menu */
		viewer.getGraphControl().addMenuDetectListener(new MenuDetectListener(){
			@Override
			public void menuDetected(MenuDetectEvent e){
				Point point = viewer.getGraphControl().toControl(e.x, e.y);
				IFigure fig = viewer.getGraphControl().getFigureAt(point.x, point.y);

				if (fig != null) {

					if(((Graph)e.widget).getSelection().get(0) instanceof GraphNode) {
						createNodeContextMenu(parent.getShell());
					} else {

						if(((Graph)e.widget).getSelection().get(0) instanceof GraphConnection) {
							createConnectionContextMenu(parent.getShell());
						}

					}
				} else {
					//createGraphContextMenu(parent.getShell());
				}
			}


		});

		viewer.getGraphControl().addMouseListener(new MouseAdapter() {        	

			public void mouseDown(MouseEvent e) {
				ISelection selection = viewer.getSelection();
				if(!selection.isEmpty()) {
					Object objSelected = ((IStructuredSelection) selection).getFirstElement();

					if(objSelected instanceof Node) {
						Node selectedNode = (Node) objSelected;
						// Node focus in outline
						Util.selectNodeInOutline(selectedNode);

						String IDView = "org.epos.metadata.editor.viewForm."+ selectedNode.getNodeName()+"-"+selectedNode.getId().replace(":", "_"); 
						IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

						IViewPart viewNew = activePage.findView("org.epos.metadata.editor.viewForm" + ":" + IDView);
						if (viewNew != null) {

							try {
								activePage.showView("org.epos.metadata.editor.viewForm", IDView, IWorkbenchPage.VIEW_ACTIVATE);		           	   

							} catch (PartInitException e1) {
								e1.printStackTrace();
							}
						}


					} else if(objSelected instanceof EntityConnectionData) {
						Connection selectedConnection = DataManager.getIstance().getConnectionByEntityConnectionData((EntityConnectionData) objSelected);
						Util.selectConnectionInOutline(selectedConnection);
					}

				}


			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				createOpenTable(e);

			}

			private void createOpenTable(MouseEvent e) {

				ISelection selection = viewer.getSelection();
				if(!selection.isEmpty()) {
					Object objSelected = ((IStructuredSelection) selection).getFirstElement();

					if(objSelected instanceof Node) {
						Node selectedNode = (Node) objSelected;
						Node node = DataManager.getIstance().getNode(selectedNode);
						String IDView = "org.epos.metadata.editor.viewForm."+ node.getNodeName()+"-"+node.getId().replace(":", "_"); 
						ViewForm.createOpenFormNode(node, IDView);
					}

				}

			}

		});

	}




	protected void createGraphContextMenu(Shell shell) {
		Menu menu = new Menu(shell, SWT.POP_UP);
		MenuItem spring = new MenuItem(menu, SWT.NONE);
		spring.setText("Spring Layout");
		menu.setVisible(true);
		spring.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {

				try {
					IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
							.getService(IHandlerService.class);
					handlerService.executeCommand("org.epos.metadata.editor.commandChangeLayout", event);
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

		});

	}




	protected void createConnectionContextMenu(Shell shell) {
		Menu menu = new Menu(shell, SWT.POP_UP);
		MenuItem remove = new MenuItem(menu, SWT.NONE);
		remove.setImage(Icons.REMOVE.image());
		remove.setText("Remove");
		menu.setVisible(true);
		remove.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {

				try {
					IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
							.getService(IHandlerService.class);
					handlerService.executeCommand("org.epos.metadata.editor.removeConnection", event);
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

		});


	}




	protected void createNodeContextMenu(Shell shell) {
		Menu menu = new Menu(shell, SWT.POP_UP);

		MenuItem properties = new MenuItem(menu, SWT.NONE);
		properties.setImage(Icons.PROPERTIES.image());
		properties.setText("Properties");
		properties.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {


				try {
					IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
							.getService(IHandlerService.class);
					handlerService.executeCommand("org.epos.metadata.editor.propertiesNode", event);
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

		});

		MenuItem changeColor = new MenuItem(menu, SWT.NONE);
		changeColor.setImage(Icons.PALETTE.image());
		changeColor.setText("Change Color");
		changeColor.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {

				Object objSelected = ((IStructuredSelection) viewer.getSelection()).getFirstElement();

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
						CompositeGraphFilter.updateColorFilters();
						ViewGraph.updateGraph();


					}
				}


			}

		});

		MenuItem rename = new MenuItem(menu, SWT.NONE);
		rename.setImage(Icons.RENAME_NODEID_ENABLED.image());
		rename.setText("Rename ID");
		rename.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {

				try {
					IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
							.getService(IHandlerService.class);
					handlerService.executeCommand("org.epos.metadata.editor.renameIDnode", event);
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

		});

		
		MenuItem duplicate = new MenuItem(menu, SWT.NONE);
		duplicate.setImage(Icons.DUPLICATE_NODE.image());
		duplicate.setText("Duplicate node");
		duplicate.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {

				try {
					IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
							.getService(IHandlerService.class);
					handlerService.executeCommand("org.epos.metadata.editor.duplicateNode", event);
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

		});
		 



		MenuItem remove = new MenuItem(menu, SWT.NONE);
		remove.setImage(Icons.REMOVE.image());
		remove.setText("Remove");
		menu.setVisible(true);
		remove.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {

				try {
					IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
							.getService(IHandlerService.class);
					handlerService.executeCommand("org.epos.metadata.editor.removeNode", event);
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

		});

	}


	private void makeAction(ZoomManager zoomManager) {
		IToolBarManager toolBar = getViewSite().getActionBars().getToolBarManager();


		Action zoomInAction = new Action() {
			public void run() {
				zoomManager.zoomIn();
			}
		};
		//zoomInAction.setText("Zoom IN");
		zoomInAction.setToolTipText("Zoom In");
		zoomInAction.setImageDescriptor(Icons.ZOOMIN.descriptor());
		toolBar.add(zoomInAction);

		Action zoomOutAction = new Action() {
			public void run() {
				zoomManager.zoomOut();
			}
		};
		//zoomOutAction.setText("Zoom OUT");
		zoomOutAction.setToolTipText("Zoom Out");
		zoomOutAction.setImageDescriptor(Icons.ZOOMOUT.descriptor());
		toolBar.add(zoomOutAction);

		
	}




	private LayoutAlgorithm setLayout() {
		LayoutAlgorithm layout;
		//layout = new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		//layout= new SpaceTreeLayout(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		//layout = new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		//layout = new GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		// layout = new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		// layout = new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		/*layout = new
				CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING, new
						LayoutAlgorithm[] { new
								DirectedGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), new
								HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING) });
		 */

		layout = new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);


		return layout;

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}


	private void fillToolBar() {
		ZoomContributionViewItem zoom = new ZoomContributionViewItem(this);
		IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(zoom);

	}


	public static void updateGraph() {
		try {
			ViewGraph graph = (ViewGraph) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewGraph");
			graph.getViewer().refresh();
			//graph.getViewer().applyLayout();
		} catch (PartInitException pie) {
			// TODO Auto-generated catch block
			pie.printStackTrace();
		}
		CompositeGraphFilter.updateFilterItems();
	}

	public static void refreshNodeOnGraph(Node node) {
		try {
			ViewGraph graph = (ViewGraph) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewGraph");
			graph.getViewer().refresh(node);
			//graph.getViewer().applyLayout();
		} catch (PartInitException pie) {
			// TODO Auto-generated catch block
			pie.printStackTrace();
		}
		CompositeGraphFilter.updateFilterItems();
	}

	public static void updateGraphAndFocusNode(Node node) {

		try {
			ViewGraph graph = (ViewGraph) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewGraph");
			graph.getViewer().refresh();
			//Util.selectNodeOnGraph(node.getNodeName()+ "-" + node.getId());
			Util.selectNodeOnGraph(node);
			//graph.getViewer().applyLayout();
		} catch (PartInitException pie) {
			// TODO Auto-generated catch block
			pie.printStackTrace();
		}		
	}

	public static void updateGraphWithLayout() {
		try {
			ViewGraph graph = (ViewGraph) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewGraph");
			graph.getViewer().refresh();
			graph.getViewer().applyLayout();
			CompositeGraphFilter.updateFilterItems();
		} catch (PartInitException pie) {
			// TODO Auto-generated catch block
			pie.printStackTrace();
		}		
	}

	public static void updateGraphNode(Node node) {
		try {
			ViewGraph graph = (ViewGraph) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewGraph");
			graph.getViewer().update(node, null);

		} catch (PartInitException pie) {
			// TODO Auto-generated catch block
			pie.printStackTrace();
		}		
	}


	public void setLayoutManager(String layout) {

		if(layout == null) {	
			return;
		}
		switch(layout) { 
		case "Spring Layout": 
			viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case "Tree Layout": 
			viewer.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case "Grid Layout": 
			viewer.setLayoutAlgorithm(new GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case "Horizontal Tree Layout": 
			viewer.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case "Radial Layout": 
			viewer.setLayoutAlgorithm(new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		default:
			viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		} 

	}



	public static GraphViewer getViewer() {
		return viewer;
	}

	
	public static void saveGraphAsImage(String fileName) {
		ViewGraph graph;
		try {
			graph = (ViewGraph) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView("org.epos.metadata.editor.viewGraph");

			Point size = new Point( graph.getViewer().getGraphControl().getContents().getSize().width,  graph.getViewer().getGraphControl().getContents().getSize().height);
			Image image = new Image(null, size.x, size.y);
			GC gc = new GC(image);
			SWTGraphics swtGraphics = new SWTGraphics(gc);
			graph.getViewer().getGraphControl().getContents().paint(swtGraphics);
			gc.copyArea(image, 0, 0);
			gc.dispose();
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] { image.getImageData() };
			loader.save(fileName, SWT.IMAGE_PNG);
			image.dispose();

		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	

	public Composite getParent() {
		return parent;
	}

	@Override
	public AbstractZoomableViewer getZoomableViewer() {
		return viewer;
	}

}
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
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.internal.ui.palette.editparts.ToolEntryEditPart;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.palette.DefaultPaletteViewerPreferences;
import org.eclipse.gef.ui.palette.PaletteContextMenuProvider;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.views.palette.PaletteView;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.epos.metadata.editor.ui.dialogs.ChooseColorDialog;
import org.epos.metadata.editor.ui.model.Project;
import org.epos.metadata.editor.ui.utils.Icons;
import org.epos.metadata.editor.ui.utils.Util;


public class ViewGraphPalette extends PaletteView {

	private static PaletteDrawer drawer;
	private PaletteViewer palette;

	@Override
	public void createPartControl(Composite parent) {

		//Composite paletteComposite = new Composite(parent, SWT.NONE);
		palette = new PaletteViewer(); 
		palette.createControl(parent);
		palette.setPaletteRoot(getPaletteRoot());
		palette.enableVerticalScrollbar(true);
		palette.getContextMenu().dispose();

		//Cursor cursor = new Cursor(Display.getDefault(), SWT.CURSOR_HAND);
		/*
		Cursor cursor = new Cursor(Display.getDefault(), Icons.PALETTE_CURSOR.image().getImageData(), 0, 0);
		palette.getControl().addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				// TODO Auto-generated method stub
				palette.setCursor(cursor);
			}


		});
		 */

		palette.getControl().addMenuDetectListener(new MenuDetectListener(){
			@Override
			public void menuDetected(MenuDetectEvent e){

				if (drawer.getChildren().size() > 0) {
					Menu menu = new Menu(parent.getShell(), SWT.POP_UP);

					MenuItem create = new MenuItem(menu, SWT.NONE);
					create.setImage(Icons.PLUS_ENABLED.image());
					create.setText("Create a new node");
					menu.setVisible(true);
					create.addListener(SWT.Selection, new Listener(){

						@Override
						public void handleEvent(Event event) {
							StructuredSelection selection = (StructuredSelection) palette.getSelection();
							ToolEntryEditPart entry = (ToolEntryEditPart) selection.getFirstElement();

							CreationToolEntry toolEntry = (CreationToolEntry) entry.getModel();
							if(toolEntry != null) {
								////System.out.println("palette mouseDoubleClick class: " + toolEntry.getToolProperty("class"));
								Class classNode = (Class) toolEntry.getToolProperty("class");
								if(classNode != null) {
									Util.addNodeOnGraph(classNode);
								}

							}
						}
					});	
					/*
				MenuItem changeLabel = new MenuItem(menu, SWT.NONE);
				changeLabel.setImage(Icons.PLUS_ENABLED.image());
				changeLabel.setText("Change shape label");
				menu.setVisible(true);
				changeLabel.addListener(SWT.Selection, new Listener(){

					@Override
					public void handleEvent(Event event) {
						StructuredSelection selection = (StructuredSelection) palette.getSelection();
						ToolEntryEditPart entry = (ToolEntryEditPart) selection.getFirstElement();

						CreationToolEntry toolEntry = (CreationToolEntry) entry.getModel();
						if(toolEntry != null) {
							////System.out.println("palette mouseDoubleClick class: " + toolEntry.getToolProperty("class"));
							Class classNode = (Class) toolEntry.getToolProperty("class");
							Field[] fields = classNode.getDeclaredFields();
							List list = new ArrayList<String>();
							for (Field field : fields) {
								field.setAccessible(true);
								if(field.getType() == String.class || field.getType() == java.net.URI.class) {
									list.add(field.getName());
								}
							}
							//System.out.println("List of possible label: " + list);

						}
					}
				});	
					 */

					MenuItem changeColor = new MenuItem(menu, SWT.NONE);
					changeColor.setImage(Icons.PALETTE.image());
					changeColor.setText("Change Color");
					menu.setVisible(true);
					changeColor.addListener(SWT.Selection, new Listener(){

						@Override
						public void handleEvent(Event event) {
							StructuredSelection selection = (StructuredSelection) palette.getSelection();
							ToolEntryEditPart entry = (ToolEntryEditPart) selection.getFirstElement();

							CreationToolEntry toolEntry = (CreationToolEntry) entry.getModel();
							if(toolEntry != null) {
								////System.out.println("palette mouseDoubleClick class: " + toolEntry.getToolProperty("class"));
								Class classNode = (Class) toolEntry.getToolProperty("class");
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
									toolEntry.setSmallIcon(descriptor);

									try {
										ViewOutline outline = (ViewOutline) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
												getActivePage().showView("org.epos.metadata.editor.viewOutline");
										outline.setShapes(map);
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

				}
			}


		});


		DefaultPaletteViewerPreferences preferences = new DefaultPaletteViewerPreferences();
		preferences.setLayoutSetting(DefaultPaletteViewerPreferences.LAYOUT_DETAILS);

		DragSource source = new DragSource(palette.getControl(), DND.DROP_MOVE);
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		source.setTransfer(types);
		source.addDragListener(new DragSourceListener() {

			@Override
			public void dragStart(DragSourceEvent event) {
				if (drawer.getChildren().size() > 0) {
					StructuredSelection selection = (StructuredSelection) palette.getSelection();
					ToolEntryEditPart entry = (ToolEntryEditPart) selection.getFirstElement();

					CreationToolEntry toolEntry = (CreationToolEntry) entry.getModel();
					if(toolEntry != null) {
						//event.image = toolEntry.getSmallIcon().createImage();
						Image paletteImage = toolEntry.getSmallIcon().createImage();
						ImageData imageData = paletteImage.getImageData().scaledTo((int)(paletteImage.getImageData().width*1.5),(int)(paletteImage.getImageData().height*1.5));
						int whitePixel = imageData.palette.getPixel(new RGB(255,255,255));
						imageData.transparentPixel = whitePixel;
						event.image = new Image(null,imageData);
					}
				}

			}

			@Override
			public void dragSetData(DragSourceEvent event) {
				if (drawer.getChildren().size() > 0) {
					// Provide the data of the requested type.
					if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
						StructuredSelection selection = (StructuredSelection) palette.getSelection();
						ToolEntryEditPart entry = (ToolEntryEditPart) selection.getFirstElement();

						CreationToolEntry toolEntry = (CreationToolEntry) entry.getModel();
						if(toolEntry != null) {
							////System.out.println("palette mouseDoubleClick class: " + toolEntry.getToolProperty("class"));
							Class classNode = (Class) toolEntry.getToolProperty("class");
							if(classNode != null) {
								event.data = classNode.getSimpleName();

							}

						}

					}
				}
			}

			@Override
			public void dragFinished(DragSourceEvent event) {


			}

		});


		palette.getControl().addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDoubleClick(MouseEvent event) {
				if (drawer.getChildren().size() > 0) {
					StructuredSelection selection = (StructuredSelection) palette.getSelection();
					ToolEntryEditPart entry = (ToolEntryEditPart) selection.getFirstElement();

					CreationToolEntry toolEntry = (CreationToolEntry) entry.getModel();
					if(toolEntry != null) {
						////System.out.println("palette mouseDoubleClick class: " + toolEntry.getToolProperty("class"));
						Class classNode = (Class) toolEntry.getToolProperty("class");
						if(classNode != null) {
							Util.addNodeOnGraph(classNode);
						} else {
							String action = (String)toolEntry.getToolProperty("action");
							if(action.equals("changecolor")) {
								IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
								Project dataProject = context.get(Project.class);
								Map<Class, Image> shapesList = dataProject.getShapesList();
								ChooseColorDialog dlg = new ChooseColorDialog(Display.getDefault().getActiveShell(), shapesList);
								if(dlg.open() == Window.OK) {
									//System.out.println("Save changes of color prefs");
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
								}
							}
						}

					}

				}
			}
		});



	}

	private PaletteRoot getPaletteRoot() {
		PaletteRoot root = new PaletteRoot();


		/*
		PaletteDrawer toolGroup = new PaletteDrawer("Controls");

		CreationToolEntry creationEntryZoomIn = new CreationToolEntry("Zoom in", "Zoom in", 
				null,  Icons.ZOOMIN.descriptor(), null);
		creationEntryZoomIn.setToolProperty("ZoomIn", "ZoomIn");		
		toolGroup.add(creationEntryZoomIn);

		CreationToolEntry creationEntryZoomOut = new CreationToolEntry("Zoom out", "Zoom out", 
				null,  Icons.ZOOMOUT.descriptor(), null);
		creationEntryZoomOut.setToolProperty("ZoomOut", "ZoomOut");		
		toolGroup.add(creationEntryZoomOut);

		CreationToolEntry creationEntryRemoveNode = new CreationToolEntry("Remove Node", "Remove Node", 
				null,  Icons.DELETE_NODE_ENABLED.descriptor(), null);
		creationEntryRemoveNode.setToolProperty("removeNode", "removeNode");		
		toolGroup.add(creationEntryRemoveNode);

		CreationToolEntry creationEntryRemoveAllNodea = new CreationToolEntry("Remove all Nodes", "Remove all Nodes", 
				null,  Icons.DELETE_ALLNODES.descriptor(), null);
		creationEntryRemoveAllNodea.setToolProperty("removeAllNodes", "removeAllNodes");		
		toolGroup.add(creationEntryRemoveAllNodea);

		CreationToolEntry creationEntryRenameNode = new CreationToolEntry("Rename Node ID", "Rename Node ID", 
				null,  Icons.RENAME_NODEID_ENABLED.descriptor(), null);
		creationEntryRenameNode.setToolProperty("renameNode", "renameNode");		
		toolGroup.add(creationEntryRenameNode);

		root.add(toolGroup);
		 */

		/*
		PaletteDrawer color = new PaletteDrawer("Controls");
		CreationToolEntry creationEntry = new CreationToolEntry("Change Node Color",
				"Change Node Color", new SimpleFactory(ColorPreferencesHandler.class), Icons.PALETTE.descriptor(), Icons.PALETTE.descriptor());
		creationEntry.setToolProperty("action", "changecolor");
		color.add(creationEntry);
		root.add(color);*/

		drawer = new PaletteDrawer("Shapes");
		root.add(drawer);

		palette.setPaletteRoot(root);

		ContextMenuProvider provider = new PaletteContextMenuProvider(palette);
		palette.setContextMenu(provider);

		return root;
	}



	public void createPaletteItems(Map<Class, Image> map) {
		if(drawer.getChildren().size() > 0) {
			drawer.getChildren().clear();
		}

		ArrayList<Class> sortedKeysList = new ArrayList<>(map.keySet());
		Collections.sort(sortedKeysList, Comparator.comparing(Class::getSimpleName));

		for (Class classShape : sortedKeysList) {

			Image icon = map.get(classShape);
			ImageDescriptor descriptor = ImageDescriptor.createFromImage(icon);
			CreationToolEntry creationEntry = new CreationToolEntry(classShape.getSimpleName(),
					classShape.getSimpleName(), new SimpleFactory(classShape.getClass()), descriptor, descriptor);
			creationEntry.setToolProperty("class", classShape);

			drawer.add(creationEntry);


		}


	}

	public static void updatePaletteColorItem(String classShape, Image icon) {

		List<PaletteEntry> list = ViewGraphPalette.drawer.getChildren();

		for (PaletteEntry entry : list) {
			if(classShape.equals(entry.getLabel())) {

				ImageDescriptor descriptor = ImageDescriptor.createFromImage(icon);
				entry.setSmallIcon(descriptor);
				break;
			}

		}
	}


	public static void updatePaletteColor() {

		IEclipseContext context = EclipseContextFactory.getServiceContext(Platform.getBundle("org.epos.metadata.editor").getBundleContext());
		Project dataProject = context.get(Project.class);
		Map<Class, Image> map = dataProject.getShapesList();

		List<PaletteEntry> list = ViewGraphPalette.drawer.getChildren();

		for (Map.Entry<Class, Image> classcategory : map.entrySet()) {
			Class classShape = classcategory.getKey();
			Image icon = classcategory.getValue();

			for (PaletteEntry entry : list) {
				if(classShape.getSimpleName().equals(entry.getLabel())) {

					ImageDescriptor descriptor = ImageDescriptor.createFromImage(icon);
					entry.setSmallIcon(descriptor);
					break;
				}

			}


		}	

	}


	public static PaletteDrawer getDrawer() {
		return drawer;
	}

}

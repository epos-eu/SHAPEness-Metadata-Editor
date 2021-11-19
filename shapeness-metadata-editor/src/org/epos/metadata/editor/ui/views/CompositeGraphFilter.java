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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.nebula.widgets.chips.Chips;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.utils.Icons;
import org.epos.metadata.editor.ui.utils.Util;

public class CompositeGraphFilter {
	
	private static Section filterSection;
	private static Composite filterArea;
	private static ArrayList<ViewerFilter> arrayFilters = null;
	private static ArrayList<Class> chipFilters = new ArrayList<Class>();
	private static Composite parent;

	public CompositeGraphFilter(Composite composite) {
		
		this.parent = composite;
		
		
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());

		// Creating the Screen
		filterSection = toolkit.createSection(parent, Section.DESCRIPTION|Section.TWISTIE|Section.TITLE_BAR|Section.EXPANDED);
		filterSection.setText("Filters on the Graph");
		filterSection.setTitleBarForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		filterSection.setTitleBarBackground(parent.getBackground());
		filterSection.setTitleBarBorderColor(parent.getBackground());
		filterSection.setBackground(parent.getBackground());
		filterSection.setLayout(new RowLayout(SWT.HORIZONTAL));
		filterSection.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		filterSection.setExpanded(false);

		//section.setDescription("This demonstrates the usage of section");

		// Composite for filtering nodes
		filterArea = toolkit.createComposite(filterSection, SWT.NONE);
		filterArea.setBackground(filterSection.getBackground());
		filterArea.setLayout(new RowLayout(SWT.HORIZONTAL));
		filterArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		filterSection.setClient(filterArea);


		createFilterToolbar(filterSection);


	}
	
	private void createFilterToolbar(Section section) {

		Action selectAllFilters = new Action("Visualise all nodes", IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {

				Control[] children = filterArea.getChildren();
				for (int i = 0; i < children.length; i++) {
					Chips child = (Chips) children[i];
					if(! child.isDisposed() && !child.getSelection()){
						child.setSelection(true);
						Class classShape = (Class) child.getData();
						ViewerFilter filter = DataManager.getIstance().getFilters().get(classShape);

						arrayFilters.remove(filter);

					}

				}
				ViewGraph.getViewer().setFilters(arrayFilters.toArray( new ViewerFilter[ arrayFilters.size() ] ));
				ViewGraph.getViewer().applyLayout();
				parent.layout(true, true);

			}
		};

		selectAllFilters.setImageDescriptor(Icons.SELECT_ALL_FILTERS.descriptor());
		selectAllFilters.setEnabled(true);
		selectAllFilters.setToolTipText("Visualise all nodes");

		Action deselectAllFilters = new Action("Hide all nodes", IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {

				Control[] children = filterArea.getChildren();
				for (int i = 0; i < children.length; i++) {
					Chips child = (Chips) children[i];
					if(! child.isDisposed() && child.getSelection()){
						child.setSelection(false);
						Class classShape = (Class) child.getData();
						ViewerFilter filter = DataManager.getIstance().getFilters().get(classShape);

						arrayFilters.add(filter);


					}

				}
				ViewGraph.getViewer().setFilters(arrayFilters.toArray( new ViewerFilter[ arrayFilters.size() ] ));

				parent.layout(true, true);

			}
		};

		deselectAllFilters.setImageDescriptor(Icons.DESELECT_ALL_FILTERS.descriptor());
		deselectAllFilters.setEnabled(true);
		deselectAllFilters.setToolTipText("Hide all nodes");

		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(section);

		toolBarManager.add(selectAllFilters);
		toolBarManager.add(deselectAllFilters);

		toolBarManager.update(true);
		section.setTextClient(toolbar);

	}
	
	public static void updateColorFilters() {
		Control[] children = filterArea.getChildren();
		for (int i = 0; i < children.length; i++) {
			Chips child = (Chips) children[i];
			Class classShape = (Class) child.getData();
			if(! child.isDisposed()){
				child.setChipsBackground(Util.getColorShapeByClassName(classShape));
				child.setBorderColor(Util.getColorShapeByClassName(classShape));
				child.setPushedStateBackground(Util.getColorShapeByClassName(classShape));
				child.redraw();
			}

		}
		parent.layout(true, true);
	}

	public static void updateFilterItems() {


		if(ViewGraph.getViewer().getFilters().length==0) {
			arrayFilters = new ArrayList<ViewerFilter>();
		} else {
			arrayFilters = new ArrayList<ViewerFilter>(Arrays.asList(ViewGraph.getViewer().getFilters()));

		}

		ArrayList<Node> nodes = DataManager.getIstance().getNodes();
		if(!nodes.isEmpty()) {

			for (Node node : nodes) {

				Class classShape = Util.searchClassByNode(node);
				if(!chipFilters.contains(classShape)) {

					Chips chip = new Chips(filterArea, SWT.CHECK);

					chip.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
					chip.setPushedStateForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
					//chip.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
					chip.setBackground(filterArea.getBackground());
					chip.setChipsBackground(Util.getColorShapeByClassName(classShape));
					chip.setBorderColor(Util.getColorShapeByClassName(classShape));
					chip.setPushedStateBackground(Util.getColorShapeByClassName(classShape));

					chip.setData(classShape);


					//chip.setImage(Icons.GRAPH_LAYOUT.image());
					//chip.setPushImage(Icons.GRAPH_LAYOUT.image());
					chip.setText(node.getNodeName() + " (" +
							DataManager.getIstance().getNodesByType(classShape.getName()).size() +")");
					chip.setSelection(true);
					chipFilters.add(classShape);

					ViewerFilter filter = DataManager.getIstance().getFilters().get(classShape);

					final Listener listener = event -> {
						final Chips chips = (Chips) event.widget;
						

						if(!chips.getSelection()) {
							////System.out.println("added filter " + filter + "  on " + chips.getText());
							arrayFilters.add(filter);
							ViewGraph.getViewer().setFilters(arrayFilters.toArray( new ViewerFilter[ arrayFilters.size() ] ));
							//viewer.applyLayout();

						} else {
							////System.out.println("remove filter  " + filter + "  on " + chips.getText());
							arrayFilters.remove(filter);
							ViewGraph.getViewer().setFilters(arrayFilters.toArray( new ViewerFilter[ arrayFilters.size() ] ));
							ViewGraph.getViewer().applyLayout();

						}
					};

					chip.addListener(SWT.Selection, listener);


				} else {
					// if chip already exist update only the number of nodes per shape
					Chips chip = searchChipsByFilter(classShape);
					chip.setText(node.getNodeName() + " (" +
							DataManager.getIstance().getNodesByType(classShape.getName()).size() +")");

				}


			}

			ViewGraph.getViewer().setFilters(arrayFilters.toArray( new ViewerFilter[ arrayFilters.size() ] ));
			parent.layout(true, true);
			//body.layout(true, true);
		} 


	}
	
	public static void enableChipsByFilter(Class filter) {
		Chips chip = searchChipsByFilter(filter);
		if(chip != null) {
			chip.setSelection(true);
			ViewerFilter viewerFilter = DataManager.getIstance().getFilters().get(filter);
			arrayFilters.remove(viewerFilter);
			ViewGraph.getViewer().setFilters(arrayFilters.toArray( new ViewerFilter[ arrayFilters.size() ] ));
			filterSection.setExpanded(true);
		}
	}

	public static void disableChipsByFilter(Class filter) {
		Chips chip = searchChipsByFilter(filter);
		if(chip != null) {
			chip.setSelection(false);
			ViewerFilter viewerFilter = DataManager.getIstance().getFilters().get(filter);
			arrayFilters.add(viewerFilter);
			ViewGraph.getViewer().setFilters(arrayFilters.toArray( new ViewerFilter[ arrayFilters.size() ] ));
		}
	}

	public static Chips searchChipsByFilter(Class filter) {
		Control[] children = filterArea.getChildren();
		for (int i = 0; i < children.length; i++) {
			Chips child = (Chips) children[i];
			Class classChips = (Class) child.getData();
			if(classChips == filter) {
				if(! child.isDisposed()){
					return child;

				}
			}

		}
		return null;

	}
	
	public static void clearAllFilterChips() {
		if(filterArea == null) return;
		Control[] children = filterArea.getChildren();
		for (int i = 0; i < children.length; i++) {
			Control child = children[i];
			if(! child.isDisposed()){
				child.dispose();
			}
			chipFilters.clear();
		}
		parent.layout(true, true);

	}



	public static void removeFilterChips(Class filter) {
		Control[] children = filterArea.getChildren();
		for (int i = 0; i < children.length; i++) {
			Chips child = (Chips) children[i];
			Class classChips = (Class) child.getData();
			if(classChips == filter) {
				if(! child.isDisposed()){
					child.dispose();
					chipFilters.remove(filter);
					if(!DataManager.getIstance().getNodesByType(filter.getName()).isEmpty()) {

						child.setText(filter.getPackageName() + " (" +
								DataManager.getIstance().getNodesByType(filter.getName()).size() +")");

					}
					parent.layout(true, true);
					return;
				}
			}

		}

	}




}

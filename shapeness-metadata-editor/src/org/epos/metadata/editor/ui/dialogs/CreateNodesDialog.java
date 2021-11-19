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
package org.epos.metadata.editor.ui.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.Connection;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.model.Project;
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.views.ViewForm;

public class CreateNodesDialog extends TitleAreaDialog {


	private Tree tree;

	private Map<Class, Image> shapeList = new HashMap<Class, Image>();
	private List<Class> shapesSelected = new ArrayList<Class>();

	public CreateNodesDialog(Shell parentShell, Map<Class, Image> shapesList) {
		super(parentShell);
		this.shapeList.putAll(shapesList);

	}


	@Override
	public void create() {
		super.create();
		setTitle("Create new nodes");
		setMessage("Select type(s) of nodes you want to create."); 
		getButton(IDialogConstants.OK_ID).setEnabled(false);

	}

	@Override
	protected Control createDialogArea(Composite parent) {

		//Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 10;
		gridLayout.marginRight = 10;
		gridLayout.marginLeft = 10;
		parent.setLayout(gridLayout);

		createTree(parent);

		return parent;
	}



	private void createTree(Composite container) {


		GridData gridData = new GridData();
		gridData.widthHint = 200;
		gridData.heightHint = 550;
		//gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		tree = new Tree(container, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		tree.setLayoutData(gridData);

		TreeItem root = new TreeItem(tree, SWT.NULL);
		root.setText("Shapes");


		ArrayList<Class> sortedKeysList = new ArrayList<>(shapeList.keySet());
		Collections.sort(sortedKeysList, Comparator.comparing(Class::getSimpleName));

		for (int i = 0; i < sortedKeysList.size(); i++) {

			Class classShape = sortedKeysList.get(i);

			TreeItem item = new TreeItem(root, SWT.NULL);
			item.setText(classShape.getSimpleName());
			item.setImage(shapeList.get(classShape));
			item.setData(classShape);

		}
		tree.getItem(0).setExpanded(true);

		tree.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {

				TreeItem item = (TreeItem)event.item;
				////System.out.println("Selected: " + item.getText());
				if (item.getChecked()){
					if(item.getText().equals("Shapes")) {
						//System.out.println(event.item + " root was checked.");
						getButton(IDialogConstants.OK_ID).setEnabled(true);
						// check all children
						checkAllItems();
					} else {
						//System.out.println(event.item + " item was checked");
						getButton(IDialogConstants.OK_ID).setEnabled(true);
						shapesSelected.add((Class) item.getData());
					}
					
					
				} else{
					if(item.getText().equals("Shapes")) {
						//System.out.println(event.item + " root was unchecked.");
						getButton(IDialogConstants.OK_ID).setEnabled(false);
						// uncheck all children
						uncheckAllItems();
						
					} else {
						//System.out.println(event.item + " item was unchecked");
						// check if there is some item checked
						numberOfChecked();
						shapesSelected.remove((Class) item.getData());
					}
				}
				
			}



		});


	}



	protected void uncheckAllItems() {
		TreeItem root = tree.getItem(0);
		TreeItem[] items = root.getItems();
		for (int j = 0; j < items.length; j++) {
			items[j].setChecked(false);
			shapesSelected.remove((Class) items[j].getData());
		}
		
	}


	protected void checkAllItems() {
		TreeItem root = tree.getItem(0);
		TreeItem[] items = root.getItems();
		for (int j = 0; j < items.length; j++) {
			items[j].setChecked(true);
			shapesSelected.add((Class) items[j].getData());
		}
		
	}


	protected void numberOfChecked() {
		
		// if there is at least one shape checked then button ok is enabled
		TreeItem root = tree.getItem(0);
		TreeItem[] items = root.getItems();
		for (int j = 0; j < items.length; j++) {
			if(items[j].getChecked()) {
				getButton(IDialogConstants.OK_ID).setEnabled(true);
				return;
			}
		}
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		return;
		
	}


	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	public void setErrorMessage(String newErrorMessage) {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null) 
			okButton.setEnabled(newErrorMessage == null);
		super.setErrorMessage(newErrorMessage);
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

	@Override
	protected void okPressed() {

		super.okPressed();
	}


	public List<Class> getShapesSelected() {
		return shapesSelected;
	}


}
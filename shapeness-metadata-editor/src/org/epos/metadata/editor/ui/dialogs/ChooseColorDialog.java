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

public class ChooseColorDialog extends TitleAreaDialog {
   
	private String messageDialog = "";
	private Color color;
	//private Label colorLabel;
	private Tree tree;
	
	private Button buttonColor;
	private Map<Class, Image> shapeList = new HashMap<Class, Image>();

	public ChooseColorDialog(Shell parentShell, Map<Class, Image> shapesList) {
		super(parentShell);
		this.shapeList.putAll(shapesList);
		
	}


	@Override
	public void create() {
		super.create();
		setTitle("Color preferences");
		setMessage(messageDialog); 

	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginLeft = 5;
		container.setLayout(gridLayout);

		RGB rgb = createTree(container);
	    color = new Color(container.getDisplay(), rgb);
	    
	    buttonColor = new Button(container, SWT.PUSH);
	    buttonColor.setText("Color...");
	    buttonColor.setImage(Util.createImageIcon(rgb));
	    buttonColor.setData("item", tree.getSelection()[0]);
	    GridData gdinfo = new GridData(GridData.VERTICAL_ALIGN_BEGINNING); //GridData.HORIZONTAL_ALIGN_FILL
	    buttonColor.setLayoutData(gdinfo);
	    
	    buttonColor.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	    	  ColorDialog dlg = new ColorDialog(container.getShell());
	    	  
	    	  
	    	  int pixelValue = buttonColor.getImage().getImageData().getPixel(5,5);
	    	  PaletteData palette = buttonColor.getImage().getImageData().palette; 
	    	  dlg.setRGB(palette.getRGB(pixelValue));

	          // Change the title bar text
	          dlg.setText("Choose a Color");

	          // Open the dialog and retrieve the selected color
	          RGB rgb = dlg.open();
	          if (rgb != null) {
	            // Dispose the old color, create the
	            // new one, and set into the label
	            //color.dispose();
	            //color = new Color(container.getDisplay(), rgb);
	            buttonColor.setImage(Util.createImageIcon(rgb));
	            
	            // change color of treeItem and shapeList
	            
	            TreeItem item = (TreeItem) buttonColor.getData("item");
	            Image icon = Util.createImageIcon(rgb);
	            item.setImage(icon);
	            
	            shapeList.put((Class) item.getData(), icon);
	          }
	      }
	    });
	    
	    
	 
		return container;
	}
	
	
	
	private RGB createTree(Composite container) {

		RGB rgb = null;
		
		GridData gridData = new GridData();
		gridData.widthHint = 300;
		gridData.heightHint = 550;
		//gridData.horizontalSpan = 4;
		//gridData.grabExcessHorizontalSpace = true;
		//gridData.horizontalAlignment = GridData.FILL;

		tree = new Tree(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
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
			if(i == 0 ) {
				tree.setSelection(item);
				Image icon = shapeList.get(classShape);
				int pixelValue = icon.getImageData().getPixel(5,5);

				PaletteData palette = icon.getImageData().palette; 
				rgb = palette.getRGB(pixelValue);
			}
			
		}
		tree.getItem(0).setExpanded(true);
		
		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				/*
				 * show color item
				 */
				TreeItem item = (TreeItem)event.item;
			    Image icon = item.getImage();
			    int pixelValue = icon.getImageData().getPixel(5,5);

				PaletteData palette = icon.getImageData().palette; 
				RGB rgb = palette.getRGB(pixelValue);
				//color = new Color(container.getDisplay(), rgb);
				buttonColor.setImage(Util.createImageIcon(rgb));
				buttonColor.setData("item", item);
	            
	            
			}
		});
		
		return rgb;


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


	public Map<Class, Image> getShapeList() {
		return shapeList;
	}


}
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.Connection;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.utils.Icons;
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.views.ViewForm;

public class ChooseNodeTypeDialog extends TitleAreaDialog {

	private List<String> list;    
	private String messageDialog = "";
	private String classChoosed = "";
	private String fieldName;
	private String value;
	private String IDvalue;
	
	

	public ChooseNodeTypeDialog(Shell parentShell, List<String> list, String fieldName) {
		super(parentShell);
		this.list = list;
		this.fieldName = fieldName.replace("_", ":");

	}


	@Override
	public void create() {
		super.create();
		setTitle("Select a shape or value for " + fieldName);
		setTitleImage(Icons.CLASS.image());
		setMessage(messageDialog); 
		getButton(IDialogConstants.OK_ID).setEnabled(false);

	}


	@Override
	protected Control createDialogArea(Composite parent) {

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(5, false);
		container.setLayout(layout);

		SelectionListener selectionListener = new SelectionAdapter () {
			public void widgetSelected(SelectionEvent event) {
				Button button = ((Button) event.widget);
				if(button.getSelection()) {
					classChoosed = (String) button.getData("className");
					if(Util.isPrimitiveClass((String) button.getData("className"))) {
						Text text = (Text) button.getData();
						text.setEnabled(true);
						value = text.getText();
						if(value.isEmpty() || value.isBlank()) {
							getButton(IDialogConstants.OK_ID).setEnabled(false);
						} else {
							getButton(IDialogConstants.OK_ID).setEnabled(true);
						}
					} else {
						
							IDvalue = Engine.getIstance().getMs().getRootOntology() + classChoosed.substring(classChoosed.lastIndexOf(".")+1, classChoosed.length()) + "/" + UUID.randomUUID();
							getButton(IDialogConstants.OK_ID).setEnabled(true);
						
						
					}
				} else {
					classChoosed = "";
					if(Util.isPrimitiveClass((String) button.getData("className"))) {
						Text text = (Text) button.getData();
						text.setEnabled(false);
						value = text.getText();
						
					}
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}

			};
		};

		Listener textListener = new Listener(){
			@Override
			public void handleEvent(Event event){

				Text text = ((Text) event.widget);
				if(text.getText().isEmpty() || text.getText().isBlank()) {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				} else {
					value = text.getText();
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}

			}
		};


		for(int i=0; i < this.list.size(); i++) {
			String className = list.get(i);
			Button button = new Button(container, SWT.RADIO);
			button.setText(className.substring(className.lastIndexOf(".")+1, className.length()));
			button.setData("className", className);

			button.addSelectionListener(selectionListener);

			try {
				Class<?> instance = ClassUtils.getClass(className);
				if(Util.isPrimitiveClass(instance.getTypeName())){
					Text text = new Text(container,SWT.NONE);
					GridData gd = new GridData();
					gd.grabExcessHorizontalSpace = true;
					gd.horizontalAlignment = GridData.FILL;
					gd.horizontalSpan = 4;
					text.setLayoutData(gd);
					text.setText("");
					text.setEnabled(false);
					text.addListener(SWT.TRAVERSE_RETURN, textListener);
					text.addListener(SWT.FocusOut, textListener);
					text.addListener(SWT.Modify, textListener);
					button.setData(text);
				} else {
					GridData gdButton = new GridData();
					gdButton.horizontalSpan = 5;
					button.setLayoutData(gdButton);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return area;
	}


	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	public void setErrorMessage(String newErrorMessage) {
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


	public String getClassChoosed() {
		return classChoosed;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getValue() {
		return value;
	}


	public String getIDvalue() {
		return IDvalue;
	}


}
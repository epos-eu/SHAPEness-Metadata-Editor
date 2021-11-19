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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.graphics.Image;
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
import org.epos.metadata.editor.ui.model.Project;
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.views.ViewForm;


public class CreateNewNodeDialog extends TitleAreaDialog {

	private String messageDialog = "";
	private String className;
	private String IDvalue;
	Map<String, String> mapIDType = new HashMap<String, String>();


	public CreateNewNodeDialog(Shell parentShell, String className) {
		super(parentShell);
		this.className = className.substring(className.lastIndexOf(".")+1, className.length());
		messageDialog = "Insert a unique ID for the node";
	}


	@Override
	public void create() {
		super.create();
		setTitle("Create a new " + className + " node");
		setMessage(messageDialog); 
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		setTitleImage(Util.getImageShapeByClassName(className));

	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(3, false);
		container.setLayout(layout);

		Label label = new Label(container, SWT.NONE);
		label.setText("ID Type");

		String schemaID = Engine.getIstance().getMs().getRootOntology();
		Combo combo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		String[] items = new String[] { "ORCID (Person)", "SCOPUS-ID (Person)", "DOI (Resource)", "PIC (Organization)", "Custom" }; 
		mapIDType.put("ORCID (Person)", "https://orcid.org/");
		mapIDType.put("SCOPUS-ID (Person)", "https://www.scopus.com/authid/detail.uri?authorId=");
		mapIDType.put("DOI (Resource)", "https://doi.org/");
		mapIDType.put("PIC (Organization)", "PIC:");
		mapIDType.put("Custom", "");

		combo.setItems(items);
		combo.setText("");
		GridData gdc = new GridData();
		//gdc.grabExcessHorizontalSpace = true;
		//gdc.grabExcessVerticalSpace = true;
		gdc.horizontalAlignment = GridData.FILL;
		gdc.horizontalSpan = 2;
		combo.setLayoutData(gdc);
		
		Label labelValue = new Label(container, SWT.NONE);
		labelValue.setText("ID Value");

		Text prefix = new Text(container, SWT.READ_ONLY);
		prefix.setText("");
		//prefix.setEnabled(false);
		prefix.setBackground(new Color(Display.getDefault(), new RGB(225,232,248)));
		GridData gdp = new GridData();
		gdp.grabExcessHorizontalSpace = true;
		gdp.grabExcessVerticalSpace = true;
		gdp.horizontalAlignment = GridData.FILL;
		prefix.setLayoutData(gdp);

		Text text = new Text(container, SWT.None);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		text.setLayoutData(gd);

		Listener textListener = new Listener(){
			@Override
			public void handleEvent(Event event){

				Text text = ((Text) event.widget);


				if(!text.getText().isEmpty() && !StringUtils.containsWhitespace(text.getText())) {
					getButton(IDialogConstants.OK_ID).setEnabled(true);

					IDvalue = prefix.getText() + text.getText();


				} else {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}

			}
		};
		text.addListener(SWT.TRAVERSE_RETURN, textListener);
		text.addListener(SWT.FocusOut, textListener);
		text.addListener(SWT.Modify, textListener);

		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int idx = combo.getSelectionIndex();

				if(combo.getText().isEmpty() || combo.getText().isBlank()) {
					prefix.setText("");
				} else {
					prefix.setText(mapIDType.get(combo.getItem(idx)));
				}



				if(!text.getText().isEmpty()) {
					getButton(IDialogConstants.OK_ID).setEnabled(true);
					IDvalue = prefix.getText() + text.getText();
				} else {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
			}

		});

		combo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {

					if(combo.getText().isEmpty() || combo.getText().isBlank()) {
						prefix.setText("");
					} else {
						prefix.setText(mapIDType.get(combo.getText()));
					}

					if(!text.getText().isEmpty()) {
						getButton(IDialogConstants.OK_ID).setEnabled(true);
						IDvalue = prefix.getText()+ text.getText();

					} else {
						getButton(IDialogConstants.OK_ID).setEnabled(false);
					}
				}
			}
		});

		combo.addModifyListener(new ModifyListener( ) {
			public void modifyText(ModifyEvent e) {
				if(combo.getText().isEmpty() || combo.getText().isBlank()) {
					prefix.setText("");
				} else {
					prefix.setText(mapIDType.get(combo.getText()));
				}

				if(!text.getText().isEmpty()) {
					getButton(IDialogConstants.OK_ID).setEnabled(true);

					IDvalue = prefix.getText() + text.getText();

				} else {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
			}
		});

		Label labelQuestion = new Label(container, SWT.NONE);
		labelQuestion.setText("Do you want to create a new node?");
		GridData gdlabel = new GridData();
		gdlabel.grabExcessHorizontalSpace = true;
		gdlabel.grabExcessVerticalSpace = true;
		gdlabel.horizontalAlignment = GridData.FILL;
		gdlabel.horizontalSpan = 3;
		labelQuestion.setLayoutData(gdlabel);

		return area;
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


	public String getIDvalue() {
		return IDvalue;
	}

}
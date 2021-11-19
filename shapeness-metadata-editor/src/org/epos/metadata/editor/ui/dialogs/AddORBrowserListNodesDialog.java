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

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.ClassUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.Connection;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.views.ViewForm;

public class AddORBrowserListNodesDialog extends TitleAreaDialog {

	private List<Node> listOfExistingNodes;    
	private Node nodeSelected;
	
	private List<String> listOfTypes;
	private String classChoosed = "";
	private String primitiveValue;
	
	private boolean createNewClass;
	private String fieldName;
	private Button browseNode;
	private Button createNewNode;
	private Table table;
	private Group groupBrowseNode;
	private Group groupNewNode;


	public AddORBrowserListNodesDialog(Shell parentShell, List<Node> listOfExistingNodes, List<String> listOfTypes, String fieldName) {
		super(parentShell);
		this.listOfExistingNodes = listOfExistingNodes;
		this.listOfTypes = listOfTypes;
		this.fieldName = fieldName.replace("_", ":");
	}


	@Override
	public void create() {
		super.create();
		setTitle(fieldName + " represents a relationship with an other node");
		setMessage("Create the relationship with a new node or with an existing node"); 
		getButton(IDialogConstants.OK_ID).setEnabled(false);

	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite area = (Composite) super.createDialogArea(parent);
		
		Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(1, false);
        container.setLayout(layout);
		
		createNewNode = new Button(container, SWT.RADIO);
		createNewNode.setText("Create a new node");
		createNewNode.setSelection(true);
		createNewClass = true;
		
		groupNewNode = new Group(container, SWT.NONE);
		//groupNewNode.setText("RDF/Turtle metadata file");
		groupNewNode.setLayout(new GridLayout());
		groupNewNode.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		createGroupNewNode(groupNewNode);
		
		browseNode = new Button(container, SWT.RADIO);
		browseNode.setText("Select an existing node");
		browseNode.setSelection(false);
		
		if(listOfExistingNodes.isEmpty()) {
			browseNode.setEnabled(false);
		}
		
		groupBrowseNode = new Group(container, SWT.NONE);
		//groupNewNode.setText("RDF/Turtle metadata file");
		groupBrowseNode.setLayout(new GridLayout());
		groupBrowseNode.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		groupBrowseNode.setEnabled(false);
		
		createGroupBrowseNode(groupBrowseNode);
		
		SelectionAdapter listener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (createNewNode.getSelection()) {
					
					groupBrowseNode.setEnabled(false);
					browseNode.setSelection(false);
					groupNewNode.setEnabled(true);
					createNewClass = true;
					

				} else if (browseNode.getSelection()) {
					groupNewNode.setEnabled(false);
					createNewNode.setSelection(false);
					createNewClass = false;
					
					Control[] buttons = groupNewNode.getChildren();
					for (Control control : buttons) {
						if(control.getClass() == org.eclipse.swt.widgets.Button.class) {
							Button radio = (Button) control;
							if(radio.getSelection()) {
								radio.setSelection(false);
							}
						}
					}
					
					groupBrowseNode.setEnabled(true);
					

				}
				validatePage();

			}
		};
		browseNode.addSelectionListener(listener);
		createNewNode.addSelectionListener(listener);
		
		return area;
	}


	protected void validatePage() {
		if (createNewNode.getSelection()) {
			Control[] buttons = groupNewNode.getChildren();
			for (Control control : buttons) {
				if(control.getClass() == org.eclipse.swt.widgets.Button.class) {
					Button radio = (Button) control;
					if(radio.getSelection()) {
						getButton(IDialogConstants.OK_ID).setEnabled(true);
						return;
					}
				}
			}
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			

		} else if (browseNode.getSelection()) {
			
			if(table.getSelectionCount() > 0) {
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			} else {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			}
		}
		
	}


	private void createGroupBrowseNode(Composite area) {
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		table = new Table(container, SWT.BORDER | SWT.V_SCROLL);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		table.setLayoutData(gd);

		for (Node node : this.listOfExistingNodes) {
			TableItem item = new TableItem(table, SWT.NULL);
			item.setText(node.getNodeName()+"-"+node.getId());
			item.setData(node);
		}

		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				TableItem selection = table.getSelection()[0];
				nodeSelected = (Node) selection.getData();
				getButton(IDialogConstants.OK_ID).setEnabled(true);

			}
		});


	}


	private void createGroupNewNode(Group group) {

		/*Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(5, false);
		container.setLayout(layout);*/
		
		group.setLayout(new GridLayout(2, false));
		
		SelectionListener selectionListener = new SelectionAdapter () {
			public void widgetSelected(SelectionEvent event) {
				Button button = ((Button) event.widget);
				if(button.getSelection()) {
					classChoosed = (String) button.getData("className");
					if(Util.isPrimitiveClass((String) button.getData("className"))) {
						Text text = (Text) button.getData();
						text.setEnabled(true);
						primitiveValue = text.getText();
						if(primitiveValue.isEmpty() || primitiveValue.isBlank()) {
							getButton(IDialogConstants.OK_ID).setEnabled(false);
						} else {
							getButton(IDialogConstants.OK_ID).setEnabled(true);
						}
					} else {

						//IDvalue = Engine.getIstance().getMs().getRootOntology() + classChoosed.substring(classChoosed.lastIndexOf(".")+1, classChoosed.length()) + "/" + UUID.randomUUID();
						getButton(IDialogConstants.OK_ID).setEnabled(true);


					}
				} else {
					classChoosed = "";
					if(Util.isPrimitiveClass((String) button.getData("className"))) {
						Text text = (Text) button.getData();
						text.setEnabled(false);
						primitiveValue = text.getText();

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
					primitiveValue = text.getText();
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}

			}
		};


		for(int i=0; i < this.listOfTypes.size(); i++) {
			String className = listOfTypes.get(i);
			Button button = new Button(group, SWT.RADIO);
			button.setText(className.substring(className.lastIndexOf(".")+1, className.length()));
			button.setData("className", className);

			button.addSelectionListener(selectionListener);

			try {
				Class<?> instance = ClassUtils.getClass(className);
				if(Util.isPrimitiveClass(instance.getTypeName())){
					Text text = new Text(group,SWT.NONE);
					GridData gd = new GridData();
					gd.grabExcessHorizontalSpace = true;
					gd.horizontalAlignment = GridData.FILL;
					//gd.horizontalSpan = 2;
					text.setLayoutData(gd);
					text.setText("");
					text.setEnabled(false);
					text.addListener(SWT.TRAVERSE_RETURN, textListener);
					text.addListener(SWT.FocusOut, textListener);
					text.addListener(SWT.Modify, textListener);
					button.setData(text);
				} else {
					GridData gdButton = new GridData();
					gdButton.horizontalSpan = 2;
					button.setLayoutData(gdButton);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


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


	public Node getNodeSelected() {
		return nodeSelected;
	}


	public boolean isCreateNewClass() {
		return createNewClass;
	}


	public String getPrimitiveValue() {
		return primitiveValue;
	}


	public String getClassChoosed() {
		return classChoosed;
	}


}
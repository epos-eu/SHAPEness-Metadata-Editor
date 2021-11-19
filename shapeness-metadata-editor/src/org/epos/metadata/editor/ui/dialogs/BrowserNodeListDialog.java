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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.Connection;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.views.ViewForm;

public class BrowserNodeListDialog extends TitleAreaDialog {

	private List<Node> list;    
	private String messageDialog = "";
	private Node nodeSelected;
	

	public BrowserNodeListDialog(Shell parentShell, List<Node> list, TableViewer tableField, Node node, String fieldName) {
		super(parentShell);
		this.list = list;
	}


	@Override
	public void create() {
		super.create();
		setTitle("Select the node to connect to");
		setMessage(messageDialog); 
		getButton(IDialogConstants.OK_ID).setEnabled(false);

	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(1, false);
        container.setLayout(layout);

		Table table = new Table(container, SWT.BORDER | SWT.V_SCROLL);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		table.setLayoutData(gd);

		for (Node node : this.list) {
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


	public Node getNodeSelected() {
		return nodeSelected;
	}


}
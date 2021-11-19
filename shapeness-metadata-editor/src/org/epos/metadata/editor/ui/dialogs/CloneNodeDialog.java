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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.ListenerList;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
import org.epos.metadata.editor.ui.utils.Util;
import org.epos.metadata.editor.ui.views.ViewForm;


public class CloneNodeDialog extends TitleAreaDialog {
    
	
	private Node node;

	private Button duplicateProperties;

	private Button duplicatePropertiesConnections;
	
	private boolean duplicateOnlyProperties;


	public CloneNodeDialog(Shell parentShell, Node node) {
		super(parentShell);
		this.node = node;
		
	}


	@Override
	public void create() {
		super.create();
		setTitle("Duplicate Node" );
		setMessage("Do you really want to duplicate node: " + node.getNodeName()+"-"+node.getId() + "?"); 
		getButton(IDialogConstants.OK_ID).setEnabled(true);

	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite area = (Composite) super.createDialogArea(parent);
		
		Composite container = new Composite(area, SWT.NONE);
		
		GridLayout layout = new GridLayout(1, false);
		//layout.marginWidth = 20;
		//layout.marginHeight = 20;
		//layout.marginRight = 20;
		//layout.marginLeft = 20;
		container.setLayout(layout);
		GridData gdata = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdata.horizontalIndent = 10;
		container.setLayoutData(gdata);
		
		duplicateProperties = new Button(parent, SWT.RADIO);
		duplicateProperties.setText("Duplicate node with its properties");
		duplicateProperties.setSelection(true);
		duplicateOnlyProperties = true;
		duplicateProperties.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent event) {
				Button button = ((Button) event.widget);
				if(button.getSelection()) {
					duplicateOnlyProperties = true;
				} else {
					duplicateOnlyProperties = false;
				}
			}
		});
		
		duplicatePropertiesConnections = new Button(parent, SWT.RADIO);
		duplicatePropertiesConnections.setText("Duplicate node with its properties and relationships");
		duplicatePropertiesConnections.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent event) {
				Button button = ((Button) event.widget);
				if(button.getSelection()) {
					duplicateOnlyProperties = false;
				} else {
					duplicateOnlyProperties = true;
				}
			}
		});
		return area;
	}


	@Override
	protected boolean isResizable() {
		return true;
	}


	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

	@Override
	protected void okPressed() {

		super.okPressed();

	}


	public Button getDuplicateProperties() {
		return duplicateProperties;
	}


	public boolean isDuplicateOnlyProperties() {
		return duplicateOnlyProperties;
	}
	

}
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
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ImportFromDOIDialog extends TitleAreaDialog {

	private String messageDialog = "";
	private String DOI;
	Map<String, String> mapIDType = new HashMap<String, String>();

	public ImportFromDOIDialog(Shell parentShell) {
		super(parentShell);
		messageDialog = "DataCite infrastructure is responsible for issuing persistent identifiers (in particular, DOIs) for Datasets."
				+ " It is possibile to retrieve metadata from DataCite and pre-compile fields.";
	}


	@Override
	public void create() {
		super.create();
		setTitle("Import metadata from DataCite");
		setMessage(messageDialog); 
		getButton(IDialogConstants.OK_ID).setEnabled(false);

	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(3, false);
		container.setLayout(layout);

		
		Label labelValue = new Label(container, SWT.NONE);
		labelValue.setText("Datacite DOI");

		Text prefix = new Text(container, SWT.READ_ONLY);
		prefix.setText("https://doi.org/");
		//prefix.setText("https://api.datacite.org/dois/application/vnd.datacite.datacite+xml/");
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
		text.setMessage("10.6092/INGV.IT-AHEAD");
		

		Listener textListener = new Listener(){
			@Override
			public void handleEvent(Event event){

				Text text = ((Text) event.widget);


				if(!text.getText().isEmpty() && !StringUtils.containsWhitespace(text.getText())) {
					getButton(IDialogConstants.OK_ID).setEnabled(true);

					DOI = text.getText();


				} else {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}

			}
		};
		text.addListener(SWT.TRAVERSE_RETURN, textListener);
		text.addListener(SWT.FocusOut, textListener);
		text.addListener(SWT.Modify, textListener);


		Label labelQuestion = new Label(container, SWT.NONE);
		labelQuestion.setText("Do you want to import metadata from the Datacite?");
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
		return DOI;
	}

}
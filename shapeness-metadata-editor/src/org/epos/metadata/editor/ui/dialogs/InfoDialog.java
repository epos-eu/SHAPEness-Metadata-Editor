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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.epos.metadata.editor.ui.utils.Icons;

public class InfoDialog extends TitleAreaDialog {

    private String infoTerm;
	private String term;

    public InfoDialog(Shell parentShell, String term, String infoTerm) {
        super(parentShell);
        this.term = term;
        this.infoTerm = infoTerm;
    }
    
    @Override
	public void create() {
		super.create();
		setTitle(this.term);
		setTitleImage(Icons.TERM_DEFINITION.image());

	}


    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        GridLayout layout = new GridLayout(1, false);
        composite.setLayout(layout);

        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.widthHint = 200;
        data.heightHint = 200;
        composite.setLayoutData(data);


        Browser browser = new Browser(composite, SWT.NONE);
        browser.setText(infoTerm);
        browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
    	createButton(parent, super.OK, "OK", true);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Term Definition");
        //shell.setMessage(term); 
    }

    @Override
    public void okPressed() {
        close();
    }

    
}

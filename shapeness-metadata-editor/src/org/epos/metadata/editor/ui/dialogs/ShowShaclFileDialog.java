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

import java.io.File;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ShowShaclFileDialog extends Dialog {

	private String shaclFile;

	public ShowShaclFileDialog(Shell parentShell, String shaclFile) {
		super(parentShell);
		this.shaclFile = shaclFile;
	}
	
	@Override
	protected boolean canHandleShellCloseEvent() {
	    return false;
	}
	
	@Override
    protected Button createButton(Composite parent, int id,
            String label, boolean defaultButton) {
        if (id == IDialogConstants.CANCEL_ID)
            return null;
        return super.createButton(parent, id, label, defaultButton);
    }
	
	@Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(this.shaclFile);
    }
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(1, false));
		File shacl = new File(shaclFile);
		//must be set to avoid exception: "[Could not detect registered XULRunner to use]"
		System.setProperty("org.eclipse.swt.browser.XULRunnerPath", shacl.getAbsolutePath());
		Browser browser = new Browser(composite,SWT.MOZILLA);
		//browser.setUrl("file:///" + shaclFile);
		browser.setUrl(shaclFile);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return composite;
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
    protected Point getInitialSize() {
        return new Point(800, 500);
    }
	
	

}

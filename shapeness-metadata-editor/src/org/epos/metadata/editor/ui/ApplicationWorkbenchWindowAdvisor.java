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
package org.epos.metadata.editor.ui;

import java.time.LocalDate;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setShowCoolBar(true);
		configurer.setShowMenuBar(true);
		configurer.setShowStatusLine(true);
		configurer.setTitle("SHAPEness Metadata Editor");
		configurer.setShowProgressIndicator(true);



		// SET the expiration date for users test phase
		/*
		if(!validateTimeStamp()) {
			MessageDialog.openWarning(Display.getDefault().getActiveShell(), 
					"Trial Period", "Your trial period has been expired!\n"
							+ "\nThank you for your contribution.");

			PlatformUI.getWorkbench().close();
		} else {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), 
					"Trial Period", "Your trial will expire on June 12th, 2020.\n");

		}*/


	}

	private boolean validateTimeStamp() {

		LocalDate testDate = LocalDate.parse("2020-06-12");
		LocalDate today = LocalDate.now();
		if(today.isAfter(testDate)) {
			return false;
		}
		return true;

	}

	@Override
	public void postWindowCreate() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		IWorkbenchWindow window = configurer.getWindow();
		window.getShell().setMaximized(true);

		// HIDE UNNEEDED ACTIONS
		IWorkbenchWindow workbenchWindow = configurer.getWindow();
		workbenchWindow.getActivePage().hideActionSet("org.eclipse.search.searchActionSet");
		workbenchWindow.getActivePage().hideActionSet("org.eclipse.ui.edit.text.actionSet.annotationNavigation");
		workbenchWindow.getActivePage().hideActionSet("org.eclipse.ui.actionSet.openFiles");
		workbenchWindow.getActivePage().hideActionSet("org.eclipse.ui.edit.text.actionSet.navigation");

		workbenchWindow.getActivePage().hideActionSet("org.eclipse.debug.ui.breakpointActionSet");
		workbenchWindow.getActivePage().hideActionSet("org.eclipse.team.ui.actionSet");
		workbenchWindow.getActivePage().hideActionSet("org.eclipse.ui.NavigateActionSet");
		workbenchWindow.getActivePage().hideActionSet("org.eclipse.debug.ui.debugActionSet");
		workbenchWindow.getActivePage().hideActionSet("org.eclipse.debug.ui.launchActionSet");

		// CLOSE WELCOME FULL PAGE
		PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_INTRO, false);


	}



	@Override
	public boolean preWindowShellClose() {

		boolean exit = MessageDialog.openQuestion(getWindowConfigurer().getWindow().getShell(),
				"Confirm Exit", "Do you want to exit?");

		if(exit) {

			// close editor
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorReference[] editors = activePage.getEditorReferences();
			if(editors.length > 0) {
				activePage.closeEditor(editors[0].getEditor(true), true);
				activePage.setEditorAreaVisible(false);
			}
			// close all views form 
			IViewReference[] viewReferences = activePage.getViewReferences();
			for (IViewReference ivr : viewReferences) {
				if (ivr.getId().startsWith("org.epos.metadata.editor.viewForm")) {
					activePage.hideView(ivr);
				}
			}
			// Save workspace before exit
			try {
				IWorkspace ws = ResourcesPlugin.getWorkspace();
				ws.save(true, new NullProgressMonitor());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return exit;
	}

	@Override
	public void postWindowOpen() {

		// SHOW WELCOME VIEW EVERY TIME
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().
			getActivePage().showView("org.epos.metadata.editor.introview");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}

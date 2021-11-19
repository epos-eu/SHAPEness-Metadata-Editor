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
package org.epos.metadata.editor.ui.handlers;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.epos.metadata.editor.ui.dialogs.MapDialog;
import org.epos.metadata.editor.ui.views.ViewGraphPalette;
import org.epos.metadata.editor.ui.wizard.WizardNewProject;

public class CreateNewProject implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		if(ViewGraphPalette.getDrawer() != null && ViewGraphPalette.getDrawer().getChildren().size() > 0) {
			
			boolean result = MessageDialog.openQuestion(HandlerUtil.getActiveShell(event), "Question", 
					"Do you really want to create a new project overriding the existing one?");
			
			if(result) {
				WizardDialog wizard = new WizardDialog(HandlerUtil.getActiveShell(event), new WizardNewProject());	
				wizard.setMinimumPageSize(300, 600);
				wizard.open();
							
			} else {
				return null;
			}
		} else {
			WizardDialog wizard = new WizardDialog(HandlerUtil.getActiveShell(event), new WizardNewProject());	
			wizard.setMinimumPageSize(300, 600);
			wizard.open();
		}
		
		return null;
		
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}

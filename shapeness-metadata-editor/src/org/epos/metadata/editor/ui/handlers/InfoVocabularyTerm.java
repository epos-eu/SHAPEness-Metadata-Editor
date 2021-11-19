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
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.commands.ParameterValuesException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.ui.dialogs.InfoDialog;

public class InfoVocabularyTerm extends AbstractHandler {

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

		Map parameters = event.getParameters();
		Object value = parameters.get("infoTerm");
		Object className = parameters.get("className");
		
		if(value != null && className != null) {
			String term = value.toString();//.replace("_", ":");
			String infoTerm = Engine.getIstance().getVocStore().searchDescriptionOrCommentByString(className.toString(), term);
			
			if(infoTerm != null) {
				////System.out.println("InfoVocabularyTerm: " + value + " >> " + infoTerm);
				//MessageDialog.openInformation(HandlerUtil.getActiveShell(event), term , infoTerm);
				new InfoDialog(HandlerUtil.getActiveShell(event), term, infoTerm).open();
			} else {
				MessageDialog.openInformation(HandlerUtil.getActiveShell(event), term , " No information about this field");
			}
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
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}

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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;
import org.epos.metadata.editor.ui.views.ViewGraph;

public class ChangeLayoutHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String currentState = event.getParameter(RadioState.PARAMETER_ID);
		if (currentState == null) {
		    return null;
		}
		
		if(HandlerUtil.matchesRadioState(event))
			return null; // we are already in the updated state - do nothing

		
		String layout = event.getParameter("org.epos.metadata.editor.commandParameterLayout");
		IViewPart findView = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().findView("org.epos.metadata.editor.viewGraph");
		ViewGraph view = (ViewGraph) findView;
		view.setLayoutManager(layout);

		// update the current state
		HandlerUtil.updateRadioState(event.getCommand(), currentState);

		return null;
	}

}

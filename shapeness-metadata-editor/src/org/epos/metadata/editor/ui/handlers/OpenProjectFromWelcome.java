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

import java.util.Properties;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.eclipse.ui.services.IServiceLocator;

public class OpenProjectFromWelcome implements IIntroAction {

	  
	  @Override
	  public void run(IIntroSite arg0, Properties arg1) {
	    
	    // close intro/welcome page
	    //final IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro(); 
	    //PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);
	    //PlatformUI.getWorkbench().getIntroManager().showIntro(null, true);

	    // Show CheatSheet
	    IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
				.getService(IHandlerService.class);
		try {
			handlerService.executeCommand("org.epos.metadata.editor.openProject", null);
		} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }


}

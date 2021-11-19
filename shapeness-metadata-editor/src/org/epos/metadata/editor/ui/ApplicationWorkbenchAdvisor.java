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

import java.net.URL;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.osgi.framework.Bundle;


public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "org.epos.metadata.editor.perspectiveEPOS";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;

	}

	@Override
	public IAdaptable getDefaultPageInput() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	public void initialize(IWorkbenchConfigurer configurer) {

		IDE.registerAdapters();
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);
		
		
		final String ICONS_PATH = "icons/full/";

		Bundle ideBundle = Platform.getBundle(IDEWorkbenchPlugin.IDE_WORKBENCH);

		declareWorkbenchImage(
				configurer, 
				ideBundle,
				IDE.SharedImages.IMG_OBJ_PROJECT, 
				ICONS_PATH + "obj16/prj_obj.png",
				true);

		declareWorkbenchImage(
				configurer, 
				ideBundle,
				IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED, 
				ICONS_PATH + "obj16/cprj_obj.png", 
				true);

		declareWorkbenchImage(
				configurer, 
				ideBundle, 
				IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW, 
				ICONS_PATH + "eview16/problems_view.png", 
				true);

		declareWorkbenchImage(
				configurer, 
				ideBundle, 
				IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW_ERROR, 
				ICONS_PATH + "eview16/problems_view_error.png", 
				true);


		declareWorkbenchImage(
				configurer, 
				ideBundle, 
				IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW_WARNING, 
				ICONS_PATH + "eview16/problems_view_warning.png", 
				true);

		declareWorkbenchImage(
				configurer, 
				ideBundle, 
				IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW_INFO, 
				ICONS_PATH + "eview16/problems_view_info.png", 
				true);

		declareWorkbenchImage(
				configurer, 
				ideBundle, 
				IDEInternalWorkbenchImages.IMG_OBJS_ERROR_PATH, 
				ICONS_PATH + "obj16/error_tsk.png", 
				true);

		declareWorkbenchImage(
				configurer, 
				ideBundle, 
				IDEInternalWorkbenchImages.IMG_OBJS_WARNING_PATH, 
				ICONS_PATH + "obj16/warn_tsk.png", 
				true);

		declareWorkbenchImage(
				configurer, 
				ideBundle, 
				IDEInternalWorkbenchImages.IMG_OBJS_INFO_PATH, 
				ICONS_PATH + "obj16/info_tsk.png", 
				true);
	
	}

	private void declareWorkbenchImage(IWorkbenchConfigurer configurer_p, Bundle ideBundle, String symbolicName, String path, boolean shared) {
		URL url = ideBundle.getEntry(path);
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		configurer_p.declareImage(symbolicName, desc, shared);
	}

}

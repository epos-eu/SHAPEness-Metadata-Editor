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

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;

public class PerspectiveFactory implements IPerspectiveFactory {


	@Override
	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		String graphArea = "org.epos.metadata.editor.viewGraph";

		// CENTER FOLDER
		IFolderLayout center = layout.createFolder("center", IPageLayout.LEFT, (float) 0.75, editorArea);
		
		center.addView(graphArea);
		IViewLayout graph = layout.getViewLayout(graphArea);
		graph.setCloseable(false);
		center.addView("org.epos.metadata.editor.introview");
		//layout.addView(graphArea, IPageLayout.LEFT, (float) 0.75, editorArea);
		//layout.addView("org.epos.metadata.editor.introview", IPageLayout.LEFT, (float) 0.75, graphArea);


		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, (float) 0.15, graphArea);
		left.addView("org.epos.metadata.editor.viewPalette");
		IViewLayout palette = layout.getViewLayout("org.epos.metadata.editor.viewPalette");
		palette.setCloseable(false);


		/*
        left.addView(IPageLayout.ID_PROJECT_EXPLORER);
		IViewLayout explorer = layout.getViewLayout(IPageLayout.ID_PROJECT_EXPLORER);
		explorer.setCloseable(false);
		 */


		// Bottom Left
		layout.addView("org.epos.metadata.editor.viewOutline", IPageLayout.BOTTOM, (float) 0.60, "org.epos.metadata.editor.viewPalette");
		IViewLayout outline = layout.getViewLayout("org.epos.metadata.editor.viewOutline");
		outline.setCloseable(false);


		// Bottom	   
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.60, graphArea);
		bottom.addPlaceholder("org.epos.metadata.editor.viewForm:*");


	}

}

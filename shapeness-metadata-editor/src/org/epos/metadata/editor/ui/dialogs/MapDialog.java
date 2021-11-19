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

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;



public class MapDialog extends TitleAreaDialog {

	private static boolean drag = false;
    private static int startX;
    private static int startY;
    private static int endX;
    private static int endY;
	
	public MapDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Map");


	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		final Canvas canvas = new Canvas(area, SWT.NONE);
        canvas.setSize(150, 150);
        canvas.setLocation(20, 20);
       

        canvas.addListener(SWT.MouseDown, new Listener() {

            @Override
            public void handleEvent(Event event) {
                startX = event.x;
                startY = event.y;

                drag = true;
            }
        });

        canvas.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                endX = event.x;
                endY = event.y;

                drag = false;

                canvas.redraw();
            }
        });

        canvas.addListener(SWT.MouseMove, new Listener() {

            @Override
            public void handleEvent(Event event) {
                if(drag)
                {
                    endX = event.x;
                    endY = event.y;

                    canvas.redraw();
                }
            }
        });

        canvas.addListener(SWT.Paint, new Listener() {

            @Override
            public void handleEvent(Event event) {
                if(drag)
                {
                    GC gc = event.gc;

                    gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
                    gc.setAlpha(128);

                    int minX = Math.min(startX, endX);
                    int minY = Math.min(startY, endY);

                    int maxX = Math.max(startX, endX);
                    int maxY = Math.max(startY, endY);

                    int width = maxX - minX;
                    int height = maxY - minY;

                    gc.fillRectangle(minX, minY, width, height);
                }
            }
        });


		return area;
	}

	


	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Map"); 
	}

	@Override
	public void okPressed() {
		close();
	}


	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}


}

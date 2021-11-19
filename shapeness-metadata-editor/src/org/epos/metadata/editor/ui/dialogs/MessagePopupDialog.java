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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class MessagePopupDialog extends PopupDialog {

	private Object href;
	private Point point;
	private ScrolledForm form;
	private FormToolkit toolkit;

	public MessagePopupDialog(Shell parent, String message, Object href, Point pt, FormToolkit toolkit, ScrolledForm form) {
		super(parent, PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE, true, false, false,  false, message, null);
		FormText text = toolkit.createFormText(parent, true);
		configureFormText(form.getForm(), text);
		this.form = form;
		this.toolkit = toolkit;

		
		if (href instanceof IMessage[])
			text.setText(createFormTextContent((IMessage[]) href),
					true, false);
					
		
		this.href = href;
		this.point = pt;

	}


	@Override
	protected void adjustBounds(){
		getShell().setLocation(point.x, point.y);
	}

	@Override
	protected Control createDialogArea(Composite parent){

		FormText text = toolkit.createFormText(parent, true);
		configureFormText(form.getForm(), text);

		if (href instanceof IMessage[])
			text.setText(createFormTextContent((IMessage[]) href),
					true, false);
		return text;
	}
	
	private String createFormTextContent(IMessage[] messages) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("<form>");
		for (int i = 0; i < messages.length; i++) {
			IMessage message = messages[i];
			pw.print("<li vspace=\"false\" style=\"image\" indent=\"16\" value=\"");
			switch (message.getMessageType()) {
			case IMessageProvider.ERROR:
				pw.print("error");
				break;
			case IMessageProvider.WARNING:
				pw.print("warning");
				break;
			case IMessageProvider.INFORMATION:
				pw.print("info");
				break;
			}
			/*
			pw.print("\"> <a href=\"");
			pw.print(i+"");
			pw.print("\">");
			*/
			pw.print("\">");
			
			
			
			if (message.getPrefix() != null)
				pw.print(message.getPrefix());
			pw.print(message.getMessage());
			//pw.println("</a></li>");
			pw.println("</li>");
		}
		pw.println("</form>");
		pw.flush();
		
		return sw.toString();
	}
	
	private void configureFormText(final Form form, FormText text) {
		text.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				String is = (String)e.getHref();
				try {
					int index = Integer.parseInt(is);
					IMessage [] messages = form.getChildrenMessages();
					IMessage message = messages[index];
					Control c = message.getControl();
					((FormText)e.widget).getShell().dispose();
					if (c!=null)
						c.setFocus();
				}
				catch (NumberFormatException ex) {
				}
			}
		});
		text.setImage("error", getImage(IMessageProvider.ERROR));
		text.setImage("warning", getImage(IMessageProvider.WARNING));
		text.setImage("info", getImage(IMessageProvider.INFORMATION));
	}

	private Image getImage(int type) {
		switch (type) {
		case IMessageProvider.ERROR:
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJS_ERROR_TSK);
		case IMessageProvider.WARNING:
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJS_WARN_TSK);
		case IMessageProvider.INFORMATION:
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJS_INFO_TSK);
		}
		return null;
	}





}

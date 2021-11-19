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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * This class is used to create a popup dialog
 * box to display the contents as information
 * to the user. This dialog is same as infopopup
 * dialog provided by Eclipse.
 * @author Debadatta Mishra(PIKU)
 * 
 *
 */
public final class InfoPopup extends PopupDialog
{
	/**
	 * The text control that displays the text.
	 */
	private Text text;
	
	/**
	 * The href shown in the popup.
	 */
	private Object href;
	
	/**
	 * Object of type {@link Rectangle}
	 */
	private Rectangle rectangle = null;
	
	
	/**Default constructor
	 * @param parent of type {@link Shell}
	 * @param rectangle of type {@link Rectangle}
	 * @param headerString of type String indicating the header
	 * @param footerString of type String indicating the footer
	 */
	public InfoPopup(Shell parent , Rectangle rectangle , String headerString , String footerString, Object href)
	{
		super(parent, PopupDialog.HOVER_SHELLSTYLE, false, false, true, false, headerString,footerString);
		this.rectangle = rectangle;
		this.href = href;
	}
	
	/**
	 * This method is used to show the animation
	 * by decreasing the x and y coordinates and
	 * by setting the size dynamically.
	 * @param shell of type {@link Shell}
	 */
	private static void doAnimation( Shell shell )
	{
		Point shellArea = shell.getSize();
		int x = shellArea.x;
		int y = shellArea.y;
		while( x != -200 )
		{
			try
			{
				shell.setSize(x--, y--);
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#handleShellCloseEvent()
	 */
	protected void handleShellCloseEvent() 
	{
		//Comment out the following if do not want any kind of animated effect.
		doAnimation(getShell());
		super.handleShellCloseEvent();
	}
	
//	protected boolean hasTitleArea()
//	{
//		return false;
//	}
	
//	private class ExitAction extends Action
//	{
//		ExitAction()
//		{
//			super("Close", IAction.AS_PUSH_BUTTON);
//		}
//
//		public void run()
//		{
//			close();
//		}
//	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#createTitleMenuArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createTitleMenuArea(Composite arg0) 
	{
		Control ctrl = super.createTitleMenuArea(arg0);
		Composite composite = (Composite)ctrl;
		Control[] ctrls = composite.getChildren();
		
		ToolBar toolBar = (ToolBar)ctrls[1];
		ToolItem[] toolItems = toolBar.getItems();
		toolItems[0].setImage(JFaceResources.getImage(Dialog.DLG_IMG_ERROR));
		
		return ctrl;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#fillDialogMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void fillDialogMenu(IMenuManager dialogMenu) 
	{
		dialogMenu.addMenuListener( new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager arg0) 
			{
//				close();
				handleShellCloseEvent();
			}
		}
		);
//		dialogMenu.add(new ExitAction());
	}

	/*
	 * Create a text control for showing the info about a proposal.
	 */
	protected Control createDialogArea(Composite parent, FormToolkit toolkit, ScrolledForm form)
	{
		/*text = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP
				| SWT.NO_FOCUS);
		text.setText(contents);
		return text;*/
		FormText text = toolkit.createFormText(parent, true);
		configureFormText(form.getForm(), text);
		
	
		if (href instanceof IMessage[])
			text.setText(createFormTextContent((IMessage[]) href),
					true, false);
		return text;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#adjustBounds()
	 */
	protected void adjustBounds()
	{
		Point pt = getShell().getDisplay().getCursorLocation();
		getShell().setBounds(pt.x,pt.y,rectangle.width,rectangle.height);
	}
	
	/**Method to set the text contents of the InfoPop dialog
	 * @param textContents of type String indicating the message
	 */
	/*public void setText( String textContents )
	{
		this.contents = textContents;
	}*/
	
	private void configureFormText(final Form form, FormText text) {
		text.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				String is = (String)e.getHref();
				try {
					int index = Integer.parseInt(is);
					IMessage [] messages = form.getChildrenMessages();
					IMessage message =messages[index];
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
			pw.print("\"> <a href=\"");
			pw.print(i+"");
			pw.print("\">");
			if (message.getPrefix() != null)
				pw.print(message.getPrefix());
			pw.print(message.getMessage());
			pw.println("</a></li>");
		}
		pw.println("</form>");
		pw.flush();
		return sw.toString();
	}

	
}

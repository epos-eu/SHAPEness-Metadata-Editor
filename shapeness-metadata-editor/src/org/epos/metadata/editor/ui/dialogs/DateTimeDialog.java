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

import java.util.Calendar;
import java.util.Date;

import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.epos.metadata.editor.ui.utils.Icons;
import org.epos.metadata.editor.ui.utils.Util;

public class DateTimeDialog extends TitleAreaDialog {

	private String stringTime;
	private String stringDate;
	private String fieldName;
	private XSDDateTime previuosValue;
	private CDateTime calendar;

    public DateTimeDialog(Shell parentShell, String fieldName, XSDDateTime previuosValue) {
        super(parentShell);
        this.fieldName = fieldName;
        this.previuosValue = previuosValue;
    }
    
    @Override
	public void create() {
		super.create();
		setTitle("Select date and time");
		setTitleImage(Icons.DATE_TIME.image());
		getButton(IDialogConstants.OK_ID).setEnabled(false);

	}
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(area, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginLeft = 5;
		composite.setLayout(gridLayout);

		Label fieldNameLabel = new Label(composite, SWT.BORDER_SOLID); 
		fieldNameLabel.setText(fieldName);
		GridData gdLabel = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		fieldNameLabel.setLayoutData(gdLabel);
		
		calendar = new CDateTime(composite, CDT.BORDER | CDT.DROP_DOWN | CDT.TIME_SHORT | CDT.CLOCK_DISCRETE | CDT.CLOCK_12_HOUR);
		//calendar.setL


		GridData gdt = new GridData(SWT.FILL, SWT.FILL, true, true);//GridData.VERTICAL_ALIGN_BEGINNING);
		//gdt.horizontalSpan = 2;
		calendar.setLayoutData(gdt);
		calendar.setPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		
		calendar.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				
				Date date = calendar.getSelection();
				Calendar calendar = Calendar.getInstance();
			    calendar.setTime(date);
				
				if(date != null) {
					//stringDate = (date.getYear()+1900) + "-" + date.getMonth() + "-" + date.getDay() + "T";
					//stringTime = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "Z";
					stringDate = calendar.get(Calendar.YEAR) + "-" +  (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "T";
					stringTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "Z"; 
					
					if(stringTime != null && stringDate != null) {
						getButton(IDialogConstants.OK_ID).setEnabled(true);
					}
					
				} 
				
			}
		});

		
		if(previuosValue != null) {
			calendar.setSelection(new Date(previuosValue.getYears()-1900, previuosValue.getMonths()-1, previuosValue.getDays(), 
					previuosValue.getHours(), previuosValue.getMinutes(), (int) previuosValue.getSeconds()));
		}
		
        return composite;
    }


    protected Control createDialogAreaOLD(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(area, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

        DateTime calendar = new DateTime (composite, SWT.CALENDAR | SWT.CALENDAR_WEEKNUMBERS);
		
		DateTime time = new DateTime (composite, SWT.TIME);
		time.addSelectionListener (new SelectionAdapter () {
			

			public void widgetSelected (SelectionEvent e) {
				////System.out.println ("time changed: " + time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds());
				stringTime = time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds() + "Z";
				stringDate = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay() + "T";
				if(stringTime != null && stringDate != null) {
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
				
			}
		});
		if(previuosValue != null) {
			calendar.setYear(previuosValue.getYears());
			calendar.setMonth(previuosValue.getMonths());
			calendar.setDay(previuosValue.getDays());
		}
		if(previuosValue != null) {
			time.setHours(previuosValue.getHours());
			time.setMinutes(previuosValue.getMinutes());
			time.setSeconds((int)previuosValue.getSeconds());
		}

		calendar.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				////System.out.println ("calendar date changed: " + calendar.getYear() + "/" + calendar.getMonth() + "/" + calendar.getDay());
				stringTime = time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds() + "Z";
				stringDate = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay() + "T";
				if(stringTime != null && stringDate != null) {
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
				
				
			}
		});

		 
        return composite;
    }


    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("DateTime selection"); 
    }

    @Override
    public void okPressed() {
        close();
    }

	public String getStringTime() {
		return stringTime;
	}

	public String getStringDate() {
		return stringDate;
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

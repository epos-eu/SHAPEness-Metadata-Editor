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
package org.epos.metadata.editor.ui.views;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.engine.converters.geometries.GeometryWKT;
import org.epos.metadata.editor.engine.utils.Shacl;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.dialogs.AddORBrowserListNodesDialog;
import org.epos.metadata.editor.ui.dialogs.BrowserNodeListDialog;
import org.epos.metadata.editor.ui.dialogs.ChooseNodeTypeDialog;
import org.epos.metadata.editor.ui.dialogs.DateTimeDialog;
import org.epos.metadata.editor.ui.dialogs.GeometryDialog;
import org.epos.metadata.editor.ui.dialogs.MessagePopupDialog;
import org.epos.metadata.editor.ui.dialogs.PropertiesNodeDialog;
import org.epos.metadata.editor.ui.dialogs.RemoveNodeDialog;
import org.epos.metadata.editor.ui.model.Connection;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.model.NodePrimitive;
import org.epos.metadata.editor.ui.utils.Icons;
import org.epos.metadata.editor.ui.utils.OSValidator;
import org.epos.metadata.editor.ui.utils.Util;





public class ViewForm extends ViewPart {

	public static FormToolkit toolkit;
	private static ScrolledForm form;

	private static Composite sectionMandatory;
	private static Composite sectionOptional;
	private static Composite sectionRecommend;


	public ViewForm() {

	}


	public void createPartControl(Composite parent) {

		ManagedForm managedForm = new ManagedForm(parent);
		toolkit = managedForm.getToolkit();
		form = managedForm.getForm();

		form.getForm().getHead().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));

		toolkit.decorateFormHeading(form.getForm());

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		form.getBody().setLayout(gridLayout);

		form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				String title = e.getLabel();
				Object href = e.getHref();

				Point pt = ((Control)e.widget).toDisplay(0, 0);
				pt.x += 10;
				pt.y += 10;

				MessagePopupDialog msgDialog = new MessagePopupDialog(form.getShell(), title, href, pt, toolkit, form);
				msgDialog.open();


			}

		});


		//toolkit.getColors().createColor(IFormColors.TB_BG, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN).getRGB());
		//Label separator = new Label(form.getBody(), SWT.HORIZONTAL | SWT.SEPARATOR);
		//separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		//Composite separator = new Composite(form.getBody(), SWT.NONE);
		//separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		sectionMandatory = createSection("Mandatory properties");
		sectionRecommend = createSection("Recommended properties");
		sectionOptional = createSection("Optional properties");

	}


	private Composite createSection(String sectionName) {
		Section section = toolkit.createSection(form.getBody(), Section.DESCRIPTION|Section.TWISTIE|Section.TITLE_BAR|Section.EXPANDED);
		section.setText(sectionName); 
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				//form.reflow(true);
			}
		});
		section.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));

		Composite sectionComposite = toolkit.createComposite(section, SWT.None);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		sectionComposite.setLayout(gridLayout);
		section.setClient(sectionComposite);
		sectionComposite.setLayoutData(new GridData(SWT.FILL,SWT.NONE,true,true));
		return sectionComposite;
	}


	public static void createOpenFormNode(Node node, String viewName) {

		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewNew = activePage.findView("org.epos.metadata.editor.viewForm" + ":" + viewName);

		try {
			ViewForm view = (ViewForm)activePage.showView("org.epos.metadata.editor.viewForm", viewName,
					IWorkbenchPage.VIEW_ACTIVATE);		           	   
			if (viewNew == null) {
				////System.out.println("createFormFields");
				//view.changePartName(node.getNodeName()+"-"+node.getId());
				view.changePartName(node.getNodeName(), node);
				createFormFields(node);
				view.setFocus();
			} 
		} catch (PartInitException e1) {
			e1.printStackTrace();
		}



	}

	private static void createFormToolbar(Node node) {

		Action renameIDBotton = new Action("Rename Node ID", IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {


				String IDView = "org.epos.metadata.editor.viewForm."+ node.getNodeName()+"-"+node.getId().replace(":", "_"); 
				IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				ViewForm viewForm = (ViewForm) activePage.findView("org.epos.metadata.editor.viewForm" + ":" + IDView);

				InputDialog dlg = new InputDialog(Display.getDefault().getActiveShell(),
						"Rename " + node.getNodeName() + " node ID " , "",node.getId(), null);
				if (dlg.open() == Window.OK && !dlg.getValue().isEmpty()) {
					if(DataManager.getIstance().checkIDNode(dlg.getValue())) {
						MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", 
								"A node with that ID already exists in the graph.\nPlease, specify a different ID.");
						return;
					}
					node.setId(dlg.getValue());
					ViewGraph.updateGraph();
					Util.updateFileEditor();
					ViewOutline.updateOutline();

					activePage.hideView(viewForm);

					String IDRenamedView = "org.epos.metadata.editor.viewForm."+ node.getNodeName()+"-"+node.getId().replace(":", "_"); 

					ViewForm.createOpenFormNode(node, IDRenamedView);

				}

			}
		};


		renameIDBotton.setImageDescriptor(Icons.RENAME_NODEID_ENABLED.descriptor());
		renameIDBotton.setEnabled(true);
		renameIDBotton.setToolTipText("Rename Node ID");
		form.getToolBarManager().add(renameIDBotton);


		Action propertiesBotton = new Action("Node properties", IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				new PropertiesNodeDialog(Display.getDefault().getActiveShell(), node).open();

			}




		};
		propertiesBotton.setImageDescriptor(Icons.PROPERTIES.descriptor());
		propertiesBotton.setEnabled(true);
		propertiesBotton.setToolTipText("Node properties");

		//form.getToolBarManager().add(propertiesBotton);


		form.getToolBarManager().update(true);

	}


	public static void createFormFields(Node node) {
		form.setText(node.getNodeName() + " <" + node.getId() + ">");

		createFormToolbar(node);

		IMessageManager mmng = form.getMessageManager();

		Composite sectionParameters = null;

		Field[] fields = node.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			Shacl annotation = field.getAnnotation(Shacl.class);
			String constraint = annotation.constraint();

			if(constraint.contains("Mandatory")){
				sectionParameters = sectionMandatory;
			} else if(constraint.contains("Optional")){
				sectionParameters = sectionOptional;
			} else if(constraint.contains("Recommended")){
				sectionParameters = sectionRecommend;
			} 
			if(sectionParameters != null) {
				if(field.getType().equals(List.class)) {
					try {
						createSWTObjAsList(sectionParameters, field, node,mmng);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				} else {
					createSWTObjAsSingleField(sectionParameters, field, node, mmng);
				}
				toolkit.paintBordersFor(sectionParameters);
				form.reflow(true);
			}
		}

		// Update node in order to visualize icon validation
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {

			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				Display.getDefault().asyncExec(
						new Runnable(){
							public void run(){

								ViewGraph.updateGraphNode(node);


							}
						}
						);
			}
		});

	}

	private static void createSWTObjAsSingleField(Composite sectionParameters, Field field, Node node, IMessageManager mmng) {
		////System.out.println("createSWTObjAsSingleField >> IMessageManager >> " + mmng);
		try {
			String fieldName = field.getName();
			Type type = field.getGenericType();
			String fieldValue = String.valueOf(field.get(node));

			if(Util.isPrimitiveClass(type)) {
				if(ClassUtils.getClass(type.getTypeName()) == org.apache.jena.datatypes.xsd.XSDDateTime.class) {
					createSWTObjDate(sectionParameters, node, type, fieldName, fieldValue, mmng);
				} else if(ClassUtils.getClass(type.getTypeName()) == GeometryWKT.class) {
					createSWTObjGeometry(sectionParameters, node, type, fieldName, fieldValue, mmng);
				} else {
					createSWTObjPrimive(sectionParameters, node, type, fieldName, fieldValue, mmng);
				}
			} else {
				List<String> listOfType = Util.getAvailableClasses(node.getClass().getTypeName(),fieldName);
				createSWTObjClassRef(sectionParameters, node, listOfType, fieldName, fieldValue, mmng);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createSWTObjGeometry(Composite sectionParameters, Node node, Type type, String fieldName,
			String fieldValue, IMessageManager mmng) {

		Button info = createButtonInfoField (sectionParameters, Util.getTermFromField(node, fieldName), node.getClass().getSimpleName());
		GridData gdinfo = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdinfo.verticalSpan = 3;
		info.setLayoutData(gdinfo);

		Label fieldNameLabel = toolkit.createLabel(sectionParameters, Util.getTermFromField(node, fieldName), SWT.BORDER_SOLID); //fieldName.replace("_", ":")
		GridData gdLabel = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdLabel.verticalSpan = 3;
		fieldNameLabel.setLayoutData(gdLabel);

		Text text = toolkit.createText(sectionParameters, fieldValue);
		Object inizialValue = Util.getValueFromBean(node,fieldName);
		if(inizialValue != null) {
			text.setText(String.valueOf(Util.getValueFromBean(node,fieldName)));
		} else {
			text.setText("");
		}

		text.setData(node);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.verticalSpan = 3;
		text.setLayoutData(gd);
		text.setEditable(false);
		text.setEnabled(false);

		Button buttonAdd = toolkit.createButton(sectionParameters, "Add", SWT.PUSH);
		buttonAdd.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonAdd.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					GeometryDialog dialog = new GeometryDialog(Display.getDefault().getActiveShell(), "");
					if(dialog.open() == Window.OK) {
						String geometry = dialog.getGeometry();
						if(geometry != null && !geometry.isBlank() && !geometry.isEmpty()) {
							Object obj = Util.createNewObjPrimitiveType(type,geometry);
							//System.out.println("Primitive OBJ created: " + obj.getClass());
							text.setText(geometry);
							Util.updateBeanAddObj(node,fieldName,obj);
							Util.updateFileEditor();
						}
						
					}

				}
			}
		});

		Button buttonModify = toolkit.createButton(sectionParameters, "Modify", SWT.PUSH);
		buttonModify.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonModify.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					GeometryDialog dialog = new GeometryDialog(Display.getDefault().getActiveShell(), text.getText());
					if(dialog.open() == Window.OK) {
						String geometry = dialog.getGeometry();
						if(geometry != null && !geometry.isBlank() && !geometry.isEmpty()) {
							Object obj = Util.createNewObjPrimitiveType(type,geometry);
							//System.out.println("Primitive OBJ created: " + obj.getClass());
							text.setText(geometry);
							Util.updateBeanAddObj(node,fieldName,obj);
							Util.updateFileEditor();
						}
					}
				}
			}
		});

		Button buttonRemove = toolkit.createButton(sectionParameters, "Remove", SWT.PUSH);
		buttonRemove.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonRemove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					Util.updateBeanAddObj(node,fieldName,null);
					Util.updateFileEditor();
					text.setText("");
				}
			}
		});



	}



	private static Button createButtonInfoField(Composite sectionParameters,  String fieldName, String className) {

		Button info = toolkit.createButton(sectionParameters, "" , SWT.PUSH | SWT.FLAT);
		info.setImage(Icons.INFO_TERM.image());
		if(!OSValidator.isUnix()) {
			info.addPaintListener(new PaintListener() {   

				@Override
				public void paintControl(PaintEvent event) {

					event.gc.fillRectangle(event.x, event.y, info.getBounds().width, info.getBounds().height);
					event.gc.drawImage(Icons.INFO_TERM.image(), 0, 0 );

				}

			});
		}

		info.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {

				IServiceLocator serviceLocator = PlatformUI.getWorkbench();
				ICommandService commandService = (ICommandService) serviceLocator.getService(ICommandService.class);

				try  { 
					Command command = commandService.getCommand("org.epos.metadata.editor.infoVocabularyTerm");
					Map<String, String> map = new HashMap<String, String>();
					map.put("infoTerm", fieldName);
					map.put("className", className);
					command.executeWithChecks(new ExecutionEvent(null, map, null, null));

				} catch (ExecutionException e1) {

				} catch (NotDefinedException e2){

				} catch(NotEnabledException e3){

				} catch(NotHandledException e4){

				}
			}

		});

		return info;
	}

	private static void createSWTObjDate(Composite sectionParameters, Node node, Type type, String fieldName, 
			String textValue, IMessageManager mmng) {

		Button info = createButtonInfoField (sectionParameters, Util.getTermFromField(node, fieldName), node.getClass().getSimpleName());
		GridData gdinfo = new GridData(GridData.VERTICAL_ALIGN_BEGINNING); //GridData.HORIZONTAL_ALIGN_FILL
		info.setLayoutData(gdinfo);

		Label fieldNameLabel = toolkit.createLabel(sectionParameters, Util.getTermFromField(node, fieldName), SWT.BORDER_SOLID); 
		GridData gdLabel = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		fieldNameLabel.setLayoutData(gdLabel);

		CDateTime cdt = new CDateTime(sectionParameters, CDT.BORDER | CDT.DROP_DOWN | CDT.TIME_SHORT | CDT.CLOCK_DISCRETE | CDT.CLOCK_12_HOUR);

		GridData gdt = new GridData(SWT.FILL, SWT.FILL, true, true);//GridData.VERTICAL_ALIGN_BEGINNING);
		gdt.horizontalSpan = 2;
		cdt.setLayoutData(gdt);
		//cdt.setPattern("YYYY-MM-dd'T'HH:mm:ss'Z'");kk
		cdt.setPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		cdt.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				////System.out.println(cdt.getSelection());
				Date date = cdt.getSelection();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				////System.out.println("date: " + date);
				Object obj = null;
				if(date != null) {
					//String stringDate = (calendar.getYear()+1900) + "-" + date.getMonth() + "-" + date.getDay() + "T";
					//String stringTime = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "Z";
					String stringDate = calendar.get(Calendar.YEAR) + "-" +  (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "T";
					String stringTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "Z"; 
					obj = createObjDateTime(node, fieldName, type, stringDate+stringTime);
					////System.out.println("date choosed: " + stringDate+stringTime);

				} 
				validateDateWidget(cdt, sectionParameters, fieldName, mmng);

				Util.updateBeanAddObj(node,fieldName,obj);
				Util.updateFileEditor();


			}
		});

		Object inizialValue = Util.getValueFromBean(node,fieldName);
		if(inizialValue != null) {
			XSDDateTime dateValue = (XSDDateTime) inizialValue;
			cdt.setSelection(new Date(dateValue.getYears()-1900, dateValue.getMonths()-1, dateValue.getDays(), 
					dateValue.getHours(), dateValue.getMinutes(), (int) dateValue.getSeconds()));

		} 
		validateDateWidget(cdt, sectionParameters, fieldName, mmng);
		toolkit.createLabel(sectionParameters,"");

	}

	private static void validateDateWidget(CDateTime cdt, Composite sectionParameters, String keyMessage, IMessageManager mmng) {
		// check if there are some error message to show
		if(cdt.getSelection() == null) {
			addValidationMessageTextEmpty((Section) sectionParameters.getParent(), cdt, 
					keyMessage, mmng);
			////System.out.println("text value is empty: " + keyMessage + " >> " + mmng);
		} else {
			////System.out.println("text value NOT is empty: " + keyMessage + " >> " + mmng);
			mmng.removeMessage(keyMessage, cdt);
		}
		sectionParameters.redraw();


	}

	private static Object createObjDateTime(Node node, String fieldName, Type type, String value) {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			cal.setTime(sdf.parse(value));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Object obj = Util.createNewObjPrimitiveType(type,cal);

		return obj;
	}

	private static void createSWTObjPrimive(Composite sectionParameters, Node node, Type type, String fieldName, 
			String textValue, IMessageManager mmng) {

		Button info = createButtonInfoField (sectionParameters,  Util.getTermFromField(node, fieldName), node.getClass().getSimpleName());
		GridData gdinfo = new GridData(GridData.VERTICAL_ALIGN_BEGINNING); //GridData.HORIZONTAL_ALIGN_FILL
		info.setLayoutData(gdinfo);

		Label fieldNameLabel = toolkit.createLabel(sectionParameters, Util.getTermFromField(node, fieldName), SWT.BORDER_SOLID); 
		GridData gdLabel = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		fieldNameLabel.setLayoutData(gdLabel);

		//Text text = toolkit.createText(sectionParameters, textValue);
		StyledText text = new StyledText(sectionParameters, SWT.BORDER);
		
		Object inizialValue = Util.getValueFromBean(node,fieldName);
		if(inizialValue != null) {
			text.setText(String.valueOf(Util.getValueFromBean(node,fieldName)));
		} else {
			text.setText("");
		}

		text.setData(node);
		GridData gd = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 3;
		text.setLayoutData(gd);

		Listener textListener = new Listener() {

			@Override
			public void handleEvent(Event event) {

				Object inizialValue = Util.getValueFromBean(node,fieldName);
				String fieldString = text.getText();


				if(!fieldString.isEmpty() && !fieldString.isBlank()) {
					// if the field is an URI trim() the string
					try {
						if(ClassUtils.getClass(type.getTypeName()) == java.net.URI.class) {
							fieldString = text.getText().replaceAll("\\s","");
							text.setText(fieldString);
						} else if(ClassUtils.getClass(type.getTypeName()) == Double.class) {// || ClassUtils.getClass(type.getTypeName()) == Float.class) {
							try {
								Double.parseDouble(fieldString);
								mmng.removeMessage(fieldName, text);
								
							}catch(java.lang.NumberFormatException ne){
								addValidationMessageErrorType((Section) sectionParameters.getParent(), text, 
										fieldName, mmng, "double");
								sectionParameters.redraw();
								return;
							}
							
							
						} else if(ClassUtils.getClass(type.getTypeName()) == Float.class) {// || ClassUtils.getClass(type.getTypeName()) == Float.class) {
							try {
								Float.parseFloat(fieldString);
								mmng.removeMessage(fieldName, text);
							}catch(java.lang.NumberFormatException ne){
								addValidationMessageErrorType((Section) sectionParameters.getParent(), text, 
										fieldName, mmng, "float");
								sectionParameters.redraw();
								return;
							}
							
							
						} else if(ClassUtils.getClass(type.getTypeName()) == Integer.class) {
							try {
								Integer.parseInt(fieldString);
								mmng.removeMessage(fieldName, text);
							}catch(java.lang.NumberFormatException ne){
								addValidationMessageErrorType((Section) sectionParameters.getParent(), text, 
										fieldName, mmng, "integer");
								sectionParameters.redraw();
								return;
							}
							
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sectionParameters.redraw();
					
					try {
						Object obj = Util.createNewObjPrimitiveType(ClassUtils.getClass(type.getTypeName()),fieldString);
						//System.out.println("Primitive OBJ created: " + obj.getClass());
						Util.updateBeanAddObj(text.getData(),fieldName,obj);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					Util.updateFileEditor();
				} else {
					Util.updateBeanAddObj(text.getData(),fieldName,null);
					Util.updateFileEditor();
				}
				validateTextWidget(text, sectionParameters,fieldName, mmng);

			}


		};

		text.addListener(SWT.CR, textListener);
		text.addListener(SWT.Traverse, textListener); //TAB
		text.addListener(SWT.FocusOut, textListener);



		validateTextWidget(text, sectionParameters,fieldName, mmng);

		/*
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {

			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				Display.getDefault().asyncExec(
						new Runnable(){
							public void run(){
								if (text != null && !text.isDisposed()) {

									validateTextWidget(text, sectionParameters,fieldName, mmng);
									try {
										if(ClassUtils.getClass(type.getTypeName()) == Double.class) {// || ClassUtils.getClass(type.getTypeName()) == Float.class) {
											try {
												Double.parseDouble(text.getText());
												mmng.removeMessage(fieldName, text);
											}catch(java.lang.NumberFormatException ne){
												addValidationMessageErrorType((Section) sectionParameters.getParent(), text, 
														fieldName, mmng, "double");
											}
											sectionParameters.redraw();
											return;
										} else if(ClassUtils.getClass(type.getTypeName()) == Float.class) {// || ClassUtils.getClass(type.getTypeName()) == Float.class) {
											try {
												Float.parseFloat(text.getText());
												mmng.removeMessage(fieldName, text);
											}catch(java.lang.NumberFormatException ne){
												addValidationMessageErrorType((Section) sectionParameters.getParent(), text, 
														fieldName, mmng, "float");
											}
											sectionParameters.redraw();
											return;
										} else if(ClassUtils.getClass(type.getTypeName()) == Integer.class) {
											try {
												Integer.parseInt(text.getText());
												mmng.removeMessage(fieldName, text);
											}catch(java.lang.NumberFormatException ne){
												addValidationMessageErrorType((Section) sectionParameters.getParent(), text, 
														fieldName, mmng, "integer");
											}
											sectionParameters.redraw();
											return;
										}
									} catch (ClassNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							}
						}
						);
			}
		});
		 */


	}


	private static void validateTextWidget(StyledText text, Composite sectionParameters, String keyMessage, IMessageManager mmng) {
		// check if there are some error message to show
		if(text.getText().isEmpty() || text.getText().isBlank()) {
			addValidationMessageTextEmpty((Section) sectionParameters.getParent(), text, 
					keyMessage, mmng);
			////System.out.println("text value is empty: " + keyMessage + " >> " + mmng);
		} else {
			////System.out.println("text value NOT is empty: " + keyMessage + " >> " + mmng);
			mmng.removeMessage(keyMessage, text);
		}
		sectionParameters.redraw();

	}



	// used as content provider comboViewer
	public static ArrayList<NodePrimitive> getNodesPrimitiveType(ArrayList<NodePrimitive> nodesPrimitive,List<String> listOfType) {
		ArrayList<NodePrimitive> list = new ArrayList<NodePrimitive>();
		for (NodePrimitive node : nodesPrimitive) {
			for (String type : listOfType) {
				try {

					if(ClassUtils.getClass(type).isInstance(node.getValueRef())){
						list.add(node);
					}


				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return list;
	}

	public static NodePrimitive getPrimitiveNode (ArrayList<NodePrimitive> nodesPrimitive, Object value) {
		////System.out.println("Primitive value to find: " + value);
		for (NodePrimitive nodePrimitive : nodesPrimitive) {
			if(nodePrimitive.getValueRef() == value) {
				////System.out.println("primitive node returned: " + nodePrimitive);
				return nodePrimitive;
			}
		}
		return null;
	}


	private static void createSWTObjClassRef(Composite sectionParameters, Node node, List<String> listOfType, 
			String fieldName, String textValue, IMessageManager mmng) {
		////System.out.println("createSWTObjClassRef" + fieldName);
		ArrayList<NodePrimitive> nodesPrimitive = new ArrayList<NodePrimitive>();

		Button info = createButtonInfoField (sectionParameters, Util.getTermFromField(node, fieldName), node.getClass().getSimpleName());
		GridData gdinfo = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		info.setLayoutData(gdinfo);

		Label labelClass = toolkit.createLabel(sectionParameters, Util.getTermFromField(node, fieldName), SWT.BORDER_SOLID);
		GridData gdLabel = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		labelClass.setLayoutData(gdLabel);

		ComboViewer comboViewer = new ComboViewer(sectionParameters,SWT.READ_ONLY);
		GridData gd = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		comboViewer.getCombo().setLayoutData(gd);

		comboViewer.setContentProvider(new ArrayContentProvider());

		comboViewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {

				if(element instanceof NodePrimitive) {
					return ((NodePrimitive) element).getValueRef().toString();
				} else if(element instanceof Node) {
					Node myNode = (Node) element;
					if(!myNode.isEmpty()) {
						return myNode.getNodeName()+"-"+myNode.getId();
					}else {
						return "";
					}
				} 
				return null;
			}

		});

		Node nodeEmpty = new Node(null,null);


		Object savedValue = Util.getValueFromBean(node,fieldName);
		//System.out.println("COMBO VIEWER -> saved value in bean --> " + savedValue);
		ISelection selection = null;
		Node savedNode = null;


		if(savedValue != null) {
			if(Util.isPrimitiveClass(savedValue.getClass())) {
				savedNode = getPrimitiveNode(nodesPrimitive,savedValue);
				if(savedNode == null) {
					savedNode = new NodePrimitive(savedValue.toString(), savedValue.toString(), savedValue.getClass().getName(), savedValue);
					//System.out.println("Saved value is NOT null and primitive: " + savedNode);
					nodesPrimitive.add((NodePrimitive) savedNode);
				}
				selection = new StructuredSelection(savedNode);

			} else {
				savedNode = (Node) savedValue;
				//System.out.println("Saved value is null and NOT primitive: " + savedNode);
				selection = new StructuredSelection(savedNode);
			}

			comboViewer.getCombo().setData(savedNode);
			comboViewer.getCombo().setData("previuosValue", savedNode);
		} else {
			//System.out.println("Saved value is empty");
			selection = new StructuredSelection(nodeEmpty);

			comboViewer.getCombo().setData(nodeEmpty);
			comboViewer.getCombo().setData("previuosValue", nodeEmpty);
		}

		List<Node> inputList = DataManager.getIstance().getNodesByType(listOfType);
		inputList.addAll(getNodesPrimitiveType(nodesPrimitive,listOfType));
		inputList.add(nodeEmpty);
		comboViewer.setInput(inputList);
		comboViewer.setSelection(selection);
		validateComboWidget(comboViewer, sectionParameters, fieldName, mmng);


		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {

			@Override
			public void resourceChanged(IResourceChangeEvent event) {

				Display.getDefault().asyncExec(
						new Runnable(){
							public void run(){
								if (comboViewer != null && !comboViewer.getControl().isDisposed()) {

									Object savedValue = Util.getValueFromBean(node,fieldName);
									ISelection selection = null;
									Node savedNode = null;


									if(savedValue != null) {
										if(Util.isPrimitiveClass(savedValue.getClass())) {

											savedNode = getPrimitiveNode(nodesPrimitive,savedValue);
											if(savedNode == null) {
												savedNode = new NodePrimitive(savedValue.toString(), savedValue.toString(), savedValue.getClass().getName(), savedValue);
												////System.out.println("Saved value is NOT null and primitive: " + savedNode);
												nodesPrimitive.add((NodePrimitive) savedNode);
											}

											selection = new StructuredSelection(savedNode);

										} else {
											savedNode = (Node) savedValue;
											////System.out.println("Saved value is null and NOT primitive: " + savedNode);
											selection = new StructuredSelection(savedNode);
										}

										comboViewer.getCombo().setData(savedNode);
										comboViewer.getCombo().setData("previuosValue", savedNode);
									} else {
										////System.out.println("Saved value is empty");
										selection = new StructuredSelection(nodeEmpty);
										comboViewer.getCombo().setData(node);
										comboViewer.getCombo().setData("previuosValue", nodeEmpty);
									}

									List<Node> inputList = DataManager.getIstance().getNodesByType(listOfType);
									inputList.addAll(getNodesPrimitiveType(nodesPrimitive,listOfType));
									inputList.add(nodeEmpty);
									comboViewer.setInput(inputList);
									comboViewer.setSelection(selection);

									validateComboWidget(comboViewer, sectionParameters, fieldName, mmng);

								}
							} 
						}
						);

			}
		}); 



		comboViewer.getCombo().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				// Get previuos value of combo selected in order to unset the link
				Node previuosNode = (Node) comboViewer.getCombo().getData("previuosValue");

				// previuos node can be: node, emptyNode, primitiveNode
				if(previuosNode == null || previuosNode.isEmpty()) {
					previuosNode = nodeEmpty;
				} else if(!(previuosNode instanceof NodePrimitive)) {
					previuosNode = DataManager.getIstance().getNode(previuosNode);
					//previuosNode = getPrimitiveNode(nodesPrimitive, ((NodePrimitive)comboViewer.getCombo().getData("previuosValue")));
				} 

				// get new value of combo selected which can be: node, emptyNode, primitiveNode
				StructuredSelection selectedItem = (StructuredSelection) comboViewer.getSelection();
				Node newNode = (Node) selectedItem.getFirstElement();


				if(previuosNode == newNode) {
					////System.out.println("previuous value = new node! exit");
					return;
				}

				// remove connection with previuos node
				if(previuosNode != null && !previuosNode.isEmpty() && (!(previuosNode instanceof NodePrimitive))) {
					Connection removedConnection = DataManager.getIstance().getConnection(fieldName, node, previuosNode);
					DataManager.getIstance().removeConnection(removedConnection);
					//DataManager.getIstance().removeConnection(fieldName, node, previuosNode);
				}

				// added connection with new node
				if (newNode != null && !newNode.isEmpty() && !(newNode instanceof NodePrimitive)) { // newNode is instance of Node

					Connection connection = new Connection(node.getId(),fieldName,node,newNode);
					DataManager.getIstance().getConnections().add(connection);
					Util.updateBeanAddObjType(node,fieldName,newNode,newNode.getClass().getTypeName());

				} else if (newNode != null && !newNode.isEmpty() && (newNode instanceof NodePrimitive)) { // newNode is instance of NodePrimitive
					NodePrimitive newNodePrimitive = (NodePrimitive) newNode;
					Util.updateBeanAddObjType(node,fieldName,newNodePrimitive.getValueRef(),newNodePrimitive.getClassName());

				} else if (newNode != null && newNode.isEmpty()) {
					if((previuosNode instanceof NodePrimitive)) {
						NodePrimitive previuosNodePrimitive = (NodePrimitive) previuosNode;
						Util.updateBeanAddObjType(node,fieldName,null,previuosNodePrimitive.getClassName());
					}else {
						Util.updateBeanAddObjType(node,fieldName,null,previuosNode.getClass().getTypeName());
					}
				}


				comboViewer.getCombo().setData("previuosValue", newNode);

				Util.updateFileEditor();
				ViewGraph.updateGraph();
				ViewOutline.updateOutline();
				CompositeGraphFilter.enableChipsByFilter(Util.searchClassByNode(newNode));

			}
		});



		Button buttonNew = toolkit.createButton(sectionParameters, "New", SWT.PUSH);
		buttonNew.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		buttonNew.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					String className = "";
					String valuePrimitve = "";
					boolean createNewClass = false;
					String IDType = "";
					String IDvalue = "";

					//System.out.println("List of class type for field: " + fieldName + " is: " + listOfType);
					if(listOfType.size() == 1) {
						className = listOfType.get(0);
						//CreateNewNodeDialog dialog = new CreateNewNodeDialog(Display.getDefault().getActiveShell(),className);
						//if(dialog.open() == Window.OK) {
						if(MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Question", "Do you want to create a new node " + className.substring(className.lastIndexOf(".")+1, className.length()) + "?")) {

							createNewClass = true;
							//IDvalue = dialog.getIDvalue();
							IDvalue = Engine.getIstance().getMs().getRootOntology() + className.substring(className.lastIndexOf(".")+1, className.length()) + "/" + UUID.randomUUID();

						}

						//createNewClass = MessageDialog.openConfirm(buttonNew.getShell(), "Question", "Do you want to create a new node " + className + "?");

					} else if(listOfType.size() > 1){
						ChooseNodeTypeDialog dialog = new ChooseNodeTypeDialog(buttonNew.getShell(),listOfType,fieldName);

						if (dialog.open() == Window.OK) {
							createNewClass = true;
							//System.out.println("Class choosed: " + dialog.getClassChoosed());
							className = dialog.getClassChoosed();
							if(Util.isPrimitiveClass(className)) {
								valuePrimitve = dialog.getValue();
								if(valuePrimitve.isEmpty()) {
									MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", "The field value cannot be empty.");
									return;
								} else {
									try {
										if(ClassUtils.getClass(className) == java.net.URI.class) {

											valuePrimitve = dialog.getValue().replaceAll("\\s","");
											////System.out.println("REMOVE SPACE FROM URI" + fieldString);
										}
									} catch (ClassNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							} else {

								IDvalue = dialog.getIDvalue();
								if(IDvalue.isEmpty()) {
									MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", "To create a node it is necessary an ID value!");
									return;
								}
							}
						}

					} else {
						MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", "There are no nodes to show!");
						return;
					}

					if(DataManager.getIstance().checkIDNode(IDType + IDvalue)) {
						// Warning
						MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", 
								"A node with that ID already exists in the graph.\nPlease, specify a different ID.");
						return;
					}

					Object newNode;
					if(createNewClass) {
						if (!Util.isPrimitiveClass(className)) { // create new instance of Node
							// create new node
							newNode = Util.createNewObjClass(className, IDType, IDvalue);
							//DataManager.getIstance().getNodes().add((Node) newNode);

							String IDFormView = "org.epos.metadata.editor.viewForm." +((Node)newNode).getNodeName()+ "-" + 
									((Node)newNode).getId().replace(":", "_");
							//createOpenFormNode((Node)newNode, IDFormView);

							Connection connection = new Connection(node.getId(), fieldName, node, (Node)newNode);
							DataManager.getIstance().getConnections().add(connection);
							////System.out.println("Create connection: " + connection.getLabel());

							Util.updateBeanAddObjType(node,fieldName,newNode,className);


						} else { // create new instance of NodePrimitive

							//System.out.println("value is primitive: " + valuePrimitve);
							Object valueRef = Util.createNewObjPrimitiveType(className,valuePrimitve);
							newNode = new NodePrimitive(valueRef.toString(), valueRef.toString(), className, valueRef);

							Util.updateBeanAddObjType(node,fieldName,((NodePrimitive)newNode).getValueRef(),className);
							nodesPrimitive.add((NodePrimitive) newNode);
						}

						// remove previuos node connection
						StructuredSelection selectedItem = (StructuredSelection) comboViewer.getSelection();
						Node previuosValue = (Node) selectedItem.getFirstElement();


						if(previuosValue != null && !previuosValue.isEmpty()){
							Node nodeConnected = DataManager.getIstance().getNode(previuosValue);
							Connection removedConnection = DataManager.getIstance().getConnection(fieldName, node, nodeConnected);
							DataManager.getIstance().removeConnection(removedConnection);
						}

						comboViewer.getCombo().setData("previuosValue", newNode);

						List<Node> inputList = DataManager.getIstance().getNodesByType(listOfType);
						inputList.addAll(getNodesPrimitiveType(nodesPrimitive,listOfType));
						inputList.add(nodeEmpty);
						//System.out.println("Input list for combo: " + inputList);
						comboViewer.setInput(inputList);

						ISelection selection = new StructuredSelection(newNode);
						comboViewer.setSelection(selection);
						//System.out.println("Set selection item for combo: " + newNode);


						// update graph & editor
						Util.updateFileEditor();
						ViewGraph.updateGraph();
						/*
						if(newNode instanceof Node) {
							//Util.selectNodeOnGraph(((Node)newNode).getNodeName()+ "-" + ((Node)newNode).getId());
							Util.selectNodeOnGraph(node);
						}*/
						ViewOutline.updateOutline();
						CompositeGraphFilter.enableChipsByFilter(Util.searchClassByNode((Node) newNode));
					}


				}
			}
		});


		form.reflow(true);

	}

	private static void validateComboWidget(ComboViewer comboViewer, Composite sectionParameters, String keyMessage, IMessageManager mmng) {
		// check if there are some errors message to show
		if(comboViewer.getCombo().getText().isEmpty()) {
			addValidationMessageTextEmpty((Section) sectionParameters.getParent(), comboViewer.getControl(), 
					keyMessage, mmng);
			////System.out.println("comboViewer value is empty");
		} else {
			////System.out.println("comboViewer value is NOT empty");
			mmng.removeMessage(keyMessage, comboViewer.getControl());
		} 
		sectionParameters.redraw();

	}



	private static void createSWTObjAsList(Composite sectionParameters, Field field, Node node, IMessageManager mmng) {

		try {
			String fieldName = field.getName();
			Type typeOfList = Util.getGenericTypeOfList(field);

			if(!Util.isPrimitiveClass(typeOfList)) {
				createSWTObjClassRefInListTable(sectionParameters, node, typeOfList, fieldName, mmng);			
			} else {
				if(ClassUtils.getClass(typeOfList.getTypeName()) == String.class) {
					createSWTStringInListTable(sectionParameters, node, fieldName, typeOfList, mmng);
				} else {
					createSWTObjPrimiveInListTable(sectionParameters, node, fieldName, typeOfList, mmng);
				}
			}

		} catch (IllegalArgumentException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	/*
	private static void createSWTObjClassRefInListTableOLD(Composite sectionParameters, Node node, Type typeOfList, String fieldName, IMessageManager mmng) {

		//ArrayList<NodePrimitive> nodesPrimitive = new ArrayList<NodePrimitive>();

		List<String> listOfType = Util.getAvailableClasses(node.getClass().getTypeName(),fieldName);
		////System.out.println("Classes available for: " + fieldName + " are: " + listOfType);

		Button info = createButtonInfoField (sectionParameters, fieldName, node.getClass().getSimpleName());
		GridData gdinfo = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdinfo.verticalSpan = 3;
		info.setLayoutData(gdinfo);
		//GridData gdinfo = (GridData) info.getLayoutData();
		//gdinfo.verticalSpan = 3;


		Label labelClass = toolkit.createLabel(sectionParameters, fieldName.replace("_", ":"), SWT.BORDER_SOLID);
		GridData gdLabel = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdLabel.verticalSpan = 3;
		labelClass.setLayoutData(gdLabel);

		TableViewer tableViewer = new TableViewer(sectionParameters,SWT.BORDER);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setData(node);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.verticalSpan = 3;
		tableViewer.getTable().setLayoutData(gd);

		TableLayout layout = new TableLayout();
		tableViewer.getTable().setLayout(layout);
		TableColumn tc = new TableColumn(tableViewer.getTable(), SWT.NONE, 0);
		tc.setText("Lines");
		layout.addColumnData(new ColumnWeightData(1, true));


		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				if(element instanceof Node) {
					Node myNode = (Node) element;
					if(!myNode.isEmpty()) {
						return myNode.getNodeName()+"-"+myNode.getId();
					}else {
						return "";
					}
				} else 
					return element.toString();

			}
		});

		List<Node> listInizialValue = Util.getNodeListFromBean(node,fieldName);
		if(listInizialValue != null && !listInizialValue.isEmpty()) {
			////System.out.println("listInizialValue for tableviewer: " + listInizialValue);
			//listInizialValue.addAll(getNodesPrimitiveType(nodesPrimitive,listOfType));
			tableViewer.setInput(listInizialValue);
		}


		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {

			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				Display.getDefault().asyncExec(
						new Runnable(){
							public void run(){
								if (tableViewer != null && !tableViewer.getControl().isDisposed()) {

									List<Node> listInizialValue = Util.getNodeListFromBean(node,fieldName);
									if(listInizialValue != null && !listInizialValue.isEmpty()) {
										tableViewer.setInput(listInizialValue);
									}
									tableViewer.refresh();
									tableViewer.getControl().redraw();

									validateTableViewerWidget(tableViewer, sectionParameters, fieldName, mmng);

								}
							}
						}
						);
			}
		});

		validateTableViewerWidget(tableViewer, sectionParameters, fieldName, mmng);

		Button buttonNew = toolkit.createButton(sectionParameters, "Add", SWT.PUSH);
		buttonNew.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonNew.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					String className = "";
					boolean createNewClass = false;
					String valuePrimitve = "";
					String IDType = "";
					String IDvalue = "";

					//System.out.println("List of class type for field: " + fieldName + " >> " + listOfType);

					if(listOfType.size() == 1) {
						className = listOfType.get(0);

						//createNewClass = MessageDialog.openConfirm(buttonNew.getShell(), "Question", "Do you want to create a new node " + className + "?");
						//CreateNewNodeDialog dialog = new CreateNewNodeDialog(Display.getDefault().getActiveShell(),className);
						//if(dialog.open() == Window.OK) {
						if(MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Question", "Do you want to create a new node " + className.substring(className.lastIndexOf(".")+1, className.length()) + "?")) {

							createNewClass = true;
							//IDvalue = dialog.getIDvalue();
							IDvalue = Engine.getIstance().getMs().getRootOntology() + className.substring(className.lastIndexOf(".")+1, className.length()) + "/" + UUID.randomUUID();
						}
					} else if(listOfType.size() > 1){
						ChooseNodeTypeDialog dialog = new ChooseNodeTypeDialog(buttonNew.getShell(),listOfType,fieldName);

						if (dialog.open() == Window.OK) {
							createNewClass = true;
							//System.out.println("Class choosed: " + dialog.getClassChoosed());
							className = dialog.getClassChoosed();
							if(Util.isPrimitiveClass(className)) {
								valuePrimitve = dialog.getValue();
								if(valuePrimitve.isEmpty()) {
									MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", "The field value cannot be empty.");
									return;
								} else {
									try {
										if(ClassUtils.getClass(className) == java.net.URI.class) {

											valuePrimitve = dialog.getValue().replaceAll("\\s","");
											////System.out.println("REMOVE SPACE FROM URI" + fieldString);
										}
									} catch (ClassNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							} else {

								IDvalue = dialog.getIDvalue();
								if(IDvalue.isEmpty()) {
									MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", "To create a node it is necessary an ID value!");
									return;
								}
							}
						}

					} else {
						MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", "There are no nodes to show!");
						return;
					}

					if(DataManager.getIstance().checkIDNode(IDType + IDvalue)) {
						// Warning
						MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", 
								"A node with that ID already exists in the graph.\nPlease, specify a different ID.");
						return;
					}


					Object newNode;
					if(createNewClass) {
						if (!Util.isPrimitiveClass(className)) { // create new instance of Node
							// create new node
							newNode = Util.createNewObjClass(className, IDType, IDvalue);
							//DataManager.getIstance().getNodes().add((Node) newNode);

							String IDFormView = "org.epos.metadata.editor.viewForm." +((Node)newNode).getNodeName()+ "-" + 
									((Node)newNode).getId().replace(":", "_");
							//createOpenFormNode((Node)newNode, IDFormView);

							Connection connection = new Connection(node.getId(), fieldName, node, (Node)newNode);
							DataManager.getIstance().getConnections().add(connection);
							//System.out.println("Create connection: " + connection.getLabel());

							Util.updateBeanAddObjType(node,fieldName,newNode,className);


						} else { // create new instance of NodePrimitive

							//System.out.println("value is primitive: " + valuePrimitve);
							newNode = Util.createNewObjPrimitiveType(className,valuePrimitve);
							Util.updateBeanAddObjType(node,fieldName,newNode,className);

						}

						// update graph & editor
						Util.updateFileEditor();
						ViewGraph.updateGraph();

						//if(newNode instanceof Node) {

							//Util.selectNodeOnGraph(node);
						//}
						ViewOutline.updateOutline();
						CompositeGraphFilter.enableChipsByFilter(Util.searchClassByNode((Node) newNode));
					}



				}
			}
		});

		Button buttonBrowse = toolkit.createButton(sectionParameters, "Browse", SWT.PUSH);
		buttonBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonBrowse.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:

					List<Node> list = DataManager.getIstance().getNodesByType(listOfType);
					//System.out.println("Browse classes ref to be added: " + list);


					List<Node> listObj = Util.getNodeListFromBean(node,fieldName);
					List<Node> listExtract;
					if(listObj != null && !listObj.isEmpty()) {
						listExtract = ListUtils.subtract(list, listObj);

					} else {
						listExtract = list;
					}


					// remove node from the classes list to show in the browse action
					listExtract.remove(node);

					if(listExtract.isEmpty()) {
						MessageDialog.openWarning(buttonBrowse.getShell(), "Warning", "There are no other nodes to connect to.");
					} else {
						BrowserNodeListDialog dialog = new BrowserNodeListDialog(buttonBrowse.getShell(),listExtract, tableViewer, node, fieldName);
						if (dialog.open() == Window.OK) {
							Connection connection = new Connection(node.getId(),fieldName,node,dialog.getNodeSelected());
							DataManager.getIstance().getConnections().add(connection);
							Node selected = dialog.getNodeSelected();
							Util.updateBeanAddObjType(node,fieldName,selected,selected.getClass().getName());
							tableViewer.setInput(Util.getNodeListFromBean(node,fieldName));
							tableViewer.refresh();

							Util.updateFileEditor();
							ViewGraph.updateGraph();
							ViewOutline.updateOutline();
							CompositeGraphFilter.enableChipsByFilter(Util.searchClassByNode(selected));
						}
					}


				}
			}
		});

		Button buttonRemove = toolkit.createButton(sectionParameters, "Remove", SWT.PUSH);
		buttonRemove.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonRemove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					if(!tableViewer.getSelection().isEmpty()) {

						IStructuredSelection sel = (IStructuredSelection) tableViewer.getStructuredSelection();
						Object removedObj = sel.getFirstElement();


						if(removedObj instanceof Node) {


							Connection removedConnection = DataManager.getIstance().getConnection(fieldName, node, (Node)removedObj);
							RemoveNodeDialog dialog = new RemoveNodeDialog(Display.getDefault().getActiveShell(), (Node)removedObj, removedConnection.getLabel());
							if(dialog.open() == Window.OK) {
								DataManager.getIstance().removeConnection(removedConnection);

								if(dialog.getRemovefromGraph()) {

									DataManager.getIstance().getNodes().remove(removedObj);
									// close form view of removed node
									String IDTableView = "org.epos.metadata.editor.viewForm."+ ((Node)removedObj).getNodeName()+"-"+((Node)removedObj).getId().replace(":", "_"); 
									IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
									IViewPart tableView = activePage.findView("org.epos.metadata.editor.viewForm" + ":" + IDTableView);
									activePage.hideView(tableView);

									List<Connection> connectionRemoved = DataManager.getIstance().removeConnectionWithDestination((Node) removedObj);
									for (Connection connection : connectionRemoved) {
										String fieldName = connection.getLabel();
										Node source = connection.getSource();
										Util.updateBeanRemoveObj(source, fieldName, removedObj, removedObj.getClass().getTypeName());
										////System.out.println("Removed connection with node: " + source.getNodeName()+"-"+source.getId());

									}

								}

							} else {
								return;
							}


						}

						Util.updateBeanRemoveObj(node,fieldName,removedObj);
						Util.updateFileEditor();

						ViewGraph.updateGraph();
						ViewOutline.updateOutline();
					}


				}
			}
		});
	}*/

	private static void createSWTObjClassRefInListTable(Composite sectionParameters, Node node, Type typeOfList, String fieldName, IMessageManager mmng) {

		//ArrayList<NodePrimitive> nodesPrimitive = new ArrayList<NodePrimitive>();

		List<String> listOfType = Util.getAvailableClasses(node.getClass().getTypeName(),fieldName);
		////System.out.println("Classes available for: " + fieldName + " are: " + listOfType);

		Button info = createButtonInfoField (sectionParameters, Util.getTermFromField(node, fieldName), node.getClass().getSimpleName());
		GridData gdinfo = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdinfo.verticalSpan = 3;
		info.setLayoutData(gdinfo);
		//GridData gdinfo = (GridData) info.getLayoutData();
		//gdinfo.verticalSpan = 3;


		Label labelClass = toolkit.createLabel(sectionParameters, Util.getTermFromField(node, fieldName), SWT.BORDER_SOLID);
		GridData gdLabel = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdLabel.verticalSpan = 3;
		labelClass.setLayoutData(gdLabel);

		TableViewer tableViewer = new TableViewer(sectionParameters,SWT.BORDER);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setData(node);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.verticalSpan = 3;
		tableViewer.getTable().setLayoutData(gd);

		TableLayout layout = new TableLayout();
		tableViewer.getTable().setLayout(layout);
		TableColumn tc = new TableColumn(tableViewer.getTable(), SWT.NONE, 0);
		tc.setText("Lines");
		layout.addColumnData(new ColumnWeightData(1, true));


		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				if(element instanceof Node) {
					Node myNode = (Node) element;
					if(!myNode.isEmpty()) {
						return myNode.getNodeName()+"-"+myNode.getId();
					}else {
						return "";
					}
				} else 
					return element.toString();

			}
		});

		List<Node> listInizialValue = Util.getNodeListFromBean(node,fieldName);
		if(listInizialValue != null && !listInizialValue.isEmpty()) {
			////System.out.println("listInizialValue for tableviewer: " + listInizialValue);
			//listInizialValue.addAll(getNodesPrimitiveType(nodesPrimitive,listOfType));
			tableViewer.setInput(listInizialValue);
		}


		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {

			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				Display.getDefault().asyncExec(
						new Runnable(){
							public void run(){
								if (tableViewer != null && !tableViewer.getControl().isDisposed()) {

									List<Node> listInizialValue = Util.getNodeListFromBean(node,fieldName);
									if(listInizialValue != null && !listInizialValue.isEmpty()) {
										tableViewer.setInput(listInizialValue);
									}
									tableViewer.refresh();
									tableViewer.getControl().redraw();

									validateTableViewerWidget(tableViewer, sectionParameters, fieldName, mmng);

								}
							}
						}
						);
			}
		});

		validateTableViewerWidget(tableViewer, sectionParameters, fieldName, mmng);

		Button buttonAdd = toolkit.createButton(sectionParameters, "Add", SWT.PUSH);
		buttonAdd.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonAdd.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					String className = "";
					
					String valuePrimitve = "";
					String IDType = "";
					String IDvalue = "";

					// prepare list of existing nodes per type
					List<Node> listTypedNode = DataManager.getIstance().getNodesByType(listOfType);
					List<Node> listObj = Util.getNodeListFromBean(node,fieldName);
					List<Node> listExtract;
					if(listObj != null && !listObj.isEmpty()) {
						listExtract = ListUtils.subtract(listTypedNode, listObj);
					} else {
						listExtract = listTypedNode;
					}

					// remove node from the classes list to show in the browse action
					listExtract.remove(node);


					AddORBrowserListNodesDialog dialog = new AddORBrowserListNodesDialog(Display.getDefault().getActiveShell(),listExtract, listOfType,fieldName);

					if (dialog.open() == Window.OK) {
						Object newNode;
						if(dialog.isCreateNewClass()) {
							
							//System.out.println("Create new node --> Class choosed: " + dialog.getClassChoosed());
							className = dialog.getClassChoosed();
							if(Util.isPrimitiveClass(className)) {
								valuePrimitve = dialog.getPrimitiveValue();
								if(valuePrimitve.isEmpty()) {
									MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", "The field value cannot be empty.");
									return;
								} else {
									try {
										if(ClassUtils.getClass(className) == java.net.URI.class) {
	
											valuePrimitve = dialog.getPrimitiveValue().replaceAll("\\s","");
											////System.out.println("REMOVE SPACE FROM URI" + fieldString);
										}
									} catch (ClassNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								//System.out.println("set primitve value: " + valuePrimitve);
								newNode = Util.createNewObjPrimitiveType(className,valuePrimitve);
								Util.updateBeanAddObjType(node,fieldName,newNode,className);
							} else {
								// create new node
								IDvalue = Engine.getIstance().getMs().getRootOntology() + className.substring(className.lastIndexOf(".")+1, className.length()) + "/" + UUID.randomUUID();
								newNode = Util.createNewObjClass(className, IDType, IDvalue);
								//DataManager.getIstance().getNodes().add((Node) newNode);

								String IDFormView = "org.epos.metadata.editor.viewForm." +((Node)newNode).getNodeName()+ "-" + 
										((Node)newNode).getId().replace(":", "_");
								//createOpenFormNode((Node)newNode, IDFormView);

								Connection connection = new Connection(node.getId(), fieldName, node, (Node)newNode);
								DataManager.getIstance().getConnections().add(connection);
								//System.out.println("Create connection: " + connection.getLabel());

								Util.updateBeanAddObjType(node,fieldName,newNode,className);
								CompositeGraphFilter.enableChipsByFilter(Util.searchClassByNode((Node) newNode));
							}
						} else {
							//System.out.println("Browse existing node --> node choosed: " );
							Connection connection = new Connection(node.getId(),fieldName,node,dialog.getNodeSelected());
							DataManager.getIstance().getConnections().add(connection);
							Node selected = dialog.getNodeSelected();
							Util.updateBeanAddObjType(node,fieldName,selected,selected.getClass().getName());
							tableViewer.setInput(Util.getNodeListFromBean(node,fieldName));
							tableViewer.refresh();

							
							CompositeGraphFilter.enableChipsByFilter(Util.searchClassByNode(selected));
						}
						
						Util.updateFileEditor();
						ViewGraph.updateGraph();
						/*
						if(newNode instanceof Node) {
							//Util.selectNodeOnGraph(((Node)newNode).getNodeName()+ "-" + ((Node)newNode).getId());
							Util.selectNodeOnGraph(node);
						}*/
						ViewOutline.updateOutline();
						
					}



					


				}
			}
		});

		

		Button buttonRemove = toolkit.createButton(sectionParameters, "Remove", SWT.PUSH);
		GridData gdR = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		//gdR.horizontalSpan = 2;
		gdR.verticalSpan = 2;
		buttonRemove.setLayoutData(gdR);
		//buttonRemove.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonRemove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					if(!tableViewer.getSelection().isEmpty()) {

						IStructuredSelection sel = (IStructuredSelection) tableViewer.getStructuredSelection();
						Object removedObj = sel.getFirstElement();


						if(removedObj instanceof Node) {


							Connection removedConnection = DataManager.getIstance().getConnection(fieldName, node, (Node)removedObj);
							RemoveNodeDialog dialog = new RemoveNodeDialog(Display.getDefault().getActiveShell(), (Node)removedObj, removedConnection.getLabel());
							if(dialog.open() == Window.OK) {
								DataManager.getIstance().removeConnection(removedConnection);

								if(dialog.getRemovefromGraph()) {

									DataManager.getIstance().getNodes().remove(removedObj);
									// close form view of removed node
									String IDTableView = "org.epos.metadata.editor.viewForm."+ ((Node)removedObj).getNodeName()+"-"+((Node)removedObj).getId().replace(":", "_"); 
									IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
									IViewPart tableView = activePage.findView("org.epos.metadata.editor.viewForm" + ":" + IDTableView);
									activePage.hideView(tableView);

									List<Connection> connectionRemoved = DataManager.getIstance().removeConnectionWithDestination((Node) removedObj);
									for (Connection connection : connectionRemoved) {
										String fieldName = connection.getLabel();
										Node source = connection.getSource();
										Util.updateBeanRemoveObj(source, fieldName, removedObj, removedObj.getClass().getTypeName());
										////System.out.println("Removed connection with node: " + source.getNodeName()+"-"+source.getId());

									}

								}

							} else {
								return;
							}


						}

						Util.updateBeanRemoveObj(node,fieldName,removedObj);
						Util.updateFileEditor();

						ViewGraph.updateGraph();
						ViewOutline.updateOutline();
					}


				}
			}
		});
	}

	private static void validateTableViewerWidget(TableViewer tableViewer, Composite sectionParameters, String keyMessage, IMessageManager mmng) {
		// check if there are some errors message to show
		if(tableViewer.getTable().getItemCount()==0) {
			addValidationMessageTextEmpty((Section) sectionParameters.getParent(), tableViewer.getControl(), 
					keyMessage, mmng);
			////System.out.println("tableViewer value is empty");
		} else {
			////System.out.println("tableViewer value is NOT empty");
			mmng.removeMessage(keyMessage, tableViewer.getControl());
		} 
		sectionParameters.redraw();

	}


	private static String insertNewLineForLongString(String longString) {
		StringBuilder sb = new StringBuilder(longString);

		int i = 0;
		while ((i = sb.indexOf(" ", i + 200)) != -1) {
			sb.replace(i, i + 1, "\n");
		}

		return sb.toString();
	}


	private static void createSWTStringInListTable(Composite sectionParameters, Node node, String fieldName, Type type, IMessageManager mmng) {

		Button info = createButtonInfoField (sectionParameters, Util.getTermFromField(node, fieldName), node.getClass().getSimpleName());
		GridData gdinfo = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdinfo.verticalSpan = 3;
		info.setLayoutData(gdinfo);

		Label fieldNameLabel = toolkit.createLabel(sectionParameters, Util.getTermFromField(node, fieldName), SWT.BORDER_SOLID); 
		GridData gdLabel = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdLabel.verticalSpan = 3;
		fieldNameLabel.setLayoutData(gdLabel);

		TableViewer tableText = new TableViewer(sectionParameters, SWT.BORDER);

		tableText.setContentProvider(ArrayContentProvider.getInstance());

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.verticalSpan = 3;
		tableText.getControl().setLayoutData(gd);

		TableLayout layout = new TableLayout();
		tableText.getTable().setLayout(layout);
		//tableText.getTable().setHeaderVisible(true);
		tableText.getTable().setLinesVisible(true);

		TableColumn tc = new TableColumn(tableText.getTable(), SWT.NONE, 0);
		tc.setText("Lines");
		layout.addColumnData(new ColumnWeightData(1, true));


		tableText.setLabelProvider(new OwnerDrawLabelProvider() {

			@Override
			protected void measure(Event event, Object element) {
				event.width = tableText.getTable().getColumn(event.index).getWidth();
				if (event.width == 0)
					return;
				String line = (String) element;
				Point size = event.gc.textExtent(line);
				int lines = size.x / event.width + 1;
				event.height = size.y * lines;

			}

			@Override
			protected void paint(Event event, Object element) {

				String entry = (String) element;
				event.gc.drawText(entry, event.x, event.y, true);
			}
		});



		ArrayList<String> entries = new ArrayList<String>();
		List<Node> listInizialValue = Util.getValueListFromBean(node,fieldName);
		if(listInizialValue != null && !listInizialValue.isEmpty()) {
			for (int i = 0; i < listInizialValue.size(); i++) {
				Object n = listInizialValue.get(i);
				entries.add(insertNewLineForLongString(n.toString()));

			}
		}
		tableText.setInput(entries);



		validateTableStringWidget(tableText, sectionParameters, fieldName, mmng);


		Button buttonNew = toolkit.createButton(sectionParameters, "Add", SWT.PUSH);
		buttonNew.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonNew.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					////System.out.println("Add value");

					InputDialog dlg = new InputDialog(buttonNew.getShell(),
							"", "Add new " + fieldNameLabel.getText(),"", null);
					if (dlg.open() == Window.OK) {

						String fieldString = dlg.getValue();
						if(!fieldString.isEmpty() && !fieldString.isBlank()) {
							entries.add(fieldString);
							tableText.setInput(entries);
							tableText.setSelection(new StructuredSelection(fieldString));
							Object obj = Util.updateBeanAddObj(node,fieldName,fieldString);
							tableText.refresh();
							tableText.getControl().redraw();
							Util.updateFileEditor();
							validateTableStringWidget(tableText, sectionParameters, fieldName, mmng);
						}
					}
				}
			}

		});

		Button buttonModify = toolkit.createButton(sectionParameters, "Modify", SWT.PUSH);
		buttonModify.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonModify.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					if(tableText.getTable().getSelectionCount() > 0) {

						StructuredSelection selectedItem = (StructuredSelection) tableText.getSelection();
						String oldValue = (String) selectedItem.getFirstElement();
						//System.out.println("Modify value: " + oldValue);


						InputDialog dlg = new InputDialog(buttonModify.getShell(),
								"", "Modify " + fieldNameLabel.getText(),oldValue, null);
						if (dlg.open() == Window.OK) {
							String newValue = dlg.getValue();
							//System.out.println("With value: " + newValue);

							if(!newValue.isEmpty() && !newValue.isBlank() && !newValue.equals(oldValue)) {

								entries.remove(oldValue);
								entries.add(newValue);
								tableText.setInput(entries);
								tableText.setSelection(new StructuredSelection(newValue));
								tableText.refresh();
								tableText.getControl().redraw();
								Object objNewValue = Util.createNewObjPrimitiveType(oldValue.getClass(),newValue);
								Util.updateBeanModifyObj(node,fieldName, oldValue, objNewValue);
								Util.updateFileEditor();
								validateTableStringWidget(tableText, sectionParameters, fieldName, mmng);
							}

						}

					}
				}
			}
		});

		Button buttonRemove = toolkit.createButton(sectionParameters, "Remove", SWT.PUSH);
		buttonRemove.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonRemove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					if(tableText.getTable().getSelectionCount() > 0) {
						StructuredSelection selectedItem = (StructuredSelection) tableText.getSelection();
						String selectedString = (String) selectedItem.getFirstElement();

						entries.remove(selectedString);
						tableText.setInput(entries);
						tableText.refresh();
						tableText.getControl().redraw();
						Util.updateBeanRemoveObj(node,fieldName,selectedString);						

						Util.updateFileEditor();
						validateTableStringWidget(tableText, sectionParameters, fieldName, mmng);
					}

				}
			}
		});
		form.reflow(true);

	}

	private static void createSWTObjPrimiveInListTable(Composite sectionParameters, Node node, String fieldName, Type type, IMessageManager mmng) {

		Button info = createButtonInfoField (sectionParameters, Util.getTermFromField(node, fieldName), node.getClass().getSimpleName());
		GridData gdinfo = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdinfo.verticalSpan = 3;
		info.setLayoutData(gdinfo);

		Label fieldNameLabel = toolkit.createLabel(sectionParameters, Util.getTermFromField(node, fieldName), SWT.BORDER_SOLID); 
		GridData gdLabel = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdLabel.verticalSpan = 3;
		fieldNameLabel.setLayoutData(gdLabel);

		Table tableText = toolkit.createTable(sectionParameters,SWT.BORDER);

		tableText.setData(node);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.verticalSpan = 3;
		tableText.setLayoutData(gd);



		List<Node> listInizialValue = Util.getValueListFromBean(node,fieldName);
		if(listInizialValue != null && !listInizialValue.isEmpty()) {
			for (Object n : listInizialValue) {
				TableItem item = new TableItem(tableText, SWT.NULL);
				item.setText(n.toString());
				item.setData(n);
			}
		}


		/*
		TableEditor editor = new TableEditor(tableText);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 200;
		// editing the second column
		int EDITABLECOLUMN = 0;
		tableText.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Clean up any previous editor control
				Control oldEditor = editor.getEditor();
				if (oldEditor != null)
					oldEditor.dispose();
				// Identify the selected row
				TableItem item = (TableItem) e.item;
				if (item == null)
					return;
				// The control that will be the editor must be a child of the Table
				Text newEditor = new Text(tableText, SWT.NONE);
				newEditor.setText(item.getText(EDITABLECOLUMN));
				newEditor.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent me) {
						Text text = (Text) editor.getEditor();
						editor.getItem().setText(EDITABLECOLUMN, text.getText());
						// modify bean value
					}
				});
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, EDITABLECOLUMN);
			}
		});
		 */

		/*
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {

			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				Display.getDefault().asyncExec(
						new Runnable(){
							public void run(){
								if (tableText != null && !tableText.isDisposed()) {

									validateTableWidget(tableText, sectionParameters, fieldName, mmng);

								}
							}
						}
						);
			}
		});
		 */


		validateTableWidget(tableText, sectionParameters, fieldName, mmng);

		Button buttonNew = toolkit.createButton(sectionParameters, "Add", SWT.PUSH);
		buttonNew.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonNew.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					////System.out.println("Add value");

					try {
						if(ClassUtils.getClass(type.getTypeName()) == org.apache.jena.datatypes.xsd.XSDDateTime.class) {

							DateTimeDialog dlg = new DateTimeDialog(Display.getDefault().getActiveShell(), fieldName, null);
							if (dlg.open() == Window.OK) {
								TableItem item = new TableItem(tableText, SWT.NULL);
								String date = dlg.getStringDate();
								String time = dlg.getStringTime();
								item.setText(date+time);

								Object obj = createObjDateTime(node, fieldName, type, date+time);
								//Util.updateBeanAddObj(tableText.getData(),fieldName,item.getText());
								item.setData(obj);
								Util.updateBeanAddObj(node,fieldName,obj);
								Util.updateFileEditor();

							}

						} else if(ClassUtils.getClass(type.getTypeName()) == GeometryWKT.class) {

							GeometryDialog dialog = new GeometryDialog(Display.getDefault().getActiveShell(), "");
							if(dialog.open() == Window.OK) {
								String geometry = dialog.getGeometry();
								if(geometry != null && !geometry.isBlank() && !geometry.isEmpty()) {

									TableItem item = new TableItem(tableText, SWT.NULL);
									item.setText(geometry);
	
									Object obj = Util.createNewObjPrimitiveType(type,geometry);
									item.setData(obj);
									Util.updateBeanAddObj(node,fieldName,obj);
									Util.updateFileEditor();
								}
							}

						} else {
							InputDialog dlg = new InputDialog(buttonNew.getShell(),
									"", "Add new " + fieldNameLabel.getText(),"", null);
							if (dlg.open() == Window.OK) {

								String fieldString = dlg.getValue();
								if(!fieldString.isEmpty() && !fieldString.isBlank()) {
									if(ClassUtils.getClass(type.getTypeName()) == Double.class) {// || ClassUtils.getClass(type.getTypeName()) == Float.class) {
										try {
											Double.parseDouble(fieldString);
										}catch(java.lang.NumberFormatException ne){
											MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", 
													"The value must be double");
											return;
										}


									} else if(ClassUtils.getClass(type.getTypeName()) == Float.class) {// || ClassUtils.getClass(type.getTypeName()) == Float.class) {
										try {
											Float.parseFloat(fieldString);

										}catch(java.lang.NumberFormatException ne){
											MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", 
													"The value must be float");
											return;
										}

									} else if(ClassUtils.getClass(type.getTypeName()) == Integer.class) {
										try {
											Integer.parseInt(fieldString);

										}catch(java.lang.NumberFormatException ne){
											MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", 
													"The value must be integer");
											return;
										}

									}

									// if the field is an URI trim() the string
									if(ClassUtils.getClass(type.getTypeName()) == java.net.URI.class) {

										fieldString = dlg.getValue().replaceAll("\\s","");
										////System.out.println("REMOVE SPACE FROM URI" + fieldString);
									}

									TableItem item = new TableItem(tableText, SWT.NULL);
									item.setText(fieldString);

									Object obj = Util.updateBeanAddObj(tableText.getData(),fieldName,item.getText());
									item.setData(obj);
									Util.updateFileEditor();
								}
							}
						}

					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				validateTableWidget(tableText, sectionParameters, fieldName, mmng);

			}

		});

		Button buttonModify = toolkit.createButton(sectionParameters, "Modify", SWT.PUSH);
		buttonModify.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonModify.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					if(tableText.getSelectionCount() > 0) {

						TableItem selectedItem = tableText.getSelection()[0];
						Object oldValue = selectedItem.getData();
						//System.out.println("Modify value: " + oldValue);

						if(oldValue.getClass() == org.apache.jena.datatypes.xsd.XSDDateTime.class) {

							DateTimeDialog dlg = new DateTimeDialog(Display.getDefault().getActiveShell(), fieldName, (XSDDateTime) oldValue);
							if (dlg.open() == Window.OK) {

								String date = dlg.getStringDate();
								String time = dlg.getStringTime();
								selectedItem.setText(date+time);

								Object objNewValue = createObjDateTime(node, fieldName, type, date+time);
								selectedItem.setData(objNewValue);
								Util.updateBeanModifyObj(tableText.getData(),fieldName, oldValue, objNewValue);
								Util.updateFileEditor();

							}

						} else if(oldValue.getClass() == GeometryWKT.class) {
							GeometryDialog dialog = new GeometryDialog(Display.getDefault().getActiveShell(), oldValue.toString());
							if(dialog.open() == Window.OK) {
								String geometry = dialog.getGeometry();
								if(geometry != null && !geometry.isBlank() && !geometry.isEmpty()) {
									selectedItem.setText(geometry);
									Object objNewValue  = Util.createNewObjPrimitiveType(type,geometry);
									selectedItem.setData(objNewValue);								
									Util.updateBeanModifyObj(tableText.getData(),fieldName, oldValue, objNewValue);
									Util.updateFileEditor();
								}
							}


						} else {

							InputDialog dlg = new InputDialog(buttonModify.getShell(),
									"", "Modify " + fieldNameLabel.getText(),selectedItem.getText(), null);
							if (dlg.open() == Window.OK) {
								String newValue = dlg.getValue();
								//System.out.println("With value: " + newValue);

								if(!newValue.isEmpty() && !newValue.isBlank() && !newValue.equals(oldValue)) {

									try {
										if(ClassUtils.getClass(type.getTypeName()) == Double.class) {// || ClassUtils.getClass(type.getTypeName()) == Float.class) {
											try {
												Double.parseDouble(newValue);
											}catch(java.lang.NumberFormatException ne){
												MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", 
														"The value must be double");
												return;
											}


										} else if(ClassUtils.getClass(type.getTypeName()) == Float.class) {// || ClassUtils.getClass(type.getTypeName()) == Float.class) {
											try {
												Float.parseFloat(newValue);

											}catch(java.lang.NumberFormatException ne){
												MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", 
														"The value must be float");
												return;
											}

										} else if(ClassUtils.getClass(type.getTypeName()) == Integer.class) {
											try {
												Integer.parseInt(newValue);

											}catch(java.lang.NumberFormatException ne){
												MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", 
														"The value must be integer");
												return;
											}

										}
									} catch (ClassNotFoundException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

									// if the field is an URI trim() the string
									try {
										if(ClassUtils.getClass(type.getTypeName()) == java.net.URI.class) {
											newValue = dlg.getValue().replaceAll("\\s","");
										}
									} catch (ClassNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									selectedItem.setText(newValue);
									Object objNewValue = Util.createNewObjPrimitiveType(oldValue.getClass(),newValue);
									selectedItem.setData(objNewValue);
									Util.updateBeanModifyObj(tableText.getData(),fieldName, oldValue, objNewValue);
									Util.updateFileEditor();
								}

							}
						}
					}
				}
				validateTableWidget(tableText, sectionParameters, fieldName, mmng);
			}
		});

		Button buttonRemove = toolkit.createButton(sectionParameters, "Remove", SWT.PUSH);
		buttonRemove.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		buttonRemove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					if(tableText.getSelectionCount() > 0) {
						TableItem removedItem = tableText.getSelection()[0];
						Util.updateBeanRemoveObj(tableText.getData(),fieldName,removedItem.getData());						
						removedItem.dispose();
						tableText.update();
						Util.updateFileEditor();
						validateTableWidget(tableText, sectionParameters, fieldName, mmng);
					}

				}
			}
		});
		form.reflow(true);

	}	

	private static void validateTableWidget(Table table, Composite sectionParameters, String keyMessage, IMessageManager mmng) {
		// check if there are some errors message to show
		if(table.getItemCount()==0) {
			addValidationMessageTextEmpty((Section) sectionParameters.getParent(), table, 
					keyMessage, mmng);
			////System.out.println("tableViewer value is empty");
		} else {
			////System.out.println("tableViewer value is NOT empty");
			mmng.removeMessage(keyMessage, table);
		} 
		sectionParameters.redraw();

	}

	private static void validateTableStringWidget(TableViewer table, Composite sectionParameters, String keyMessage, IMessageManager mmng) {
		// check if there are some errors message to show
		if(table.getTable().getItemCount()==0) {
			addValidationMessageTextEmpty((Section) sectionParameters.getParent(), table.getControl(), 
					keyMessage, mmng);
			////System.out.println("tableViewer value is empty");
		} else {
			////System.out.println("tableViewer value is NOT empty");
			mmng.removeMessage(keyMessage, table.getControl());
		} 
		sectionParameters.redraw();

	}


	public void setFocus() {

		//Util.selectNodeOnGraph(getPartName() + "-" + super.getPartProperty("IDNode"));
		form.getMessageManager().update();


		//form.requestLayout();


		// select relative node on ghaph
		String IDNode = super.getPartProperty("IDNode");
		Node node = DataManager.getIstance().getNodeById(IDNode);
		Util.selectNodeOnGraph(node);
	}

	public void dispose() {
		super.dispose();
	}

	public void changePartName(String newName, Node node) {
		super.setPartProperty("IDNode", node.getId());
		super.setPartName(newName);
	}

	private static void addValidationMessageTextEmpty(Section section, Control widget, String keyMessage, IMessageManager mmng) {
		////System.out.println("addValidationMessage");
		int messageType = 0;
		if(section.getText().contains("Mandatory")) {
			messageType = IMessageProvider.ERROR;
		} else if(section.getText().contains("Recommended")) {
			messageType = IMessageProvider.WARNING;
		} if(section.getText().contains("Optional")) {
			return;
		}
		mmng.addMessage(keyMessage, "A value must be specified", null, messageType, widget);
	}

	private static void addValidationMessageErrorType(Section section, Control widget, String keyMessage, IMessageManager mmng, String type) {
		////System.out.println("addValidationMessage");
		int messageType = 0;
		if(section.getText().contains("Mandatory")) {
			messageType = IMessageProvider.ERROR;
		} else if(section.getText().contains("Recommended")) {
			messageType = IMessageProvider.WARNING;
		} if(section.getText().contains("Optional")) {
			return;
		}
		mmng.addMessage(keyMessage, "the value must be " + type, null, messageType, widget);
	}




}

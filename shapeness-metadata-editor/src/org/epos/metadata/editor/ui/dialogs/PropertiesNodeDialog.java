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

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.opal.propertytable.PTProperty;
import org.eclipse.nebula.widgets.opal.propertytable.PropertyTable;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTCheckboxEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTColorEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTComboEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTDateEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTDimensionEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTDirectoryEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTFileEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTFloatEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTFontEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTInsetsEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTIntegerEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTPasswordEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTRectangleEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTSpinnerEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTStringEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTURLEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IMessageManager;
import org.epos.metadata.editor.engine.converters.geometries.GeometryWKT;
import org.epos.metadata.editor.engine.utils.Shacl;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.model.DataManager;
import org.epos.metadata.editor.ui.utils.Icons;
import org.epos.metadata.editor.ui.utils.Util;

public class PropertiesNodeDialog extends TitleAreaDialog {

	private Node node;


	public PropertiesNodeDialog(Shell parentShell, Node node) {
		super(parentShell);
		this.node = node;

	}

	@Override
	public void create() {
		super.create();
		setTitle("Node Properties");
		//setTitleImage(Icons.DATE_TIME.image());
		getButton(IDialogConstants.OK_ID).setEnabled(true);

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(area, SWT.NONE);

		composite.setLayout(new FillLayout(SWT.VERTICAL));
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));

		TabFolder tabFolder = new TabFolder(composite, SWT.BORDER);

		TabItem itemMandatory = new TabItem(tabFolder, SWT.NONE);
		itemMandatory.setText("Mandatory");
		PropertyTable tableMandatory = new PropertyTable(tabFolder, SWT.NONE);

		//item1.setControl(buildMandatoryPropertyTable(tabFolder, true, true, true));

		TabItem itemRecommended = new TabItem(tabFolder, SWT.NONE);
		itemRecommended.setText("Recommended");
		PropertyTable tableRecommended = new PropertyTable(tabFolder, SWT.NONE);
		//itemRecommended.setControl(tableRecommended);
		//itemRecommended.setControl(buildRecommendedPropertyTable(tabFolder, false, true, false));

		TabItem itemOptional = new TabItem(tabFolder, SWT.NONE);
		itemOptional.setText("Optional");
		PropertyTable tableOptional = new PropertyTable(tabFolder, SWT.NONE);
		//itemOptional.setControl(tableOptional);
		//itemOptional.setControl(buildOptionalPropertyTable(tabFolder, true, false, true));

		PropertyTable tableFields = null;
		Field[] fields = node.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			Shacl annotation = field.getAnnotation(Shacl.class);
			String constraint = annotation.constraint();

			if(constraint.contains("Mandatory")){
				tableFields = tableMandatory;
				//System.out.println("Mandatory: " + field.getName());
			} else if(constraint.contains("Optional")){
				tableFields = tableRecommended;
			} else if(constraint.contains("Recommended")){
				tableFields = tableOptional;
			}
			try {
				String fieldName = field.getName();
				String fieldValue = String.valueOf(field.get(node));
				Type type = field.getGenericType();

				if(field.getType().equals(List.class)) {
					//tableFields.addProperty(new PTProperty(fieldName, fieldName, "Description of this list")).setCategory("List");

					Type typeOfList = Util.getGenericTypeOfList(field);

					/*
					if(!Util.isPrimitiveClass(typeOfList)) {
						
						createPrimitiveProperties(field, null, tableFields, field.getName() + " - Obj List");
					} else {
						
						List<Node> listInizialValue = Util.getValueListFromBean(node,fieldName);
						if(listInizialValue != null && !listInizialValue.isEmpty()) {
							for (Object value : listInizialValue) {
								createPrimitiveProperties(field, value, tableFields, field.getName() + " - Primitive List");
							}
						}

					}
					*/
					List<Node> listInizialValue = Util.getValueListFromBean(node,fieldName);
					if(listInizialValue != null && !listInizialValue.isEmpty()) {
						for (Object value : listInizialValue) {
							createPrimitiveProperties(field, value, typeOfList, tableFields, field.getName() + " - List");
						}
					}


				} else {

					
					if(Util.isPrimitiveClass(type)) {

						createPrimitiveProperties(field, fieldValue, type, tableFields, field.getName() + " - Primitive");

					} else {
						// create an Editor to create a link between node and other entities
						List<String> listOfType = Util.getAvailableClasses(node.getClass().getTypeName(),fieldName);
						List<Node> inputList = DataManager.getIstance().getNodesByType(listOfType);
						tableFields.addProperty(new PTProperty(fieldName, fieldName, "Description for field obj", fieldValue)).setCategory(field.getName() + " - Refs").
						setEditor(new PTComboEditor(true, inputList.toArray()));


						//createSWTObjClassRef(sectionParameters, node, listOfType, fieldName, fieldValue, mmng);
					} 




				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}



		itemMandatory.setControl(tableMandatory);
		itemRecommended.setControl(tableRecommended);
		itemOptional.setControl(tableOptional);
		return area;
	}

	private void createPrimitiveProperties(Field field, Object fieldValue, Type type, PropertyTable tableFields, String category) {

		String fieldName = field.getName();
		try {
			

			if(ClassUtils.getClass(type.getTypeName()) == org.apache.jena.datatypes.xsd.XSDDateTime.class) {
				tableFields.addProperty(new PTProperty(fieldName, fieldName, "Description of date", fieldValue)).setCategory(category).setEditor(new PTDateEditor()); 

			} else if(ClassUtils.getClass(type.getTypeName()) == GeometryWKT.class) {
				//createSWTObjGeometry(sectionParameters, node, type, fieldName, fieldValue, mmng);
			} else if(ClassUtils.getClass(type.getTypeName()) == java.net.URI.class) {

				tableFields.addProperty(new PTProperty(fieldName, fieldName, "This is a nice <b>URL</b>", fieldValue).setCategory(category)).setEditor(new PTURLEditor());

			} else if(ClassUtils.getClass(type.getTypeName()) == Float.class) {

				tableFields.addProperty(new PTProperty(fieldName, fieldName, "Type any float", fieldValue)).setCategory(category).setEditor(new PTFloatEditor());

			} else if(ClassUtils.getClass(type.getTypeName()) == Integer.class) {

				tableFields.addProperty(new PTProperty(fieldName, fieldName, "Type any integer", fieldValue)).setCategory(category).setEditor(new PTIntegerEditor());

			} else {
				//System.out.println("tableFields text: " +  field.getName());
				tableFields.addProperty(new PTProperty(fieldName, fieldName, "Description for the description field", fieldValue)).setCategory(category).setEditor(new PTStringEditor());
			}


		} catch (ClassNotFoundException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private static PropertyTable buildMandatoryPropertyTable(final TabFolder tabFolder, final boolean showButton, final boolean showAsCategory, final boolean showDescription) {

		PropertyTable table = new PropertyTable(tabFolder, SWT.NONE);

		if (showButton) {
			table.showButtons();
		} else {
			table.hideButtons();
		}

		if (showAsCategory) {
			table.viewAsCategories();
		} else {
			table.viewAsFlatList();
		}

		if (showDescription) {
			table.showDescription();
		} else {
			table.hideDescription();
		}
		table.addProperty(new PTProperty("id", "Identifier", "Description for identifier", "My id")).setCategory("General");
		table.addProperty(new PTProperty("text", "Description", "Description for the description field", "blahblah...")).setCategory("General");
		table.addProperty(new PTProperty("url", "URL:", "This is a nice <b>URL</b>", "http://www.google.com").setCategory("General")).setEditor(new PTURLEditor());
		table.addProperty(new PTProperty("password", "Password", "Enter your <i>password</i> and keep it secret...", "password")).setCategory("General").setEditor(new PTPasswordEditor());
		table.addProperty(new PTProperty("longText", "Long", "Description for the description field, which is soooooo long that you have to scroll to see eveything and this is not user-friendly obviously.\n" + //
				"I keep typing but I've no idea, I just want to make it as long as possible, but\nI've definitively no idea.\n" + //
				"I think this is the last line...\nDid you know that I'm a huge fan of Pink Floyd ?", //
				"too long...")).setCategory("General");

		table.addProperty(new PTProperty("int", "An integer", "Type any integer", "123")).setCategory("Number").setEditor(new PTIntegerEditor());
		table.addProperty(new PTProperty("float", "A float", "Type any float", "123.45")).setCategory("Number").setEditor(new PTFloatEditor());
		table.addProperty(new PTProperty("spinner", "Another integer", "Use a spinner to enter an integer")).setCategory("Number").setEditor(new PTSpinnerEditor(0, 100));


		return table;
	}

	private static PropertyTable buildRecommendedPropertyTable(final TabFolder tabFolder, final boolean showButton, final boolean showAsCategory, final boolean showDescription) {

		PropertyTable table = new PropertyTable(tabFolder, SWT.NONE);

		if (showButton) {
			table.showButtons();
		} else {
			table.hideButtons();
		}

		if (showAsCategory) {
			table.viewAsCategories();
		} else {
			table.viewAsFlatList();
		}

		if (showDescription) {
			table.showDescription();
		} else {
			table.hideDescription();
		}

		table.addProperty(new PTProperty("directory", "Directory", "Select a directory")).setCategory("Directory/File").setEditor(new PTDirectoryEditor());
		table.addProperty(new PTProperty("file", "File", "Select a file")).setCategory("Directory/File").setEditor(new PTFileEditor());

		table.addProperty(new PTProperty("comboReadOnly", "Combo (read-only)", "A simple combo with seasons")).setCategory("Combo").setEditor(new PTComboEditor(true, new Object[] { "Spring", "Summer", "Autumn", "Winter" }));
		table.addProperty(new PTProperty("combo", "Combo", "A combo that is not read-only")).setCategory("Combo").setEditor(new PTComboEditor("Value 1", "Value 2", "Value 3"));

		table.addProperty(new PTProperty("cb", "Checkbox", "A checkbox")).setCategory("Checkbox").setEditor(new PTCheckboxEditor()).setCategory("Checkbox");
		table.addProperty(new PTProperty("cb2", "Checkbox (disabled)", "A disabled checkbox...")).setEditor(new PTCheckboxEditor()).setCategory("Checkbox").setEnabled(false);


		return table;
	}

	private static PropertyTable buildOptionalPropertyTable(final TabFolder tabFolder, final boolean showButton, final boolean showAsCategory, final boolean showDescription) {

		PropertyTable table = new PropertyTable(tabFolder, SWT.NONE);

		if (showButton) {
			table.showButtons();
		} else {
			table.hideButtons();
		}

		if (showAsCategory) {
			table.viewAsCategories();
		} else {
			table.viewAsFlatList();
		}

		if (showDescription) {
			table.showDescription();
		} else {
			table.hideDescription();
		}


		table.addProperty(new PTProperty("color", "Color", "Pick it !")).setCategory("Misc").setEditor(new PTColorEditor());
		table.addProperty(new PTProperty("font", "Font", "Pick again my friend")).setEditor(new PTFontEditor()).setCategory("Misc");
		table.addProperty(new PTProperty("dimension", "Dimension", "A dimension is composed of a width and a height")).setCategory("Misc").setEditor(new PTDimensionEditor());
		table.addProperty(new PTProperty("rectangle", "Rectangle", "A rectangle is composed of a position (x,y) and a dimension(width,height)")).setCategory("Misc").setEditor(new PTRectangleEditor());
		table.addProperty(new PTProperty("inset", "Inset", "An inset is composed of the following fields:top,left,bottom,right)")).setCategory("Misc").setEditor(new PTInsetsEditor());
		table.addProperty(new PTProperty("date", "Date", "Well, is there something more to say ?")).setCategory("Misc").setEditor(new PTDateEditor()); 

		return table;
	}






	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Node Properties"); 
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

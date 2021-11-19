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
package org.epos.metadata.editor.ui.wizard;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.engine.VocabularyStore;
import org.epos.metadata.editor.engine.converters.Classes2Model;
import org.epos.metadata.editor.engine.converters.Model2Classes;
import org.epos.metadata.editor.engine.io.FileReader;
import org.epos.metadata.editor.engine.io.FileWriter;
import org.epos.metadata.editor.engine.io.ModelReader;
import org.epos.metadata.editor.engine.model.ModelStored;
import org.epos.metadata.editor.ui.model.Project;
import org.epos.metadata.editor.ui.utils.Icons;
import org.epos.metadata.editor.ui.utils.Util;

public class WizardImportVocabularies extends WizardPage {

	private Map<Class, Image> shapesList;
	Project dataproject;
	private Composite container;
	private Tree tree;
	Logger log = Logger.getLogger("RCP_log");
	//private Button importAdditionalVocab;
	private Button buttonColor;

	public WizardImportVocabularies(Project dataproject) {
		super("Second Page");
		this.dataproject = dataproject;
		setTitle("Project");
		setDescription("These will be the colors used to represent metadata entities according to the shape type.");
		setImageDescriptor(ImageDescriptor.createFromImage(Icons.NEW_PROJECT_WIZ.image()));
		shapesList = dataproject.getShapesList();

	}


	@Override
	public void setVisible(boolean visible){
		final String[] exception = new String[1];
		if(visible) {
			if(shapesList.size() > 0) {
				tree.removeAll();
			}



			try {
				getContainer().run(true, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						monitor.beginTask("Building project...", 5);

						try {

							new Engine.EngineBuilder()
							.setModel2Classes(new Model2Classes())
							.setModelStored(new ModelStored())
							.setModelReader(new ModelReader(dataproject.getUrlShacl()))
							.setClasses2Model(new Classes2Model())
							.setFileReader(new FileReader())
							.setFileWriter(new FileWriter())
							.setVocabularyStore(new VocabularyStore(null))
							.build();


							ArrayList<Class<?>> list = Engine.getIstance().getM2c().getClasses();
							monitor.worked(3);

							for (Class shape : list) 
								shapesList.put(shape,null);

							dataproject.setShapesList(shapesList);
							//importAdditionalVocab.setEnabled(true);

							// check if RDF/Turtle file is valid
							if(!dataproject.isNewFile()) {
								try {
									Engine.getIstance().getFreader().read(dataproject.getTurtleFile());

								} catch (IOException e) {
									// TODO Auto-generated catch block
									//e.printStackTrace();
									exception[0] = "RDF/Turtle file not valid:\n" + e.getMessage();
									//System.out.println("RDF/Turtle file not loaded");
									
								} catch (org.apache.jena.riot.RiotException e) {
									exception[0] = "RDF/Turtle file not valid:\n" + e.getMessage();
									//System.out.println("***************RDF/Turtle file not valid");									
									

								}

							}
							monitor.worked(2);



						} catch (NullPointerException n) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							exception[0] = "SHACL file not valid:\n" + n.getMessage();
							//System.out.println("InputStream is null");
							

						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							exception[0] = "SHACL file not valid:\n" + e.getMessage();
							//System.out.println("SHACL file not loaded");
							
						} catch (org.apache.jena.riot.RiotException e) {
							exception[0] = "SHACL file not valid:\n" + e.getMessage();
							//System.out.println("***************SHACL file not valid");
							

						}

						monitor.done();

					}
				});

				if (exception[0] == null) {
					createTreeItems();
					buttonColor.setVisible(true);
					tree.setVisible(true);
					setPageComplete(true);
					setErrorMessage(null);
					super.setVisible(visible);
				} else {
					setErrorMessage(exception[0]);
					setPageComplete(false);
					buttonColor.setVisible(false);
					tree.setVisible(false);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}


	@Override
	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginLeft = 5;
		container.setLayout(gridLayout);

		createTree();
		//createSeparator();
		//createButtonVocabularies();

		setControl(container);
		setPageComplete(false);

	}


	private void createSeparator() {
		Label separator = new Label(container, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		data.verticalSpan = 10;
		separator.setLayoutData(data);
	}

	/*
	private void createButtonVocabularies() {

		importAdditionalVocab = new Button(container, SWT.CHECK);
		importAdditionalVocab.setText("Download additional vocabularies");
		importAdditionalVocab.setSelection(false);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		importAdditionalVocab.setLayoutData(data);

		importAdditionalVocab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(importAdditionalVocab.getSelection()) {
					dataproject.setAdditionalVocabularies(true);
					////System.out.println("project folder: " + dataproject.getProjectFolder());
					File tmp_vocab_folder = new File(dataproject.getProjectFolder()+"/tmp/");
					//Path tmp_vocab_folder = new Path(dataproject.getProjectFolder()+"/tmp/");
					if (!tmp_vocab_folder.exists()) {
						tmp_vocab_folder.mkdir();
					}
					try {
						getContainer().run(true, false, new CopyVocabulariesThread(Engine.getIstance().getMs().
								getShaclModel().getNsPrefixMap(),tmp_vocab_folder.getAbsolutePath()+"/" , new HashMap<String, String>()));
						importAdditionalVocab.setEnabled(false);
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				} else {
					dataproject.setAdditionalVocabularies(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
	}
	 */

	private void createButtonColor() {

		buttonColor = new Button(container, SWT.PUSH);
		buttonColor.setText("Color...");

		GridData gdColor = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdColor.widthHint = 100;
		//gdinfo.grabExcessHorizontalSpace = true;

		buttonColor.setLayoutData(gdColor);

		buttonColor.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ColorDialog dlg = new ColorDialog(container.getShell());

				int pixelValue = buttonColor.getImage().getImageData().getPixel(5,5);
				PaletteData palette = buttonColor.getImage().getImageData().palette; 
				dlg.setRGB(palette.getRGB(pixelValue));

				dlg.setText("Choose a Color");

				// Open the dialog and retrieve the selected color
				RGB rgb = dlg.open();
				if (rgb != null) {

					buttonColor.setImage(Util.createImageIcon(rgb));

					TreeItem item = (TreeItem) buttonColor.getData("item");
					Image icon = Util.createImageIcon(rgb);
					item.setImage(icon);

					shapesList.replace((Class) item.getData(), icon);
				}
			}
		});


	}

	private void createTree() {

		GridData gridData = new GridData();
		gridData.widthHint = 300;
		gridData.heightHint = 550;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		tree = new Tree(container, SWT.BORDER);
		tree.setLayoutData(gridData);

		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				/*
				 * show color item
				 */
				TreeItem item = (TreeItem)event.item;
				Image icon = item.getImage();
				int pixelValue = icon.getImageData().getPixel(5,5);

				PaletteData palette = icon.getImageData().palette; 
				RGB rgb = palette.getRGB(pixelValue);
				//color = new Color(container.getDisplay(), rgb);
				buttonColor.setImage(Util.createImageIcon(rgb));
				buttonColor.setData("item", item);

			}
		});

		createButtonColor();


	}

	private void createTreeItems() {
		TreeItem root = new TreeItem(tree, SWT.NULL);
		root.setText("Shapes");

		ArrayList<Class> sortedKeysList = new ArrayList<>(shapesList.keySet());
		Collections.sort(sortedKeysList, Comparator.comparing(Class::getSimpleName));

		for (Class classShape : sortedKeysList) {

			TreeItem item = new TreeItem(root, SWT.NULL);
			item.setText(classShape.getSimpleName());
			Image icon = Util.createImageIcon(classShape.getSimpleName());
			item.setImage(icon);
			item.setData(classShape);
			shapesList.put(classShape, icon);
		}
		tree.getItem(0).setExpanded(true);
		TreeItem firstItem = tree.getItem(0).getItem(0);
		tree.setSelection(firstItem);
		Image icon = firstItem.getImage();

		int pixelValue = icon.getImageData().getPixel(5,5);
		PaletteData palette = icon.getImageData().palette;		
		buttonColor.setImage(Util.createImageIcon(palette.getRGB(pixelValue)));
		buttonColor.setData("item", tree.getSelection()[0]);
	}



	


	private ArrayList<Color> colors = new ArrayList<Color>();
	private double distanceFactor = 200.0; //Color distance factor


	public  org.eclipse.swt.graphics.Color randomColor() {
		Color tColor = null;
		while(true) {
			float red = ThreadLocalRandom.current().nextFloat() ;
			float green = ThreadLocalRandom.current().nextFloat();
			float blue = ThreadLocalRandom.current().nextFloat();

			tColor = new Color(red,green,blue);
			tColor = tColor.brighter();

			if(!colors.contains(tColor)) {
				if(checkColorDistance(tColor) || colors.isEmpty()) {
					colors.add(tColor);
					return new org.eclipse.swt.graphics.Color(Display.getDefault(), tColor.getRed(), tColor.getGreen(), tColor.getBlue());
				}
			}
		}
	}


	private boolean checkColorDistance(Color tColor) {
		for(Color c : colors) {
			if(colorDistance(tColor, c) > distanceFactor && colorDistance(tColor, new Color(0,0,0)) > distanceFactor){
				colors.add(tColor);
				return true;
			}
		}
		return false;
	}


	private double colorDistance(Color leftColor, Color rightColor) {
		double value = Math.pow(leftColor.getRed()-rightColor.getRed(),2)+Math.pow(leftColor.getBlue()-rightColor.getBlue(),2)+Math.pow(leftColor.getGreen()-rightColor.getGreen(),2);
		double distance = Math.sqrt(value);
		return distance;
	}

}
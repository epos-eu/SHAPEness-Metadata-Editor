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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;

public class WizardImportShacl extends WizardPage {

	Logger log = Logger.getLogger("RCP_log");

	Project dataProject;

	private Text text_projectName;
	private Label locationPathField;
	private Text shaclTextFolder;
	private Button createNewFileButton;
	private Text locationNewFile;
	private Button importTTLFileButton;
	private Text pathImportedFileFromFolder;

	private Text shaclTextURL;

	private Button radioLocalFolder;

	private Button radioURL;

	private Button importTTLFromFolder;

	private Button importTTLFromURL;

	private Text pathImportedFileURL;

	public WizardImportShacl(Project data) {
		super("wizardPage");
		setTitle("Project");
		setDescription("Create a new project.");
		setImageDescriptor(ImageDescriptor.createFromImage(Icons.NEW_PROJECT_WIZ.image()));
		this.dataProject = data;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginLeft = 5;
		container.setLayout(gridLayout);

		createProjectNameGroup(container);
		createSeparator(container);
		createLocationGroup(container);
		createSeparator(container);
		createShaclGroup(container);
		createSeparator(container);
		createTurtleGroup(container);

		setControl(container);
		validatePage();
		//setPageComplete(true);
	}
	private void createSeparator(Composite composite) {
		Label separator = new Label(composite, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		data.verticalSpan = 4;
		separator.setLayoutData(data);
	}

	private void createTurtleGroup(Composite composite) {

		Group group = new Group(composite, SWT.NONE);
		group.setText("RDF/Turtle metadata file");

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginLeft = 5;
		group.setLayout(gridLayout);

		GridData dataG = new GridData(GridData.FILL_HORIZONTAL);
		dataG.horizontalSpan = 4;
		group.setLayoutData(dataG);

		createNewFileButton = new Button(group, SWT.RADIO);
		createNewFileButton.setText("Create a new RDF/Turtle file");
		createNewFileButton.setSelection(true);

		locationNewFile = new Text(group, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;

		locationNewFile.setLayoutData(data);
		locationNewFile.setEnabled(true);
		locationNewFile.setText("example");
		locationNewFile.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();

			}
		});

		importTTLFileButton = new Button(group, SWT.RADIO);
		importTTLFileButton.setText("Import existing RDF/Turtle file");
		importTTLFileButton.setSelection(false);

		GridData layout = new GridData(GridData.FILL_HORIZONTAL);
		layout.horizontalSpan = 4;
		importTTLFileButton.setLayoutData(layout);
		importTTLFileButton.setSelection(false);

		Group groupImport = new Group(group, SWT.NONE);
		groupImport.setEnabled(false);
		//group.setText("RDF/Turtle metadata file");

		GridLayout gridLayoutG = new GridLayout();
		gridLayoutG.numColumns = 4;
		gridLayoutG.marginWidth = 5;
		gridLayoutG.marginHeight = 5;
		gridLayoutG.marginRight = 5;
		gridLayoutG.marginLeft = 5;
		groupImport.setLayout(gridLayoutG);

		GridData dataGI = new GridData(GridData.FILL_HORIZONTAL);
		dataGI.horizontalSpan = 4;
		groupImport.setLayoutData(dataGI);

		importTTLFromFolder = new Button(groupImport, SWT.RADIO);
		importTTLFromFolder.setText("Select from local folder: ");
		GridData layoutF = new GridData();
		layoutF.horizontalIndent = 20;
		importTTLFromFolder.setLayoutData(layoutF);

		pathImportedFileFromFolder = new Text(groupImport, SWT.BORDER);
		GridData layoutFolder = new GridData(GridData.FILL_HORIZONTAL);
		layoutFolder.horizontalSpan = 2;
		pathImportedFileFromFolder.setEnabled(false);
		pathImportedFileFromFolder.setLayoutData(layoutFolder);
		pathImportedFileFromFolder.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();

			}
		});

		Button buttonBrowse = new Button(groupImport, SWT.NONE);
		buttonBrowse.setText("Browse...");
		buttonBrowse.setEnabled(false);
		buttonBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(e.display.getActiveShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String [] {"*.ttl"});
				dialog.setFilterPath(System.getProperty("user.home"));
				String result = dialog.open();
				if(result != null || !result.isEmpty()) {

					try {
						URL urlLocalFile = new File(result).toURI().toURL();
						log.log(Level.INFO, "urlLocalFile: " + urlLocalFile);
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						log.log(Level.WARNING, e1.getMessage()+" Specify the path to a valid SHACL file"); 
						setErrorMessage("Specify the path to a valid SHACL file");
					}

					pathImportedFileFromFolder.setText(result);

				} else {
					setErrorMessage("Specify the path to a valid SHACL file");
				}
				validatePage();
			}
		});

		importTTLFromURL = new Button(groupImport, SWT.RADIO);
		importTTLFromURL.setText("Select from URL: ");
		GridData layoutU = new GridData();
		layoutU.horizontalIndent = 20;
		importTTLFromURL.setLayoutData(layoutU);

		pathImportedFileURL = new Text(groupImport, SWT.BORDER);
		GridData layoutURL = new GridData(GridData.FILL_HORIZONTAL);
		layoutURL.horizontalSpan = 3;
		pathImportedFileURL.setEnabled(false);
		pathImportedFileURL.setLayoutData(layoutURL);
		pathImportedFileURL.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();

			}
		});

		SelectionAdapter listener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (importTTLFromFolder.getSelection()) {
					pathImportedFileFromFolder.setEnabled(true);
					buttonBrowse.setEnabled(true);

					pathImportedFileURL.setEnabled(false);


				} else if (importTTLFromURL.getSelection()) {
					pathImportedFileFromFolder.setEnabled(false);
					buttonBrowse.setEnabled(false);

					pathImportedFileURL.setEnabled(true);

				}
				validatePage();

			}
		};

		importTTLFromFolder.addSelectionListener(listener);
		importTTLFromURL.addSelectionListener(listener);



		createNewFileButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (createNewFileButton.getSelection()) {
					locationNewFile.setEnabled(true);
					groupImport.setEnabled(false);
					importTTLFromFolder.setSelection(false);
					importTTLFromURL.setSelection(false);

				} else if (importTTLFileButton.getSelection())  {
					locationNewFile.setEnabled(false);					
					groupImport.setEnabled(true);
					if(!importTTLFromFolder.getSelection() && !importTTLFromURL.getSelection()) {
						importTTLFromFolder.setSelection(true);
						pathImportedFileFromFolder.setEnabled(true);
						buttonBrowse.setEnabled(true);
						pathImportedFileURL.setEnabled(false);
					}

				}
				validatePage();

			}
		});

		importTTLFileButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (importTTLFileButton.getSelection()) {
					locationNewFile.setEnabled(false);					
					groupImport.setEnabled(true);	
					if(!importTTLFromFolder.getSelection() && !importTTLFromURL.getSelection()) {
						importTTLFromFolder.setSelection(true);
						pathImportedFileFromFolder.setEnabled(true);
						buttonBrowse.setEnabled(true);
						pathImportedFileURL.setEnabled(false);
					}

				} else if (createNewFileButton.getSelection()) {

					locationNewFile.setEnabled(true);
					groupImport.setEnabled(false);
					importTTLFromFolder.setSelection(false);
					importTTLFromURL.setSelection(false);

				}
				validatePage();

			}
		});



	}


	private void createLocationGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setText("Working directory");

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginLeft = 5;
		group.setLayout(gridLayout);

		GridData dataG = new GridData(GridData.FILL_HORIZONTAL);
		dataG.horizontalSpan = 4;
		group.setLayoutData(dataG);

		locationPathField = new Label(group, SWT.NONE);
		locationPathField.setText(IDEWorkbenchMessages.ProjectLocationSelectionDialog_locationLabel + " " + Util.getDefaultLocation());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		locationPathField.setLayoutData(data);

		/*
		TreeViewer tv = new TreeViewer(group);
		GridData dataTree = new GridData(GridData.FILL_BOTH);
		dataTree.heightHint = 100;
		dataTree.horizontalSpan = 4;
		tv.getTree().setLayoutData(dataTree);
		tv.setContentProvider(new FileTreeContentProvider());
		tv.setLabelProvider(new FileTreeLabelProvider());
		tv.setInput(ResourcesPlugin.getWorkspace());
		 */
	}

	/*
	private void createLocationGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setText("Working directory");

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginLeft = 5;
		group.setLayout(gridLayout);

		GridData dataG = new GridData(GridData.FILL_HORIZONTAL);
		dataG.horizontalSpan = 4;
		group.setLayoutData(dataG);

		Button useDefaultsButton = new Button(group, SWT.CHECK);
		useDefaultsButton.setText(IDEWorkbenchMessages.ProjectLocationSelectionDialog_useDefaultLabel);
		useDefaultsButton.setSelection(true);
		GridData useDefaultsData = new GridData(GridData.FILL_HORIZONTAL);
		useDefaultsData.horizontalSpan = 4;
		useDefaultsButton.setLayoutData(useDefaultsData);


		// location label
		Label locationLabel = new Label(group, SWT.NONE);
		locationLabel.setText(IDEWorkbenchMessages.ProjectLocationSelectionDialog_locationLabel);

		// project location entry field
		locationPathField = new Text(group, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		locationPathField.setLayoutData(data);
		locationPathField.setEnabled(false);
		locationPathField.setText(getDefaultLocation());
		locationPathField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();

			}
		});

		// browse button
		Button browseButton = new Button(group, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.setEnabled(false);
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dialog = new DirectoryDialog(locationPathField.getShell(), SWT.SHEET);
				dialog.setMessage(IDEWorkbenchMessages.ProjectLocationSelectionDialog_directoryLabel);
				String dirLocation = dialog.open();
				if(!dirLocation.isEmpty()) {
					locationPathField.setText(dirLocation);
				}
				validatePage();
			}
		});

		useDefaultsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean useDefaults = useDefaultsButton.getSelection();

				if (useDefaults) {
					locationPathField.setEnabled(false);
					browseButton.setEnabled(false);
					locationPathField.setText(getDefaultLocation());

				} else {
					locationPathField.setEnabled(true);
					browseButton.setEnabled(true);


				}
				validatePage();

			}
		});

	}
	 */

	private void createProjectNameGroup(Composite parent) {

		Label lbtProjectName = new Label(parent, SWT.NONE);
		lbtProjectName.setText("Project name");

		GridData layout = new GridData();
		layout.horizontalSpan = 3;
		layout.grabExcessHorizontalSpace = true;
		layout.horizontalAlignment = GridData.FILL;

		text_projectName = new Text(parent, SWT.BORDER);
		text_projectName.setLayoutData(layout);
		text_projectName.setText("MyProject");

		text_projectName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();

			}
		});


	}


	private void createShaclGroup(Composite composite) {

		Group group = new Group(composite, SWT.NONE);
		group.setText("Import SHACL constraints");

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginLeft = 5;
		group.setLayout(gridLayout);

		GridData dataG = new GridData(GridData.FILL_HORIZONTAL);
		dataG.horizontalSpan = 4;
		group.setLayoutData(dataG);

		radioLocalFolder = new Button(group, SWT.RADIO);
		radioLocalFolder.setText("Select from local folder: ");

		//Label lbtFileName = new Label(group, SWT.NONE);
		//lbtFileName.setText("Select from local folder: ");

		shaclTextFolder = new Text(group, SWT.BORDER);
		//shaclText.setText("https://raw.githubusercontent.com/epos-eu/EPOS-DCAT-AP/EPOS-DCAT-AP-shapes/epos-dcat-ap_draft_update.ttl");


		
		shaclTextFolder.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();

			}
		});
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		shaclTextFolder.setLayoutData(data);


		Button buttonBrowse = new Button(group, SWT.NONE);
		buttonBrowse.setText("Browse...");
		buttonBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(e.display.getActiveShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String [] {"*.ttl"});
				dialog.setFilterPath(System.getProperty("user.home"));
				String result = dialog.open();
				if(result != null || !result.isEmpty()) {


					try {
						URL urlLocalFile = new File(shaclTextFolder.getText()).toURI().toURL();
						log.log(Level.INFO, "urlLocalFile: " + urlLocalFile);


					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						log.log(Level.WARNING, e1.getMessage()+" Specify the path to a valid SHACL file"); 
						setErrorMessage("SHACL file cannot be empty.");
					}

					shaclTextFolder.setText(result);
					setErrorMessage(null);

				} else {
					setErrorMessage("Specify the path to a valid SHACL file");
				}
				validatePage();
			}

		});

		radioURL = new Button(group, SWT.RADIO);
		radioURL.setText("Select from URL: ");
		radioURL.setSelection(true);

		//Label lbtFileName = new Label(group, SWT.NONE);
		//lbtFileName.setText("Select from local folder: ");

		shaclTextURL = new Text(group, SWT.BORDER);
		shaclTextURL.setText("https://raw.githubusercontent.com/epos-eu/EPOS-DCAT-AP/EPOS-DCAT-AP-shapes/epos-dcat-ap_shapes.ttl");

		shaclTextURL.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();

			}
		});
		GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.horizontalSpan = 3;
		shaclTextURL.setLayoutData(data2);

		SelectionAdapter listener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (radioLocalFolder.getSelection()) {
					shaclTextFolder.setEnabled(true);
					buttonBrowse.setEnabled(true);

					shaclTextURL.setEnabled(false);


				} else if (radioURL.getSelection()) {
					shaclTextFolder.setEnabled(false);
					buttonBrowse.setEnabled(false);

					shaclTextURL.setEnabled(true);

				}
				validatePage();

			}
		};

		radioURL.addSelectionListener(listener);
		radioLocalFolder.addSelectionListener(listener);

	}

	private boolean validatePage() {

		if(!text_projectName.getText().isEmpty() && !text_projectName.getText().isBlank() &&
				((radioLocalFolder.getSelection() && !shaclTextFolder.getText().isEmpty() && !shaclTextFolder.getText().isBlank())
						|| radioURL.getSelection() && !shaclTextURL.getText().isEmpty() && !shaclTextURL.getText().isEmpty())

				&& ((createNewFileButton.getSelection() && !locationNewFile.getText().isEmpty() && !locationNewFile.getText().isBlank()) 
						//&& !(new File(locationPathField.getText()+"/"+text_projectName.getText()+"/"+locationNewFile.getText()).exists()))

						|| ( (importTTLFileButton.getSelection() && importTTLFromFolder.getSelection() && !pathImportedFileFromFolder.getText().isEmpty() && !pathImportedFileFromFolder.getText().isBlank())
								|| (importTTLFileButton.getSelection() && importTTLFromURL.getSelection() && !pathImportedFileURL.getText().isEmpty() && !pathImportedFileURL.getText().isBlank())
								)			
						)) {
			if(new File(Util.getDefaultLocation()+"/"+text_projectName.getText()).exists()) {
				setErrorMessage("A project with that name already exists in the workspace.");
				setPageComplete(false);
				return false;
			} else if(new File(Util.getDefaultLocation()+"/"+text_projectName.getText()+"/"+"metadata"+"/"+locationNewFile.getText()).exists()) {
				setErrorMessage("A Turtle file with that name already exists in the workspace.");
				setPageComplete(false);
				return false;
			}


			setPageComplete(true);
			setErrorMessage(null);
			return true;
		} else {

			setPageComplete(false);
			if(text_projectName.getText().isEmpty() || text_projectName.getText().isBlank()) {
				setErrorMessage("Project name cannot be empty.");
			} else if (locationPathField.getText().isEmpty()) {
				setErrorMessage("Working directory path cannot be empty.");
			} else if(radioLocalFolder.getSelection() && (shaclTextFolder.getText().isEmpty() || shaclTextFolder.getText().isBlank())) {
				setErrorMessage("SHACL file path cannot be empty.");
			} else if(radioURL.getSelection() && (shaclTextURL.getText().isEmpty() || shaclTextURL.getText().isBlank())) {
				setErrorMessage("SHACL file path cannot be empty.");
			} else if(createNewFileButton.getSelection() && (locationNewFile.getText().isEmpty() || locationNewFile.getText().isBlank())) {
				setErrorMessage("Turtle file path cannot be empty.");
			} else if(createNewFileButton.getSelection() && (new File(locationPathField.getText()+"/"+text_projectName.getText()+"/"+locationNewFile.getText()).exists())) {
				setErrorMessage("Turtle file already exists.");
			} else if(importTTLFileButton.getSelection() && 
					(importTTLFromFolder.getSelection() && pathImportedFileFromFolder.getText().isEmpty() || pathImportedFileFromFolder.getText().isBlank())
					|| (importTTLFromURL.getSelection() && pathImportedFileURL.getText().isEmpty() || pathImportedFileURL.getText().isBlank())
					) {
				setErrorMessage("Turtle file path cannot be empty.");
			} 
			return false;
		}
	}




	@Override
	public IWizardPage getNextPage() {

		dataProject.setProjectName(text_projectName.getText());
		dataProject.setProjectFolder(Util.getDefaultLocation());

		if(radioLocalFolder.getSelection()) {
			dataProject.setUrlShacl(shaclTextFolder.getText());
		} else if (radioURL.getSelection()) {
			dataProject.setUrlShacl(shaclTextURL.getText());
		}

		if(createNewFileButton.getSelection()) {
			if(!locationNewFile.getText().contains(".ttl")) {
				dataProject.setTurtleFile(locationNewFile.getText()+".ttl");
			} else {
				dataProject.setTurtleFile(locationNewFile.getText());
			}

			dataProject.setNewFile(true);
		} else if(importTTLFileButton.getSelection()) {
			if(importTTLFromFolder.getSelection()) {
				dataProject.setTurtleFile(pathImportedFileFromFolder.getText());
			} else if(importTTLFromURL.getSelection()) {
				dataProject.setTurtleFile(pathImportedFileURL.getText());
			} 
			dataProject.setNewFile(false);
		}


		return super.getNextPage();
	}

	public Text getShaclText() {
		return shaclTextFolder;
	}

	public void setShaclText(Text shaclText) {
		this.shaclTextFolder = shaclText;
	}

	class FileTreeContentProvider implements ITreeContentProvider {

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}


		public Object[] getElements(Object inputElement) {
			return ResourcesPlugin.getWorkspace().getRoot().getProjects();
		}


		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IProject) {
				IProject projects = (IProject) parentElement;
				try {
					return projects.members();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (parentElement instanceof IFolder) {
				IFolder ifolder = (IFolder) parentElement;
				try {
					return ifolder.members();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return null;
		}

		public Object getParent(Object element) {
			if (element instanceof IProject) {
				IProject projects = (IProject) element;
				return projects.getParent();
			}
			if (element instanceof IFolder) {
				IFolder folder = (IFolder) element;
				return folder.getParent();
			}
			if (element instanceof IFile) {
				IFile file = (IFile) element;
				return file.getParent();
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof IProject) {
				IProject projects = (IProject) element;
				try {
					return projects.members().length > 0;
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (element instanceof IFolder) {
				IFolder folder = (IFolder) element;
				try {
					return folder.members().length > 0;
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;
		}

	}


	class FileTreeLabelProvider implements ILabelProvider {

		public void addListener(ILabelProviderListener listener) {

		}

		public void dispose() {

		}

		public boolean isLabelProperty(Object element, String property) {

			return false;
		}

		public void removeListener(ILabelProviderListener listener) {

		}

		public Image getImage(Object element) {

			return null;
		}

		public String getText(Object element) {
			if (element instanceof IProject) {
				String text = ((IProject) element).getName();
				return text;
			}
			if (element instanceof IFolder) {
				String text = ((IFolder) element).getName();
				return text;
			}
			if (element instanceof IFile) {
				String text = ((IFile) element).getName();
				return text;
			}

			return null;
		}
	}
}

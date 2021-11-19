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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.epos.metadata.editor.engine.GitModule;
import org.epos.metadata.editor.ui.model.Project;
import org.epos.metadata.editor.ui.utils.Icons;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class GitDialog extends TitleAreaDialog {

	Logger log = Logger.getLogger("RCP_log");

	private String userString = "";

	private String pwdString = "";

	private String repositoryString = "";

	private String branchString = "";

	private String commitMsg = "";

	private Project dataProject;

	private boolean savePrefs;

	private Button eyeButton;



	public GitDialog(Shell parentShell, Project project) {
		super(parentShell);
		dataProject = project;
	}


	@Override
	public void create() {
		super.create();
		setTitle("Destination Git Repository");
		setMessage("Enter the location of the destination repository.");
		setTitleImage(Icons.GIT_PUSH.image());
		getButton(IDialogConstants.OK_ID).setEnabled(false);

	}

	@Override
	protected Control createDialogArea(Composite parent) {

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 10;
		gridLayout.marginRight = 10;
		gridLayout.marginLeft = 10;
		parent.setLayout(gridLayout);

		createSeparator(parent);
		createGroupRepo(parent);
		createSeparator(parent);
		createGroupAuth(parent);
		createSeparator(parent);

		Button savePrefsButton = new Button(parent, SWT.CHECK);
		savePrefsButton.setText("Save Preferences");
		savePrefsButton.setSelection(true);
		savePrefs = true;
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalIndent = 15;
		savePrefsButton.setLayoutData(data);
		savePrefsButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(savePrefsButton.getSelection()) {
					savePrefs = true;
				} else {
					savePrefs = false;
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});


		return parent;
	}

	private void createSeparator(Composite composite) {
		Label separator = new Label(composite, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		data.verticalSpan = 4;
		separator.setLayoutData(data);
	}


	private void createGroupRepo(Composite parent) {

		Preferences projectNode = getProjectPreferences();

		Group group = new Group(parent, SWT.NONE);
		group.setText("Location");

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


		Label lbtRepo = new Label(group, SWT.NONE);
		lbtRepo.setText("URL repository:");

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;


		Text repository = new Text(group, SWT.BORDER);
		repository.setLayoutData(data);


		if(projectNode.get("gitRepository", null) != null) {
			repository.setText(projectNode.get("gitRepository", null).toString());
			repositoryString = repository.getText();
		}
		repository.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {

				repositoryString = repository.getText();
				valiteFields();

			}
		});

		Label lbtBranch = new Label(group, SWT.NONE);
		lbtBranch.setText("Create from branch:");

		GridData dataB = new GridData(GridData.FILL_HORIZONTAL);
		dataB.horizontalSpan = 3;

		Text branch = new Text(group, SWT.BORDER);
		branch.setLayoutData(dataB);

		if(projectNode.get("gitBranch",null) != null) {
			branch.setText(projectNode.get("gitBranch",null).toString());
			branchString = branch.getText();
		}
		branch.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {

				branchString = branch.getText();

			}
		});

		Label lbtDescription = new Label(group, SWT.NONE);
		lbtDescription.setText("Commit Message:");

		GridData dataD = new GridData(GridData.FILL_HORIZONTAL);
		dataD.horizontalSpan = 3;

		Text description = new Text(group, SWT.BORDER);
		description.setLayoutData(dataD);
		description.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {

				commitMsg = description.getText();
				valiteFields();
			}
		});

		Label lbtTTLFile = new Label(group, SWT.NONE);
		lbtTTLFile.setText("File to push: ");
		Text TTlFile = new Text(group, SWT.BORDER);
		TTlFile.setLayoutData(dataD);
		TTlFile.setText(dataProject.getTurtleFile());
		TTlFile.setEnabled(false);
		GridData dataT = new GridData(GridData.FILL_HORIZONTAL);
		dataT.horizontalSpan = 3;
		TTlFile.setLayoutData(dataT);


	}

	private void createGroupAuth(Composite container) {

		Preferences projectNode = getProjectPreferences();

		Group group = new Group(container, SWT.NONE);
		group.setText("Authentication");

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


		Label lbtUser = new Label(group, SWT.NONE);
		lbtUser.setText("User: ");

		Text user = new Text(group, SWT.BORDER);	
		GridData dataU = new GridData(GridData.FILL_HORIZONTAL);
		dataU.horizontalSpan = 3;
		user.setLayoutData(dataU);
		if(projectNode.get("gitUsername",null) != null) {
			user.setText(projectNode.get("gitUsername",null).toString());
			userString = user.getText();
		}
		user.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {

				userString = user.getText();
				valiteFields();

			}
		});


		Label lbtPwd = new Label(group, SWT.NONE);
		lbtPwd.setText("Password: ");

		Text pwd = new Text(group, SWT.BORDER);
		pwd.setEchoChar('*');
		GridData dataP = new GridData(GridData.FILL_HORIZONTAL);
		dataP.horizontalSpan = 2;
		pwd.setLayoutData(dataP);
		if(projectNode.get("gitPwd",null) != null) {
			pwd.setText(projectNode.get("gitPwd",null).toString());
			pwdString = pwd.getText();
		}
		pwd.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {

				pwdString = pwd.getText();
				valiteFields();

			}
		});


		eyeButton = new Button(group, SWT.TOGGLE);
		eyeButton.setImage(Icons.EYE_DISABLED.image());

		SelectionListener selectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if (eyeButton.getSelection()) {
					//System.out.println("Reveal pwd");
					pwd.setEchoChar('\0');
					eyeButton.setImage(Icons.EYE_ENABLED.image());					

				} else {
					//System.out.println("Hide pwd");
					pwd.setEchoChar('*');					
					eyeButton.setImage(Icons.EYE_DISABLED.image());		
					

				}

			}
		};

		eyeButton.addSelectionListener(selectionListener);


	}
	
	

	@Override
	protected boolean isResizable() {
		return true;
	}


	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

	@Override
	protected void okPressed() {
		super.okPressed();

		if(MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Confirm", "Do you want to push " + dataProject.getTurtleFile() + " to the Git Repository?")) {

			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException {
					monitor.beginTask("Push to " + repositoryString + " ...", 5);
					// Git Push
					//create git folder 
					File gitFolder = new File(dataProject.getProjectFolder() + dataProject.getProjectName() + "/"+"git");
					if(!gitFolder.exists()) {
						gitFolder.mkdir();
					}

					// create new instance
					GitModule.getInstance().setUp(gitFolder, userString, pwdString, repositoryString, branchString);
					monitor.worked(1);

					try {
						GitModule.getInstance().cloneRemoteRepository();
					} catch (InvalidRemoteException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						throw new InvocationTargetException(e,e.toString());
					} catch (TransportException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						throw new InvocationTargetException(e,e.toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						throw new InvocationTargetException(e,e.toString());
					} catch (GitAPIException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						throw new InvocationTargetException(e,e.toString());
					}
					monitor.worked(1);

					if(GitModule.getInstance().getGit()== null) {
						GitModule.getInstance().openLocalRepository();
					}

					//copy TTl file into git folder

					try {
						FileUtils.copyFileToDirectory(new File(dataProject.getTurtleFile()), gitFolder);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
						throw new InvocationTargetException(e,e.toString());
					}
					monitor.worked(1);

					GitModule.getInstance().commitAndPush(commitMsg);
					monitor.worked(2);
					monitor.done();

				}
			};


			IWorkbenchWindow win  = PlatformUI.getWorkbench().getActiveWorkbenchWindow();			

			try {
				new ProgressMonitorDialog(win.getShell()).run(true, false, op);
			} catch (InvocationTargetException | InterruptedException e) {
				// TODO Auto-generated catch block
				MessageDialog.openError(win.getShell(), "An error occured pushing the file to the Git Repository!", e.toString());
				return;
			}
			

			if(savePrefs) {
				savePreferences();
			} else {
				resetPreferences();
			}
			
			MessageDialog.openInformation(win.getShell(), "Git Push", "The file has been pushed to the Git Repository.");
		}

	}


	private void resetPreferences() {
		Preferences projectNode = getProjectPreferences();

		projectNode.remove("gitRepository");
		projectNode.remove("gitUsername");
		projectNode.remove("gitPwd");
		projectNode.remove("gitBranch");
		try {
			projectNode.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private void savePreferences() {

		Preferences projectNode = getProjectPreferences();

		projectNode.put("gitRepository", repositoryString);
		projectNode.put("gitUsername", userString);
		projectNode.put("gitPwd", pwdString);
		projectNode.put("gitBranch", branchString);
		try {
			projectNode.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Preferences getProjectPreferences() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project= root.getProject(dataProject.getProjectName());

		IScopeContext projectScope = new ProjectScope(project);
		Preferences projectNode = projectScope.getNode(project.getName());
		return projectNode;
	}


	private boolean valiteFields() {
		if(!repositoryString.isEmpty() && !repositoryString.isBlank() && 				
				!userString.isEmpty() && !userString.isBlank() &&
				!pwdString.isEmpty() && !pwdString.isBlank() &&
				!commitMsg.isEmpty() && !commitMsg.isBlank()) {
			getButton(IDialogConstants.OK_ID).setEnabled(true);
			setErrorMessage(null);
			return true;

		} else if(repositoryString.isEmpty() || repositoryString.isBlank()){
			setErrorMessage("Repository cannot be empty");
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		} else if(commitMsg.isEmpty() || commitMsg.isBlank()){
			setErrorMessage("Commit description cannot be empty");
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		} else if(userString.isEmpty() || userString.isBlank()){
			setErrorMessage("Username cannot be empty");
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		} else if(pwdString.isEmpty() || pwdString.isBlank()){
			setErrorMessage("Password cannot be empty");
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
		return false;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);

		Button ok = getButton(IDialogConstants.OK_ID);
		ok.setText("Push");
		setButtonLayoutData(ok);

		Button cancel = getButton(IDialogConstants.CANCEL_ID);
		cancel.setText("Cancel");
		setButtonLayoutData(cancel);
	}


}
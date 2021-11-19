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
package org.epos.metadata.editor.engine;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FileUtils;
import org.topbraid.jenax.progress.SimpleProgressMonitor;

public class GitModule {

	private static GitModule gitmodule;

	private File repositoryFolder;
	private Repository repository;
	private String repositoryURL;
	private String username;
	private String password;
	private String branch;
	private UsernamePasswordCredentialsProvider credentialProvider;
	private Git git = null;


	public static GitModule getInstance() {
		if(gitmodule==null) gitmodule = new GitModule();
		return gitmodule;
	}

	private GitModule() {}


	public void setUp(File repositoryFolder, String username, String password, String repositoryURL, String branch) {

		this.repositoryFolder = repositoryFolder;
		this.username = username;
		this.password = password;
		this.repositoryURL = repositoryURL;
		this.branch = branch;
		credentialProvider = new UsernamePasswordCredentialsProvider(username, password);

	}

	private void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDir(f);
			}
		}
		file.delete();
	}

	/**
	 * 
	 * Clone and open a remote repository
	 * @throws IOException 
	 * @throws GitAPIException 
	 * @throws TransportException 
	 * @throws InvalidRemoteException 
	 * 
	 */
	public void cloneRemoteRepository() throws IOException, InvalidRemoteException, TransportException, GitAPIException {

		deleteDir(repositoryFolder);

		if(branch.isBlank()) {
			try (Git result = Git.cloneRepository()
					.setURI(repositoryURL)
					.setDirectory(repositoryFolder)
					.setCredentialsProvider(credentialProvider)
					.setProgressMonitor(new SimpleProgressMonitor())
					.call()) {
				// Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
				////System.out.println("Having repository: " + result.getRepository().getDirectory());

				git = result;
			}

		}else {
			try (Git result = Git.cloneRepository()
					.setURI(repositoryURL)
					.setDirectory(repositoryFolder)
					.setCredentialsProvider(credentialProvider)
					.setProgressMonitor(new SimpleProgressMonitor())
					.setBranch(branch)
					.call()) {
				// Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
				////System.out.println("Having repository: " + result.getRepository().getDirectory());

				git = result;
			}
		}

		if(git!=null) {
			String branchName =  username.split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmm");  
			LocalDateTime now = LocalDateTime.now();  
			branchName+="-"+dtf.format(now)+"-shapeness";
			try {
				git.checkout()
				.setCreateBranch(true)
				.setName(branchName)
				.call();
			} catch (GitAPIException e) {
				//System.out.println("Error "+e.getMessage());
				try {
					git.checkout()
					.setName(branchName)
					.call();
				} catch (GitAPIException e1) {
					e1.printStackTrace();
				}
			}
		}

		try {
			if(git!=null) git.pull().setCredentialsProvider(credentialProvider).call();
		}catch(RefNotAdvertisedException e) {}

		if(git!=null) repository = git.getRepository();

	}

	/**
	 * 
	 * Open the local repository
	 * 
	 */
	public void openLocalRepository() {


		if(git!=null) {
			try {
				git.checkout()
				.setCreateBranch(true)
				.setName(username+"-metadata-editor")
				.call();
			} catch (GitAPIException e) {
				//System.out.println("Error "+e.getMessage());
				try {
					git.checkout()
					.setName(username+"-metadata-editor")
					.call();
				} catch (GitAPIException e1) {
					e1.printStackTrace();
				}
			}
		}

		if(git == null) {
			try {
				git = Git.open(repositoryFolder);
			} catch (IOException e) {
				//System.out.println("Error "+e.getMessage());
			}
			try {
				git.pull().setCredentialsProvider(credentialProvider).call();
			}catch(GitAPIException e) {}
		}

		repository = git.getRepository();
	}

	/**
	 * 
	 * Add, Commit, Push
	 * 
	 * @param commitMessage
	 */
	public void commitAndPush(String commitMessage) {

		try {
			git.add().addFilepattern(".").call();
		} catch (GitAPIException e) {
			//System.out.println("Error "+e.getMessage());
		}

		try {
			git.commit().setAll(true).setMessage(commitMessage).call();
		} catch (GitAPIException e) {
			//System.out.println("Error "+e.getMessage());
		}
		//System.out.println("Committed file metadata editor to repository at " + git.getRepository().getDirectory());


		try {
			git.push().setCredentialsProvider(credentialProvider).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		//System.out.println("Push file metadata editor to repository at " + git.getRepository().getDirectory());
	}


	public File getRepositoryFolder() {
		return repositoryFolder;
	}


	public void setRepositoryFolder(File repositoryFolder) {
		this.repositoryFolder = repositoryFolder;
	}


	public Repository getRepository() {
		return repository;
	}


	public void setRepository(Repository repository) {
		this.repository = repository;
	}


	public String getRepositoryURL() {
		return repositoryURL;
	}


	public void setRepositoryURL(String repositoryURL) {
		this.repositoryURL = repositoryURL;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public UsernamePasswordCredentialsProvider getCredentialProvider() {
		return credentialProvider;
	}


	public void setCredentialProvider(UsernamePasswordCredentialsProvider credentialProvider) {
		this.credentialProvider = credentialProvider;
	}


	public Git getGit() {
		return git;
	}


	public void setGit(Git git) {
		this.git = git;
	}

	private static class SimpleProgressMonitor implements ProgressMonitor {
		@Override
		public void start(int totalTasks) {
			//System.out.println("Starting work on " + totalTasks + " tasks");
		}

		@Override
		public void beginTask(String title, int totalWork) {
			//System.out.println("Start " + title + ": " + totalWork);
		}

		@Override
		public void update(int completed) {
			System.out.print(completed + "-");
		}

		@Override
		public void endTask() {
			//System.out.println("Done");
		}

		@Override
		public boolean isCancelled() {
			return false;
		}
	}

}

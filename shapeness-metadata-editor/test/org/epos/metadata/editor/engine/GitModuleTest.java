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
import java.io.PrintWriter;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public class GitModuleTest {

	public static void main(String[] args) throws InvalidRemoteException, TransportException, IOException, GitAPIException {
		GitModule.getInstance()
				.setUp(new File("gitTest"), "valerio.vinciarelli@ingv.it", "", "https://gitlab.rm.ingv.it/daniele.bailo/intelligent-metadata-editor.git", "master");
	
		GitModule.getInstance().cloneRemoteRepository();
		
		//GitModule.getInstance().openLocalRepository();
		
		
		

        File myFile = new File(GitModule.getInstance().getRepository().getDirectory().getParent(), "testfile2");
        if(!myFile.createNewFile()) {
            throw new IOException("Could not create file " + myFile);
        }
        
        try(PrintWriter writer = new PrintWriter(myFile)) {
            writer.append("Hello, world! - ancò");
        }
		
		GitModule.getInstance().commitAndPush("CIAONE");
	}
}

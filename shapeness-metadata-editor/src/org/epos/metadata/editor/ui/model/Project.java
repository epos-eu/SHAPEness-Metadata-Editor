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
package org.epos.metadata.editor.ui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

public class Project {
	
	private String projectName = null;
	private String projectFolder = null;
	private String urlShacl = null;
	private String turtleFile = null;
	private boolean additionalVocabularies;
	private boolean isNewFile;
	private Map<Class, Image> shapesList = new HashMap<Class,Image>(); 

	public String getUrlShacl() {
		return urlShacl;
	}

	public void setUrlShacl(String urlShacl) {
		this.urlShacl = urlShacl;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectFolder() {
		return projectFolder;
	}

	public void setProjectFolder(String projectFolder) {
		this.projectFolder = projectFolder;
	}

	public String getTurtleFile() {
		return turtleFile;
	}

	public void setTurtleFile(String turtleFile) {
		this.turtleFile = turtleFile;
	}

	public Map<Class, Image> getShapesList() {
		return shapesList;
	}

	public void setShapesList(Map<Class, Image> shapesList) {
		this.shapesList = shapesList;
	}

	public boolean isAdditionalVocabularies() {
		return additionalVocabularies;
	}

	public void setAdditionalVocabularies(boolean additionalVocabularies) {
		this.additionalVocabularies = additionalVocabularies;
	}

	public boolean isNewFile() {
		return isNewFile;
	}

	public void setNewFile(boolean isNewFile) {
		this.isNewFile = isNewFile;
	}

	@Override
	public String toString() {
		return "Project [projectName=" + projectName + ", projectFolder=" + projectFolder + ", urlShacl=" + urlShacl
				+ ", turtleFile=" + turtleFile + ", additionalVocabularies=" + additionalVocabularies + ", isNewFile="
				+ isNewFile + ", shapesList=" + shapesList + "]";
	}

    
}


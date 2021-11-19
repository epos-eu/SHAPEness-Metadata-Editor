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
package org.epos.metadata.editor.reflection.shapes;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.engine.converters.geometries.AbstractGeometry;
import org.epos.metadata.editor.engine.utils.Shacl;
import org.epos.metadata.editor.ui.utils.Util;

public class Node implements Serializable {

	private String id;
	private String nodeName;

	public Node(String id, String name) {
		this.id = id;
		this.nodeName = name;
	}

	public String getId() {
		return id;
	}

	public String getNodeName() {
		return nodeName;
	}

	public Individual serialize(Model m, OntModel om, String uri, Object o) {
		return null;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	public boolean isEmpty(){
		if(this.id == null && this.nodeName == null) {
			return true;
		}
		return false;
	}

	public Node copy() throws IllegalAccessException, InstantiationException {
	    
	    String IDvalue = Engine.getIstance().getMs().getRootOntology() + this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".")+1, this.getClass().getName().length()) + "/" + UUID.randomUUID();
	    Node newNode = Util.createNewObjClass(this.getClass().getName(),"",IDvalue);
	    setFields(this, newNode);
	    return newNode;
	}
	
	private void setFields(Object from, Object to) {
        Field[] fields = from.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                Field fieldFrom = from.getClass().getDeclaredField(field.getName());
                fieldFrom.setAccessible(true);
                Object value = fieldFrom.get(from);
                
                Field fieldTo = to.getClass().getDeclaredField(field.getName());
                fieldTo.setAccessible(true);
                
                if(fieldFrom.getType().equals(List.class) && value != null) {
                	List copy = new ArrayList();
                	copy.addAll((List)value);
                	fieldTo.set(to, copy);
                } else {
                	fieldTo.set(to, value);
                }
                
 
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }


}

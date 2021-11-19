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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ClassUtils;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.epos.metadata.editor.reflection.shapes.Node;

public class DataManager implements IStructuredContentProvider{

	private static DataManager istance = null;
	private ArrayList<Node> nodes = new ArrayList<Node>();
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private Map<Class,ViewerFilter> filters = new HashMap<Class, ViewerFilter>();

	private DataManager() {} 

	public static DataManager getIstance() {
		if(istance==null)
			istance = new DataManager();
		return istance;
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}


	public ArrayList<Node> getNodesByType(Type type) {
		ArrayList<Node> list = new ArrayList<Node>();
		for (Node node : nodes) {
			try {
				if(ClassUtils.getClass(type.getTypeName()).isInstance(node)){
					list.add(node);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public ArrayList<Node> getNodesByType(String typeName) {
		ArrayList<Node> list = new ArrayList<Node>();
		for (Node node : nodes) {
			try {
				if(ClassUtils.getClass(typeName).isInstance(node)){
					list.add(node);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	
	public ArrayList<Node> getNodesByType(List<String> listOfType) {
		ArrayList<Node> list = new ArrayList<Node>();
		for (Node node : nodes) {
			for (String type : listOfType) {
				try {

					if(ClassUtils.getClass(type).isInstance(node)){
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

	
	/*
	public Node getNode(String nodeString) {

		for (Node node : nodes) {
			String text = node.getNodeName()+"-"+node.getId();
			if(text.equals(nodeString))
				return node;
		}
		return null;
	}
	*/

	public Node getNode(Object obj) {

		for (Node node : nodes) {

			if(node == obj)
				return node;
		}
		return null;
	}

	public Node getNodeById(String nodeId) {

		for (Node node : nodes) {
			if(node.getId().equals(nodeId))
				return node;
		}
		return null;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		// return nodes.toArray();
		return ListUtils.union(nodes , connections).toArray();

	}

	public ArrayList<Connection> getConnections() {
		return connections;
	}

	public ArrayList<Connection> getConnectionsSourceNode(Node source) {
		ArrayList<Connection> list = new ArrayList<Connection>();
		for (Connection connection : connections) {
			if(connection.getSource() == source) {
				list.add(connection);

			}
		}
		return list;
	}

	public ArrayList<Node> getNodesDestinationFromSourceAndField(Node source, String fieldName) {
		ArrayList<Node> list = new ArrayList<Node>();
		for (Connection connection : connections) {
			if(connection.getSource() == source && connection.getLabel().equals(fieldName)) {
				list.add(connection.getDestination());

			}
		}
		return list;
	}

	public ArrayList<Connection> getConnectionsDestination(Node destination) {
		ArrayList<Connection> list = new ArrayList<Connection>();
		for (Connection connection : connections) {
			if(connection.getDestination() == destination) {
				list.add(connection);

			}
		}
		return list;
	}

	public ArrayList<Connection> getConnectionsSourceDestination(Node source, Node destination) {
		ArrayList<Connection> list = new ArrayList<Connection>();
		for (Connection connection : connections) {
			if(connection.getSource() == source && connection.getDestination() == destination) {
				list.add(connection);

			}
		}
		return list;
	}
	
	public Connection getConnection(String label, Node source, Node destination) {
		
		for (Connection connection : connections) {
			////System.out.println("connection in list: " + connection.getLabel() + " " + connection.getSource().hashCode() + " " + connection.getDestination().hashCode());
			if(connection.getLabel().equals(label) && connection.getSource() == source && connection.getDestination() == destination) {
				return connection;

			}
		}
		return null;
	}



	public ArrayList<Node> getNodesConnectedToSourceNode(Node source) {
		ArrayList<Node> list = new ArrayList<Node>();
		for (Connection connection : connections) {
			if(connection.getSource() == source) {

				Node destination = connection.getDestination();
				list.add(destination);

			}
		}
		return list;
	}

	public boolean removeConnection(String label, Node source, Node destination) {
		for (Connection connection : connections) {
			if(connection.getLabel().contentEquals(label) && 
					connection.getSource() == source  && 
					connection.getDestination() == destination) {

				return connections.remove(connection);
			}
		}
		return false;
	}
	
	public boolean removeConnection(Connection connection) {
		return connections.remove(connection);
			
	}



	public List<Connection> removeConnectionWithDestination(Node destination) {
		ArrayList<Connection> list = new ArrayList<Connection>();
		for(Iterator<Connection> itr = connections.iterator(); itr.hasNext();){
			Connection connection = itr.next();
			if(connection.getDestination() == destination) {
				list.add(connection);
				itr.remove();

			}
		}
		return list;
	}
	
	public void removeConnectionWithSource(Node source) {
		
		for(Iterator<Connection> itr = connections.iterator(); itr.hasNext();){
			Connection connection = itr.next();
			if(connection.getSource() == source) {
				itr.remove();

			}
		}
		
	}
	
	public boolean checkIDNode(String IDNode) {
		for (Node node : nodes) {
			
			if(node.getId().equals(IDNode))
				return true;
		}
		return false;
	}

	public Connection getConnectionByEntityConnectionData(EntityConnectionData entityConnectionData) {
		for (Connection connection : connections) {
			////System.out.println("connection in list: " + connection.getLabel() + " " + connection.getSource().hashCode() + " " + connection.getDestination().hashCode());
			if(connection.getEntityConnectionData() == entityConnectionData) {
				return connection;

			}
		}
		return null;
		
	}

	public Map<Class, ViewerFilter> getFilters() {
		return filters;
	}
	
	


}

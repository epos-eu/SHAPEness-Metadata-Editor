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
package org.epos.metadata.editor.engine.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

public class Utils {
	public static List<Resource> explodeAnonymousResource(Resource resource)
	{
		List<Property> collectionProperties = new LinkedList<Property>(Arrays.asList(OWL.unionOf,OWL.intersectionOf,RDF.first,RDF.rest));

		List<Resource> resources=new LinkedList<Resource>();
		Boolean needToTraverseNext=false;

		if(resource.isAnon())
		{
			for(Property cp:collectionProperties)
			{
				if(resource.hasProperty(cp) && !resource.getPropertyResourceValue(cp).equals(RDF.nil))
				{
					Resource nextResource=resource.getPropertyResourceValue(cp);
					resources.addAll(explodeAnonymousResource(nextResource));
					needToTraverseNext=true;
				}
			}
			if(!needToTraverseNext)
			{
				resources.add(resource);
			}
		}
		else
		{
			resources.add(resource);
		}
		return resources;
	}
}

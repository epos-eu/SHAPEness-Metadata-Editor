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
package org.epos.metadata.editor.engine.converters.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import org.topbraid.shacl.vocabulary.SH;

/**
 * 
 * This class implements a static method to merge duplicate objects read by the model and generate a single object
 * 
 * 
 * @author valerio
 *
 */
public class ItemMerge {

	private static final Logger logger = Logger.getGlobal();
	
	public static HashMap<String,ArrayList<String>> mergeProperties(HashMap<String,ArrayList<String>> item){
		logger.info("Merging duplicates in single elements.... ");

		HashMap<String,ArrayList<String>> returnItem = new HashMap<String, ArrayList<String>>();

		item.entrySet().forEach(it->{
			Optional<String> element = it.getValue().stream().filter(e->e.contains(SH.path.toString())).findAny();
			boolean put = false;
			if(returnItem.containsKey(it.getKey())) {
				returnItem.get(it.getKey()).clear();
				returnItem.get(it.getKey()).addAll(new HashSet<String>(it.getValue()));
				put = true;
			}
			if(!element.isEmpty() && !put) {

				Set<String> temp = new HashSet<String>(it.getValue());
				String key = "";
				for(Entry<String, ArrayList<String>> it2 : returnItem.entrySet()) {
					if(it2.getValue().contains(element.get())) {
						temp.addAll(it2.getValue());
						key = it2.getKey();
					}
				}
				returnItem.remove(key);
				returnItem.put(it.getKey(), new ArrayList<String>(temp));
				put=true;
			}
			if(!put) {
				returnItem.put(it.getKey(), it.getValue());
			}
		});

		return returnItem;
	}

}

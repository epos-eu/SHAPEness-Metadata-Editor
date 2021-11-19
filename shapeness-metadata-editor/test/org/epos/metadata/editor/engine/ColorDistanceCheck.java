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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.swt.widgets.Display;

public class ColorDistanceCheck {

	public static void main(String[] args) {
		ColorDistanceCheck cdc = new ColorDistanceCheck();
		for(int i = 0; i<20;i++)
			cdc.randomColor();
	}
	private ArrayList<Color> colors = new ArrayList<Color>();
	private int high = 200;
	private int low = 100;

	public org.eclipse.swt.graphics.Color randomColor() {
		Color tColor = null;

		while(true) {
			float red = ThreadLocalRandom.current().nextFloat();
			float green = ThreadLocalRandom.current().nextFloat();
			float blue = ThreadLocalRandom.current().nextFloat();

			tColor = new Color(red,green,blue);
			tColor = tColor.brighter();
			
			//System.out.println(tColor);

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
			if(colorDistance(tColor, c) > 180.0){
				colors.add(tColor);
				return true;
			}
		}
		return false;
	}


	private double colorDistance(Color leftColor, Color rightColor) {
		double value = Math.pow(leftColor.getRed()-rightColor.getRed(),2)+Math.pow(leftColor.getBlue()-rightColor.getBlue(),2)+Math.pow(leftColor.getGreen()-rightColor.getGreen(),2);
		double distance = Math.sqrt(value);
		//System.out.println("Dist: "+distance);
		return distance;
	}

}

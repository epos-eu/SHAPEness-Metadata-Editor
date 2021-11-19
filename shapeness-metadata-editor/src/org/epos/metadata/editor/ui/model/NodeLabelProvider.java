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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.ClassUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.Animation;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.viewers.IFigureProvider;
import org.eclipse.zest.core.viewers.ISelfStyleProvider;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.epos.metadata.editor.reflection.shapes.Node;
import org.epos.metadata.editor.ui.utils.Icons;
import org.epos.metadata.editor.ui.utils.Util;
import org.eclipse.draw2d.Label;


public class NodeLabelProvider extends LabelProvider implements ISelfStyleProvider{


	@Override
	public Image getImage(Object element) {
		if (element instanceof Node) {
			Node node = (Node) element;
		
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			Set<ConstraintViolation<Object>> constraintViolations =
					validator.validate(node);
			
			List<?> mandatory = constraintViolations
					.stream()
					.filter(e->e.getMessageTemplate()
							//.equals("{Invalid.Mandatory}"))
							.equals("{javax.validation.constraints.NotEmpty.message}"))
					.collect(Collectors.toList());
			System.out.println("********************constraintViolations*********" + constraintViolations);
			
			
			if(!mandatory.isEmpty()){
				System.out.println("********************Invalid.Mandatory*********");
				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_OBJS_ERROR_TSK);
			}
			
			List<?> recommended = constraintViolations
					.stream()
					.filter(e->e.getMessageTemplate()
							.equals("{Invalid.Recommended}"))
					.collect(Collectors.toList());

			if(!recommended.isEmpty()) {
				System.out.println("********************Invalid.Recommended*********");
				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_OBJS_WARN_TSK);
			}
			
			
		}
		return null;
	}

	@Override
	public String getText(Object element) {

		if (element instanceof Node) {
			Node myNode = (Node) element;
			//return myNode.getNodeName()+"-"+myNode.getId();
			return myNode.getNodeName();
		}

		if (element instanceof EntityConnectionData) {
			String labelConnection = "";
			EntityConnectionData ecd = (EntityConnectionData) element;
			List<Connection> connections = DataManager.getIstance().getConnectionsSourceDestination((Node)ecd.source, (Node)ecd.dest);
			for (Connection connection : connections) {
				labelConnection +=connection.getLabel().replace("_", ":") + "\n";
				connection.setEntityConnectionData((EntityConnectionData) element);

			}

			return labelConnection;
		}
		return null;

	}

	@Override
	public void selfStyleConnection(Object element, GraphConnection connection) {
		// TODO Auto-generated method stub
		FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];
		fontData.height = 12;
		connection.setFont(new Font(null, fontData));

	}

	@Override
	public void selfStyleNode(Object element, GraphNode node) {

		Node nodeObj = (Node) element;
		node.setData("node", element);

		Color colorNode = new Color(Display.getDefault(), Util.searchRGBColorForNode(nodeObj));
		node.setBackgroundColor(colorNode);
		node.setForegroundColor(new Color(Display.getDefault(), 0, 0, 0));
		node.setBorderWidth(3);
		node.setBorderColor(colorNode);

		node.setTooltip(new Label(nodeObj.getNodeName()+"\n<"+nodeObj.getId()+">"));

		node.setHighlightColor(colorNode);
		node.setBorderHighlightColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		

		FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];
		fontData.height = 15;
		//fontData.style = SWT.BOLD;
		node.setFont(new Font(null, fontData));
		
	}
	


}

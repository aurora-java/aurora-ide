/******************************************************************************* 
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 * @author Innar Made
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.features.choreography;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.graphiti.mm.PropertyContainer;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

public class ChoreographySelectionBehavior {

	public static boolean canApplyTo(PictogramElement pe) {
		if (!(pe instanceof PropertyContainer)) {
			return false;
		}

		String property = Graphiti.getPeService().getPropertyValue(pe, ChoreographyUtil.MESSAGE_LINK);
		if (property == null) {
			return false;
		}

		return new Boolean(property);
	}

	public static GraphicsAlgorithm[] getClickArea(PictogramElement element) {
		Collection<PictogramElement> children = Graphiti.getPeService().getPictogramElementChildren(element);
		PictogramElement first = children.iterator().next();
		return new GraphicsAlgorithm[] { first.getGraphicsAlgorithm() };
	}

	public static GraphicsAlgorithm getSelectionBorder(PictogramElement element) {
		Collection<Shape> children = Graphiti.getPeService().getAllContainedShapes((ContainerShape) element);
		PictogramElement first = children.iterator().next();
		return first.getGraphicsAlgorithm();
	}

}
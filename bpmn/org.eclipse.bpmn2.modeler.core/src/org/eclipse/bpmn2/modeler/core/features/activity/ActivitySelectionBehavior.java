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
 * @author Ivar Meikas
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features.activity;

import java.util.Collection;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

// TODO: Auto-generated Javadoc
/**
 * The Class ActivitySelectionBehavior.
 */
public class ActivitySelectionBehavior {

	/**
	 * Can apply to.
	 *
	 * @param element the element
	 * @return true, if successful
	 */
	public static boolean canApplyTo(PictogramElement element) {
		if (element.getLink() == null || !(element instanceof ContainerShape) || FeatureSupport.isLabelShape(element)) {
			return false;
		}

		EList<EObject> objects = element.getLink().getBusinessObjects();

		for (EObject eObject : objects) {
			if (eObject instanceof Activity) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the click area.
	 *
	 * @param element the element
	 * @return the click area
	 */
	public static GraphicsAlgorithm[] getClickArea(PictogramElement element) {
		Collection<PictogramElement> children = Graphiti.getPeService().getPictogramElementChildren(element);
		PictogramElement first = children.iterator().next();
		return new GraphicsAlgorithm[] { first.getGraphicsAlgorithm() };
	}

	/**
	 * Gets the selection border.
	 *
	 * @param element the element
	 * @return the selection border
	 */
	public static GraphicsAlgorithm getSelectionBorder(PictogramElement element) {
		Collection<PictogramElement> children = Graphiti.getPeService().getPictogramElementChildren(element);
		PictogramElement first = children.iterator().next();
		return first.getGraphicsAlgorithm();
	}
}
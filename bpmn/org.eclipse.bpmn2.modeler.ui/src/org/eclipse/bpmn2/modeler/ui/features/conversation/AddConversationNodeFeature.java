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
package org.eclipse.bpmn2.modeler.ui.features.conversation;

import org.eclipse.bpmn2.ConversationNode;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.label.AddShapeLabelFeature;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;

public abstract class AddConversationNodeFeature<T extends ConversationNode> extends AbstractBpmn2AddFeature<T> {

	public AddConversationNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	public IAddFeature getAddLabelFeature(IFeatureProvider fp) {
		return new AddShapeLabelFeature(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		return context.getTargetContainer().equals(getDiagram());
	}

	@Override
	public PictogramElement add(IAddContext context) {
		IGaService gaService = Graphiti.getGaService();
		IPeService peService = Graphiti.getPeService();
		T businessObject = getBusinessObject(context);

		int width = this.getWidth(context);
		int height = this.getHeight(context);

		ContainerShape containerShape = peService.createContainerShape(context.getTargetContainer(), true);
		Rectangle rect = gaService.createInvisibleRectangle(containerShape);
		gaService.setLocationAndSize(rect, context.getX(), context.getY(), width, height);

		Shape hexShape = peService.createShape(containerShape, false);
		int w_5th = width / 5;
		int[] xy = { w_5th, 0, w_5th * 4, 0, width, height / 2, w_5th * 4, height, w_5th, height, 0, height / 2 };
		Polygon hexagon = gaService.createPolygon(hexShape, xy);
		StyleUtil.applyStyle(hexagon, businessObject);
		link(containerShape, businessObject);

		boolean isImport = context.getProperty(GraphitiConstants.IMPORT_PROPERTY) != null;

		createDIShape(containerShape, businessObject, !isImport);
		
		// hook for subclasses to inject extra code
		((AddContext)context).setWidth(width);
		((AddContext)context).setHeight(height);
		decorateShape(context, containerShape, businessObject);

		peService.createChopboxAnchor(containerShape);
		AnchorUtil.addFixedPointAnchors(containerShape, rect);

		return containerShape;
	}
}
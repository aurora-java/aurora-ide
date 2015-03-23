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


import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.activity.UpdateActivityLoopAndMultiInstanceMarkerFeature.LoopCharacteristicType;
import org.eclipse.bpmn2.modeler.core.features.label.AddShapeLabelFeature;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractAddActivityFeature.
 *
 * @param <T> the generic type
 */
public abstract class AbstractAddActivityFeature<T extends Activity>
	extends AbstractBpmn2AddFeature<T> {
	
	/**
	 * Instantiates a new abstract add activity feature.
	 *
	 * @param fp the fp
	 */
	public AbstractAddActivityFeature(IFeatureProvider fp) {
		super(fp);
	}

	public IAddFeature getAddLabelFeature(IFeatureProvider fp) {
		return new AddShapeLabelFeature(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IAdd#canAdd(org.eclipse.graphiti.features.context.IAddContext)
	 */
	@Override
	public boolean canAdd(IAddContext context) {
		return FeatureSupport.isValidFlowElementTarget(context);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
	 */
	@Override
	public PictogramElement add(IAddContext context) {
		T businessObject = getBusinessObject(context);

		int width = getWidth(context);
		int height = getHeight(context);
		
		GraphicsAlgorithm targetAlgorithm = context.getTargetContainer().getGraphicsAlgorithm();
		
		if (targetAlgorithm != null) {
			width = Math.min(targetAlgorithm.getWidth(), width);
			height = Math.min(targetAlgorithm.getHeight(), height);
		}
		
		adjustLocation(context,width,height);
		
		int x = context.getX();
		int y = context.getY();

		ContainerShape containerShape = peService.createContainerShape(context.getTargetContainer(), true);
		Rectangle invisibleRect = gaService.createInvisibleRectangle(containerShape);
		gaService.setLocationAndSize(invisibleRect, x, y, width, height);

		Shape rectShape = ShapeDecoratorUtil.createActivityBorder(containerShape, businessObject);
		
		boolean isImport = context.getProperty(GraphitiConstants.IMPORT_PROPERTY) != null;
		createDIShape(containerShape, businessObject, !isImport);

		peService.setPropertyValue(containerShape, GraphitiConstants.IS_COMPENSATE_PROPERTY, Boolean.toString(false));
		peService.setPropertyValue(containerShape, GraphitiConstants.IS_LOOP_OR_MULTI_INSTANCE, LoopCharacteristicType.NULL.getName());

		// hook for subclasses to inject extra code
		((AddContext)context).setWidth(width);
		((AddContext)context).setHeight(height);
		decorateShape(context, containerShape, businessObject);
		
		peService.createChopboxAnchor(containerShape);
		AnchorUtil.addFixedPointAnchors(containerShape, rectShape.getGraphicsAlgorithm());

		splitConnection(context, containerShape);
		
		return containerShape;
	}

	/**
	 * Gets the marker container offset.
	 *
	 * @return the marker container offset
	 */
	protected int getMarkerContainerOffset() {
		return 0;
	}
}
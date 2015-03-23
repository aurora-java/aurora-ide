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
package org.eclipse.bpmn2.modeler.ui.features.event;

import static org.eclipse.bpmn2.modeler.ui.features.event.BoundaryEventFeatureContainer.BOUNDARY_EVENT_CANCEL;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.IFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.event.AbstractUpdateEventFeature;
import org.eclipse.bpmn2.modeler.core.features.label.AddShapeLabelFeature;
import org.eclipse.bpmn2.modeler.core.features.label.LabelFeatureContainer;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.BoundaryEventPositionHelper;
import org.eclipse.bpmn2.modeler.core.utils.BoundaryEventPositionHelper.PositionOnLine;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;

public class AddBoundaryEventFeature extends AbstractBpmn2AddFeature<BoundaryEvent> {

	public static final String BOUNDARY_EVENT_RELATIVE_Y = "boundary.event.relative.y"; //$NON-NLS-1$

	private final IPeService peService = Graphiti.getPeService();
	private final IGaService gaService = Graphiti.getGaService();

	public AddBoundaryEventFeature(IFeatureProvider fp) {
		super(fp);
	}

	public IAddFeature getAddLabelFeature(IFeatureProvider fp) {
		return new AddShapeLabelFeature(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (!(getBusinessObject(context) instanceof BoundaryEvent)) {
			return false;
		}

		Object prop = context.getProperty(GraphitiConstants.IMPORT_PROPERTY);
		if (prop != null && (Boolean) prop) {
			return true;
		}

		Object bo = getBusinessObjectForPictogramElement(context.getTargetContainer());
		return bo != null && bo instanceof Activity;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		BoundaryEvent businessObject = getBusinessObject(context);

		boolean isImport = context.getProperty(GraphitiConstants.IMPORT_PROPERTY) != null;
		// FIXME: what's going on here?
		ContainerShape target = isImport ? context.getTargetContainer() : (ContainerShape) context
		        .getTargetContainer().eContainer();
		if (target==null)
			target = context.getTargetContainer();
		ContainerShape containerShape = peService.createContainerShape(target, true);
		Ellipse ellipse = gaService.createEllipse(containerShape);
		StyleUtil.applyStyle(ellipse, businessObject);
		
		int width = this.getWidth(context);
		int height = this.getHeight(context);


		if (isImport) { // if loading from DI then place according to context
			gaService.setLocationAndSize(ellipse, context.getX(), context.getY(), width, height);
		} else { // otherwise place it in the center of shape for user to adjust it
			GraphicsAlgorithm ga = context.getTargetContainer().getGraphicsAlgorithm();
			int x = ga.getX() + context.getX() - (width / 2);
			int y = ga.getY() + context.getY() - (height / 2);
			gaService.setLocationAndSize(ellipse, x, y, height, height);
		}

		Ellipse circle = ShapeDecoratorUtil.createIntermediateEventCircle(ellipse);
		circle.setStyle(StyleUtil.getStyleForClass(getDiagram()));
		createDIShape(containerShape, businessObject, !isImport);

		Activity activity = businessObject.getAttachedToRef();
		PictogramElement foundElem = BusinessObjectUtil.getFirstBaseElementFromDiagram(getDiagram(), activity);
		if (foundElem != null && foundElem instanceof ContainerShape) {
			ContainerShape activityContainer = (ContainerShape) foundElem;
			PositionOnLine pos = BoundaryEventPositionHelper.getPositionOnLineUsingBPMNShape(containerShape,
			        activityContainer);
			BoundaryEventPositionHelper.assignPositionOnLineProperty(containerShape, pos);
		}

		peService.setPropertyValue(containerShape, BOUNDARY_EVENT_CANCEL, Boolean.toString(businessObject.isCancelActivity()));
		peService.setPropertyValue(containerShape, GraphitiConstants.EVENT_MARKER_CONTAINER, Boolean.toString(true));
		peService.setPropertyValue(containerShape,
				UpdateBoundaryEventFeature.BOUNDARY_EVENT_MARKER,
				AbstractUpdateEventFeature.getEventDefinitionsValue(businessObject));

		// hook for subclasses to inject extra code
		decorateShape(context, containerShape, businessObject);

		ChopboxAnchor anchor = peService.createChopboxAnchor(containerShape);
		anchor.setReferencedGraphicsAlgorithm(ellipse);
		AnchorUtil.addFixedPointAnchors(containerShape, ellipse);

		return containerShape;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getBusinessObjectType()
	 */
	@Override
	public Class getBusinessObjectType() {
		return BoundaryEvent.class;
	}
}
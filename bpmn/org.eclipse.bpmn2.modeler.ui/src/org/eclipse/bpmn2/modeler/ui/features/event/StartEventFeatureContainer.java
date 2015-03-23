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

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2UpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.MultiUpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.event.AbstractCreateEventFeature;
import org.eclipse.bpmn2.modeler.core.features.event.AbstractUpdateEventFeature;
import org.eclipse.bpmn2.modeler.core.features.event.AddEventFeature;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeService;

public class StartEventFeatureContainer extends AbstractEventFeatureContainer {

	static final String INTERRUPTING = "interrupting"; //$NON-NLS-1$

	@Override
	public boolean canApplyTo(Object o) {
		return super.canApplyTo(o) && o instanceof StartEvent;
	}

	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return new CreateStartEventFeature(fp);
	}

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddStartEventFeature(fp);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		MultiUpdateFeature updateFeature = new MultiUpdateFeature(fp);
		updateFeature.addFeature(super.getUpdateFeature(fp));
		updateFeature.addFeature(new UpdateEventSubProcessFeature(fp));
		updateFeature.addFeature(new UpdateStartEventFeature(fp));
		return updateFeature;
	}

	public class AddStartEventFeature extends AddEventFeature<StartEvent> {
		public AddStartEventFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		protected void decorateShape(IAddContext context, ContainerShape containerShape, StartEvent businessObject) {
			super.decorateShape(context, containerShape, businessObject);
			Ellipse e = (Ellipse)getGraphicsAlgorithm(containerShape);
			Graphiti.getPeService().setPropertyValue(containerShape, INTERRUPTING, Boolean.toString(true));
			IPeService peService = Graphiti.getPeService();
			peService.setPropertyValue(containerShape,
					UpdateStartEventFeature.START_EVENT_MARKER,
					AbstractUpdateEventFeature.getEventDefinitionsValue((StartEvent)businessObject));
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getBusinessObjectType()
		 */
		@Override
		public Class getBusinessObjectType() {
			return StartEvent.class;
		}
	}

	public static class CreateStartEventFeature extends AbstractCreateEventFeature<StartEvent> {

		public CreateStartEventFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public String getStencilImageId() {
			return ImageProvider.IMG_16_START_EVENT;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractCreateFlowElementFeature#getFlowElementClass()
		 */
		@Override
		public EClass getBusinessObjectClass() {
			return Bpmn2Package.eINSTANCE.getStartEvent();
		}
	}

	protected static class UpdateStartEventFeature extends AbstractUpdateEventFeature<StartEvent> {

		public static String START_EVENT_MARKER = "marker.start.event"; //$NON-NLS-1$

		/**
		 * @param fp
		 */
		public UpdateStartEventFeature(IFeatureProvider fp) {
			super(fp);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.activity.AbstractUpdateMarkerFeature#getPropertyKey()
		 */
		@Override
		protected String getPropertyKey() {
			return START_EVENT_MARKER;
		}
	}

	private class UpdateEventSubProcessFeature extends AbstractBpmn2UpdateFeature {

		public UpdateEventSubProcessFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public boolean canUpdate(IUpdateContext context) {
			Object o = getBusinessObjectForPictogramElement(context.getPictogramElement());
			return o != null && o instanceof StartEvent;
		}

		@Override
		public IReason updateNeeded(IUpdateContext context) {
			IPeService peService = Graphiti.getPeService();
			PictogramElement element = context.getPictogramElement();

			String prop = peService.getPropertyValue(element, INTERRUPTING);
			if (prop == null) {
				return Reason.createFalseReason();
			}

			StartEvent event = (StartEvent) getBusinessObjectForPictogramElement(element);
			boolean interrupting = Boolean.parseBoolean(prop);
			IReason reason = event.isIsInterrupting() == interrupting ? Reason.createFalseReason() : Reason
					.createTrueReason("Is Interrupting");
			return reason;
		}

		@Override
		public boolean update(IUpdateContext context) {
			IPeService peService = Graphiti.getPeService();
			ContainerShape container = (ContainerShape) context.getPictogramElement();
			StartEvent event = (StartEvent) getBusinessObjectForPictogramElement(container);

			GraphicsAlgorithm ga = peService.getAllContainedShapes(container).iterator().next().getGraphicsAlgorithm();
			if (ga instanceof Ellipse) {
			Ellipse ellipse = (Ellipse) ga;
				LineStyle style = event.isIsInterrupting() ? LineStyle.SOLID : LineStyle.DASH;
				ellipse.setLineStyle(style);
			}
			
			peService.setPropertyValue(container, INTERRUPTING, Boolean.toString(event.isIsInterrupting()));
			return true;
		}
	}
}
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
package org.eclipse.bpmn2.modeler.ui.features.activity.subprocess;

import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.Transaction;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.MultiUpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.activity.AbstractCreateExpandableFlowNodeFeature;
import org.eclipse.bpmn2.modeler.core.features.activity.AbstractUpdateMarkerFeature;
import org.eclipse.bpmn2.modeler.core.features.event.AbstractUpdateEventFeature;
import org.eclipse.bpmn2.modeler.core.features.event.definitions.AbstractUpdateEventDefinitionFeature;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.bpmn2.modeler.ui.features.event.definitions.CompensateEventDefinitionContainer;
import org.eclipse.bpmn2.modeler.ui.features.event.definitions.ConditionalEventDefinitionContainer;
import org.eclipse.bpmn2.modeler.ui.features.event.definitions.ErrorEventDefinitionContainer;
import org.eclipse.bpmn2.modeler.ui.features.event.definitions.EscalationEventDefinitionContainer;
import org.eclipse.bpmn2.modeler.ui.features.event.definitions.MessageEventDefinitionContainer;
import org.eclipse.bpmn2.modeler.ui.features.event.definitions.SignalEventDefinitionContainer;
import org.eclipse.bpmn2.modeler.ui.features.event.definitions.TimerEventDefinitionContainer;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

public class SubProcessFeatureContainer extends AbstractExpandableActivityFeatureContainer {

	@Override
	public boolean canApplyTo(Object o) {
		return super.canApplyTo(o) && o instanceof SubProcess &&
				 !(o instanceof AdHocSubProcess ||  o instanceof Transaction);
	}

	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return new CreateSubProcessFeature(fp);
	}

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddExpandableActivityFeature<SubProcess>(fp) {

			@Override
			public Class getBusinessObjectType() {
				return SubProcess.class;
			}
			
		};
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		MultiUpdateFeature multiUpdate = (MultiUpdateFeature) super.getUpdateFeature(fp);
		multiUpdate.addFeature(new UpdateSubProcessDecoratorFeature(fp));
		return multiUpdate;
	}

	public static class CreateSubProcessFeature extends AbstractCreateExpandableFlowNodeFeature<SubProcess> {

		public CreateSubProcessFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		protected String getStencilImageId() {
			return ImageProvider.IMG_16_SUB_PROCESS;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractCreateFlowElementFeature#getFlowElementClass()
		 */
		@Override
		public EClass getBusinessObjectClass() {
			return Bpmn2Package.eINSTANCE.getSubProcess();
		}
	}
	
	public static class UpdateSubProcessDecoratorFeature extends AbstractUpdateMarkerFeature<SubProcess> {
		
		/**
		 * @param fp
		 */
		public UpdateSubProcessDecoratorFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public boolean canUpdate(IUpdateContext context) {
			PictogramElement pe = context.getPictogramElement();
			if (pe instanceof ContainerShape) {
				Object o = getBusinessObjectForPictogramElement(pe);
				return o instanceof SubProcess;
			}
			return false;
		}

		@Override
		public IReason updateNeeded(IUpdateContext context) {
			PictogramElement pe = context.getPictogramElement();
			if (pe instanceof ContainerShape) {
				boolean isVisible = false;
				
				ContainerShape subProcessShape = (ContainerShape) pe;
				SubProcess subProcess = (SubProcess) getBusinessObjectForPictogramElement(pe);
				for (Shape s : subProcessShape.getChildren()) {
					if (ShapeDecoratorUtil.isEventSubProcessDecorator(s)) {
						isVisible = pe.isVisible();
					}
				}
				EventDefinition eventDefinition = null;
				for (FlowElement fe : subProcess.getFlowElements()) {
					if (fe instanceof StartEvent) {
						StartEvent startEvent = (StartEvent) fe;
						if (startEvent.getEventDefinitions().size()>0) {
							eventDefinition = startEvent.getEventDefinitions().get(0);
						}
						break;
					}
				}
				
				if (subProcess.isTriggeredByEvent()) {
					// check if we need to draw the image decorator if the
					// SubProcess is collapsed
					if (!FeatureSupport.isElementExpanded(subProcess)) {
						if (super.updateNeeded(context).toBoolean()) {
							return Reason.createTrueReason("SubProcess Decorator Changed");
						}
						if (!isVisible && eventDefinition!=null)
							return Reason.createTrueReason("Show SubProcess Decorator");
					}
				}
				else if (isVisible) {
					return Reason.createTrueReason("Hide SubProcess Decorator");
				}
			}
			return Reason.createFalseReason();
		}
		
		@Override
	    public boolean update(IUpdateContext context) {
			return super.update(context);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.activity.AbstractUpdateMarkerFeature#getPropertyKey()
		 */
		@Override
		protected String getPropertyKey() {
			return GraphitiConstants.EVENT_SUBPROCESS_DECORATOR;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.activity.AbstractUpdateMarkerFeature#isPropertyChanged(org.eclipse.bpmn2.FlowElement, java.lang.String)
		 */
		@Override
		protected boolean isPropertyChanged(SubProcess element, String propertyValue) {
			return !convertPropertyToString(element).equals(propertyValue);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.activity.AbstractUpdateMarkerFeature#doUpdate(org.eclipse.bpmn2.FlowElement, org.eclipse.graphiti.mm.pictograms.ContainerShape)
		 */
		@Override
		protected void doUpdate(SubProcess subProcess, ContainerShape subProcessShape) {
			EventDefinition eventDefinition = null;
			boolean isMultiple = false;
			boolean isParallel = false;
			StartEvent startEvent = null;
			for (FlowElement fe : subProcess.getFlowElements()) {
				if (fe instanceof StartEvent) {
					startEvent = (StartEvent) fe;
					if (startEvent.getEventDefinitions().size()>0) {
						eventDefinition = startEvent.getEventDefinitions().get(0);
					}
					if (startEvent.getEventDefinitions().size()>1) {
						isMultiple = true;
						isParallel = startEvent.isParallelMultiple();
					}
					break;
				}
			}

			for (Shape s : subProcessShape.getChildren()) {
				if (ShapeDecoratorUtil.isEventSubProcessDecorator(s)) {
					Graphiti.getPeService().deletePictogramElement(s);
					break;
				}
			}
			
			if (subProcess.isTriggeredByEvent() && !FeatureSupport.isElementExpanded(subProcessShape)) {
				ContainerShape decoratorShape = null;
				if (eventDefinition!=null) {
					decoratorShape = ShapeDecoratorUtil.createEventSubProcessDecorator(
							subProcessShape, startEvent.isIsInterrupting());
				}
				
				if (isMultiple) {
					// TODO: Why does the Multiple figure require an additional
					// ContainerShape to get the correct size ratios?
					Shape s = Graphiti.getPeCreateService().createContainerShape(decoratorShape, false);
					Rectangle r = Graphiti.getGaCreateService().createInvisibleRectangle(decoratorShape);
					Graphiti.getGaService().setLocationAndSize(r, 1, 1, 20, 20);
					if (isParallel)
						AbstractUpdateEventDefinitionFeature.drawParallelMultiple(startEvent, s);
					else
						AbstractUpdateEventDefinitionFeature.drawMultiple(startEvent, s);
				}
				else if (eventDefinition instanceof MessageEventDefinition) {
					MessageEventDefinitionContainer.draw(decoratorShape);
				}
				else if (eventDefinition instanceof TimerEventDefinition) {
					TimerEventDefinitionContainer.draw(decoratorShape);
				}
				else if (eventDefinition instanceof CompensateEventDefinition) {
					CompensateEventDefinitionContainer.draw(decoratorShape);
				}
				else if (eventDefinition instanceof ConditionalEventDefinition) {
					ConditionalEventDefinitionContainer.draw(decoratorShape);
				}
				else if (eventDefinition instanceof ErrorEventDefinition) {
					ErrorEventDefinitionContainer.draw(decoratorShape);
				}
				else if (eventDefinition instanceof EscalationEventDefinition) {
					EscalationEventDefinitionContainer.draw(decoratorShape);
				}
				else if (eventDefinition instanceof SignalEventDefinition) {
					SignalEventDefinitionContainer.draw(decoratorShape);
				}
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.activity.AbstractUpdateMarkerFeature#convertPropertyToString(org.eclipse.bpmn2.FlowElement)
		 */
		@Override
		protected String convertPropertyToString(SubProcess element) {
			for (FlowElement fe : element.getFlowElements()) {
				if (fe instanceof StartEvent) {
					return AbstractUpdateEventFeature.getEventDefinitionsValue((StartEvent)fe);
				}
			}
			return "";
		}
		
	}
}
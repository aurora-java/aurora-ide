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
package org.eclipse.bpmn2.modeler.ui.features.callactivity;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.GlobalBusinessRuleTask;
import org.eclipse.bpmn2.GlobalManualTask;
import org.eclipse.bpmn2.GlobalScriptTask;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.GlobalUserTask;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.AbstractUpdateBaseElementFeature;
import org.eclipse.bpmn2.modeler.core.features.DefaultResizeBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.MultiUpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.activity.AbstractCreateExpandableFlowNodeFeature;
import org.eclipse.bpmn2.modeler.core.features.activity.task.DirectEditTaskFeature;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.bpmn2.modeler.ui.features.activity.AbstractActivityFeatureContainer;
import org.eclipse.bpmn2.modeler.ui.features.activity.DeleteActivityFeature;
import org.eclipse.bpmn2.modeler.ui.features.activity.subprocess.LayoutExpandableActivityFeature;
import org.eclipse.bpmn2.modeler.ui.features.activity.subprocess.Messages;
import org.eclipse.bpmn2.modeler.ui.features.callactivity.AbstractCallGlobalTaskFeatureContainer.AddCallGlobalTaskFeature;
import org.eclipse.bpmn2.modeler.ui.features.choreography.ShowDiagramPageFeature;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;

public class CallActivityFeatureContainer extends AbstractActivityFeatureContainer {

	protected static final int MARKER_OFFSET = 4;
	protected static final String CALL_ACTIVITY_REF_PROPERTY = "call.activity.ref"; //$NON-NLS-1$
	protected static final String GLOBAL_TASK_SHAPE_PROPERTY = "global.task.shape"; //$NON-NLS-1$

	@Override
	public boolean canApplyTo(Object o) {
		return super.canApplyTo(o) && (o instanceof CallActivity || o instanceof GlobalTask);
	}

	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return new CreateCallActivityFeature(fp);
	}

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddCallGlobalTaskFeature(fp);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return new DeleteActivityFeature(fp) {
			@Override
			public void delete(final IDeleteContext context) {
				PictogramElement pe = context.getPictogramElement();
				CallActivity callActivity = BusinessObjectUtil.getFirstElementOfType(pe, CallActivity.class);
				CallableElement calledActivity = callActivity.getCalledElementRef();
				// if there are no other references to this called element, delete it from the model
				boolean canDeleteCalledActivity = (calledActivity!=null);
				if (canDeleteCalledActivity) {
					Definitions definitions = ModelUtil.getDefinitions(callActivity);
					TreeIterator<EObject> iter = definitions.eAllContents();
					while (iter.hasNext() && canDeleteCalledActivity) {
						EObject o = iter.next();
						if (o!=callActivity && o instanceof BaseElement) {
							for (EObject cr : o.eCrossReferences()) {
								if (cr == calledActivity) {
									canDeleteCalledActivity = false;
									break;
								}
							}
						}
					}
				}
				
				super.delete(context);
				
				if (canDeleteCalledActivity) {
					// if the called activity is a Process, it may have its own
					// diagram page which needs to be removed as well.
					BPMNDiagram bpmnDiagram = DIUtils.findBPMNDiagram(calledActivity);
					if (bpmnDiagram != null) {
						DIUtils.deleteDiagram(getDiagramBehavior(), bpmnDiagram);
					}
					EcoreUtil.delete(calledActivity, true);
				}
			}
		};
	}

	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return new LayoutExpandableActivityFeature(fp) {
			protected int getMarkerContainerOffset() {
				return MARKER_OFFSET;
			}
		};
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		IUpdateFeature updateFeature = super.getUpdateFeature(fp);
		MultiUpdateFeature multiUpdate;
		if (updateFeature instanceof MultiUpdateFeature)
			multiUpdate = (MultiUpdateFeature)updateFeature;
		else
			multiUpdate = new MultiUpdateFeature(fp);
		multiUpdate.addFeature(new UpdateCallActivityFeature(fp));
		return multiUpdate;
	}
	
	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		return new DefaultResizeBPMNShapeFeature(fp);
	}
	
	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		ICustomFeature[] superFeatures = super.getCustomFeatures(fp);
		ICustomFeature[] thisFeatures = new ICustomFeature[1 + superFeatures.length];
		thisFeatures[0] = new ShowDiagramPageFeature(fp);
		for (int superIndex=0, thisIndex=1; superIndex<superFeatures.length; ++superIndex) {
			thisFeatures[thisIndex++] = superFeatures[superIndex];
		}
		return thisFeatures;
	}

	public static class CreateCallActivityFeature extends AbstractCreateExpandableFlowNodeFeature<CallActivity> {

		// NOTE: Even though the Call Activity is an expandable figure, the contents for its "innards"
		// are (usually) defined somewhere else, so it doesn't make much sense to be able to expand it in the
		// same sense that a SubProcess would be expanded and rendered. When the "expand" button is clicked
		// we will locate the process where this thing is defined (if possible) and open an
		// editor to display its contents.
		
		public CreateCallActivityFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public String getStencilImageId() {
			return ImageProvider.IMG_16_CALL_ACTIVITY;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractCreateFlowElementFeature#getFlowElementClass()
		 */
		@Override
		public EClass getBusinessObjectClass() {
			return Bpmn2Package.eINSTANCE.getCallActivity();
		}
	}

	protected class UpdateCallActivityFeature extends AbstractUpdateBaseElementFeature<CallActivity> {

		public UpdateCallActivityFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public IReason updateNeeded(IUpdateContext context) {
			IReason reason = super.updateNeeded(context);
			if (reason.toBoolean())
				return reason;

			IPeService peService = Graphiti.getPeService();
			PictogramElement element = context.getPictogramElement();
			String property = peService.getPropertyValue(element, CALL_ACTIVITY_REF_PROPERTY);
			if (property == null) {
				return Reason.createFalseReason();
			}
			CallActivity callActivity = BusinessObjectUtil.getFirstElementOfType(context.getPictogramElement(),
					CallActivity.class);
			String value = getCallableElementStringValue(callActivity.getCalledElementRef());
			boolean changed = !value.equals(property);
			return changed ? Reason.createTrueReason("Called Activity") : Reason.createFalseReason();
		}

		@Override
		public boolean update(IUpdateContext context) {
			IPeService peService = Graphiti.getPeService();
			IGaService gaService = Graphiti.getGaService();

			ContainerShape container = (ContainerShape) context.getPictogramElement();
			CallActivity callActivity = BusinessObjectUtil.getFirstElementOfType(context.getPictogramElement(),
					CallActivity.class);

			Shape globalTaskShape = ShapeDecoratorUtil.getContainedShape(container, GLOBAL_TASK_SHAPE_PROPERTY);

			if (callActivity.getCalledElementRef() == null) {
				ShapeDecoratorUtil.hideActivityMarker(container, GraphitiConstants.ACTIVITY_MARKER_EXPAND);
				if (globalTaskShape != null) {
					peService.deletePictogramElement(globalTaskShape);
				}
			}

			else if (callActivity.getCalledElementRef() instanceof GlobalTask) {
				ShapeDecoratorUtil.hideActivityMarker(container, GraphitiConstants.ACTIVITY_MARKER_EXPAND);
				GlobalTask globalTask = (GlobalTask) callActivity.getCalledElementRef();
				String imageId = getImageId(globalTask);
				if (imageId != null) {
				if (globalTaskShape == null) {
					globalTaskShape = peService.createShape(container, false);
					peService.setPropertyValue(globalTaskShape, GLOBAL_TASK_SHAPE_PROPERTY, Boolean.toString(true));
				}
					Image image = gaService.createImage(globalTaskShape, imageId);
					gaService.setLocationAndSize(image, MARKER_OFFSET + 2, MARKER_OFFSET + 2, 16, 16);
				}
				else if (globalTaskShape != null) {
					peService.deletePictogramElement(globalTaskShape);
				}
			}

			else if (callActivity.getCalledElementRef() instanceof Process) {
				if (globalTaskShape != null) {
					peService.deletePictogramElement(globalTaskShape);
				}
				ShapeDecoratorUtil.showActivityMarker(container, GraphitiConstants.ACTIVITY_MARKER_EXPAND);
			}

			peService.setPropertyValue(container, CALL_ACTIVITY_REF_PROPERTY,
					getCallableElementStringValue(callActivity.getCalledElementRef()));
			return true;
		}
	}

	protected static String getCallableElementStringValue(CallableElement element) {
		if (element == null) {
			return "null"; //$NON-NLS-1$
		}
		return element.getClass().getSimpleName();
	}

	protected String getImageId(GlobalTask task) {
		if (task instanceof GlobalBusinessRuleTask) {
			return ImageProvider.IMG_16_BUSINESS_RULE_TASK;
		} else if (task instanceof GlobalManualTask) {
			return ImageProvider.IMG_16_MANUAL_TASK;
		} else if (task instanceof GlobalScriptTask) {
			return ImageProvider.IMG_16_SCRIPT_TASK;
		} else if (task instanceof GlobalUserTask) {
			return ImageProvider.IMG_16_USER_TASK;
		} else {
			return null;
		}
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IFeatureProvider fp) {
		return new DirectEditTaskFeature(fp);
	}
}
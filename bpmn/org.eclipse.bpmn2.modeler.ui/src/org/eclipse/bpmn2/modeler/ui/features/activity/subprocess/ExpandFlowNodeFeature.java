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
 * @author Bob Brodt
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.features.activity.subprocess;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.bpmn2.modeler.ui.features.choreography.ShowDiagramPageFeature;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

// NOT USED YET
public class ExpandFlowNodeFeature extends ShowDiagramPageFeature {

	private final static String NAME = Messages.ExpandFlowNodeFeature_Name;
	private final static String DESCRIPTION = Messages.ExpandFlowNodeFeature_Description;
	
	private String name = NAME;
	private String description = DESCRIPTION;
	
	public ExpandFlowNodeFeature(IFeatureProvider fp) {
	    super(fp);
    }
	
	@Override
	public String getName() {
	    return name;
	}
	
	@Override
	public String getDescription() {
	    return description;
	}

	@Override
	public String getImageId() {
		return ImageProvider.IMG_16_EXPAND;
	}

	@Override
	public boolean isAvailable(IContext context) {
		return true;
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		if (super.canExecute(context)) {
			name = super.getName();
			description = super.getDescription();
			return true;
		}
		else {
			name = NAME;
			description = DESCRIPTION;
		}
		
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			BaseElement be = BusinessObjectUtil.getFirstBaseElement(pes[0]);
			return !FeatureSupport.isElementExpanded(be);
		}
		return ret;
	}

	@Override
	public void execute(ICustomContext context) {
		if (super.canExecute(context)) {
			super.execute(context);
			return;
		}
		
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			PictogramElement pe0 = pes[0];
			Object bo = getBusinessObjectForPictogramElement(pe0);
			if (pe0 instanceof ContainerShape && bo instanceof FlowNode) {
				ContainerShape containerShape = (ContainerShape)pe0;
				FlowNode flowNode = (FlowNode)bo;
				try {
					BPMNShape bpmnShape = DIUtils.findBPMNShape(flowNode);
					if (!bpmnShape.isIsExpanded()) {
						
						// SubProcess is collapsed - resize to minimum size such that all children are visible
						// NOTE: children tasks will be set visible in LayoutExpandableActivityFeature

						bpmnShape.setIsExpanded(true);

						GraphicsAlgorithm ga = containerShape.getGraphicsAlgorithm();
						ResizeShapeContext resizeContext = new ResizeShapeContext(containerShape);
						IResizeShapeFeature resizeFeature = getFeatureProvider().getResizeShapeFeature(resizeContext);
						IDimension oldSize = FeatureSupport.getExpandedSize(containerShape);
						int oldWidth = ga.getWidth();
						int oldHeight = ga.getHeight();
						FeatureSupport.setCollapsedSize(containerShape, oldWidth, oldHeight);
						ResizeExpandableActivityFeature.SizeCalculator newSize = new ResizeExpandableActivityFeature.SizeCalculator(containerShape);
						if (newSize.getWidth() > oldSize.getWidth())
							oldSize.setWidth(newSize.getWidth());
						if (newSize.getHeight() > oldSize.getHeight())
							oldSize.setHeight(newSize.getHeight());
						int newWidth = oldSize.getWidth();
						int newHeight = oldSize.getHeight();
						resizeContext.setX(ga.getX() + oldWidth/2 - newWidth/2);
						resizeContext.setY(ga.getY() + oldHeight/2 - newHeight/2);
						resizeContext.setWidth(newWidth);
						resizeContext.setHeight(newHeight);
						resizeFeature.resizeShape(resizeContext);
						
						UpdateContext updateContext = new UpdateContext(containerShape);
						IUpdateFeature updateFeature = getFeatureProvider().getUpdateFeature(updateContext);
						if (updateFeature.updateNeeded(updateContext).toBoolean())
							updateFeature.update(updateContext);
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
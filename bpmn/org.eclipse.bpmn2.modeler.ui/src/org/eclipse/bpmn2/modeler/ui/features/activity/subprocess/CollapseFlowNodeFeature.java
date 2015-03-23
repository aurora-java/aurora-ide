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
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class CollapseFlowNodeFeature extends AbstractCustomFeature {

	public CollapseFlowNodeFeature(IFeatureProvider fp) {
		super(fp);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return "Collapse"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {

		return "Collapse the Activity and hide contents"; //$NON-NLS-1$
	}

	@Override
	public String getImageId() {
		return ImageProvider.IMG_16_COLLAPSE;
	}

	@Override
	public boolean isAvailable(IContext context) {
		return true;
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			BaseElement be = BusinessObjectUtil.getFirstBaseElement(pes[0]);
			return FeatureSupport.isElementExpanded(be);
		}
		return false;
	}

	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			PictogramElement pe0 = pes[0];
			Object bo = getBusinessObjectForPictogramElement(pe0);
			if (pe0 instanceof ContainerShape && bo instanceof FlowNode) {
				ContainerShape containerShape = (ContainerShape)pe0;
				FlowNode flowNode = (FlowNode)bo;
				try {
					BPMNShape bpmnShape = DIUtils.findBPMNShape(flowNode);
					if (bpmnShape.isIsExpanded()) {
						Bpmn2Preferences preferences = Bpmn2Preferences.getInstance(getDiagram());
						ShapeStyle ss = preferences.getShapeStyle("TASKS");
						
						// SubProcess is expanded - resize to standard Task size
						// NOTE: children tasks will be set not-visible in LayoutExpandableActivityFeature
						
						bpmnShape.setIsExpanded(false);

						GraphicsAlgorithm ga = containerShape.getGraphicsAlgorithm();
						ResizeShapeContext resizeContext = new ResizeShapeContext(containerShape);
						IResizeShapeFeature resizeFeature = getFeatureProvider().getResizeShapeFeature(resizeContext);
						IDimension oldSize = FeatureSupport.getCollapsedSize(containerShape);
						int oldWidth = ga.getWidth();
						int oldHeight = ga.getHeight();
						FeatureSupport.setExpandedSize(containerShape, oldWidth, oldHeight);

						int newWidth = ss.getDefaultWidth();
						int newHeight = ss.getDefaultHeight();
						if (newWidth < oldSize.getWidth())
							oldSize.setWidth(newWidth);
						if (newHeight < oldSize.getHeight())
							oldSize.setHeight(newHeight);
						newWidth = oldSize.getWidth();
						newHeight = oldSize.getHeight();
						resizeContext.setX(ga.getX() + oldWidth/2 - newWidth/2);
						resizeContext.setY(ga.getY() + oldHeight/2 - newHeight/2);
						resizeContext.setWidth(newWidth);
						resizeContext.setHeight(newHeight);
						resizeFeature.resizeShape(resizeContext);
						
						UpdateContext updateContext = new UpdateContext(containerShape);
						IUpdateFeature updateFeature = getFeatureProvider().getUpdateFeature(updateContext);
						if (updateFeature.updateNeeded(updateContext).toBoolean())
							updateFeature.update(updateContext);
						
						getDiagramEditor().selectPictogramElements(new PictogramElement[] {});
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}

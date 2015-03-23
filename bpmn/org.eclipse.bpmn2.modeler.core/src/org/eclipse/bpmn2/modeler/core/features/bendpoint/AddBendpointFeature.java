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
package org.eclipse.bpmn2.modeler.core.features.bendpoint;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.BendpointConnectionRouter;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.bpmn2.modeler.core.model.ModelHandlerLocator;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.dd.dc.DcFactory;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddBendpointContext;
import org.eclipse.graphiti.features.impl.DefaultAddBendpointFeature;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;

public class AddBendpointFeature extends DefaultAddBendpointFeature {

	public AddBendpointFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAddBendpoint(IAddBendpointContext context) {
		try {
			FreeFormConnection connection = context.getConnection();
			BaseElement element = (BaseElement) BusinessObjectUtil.getFirstElementOfType(connection, BaseElement.class);
			BPMNEdge edge = DIUtils.findBPMNEdge(element);
			return edge!=null;
		} catch (Exception e) {
			Activator.logError(e);
		}
		return false;
	}

	@Override
	public void addBendpoint(IAddBendpointContext context) {
		super.addBendpoint(context);
		try {
			
			FreeFormConnection connection = context.getConnection();
			BaseElement element = (BaseElement) BusinessObjectUtil.getFirstElementOfType(connection, BaseElement.class);

			org.eclipse.dd.dc.Point p = DcFactory.eINSTANCE.createPoint();
			p.setX(context.getX());
			p.setY(context.getY());

			BPMNEdge edge = DIUtils.findBPMNEdge(element);
			int index = context.getBendpointIndex() + 1;
			edge.getWaypoint().add(index, p);
			BendpointConnectionRouter.setAddedBendpoint(connection, context.getBendpointIndex());
			FeatureSupport.updateConnection(getFeatureProvider(), connection);
			
		} catch (Exception e) {
			Activator.logError(e);
		}
	}

}
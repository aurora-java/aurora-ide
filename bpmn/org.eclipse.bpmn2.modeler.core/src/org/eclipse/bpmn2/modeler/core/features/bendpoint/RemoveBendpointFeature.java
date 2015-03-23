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
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IRemoveBendpointContext;
import org.eclipse.graphiti.features.impl.DefaultRemoveBendpointFeature;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class RemoveBendpointFeature extends DefaultRemoveBendpointFeature {

	public RemoveBendpointFeature(IFeatureProvider fp) {
	    super(fp);
    }

	@Override
	public void removeBendpoint(IRemoveBendpointContext context) {
	    super.removeBendpoint(context);
		FreeFormConnection connection = context.getConnection();
	    try {
			BaseElement element = (BaseElement) BusinessObjectUtil.getFirstElementOfType(connection, BaseElement.class);
			BPMNEdge edge = DIUtils.findBPMNEdge(element);
			edge.getWaypoint().remove(context.getBendpointIndex() + 1);
		} catch (Exception e) {
			Activator.logError(e);
		}
	    
		BendpointConnectionRouter.setRemovedBendpoint(connection, context.getBendpointIndex());
		FeatureSupport.updateConnection(getFeatureProvider(), connection);
	}
}
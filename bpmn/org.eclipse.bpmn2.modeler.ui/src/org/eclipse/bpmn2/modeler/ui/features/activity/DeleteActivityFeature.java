/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.features.activity;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.modeler.core.features.event.AbstractBoundaryEventOperation;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.ui.features.AbstractDefaultDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;

public class DeleteActivityFeature extends AbstractDefaultDeleteFeature {

	public DeleteActivityFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public void delete(final IDeleteContext context) {
		Activity activity = BusinessObjectUtil.getFirstElementOfType(context.getPictogramElement(),
				Activity.class);
		new AbstractBoundaryEventOperation() {
			@Override
			protected void doWorkInternal(ContainerShape container) {
				IDeleteContext delete = new DeleteContext(container);
				getFeatureProvider().getDeleteFeature(delete).delete(delete);
			}
		}.doWork(activity, getDiagram());
		super.delete(context);
	}

}

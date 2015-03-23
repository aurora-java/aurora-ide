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
package org.eclipse.bpmn2.modeler.ui.features.activity.task;

import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.modeler.core.features.activity.task.AbstractAddTaskFeature;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;

public abstract class AbstractAddDecoratedTaskFeature<T extends Task> extends AbstractAddTaskFeature<T> {
	
	public AbstractAddDecoratedTaskFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected void decorateShape(IAddContext context, ContainerShape containerShape, T businessObject) {
		super.decorateShape(context, containerShape, businessObject);
		ShapeDecoratorUtil.createActivityImage(containerShape, getStencilImageId());
	}
	
	protected abstract String getStencilImageId();

}

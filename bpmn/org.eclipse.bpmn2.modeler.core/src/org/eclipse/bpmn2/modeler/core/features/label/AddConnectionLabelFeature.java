/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
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

package org.eclipse.bpmn2.modeler.core.features.label;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Add a Label for a Connection. This Add Feature class must be invoked from a {@link org.eclipse.bpmn2.modeler.core.features.MultiAddFeature}
 * immediately following the Add Feature that creates the Connection.    
 */
public class AddConnectionLabelFeature extends AbstractAddLabelFeature {

	/**
	 * @param fp
	 */
	public AddConnectionLabelFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.label.AbstractAddLabelFeature#add(org.eclipse.graphiti.features.context.IAddContext)
	 */
	@Override
	public PictogramElement add(IAddContext context) {
		ConnectionDecorator labelShape = null;
		
		BaseElement businessObject = (BaseElement) context.getNewObject();
		PictogramElement labelOwner = FeatureSupport.getLabelOwner(context);
		if (labelOwner instanceof Connection) {
			labelShape = peService.createConnectionDecorator((Connection)labelOwner, true, 0.5, true);
			createText(labelOwner, labelShape, businessObject);
		}
		return labelShape;
	}

}

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

import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveConnectionDecoratorContext;
import org.eclipse.graphiti.features.impl.DefaultMoveConnectionDecoratorFeature;

/**
 *
 */
public class MoveConnectionLabelFeature extends DefaultMoveConnectionDecoratorFeature {

	/**
	 * @param fp
	 */
	public MoveConnectionLabelFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canMoveConnectionDecorator(IMoveConnectionDecoratorContext context) {
		return true;
	}

	@Override
	public void moveConnectionDecorator(IMoveConnectionDecoratorContext context) {
		super.moveConnectionDecorator(context);
		FeatureSupport.updateLabel(
				getFeatureProvider(),
				context.getConnectionDecorator().getConnection(),
				null);
	}

}

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

package org.eclipse.bpmn2.modeler.core.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultRemoveBPMNShapeFeature.
 *
 * @author Bob Brodt
 */
public class DefaultRemoveBPMNShapeFeature extends DefaultRemoveFeature {

	/**
	 * Instantiates a new default remove bpmn shape feature.
	 *
	 * @param fp the fp
	 */
	public DefaultRemoveBPMNShapeFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.DefaultRemoveFeature#canRemove(org.eclipse.graphiti.features.context.IRemoveContext)
	 */
	@Override
	public boolean canRemove(IRemoveContext context) {
		return false;
	}

}

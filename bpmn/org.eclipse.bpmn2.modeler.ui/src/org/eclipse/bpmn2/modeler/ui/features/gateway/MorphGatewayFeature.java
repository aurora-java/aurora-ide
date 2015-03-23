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

package org.eclipse.bpmn2.modeler.ui.features.gateway;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.bpmn2.modeler.ui.features.AbstractMorphNodeFeature;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.IFeatureProvider;

/**
 * @author Bob Brodt
 *
 */
public class MorphGatewayFeature extends AbstractMorphNodeFeature<Gateway> {

	/**
	 * @param fp
	 */
	public MorphGatewayFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return Messages.MorphGatewayFeature_Name;
	}

	@Override
	public String getDescription() {
		return Messages.MorphGatewayFeature_Description;
	}

	@Override
	public String getImageId() {
		return ImageProvider.IMG_16_MORPH;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.ui.features.AbstractAppendNodeNodeFeature#getBusinessObjectClass()
	 */
	@Override
	public EClass getBusinessObjectClass() {
		return Bpmn2Package.eINSTANCE.getGateway();
	}

	@Override
	public void copyBusinessObject(Gateway oldObject, Gateway newObject) {
		newObject.setGatewayDirection( oldObject.getGatewayDirection() );
		newObject.setName( oldObject.getName() );
	}
}

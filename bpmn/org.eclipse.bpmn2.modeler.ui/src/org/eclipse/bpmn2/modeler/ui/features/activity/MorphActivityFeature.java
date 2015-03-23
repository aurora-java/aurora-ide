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

package org.eclipse.bpmn2.modeler.ui.features.activity;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.bpmn2.modeler.ui.features.AbstractMorphNodeFeature;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.IFeatureProvider;

/**
 * @author Bob Brodt
 *
 */
public class MorphActivityFeature extends AbstractMorphNodeFeature<Activity> {

	/**
	 * @param fp
	 */
	public MorphActivityFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return Messages.MorphActivityFeature_Name;
	}

	@Override
	public String getDescription() {
		return Messages.MorphActivityFeature_Description;
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
		return Bpmn2Package.eINSTANCE.getActivity();
	}

	@Override
	public void copyBusinessObject(Activity oldObject, Activity newObject) {
		newObject.setCompletionQuantity( oldObject.getCompletionQuantity() );
		newObject.setDefault( oldObject.getDefault() );
		newObject.setIoSpecification( oldObject.getIoSpecification() );
		newObject.setIsForCompensation( oldObject.isIsForCompensation() );
		newObject.setLoopCharacteristics( oldObject.getLoopCharacteristics() );
		newObject.setName( oldObject.getName() );
		newObject.setStartQuantity( oldObject.getStartQuantity() );
	}
}

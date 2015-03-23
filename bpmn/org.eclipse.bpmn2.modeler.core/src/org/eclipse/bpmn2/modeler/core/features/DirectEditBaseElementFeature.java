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
package org.eclipse.bpmn2.modeler.core.features;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

// TODO: Auto-generated Javadoc
/**
 * The Class DirectEditBaseElementFeature.
 */
public class DirectEditBaseElementFeature extends AbstractDirectEditingFeature {

	/**
	 * Instantiates a new direct edit base element feature.
	 *
	 * @param fp the fp
	 */
	public DirectEditBaseElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IDirectEditing#getEditingType()
	 */
	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IDirectEditing#getInitialValue(org.eclipse.graphiti.features.context.IDirectEditingContext)
	 */
	@Override
	public String getInitialValue(IDirectEditingContext context) {
		BaseElement be = getBusinessObject(context);
		EStructuralFeature feature = be.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
		if (feature!=null)
			return (String) be.eGet(feature);
		return ""; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature#setValue(java.lang.String, org.eclipse.graphiti.features.context.IDirectEditingContext)
	 */
	@Override
	public void setValue(String value, IDirectEditingContext context) {
		BaseElement be = getBusinessObject(context);
		EStructuralFeature feature = be.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
		if (feature!=null) {
			be.eSet(feature, value);
			PictogramElement pe = context.getPictogramElement();
			FeatureSupport.updateLabel(getFeatureProvider(), ((Shape) pe).getContainer(), null);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature#canDirectEdit(org.eclipse.graphiti.features.context.IDirectEditingContext)
	 */
	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof BaseElement && ((BaseElement)bo).eClass().getEStructuralFeature("name")!=null) //$NON-NLS-1$
			return true;
		return false;
	}

	private BaseElement getBusinessObject(IDirectEditingContext context) {
		return (BaseElement) getBusinessObjectForPictogramElement(context.getPictogramElement());
	}
}

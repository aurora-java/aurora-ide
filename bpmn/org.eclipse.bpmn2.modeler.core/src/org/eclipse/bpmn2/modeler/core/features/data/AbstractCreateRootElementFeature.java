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
package org.eclipse.bpmn2.modeler.core.features.data;

import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public abstract class AbstractCreateRootElementFeature<T extends RootElement> extends AbstractBpmn2CreateFeature<T> {

	public AbstractCreateRootElementFeature(IFeatureProvider fp) {
	    super(fp);
    }

	@Override
    public boolean canCreate(ICreateContext context) {
	    return true;
    }

	@Override
    public Object[] create(ICreateContext context) {
		RootElement element = createBusinessObject(context);
		if (element!=null) {
			PictogramElement pe = addGraphicalRepresentation(context, element);
			return new Object[] { element, pe };
		}

		return new Object[] { element };
    }
	
	@Override
	public EClass getBusinessObjectClass() {
		// TODO Auto-generated method stub
		return null;
	}

	protected abstract String getStencilImageId();
	
	@Override
	public String getCreateImageId() {
	    return getStencilImageId();
	}
	
	@Override
	public String getCreateLargeImageId() {
	    return getStencilImageId();
	}
}

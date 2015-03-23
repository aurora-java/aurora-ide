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
 * @author Innar Made
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.features.callactivity;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.GlobalManualTask;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;


public class CallGlobalManualTaskFeatureContainer extends AbstractCallGlobalTaskFeatureContainer<GlobalManualTask> {
	

	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return new CreateCallGlobalManualTaskFeature(fp);
	}

	public class CreateCallGlobalManualTaskFeature extends AbstractCreateCallGlobalTaskFeature<GlobalManualTask> {

		/**
		 * @param fp
		 */
		public CreateCallGlobalManualTaskFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public String getStencilImageId() {
			return ImageProvider.IMG_16_CALL_GLOBAL_MANUAL_TASK;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature#getFeatureClass()
		 */
		@Override
		public EClass getFeatureClass() {
			return Bpmn2Package.eINSTANCE.getGlobalManualTask();
		}
	}
}
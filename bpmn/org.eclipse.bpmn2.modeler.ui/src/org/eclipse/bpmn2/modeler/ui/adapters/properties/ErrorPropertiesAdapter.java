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

package org.eclipse.bpmn2.modeler.ui.adapters.properties;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Bob Brodt
 *
 */
public class ErrorPropertiesAdapter extends RootElementPropertiesAdapter<Error> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public ErrorPropertiesAdapter(AdapterFactory adapterFactory, Error object) {
		super(adapterFactory, object);

		EStructuralFeature feature = Bpmn2Package.eINSTANCE.getError_StructureRef();
		setProperty(feature, UI_IS_MULTI_CHOICE, Boolean.TRUE);
    	setFeatureDescriptor(feature, new ItemDefinitionRefFeatureDescriptor<Error>(this, object, feature));
		
    	setObjectDescriptor(new RootElementObjectDescriptor<Error>(this, object) {
			@Override
			public String getTextValue() {
				String text = ""; //$NON-NLS-1$
				if (object.getName()!=null) {
					text += object.getName();
				}
				else if (object.getErrorCode()!=null) {
					text += Messages.ErrorPropertiesAdapter_Error_Code + object.getErrorCode();
				}
				if (text.isEmpty())
					text = Messages.ErrorPropertiesAdapter_ID + object.getId();
				
				if (object.getStructureRef()!=null) {
					String type = "(" + ExtendedPropertiesProvider.getTextValue(object.getStructureRef()) +")"; //$NON-NLS-1$ //$NON-NLS-2$
					text += type;
				}
				return text;
			}
    	});
	}

}

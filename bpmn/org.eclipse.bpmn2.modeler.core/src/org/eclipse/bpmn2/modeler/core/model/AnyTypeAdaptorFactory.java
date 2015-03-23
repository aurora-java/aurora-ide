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

package org.eclipse.bpmn2.modeler.core.model;

import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * Adapter Factory class that adapts AnyType objects using our special ExtendedPropertiesAdapter.
 * This adapter knows how to resolve dynamic attributes and elements.
 */
class AnyTypeAdaptorFactory extends AdapterFactoryImpl {

	public static AnyTypeAdaptorFactory INSTANCE = new AnyTypeAdaptorFactory();
	
	@Override
	public boolean isFactoryForType(Object type) {
		return type == ExtendedPropertiesAdapter.class;
	}

	@Override
	public Adapter adaptNew(Notifier object, Object type) {
		Adapter adapter = null;
		if (type == ExtendedPropertiesAdapter.class) {
			if (object instanceof EClass) {
				object = ExtendedPropertiesAdapter.getDummyObject((EClass)object);
			}
			adapter = new ExtendedPropertiesAdapter(this, (EObject)object);
		}
		return adapter;
	}
}
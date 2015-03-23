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

import org.eclipse.bpmn2.modeler.core.LifecycleEvent;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent.EventType;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;

/**
 * Object Factory class used to construct dynamic EObjects. We use the XMLTypeFactory's
 * AnyType as our generic container for attributes and elements.
 * 
 * Once the object is created, an ExtendedPropertiesAdapter is added, which handles resolution
 * of dynamic attributes and elements.
 */
class AnyTypeObjectFactory extends EFactoryImpl {
	public ModelDecorator modelDecorator;
	
	public AnyTypeObjectFactory(ModelDecorator modelDecorator) {
		this.modelDecorator = modelDecorator;
	}
	
	@Override
	public EObject create(EClass eClass) {
		EObject object;
		if (eClass == EcorePackage.eINSTANCE.getEObject()) {
			object = XMLTypeFactory.eINSTANCE.createAnyType();
		}
		else if (getEPackage() != eClass.getEPackage()) {
			object = eClass.getEPackage().getEFactoryInstance().create(eClass);
		}
		else {
			object = super.create(eClass);
		}
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
    	TargetRuntime rt = TargetRuntime.getCurrentRuntime();
		rt.notify(new LifecycleEvent(EventType.BUSINESSOBJECT_CREATED, object));
		return object;
	}
	
	protected EObject basicCreate(EClass eClass) {
		return eClass.getInstanceClassName() == "java.util.Map$Entry" ? //$NON-NLS-1$
				new DynamicEObjectImpl.BasicEMapEntry<String, String>(eClass) :
				XMLTypeFactory.eINSTANCE.createAnyType();
	}
}
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
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.bpmn2.modeler.core.model.RootElementComparator;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Bob Brodt
 *
 */
public class RootElementPropertiesAdapter<T extends RootElement> extends ExtendedPropertiesAdapter<T> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public RootElementPropertiesAdapter(AdapterFactory adapterFactory, T object) {
		super(adapterFactory, object);
		
		// create a Root Element Reference feature descriptor for every feature that is a reference to a RootElement
		// the RootElementRefFeatureDescriptor is used to construct RootElement objects via its createFeature()
		// method, and inserts those objects into the document root.
		EList<EStructuralFeature>list = object.eClass().getEAllStructuralFeatures();
		for (EStructuralFeature ref : list) {
			EClassifier type = ref.getEType();
			if (type instanceof EClass) {
				EList<EClass> supertypes = ((EClass)type).getEAllSuperTypes();
				for (EClass st : supertypes) {
					if (st == Bpmn2Package.eINSTANCE.getRootElement()) {
						setFeatureDescriptor(ref, new RootElementRefFeatureDescriptor<T>(this,object,ref));
					}
				}
			}
		}
		
		setObjectDescriptor(new RootElementObjectDescriptor<T>(this, object));
	}
	
	public class RootElementObjectDescriptor<T extends RootElement> extends ObjectDescriptor<T> {

		public RootElementObjectDescriptor(ExtendedPropertiesAdapter<T> owner, T object) {
			super(owner, object);
		}
		
		@Override
		public T createObject(Resource resource, EClass eclass) {
			T rootElement = super.createObject(resource, eclass);
			
			Definitions definitions = null;
			if (resource!=null)
				definitions = ModelUtil.getDefinitions(resource);
			else
				definitions = ModelUtil.getDefinitions(rootElement);
			if (definitions!=null) {
				try {
					definitions.getRootElements().add(rootElement);
					ECollections.sort((EList<RootElement>)definitions.getRootElements(), new RootElementComparator());
				}
				catch (IllegalStateException e) {
					try {
						// well this is odd...
						// even though we did not have an open write transaction,
						// the getRootElements().add() does not get rolled back.
						definitions.getRootElements().remove(rootElement);
					}
					catch (Exception e2) {
						throw e;
					}
				}
			}
			return rootElement;
		}
	}

}

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

import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Bob Brodt
 *
 */
public class RootElementRefFeatureDescriptor<T extends BaseElement> extends FeatureDescriptor<T> {

	public RootElementRefFeatureDescriptor(ExtendedPropertiesAdapter<T> owner, T object, EStructuralFeature feature) {
		super(owner, object, feature);
	}

	@Override
	public EObject createFeature(Resource resource, EClass eClass) {
		if (resource==null && object.eResource()!=null)
			resource = object.eResource();
		
		EObject rootElement = null;
		if (eClass==null)
			eClass = (EClass)feature.getEType();
		else if (feature.getEType() != eClass) {
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(eClass);
			if (adapter!=null) {
				rootElement = adapter.getObjectDescriptor().createObject(resource, eClass);
			}
		}
		
		if (rootElement==null) {
			rootElement = Bpmn2ModelerFactory.create(resource, eClass);
		}
		
		return rootElement;
	}
	
	@Override
	public Hashtable<String, Object> getChoiceOfValues() {
		Hashtable<String,Object> choices = new Hashtable<String,Object>();
		Object value = object.eGet(feature);
		if (value instanceof EList) {
			Iterator iter = ((EList)value).iterator();
			while (iter.hasNext()) {
				value = iter.next();
				if (value instanceof RootElement)
					choices.put(ModelUtil.getTextValue(value), value);
			}
		}
		else if (value instanceof EObject) {
			EObject rootElement = (EObject) value;
			if (rootElement!=null)
				choices.put(getChoiceString(rootElement), rootElement);
		}
		Definitions definitions = ModelUtil.getDefinitions(object);
		if (definitions!=null) {
			for (RootElement re : definitions.getRootElements()) {
				if (re.eClass() == feature.getEType()) {
					choices.put(ExtendedPropertiesProvider.getTextValue(re), re);
				}
			}
		}
		return choices;
	}
}

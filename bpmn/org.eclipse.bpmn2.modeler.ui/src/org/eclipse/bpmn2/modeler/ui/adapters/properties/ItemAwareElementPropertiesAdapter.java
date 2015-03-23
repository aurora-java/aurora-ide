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
import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.DataState;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectPropertyProvider;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Bob Brodt
 *
 */
public class ItemAwareElementPropertiesAdapter<T extends ItemAwareElement> extends ExtendedPropertiesAdapter<T> {

	/**
	 * @param adapterFactory
	 * @param target
	 */
	public ItemAwareElementPropertiesAdapter(AdapterFactory adapterFactory, T object) {
		super(adapterFactory, object);
		
    	EStructuralFeature feature = Bpmn2Package.eINSTANCE.getItemAwareElement_ItemSubjectRef();
		setProperty(feature, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		setProperty(feature, UI_CAN_CREATE_NEW, Boolean.TRUE);
    	setFeatureDescriptor(feature,
			new ItemDefinitionRefFeatureDescriptor<T>(this, object, feature)
    	);
    	
    	feature = Bpmn2Package.eINSTANCE.getItemAwareElement_DataState();
    	setProperty(feature, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		setProperty(feature, UI_CAN_CREATE_NEW, Boolean.TRUE);
    	setFeatureDescriptor(feature,
			new FeatureDescriptor<T>(this, object,feature) {
    		
				@Override
				protected void internalSet(ItemAwareElement element, EStructuralFeature feature, Object value, int index) {
					if (value instanceof String) {
						// construct a DataState from the given name string
						DataState ds = Bpmn2ModelerFactory.create(DataState.class);
						ds.setName((String)value);
						value = ds;
					}
					if (value instanceof DataState) {
						DataState oldValue = (DataState) element.eGet(feature);
						if (value != oldValue) {
							// if this DataState belongs to some other ItemAwareElement, make a copy
							DataState newValue = null;
							if (((DataState)value).eContainer()!=null)
								newValue = EcoreUtil.copy((DataState) value);
							else
								newValue = (DataState)value;
							element.eSet(feature, newValue);
						}
					}
				}

				@Override
				public Hashtable<String, Object> getChoiceOfValues() {
					Hashtable<String,Object> choices = new Hashtable<String,Object>();
					try {
						Resource resource = ObjectPropertyProvider.getResource(object);
						List<DataState> states = ModelHandler.getAll(resource, DataState.class);
						for (DataState s : states) {
							String label = s.getName();
							if (label==null || label.isEmpty())
								label = Messages.ItemAwareElementPropertiesAdapter_ID + s.getId();
//							else
//								label += " (ID: " +  s.getId() + ")";
							choices.put(label,s);
						}
					} catch (Exception e) {
					}
					return choices;
				}
			}
    	);
	}
}

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

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.modeler.core.IBpmn2RuntimeExtension;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.runtime.ExpressionLanguageDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.runtime.TypeLanguageDescriptor;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Bob Brodt
 *
 */
public class DefinitionsPropertiesAdapter extends ExtendedPropertiesAdapter<Definitions> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public DefinitionsPropertiesAdapter(AdapterFactory adapterFactory, Definitions object) {
		super(adapterFactory, object);
    	
		EStructuralFeature feature = Bpmn2Package.eINSTANCE.getDefinitions_TypeLanguage();
		setProperty(feature, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		
		setFeatureDescriptor(feature, new FeatureDescriptor<Definitions>(this,object, feature) {

			@Override
			protected void internalSet(Definitions object, EStructuralFeature feature, Object value, int index) {
				super.internalSet(object, feature, value, index);
			}
			
			@Override
			public Hashtable<String, Object> getChoiceOfValues() {
				Hashtable<String,Object> choices = new Hashtable<String,Object>();
				TargetRuntime rt = TargetRuntime.getCurrentRuntime();
				for (TypeLanguageDescriptor tld : rt.getTypeLanguageDescriptors()) {
					choices.put(tld.getName(), tld.getUri());
				}
				return choices;
			}
		});
    	
		setObjectDescriptor(new ObjectDescriptor<Definitions>(this,object) {
			@Override
			public Definitions createObject(Resource resource, EClass eclass) {
				Definitions definitions = Bpmn2Factory.eINSTANCE.createDefinitions();
				TargetRuntime rt = TargetRuntime.getCurrentRuntime();
				definitions.setTypeLanguage(rt.getTypeLanguage());
				definitions.setExpressionLanguage(rt.getExpressionLanguage());
				return definitions;
			}
		});

		feature = Bpmn2Package.eINSTANCE.getDefinitions_ExpressionLanguage();
		setProperty(feature, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		
		setFeatureDescriptor(feature, new FeatureDescriptor<Definitions>(this,object, feature) {

			@Override
			protected void internalSet(Definitions object, EStructuralFeature feature, Object value, int index) {
				super.internalSet(object, feature, value, index);
			}
			
			@Override
			public Hashtable<String, Object> getChoiceOfValues() {
				Hashtable<String,Object> choices = new Hashtable<String,Object>();
				TargetRuntime rt = TargetRuntime.getCurrentRuntime();
				for (ExpressionLanguageDescriptor eld : rt.getExpressionLanguageDescriptors()) {
					choices.put(eld.getName(), eld.getUri());
				}
				return choices;
			}
		});
	}

}

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
import org.eclipse.bpmn2.Category;
import org.eclipse.bpmn2.CategoryValue;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Bob Brodt
 *
 */
public class CategoryValuePropertiesAdapter extends ExtendedPropertiesAdapter<CategoryValue> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public CategoryValuePropertiesAdapter(AdapterFactory adapterFactory, CategoryValue object) {
		super(adapterFactory, object);

		EStructuralFeature ref = Bpmn2Package.eINSTANCE.getCategoryValue_Value();
//		setProperty(ref, UI_CAN_CREATE_NEW, Boolean.TRUE);
//		setProperty(ref, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		setProperty(ref, UI_CAN_SET_NULL, Boolean.TRUE);
		// this is a read-only list
		setProperty(Bpmn2Package.eINSTANCE.getCategoryValue_CategorizedFlowElements(), UI_CAN_EDIT, Boolean.FALSE);
		
    	setFeatureDescriptor(ref,
			new FeatureDescriptor<CategoryValue>(this, object,ref) {
				@Override
				public String getTextValue() {
					return CategoryValuePropertiesAdapter.getDisplayName(object);
				}

				@Override
				public Object getValue() {
					return super.getValue();
				}

				@Override
		   		protected void internalSet(CategoryValue categoryValue, EStructuralFeature feature, Object value, int index) {
					if (value instanceof String) {
						int i = ((String) value).indexOf(":"); //$NON-NLS-1$
						if (i>=0)
							value = ((String) value).substring(i+1);
					}
					super.internalSet(categoryValue, feature, value, index);
				}
				
			}
    	
    	);
    	
		setObjectDescriptor(new ObjectDescriptor<CategoryValue>(this, object) {
			@Override
			public String getTextValue() {
				return CategoryValuePropertiesAdapter.getDisplayName(object);
			}
		});
	}
	
	private static String getDisplayName(CategoryValue categoryValue) {
		Category category = (Category) categoryValue.eContainer();
		String prefix = (category==null || category.getName()==null) ? "" : category.getName() + ":"; //$NON-NLS-1$
		String suffix = categoryValue.getValue();
		return prefix + suffix;
	}
}

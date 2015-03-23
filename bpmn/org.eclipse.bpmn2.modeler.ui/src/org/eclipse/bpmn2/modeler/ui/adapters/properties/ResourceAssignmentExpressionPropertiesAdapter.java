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
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ResourceAssignmentExpression;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Bob Brodt
 *
 */
public class ResourceAssignmentExpressionPropertiesAdapter extends ExtendedPropertiesAdapter<ResourceAssignmentExpression> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public ResourceAssignmentExpressionPropertiesAdapter(AdapterFactory adapterFactory, ResourceAssignmentExpression object) {
		super(adapterFactory, object);

    	final EStructuralFeature ref = Bpmn2Package.eINSTANCE.getResourceAssignmentExpression_Expression();
    	setFeatureDescriptor(ref,
			new FeatureDescriptor<ResourceAssignmentExpression>(this,object,ref) {

				@Override
				public String getTextValue() {
					String text = null;
					if (object.getExpression() instanceof FormalExpression) {
						text = ModelUtil.getExpressionBody((FormalExpression)object.getExpression());
					}
					if (text==null)
						return ""; //$NON-NLS-1$
					return text;
				}

				@Override
				protected void internalSet(ResourceAssignmentExpression rae, EStructuralFeature feature, Object value, int index) {
					if (!(rae.getExpression() instanceof FormalExpression)) {
						if (value instanceof String) {
							final FormalExpression e = Bpmn2ModelerFactory.create(FormalExpression.class);
							e.setBody((String) value);
							rae.eSet(feature, e);
						}
						else if (value instanceof FormalExpression) {
							rae.eSet(feature, (FormalExpression)value);
						}
					}
				}
    		}
    	);
    	setObjectDescriptor(new ObjectDescriptor<ResourceAssignmentExpression>(this,object) {
			@Override
			public String getTextValue() {
				return getFeatureDescriptor(ref).getTextValue();
			}
    	});
	}

}

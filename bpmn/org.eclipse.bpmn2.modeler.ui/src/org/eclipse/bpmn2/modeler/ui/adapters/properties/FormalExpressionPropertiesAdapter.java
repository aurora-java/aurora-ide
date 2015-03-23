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

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.runtime.ExpressionLanguageDescriptor;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * @author Bob Brodt
 *
 */
public class FormalExpressionPropertiesAdapter extends ExtendedPropertiesAdapter<FormalExpression> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public FormalExpressionPropertiesAdapter(AdapterFactory adapterFactory, FormalExpression object) {
		super(adapterFactory, object);

    	final EStructuralFeature body = Bpmn2Package.eINSTANCE.getFormalExpression_Body();
    	setFeatureDescriptor(body,
			new FeatureDescriptor<FormalExpression>(this,object,body) {
    		
    			@Override
    	   		protected void internalSet(FormalExpression formalExpression, EStructuralFeature feature, Object value, int index) {
    				String body = value==null ? null : value.toString();
    				InsertionAdapter.executeIfNeeded(formalExpression);
    				formalExpression.setBody(body);
    			}
    			
	    		@Override
	    		public String getTextValue() {
					String body = ModelUtil.getExpressionBody(object);
					if (body==null)
						return ""; //$NON-NLS-1$
					return body;
	    		}
	    		
				@Override
				public String getLabel() {
					if (object.eContainer() instanceof SequenceFlow)
						return Messages.FormalExpressionPropertiesAdapter_Constraint;
					return Messages.FormalExpressionPropertiesAdapter_Script;
				}

				@Override
				public boolean isMultiLine() {
					// formal expression body is always a multiline text field
					return true;
				}
			}
    	);
    	
    	final EStructuralFeature language = Bpmn2Package.eINSTANCE.getFormalExpression_Language();
		setProperty(language, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		setProperty(language, UI_CAN_SET_NULL, Boolean.TRUE);
    	setFeatureDescriptor(language,
    		new FeatureDescriptor<FormalExpression>(this,object,language) {
    		
				@Override
				public String getLabel() {
					return Messages.FormalExpressionPropertiesAdapter_Script_Language;
				}
	
				@Override
				public Hashtable<String, Object> getChoiceOfValues() {
					if (choiceOfValues==null) {
						choiceOfValues = new Hashtable<String, Object>();
						TargetRuntime rt = TargetRuntime.getCurrentRuntime();
						for (ExpressionLanguageDescriptor el : rt.getExpressionLanguageDescriptors()) {
							choiceOfValues.put(el.getName(), el.getUri());
						}
					}
					return choiceOfValues;
				}
				
			}
    	);
		
		EStructuralFeature feature = Bpmn2Package.eINSTANCE.getFormalExpression_EvaluatesToTypeRef();
		setProperty(feature, UI_IS_MULTI_CHOICE, Boolean.TRUE);
    	setFeatureDescriptor(feature, new ItemDefinitionRefFeatureDescriptor<FormalExpression>(this, object, feature));

		setObjectDescriptor(new ObjectDescriptor<FormalExpression>(this,object) {
			@Override
			public String getTextValue() {
				return getFeatureDescriptor(body).getTextValue();
			}
		});
	}

}

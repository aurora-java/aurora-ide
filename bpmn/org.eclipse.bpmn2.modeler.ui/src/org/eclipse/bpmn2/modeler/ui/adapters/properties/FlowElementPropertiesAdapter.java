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

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Bob Brodt
 * TODO: This was intended for elements that are both FlowElements and ItemAwareElements like
 * DataObject, DataObjectReference and DataStoreReference but alas, multiple inheritance ain't
 * happening in java. need to figure this out...
 */
public class FlowElementPropertiesAdapter<T extends FlowElement> extends ExtendedPropertiesAdapter<T> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public FlowElementPropertiesAdapter(AdapterFactory adapterFactory, T object) {
		super(adapterFactory, object);
		
    	EStructuralFeature f = Bpmn2Package.eINSTANCE.getFlowElement_Name();
		final FeatureDescriptor<T> fd = new FeatureDescriptor<T>(this,object,f) {

			@Override
			public void setTextValue(String text) {
				int i = text.lastIndexOf("/"); //$NON-NLS-1$
				if (i>=0)
					text = text.substring(i+1);
				text = text.trim();
				((T)object).setName(text);
			}

			@Override
			public String getTextValue() {
				String text = ""; //$NON-NLS-1$
				if (feature.getName().equals("name")) //$NON-NLS-1$
					return (String)object.getName();

				EStructuralFeature f = object.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
				if (f!=null) {
					String name = (String)object.eGet(f);
					if (name!=null && !name.isEmpty())
						text = name;
				}
				if (text.isEmpty()) {
					f = object.eClass().getEStructuralFeature("id"); //$NON-NLS-1$
					if (f!=null) {
						Object id = object.eGet(f);
						if (id!=null && !id.toString().isEmpty()) {
							String className = object.eClass().getName();
							String idString = id.toString();
							if (!idString.contains(className)) {
								text = ModelUtil.toCanonicalString(className) + " '" + id + "'"; //$NON-NLS-1$ //$NON-NLS-2$
							}
							else
								text = idString;
						}
					}
				}
				return text;
			}
			
			@Override
			public String getChoiceString(Object context) {
				T flowElement = adopt(context);
				String text = flowElement.getName();
				if (text==null || text.isEmpty())
					text = flowElement.getId();
				
				EObject container = flowElement.eContainer();
				while (container!=null) {
					if (container instanceof Participant) {
						container = ((Participant)container).getProcessRef();
						if (container==null)
							break;
					}
					if (container instanceof Activity || container instanceof Process) {
						text = ExtendedPropertiesProvider.getTextValue(container) + "/" + text; //$NON-NLS-1$
					}
					container = container.eContainer();
				}

				if (text!=null) {
					if (flowElement instanceof ItemAwareElement) {
						String type = ExtendedPropertiesProvider.getTextValue(((ItemAwareElement)flowElement).getItemSubjectRef());
						if (type!=null)
							text += " (" + type + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				return text;
			}
			
		};
		setFeatureDescriptor(f, fd);

		if (object instanceof ItemAwareElement) {
			f = Bpmn2Package.eINSTANCE.getItemAwareElement_ItemSubjectRef();
			setProperty(f, UI_IS_MULTI_CHOICE, Boolean.TRUE);
	    	setFeatureDescriptor(f, new ItemDefinitionRefFeatureDescriptor<T>(this, object, f) {

	    		@Override
	    		public Hashtable<String, Object> getChoiceOfValues() {
					return super.getChoiceOfValues();
	    		}
		
	    	});
		}
		
		setObjectDescriptor(new ObjectDescriptor<T>(this,object) {

			@Override
			public void setTextValue(String text) {
				fd.setTextValue(text);
				ModelUtil.setID(object);
			}

			@Override
			public String getTextValue() {
				return fd.getTextValue();
			}
		});
	}

}

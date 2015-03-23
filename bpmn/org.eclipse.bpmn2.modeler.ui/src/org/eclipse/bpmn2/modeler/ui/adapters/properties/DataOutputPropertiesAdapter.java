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

import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Bob Brodt
 *
 */
public class DataOutputPropertiesAdapter extends ItemAwareElementPropertiesAdapter<DataOutput> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public DataOutputPropertiesAdapter(AdapterFactory adapterFactory, DataOutput object) {
		super(adapterFactory, object);
    	EStructuralFeature f = Bpmn2Package.eINSTANCE.getDataOutput_Name();
		final FeatureDescriptor<DataOutput> fd = new FeatureDescriptor<DataOutput>(this,object,f) {

			@Override
			public void setTextValue(String text) {
				int i = text.lastIndexOf("/"); //$NON-NLS-1$
				if (i>=0)
					text = text.substring(i+1);
				text = text.trim();
				((DataOutput)object).setName(text);
			}

			@Override
			public String getChoiceString(Object context) {
				DataOutput dataOutput = adopt(context);
				String text = dataOutput.getName();
				if (text==null || text.isEmpty())
					text = dataOutput.getId();

				if (text!=null) {
					if (dataOutput.isIsCollection())
						text += "[]"; //$NON-NLS-1$
					String type = ExtendedPropertiesProvider.getTextValue(dataOutput.getItemSubjectRef());
					if (type!=null)
						text += " (" + type + ")"; //$NON-NLS-1$ //$NON-NLS-2$
				}
				return text;
			}
			
		};
		setFeatureDescriptor(f, fd);
		
		setObjectDescriptor(new ObjectDescriptor<DataOutput>(this,object) {

			@Override
			public void setTextValue(String text) {
				fd.setTextValue(text);
				ModelUtil.setID(object);
			}

			@Override
			public String getTextValue() {
				return fd.getChoiceString(object);
			}
		});
	}

	public static DataOutput createDataOutput(Resource resource, List<DataOutput> dataOutputs) {
		DataOutput dataOutput = Bpmn2ModelerFactory.create(resource,DataOutput.class);
		dataOutput.setName( generateName(dataOutputs) );
		dataOutputs.add(dataOutput);
	
		return dataOutput;
	}

	public static String generateName(List<DataOutput> dataOutputs) {
		String base = "output"; //$NON-NLS-1$
		int suffix = 1;
		String name = base + suffix;
		for (;;) {
			boolean found = false;
			for (DataOutput p : dataOutputs) {
				if (name.equals(p.getName())) {
					found = true;
					break;
				}
			}
			if (!found)
				break;
			name = base + ++suffix;
		}
		return name;
	}
}

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
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.ItemKind;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.utils.ImportUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Bob Brodt
 *
 */
public class ItemDefinitionRefFeatureDescriptor<T extends BaseElement> extends FeatureDescriptor<T> {

	protected ImportUtil importer = new ImportUtil();
	
	/**
	 * @param adapterFactory
	 * @param object
	 * @param feature
	 */
	public ItemDefinitionRefFeatureDescriptor(ExtendedPropertiesAdapter<T> owner, T object, EStructuralFeature feature) {
		super(owner, object, feature);
		// I found a couple of instances where this class was used for references that were NOT
		// RootElements - just check to make sure here...
		Assert.isTrue( RootElement.class.isAssignableFrom(feature.getEType().getInstanceClass()) );
	}
	
	@Override
	public String getLabel() {
		return ItemDefinitionPropertiesAdapter.getLabel();
	}

	@Override
	public String getTextValue() {
		ItemDefinition itemDefinition = (ItemDefinition) object.eGet(feature);
		return ItemDefinitionPropertiesAdapter.getDisplayName(itemDefinition);
	}
	
	@Override
	public EObject createFeature(Resource resource, EClass eClass) {
		ItemDefinition itemDefinition = ItemDefinitionPropertiesAdapter.createItemDefinition(object.eResource());
		return itemDefinition;
	}

	@Override
	public Object getValue() {
		ItemDefinition itemDefinition = (ItemDefinition) object.eGet(feature);
		return ItemDefinitionPropertiesAdapter.getStructureRef(itemDefinition);
	}

	@Override
	protected void internalSet(T object, EStructuralFeature feature, Object value, int index) {
		Definitions definitions = ModelUtil.getDefinitions(object);
		if (value instanceof String) {
			value = importer.createItemDefinition(definitions, null, (String)value, ItemKind.INFORMATION);
		}
		
		if (value==null || value instanceof ItemDefinition) {
			ItemDefinition itemDefinition = (ItemDefinition) value;

			super.internalSet(object, feature, itemDefinition, index);
			
			// if there are any DataInputAssociations or DataOutputAssociations that map to this object
			// then change their ItemDefinitions to match.
			if (definitions!=null) {
				TreeIterator<EObject> iter = definitions.eAllContents();
				while (iter.hasNext()) {
					EObject o = iter.next();
					if (o instanceof DataInput) {
						DataInput input = (DataInput) o;
						if (input.eContainer() instanceof InputOutputSpecification) {
							InputOutputSpecification ioSpec = (InputOutputSpecification) input.eContainer();
							if (ioSpec.eContainer() instanceof Activity) {
								Activity activity = (Activity) ioSpec.eContainer();
								for (DataInputAssociation dia : activity.getDataInputAssociations()) {
									if (!dia.getSourceRef().isEmpty() && dia.getSourceRef().get(0) == object) {
										input.setItemSubjectRef(itemDefinition);
									}
								}
							}
						}
						else if (input.eContainer() instanceof ThrowEvent) {
							ThrowEvent event = (ThrowEvent) input.eContainer();
							for (DataInputAssociation dia : event.getDataInputAssociation()) {
								if (!dia.getSourceRef().isEmpty() && dia.getSourceRef().get(0) == object) {
									input.setItemSubjectRef(itemDefinition);
								}
							}
						}
					}
					else if (o instanceof DataOutput) {
						DataOutput output = (DataOutput) o;
						if (output.eContainer() instanceof InputOutputSpecification) {
							InputOutputSpecification ioSpec = (InputOutputSpecification) output.eContainer();
							if (ioSpec.eContainer() instanceof Activity) {
								Activity activity = (Activity) ioSpec.eContainer();
								for (DataOutputAssociation doa : activity.getDataOutputAssociations()) {
									if (doa.getTargetRef() == object) {
										output.setItemSubjectRef(itemDefinition);
									}
								}
							}
						}
						else if (output.eContainer() instanceof CatchEvent) {
							CatchEvent event = (CatchEvent) output.eContainer();
							for (DataOutputAssociation doa : event.getDataOutputAssociation()) {
								if (doa.getTargetRef() == object) {
									output.setItemSubjectRef(itemDefinition);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public Hashtable<String, Object> getChoiceOfValues() {
		return ItemDefinitionPropertiesAdapter.getChoiceOfValues(object);
	}
}

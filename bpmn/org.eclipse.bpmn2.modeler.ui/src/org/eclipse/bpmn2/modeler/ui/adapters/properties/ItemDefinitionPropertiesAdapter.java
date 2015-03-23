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
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.ItemKind;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectPropertyProvider;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.runtime.TypeLanguageDescriptor;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.NamespaceUtil;
import org.eclipse.bpmn2.modeler.core.validation.SyntaxCheckerUtils;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.xsd.XSDElementDeclaration;

/**
 * @author Bob Brodt
 *
 */
public class ItemDefinitionPropertiesAdapter extends ExtendedPropertiesAdapter<ItemDefinition> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public ItemDefinitionPropertiesAdapter(AdapterFactory adapterFactory, ItemDefinition object) {
		super(adapterFactory, object);

		final EStructuralFeature ref = Bpmn2Package.eINSTANCE.getItemDefinition_StructureRef();
		setProperty(ref, UI_CAN_CREATE_NEW, Boolean.TRUE);
		setProperty(ref, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		
    	setFeatureDescriptor(ref,
			new FeatureDescriptor<ItemDefinition>(this,object,ref) {
    		
				@Override
				public String getLabel() {
					return Messages.ItemDefinitionPropertiesAdapter_Structure;
				}

				@Override
				public String getTextValue() {
					return ItemDefinitionPropertiesAdapter.getStructureName(object);
				}
				
	    		@Override
				public EObject createFeature(Resource resource, EClass eClass) {
					EObject structureRef = ModelUtil.createStringWrapper(""); //$NON-NLS-1$
					object.setStructureRef(structureRef);
					return structureRef;
	    		}

	    		@Override
	    		public Object getValue() {
					Object value = ItemDefinitionPropertiesAdapter.getStructureRef(object);
					if (value==null || (ModelUtil.isStringWrapper(value) && ModelUtil.getStringWrapperValue(value).isEmpty())) {
						value = object.getId();
					}
					return value;
	    		}

	    		@Override
	    		protected void internalSet(ItemDefinition itemDefinition, EStructuralFeature feature, Object value, int index) {
					if (value instanceof ItemDefinition) {
						value = ((ItemDefinition)value).getStructureRef();
						if (ModelUtil.isStringWrapper(value))
							value = ModelUtil.getStringWrapperTextValue(value);
					}
					if (value instanceof String) {
						if (itemDefinition.getStructureRef()==null) {
							String oldValue = ItemDefinitionPropertiesAdapter.getStructureName(itemDefinition);
							value = ((String) value).replace(oldValue, ""); //$NON-NLS-1$
						}
						value = SyntaxCheckerUtils.toXMLString((String)value);
						value = ModelUtil.createStringWrapper((String)value);
					}
					super.internalSet(itemDefinition, feature, value, index);
	    		}

				@Override
				public Hashtable<String, Object> getChoiceOfValues() {
					return ItemDefinitionPropertiesAdapter.getChoiceOfValues(object);
				}
			}
    	);
    	
		setObjectDescriptor(new ObjectDescriptor<ItemDefinition>(this,object) {
			
			@Override
			public String getTextValue() {
				return ItemDefinitionPropertiesAdapter.getDisplayName(object);
			}
			
			@Override
			public String getLabel() {
				return ItemDefinitionPropertiesAdapter.getLabel();
			}
			
			@Override
			public ItemDefinition createObject(Resource resource, EClass eclass) {
				ItemDefinition itemDefinition = ItemDefinitionPropertiesAdapter.createItemDefinition(resource);
				return itemDefinition;
			}

			@Override
			public boolean equals(Object obj) {
				if (obj instanceof ItemDefinition) {
					return super.equals(obj);
				}
				else if (obj instanceof String) {
					String otherWrapper = (String) obj;
					Object thisStructure = object.getStructureRef();
					if (thisStructure==null) {
						if (otherWrapper.isEmpty())
							return true;
						return false;
					}
					if (ModelUtil.isStringWrapper(thisStructure)) {
						String thisWrapper = ModelUtil.getStringWrapperTextValue(object.getStructureRef());
						return thisWrapper.equals(otherWrapper);
					}
				}
				return true;
			}
		});
	}


	/*
	 * Methods for dealing with ItemDefinitions
	 */
	public static String getLabel() {
		return Messages.ItemDefinitionPropertiesAdapter_ItemDefinition_Label;
	}

	public static ItemDefinition createItemDefinition(Resource resource) {
		ItemDefinition itemDefinition = Bpmn2ModelerFactory.eINSTANCE.createItemDefinition();
		ModelUtil.setID(itemDefinition, resource);
		Definitions defs = ModelUtil.getDefinitions(resource);
		if (defs!=null) {
			defs.getRootElements().add(itemDefinition);
		}

		return itemDefinition;
	}
	
	public static String getDisplayName(ItemDefinition itemDefinition) {
		String name = ""; //$NON-NLS-1$
		if (itemDefinition!=null) {
			name = getStructureName(itemDefinition);
			if (itemDefinition.isIsCollection())
				name += "[]"; //$NON-NLS-1$
		}
		return name;
	}
	
	public static String getStructureName(ItemDefinition itemDefinition) {
		Resource resource = ObjectPropertyProvider.getResource(itemDefinition);
		String name = ""; //$NON-NLS-1$
		if (itemDefinition!=null) {
			Object value = itemDefinition.getStructureRef();
			if (value instanceof XSDElementDeclaration) {
				XSDElementDeclaration elem = (XSDElementDeclaration)value;
				String prefix = NamespaceUtil.getPrefixForNamespace(resource, elem.getSchema().getTargetNamespace());
				name = elem.getName();
				if (prefix!=null && !prefix.isEmpty())
					name = prefix + ":" + name; //$NON-NLS-1$
			}
			else if (value instanceof Message) {
				Message message = (Message)value;
				name = NamespaceUtil.normalizeQName(resource,message.getQName());
			}
			else if (value instanceof Fault) {
				Fault fault = (Fault)value;
				String prefix = NamespaceUtil.getPrefixForNamespace(resource, fault.getEnclosingDefinition().getTargetNamespace());
				name = fault.getName();
				if (prefix!=null && !prefix.isEmpty())
					name = prefix + ":" + name; //$NON-NLS-1$
			}
			else if (ModelUtil.isStringWrapper(value)) {
				name = ModelUtil.getStringWrapperTextValue(value);
			}

			if (name==null || name.isEmpty()) {
				name = ModelUtil.generateUndefinedID(itemDefinition.getId());
			}
		}
		return name;
	}
	
	public static Object getStructureRef(ItemDefinition itemDefinition) {
		Object value = null;
		if (itemDefinition!=null)
			value = itemDefinition.getStructureRef();
		if (ModelUtil.isStringWrapper(value) && ModelUtil.getStringWrapperValue(value).isEmpty())
			value = null;
		return value;
	}

	public static Hashtable<String, Object> getChoiceOfValues(EObject context) {
		Hashtable<String,Object> choices = new Hashtable<String,Object>();
		if (context!=null) {
			String s;
			Definitions definitions = ModelUtil.getDefinitions(context);
			// add all existing ItemDefinitions
			List<ItemDefinition> itemDefs = ModelUtil.getAllRootElements(definitions, ItemDefinition.class);
			for (ItemDefinition id : itemDefs) {
				s = getDisplayName(id);
				if (s==null || s.isEmpty())
					s = id.getId();
				choices.put(s,id);
			}
			
			// add all primitive data types defined by the default typeLanguage
			String typeLanguage = definitions.getTypeLanguage();
			if (typeLanguage!=null) {
				TargetRuntime rt = TargetRuntime.getCurrentRuntime();
				TypeLanguageDescriptor tld = rt.getTypeLanguageDescriptor(typeLanguage);
				if (tld!=null) {
					for (TypeLanguageDescriptor.Type type : tld.getTypes()) {
						// We'll create temporary ItemDefinition objects for all of these
						// that don't already have ItemDefinitions. Attach an InsertionAdapter
						// that will cause these to be added to our Definitions if the user
						// changes anything in one of these temporary objects; this includes
						// setting the object as the target of an ItemAwareElement.itemSubjectRef 
						s = type.getQName(definitions.eResource());
						if (!choices.containsKey(s)) {
							ItemDefinition itemDefinition = Bpmn2ModelerFactory.eINSTANCE.createItemDefinition();
							itemDefinition.setStructureRef(ModelUtil.createStringWrapper(s));
							itemDefinition.setItemKind(ItemKind.INFORMATION);
							ModelUtil.setID(itemDefinition, context.eResource());
							InsertionAdapter.add(
									definitions,
									Bpmn2Package.eINSTANCE.getDefinitions_RootElements(),
									itemDefinition);
							choices.put(type.getQName(definitions.eResource()), itemDefinition);
						}
					}
				}
			}
		}
		return choices;
	}
	
}

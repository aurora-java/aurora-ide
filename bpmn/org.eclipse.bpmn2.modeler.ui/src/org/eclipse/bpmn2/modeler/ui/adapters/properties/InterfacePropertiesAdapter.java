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

import java.util.Iterator;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectPropertyProvider;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.NamespaceUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.wsdl.PortType;

/**
 * @author Bob Brodt
 *
 */
public class InterfacePropertiesAdapter extends ExtendedPropertiesAdapter<Interface> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public InterfacePropertiesAdapter(AdapterFactory adapterFactory, Interface object) {
		super(adapterFactory, object);
		
    	EStructuralFeature ref = Bpmn2Package.eINSTANCE.getInterface_ImplementationRef();
    	setFeatureDescriptor(ref, new ImplementationRefFeatureDescriptor<Interface>(this, adapterFactory, object, ref));
    	
    	ref = Bpmn2Package.eINSTANCE.getInterface_Operations();
    	setFeatureDescriptor(ref, new FeatureDescriptor<Interface>(this,object, ref) {

			@Override
			public EObject createFeature(Resource resource, EClass eclass) {
				Operation operation = Bpmn2ModelerFactory.create(Operation.class);
				ModelUtil.setID(operation, resource);
				operation.setName( ModelUtil.toCanonicalString(operation.getId()) );
//				InsertionAdapter.add(intf, Bpmn2Package.eINSTANCE.getInterface_Operations(), operation);
				object.getOperations().add(operation);
				return operation;
			}
			
			@Override
			public String getLabel() {
				return Messages.Interface_Operations_Label;
			}

			@Override
			public String getTextValue() {
				String text = ""; //$NON-NLS-1$
				Iterator<Operation> iter = object.getOperations().iterator();
				while (iter.hasNext()) {
					Operation op = iter.next();
					String name = op.getName();
					if (name==null || name.isEmpty())
						name = op.getId();
					text += name;
					if (iter.hasNext())
						text += ", "; //$NON-NLS-1$
				}
				return text;
			}
    	});
    	
    	
	}

	public static class ImplementationRefFeatureDescriptor<T extends BaseElement> extends FeatureDescriptor<T> {

		public ImplementationRefFeatureDescriptor(final ExtendedPropertiesAdapter<T> owner,
				AdapterFactory adapterFactory, final T object, final EStructuralFeature feature) {
			super(owner, object, feature);

	    	owner.setProperty(feature, UI_IS_MULTI_CHOICE, Boolean.FALSE);
		}

		@Override
		public String getTextValue() {
			Resource resource = ObjectPropertyProvider.getResource(object);
			String text = null;
			EObject value = (EObject)object.eGet(feature);
			if (value!=null) {
				if (ModelUtil.isStringWrapper(value)) {
					text = ModelUtil.getStringWrapperTextValue( object.eGet(feature) ); // + type;
				}
				else if (value instanceof PortType) {
					PortType portType = (PortType)value;
					text = NamespaceUtil.normalizeQName(resource, portType.getQName());
				}
				else if (value instanceof org.eclipse.wst.wsdl.Operation) {
					org.eclipse.wst.wsdl.Operation operation = (org.eclipse.wst.wsdl.Operation)value;
					String name = operation.getName();
					String namespace = operation.getEnclosingDefinition().getTargetNamespace();
					String prefix = NamespaceUtil.getPrefixForNamespace(resource, namespace);
					if (prefix!=null && !prefix.isEmpty())
						text = prefix + ":" + name; //$NON-NLS-1$
					else
						text += name;
				}
				if (text==null)
					return ModelUtil.getTextValue(object.eGet(feature));
				return text;
			}
			text = ModelUtil.getName(object);
			if (text==null || text.isEmpty())
				return object.getId();
			return text;
		}
		
		@Override
		public EObject createFeature(Resource resource, EClass eClass) {
			EObject impl = ModelUtil.createStringWrapper(""); //$NON-NLS-1$
			object.eSet(feature,impl);
			return impl;
		}

		@Override
		public Object getValue() {
			Object value = object.eGet(feature);
			if (value!=null) {
				if (ModelUtil.isStringWrapper(value))
					return value;
				return getTextValue();
			}
			return ModelUtil.createStringWrapper(""); //$NON-NLS-1$
		}

		@Override
		protected void internalSet(T object, EStructuralFeature feature, Object value, int index) {
			Resource resource = ObjectPropertyProvider.getResource(object);
			
			if (value instanceof PortType) {
				PortType portType = (PortType)value;
				value = NamespaceUtil.normalizeQName(resource, portType.getQName());;
			}
			else if (value instanceof Process) {
				Process process = (Process)value;
				if (process.getSupportedInterfaceRefs().size()>0)
					value = process.getSupportedInterfaceRefs().get(0).getImplementationRef();
			}

			if (value instanceof String) {
				value = ModelUtil.createStringWrapper((String)value);
			}
			else if (!ModelUtil.isStringWrapper(value)) {
				return;
			}
			super.internalSet(object, feature, value, index);
		}
	}

}

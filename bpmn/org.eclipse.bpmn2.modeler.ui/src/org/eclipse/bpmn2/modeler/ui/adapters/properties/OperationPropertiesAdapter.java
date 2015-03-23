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
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.adapters.properties.InterfacePropertiesAdapter.ImplementationRefFeatureDescriptor;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Bob Brodt
 *
 */
public class OperationPropertiesAdapter extends ExtendedPropertiesAdapter<Operation> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public OperationPropertiesAdapter(AdapterFactory adapterFactory, Operation object) {
		super(adapterFactory, object);

		EStructuralFeature ref = Bpmn2Package.eINSTANCE.getOperation_ImplementationRef();
    	setFeatureDescriptor(ref, new ImplementationRefFeatureDescriptor<Operation>(this, adapterFactory, object, ref));

    	ref = Bpmn2Package.eINSTANCE.getOperation_InMessageRef();
    	setFeatureDescriptor(ref, new RootElementRefFeatureDescriptor<Operation>(this,object,ref));
       	setProperty(ref, UI_IS_MULTI_CHOICE, Boolean.TRUE);

    	ref = Bpmn2Package.eINSTANCE.getOperation_OutMessageRef();
       	setFeatureDescriptor(ref, new RootElementRefFeatureDescriptor<Operation>(this,object,ref));
       	setProperty(ref, UI_IS_MULTI_CHOICE, Boolean.TRUE);
    	
    	setObjectDescriptor( new ObjectDescriptor<Operation>(this,object) {

			@Override
			public Operation createObject(Resource resource, EClass eclass) {
				Operation operation = super.createObject(resource, eclass);
				// find an Interface to which we can add this new Operation
				// Ask user which Interface if there are more than one.
				Definitions definitions = ModelUtil.getDefinitions(resource);
				Interface intf = null;
				final List<Interface> interfaces = ModelUtil.getAllRootElements(definitions, Interface.class);
				if (interfaces.size()>1) {
					ListDialog dialog = new ListDialog(Display.getCurrent().getActiveShell());
					dialog.setContentProvider(new IStructuredContentProvider() {
						
						@Override
						public void dispose() {
						}
			
						@Override
						public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
						}
			
						@Override
						public Object[] getElements(Object inputElement) {
							return interfaces.toArray();
						}
						
					});
					
					dialog.setLabelProvider(new ILabelProvider() {
			
						@Override
						public void addListener(ILabelProviderListener listener) {
						}
			
						@Override
						public void dispose() {
						}
			
						@Override
						public boolean isLabelProperty(Object element, String property) {
							return false;
						}
			
						@Override
						public void removeListener(ILabelProviderListener listener) {
						}
			
						@Override
						public Image getImage(Object element) {
							return null;
						}
			
						@Override
						public String getText(Object element) {
							return ModelUtil.toCanonicalString( ((Interface)element).getName() );
						}
						
					});
					
					dialog.setTitle(Messages.OperationPropertiesAdapter_Title);
					dialog.setAddCancelButton(true);
					dialog.setHelpAvailable(false);
					dialog.setInput(new Object());

					if (dialog.open()==Window.OK){
						intf = (Interface)dialog.getResult()[0];
					}
					else {
						intf = interfaces.get(0);
					}
				}
				else if (interfaces.size()==1) {
					intf = interfaces.get(0);
				}
				else if (definitions != null) {
					intf = Bpmn2ModelerFactory.create(resource, Interface.class);
//					InsertionAdapter.add(definitions, Bpmn2Package.eINSTANCE.getDefinitions_RootElements(), intf);
					definitions.getRootElements().add(intf);
				}
				
//				InsertionAdapter.add(intf, Bpmn2Package.eINSTANCE.getInterface_Operations(), operation);
				if (intf!=null) {
					intf.getOperations().add(operation);
				}
				return operation;
			}
    		
    	});
	}

}

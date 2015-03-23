/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 * All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.property.data;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractListComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultListComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultPropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.ListCompositeColumnProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.TableColumn;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ListDialog;

public class InterfacePropertySection extends DefaultPropertySection {

	@Override
	protected AbstractDetailComposite createSectionRoot() {
		return new InterfaceSectionRoot(this);
	}

	public InterfacePropertySection() {
		super();
	}
	
	@Override
	public boolean appliesTo(IWorkbenchPart part, ISelection selection) {
		if (super.appliesTo(part, selection)) {
			if (isModelObjectEnabled(Bpmn2Package.eINSTANCE.getInterface())) {
				EObject bo = getBusinessObjectForSelection(selection);
				return bo!=null;
			}
		}
		return false;
	}
	
	@Override
	public EObject getBusinessObjectForSelection(ISelection selection) {
		EObject bo = super.getBusinessObjectForSelection(selection);
		if (
				bo instanceof CallableElement || // includes Process
				bo instanceof Interface ||
				bo instanceof Collaboration // includes Choreography
			) {
			return bo;
		}
		
		return null;
	}
	
	public class InterfaceSectionRoot extends InterfaceDetailComposite {

		protected DefinedInterfaceListComposite definedInterfacesTable;
		protected ProvidedInterfaceListComposite providedInterfacesTable;
		
		/**
		 * @param parent
		 * @param style
		 */
		public InterfaceSectionRoot(Composite parent, int style) {
			super(parent, style);
		}

		/**
		 * @param section
		 */
		public InterfaceSectionRoot(AbstractBpmn2PropertySection section) {
			super(section);
		}

		@Override
		public void cleanBindings() {
			super.cleanBindings();
			definedInterfacesTable = null;
			providedInterfacesTable = null;
		}

		@Override
		public void createBindings(EObject be) {
			if (be instanceof Interface) {
				super.createBindings(be);
			}
			else {
				definedInterfacesTable = new DefinedInterfaceListComposite(this);
				definedInterfacesTable.bindList(be);
				definedInterfacesTable.setTitle(Messages.InterfacePropertySection_Interfaces_Title);
	
				if (be instanceof Participant) {
					providedInterfacesTable = new ProvidedInterfaceListComposite(this);
					providedInterfacesTable.bindList(be, getFeature(be, "interfaceRefs")); //$NON-NLS-1$
				}
				else if (be instanceof CallableElement) {
					CallableElement ce = (CallableElement)be;
					providedInterfacesTable = new ProvidedInterfaceListComposite(this);
					providedInterfacesTable.bindList(be, getFeature(be, "supportedInterfaceRefs")); //$NON-NLS-1$
				}
			}
		}
	}
	
	public class DefinedInterfaceListComposite extends DefaultListComposite {
		
		/**
		 * @param parent
		 */
		public DefinedInterfaceListComposite(Composite parent) {
			super(parent, DEFAULT_STYLE);
		}

		@Override
		public EClass getListItemClass(EObject object, EStructuralFeature feature) {
			return listItemClass = Bpmn2Package.eINSTANCE.getInterface();
		}

		public void bindList(EObject theobject) {
			Definitions defs = ModelUtil.getDefinitions(theobject);
			super.bindList(defs, Bpmn2Package.eINSTANCE.getDefinitions_RootElements());
		}

		@Override
		protected EObject addListItem(EObject object, EStructuralFeature feature) {
			Interface iface = Bpmn2ModelerFactory.create(object.eResource(), Interface.class);
			
			EList<EObject> list = (EList<EObject>)object.eGet(feature);
			list.add(iface);
			return iface;
		}
		
		@Override
		public ListCompositeColumnProvider getColumnProvider(EObject object, EStructuralFeature feature) {
			if (columnProvider==null) {
				columnProvider = new ListCompositeColumnProvider(this);
				TableColumn tc = new TableColumn(object, Bpmn2Package.eINSTANCE.getInterface_Name());
				columnProvider.add(tc);
				tc.setEditable(false);
				
				tc = new TableColumn(object,Bpmn2Package.eINSTANCE.getInterface_ImplementationRef());
				columnProvider.add(tc).setHeaderText(Messages.InterfacePropertySection_Implementation_Header);
				tc.setEditable(false);
			}
			return columnProvider;
		}
	}
	
	public static class ProvidedInterfaceListComposite extends DefaultListComposite {
		
		/**
		 * @param parent
		 */
		public ProvidedInterfaceListComposite(Composite parent) {
			// only allow details editing in DefinedInterfacesTable
			super(parent, AbstractListComposite.READ_ONLY_STYLE);
		}
		
		public void bindList(final EObject theobject, final EStructuralFeature thefeature) {
			super.bindList(theobject, thefeature);
			if (theobject instanceof Participant)
				setTitle(Messages.InterfacePropertySection_Participant_Title);
			else if (theobject instanceof CallableElement)
				setTitle(Messages.InterfacePropertySection_Process_Title);
		}
		
		@Override
		protected EObject addListItem(EObject object, EStructuralFeature feature) {
			Definitions defs = ModelUtil.getDefinitions(object);
			final List<Interface>items = new ArrayList<Interface>();
			for (EObject o : defs.getRootElements()) {
				if (o instanceof Interface) {
					if (object instanceof Participant) {
						Participant participant = (Participant)object;
						if (!participant.getInterfaceRefs().contains(o))
							items.add((Interface)o);
					} else if (object instanceof CallableElement) {
						CallableElement callableElement = (CallableElement)object;
						if (!callableElement.getSupportedInterfaceRefs().contains(o))
							items.add((Interface)o);
					}
				}
			}
			Interface iface = null;
			ListDialog dialog = new ListDialog(getShell());
			if (items.size()>1) {
				dialog.setContentProvider(new IStructuredContentProvider() {
		
					@Override
					public void dispose() {
					}
		
					@Override
					public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
					}
		
					@Override
					public Object[] getElements(Object inputElement) {
						return items.toArray();
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
						return ModelUtil.getName((BaseElement)element);
					}
					
				});
				dialog.setTitle(Messages.InterfacePropertySection_Interfaces_Title);
				dialog.setMessage(Messages.InterfacePropertySection_Interfaces_Message);
				dialog.setAddCancelButton(true);
				dialog.setHelpAvailable(false);
				dialog.setInput(new Object());

				if (dialog.open() == Window.OK) {
					iface = (Interface)dialog.getResult()[0];
				}
			}
			else if (items.size()==1) {
				iface = items.get(0);
			}
			else {
				MessageDialog.openInformation(getShell(), Messages.InterfacePropertySection_No_Interfaces_Error_Title,
						Messages.InterfacePropertySection_No_Interfaces_Error_Message
				);
			}
			
			if (iface!=null) {
				if (object instanceof Participant) {
					Participant participant = (Participant)object;
					participant.getInterfaceRefs().add(iface);
				} else if (object instanceof CallableElement) {
					CallableElement callableElement = (CallableElement)object;
					callableElement.getSupportedInterfaceRefs().add(iface);
				}
			}

			return iface;
		}
	}
	
}

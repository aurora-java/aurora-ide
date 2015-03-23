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
 * @author Innar Made
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.merrimac.clad;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;

public class DefaultPropertySection extends AbstractBpmn2PropertySection {

	protected AbstractPropertiesProvider propertiesProvider = null;
	protected List<Class> appliesToClasses = new ArrayList<Class>();

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.ui.property.AbstractBpmn2PropertySection#createSectionRoot()
	 */
	@Override
	protected AbstractDetailComposite createSectionRoot() {
		AbstractDetailComposite composite = null;
		// If this section applies to only one type of BPMN2 element, search the registry for
		// a Properties Composite of the given element type.
		if (appliesToClasses.size()==1) {
			composite = PropertiesCompositeFactory.INSTANCE.createDetailComposite(appliesToClasses.get(0), this);
		}
		else {
			composite = new DefaultDetailComposite(this);
		}
		composite.setPropertiesProvider(propertiesProvider);
		return composite;
	}

	@Override
	public AbstractDetailComposite createSectionRoot(Composite parent, int style) {
		AbstractDetailComposite composite = null;
		// If this section applies to only one type of BPMN2 element, search the registry for
		// a Properties Composite of the given element type.
		if (appliesToClasses.size()==1) {
			composite = PropertiesCompositeFactory.INSTANCE.createDetailComposite(appliesToClasses.get(0), parent, style);
		}
		else {
			composite = new DefaultDetailComposite(parent, style);
		}
		composite.setPropertiesProvider(propertiesProvider);
		return composite;
	}

	protected void setProperties(DefaultDetailComposite composite, String[] properties) {
		setProperties(properties);
		composite.setPropertiesProvider(propertiesProvider);
	}
	
	public void setProperties(String[] properties) {
		propertiesProvider = new AbstractPropertiesProvider(null) {
			String[] properties = null;
			@Override
			public String[] getProperties() {
				return properties;
			}
			
			public void setProperties(String[] properties) {
				this.properties = properties;
			}
		};
		propertiesProvider.setProperties(properties);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection#appliesTo(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public boolean appliesTo(IWorkbenchPart part, ISelection selection) {
		if (super.appliesTo(part, selection)) {
			if (appliesToClasses.isEmpty()) {
				return true;
			}
			
			PictogramElement pe = BusinessObjectUtil.getPictogramElementForSelection(selection);
			if (pe instanceof ConnectionDecorator) {
				pe = ((ConnectionDecorator)pe).getConnection();
				// this is a special hack to allow selection of connection decorator labels:
				// the connection decorator does not have a business object linked to it,
				// but its parent (the connection) does.
				if (pe.getLink()==null && pe.eContainer() instanceof PictogramElement)
					pe = (PictogramElement)pe.eContainer();
		
				// check all linked BusinessObjects for a match
				if (pe.getLink()!=null) {
					for (EObject eObj : pe.getLink().getBusinessObjects()){
						if (appliesTo(eObj))
							return true;
					}
				}
			}
			return appliesTo(getBusinessObjectForSelection(selection));
		}
		return false;
	}

	public boolean appliesTo(EObject eObj) {
		if (isModelObjectEnabled(eObj)) {
			for (Class c : appliesToClasses) {
				if (c.isInstance(eObj))
					return true;
			}
		}
		return false;
	}
	
	public void addAppliesToClass(Class c) {
		appliesToClasses.add(c);
	}
	
	public EObject getBusinessObjectForSelection(ISelection selection) {
		EObject bo = BusinessObjectUtil.getBusinessObjectForSelection(selection);
		if (bo instanceof BPMNDiagram) {
			if (((BPMNDiagram)bo).getPlane()!=null && ((BPMNDiagram)bo).getPlane().getBpmnElement()!=null)
				return ((BPMNDiagram)bo).getPlane().getBpmnElement();
		}
		return bo;
	}
}

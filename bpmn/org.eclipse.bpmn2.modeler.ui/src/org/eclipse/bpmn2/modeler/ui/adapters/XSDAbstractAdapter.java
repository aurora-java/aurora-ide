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
/**
 * 
 */
package org.eclipse.bpmn2.modeler.ui.adapters;

import java.util.Map;

import org.eclipse.bpmn2.modeler.core.adapters.AbstractAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.IStatefullAdapter;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.NamespaceUtil;
import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.bpmn2.modeler.ui.IConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xsd.XSDNamedComponent;

/**
 * @author Michal Chmielewski (michal.chmielewski@oracle.com)
 *
 */

public class XSDAbstractAdapter extends AbstractAdapter 
	implements ILabeledElement, IStatefullAdapter 
{
	
	public Image getLargeImage(Object object) {
		return Activator.getDefault().getImage(IConstants.ICON_PART_32);
	}	

	public Image getSmallImage(Object object) {
		return Activator.getDefault().getImage(IConstants.ICON_PART_16);
	}	

	public String getNamespacePrefix(String namespace) {
		Object context = getContext();
		// if this is 
		if (context instanceof EObject) {
			EObject eObject = (EObject) context;
			return NamespaceUtil.getNamespacePrefix(eObject, namespace);
		} else if (context instanceof Map) {
			return (String) ((Map)context).get(namespace);
		}
		
		return null;
	}

	public String getTypeLabel ( Object obj ) {
		return obj.getClass().getName();
	}
	
	
	public String getLabel ( Object obj  )
	{	
		XSDNamedComponent component = (XSDNamedComponent) ModelUtil.resolveXSDObject(obj);;
		String name =  component.getName();
		String ns = component.getTargetNamespace();
		
		if (name == null) {
			return getTypeLabel( obj );
		}
		
		if (ns == null) {
			return name;
		}
		
		String prefix = getNamespacePrefix(ns);
		
		if (prefix == null) {
			return "{" + ns + "}" + name; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return prefix + ":" + name; //$NON-NLS-1$
	}	
}
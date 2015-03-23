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
package org.eclipse.bpmn2.modeler.ui.property.tasks;

import java.util.List;

import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.TableColumn;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class IoParameterNameColumn extends TableColumn {

	public IoParameterNameColumn(EObject o, EStructuralFeature f) {
		super(o, f);
//		setEditable(false);
	}

	@Override
	public String getText(Object object) {
		String text = null;
		EObject o = (EObject)object;
		if (o instanceof InputSet){
			Object v = o.eGet(feature);
			for ( Object e : (List)v) {
				if (e instanceof DataInput) {
					if (text==null)
						text = ((DataInput)e).getName();
					else
						text += ", " + ((DataInput)e).getName(); //$NON-NLS-1$
				}
				else if (e instanceof DataOutput) {
					if (text==null)
						text = ((DataOutput)e).getName();
					else
						text += ", " + ((DataOutput)e).getName(); //$NON-NLS-1$
				}
			}
		}
		if (o instanceof OutputSet){
			Object v = o.eGet(feature);
			for ( Object e : (List)v) {
				if (e instanceof DataInput) {
					if (text==null)
						text = ((DataInput)e).getName();
					else
						text += ", " + ((DataInput)e).getName(); //$NON-NLS-1$
				}
				else if (e instanceof DataOutput) {
					if (text==null)
						text = ((DataOutput)e).getName();
					else
						text += ", " + ((DataOutput)e).getName(); //$NON-NLS-1$
				}
			}
		}
		else if (o instanceof DataInput) {
			text = ((DataInput)o).getName();
		}
		else if (o instanceof DataOutput) {
			text = ((DataOutput)o).getName();
		}
		if (text==null)
			return ""; //$NON-NLS-1$
		return text;
	}
}
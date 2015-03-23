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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.DataAssociation;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.TableColumn;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class IoParameterMappingColumn extends TableColumn {

	protected DataAssociation association = null;
	
	public IoParameterMappingColumn(EObject o, EStructuralFeature f) {
		super(o, f);
	}

	@Override
	public String getHeaderText() {
		if (headerText!=null)
			return headerText;
		
		return ExtendedPropertiesProvider.getLabel(
				Bpmn2Package.eINSTANCE.getDataAssociation(),
				feature.getName().startsWith("dataInput") ? //$NON-NLS-1$
					Bpmn2Package.eINSTANCE.getDataAssociation_SourceRef() :
					Bpmn2Package.eINSTANCE.getDataAssociation_TargetRef()
		);
	}

	@Override
	public String getText(Object element) {
		String result = null;
		ItemAwareElement source = (ItemAwareElement)element;
		for (DataAssociation da : getDataAssociations(source)) {
			String text = null;
			List<ItemAwareElement> target = getTargetElements(da);
			if (!target.isEmpty())
				for (ItemAwareElement e : target) {
					if (text==null)
						text = "";
					else
						text += ", ";
					text += ExtendedPropertiesProvider.getTextValue(e);
				}
			else {
				if (da.getTransformation()!=null) {
					text = Messages.IoParameterMappingColumn_Transform_Prefix + ExtendedPropertiesProvider.getTextValue(da.getTransformation());
				}
				if (!da.getAssignment().isEmpty()) {
					String text2 = null;
					for ( Assignment assign : da.getAssignment()) {
						FormalExpression expr  = getTargetExpression(da, assign);
						String body = ExtendedPropertiesProvider.getTextValue(expr);
						if (text2==null)
							text2 = "\"" + body + "\""; //$NON-NLS-1$ //$NON-NLS-2$
						else
							text2 += ",\n" + body; //$NON-NLS-1$
					}
					if (text==null)
						text = text2;
					else
						text += " + " + text2; //$NON-NLS-1$
				}
			}
			if (text!=null && !text.isEmpty()) {
				if (result==null)
					result = "";
				else
					result += ", ";
				result += text;
			}
		}
		return result==null ? "" : result; //$NON-NLS-1$
	}

	private FormalExpression getTargetExpression(DataAssociation da, Assignment assign) {
		return (FormalExpression) ((da instanceof DataInputAssociation) ? assign.getFrom() : assign.getTo());
	}

	private List<DataAssociation> getDataAssociations(ItemAwareElement element) {
		List<DataAssociation> list = null;
		if (element instanceof DataInput)
			list = getDataInputAssociations();
		if (element instanceof DataOutput)
			list = getDataOutputAssociations();
		List<DataAssociation> result = new ArrayList<DataAssociation>();
		if (list!=null) {
			for (DataAssociation da : list) {
				for (ItemAwareElement e : getSourceElements(da)) {
					if (element==e)
						result.add(da);
				}
			}
		}
		return result;
	}
	
	private List getDataInputAssociations() {
		if (object instanceof Activity) {
			return ((Activity)object).getDataInputAssociations();
		}
		else if (object instanceof ThrowEvent) {
			return ((ThrowEvent)object).getDataInputAssociation();
		}
		return null;
	}
	
	private List getDataOutputAssociations() {
		if (object instanceof Activity) {
			return ((Activity)object).getDataOutputAssociations();
		}
		else if (object instanceof CatchEvent) {
			return ((CatchEvent)object).getDataOutputAssociation();
		}
		return null;
	}
	
	private List<ItemAwareElement> getSourceElements(DataAssociation da) {
		List<ItemAwareElement> result = new ArrayList<ItemAwareElement>();
		if (da instanceof DataOutputAssociation) {
			if (da.getSourceRef().size()==1)
				result.addAll(da.getSourceRef());
		}
		else if (da instanceof DataInputAssociation) {
			result.add(da.getTargetRef());
		}
		return result;
	}
	
	private List<ItemAwareElement> getTargetElements(DataAssociation da) {
		List<ItemAwareElement> result = new ArrayList<ItemAwareElement>();
		if (da instanceof DataInputAssociation) {
			if (da.getSourceRef().size()==1)
				result.addAll(da.getSourceRef());
		}
		else if (da instanceof DataOutputAssociation) {
			result.add(da.getTargetRef());
		}
		return result;
	}
	
	private EStructuralFeature getTargetFeature(ItemAwareElement element) {
		return element instanceof DataInput ?
				Bpmn2Package.eINSTANCE.getDataAssociation_SourceRef() :
				Bpmn2Package.eINSTANCE.getDataAssociation_TargetRef();
	}
}
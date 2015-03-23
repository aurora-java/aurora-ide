/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
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

package org.eclipse.bpmn2.modeler.ui.features.data;

import java.util.Iterator;

import org.eclipse.bpmn2.DataState;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2UpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.AbstractUpdateBaseElementFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeService;

public class UpdateItemAwareElementFeature<T extends ItemAwareElement> extends AbstractUpdateBaseElementFeature<ItemAwareElement> {

	public UpdateItemAwareElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		IReason reason = super.updateNeeded(context);
		if (reason.toBoolean())
			return reason;

		IPeService peService = Graphiti.getPeService();
		ContainerShape container = (ContainerShape) context.getPictogramElement();
		ItemAwareElement element = (ItemAwareElement) getBusinessObjectForPictogramElement(container);
		
		EStructuralFeature isCollection = element.eClass().getEStructuralFeature("isCollection"); //$NON-NLS-1$
		if (isCollection!=null) {
			boolean newIsCollection = (Boolean) element.eGet(isCollection);
			boolean oldIsCollection = Boolean.parseBoolean(peService.getPropertyValue(container, GraphitiConstants.COLLECTION_PROPERTY));
			if (newIsCollection != oldIsCollection)
				return Reason.createTrueReason("Cardinality Changed"); //$NON-NLS-1$
		}
		
		String newDataState = getDataStateAsString(element);
		Object oldDataState = peService.getPropertyValue(container,GraphitiConstants.DATASTATE_PROPERTY);

		if (!newDataState.equals(oldDataState))
			return Reason.createTrueReason("Data State Changed"); //$NON-NLS-1$

		return Reason.createFalseReason();
	}

	@Override
	public boolean update(IUpdateContext context) {
		IPeService peService = Graphiti.getPeService();
		ContainerShape container = (ContainerShape) context.getPictogramElement();
		ItemAwareElement element = (ItemAwareElement) getBusinessObjectForPictogramElement(container);

		// Update the "is collection" feature if the ItemAwareElement has one
		EStructuralFeature isCollection = element.eClass().getEStructuralFeature("isCollection"); //$NON-NLS-1$
		if (isCollection!=null) {
			boolean newIsCollection = (Boolean) element.eGet(isCollection);
	
			// Find the shape that represents the "is collection" marker
			Iterator<Shape> iterator = peService.getAllContainedShapes(container).iterator();
			while (iterator.hasNext()) {
				Shape shape = iterator.next();
				String prop = peService.getPropertyValue(shape, GraphitiConstants.HIDEABLE_PROPERTY);
				if (prop != null && new Boolean(prop)) {
					Polyline line = (Polyline) shape.getGraphicsAlgorithm();
					line.setLineVisible(newIsCollection);
				}
			}
	
			peService.setPropertyValue(container, GraphitiConstants.COLLECTION_PROPERTY, Boolean.toString(newIsCollection));
		}
		
		// Update the Data State
		String newDataState = getDataStateAsString(element);
		peService.setPropertyValue(container, GraphitiConstants.DATASTATE_PROPERTY, newDataState);

		return true;
	}
	
	private String getDataStateAsString(ItemAwareElement element) {
		DataState dataState = element.getDataState();
		if (dataState==null)
			return ""; //$NON-NLS-1$
		String name = dataState.getName();
		if (name==null || name.isEmpty())
			name = "no_name"; //$NON-NLS-1$
		String id = dataState.getId();
		if (id==null || id.isEmpty())
			id = "no_id"; //$NON-NLS-1$
		return name + " - " + id; //$NON-NLS-1$
	}
}
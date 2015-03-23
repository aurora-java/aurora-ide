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
 * @author Ivar Meikas
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features.event.definitions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.IExecutionInfo;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IFeatureAndContext;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public abstract class AbstractAddEventDefinitionFeature<T extends EventDefinition>
	extends AbstractBpmn2AddFeature<T> {


	public AbstractAddEventDefinitionFeature(IFeatureProvider fp) {
		super(fp);
	}

	public IAddFeature getAddLabelFeature(IFeatureProvider fp) {
		// Event Definitions don't have labels
		return null;
	}

	@Override
	public boolean canAdd(IAddContext context) {
		Object bo = getBusinessObjectForPictogramElement(context.getTargetContainer());
		Object ed = context.getNewObject();
		if (bo instanceof Event && ed instanceof EventDefinition) {
			List<EClass> allowedItems = FeatureSupport.getAllowedEventDefinitions(
					(Event) bo,
					(BaseElement) context.getProperty(GraphitiConstants.PARENT_CONTAINER));
			if (allowedItems.contains(((EventDefinition)ed).eClass()))
				return true;
		}
		return false;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		ContainerShape containerShape = context.getTargetContainer();
		T businessObject = getBusinessObject(context);

		// hook for subclasses to inject extra code
		decorateShape(context, containerShape, businessObject);
		return null;
	}
	
	abstract protected void decorateShape(IAddContext context, ContainerShape containerShape, T businessObject);

	@Override
	public T getBusinessObject(IAddContext context) {
		Object businessObject = context.getProperty(GraphitiConstants.BUSINESS_OBJECT);
		if (businessObject instanceof EventDefinition)
			return (T)businessObject;
		return (T)context.getNewObject();
	}

	@Override
	public void putBusinessObject(IAddContext context, T businessObject) {
		context.putProperty(GraphitiConstants.BUSINESS_OBJECT, businessObject);
	}

	@Override
	public void postExecute(IExecutionInfo executionInfo) {
		List<PictogramElement> pes = new ArrayList<PictogramElement>();
		for (IFeatureAndContext fc : executionInfo.getExecutionList()) {
			IContext context = fc.getContext();
			IFeature feature = fc.getFeature();
			if (context instanceof AddContext) {
				AddContext ac = (AddContext)context;
				pes.add(ac.getTargetContainer());
			}
		}
		getDiagramEditor().setPictogramElementsForSelection(pes.toArray(new PictogramElement[pes.size()]));
	}
}
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

import java.util.List;

import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public abstract class AbstractCreateEventDefinitionFeature<T extends EventDefinition> extends AbstractBpmn2CreateFeature<T> {

	public AbstractCreateEventDefinitionFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		if (!super.canCreate(context))
			return false;

		Object bo = getBusinessObjectForPictogramElement(context.getTargetContainer());
		if (bo instanceof Event) {
			List<EClass> allowedItems = FeatureSupport.getAllowedEventDefinitions(
					(Event) bo,
					context.getProperty(GraphitiConstants.PARENT_CONTAINER));
			if (allowedItems.contains(getBusinessObjectClass()))
				return true;
		}
		return false;
	}

	@Override
	public Object[] create(ICreateContext context) {
		Event e = (Event) getBusinessObjectForPictogramElement(context.getTargetContainer());
		List<EventDefinition> eventDefinitions = ModelUtil.getEventDefinitions(e);
		EventDefinition definition = createBusinessObject(context);
		eventDefinitions.add(definition);
		PictogramElement pe = addGraphicalRepresentation(context, definition);
		ModelUtil.setID(definition);
		return new Object[] { definition, pe };
	}

	protected abstract String getStencilImageId();

	@Override
	public String getCreateImageId() {
		return getStencilImageId();
	}

	@Override
	public String getCreateLargeImageId() {
		return getStencilImageId(); // FIXME
	}

//	@Override
//	public void postExecute(IExecutionInfo executionInfo) {
//		for (IFeatureAndContext fc : executionInfo.getExecutionList()) {
//			IContext context = fc.getContext();
//			if (context instanceof ICreateContext) {
//				ICreateContext cc = (ICreateContext)context;
//				T businessObject = getBusinessObject(cc);
//				Bpmn2Preferences prefs = (Bpmn2Preferences) ((DiagramEditor) getDiagramEditor()).getAdapter(Bpmn2Preferences.class);
//				if (prefs!=null && prefs.getShowPopupConfigDialog(businessObject)) {
//					ObjectEditingDialog dialog =
//							new ObjectEditingDialog((DiagramEditor)getDiagramEditor(), businessObject);
//					dialog.open();
//				}
//			}
//		}
//	}
}
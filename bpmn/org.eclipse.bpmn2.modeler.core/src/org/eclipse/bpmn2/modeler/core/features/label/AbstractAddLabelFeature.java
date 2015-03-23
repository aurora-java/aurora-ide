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

package org.eclipse.bpmn2.modeler.core.features.label;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.features.impl.AbstractAddPictogramElementFeature;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;

/**
 *
 */
abstract public class AbstractAddLabelFeature extends AbstractAddPictogramElementFeature {

	protected final static IGaService gaService = Graphiti.getGaService();
	protected final static IPeService peService = Graphiti.getPeService();

	/**
	 * @param fp
	 */
	public AbstractAddLabelFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public boolean canAdd(IAddContext context) {
		return true;
	}

	abstract public PictogramElement add(IAddContext context);

	protected AbstractText createText(PictogramElement labelOwner, Shape labelShape, BaseElement businessObject) {
		// the actual text will be set into the Text GA during the initial update()
		// which is done immediately after the add()
		AbstractText text = createText(labelShape, ""); //$NON-NLS-1$
		applyStyle(text, businessObject);
		peService.setPropertyValue(labelShape, GraphitiConstants.LABEL_SHAPE, Boolean.toString(true));
		
		link(labelShape, new Object[] {businessObject, labelOwner});
		link(labelOwner, new Object[] {labelShape});
		
		return text;
	}
	
	protected AbstractText createText(Shape labelShape, String labelText) {
		return gaService.createDefaultMultiText(getDiagram(), labelShape, labelText);
	}

	public String getLabelString(BaseElement element) {
		return ModelUtil.getName(element);
	}

	public void applyStyle(AbstractText text, BaseElement be) {
		StyleUtil.applyStyle(text, be);
		text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
		text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
	}
	
	protected PictogramElement getLabelOwner(IAddContext context) {
		return FeatureSupport.getLabelOwner(context);
	}
	
	public IReason updatePictogramElement(IAddContext addContext, PictogramElement pe) {
		UpdateContext updateContext = new UpdateContext(pe);
		for (Object key : addContext.getPropertyKeys()) {
			Object value = addContext.getProperty(key);
			updateContext.putProperty(key, value);
		}
		return getFeatureProvider().updateIfPossible(updateContext);
	}
}

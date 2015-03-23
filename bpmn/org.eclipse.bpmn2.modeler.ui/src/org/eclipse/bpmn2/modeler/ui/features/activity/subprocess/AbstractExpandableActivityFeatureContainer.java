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
package org.eclipse.bpmn2.modeler.ui.features.activity.subprocess;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.features.MultiUpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.activity.task.DirectEditTaskFeature;
import org.eclipse.bpmn2.modeler.core.features.label.UpdateLabelFeature;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle.LabelPosition;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.ui.features.activity.AbstractActivityFeatureContainer;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public abstract class AbstractExpandableActivityFeatureContainer extends AbstractActivityFeatureContainer {

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IFeatureProvider fp) {
		return new DirectEditTaskFeature(fp);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		MultiUpdateFeature multiUpdate = (MultiUpdateFeature) super.getUpdateFeature(fp);
		// we'll provide our own label update feature so remove the one from super()
		for (IUpdateFeature feature : multiUpdate.getFeatures()) {
			if (feature instanceof UpdateLabelFeature) {
				multiUpdate.getFeatures().remove(feature);
				break;
			}
		}
		multiUpdate.addFeature(new UpdateExpandableActivityFeature(fp));
		multiUpdate.addFeature(new UpdateLabelFeature(fp) {
			
			protected LabelPosition getHorizontalLabelPosition(AbstractText text) {
				PictogramElement pe = FeatureSupport.getLabelOwner(text);
				BaseElement be = BusinessObjectUtil.getFirstBaseElement(pe);
				if (FeatureSupport.isElementExpanded(be)) {
					return LabelPosition.LEFT;
				}
				return LabelPosition.CENTER;
			}
			
			protected LabelPosition getVerticalLabelPosition(AbstractText text) {
				PictogramElement pe = FeatureSupport.getLabelOwner(text);
				BaseElement be = BusinessObjectUtil.getFirstBaseElement(pe);
				if (FeatureSupport.isElementExpanded(be)) {
					return LabelPosition.TOP;
				}
				return LabelPosition.CENTER;
			}
		});
		return multiUpdate;
	}

	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return new LayoutExpandableActivityFeature(fp);
	}

	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		return new ResizeExpandableActivityFeature(fp);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return new DeleteExpandableActivityFeature(fp);
	}

	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		ICustomFeature[] superFeatures = super.getCustomFeatures(fp);
		ICustomFeature[] thisFeatures = new ICustomFeature[4 + superFeatures.length];
		thisFeatures[0] = new ExpandFlowNodeFeature(fp);
		thisFeatures[1] = new CollapseFlowNodeFeature(fp);
		thisFeatures[2] = new PushdownFeature(fp);
		thisFeatures[3] = new PullupFeature(fp);
		for (int i=0; i<superFeatures.length; ++i)
			thisFeatures[4+i] = superFeatures[i];
		return thisFeatures;
	}
}
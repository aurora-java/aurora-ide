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
package org.eclipse.bpmn2.modeler.core.features;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.ui.views.properties.IPropertySheetPage;

// TODO: Auto-generated Javadoc
/**
 * The Class MultiUpdateFeature.
 */
public class MultiUpdateFeature extends AbstractUpdateFeature {

	/** The features. */
	protected List<IUpdateFeature> features = new ArrayList<IUpdateFeature>();
	protected boolean[] updateNeeded;
	protected boolean[] canUpdate;
	IReason reason = null;

	/**
	 * Instantiates a new multi update feature.
	 *
	 * @param fp the fp
	 */
	public MultiUpdateFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IUpdate#canUpdate(org.eclipse.graphiti.features.context.IUpdateContext)
	 */
	@Override
	public boolean canUpdate(IUpdateContext context) {
		// keep track of which features can update so we don't have to waste time
		// asking each feature again during @{link #updateNeeded(IUpdateContext)}
		boolean result = false;
		if (canUpdate==null) {
			canUpdate = new boolean[features.size()];
			int i = 0;
			for (IUpdateFeature f : features) {
				if (f.canUpdate(context)) {
					canUpdate[i] = true;
					result = true;
				}
				++i;
			}
		}
		else {
			for (int i=0; i<canUpdate.length; ++i) {
				if (canUpdate[i]) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IUpdate#updateNeeded(org.eclipse.graphiti.features.context.IUpdateContext)
	 */
	@Override
	public IReason updateNeeded(IUpdateContext context) {
		if (reason==null) {
			String text = null;
			canUpdate(context);
			// keep track of which features need updating so we don't have to waste time
			// asking each feature again during @{link #update(IUpdateContext)}
			updateNeeded = new boolean[features.size()];
			int i = 0;
			for (IUpdateFeature f : features) {
				// This MultiUpdateFeature will be called for PictogramElement
				// children but not all Features in our list may apply to all
				// children of the parent PE, so we need to ask the Feature
				// again if it applies this PE.
				if (canUpdate[i]) {
					IReason reason = f.updateNeeded(context);
					if (reason.toBoolean()) {
						updateNeeded[i] = true;
						if (text==null) {
							text = f.getName() + ": " + reason.getText();
						}
						else
							text += "\n" + f.getName() + ": " + reason.getText(); //$NON-NLS-1$
					}
				}
				++i;
			}
			if (text!=null)
				reason = Reason.createTrueReason(text);
			else
				reason = Reason.createFalseReason();
		}
		return reason;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IUpdate#update(org.eclipse.graphiti.features.context.IUpdateContext)
	 */
	@Override
	public boolean update(IUpdateContext context) {
		boolean updated = false;
		boolean forceUpdate =  Boolean.TRUE.equals(context.getProperty(GraphitiConstants.FORCE_UPDATE_ALL));
		
		updateNeeded(context);
		
		int i = 0;
		for (IUpdateFeature f : features) {
			if ((updateNeeded[i] || forceUpdate) && f.update(context)) {
				updated = true;
			}
			++i;
		}
		return updated;
	}

	/**
	 * Adds the update feature.
	 *
	 * @param feature the feature
	 */
	public void addFeature(IUpdateFeature feature) {
		if (feature != null) {
			features.add(feature);
		}
	}
	
	/**
	 * Get the list of individual UpdateFeatures that will be evaluated.
	 * 
	 * @return
	 */
	public List<IUpdateFeature> getFeatures() {
		return features;
	}
}
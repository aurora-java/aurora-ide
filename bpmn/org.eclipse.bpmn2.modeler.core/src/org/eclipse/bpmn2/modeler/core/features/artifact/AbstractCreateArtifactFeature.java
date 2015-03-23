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
package org.eclipse.bpmn2.modeler.core.features.artifact;

import org.eclipse.bpmn2.Artifact;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.SubChoreography;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public abstract class AbstractCreateArtifactFeature<T extends Artifact> extends AbstractBpmn2CreateFeature<T> {

	public AbstractCreateArtifactFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return FeatureSupport.isValidArtifactTarget(context);
	}

	@Override
	public Object[] create(ICreateContext context) {
		T artifact = createBusinessObject(context);
		PictogramElement pe = addGraphicalRepresentation(context, artifact);
		return new Object[] { artifact, pe };
	}

	protected abstract String getStencilImageId();

	@Override
	public String getCreateImageId() {
		return getStencilImageId();
	}

	@Override
	public String getCreateLargeImageId() {
		return getStencilImageId();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public T createBusinessObject(ICreateContext context) {
		T artifact = super.createBusinessObject(context);
		EObject bo = BusinessObjectUtil.getBusinessObjectForPictogramElement(context.getTargetContainer());
		if (bo instanceof BPMNDiagram) {
			bo = ((BPMNDiagram)bo).getPlane().getBpmnElement();
		}
		else if (bo instanceof Participant) {
			bo = ((Participant)bo).getProcessRef();
		}
		else if (bo instanceof Lane) {
			while (!(bo instanceof Process) && bo!=null) {
				bo = bo.eContainer();
			}
		}
		if (bo instanceof Collaboration) {
			((Collaboration)bo).getArtifacts().add(artifact);
		}
		else if (bo instanceof Process) {
			((Process)bo).getArtifacts().add(artifact);
		}
		else if (bo instanceof SubProcess) {
			((SubProcess)bo).getArtifacts().add(artifact);
		}
		else if (bo instanceof SubChoreography) {
			((SubChoreography)bo).getArtifacts().add(artifact);
		}
		return artifact;
	}
}
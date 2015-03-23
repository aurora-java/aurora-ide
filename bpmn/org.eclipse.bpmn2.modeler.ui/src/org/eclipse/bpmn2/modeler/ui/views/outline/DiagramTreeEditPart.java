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
package org.eclipse.bpmn2.modeler.ui.views.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class DiagramTreeEditPart extends AbstractGraphicsTreeEditPart {

	List<Diagram> diagrams;
	int id;
	
	public DiagramTreeEditPart(int id, Diagram diagram) {
		super(null, diagram);
		setDiagramEditPart(this);
		this.id = id;
	}

	public Diagram getDiagram() {
		return (Diagram) getModel();
	}

	public List<Diagram> getAllDiagrams() {
		if (diagrams==null)
			diagrams = new ArrayList<Diagram>();
		return diagrams;
	}
	
	// ======================= overwriteable behaviour ========================

	@Override
	protected void createEditPolicies() {
	}

	/**
	 * Returns the children of this EditPart.
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@Override
	protected List<Object> getModelChildren() {
		List<Object> retList = new ArrayList<Object>();
		Diagram diagram = getDiagram();
		BPMNDiagram bpmnDiagram = (BPMNDiagram) BusinessObjectUtil.getBusinessObjectForPictogramElement(diagram);
		if (bpmnDiagram!=null) {
			Definitions definitions = (Definitions)bpmnDiagram.eContainer();
			if (id == BPMN2EditorOutlinePage.ID_BUSINESS_MODEL_OUTLINE) {
				for (RootElement elem : definitions.getRootElements()) {
					boolean addIt = true;
					if (elem instanceof Process) {
						// don't include this Process in root children if it's already
						// being accounted for inside a Pool
						TreeIterator<EObject> iter = definitions.eAllContents();
						while (iter.hasNext()) {
							EObject next = iter.next();
							if (next instanceof Participant) {
								Participant participant = (Participant)next;
								if (participant.getProcessRef() == elem) {
									addIt = false;
									break;
								}
							}
						}
					}
					if (addIt)
						retList.add(elem);
				}
			}
			else if (id == BPMN2EditorOutlinePage.ID_INTERCHANGE_MODEL_OUTLINE)
				retList.addAll(definitions.getDiagrams());
			
			// build a list of all Graphiti Diagrams - these will be needed by other
			// TreeEditParts to map the business objects to PictogramElements
			ResourceSet resourceSet = diagram.eResource().getResourceSet();
			for (BPMNDiagram bd : definitions.getDiagrams()) {
				getAllDiagrams().add( DIUtils.findDiagram(resourceSet, bd) );
			}
		}		
		return retList;
	}

	@Override
	protected String getText() {
		BPMNDiagram bpmnDiagram = (BPMNDiagram) BusinessObjectUtil.getBusinessObjectForPictogramElement(getDiagram());
		if (bpmnDiagram!=null && bpmnDiagram.getName()!=null)
			return bpmnDiagram.getName();
		return ""; //$NON-NLS-1$
		
	}
}
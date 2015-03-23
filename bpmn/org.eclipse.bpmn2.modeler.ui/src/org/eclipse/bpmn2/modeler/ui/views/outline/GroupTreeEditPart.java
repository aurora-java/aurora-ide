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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

public class GroupTreeEditPart extends AbstractGraphicsTreeEditPart {

	public GroupTreeEditPart(DiagramTreeEditPart dep, Group baseElement) {
		super(dep, baseElement);
	}

	public BaseElement getBaseElement() {
		return (BaseElement) getModel();
	}

	public Group getGroup() {
		return (Group) getModel();
	}
	
	// ======================= overwriteable behaviour ========================

	/**
	 * Creates the EditPolicies of this EditPart. Subclasses often overwrite
	 * this method to change the behaviour of the editpart.
	 */
	@Override
	protected void createEditPolicies() {
	}

	@Override
	protected List<Object> getModelChildren() {
		List<Object> retList = new ArrayList<Object>();
		BPMN2EditorOutlineTreeViewer viewer = (BPMN2EditorOutlineTreeViewer)getViewer();
		if (viewer.diagramEditor!=null) {
			// We'll use the FeatureSupport utility class to find all of the
			// shapes that are contained in this group. To do that we'll have
			// to reach out to the Graphiti graphical viewer.
			Group group = getGroup();
			Diagram diagram = viewer.diagramEditor.getDiagramTypeProvider().getDiagram();
			ContainerShape groupShape = null;
			// find the ContainerShape for this Group BPMN2 element 
			List<PictogramElement> pes = Graphiti.getLinkService().getPictogramElements(diagram, group);
			for (PictogramElement pe : pes) {
				if (pe instanceof ContainerShape) {
					groupShape = (ContainerShape)pe;
					List<ContainerShape> groupedShapes = FeatureSupport.findGroupedShapes(groupShape);
					// now get all of the BPMN2 elements for these contained shapes;
					// these are the "children" of this Group element
					for (ContainerShape shape : groupedShapes) {
						BaseElement be = BusinessObjectUtil.getFirstBaseElement(shape);
						if (be!=null && !retList.contains(be)) {
							retList.add(be);
						}
					}
					
					// put these in some kind of order because selection will cause them
					// to switch Z-order, which will result in a different order each time
					// in the Outline tree
					Collections.sort(retList, new Comparator<Object>() {
						@Override
						public int compare(Object arg0, Object arg1) {
							String id0 = ((BaseElement)arg0).getId();
							String id1 = ((BaseElement)arg1).getId();
							if (id0!=null)
								return id0.compareTo(id1);
							return -1;
						}
					});
					// and we're outta here
					break;
				}
			}
		}
		return retList;
	}
}
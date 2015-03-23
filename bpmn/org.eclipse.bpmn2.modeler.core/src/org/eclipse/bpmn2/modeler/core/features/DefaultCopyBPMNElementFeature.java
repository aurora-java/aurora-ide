/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractCopyFeature;

/**
 * Default Graphiti {@code CopyFeature} base class for all BPMN2 model elements which
 * are associated with BPMN DI elements.
 */
public class DefaultCopyBPMNElementFeature extends AbstractCopyFeature {

	/**
	 * Instantiates a new default {@code CopyFeature).
	 *
	 * @param fp the Feature Provider
	 */
	public DefaultCopyBPMNElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.ICopyFeature#canCopy(org.eclipse.graphiti.features.context.ICopyContext)
	 */
	@Override
	public boolean canCopy(ICopyContext context) {
        final PictogramElement[] pes = context.getPictogramElements();
        if (pes == null || pes.length == 0) {  // nothing selected
            return false;
        }
       
        // return true if all selected elements have linked BaseElements
        for (PictogramElement pe : pes) {
            final Object bo = BusinessObjectUtil.getFirstBaseElement(pe);
            if (!(bo instanceof BaseElement)) {
                return false;
            }
        }
        return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.ICopyFeature#copy(org.eclipse.graphiti.features.context.ICopyContext)
	 */
	@Override
	public void copy(ICopyContext context) {
        PictogramElement[] pes = context.getPictogramElements();
        List<PictogramElement> copied = new ArrayList<PictogramElement>();
        for (int i = 0; i < pes.length; i++) {
            PictogramElement pe = pes[i];
            // only copy connections if both source and target Shapes
            // are also selected (i.e. don't copy a "dangling" connection)
            if (pe instanceof Connection) {
            	Connection connection = (Connection)pe;
            	PictogramElement source = connection.getStart().getParent();
            	PictogramElement target = connection.getEnd().getParent();
            	boolean containsSource = false;
            	boolean containsTarget = false;
            	for (PictogramElement p : pes) {
            		if (source==p)
            			containsSource = true;
            		else if (target==p)
            			containsTarget = true;
            	}
        		if (containsSource && containsTarget) {
        			copied.add(pe);
        		}
            }
            else {
    			copied.add(pe);
            }
        }
        
        // include all connections between the selected shapes, even if they
        // are not selected.
        copied.addAll(findAllConnections(copied));

        // remove PEs that are contained in FlowElementsContainers
        List<PictogramElement> ignored = new ArrayList<PictogramElement>();
        for (PictogramElement pe : copied) {
        	if (pe instanceof ContainerShape) {
        		for (PictogramElement childPe : ((ContainerShape) pe).getChildren()) {
        			if (copied.contains(childPe))
        				ignored.add(childPe);
        		}
        	}
        }
        copied.removeAll(ignored);
        
        // copy all PictogramElements to the clipboard
        putToClipboard(copied.toArray());
	}
	
	/**
	 * Find all outgoing and incoming connections attached to the given list of shapes.
	 *
	 * @param shapes the shapes
	 * @return a list of connections
	 */
	public static List<Connection> findAllConnections(List<PictogramElement> shapes) {
        List<Connection> connections = new ArrayList<Connection>();
        for (PictogramElement pe : shapes) {
        	if (pe instanceof ContainerShape) {
        		ContainerShape shape = (ContainerShape)pe;
        		for (Anchor a : shape.getAnchors()) {
        			for (Connection c : a.getIncomingConnections()) {
        				if (	(shapes.contains(c.getStart().getParent()) ||
        						shapes.contains(c.getEnd().getParent())) &&
        						!shapes.contains(c) && !connections.contains(c)) {
        					connections.add(c);
        				}
        			}
        			for (Connection c : a.getOutgoingConnections()) {
        				if (	(shapes.contains(c.getStart().getParent()) ||
        						shapes.contains(c.getEnd().getParent())) &&
        						!shapes.contains(c) && !connections.contains(c)) {
        					connections.add(c);
        				}
        			}
        		}
        	}
        }
        return connections;
	}
}

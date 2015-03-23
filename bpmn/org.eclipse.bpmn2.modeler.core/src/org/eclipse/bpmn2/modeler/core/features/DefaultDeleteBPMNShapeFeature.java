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
import java.util.Iterator;
import java.util.List;

import org.eclipse.bpmn2.modeler.core.LifecycleEvent;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent.EventType;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

// TODO: Auto-generated Javadoc
/**
 * Default Graphiti {@code DeleteFeature} class for Shapes.
 * <p>
 */
public class DefaultDeleteBPMNShapeFeature extends DefaultDeleteFeature {

	/**
	 * Instantiates a new default delete bpmn shape feature.
	 *
	 * @param fp the fp
	 */
	public DefaultDeleteBPMNShapeFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultDeleteFeature#getUserDecision(org.eclipse.graphiti.features.context.IDeleteContext)
	 */
	@Override
	protected boolean getUserDecision(IDeleteContext context) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultDeleteFeature#canDelete(org.eclipse.graphiti.features.context.IDeleteContext)
	 */
	public boolean canDelete(IDeleteContext context) {
		// don't delete the Diagram!
		if (context.getPictogramElement() instanceof Diagram)
			return false;
		LifecycleEvent event = new LifecycleEvent(EventType.PICTOGRAMELEMENT_CAN_DELETE,
				getFeatureProvider(), context, context.getPictogramElement());
		TargetRuntime.getCurrentRuntime().notify(event);
		return event.doit;
	}
	public void delete(IDeleteContext context) {
		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof Connection) {
			// If the connection has other connections connected to it,
			// remove those as well.
			List<Connection> connections = new ArrayList<Connection>();
			for (Shape shape : AnchorUtil.getConnectionPoints((Connection)pe)) {
				for (Anchor a : shape.getAnchors()) {
					connections.addAll(a.getIncomingConnections());
					connections.addAll(a.getOutgoingConnections());
				}
			}
			for  (Connection c : connections) {
				DeleteContext dc = new DeleteContext(c);
				IDeleteFeature f = getFeatureProvider().getDeleteFeature(dc);
				f.delete(dc);
			}
		}
		super.delete(context);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultDeleteFeature#deleteBusinessObject(java.lang.Object)
	 */
	@Override
	protected void deleteBusinessObject(Object bo) {
		EStructuralFeature reference = ((EObject)bo).eClass().getEStructuralFeature("categoryValueRef"); //$NON-NLS-1$
		if (reference!=null) {
			Object v = ((EObject)bo).eGet(reference);
			if (v instanceof EList) {
				((EList)v).clear();
			}
		}

		List<PictogramElement> pictElements = Graphiti.getLinkService().getPictogramElements(getDiagram(), (EObject) bo);
		for (Iterator<PictogramElement> iterator = pictElements.iterator(); iterator.hasNext();) {
			PictogramElement pe = iterator.next();
			deletePeEnvironment(pe);
			Graphiti.getPeService().deletePictogramElement(pe);
		}

		TargetRuntime.getCurrentRuntime().notify(new LifecycleEvent(EventType.BUSINESSOBJECT_DELETED, bo));

		super.deleteBusinessObject(bo);
	}
	
	/**
	 * Delete pe environment.
	 *
	 * @param pictogramElement the pictogram element
	 */
	protected void deletePeEnvironment(PictogramElement pictogramElement){
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape containerShape = (ContainerShape) pictogramElement;
			EList<Anchor> anchors = containerShape.getAnchors();
			for (Anchor anchor : anchors) {
				deleteConnections(getFeatureProvider(), anchor.getIncomingConnections());
				deleteConnections(getFeatureProvider(), anchor.getOutgoingConnections());
			}
			deleteContainer(getFeatureProvider(), containerShape);
		}
	}
	
	/**
	 * Delete container.
	 *
	 * @param fp the fp
	 * @param containerShape the container shape
	 */
	protected void deleteContainer(IFeatureProvider fp, ContainerShape containerShape) {
		Object[] children = containerShape.getChildren().toArray();
		for (Object shape : children) {
			if (shape instanceof ContainerShape) {
				DeleteContext context = new DeleteContext((PictogramElement) shape);

				TargetRuntime rt = TargetRuntime.getCurrentRuntime();
				rt.notify(new LifecycleEvent(EventType.PICTOGRAMELEMENT_DELETED, fp, context, shape));

				fp.getDeleteFeature(context).delete(context);
			}
		}

		TargetRuntime rt = TargetRuntime.getCurrentRuntime();
		rt.notify(new LifecycleEvent(EventType.PICTOGRAMELEMENT_DELETED, fp, null, containerShape));
	}

	/**
	 * Delete connections.
	 *
	 * @param fp the fp
	 * @param connections the connections
	 */
	protected void deleteConnections(IFeatureProvider fp, EList<Connection> connections) {
		List<Connection> allConnections = new ArrayList<Connection>();
		allConnections.addAll(connections);
		for (Connection connection : allConnections) {
			IDeleteContext context = new DeleteContext(connection);

			TargetRuntime rt = TargetRuntime.getCurrentRuntime();
			rt.notify(new LifecycleEvent(EventType.PICTOGRAMELEMENT_DELETED, fp, context, connection));

			fp.getDeleteFeature(context).delete(context);
		}
	}
	
}

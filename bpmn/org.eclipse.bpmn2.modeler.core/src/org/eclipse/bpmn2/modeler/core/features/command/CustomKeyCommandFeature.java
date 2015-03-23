/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features.command;

import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.CopyContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.context.impl.PasteContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;

public class CustomKeyCommandFeature extends AbstractCustomFeature implements ICustomCommandFeature {

	public CustomKeyCommandFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public boolean isAvailable(String hint) {
		if (hint!=null) {
			if (hint.startsWith("move")) //$NON-NLS-1$
				return true;
			if (hint.startsWith("duplicate")) //$NON-NLS-1$
				return true;
		}
		return false;
	}
	
	@Override
	public boolean canExecute(ICustomContext context) {
		String command = (String) context.getProperty(GraphitiConstants.COMMAND_HINT);
		if (command!=null) {
			if (command.startsWith("move")) //$NON-NLS-1$
				return canExecuteMove(context);
			else if (command.startsWith("duplicate")) //$NON-NLS-1$
				return canExecuteDuplicate(context);
		}
		return false;
	}
	
	private boolean canExecuteMove(ICustomContext context) {
		PictogramElement pes[] = context.getPictogramElements();
		if (pes.length==0)
			return false;
		
		for (PictogramElement pe : pes) {
			if (!(pe instanceof ContainerShape))
				return false;
			ContainerShape cs = (ContainerShape) pe;
			MoveShapeContext moveContext = new MoveShapeContext(cs);
			moveContext.setSourceContainer(cs.getContainer());
			moveContext.setTargetContainer(cs.getContainer());
			IMoveShapeFeature f = getFeatureProvider().getMoveShapeFeature(moveContext);
			if (!f.canMoveShape(moveContext))
				return false;
		}
		
		return true;
	}

	private boolean canExecuteDuplicate(ICustomContext context) {
		PictogramElement pes[] = context.getPictogramElements();
		if (pes.length==0)
			return false;
		
		CopyContext copyContext = new CopyContext(pes);
		ICopyFeature copyFeature = getFeatureProvider().getCopyFeature(copyContext);
		if (copyFeature==null || !copyFeature.canCopy(copyContext))
			return false;
		
		DiagramBehavior db = (DiagramBehavior) getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior();
		org.eclipse.draw2d.geometry.Point p = db.getMouseLocation();
		p = db.calculateRealMouseLocation(p);
		Point point = GraphicsUtil.createPoint(p.x, p.y);
		
		PasteContext pasteContext = new PasteContext(pes, point.getX(), point.getY());
		pasteContext.putProperty(GraphitiConstants.COPY_FROM_CONTEXT, Boolean.TRUE);
		IPasteFeature pasteFeature = getFeatureProvider().getPasteFeature(pasteContext);
		if (pasteFeature==null || !pasteFeature.canPaste(pasteContext))
			return false;

		return true;
	}

	@Override
	public void execute(ICustomContext context) {
		String command = (String) context.getProperty(GraphitiConstants.COMMAND_HINT);
		if (command.startsWith("move")) //$NON-NLS-1$
			executeMove(context);
		else if (command.startsWith("duplicate")) { //$NON-NLS-1$
			executeDuplicate(context);
		}
	}
	
	private void executeMove(ICustomContext context) {
		int dx = 0;
		int dy = 0;
		int mult = 1;
		String command = (String) context.getProperty(GraphitiConstants.COMMAND_HINT);
		if (command.endsWith("10")) { //$NON-NLS-1$
			mult = 10;
			command = command.replace("10", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if ("moveup".equals(command))  //$NON-NLS-1$
			dy = -1;
		else if ("movedown".equals(command))  //$NON-NLS-1$
			dy = 1;
		else if ("moveleft".equals(command))  //$NON-NLS-1$
			dx = -1;
		else if ("moveright".equals(command))  //$NON-NLS-1$
			dx = 1;
		
		dx *= mult;
		dy *= mult;
		
		PictogramElement pes[] = context.getPictogramElements();
		for (PictogramElement pe : pes) {
			ContainerShape cs = (ContainerShape) pe;
			MoveShapeContext moveContext = new MoveShapeContext(cs);
			ILocation loc = Graphiti.getPeService().getLocationRelativeToDiagram(cs);
			EObject c = cs.eContainer();
			if (c instanceof ContainerShape) {
				ILocation lc = Graphiti.getPeService().getLocationRelativeToDiagram((ContainerShape)c);
				loc.setX(loc.getX() - lc.getX());
				loc.setY(loc.getY() - lc.getY());
			}
			moveContext.setDeltaX(dx);
			moveContext.setDeltaY(dy);
			moveContext.setX(loc.getX() + dx);
			moveContext.setY(loc.getY() + dy);
			moveContext.setSourceContainer(cs.getContainer());
			moveContext.setTargetContainer(cs.getContainer());
			IMoveShapeFeature f = getFeatureProvider().getMoveShapeFeature(moveContext);
			f.moveShape(moveContext);
		}
	}
	
	private void executeDuplicate(ICustomContext context) {
		CopyContext copyContext = new CopyContext(context.getPictogramElements());
		ICopyFeature copyFeature = getFeatureProvider().getCopyFeature(copyContext);
		copyFeature.copy(copyContext);
		
		DiagramBehavior db = (DiagramBehavior) getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior();
		org.eclipse.draw2d.geometry.Point p = db.getMouseLocation();
		p = db.calculateRealMouseLocation(p);
		Point point = GraphicsUtil.createPoint(p.x, p.y);
		
		PasteContext pasteContext = new PasteContext(context.getPictogramElements(), point.getX(), point.getY());
		IPasteFeature pasteFeature = getFeatureProvider().getPasteFeature(pasteContext);
		pasteFeature.paste(pasteContext);
	}
}

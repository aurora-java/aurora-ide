/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package aurora.ide.meta.gef.editors.components.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.figures.BoxFigure;
import aurora.ide.meta.gef.editors.layout.RowColSpanBackLayout;
import aurora.ide.meta.gef.editors.parts.ContainerPart;
import aurora.ide.meta.gef.editors.policies.ContainerLayoutEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeEditPolicy;
import aurora.ide.meta.gef.editors.policies.PasteComponentEditPolicy;
import aurora.plugin.source.gen.screen.model.BOX;
import aurora.plugin.source.gen.screen.model.CustomTree;
import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;

/**
 * @author shily Created on Feb 16, 2009
 */
public class CustomTreePart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
//		LabeledContainer f = new LabeledContainer();
//		f.setLabel("Tree");
//		f.setSize(TreeLayoutManager.TREE_DEFUAULT_SIZE);
//		// f.setLayoutManager(new TreeContainerLayoutManager());
//		return f;
		BoxFigure figure = new BoxFigure();
		BOX model = (BOX) getModel();
		figure.setBox(model);
		figure.setBorder(null);
		return figure;
	}

	@Override
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		return super.getTargetEditPart(request);
	}
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String prop = evt.getPropertyName();
		if (CustomTree.CHECKED_TREE.equals(prop))
			refreshChildren();
	}
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new ContainerLayoutEditPolicy());
		installEditPolicy("Paste Components", new PasteComponentEditPolicy());
		// installEditPolicy(EditPolicy.LAYOUT_ROLE, new XYLayoutEditPolicy() {
		//
		// @Override
		// protected Command createChangeConstraintCommand(EditPart child,
		// Object constraint) {
		// return null;
		// }
		//
		// @Override
		// protected Command getCreateCommand(CreateRequest request) {
		//
		// return null;
		// }
		//
		// protected EditPolicy createChildEditPolicy(EditPart child) {
		// // child selection
		// return new ResizableEditPolicy();
		// }
		//
		// });

	}

	// protected List getModelChildren() {
	// CustomTreeContainerNode root = this.getTree().getRoot();
	// List modelChildren;
	// if (root != null) {
	// modelChildren = new ArrayList();
	// modelChildren.add(root);
	// } else {
	// modelChildren = super.getModelChildren();
	// }
	// return modelChildren;
	// }

	private CustomTree getTree() {
		return (CustomTree) this.getModel();
	}

	public int getResizeDirection() {
		return NSEW;
	}

	// public boolean isLayoutHorizontal() {
	// BOX model = (BOX) getModel();
	// int col = model.getCol();
	// return col > 1;
	// }

	private static final Insets BOX_PADDING = new Insets(0, 0, 0, 0);

	public Rectangle layout() {
//		RowColBackLayout rowColBackLayout = new RowColBackLayout();
		RowColSpanBackLayout rowColBackLayout = new RowColSpanBackLayout();
		rowColBackLayout.setPadding(BOX_PADDING);
		return rowColBackLayout.layout(this);
	}

}

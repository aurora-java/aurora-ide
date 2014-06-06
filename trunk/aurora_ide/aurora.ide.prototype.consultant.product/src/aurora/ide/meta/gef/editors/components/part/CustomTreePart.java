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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LabeledContainer;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.components.figure.TreeLayoutManager;
import aurora.ide.meta.gef.editors.parts.ContainerPart;
import aurora.plugin.source.gen.screen.model.CustomTree;
import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;

/**
 * @author shily Created on Feb 16, 2009
 */
public class CustomTreePart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		LabeledContainer f = new LabeledContainer();
		f.setLabel("Tree");
		f.setSize(TreeLayoutManager.TREE_DEFUAULT_SIZE);
		f.setLayoutManager(new TreeLayoutManager());
		return f;
	}

	@Override
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		return super.getTargetEditPart(request);
	}

	@Override
	protected void createEditPolicies() {
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
		//			}
		//
		//		});

	}

	protected List getModelChildren() {
		CustomTreeContainerNode root = this.getTree().getRoot();
		List modelChildren;
		if (root != null) {
			modelChildren = new ArrayList();
			modelChildren.add(root);
		} else {
			modelChildren = super.getModelChildren();
		}
		return modelChildren;
	}

	private CustomTree getTree() {
		return (CustomTree) this.getModel();
	}
}

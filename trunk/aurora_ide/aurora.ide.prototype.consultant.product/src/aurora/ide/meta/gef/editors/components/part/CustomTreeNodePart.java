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

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.eidtpolicy.TreeNodeDeleteableEditPolicy;
import aurora.ide.meta.gef.editors.components.figure.TreeLayoutManager;
import aurora.ide.meta.gef.editors.components.figure.TreeNodeFigure;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.plugin.source.gen.screen.model.CustomTreeNode;

/**
 * @author shily Created on Feb 16, 2009
 */
public class CustomTreeNodePart extends ComponentPart {

	@Override
	protected IFigure createFigure() {
		TreeNodeFigure node = new TreeNodeFigure(PrototypeImagesUtils.getImage("palette/itembar_02.png"));
		node.setSize(TreeLayoutManager.NODE_DEFUAULT_SIZE);
		return node;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(TreeNodeDeleteableEditPolicy.DELETE_NODE_POLICY,
				new TreeNodeDeleteableEditPolicy());
	}

	protected void refreshVisuals() {
		super.refreshVisuals();
		((TreeNodeFigure) this.getFigure())
				.refreshVisuals((CustomTreeNode) getModel());
	}

	@Override
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		return super.getTargetEditPart(request);
	}
}

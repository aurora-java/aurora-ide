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
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.components.eidtpolicy.TextStyleSupport;
import aurora.ide.meta.gef.editors.components.figure.BackTreeLayout;
import aurora.ide.meta.gef.editors.components.figure.TreeNodeFigure;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.policies.NodeEditPolicy;
import aurora.plugin.source.gen.screen.model.CustomTreeNode;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

/**
 * @author shily Created on Feb 16, 2009
 */
public class CustomTreeNodePart extends ComponentPart {

	@Override
	protected IFigure createFigure() {
		TreeNodeFigure node = new TreeNodeFigure((CustomTreeNode) getModel());
//		node.setSize(TreeLayoutManager.NODE_DEFUAULT_SIZE);
		return node;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
	}

	protected void refreshVisuals() {
		((TreeNodeFigure) this.getFigure()).refreshVisuals();
	}

	@Override
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		return super.getTargetEditPart(request);
	}
	public Rectangle layout() {
		return new BackTreeLayout().layout(this);
	}
	public void performRequest(Request req) {
		new TextStyleSupport(this,ComponentProperties.prompt).performRequest(req);
	}
}

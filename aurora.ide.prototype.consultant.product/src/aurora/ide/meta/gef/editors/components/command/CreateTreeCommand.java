/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package aurora.ide.meta.gef.editors.components.command;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.CustomTree;
import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;

/**
 * @author shily Created on Feb 16, 2009
 */
public class CreateTreeCommand extends Command {
	private Container container;
	private CustomTree tree;
	private CreateRequest request;

	public CreateTreeCommand(String label, Container object,
			CreateRequest request) {
		super(label);
		this.container = object;
		this.request = request;
	}

	@Override
	public boolean canExecute() {
		return this.getContainer() != null;
	}

	@Override
	public boolean canUndo() {
		return tree != null;
	}

	@Override
	public void execute() {
		tree = (CustomTree) request.getNewObject();
		container.addChild(tree);
		CustomTreeContainerNode root = new CustomTreeContainerNode();
		root.setName("NO_NAME");
		// root.setId("TreeContainer.ROOT_ID");
		tree.addChild(root);
	}

	@Override
	public void redo() {
		super.redo();
	}

	@Override
	public void undo() {
		super.undo();
		container.removeChild(tree);
		// diagram.removeTree(tree);
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

}

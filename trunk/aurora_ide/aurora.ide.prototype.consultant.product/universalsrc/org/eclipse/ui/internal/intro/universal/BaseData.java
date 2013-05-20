/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.intro.universal;

import java.io.PrintWriter;

public abstract class BaseData {

	private GroupData parent;
	protected String id;

	protected void setParent(GroupData gd) {
		this.parent = gd;
	}

	public GroupData getParent() {
		return parent;
	}
	
	public abstract void write(PrintWriter writer, String indent);

	public String getId() {
		return id;
	}
}

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
package org.eclipse.bpmn2.modeler.ui.property;

import java.util.ArrayList;

import org.eclipse.bpmn2.modeler.core.runtime.PropertyTabDescriptor;

public class TabDescriptorList extends ArrayList<PropertyTabDescriptor> {
	private static final long serialVersionUID = -296768469891312674L;

	@Override
	public PropertyTabDescriptor[] toArray() {
		return this.toArray(new PropertyTabDescriptor[this.size()]);
	}
}

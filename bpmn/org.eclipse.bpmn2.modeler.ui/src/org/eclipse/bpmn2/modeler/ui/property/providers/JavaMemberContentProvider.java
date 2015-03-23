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
 * @author Bob Brodt
 ******************************************************************************/

package org.eclipse.bpmn2.modeler.ui.property.providers;

import java.util.List;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;

/**
 * @author Bob Brodt
 *
 */
public class JavaMemberContentProvider extends AbstractContentProvider {
	
	@Override
	public void collectElements(Object input, List list)  {
		if (input instanceof IMember && !(input instanceof IType)) {
			list.add(input);
			return;
		}			
	}

}

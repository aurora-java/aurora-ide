/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package aurora.ide.views.bm.view;

import org.eclipse.jface.viewers.Viewer;
//import org.eclipse.ui.dialogs.PatternFilter;

/**
 * A filter used in conjunction with <code>FilteredTree</code>. In order to
 * determine if a node should be filtered it uses the content and label provider
 * of the tree to do pattern matching on its children. This causes the entire
 * tree structure to be realized. Note that the label provider must implement
 * ILabelProvider.
 * 
 * @see org.eclipse.ui.dialogs.FilteredTree
 * @since 3.2
 */
public class PatternFilter extends org.eclipse.ui.dialogs.PatternFilter {

	@Override
	public void setPattern(String patternString) {
		// TODO Auto-generated method stub
		super.setPattern(patternString);
	}

	@Override
	public boolean isElementSelectable(Object element) {
		// TODO Auto-generated method stub
		return super.isElementSelectable(element);
	}

	@Override
	public boolean isElementVisible(Viewer viewer, Object element) {
		// TODO Auto-generated method stub
		return super.isElementVisible(viewer, element);
	}

	@Override
	public boolean isParentMatch(Viewer viewer, Object element) {
		// TODO Auto-generated method stub
		return super.isParentMatch(viewer, element);
	}

	@Override
	public boolean isLeafMatch(Viewer viewer, Object element) {
		// TODO Auto-generated method stub
		return super.isLeafMatch(viewer, element);
	}

	@Override
	public boolean wordMatches(String text) {
		// TODO Auto-generated method stub
		return super.wordMatches(text);
	}
	
}

/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package ext.org.eclipse.jdt.internal.ui.javaeditor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import patch.org.eclipse.jdt.ui.actions.SelectionDispatchAction;


import ext.org.eclipse.jdt.internal.ui.actions.SelectionConverter;


/**
 * Java element implementation hyperlink detector for methods.
 * 
 * @since 3.5
 */
public class JavaElementHyperlinkImplementationDetector extends JavaElementHyperlinkDetector {

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaElementHyperlinkDetector#createHyperlink(org.eclipse.jface.text.IRegion, org.eclipse.jdt.ui.actions.SelectionDispatchAction, org.eclipse.jdt.core.IJavaElement, boolean, org.eclipse.ui.texteditor.ITextEditor)
	 * @since 3.5
	 */
	@Override
	protected IHyperlink createHyperlink(IRegion wordRegion, SelectionDispatchAction openAction, IJavaElement element, boolean qualify, JavaEditor editor) {
		if (element.getElementType() == IJavaElement.METHOD && SelectionConverter.canOperateOn(editor)) {
			return new JavaElementImplementationHyperlink(wordRegion, openAction, (IMethod)element, qualify, editor);
		}
		return null;
	}
}

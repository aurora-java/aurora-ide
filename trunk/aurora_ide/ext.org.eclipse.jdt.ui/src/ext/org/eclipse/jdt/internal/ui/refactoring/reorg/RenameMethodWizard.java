/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package ext.org.eclipse.jdt.internal.ui.refactoring.reorg;

import org.eclipse.ltk.core.refactoring.Refactoring;

import ext.org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import ext.org.eclipse.jdt.internal.ui.JavaPluginImages;
import ext.org.eclipse.jdt.internal.ui.refactoring.RefactoringMessages;

public class RenameMethodWizard extends RenameRefactoringWizard {

	public RenameMethodWizard(Refactoring refactoring) {
		super(refactoring,
			RefactoringMessages.RenameMethodWizard_defaultPageTitle,
			RefactoringMessages.RenameMethodWizard_inputPage_description,
			JavaPluginImages.DESC_WIZBAN_REFACTOR_METHOD,
			IJavaHelpContextIds.RENAME_METHOD_WIZARD_PAGE);
	}
}

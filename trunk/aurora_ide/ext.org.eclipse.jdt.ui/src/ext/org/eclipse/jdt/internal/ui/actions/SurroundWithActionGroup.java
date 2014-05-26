/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package ext.org.eclipse.jdt.internal.ui.actions;

import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jdt.ui.actions.JdtActionConstants;
import patch.org.eclipse.jdt.ui.actions.SurroundWithTryCatchAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;

import org.eclipse.jface.text.ITextSelection;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionGroup;


import ext.org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;

public class SurroundWithActionGroup extends ActionGroup {

	private CompilationUnitEditor fEditor;
	private SurroundWithTryCatchAction fSurroundWithTryCatchAction;
	private final String fGroup;

	public SurroundWithActionGroup(CompilationUnitEditor editor, String group) {
		fEditor= editor;
		fGroup= group;
		fSurroundWithTryCatchAction= createSurroundWithTryCatchAction(fEditor);
	}

	@Override
	public void fillActionBars(IActionBars actionBar) {
		actionBar.setGlobalActionHandler(JdtActionConstants.SURROUND_WITH_TRY_CATCH, fSurroundWithTryCatchAction);
	}

	/**
	 * The Menu to show when right click on the editor
	 * {@inheritDoc}
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		ISelectionProvider selectionProvider= fEditor.getSelectionProvider();
		if (selectionProvider == null)
			return;

		ISelection selection= selectionProvider.getSelection();
		if (!(selection instanceof ITextSelection))
			return;

		ITextSelection textSelection= (ITextSelection)selection;
		if (textSelection.getLength() == 0)
			return;

		String menuText= ActionMessages.SurroundWithTemplateMenuAction_SurroundWithTemplateSubMenuName;

		MenuManager subMenu = new MenuManager(menuText, SurroundWithTemplateMenuAction.SURROUND_WITH_QUICK_MENU_ACTION_ID);
		subMenu.setActionDefinitionId(SurroundWithTemplateMenuAction.SURROUND_WITH_QUICK_MENU_ACTION_ID);
		menu.appendToGroup(fGroup, subMenu);
		subMenu.add(new Action() {});
		subMenu.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.removeAll();
				SurroundWithTemplateMenuAction.fillMenu(manager, fEditor, fSurroundWithTryCatchAction);
			}
		});
	}

	private static SurroundWithTryCatchAction createSurroundWithTryCatchAction(CompilationUnitEditor editor) {
		SurroundWithTryCatchAction result= new SurroundWithTryCatchAction(editor);
		result.setText(ActionMessages.SurroundWithTemplateMenuAction_SurroundWithTryCatchActionName);
		result.setActionDefinitionId(IJavaEditorActionDefinitionIds.SURROUND_WITH_TRY_CATCH);
		editor.setAction("SurroundWithTryCatch", result); //$NON-NLS-1$
		return result;
	}
}

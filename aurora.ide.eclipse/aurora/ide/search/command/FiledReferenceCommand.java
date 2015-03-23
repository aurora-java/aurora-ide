package aurora.ide.search.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import aurora.ide.search.action.FieldReferenceAction;

public class FiledReferenceCommand extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public FiledReferenceCommand() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FieldReferenceAction action = new FieldReferenceAction();
		IEditorPart activeEditor = getTextEditor(HandlerUtil
				.getActiveEditor(event));
		action.setActiveEditor(null, activeEditor);
		ISelection selection = getSelection(event);
		action.selectionChanged(null, selection);
		action.run(null);
		return null;
	}

	private ITextEditor getTextEditor(IEditorPart editor) {
		if (editor instanceof ITextEditor) {
			return (ITextEditor) editor;
		} else if (editor instanceof FormEditor) {
			FormEditor me = (FormEditor) editor;
			editor = me.getActiveEditor();
			if (editor instanceof ITextEditor) {
				//must be a TextPage
				return (ITextEditor) editor;
			}
		}
		return null;
	}

	private ISelection getSelection(ExecutionEvent event) {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return null;
		}
		ISelection selection = page.getSelection();
		if (selection == null) {
			IWorkbenchSite activeSite = HandlerUtil.getActiveSite(event);
			ISelectionProvider selectionProvider = activeSite
					.getSelectionProvider();
			if (selectionProvider != null)
				selection = selectionProvider.getSelection();
		}
		if (selection == null) {
			Control focus = page.getWorkbenchWindow().getShell().getDisplay()
					.getFocusControl();
			if (focus != null)
				return getTextSelection(focus);
		}
		return null;

	}

	private TextSelection getTextSelection(Control control) {
		if (control instanceof StyledText) {
			StyledText text = (StyledText) control;
			Point selectionRange = text.getSelectionRange();
			TextSelection textSel = new TextSelection(selectionRange.x,
					selectionRange.y);
			return textSel;
		}

		return null;
	}

}

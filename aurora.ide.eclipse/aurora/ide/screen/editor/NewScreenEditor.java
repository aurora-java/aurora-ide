package aurora.ide.screen.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.texteditor.IDocumentProvider;

import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.textpage.XMLTextFileDocumentProvider;

public class NewScreenEditor extends TextPage {

	public static final String EDITOR_ID = "aurora.ide.screen.editor.NewScreenEditor";

	public NewScreenEditor() {
		this(null, EDITOR_ID, "");
	}

	public NewScreenEditor(FormEditor editor, String id, String title) {
		super(editor, id, title);
		this.setDocumentProvider(new XMLTextFileDocumentProvider());
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId("#TextEditorContext");
		setRulerContextMenuId("#TextRulerContext");
	}

	@Override
	public void refresh(boolean dirty) {
		// do nothing
	}

	@Override
	protected void createActions() {
		super.createActions();
		// CFormatAction action = new CFormatAction();
		// action.setActiveEditor(null, this);
		// this.setAction("format", action);
	}

	@Override
	public IFile getFile() {
		return ResourceUtil.getFile(getEditorInput());
	}

	@Override
	public IEditorInput getInput() {
		return super.getInput();
	}

	public void doSave(IProgressMonitor monitor) {



		IDocumentProvider p= getDocumentProvider();
		if (p == null)
			return;

		if (p.isDeleted(getEditorInput())) {

			if (isSaveAsAllowed()) {

				/*
				 * 1GEUSSR: ITPUI:ALL - User should never loose changes made in the editors.
				 * Changed Behavior to make sure that if called inside a regular save (because
				 * of deletion of input element) there is a way to report back to the caller.
				 */
				performSaveAs(monitor);

			} else {

				Shell shell= getSite().getShell();
				String title= "Cannot Save";
				String msg= "The file has been deleted or is not accessible.";
				MessageDialog.openError(shell, title, msg);
			}

		} else {
			updateState(getEditorInput());
			validateState(getEditorInput());
			performSave(false, monitor);
		}
	
	}

}

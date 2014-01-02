package aurora.ide.prototype.consultant.product.editor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

import aurora.ide.editor.editorInput.PathEditorInput;
import aurora.ide.prototype.consultant.product.Activator;

public class EditorManager {
	public static IEditorPart getActiveEditor() {
		IEditorPart activeEditor = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		return activeEditor;
	}

	public static IEditorInput getEditorInput() {
		// PathEditorInput
		return getActiveEditor().getEditorInput();
	}

	public static IPath getActiveEditorFile() {
		IEditorInput editorInput = getActiveEditor().getEditorInput();
		if (editorInput instanceof PathEditorInput) {
			return ((PathEditorInput) editorInput).getPath();
		}
		return null;
	}

}

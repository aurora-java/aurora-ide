package aurora.ide.core.debug;

import org.eclipse.ui.IEditorPart;

public interface ITestViewerPart {
	void editorChanged(IEditorPart activeEditor);
}

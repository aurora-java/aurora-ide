package aurora.sql.java.ide.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;

import ext.org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;

import aurora.sql.java.ide.builder.ToggleNatureAction;

public class SQLJEDitor extends CompilationUnitEditor {

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		IFile file = (IFile) input.getAdapter(IFile.class);
		if (file != null)
			new ToggleNatureAction().addToBuildSpec((IProject) file
					.getProject());
	}
	// {
	// new ToggleNatureAction().addToBuildSpec((IProject) resource);
	// }
}

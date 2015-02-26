package aurora.sql.java.ide.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.INewWizard;

import ext.org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;

public class NewSQLJWizard extends NewClassCreationWizard implements INewWizard {
	public boolean performFinish() {
//		warnAboutTypeCommentDeprecation();
		boolean res= super.performFinish();
		if (res) {
			IResource resource= getfPage().getModifiedResource();
			try {
				IWorkspaceRoot root = resource.getWorkspace().getRoot();
				IPath changeExt = changeExt(resource);
				IFile file = root.getFile(changeExt);
				file.refreshLocal(0, null);
				if (file != null) {
					selectAndReveal(file);
//					if (fOpenEditorOnFinish) {
						openResource((IFile) file);
//					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
			
		}
		return res;
	}

	private IPath changeExt(IResource resource) throws CoreException {
		IPath fullPath = resource.getFullPath();
		IPath destination = fullPath.removeFileExtension().addFileExtension("sqlje");
		resource.move(destination, true, null);
		return destination;
		
	}
}

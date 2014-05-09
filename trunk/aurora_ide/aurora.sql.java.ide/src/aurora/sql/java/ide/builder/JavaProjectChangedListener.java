package aurora.sql.java.ide.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;

public class JavaProjectChangedListener implements IResourceChangeListener,
		IResourceDeltaVisitor {

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		return false;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {

		IResourceDelta delta = event.getDelta();
		if (delta == null)
			return;
		try {
			int kind = delta.getKind();
			if (IResourceDelta.ADDED == kind) {
				IResource resource = delta.getResource();
				int type = resource.getType();
				if (IResource.PROJECT == type
						&& ((IProject) resource).hasNature(JavaCore.NATURE_ID)) {
				
				new ToggleNatureAction().addToBuildSpec((IProject) resource);
				
				}
			}
		} catch (CoreException e) {
		}

	}

}

package aurora.ide.core.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.WorkspaceSourceContainer;

public class AuroraSourcePathComputerDelegate implements ISourcePathComputerDelegate {
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.sourcelookup.ISourcePathComputerDelegate#computeSourceContainers(org.eclipse.debug.core.ILaunchConfiguration, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
//		String path = configuration.getAttribute(DebugCorePlugin.ATTR_PDA_PROGRAM, (String)null);
		ISourceContainer sourceContainer = null;
//		if (path != null) {
//			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(path));
//			if (resource != null) {
//				//#ifdef ex4
////#				// TODO: Exercise 4 - seed the source lookup path
//				//#else
//				IContainer container = resource.getParent();
//				if (container.getType() == IResource.PROJECT) {
//					sourceContainer = new ProjectSourceContainer((IProject)container, false);
//				} else if (container.getType() == IResource.FOLDER) {
//					sourceContainer = new FolderSourceContainer(container, false);
//				}
//				//#endif
//			}
//		}
		if (sourceContainer == null) {
			sourceContainer = new WorkspaceSourceContainer();
		}
		return new ISourceContainer[]{sourceContainer};
	}
}
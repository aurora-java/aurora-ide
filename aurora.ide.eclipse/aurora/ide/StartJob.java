package aurora.ide;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.ProjectUtil;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AuroraFileFinder;

public class StartJob extends Job {

	public StartJob() {
		super("Aurora IDE startup");
		this.setSystem(true);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		List<IProject> allAuroraProjects = ProjectUtil.getALLAuroraProjects();
		AuroraFileFinder aff = new AuroraFileFinder();
		for (IProject p : allAuroraProjects) {
			try {
				p.accept(aff);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		List<IResource> result = aff.getResult();
		monitor.beginTask("Aurora IDE Init Workspace", result.size());
		int i = 0;
		for (IResource file : result) {
			if (i == 0) {
				i++;
				continue;
			}
			if (file instanceof IFile) {
				try {
					i--;
					CacheManager.getCompositeMap((IFile) file);
					monitor.worked(2);
				} catch (CoreException e) {
					// e.printStackTrace();
				} catch (ApplicationException e) {
					// e.printStackTrace();
				}
			}
		}
		return Status.OK_STATUS;
	}

}

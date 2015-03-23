package aurora.ide.project.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import aurora.ide.AuroraPlugin;
import aurora.ide.AuroraProjectNature;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public class ProjectWizard extends BasicNewProjectResourceWizard implements
		Runnable, IRunnableWithProgress {

	public static final String copyright = "(c) Copyright HAND Enterprise Solutions Company Ltd.";

	public void addPages() {
		super.addPages();
	}

	public boolean performFinish() {
		if (!super.performFinish())
			return false;
		IProject proj = getNewProject();
		if (proj == null)
			return true;
		try {
			proj.setDefaultCharset(AuroraConstant.ENCODING, null);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		IFolder web_inf = ResourceUtil.searchWebInf(proj);
		if (web_inf == null) {
			createBasicFolders(proj);
		} else {
			setPersistentProperty(proj, web_inf.getParent().getFullPath()
					.toString(), web_inf.getFolder("classes").getFullPath()
					.toString());
		}
		try {
			AuroraProjectNature.addAuroraNature(proj);
			AuroraProjectNature.autoSetProjectProperty(proj);
			if (web_inf != null) {
				startBuild();
			}
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		}
		return true;
	}

	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
	}

	private void createBasicFolders(IProject proj) {
		String[] segs = { "webRoot", "WEB-INF", "classes" };
		IPath path = proj.getFullPath();
		try {
			for (String s : segs) {
				path = path.append(s);
				IFolder f = proj.getParent().getFolder(path);
				f.create(false, true, null);
			}
			setPersistentProperty(proj, proj.getFullPath().append(segs[0])
					.toString(), path.toString());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void setPersistentProperty(IProject proj, String webHome,
			String bmHome) {
		try {
			proj.setPersistentProperty(ProjectPropertyPage.WebQN, webHome);
			proj.setPersistentProperty(ProjectPropertyPage.BMQN, bmHome);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void startBuild() {
		//TODO
		Display.getCurrent().asyncExec(this);
	}

	public void run() {
		try {
			AuroraPlugin.getDefault().getWorkbench().getProgressService()
					.busyCursorWhile(this);
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		try {
			getNewProject()
					.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		} catch (CoreException e) {
		}
	}
}

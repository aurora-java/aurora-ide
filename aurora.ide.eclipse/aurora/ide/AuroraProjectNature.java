package aurora.ide;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ProjectUtil;
import aurora.ide.perspectives.AuroraPerspective;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public class AuroraProjectNature implements IProjectNature {

	private IProject project;
	public static final String ID = AuroraPlugin.PLUGIN_ID + ".auroranature";

	public void configure() throws CoreException {
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();

		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(AuroraBuilder.BUILDER_ID)) {
				return;
			}
		}

		ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 0, commands.length);
		ICommand command = desc.newCommand();
		command.setBuilderName(AuroraBuilder.BUILDER_ID);
		newCommands[newCommands.length - 1] = command;
		desc.setBuildSpec(newCommands);
		project.setDescription(desc, null);
	}

	public void deconfigure() throws CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(AuroraBuilder.BUILDER_ID)) {
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i,
						commands.length - i - 1);
				description.setBuildSpec(newCommands);
				project.setDescription(description, null);
				return;
			}
		}
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public static void addAuroraNature(final IProject project)
			throws CoreException {
		if (project.hasNature(ID))
			return;
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] configEle = registry
				.getConfigurationElementsFor("org.eclipse.ui.newWizards");
		for (IConfigurationElement ce : configEle) {
			if (AuroraPerspective.PERSPECTIVE_ID.equals(ce
					.getAttribute("finalPerspective"))) {
				BasicNewProjectResourceWizard.updatePerspective(ce);
				break;
			}
		}

		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					IProjectDescription description = project.getDescription();
					String[] ids = description.getNatureIds();
					String[] newIds = new String[ids.length + 1];
					System.arraycopy(ids, 0, newIds, 0, ids.length);
					newIds[ids.length] = ID;
					description.setNatureIds(newIds);
					project.setDescription(description, null);

				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
		IProgressService service = PlatformUI.getWorkbench()
				.getProgressService();
		try {
			service.run(false, false, runnable);
		} catch (InterruptedException e) {
			// Ignore interrupted exceptions
		} catch (InvocationTargetException e) {
			return;
		}
//		autoSetProjectProperty(project);

	}

	/**
	 * 
	 * @param project
	 *            must has aurora nature
	 * @throws CoreException
	 */
	public static void autoSetProjectProperty(IProject project)
			throws CoreException {
		ArrayList<IFolder> folders = ResourceUtil.findAllWebInf(project);
		for (int i = 0; i < folders.size(); i++) {
			IResource res = folders.get(i).findMember("classes");
			if (!(res instanceof IFolder))
				folders.remove(i--);
		}
		IFolder wiFolder = null;
		if (folders.size() == 0) {
			DialogUtil.showErrorMessageBox(project.getName()
					+ " has no proper WEB-INF folder.");
			return;
		} else if (folders.size() > 1) {
			ProjectUtil.openProjectPropertyPage(project);
			return;
		} else {
			wiFolder = folders.get(0);
			project.setPersistentProperty(ProjectPropertyPage.LoclaUrlHomeQN,
					"http://127.0.0.1:8080/" + project.getName());
			project.setPersistentProperty(ProjectPropertyPage.WebQN, wiFolder
					.getParent().getFullPath().toString());
			project.setPersistentProperty(ProjectPropertyPage.BMQN, wiFolder
					.getFolder("classes").getFullPath().toString());
		}
	}

	public static void removeAuroraNature(IProject project)
			throws CoreException {
		if (!project.hasNature(ID))
			return;
		IProjectDescription description = project.getDescription();
		String[] ids = description.getNatureIds();
		for (int i = 0; i < ids.length; ++i) {
			if (ids[i].equals(ID)) {
				String[] newIds = new String[ids.length - 1];
				System.arraycopy(ids, 0, newIds, 0, i);
				System.arraycopy(ids, i + 1, newIds, i, ids.length - i - 1);
				description.setNatureIds(newIds);
				project.setDescription(description, null);
				return;
			}
		}
	}

	public static boolean hasAuroraNature(IProject project)
			throws CoreException {

		return project.hasNature(ID);
	}

}

package aurora.ide.views.wizard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.undo.CreateFileOperation;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.screen.editor.ServiceEditor;

public class NewScreenWizard extends Wizard implements INewWizard {
	private SelectTemplatelWizardPage selectTplWizardPage = new SelectTemplatelWizardPage();
	private SelectBmWizardPage selectBmWizardPage = new SelectBmWizardPage();
	private OldTemplateWizardPage oldTemplatesWizardPage = new OldTemplateWizardPage();
	private UserInput us = new UserInput();
	private IWorkbench workbench;
	static final Color WRONG_COLOR = new Color(null, 243, 180, 212);

	public NewScreenWizard() {
		setWindowTitle(Messages.NewScreenWizard_0);
		selectTplWizardPage.setUserInput(us);
		selectBmWizardPage.setUserInput(us);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		if (selection instanceof IStructuredSelection) {
			Object obj = selection.getFirstElement();
			if (obj instanceof IResource) {
				IResource resource = (IResource) obj;
				IProject proj = resource.getProject();
				if ((resource instanceof IProject)
						&& ResourceUtil.isAuroraProject(proj)) {
					String webHome = ResourceUtil.getWebHome(proj);
					if (webHome != null)
						us.setDir(webHome);
				} else if (resource instanceof IFolder) {
					us.setDir(resource.getFullPath().toString());
				} else if (resource instanceof IFile) {
					us.setDir(resource.getParent().getFullPath().toString());
				}
			}
		}
	}

	@Override
	public void addPages() {
		addPage(selectTplWizardPage);
		addPage(selectBmWizardPage);
		addPage(oldTemplatesWizardPage);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == selectTplWizardPage) {
			return us.noUseModel ? oldTemplatesWizardPage : selectBmWizardPage;
		}
		return null;
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		if (page != selectTplWizardPage)
			return selectTplWizardPage;
		return super.getPreviousPage(page);
	}

	@Override
	public boolean canFinish() {
		IWizardPage page = getContainer().getCurrentPage();
		if (page == selectTplWizardPage)
			return false;
		return page.isPageComplete();
	}

	@Override
	public boolean performFinish() {
		IPath path = new Path(us.dir);
		String fn = us.fileName;
		int idx = fn.indexOf('.');
		if (idx == -1)
			fn = fn + "." + AuroraConstant.ScreenFileExtension; //$NON-NLS-1$
		path = path.append(fn);
		IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if (!us.noUseModel) {
			createViaMetaTemplate(f);
		} else
			createViaEcpeTemplate(f);
		try {
			IDE.openEditor(
					workbench.getActiveWorkbenchWindow().getActivePage(), f,
					ServiceEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return true;
	}

	void createViaMetaTemplate(IFile f) {
		ScreenGenerator generator = new ScreenGenerator();
		generator.setProject(f.getProject());
		try {
			CompositeMap screenMap = generator.gen(us.template);
			String content = AuroraResourceUtil.xml_decl
					+ AuroraResourceUtil.getSign()
					+ AuroraResourceUtil.LineSeparator + screenMap.toXML();
			writeToFile(f, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void createViaEcpeTemplate(IFile f) {
		String content = oldTemplatesWizardPage.getTemplateContent();
		try {
			if (content == null || content.length() == 0) {
				QualifiedName screenQN = AuroraConstant.ScreenQN;
				CompositeMap rootElement = new CommentCompositeMap("a", //$NON-NLS-1$
						screenQN.getNameSpace(), screenQN.getLocalName());
				content = AuroraResourceUtil.xml_decl
						+ AuroraResourceUtil.getSign()
						+ AuroraResourceUtil.LineSeparator
						+ rootElement.toXML();
			}
			writeToFile(f, content);
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void writeToFile(IFile file, String content) throws CoreException,
			IOException, ExecutionException {
		InputStream is = new ByteArrayInputStream(
				content.getBytes(AuroraConstant.ENCODING));
		if (file.exists()) {
			file.setContents(is, IFile.FORCE, new NullProgressMonitor());
			is.close();
		} else {
			CreateFileOperation cfo = new CreateFileOperation(file, null, is,
					"Create " + file.getFullPath()); //$NON-NLS-1$
			cfo.execute(new NullProgressMonitor(), null);
		}
	}

	public class UserInput {
		public boolean noUseModel = false;
		public Template template = null;
		public String dir = ""; //$NON-NLS-1$
		public String fileName = ""; //$NON-NLS-1$

		public boolean isNoUseModel() {
			return noUseModel;
		}

		public void setNoUseModel(boolean noUseModel) {
			this.noUseModel = noUseModel;
		}

		public Template getTemplate() {
			return template;
		}

		public void setTemplate(Template template) {
			this.template = template;
		}

		public String getDir() {
			return dir;
		}

		public void setDir(String dir) {
			this.dir = dir;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	}
}

package aurora.ide.meta.gef.editors.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.gef.editors.VScreenEditor;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.models.io.ModelIOManager;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.search.ui.EditorOpener;

public class CreateMetaWizard extends Wizard implements INewWizard {
	private NewWizardPage newPage = new NewWizardPage();
	private SelectModelWizardPage selectPage = new SelectModelWizardPage();
	private SetLinkOrRefWizardPage settingPage = new SetLinkOrRefWizardPage();
	private AddModelWizardPage modelsPage = new AddModelWizardPage(this);

	private IWorkbench workbench;
	private ViewDiagram viewDiagram;

	private Template template;

	public void addPages() {
		addPage(newPage);
		addPage(selectPage);
		addPage(settingPage);
		addPage(modelsPage);
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		WizardDialog dialog = (WizardDialog) getContainer();
		dialog.addPageChangingListener(new IPageChangingListener() {
			public void handlePageChanging(PageChangingEvent event) {
				if (eq(event.getCurrentPage(), newPage)
						&& eq(event.getTargetPage(), selectPage)) {
					IProject metaProject = newPage.getMetaProject();
					if (metaProject != null
							&& (!eq(template, newPage.getTemplate()))) {
						template = newPage.getTemplate();
						selectPage.setBMPath(metaProject);
						selectPage.createDynamicTextComponents(template);
					}
				} else if (eq(event.getCurrentPage(), selectPage)
						&& eq(event.getTargetPage(), settingPage)) {
					if (selectPage.isModify()) {
						selectPage.setModify(false);
						viewDiagram = selectPage.getViewDiagram();
						settingPage.createCustom(viewDiagram);
					}
				}
			}
		});
	}

	public IProject getMetaProject() {
		return newPage.getMetaProject();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page.equals(newPage) && newPage.isNoTemplate()) {
			return modelsPage;
		}
		IWizardPage nextPage = super.getNextPage(page);
		if (modelsPage.equals(nextPage)) {
			return null;
		}
		return nextPage;
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		if (modelsPage.equals(page))
			return newPage;
		return super.getPreviousPage(page);
	}

	private boolean eq(Object o1, Object o2) {
		if (o1 == null) {
			return o1 == o2;
		}
		return o1.equals(o2);
	}

	@Override
	public boolean performFinish() {
		ViewDiagram vd = viewDiagram;
		if (newPage.isNoTemplate()) {
			vd = new ViewDiagram();
			List<String> models = this.modelsPage.getModels();
			for (String m : models) {
				vd.addUnBindModel(m);
			}
		} else if (vd == null) {
			vd = selectPage.getViewDiagram();
		}
		try {
			performFinish(vd);
		} catch (Exception e) {
			DialogUtil.logErrorException(e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void performFinish(ViewDiagram viewDiagram)
			throws InvocationTargetException, InterruptedException,
			PartInitException {
		EditorOpener editorOpener = new EditorOpener();
		IFile file = ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getFile(
						new Path(newPage.getPath() + "/"
								+ newPage.getFileName()));
		CommentCompositeMap rootMap = null;
		rootMap = (CommentCompositeMap) ModelIOManager.getNewInstance()
				.toCompositeMap(viewDiagram);
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ rootMap.toXML();
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			DialogUtil.logErrorException(e1);
			e1.printStackTrace();
		}
		final CreateFileOperation cfo = new CreateFileOperation(file, null, is,
				"create template.");
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				try {
					cfo.execute(monitor,
							WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
				} catch (ExecutionException e) {
					DialogUtil.logErrorException(e);
					e.printStackTrace();
				}
			}
		};
		getContainer().run(true, true, op);
		IEditorPart editor = editorOpener.open(workbench
				.getActiveWorkbenchWindow().getActivePage(), file, true);
		if (editor instanceof VScreenEditor) {
			((VScreenEditor) editor).markDirty();
		}
	}

	public boolean canFinish() {
		IWizardPage page = getContainer().getCurrentPage();
		if (page instanceof SelectModelWizardPage) {
			if (page.isPageComplete()) {
				return true;
			}
		} else if ((page instanceof SetLinkOrRefWizardPage)
				&& page.isPageComplete()) {
			return true;
		}
		if (modelsPage.equals(page)) {
			return true;
		}
		return false;
	}

	public boolean needsProgressMonitor() {
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
	}
}

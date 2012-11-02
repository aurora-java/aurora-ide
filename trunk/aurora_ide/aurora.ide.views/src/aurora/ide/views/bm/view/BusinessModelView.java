package aurora.ide.views.bm.view;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

import aurora.ide.editor.textpage.TextPage;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.project.AuroraProject;
import aurora.ide.screen.editor.ServiceEditor;
import aurora.ide.views.Activator;
import aurora.ide.views.IListener;
import aurora.ide.views.bm.BMTransferDropTargetListener;

public class BusinessModelView extends ViewPart {

	private IListener moduleChangedListener = new IListener() {

		@Override
		public void handleEvent(Object object) {
			modelsViewer.setInput((IFolder) object);
		}
	};
	private IPartListener partListener = new IPartListener() {


		@Override
		public void partActivated(IWorkbenchPart part) {
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
		}

		@Override
		public void partOpened(IWorkbenchPart part) {
			if (part instanceof ServiceEditor) {
				TextPage textPage = (TextPage) ((ServiceEditor) part)
						.getTextPage();
				StyledText textWidget = (StyledText) textPage
						.getAdapter(StyledText.class);
				BMTransferDropTargetListener listener = new BMTransferDropTargetListener(textPage);
				DropTarget realDropTarget = (DropTarget) textWidget
						.getData(DND.DROP_TARGET_KEY);
				if (realDropTarget == null)
					return;
				realDropTarget.addDropListener(listener);
				Transfer[] transfers = realDropTarget.getTransfer();
				Transfer[] allTransfers = new Transfer[transfers.length + 1];
				int curTransfer = 0;
				for (int i = 0; i < transfers.length; i++) {
					allTransfers[curTransfer++] = transfers[i];
				}
				allTransfers[curTransfer++] = listener.getTransfer();
				realDropTarget.setTransfer(allTransfers);
			}
		}
	};

	private ModulesComposite modulesComposite;
	private BMViewer modelsViewer;

	public BusinessModelView() {
		IWorkbench workbench = Activator.getDefault().getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IPartService partService = window.getPartService();
		partService.addPartListener(partListener);
		IWorkbenchPage activePage = window.getActivePage();
		if(activePage!=null){
			IEditorReference[] editorReferences = activePage.getEditorReferences();
			for (IEditorReference e : editorReferences) {
				IEditorPart editor = e.getEditor(false);
				partListener.partOpened(editor);
			}	
		}
	}

	@Override
	public void dispose() {
		IWorkbench workbench = Activator.getDefault().getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		window.getPartService().removePartListener(partListener);
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		// SashForm sf = new SashForm(parent, SWT.HORIZONTAL | SWT.BORDER);

		Composite p = new Composite(parent, SWT.NONE);
		p.setLayout(new GridLayout(2, false));
		ScrolledComposite sc1 = new ScrolledComposite(p, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		sc1.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		modulesComposite = new ModulesComposite(sc1, SWT.NONE);
		sc1.setContent(modulesComposite);

		// modulesComposite.setLayout(new GridLayout());
		modulesComposite.addListener(moduleChangedListener);

		// test.aurora.project
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("sel_app");
		modelsViewer = new BMViewer(p, project);
		this.setInput(project);
	}

	public void setInput(IProject p) {
		AuroraProject ap = new AuroraProject(p);
		try {
			IFolder web_classes = ap.getWeb_classes();
			setInput(web_classes);
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
			e.printStackTrace();
		}
		// modelsViewer
	}

	private void setInput(IFolder web_classes) {
		modulesComposite.setInput(web_classes);
		modelsViewer.setInput(web_classes);
	}

	@Override
	public void setFocus() {

	}

}

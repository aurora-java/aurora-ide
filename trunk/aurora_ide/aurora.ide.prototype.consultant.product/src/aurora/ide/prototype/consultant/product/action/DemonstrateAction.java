package aurora.ide.prototype.consultant.product.action;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.prototype.consultant.product.Activator;
import aurora.ide.prototype.consultant.product.demonstrate.DemonstratingLoginPageDialog;
import aurora.ide.prototype.consultant.view.NavigationView;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;

public class DemonstrateAction extends Action implements
		IWorkbenchWindowActionDelegate, ISelectionListener {

	private IWorkbenchWindow window;
	private File project;

	static private boolean isDemon;

	public DemonstrateAction(IWorkbenchWindow window) {
		super(Messages.DemonstrateAction_0, AS_CHECK_BOX);
		this.window = window;
		setText(Messages.DemonstrateAction_1);
		this.setId("aurora.ide.prototype.consultant.product.action.DemonstrateAction"); //$NON-NLS-1$
		setImageDescriptor(aurora.ide.prototype.consultant.product.Activator
				.getImageDescriptor("/icons/pictures.png")); //$NON-NLS-1$
		this.setToolTipText(Messages.DemonstrateAction_4);
		setChecked();
		window.getSelectionService().addSelectionListener(this);
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
		setChecked();
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void run() {
		updateStatus();
		DemonstratingLoginPageDialog dd = new DemonstratingLoginPageDialog(
				this.window.getShell(), project);
		dd.setDemonstrateAction(this);
		dd.open();
	}

	public void updateStatus() {
		boolean b = getIsDemon();
		// Activator.getDefault().getPreferenceStore()
		//				.setValue("IS_DEMONSTRATED", !b); //$NON-NLS-1$
		isDemon = !b;
		setChecked();
	}

	private void setChecked() {
		boolean isDemon = getIsDemon();
		this.setChecked(isDemon);
		MetaPlugin.isDemonstrate = isDemon;
	}

	static public boolean getIsDemon() {
		// return Activator.getDefault().getPreferenceStore()
		// .getBoolean("IS_DEMONSTRATED");
		return isDemon;
	}

	private File getProject() {
		return project;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof NavigationView) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			int size = ss.size();
			if (size == 1) {
				Object firstElement = ss.getFirstElement();
				File project = ResourceUtil.getProjectNode((Node) firstElement)
						.getFile();
				boolean isProject = ResourceUtil.isProject(project);
				if (isProject) {
					this.setEnabled(true);
					this.project = project;
					return;
				}
			}
		}
		project = null;
		this.setEnabled(false);
	}
}
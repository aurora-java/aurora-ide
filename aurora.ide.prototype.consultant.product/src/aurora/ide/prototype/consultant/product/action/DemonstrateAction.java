package aurora.ide.prototype.consultant.product.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.prototype.consultant.product.Activator;
import aurora.ide.prototype.consultant.product.demonstrate.DemonstratingLoginPageDialog;

public class DemonstrateAction extends Action implements
		IWorkbenchWindowActionDelegate {


	private IWorkbenchWindow window;

	public DemonstrateAction(IWorkbenchWindow window) {
		super(Messages.DemonstrateAction_0,AS_CHECK_BOX);
		this.window = window;
		setText(Messages.DemonstrateAction_1);
		this.setId("aurora.ide.prototype.consultant.product.action.DemonstrateAction"); //$NON-NLS-1$
		setImageDescriptor(aurora.ide.prototype.consultant.product.Activator
				.getImageDescriptor("/icons/pictures.png")); //$NON-NLS-1$
		this.setToolTipText(Messages.DemonstrateAction_4);
		setChecked();
		
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
		DemonstratingLoginPageDialog dd = new DemonstratingLoginPageDialog(this.window.getShell());
		dd.setDemonstrateAction(this);
		dd.open();
	}

	public  void updateStatus() {
		boolean b = getIsDemon();
		Activator.getDefault().getPreferenceStore()
				.setValue("IS_DEMONSTRATED", !b); //$NON-NLS-1$
		setChecked();
	}

	private void setChecked() {
		boolean isDemon = getIsDemon();
		this.setChecked(isDemon);
		MetaPlugin.isDemonstrate = isDemon;
	}

	static public boolean getIsDemon() {
		return Activator.getDefault().getPreferenceStore()
				.getBoolean("IS_DEMONSTRATED"); //$NON-NLS-1$
	}
}
package aurora.ide.prototype.consultant.view;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.prototype.consultant.view.wizard.CreateFunctionWizard;

public class FunctionSelectionDialog {
	public String openFolderSelectionDialog(String msg,
			org.eclipse.swt.widgets.Shell shell, Object input) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				shell, new CNFLabelProvider(), new CNFContentProvider());

		dialog.setMessage(msg);
		dialog.setInput(input);
		dialog.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof Node) {
					return true;
				}
				return false;
			}
		});
		dialog.setValidator(new ISelectionStatusValidator() {

			@Override
			public IStatus validate(Object[] selection) {

				if (selection.length > 0 && selection[0] != null
						&& selection[0] instanceof Node) {
					boolean function = ResourceUtil
							.isFunction(((Node) selection[0]).getFile());
					if (function)
						return Status.OK_STATUS;
				}
				Status s = new Status(Status.ERROR, "unknown", 1, //$NON-NLS-1$
						Messages.FunctionSelectionDialog_1, null);
				return s;
			}
		});
		int open = dialog.open();
		if (ElementTreeSelectionDialog.OK == open) {
			Object firstResult = dialog.getFirstResult();
			if (firstResult instanceof Node) {
				return new File(((Node) firstResult).getFile(),
						CreateFunctionWizard.QUICK_UI_FUNCTION).getPath();
			}
		}
		return null;
	}
	
	
	public String openUIPSelectionDialog(String msg,
			org.eclipse.swt.widgets.Shell shell, Object input) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				shell, new CNFLabelProvider(), new CNFContentProvider());

		dialog.setMessage(msg);
		dialog.setInput(input);
		dialog.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof Node) {
					return true;
				}
				return false;
			}
		});
		dialog.setValidator(new ISelectionStatusValidator() {

			@Override
			public IStatus validate(Object[] selection) {

				if (selection.length > 0 && selection[0] != null
						&& selection[0] instanceof Node) {
					boolean function = ResourceUtil.isUIP(((Node) selection[0]).getFile());
					if (function)
						return Status.OK_STATUS;
				}
				Status s = new Status(Status.ERROR, "unknown", 1, //$NON-NLS-1$
						Messages.FunctionSelectionDialog_3, null);
				return s;
			}
		});
		int open = dialog.open();
		if (ElementTreeSelectionDialog.OK == open) {
			Object firstResult = dialog.getFirstResult();
			if (firstResult instanceof Node) {
				return (((Node) firstResult).getFile()).getPath();
			}
		}
		return null;
	}

	// protected IAdaptable getInitialInput() {
	// Root root = new Root();
	// NavViewSetting nvs = new NavViewSetting();
	// String[] folders = nvs.getFolders();
	// for (String string : folders) {
	// root.addChild(new Node(new Path(string)));
	// }
	// return root;
	// }

}

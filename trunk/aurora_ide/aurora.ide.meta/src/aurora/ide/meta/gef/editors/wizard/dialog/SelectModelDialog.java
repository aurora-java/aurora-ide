package aurora.ide.meta.gef.editors.wizard.dialog;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class SelectModelDialog extends Dialog {

	private IResource resource;
	private Object result;
	private String message;

	public SelectModelDialog(Shell parentShell, IResource resource, String message) {
		super(parentShell);
		this.resource = resource;
		this.message = message;
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.HELP;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 500);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("文件选择");

		Label label = new Label(container, SWT.None);
		label.setText(message);

		TreeViewer tree = new TreeViewer(container);
		tree.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		tree.setContentProvider(new WorkbenchContentProvider());
		tree.setLabelProvider(new WorkbenchLabelProvider());
		tree.setInput(resource);
		tree.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				TreeSelection ts = (TreeSelection) event.getSelection();
				setResult(ts.getFirstElement());
				if (getResult() instanceof IFile) {
					getButton(OK).setEnabled(true);
				} else {
					getButton(OK).setEnabled(false);
				}
			}
		});
		return container;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		getButton(OK).setEnabled(false);
		return control;
	}
}

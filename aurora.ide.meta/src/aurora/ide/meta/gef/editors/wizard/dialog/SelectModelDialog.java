package aurora.ide.meta.gef.editors.wizard.dialog;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import aurora.ide.meta.gef.i18n.Messages;

public class SelectModelDialog extends Dialog {

	private IResource resource;
	private Object result;
	private TreeSelection ts;

	public SelectModelDialog(Shell parentShell, IResource resource) {
		super(parentShell);
		this.resource = resource;
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
		container.getShell().setText(Messages.SelectModelDialog_Select_File);

		final FilteredTree tree = new FilteredTree(container, SWT.BORDER, new PatternFilter(), true);

		tree.getViewer().setContentProvider(new WorkbenchContentProvider());
		tree.getViewer().setLabelProvider(new WorkbenchLabelProvider());
		tree.getViewer().setInput(resource);

		tree.getViewer().getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (getButton(OK).isEnabled()) {
					okPressed();
				} else if (result instanceof IFolder) {
					if (tree.getViewer().getExpandedState(ts.getPaths()[0])) {
						tree.getViewer().collapseToLevel(ts.getPaths()[0], 1);
					} else {
						tree.getViewer().expandToLevel(ts.getPaths()[0], 1);
					}
				}
			}
		});

		tree.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ts = (TreeSelection) event.getSelection();
				result = ts.getFirstElement();
				if ((result instanceof IFile) && ((IFile) result).getFileExtension().equalsIgnoreCase("bm")) {
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

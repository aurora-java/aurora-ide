package aurora.ide.meta.gef.editors.wizard.dialog;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.search.core.Util;

public class SelectModelDialog extends Dialog {

	private IResource resource;
	private Object result;
	private ModelFilter filter;

	public SelectModelDialog(Shell parentShell, IResource resource) {
		super(parentShell);
		this.resource = resource;
		filter = new ModelFilter("");
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

		Composite composite = new Composite(container, SWT.None);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lblFileName = new Label(composite, SWT.None);
		lblFileName.setText("文件名：");
		final Text txtFileName = new Text(composite, SWT.BORDER);
		txtFileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite treeViewer = new Composite(composite, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		treeViewer.setLayout(gl);
		treeViewer.setLayoutData(gd);
		treeViewer.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));

		final ToolBar toolBar = new ToolBar(treeViewer, SWT.FLAT|SWT.RIGHT_TO_LEFT);
		toolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.END));

		final TreeViewer tree = new TreeViewer(treeViewer, SWT.RIGHT_TO_LEFT);
		tree.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		tree.setContentProvider(new WorkbenchContentProvider());
		tree.setLabelProvider(new WorkbenchLabelProvider());
		tree.setInput(resource);
		tree.addFilter(filter);

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

		tree.getTree().addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY));
				e.gc.drawLine(0, 0, e.width, 0);
			}
		});
		
		txtFileName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				tree.removeFilter(filter);
				filter = new ModelFilter(txtFileName.getText());
				tree.addFilter(filter);
			}
		});

		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		Action expand = new Action("expand", MetaPlugin.imageDescriptorFromPlugin(MetaPlugin.PLUGIN_ID, "icons/expandall.gif")) {
			public void run() {
				tree.getControl().setRedraw(false);
				tree.expandAll();
				tree.getControl().setRedraw(true);
			}
		};

		Action collapse = new Action("collapse", MetaPlugin.imageDescriptorFromPlugin(MetaPlugin.PLUGIN_ID, "icons/collapseall.gif")) {
			public void run() {
				tree.getControl().setRedraw(false);
				tree.collapseAll();
				tree.getControl().setRedraw(true);
			}
		};
		toolBarManager.add(expand);
		toolBarManager.add(collapse);
		toolBarManager.update(true);
		toolBar.pack();

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

class ModelFilter extends ViewerFilter {
	private String fileName;

	public ModelFilter(String fileName) {
		this.fileName = fileName + "*";
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IFile) {
			IFile o = (IFile) element;
			if (fileName.length() == 0) {
				return true;
			} else {
				return Util.stringMatch(fileName, o.getName(), false, false);
			}
		}
		return true;
	}

}

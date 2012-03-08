package aurora.ide.meta.gef.editors.wizard.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.i18n.Messages;
import aurora.ide.search.core.Util;

public class SelectModelDialog extends Dialog {

	private IResource resource;
	private Object result;
	private String fileName = "";
	private IFile selectFile;

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

		Composite composite = new Composite(container, SWT.None);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lblFileName = new Label(composite, SWT.None);
		lblFileName.setText(Messages.SelectModelDialog_File_Name);
		final Text txtFileName = new Text(composite, SWT.BORDER);
		txtFileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label lbl = new Label(composite, SWT.NONE);
		lbl.setText("? = any character , * = any string");
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		lbl.setLayoutData(gd);

		Composite treeViewer = new Composite(composite, SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		treeViewer.setLayout(gl);
		treeViewer.setLayoutData(gd);
		treeViewer.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));

		ToolBar toolBar = new ToolBar(treeViewer, SWT.FLAT | SWT.RIGHT_TO_LEFT);
		toolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.END));
		toolBar.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY));
				e.gc.drawLine(0, e.height - 1, e.width, e.height - 1);
			}
		});

		final TreeViewer tree = new TreeViewer(treeViewer, SWT.None);
		tree.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		tree.setContentProvider(new ModelContentProvider());
		tree.setLabelProvider(new WorkbenchLabelProvider());
		tree.setInput(resource);

		tree.getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (selectFile != null) {
					okPressed();
				}
			}
		});

		tree.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				TreeSelection ts = (TreeSelection) event.getSelection();
				setResult(ts.getFirstElement());
				if ((getResult() instanceof IFile) && ((IFile) getResult()).getFileExtension().equalsIgnoreCase("bm")) {
					getButton(OK).setEnabled(true);
					selectFile = (IFile) getResult();
				} else {
					getButton(OK).setEnabled(false);
					selectFile = null;
				}
			}
		});

		txtFileName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fileName = txtFileName.getText() + "*";
				if (fileName.equals("*")) {
					tree.collapseAll();
					tree.setContentProvider(new ModelContentProvider());
				} else {
					tree.setContentProvider(new ModelContentProvider());
					tree.expandAll();
				}
			}
		});

		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		Action expand = new Action("expand", MetaPlugin.imageDescriptorFromPlugin(MetaPlugin.PLUGIN_ID, "icons/expandall.gif")) { //$NON-NLS-1$ //$NON-NLS-2$
			public void run() {
				tree.expandAll();
			}
		};

		Action collapse = new Action("collapse", MetaPlugin.imageDescriptorFromPlugin(MetaPlugin.PLUGIN_ID, "icons/collapseall.gif")) { //$NON-NLS-1$ //$NON-NLS-2$
			public void run() {
				tree.collapseAll();
			}
		};
		toolBarManager.add(collapse);
		toolBarManager.add(expand);
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

	class ModelContentProvider extends WorkbenchContentProvider {
		public Object[] getChildren(Object element) {
			IWorkbenchAdapter adapter = getAdapter(element);
			if (adapter != null) {
				Object[] os = adapter.getChildren(element);
				List<Object> result = new ArrayList<Object>();
				for (Object o : os) {
					try {
						if (fileName.length() == 0) {
							result.add(o);
						} else if (filter(o)) {
							result.add(o);
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				return result.toArray(new Object[result.size()]);
			}
			return new Object[0];
		}

		private boolean filter(Object obj) throws CoreException {
			boolean bool = true;
			if (obj instanceof IFolder) {
				IFolder folder = (IFolder) obj;
				for (IResource r : folder.members()) {
					if (r instanceof IFile) {
						IFile o = (IFile) r;
						if (Util.stringMatch(fileName, o.getName(), false, false)) {
							bool = true;
							break;
						} else {
							bool = false;
						}
					} else {
						return filter(r);
					}
				}
			} else if (obj instanceof IFile) {
				return Util.stringMatch(fileName, ((IFile) obj).getName(), false, false);
			}
			return bool;
		}
	}
}

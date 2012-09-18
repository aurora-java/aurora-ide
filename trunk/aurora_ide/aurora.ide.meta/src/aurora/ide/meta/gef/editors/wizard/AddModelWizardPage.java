package aurora.ide.meta.gef.editors.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.property.ResourceSelector;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.search.core.Util;

public class AddModelWizardPage extends WizardPage {

	private Composite composite;
	private TableViewer viewer;
	private Table table;
	private CreateMetaWizard wizard;

	private AddModelWizardPage() {
		super("aurora.wizard.model.Page"); //$NON-NLS-1$
		setTitle("选择BM文件");
		setDescription("选择将在次原型设计文件中需要用到的BM文件。");
		this.setPageComplete(true);
	}

	public AddModelWizardPage(CreateMetaWizard wizard) {
		this();
		this.wizard = wizard;
	}

	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		setControl(composite);
		createViewer();
	}

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent instanceof String[]) {
				return (String[]) parent;
			}
			return null;
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {

			switch (index) {
			case 0: {
				return obj.toString();
			}
			}

			return "";
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	private void createViewer() {

		Composite container = composite;
		viewer = new TableViewer(container, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.CHECK | SWT.BORDER);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());

		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		container.setLayout(gl);

		table = viewer.getTable();

		GridData gd = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(gd);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(300);
		tableColumn.setText("BM-Path");

		gd = new GridData(GridData.FILL_VERTICAL);
		Composite c = new Composite(container, SWT.NONE);
		gl = new GridLayout();
		c.setLayoutData(gd);
		c.setLayout(gl);
		gl.numColumns = 1;

		Button addFile = new Button(c, SWT.NONE);
		addFile.setText("增加");
		addFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addFile();
			}
		});

		Button delete = new Button(c, SWT.NONE);
		delete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = table.getItems();
				for (TableItem i : items) {
					boolean checked = i.getChecked();
					if (checked) {
						viewer.remove(i.getData());
					}
				}
			}
		});
		delete.setText("删除");
	}

	public IFile openResourceSelector(Shell shell, String[] exts,
			IContainer root) {
		ResourceSelector fss = new ResourceSelector(shell);
		fss.setExtFilter(exts);
		fss.setInput((IContainer) root);
		Object obj = fss.getSelection();
		if (!(obj instanceof IFile)) {
			return null;
		}
		return (IFile) obj;
	}

	protected void addFile() {
		String[] as = { "bm" };
		IContainer bmHome = getBMHome();
		if (bmHome == null || !bmHome.exists()) {
			DialogUtil.showWarningMessageBox("找不到BM主目录，需要配置关联工程。");
			return;
		}
		IFile file = openResourceSelector(getShell(), as, bmHome);
		if (file == null || !file.exists())
			return;
		String bmpkg = Util.toBMPKG(file);
		viewer.add(bmpkg);
	}

	public List<String> getModels() {
		List<String> models = new ArrayList<String>();
		TableItem[] items = table.getItems();
		for (TableItem o : items) {
			models.add(o.getData().toString());
		}
		return models;
	}

	private IContainer getBMHome() {
		IProject metaProject = wizard.getMetaProject();
		if (metaProject == null)
			return null;
		AuroraMetaProject metaPro = new AuroraMetaProject(metaProject);
		try {
			IProject auroraProj = metaPro.getAuroraProject();
			IFolder bmHomeFolder = ResourceUtil.getBMHomeFolder(auroraProj);
			return bmHomeFolder;
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}

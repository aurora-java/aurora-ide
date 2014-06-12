package aurora.ide.meta.gef.editors.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeColumn;

import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.property.MutilInputResourceSelector;
import aurora.ide.meta.gef.editors.template.handle.TemplateConfig;
import aurora.ide.meta.gef.editors.template.handle.TemplateHandle;
import aurora.ide.meta.gef.editors.template.handle.TemplateHelper;
import aurora.ide.meta.gef.editors.wizard.dialog.CridColumnDialog;
import aurora.ide.meta.gef.editors.wizard.dialog.StyleSettingDialog;
import aurora.ide.meta.gef.i18n.Messages;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.Renderer;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.TabItem;

public class SetLinkOrRefWizardPage extends WizardPage {

	private Composite composite;
	private ScreenBody viewDiagram;
	//private TemplateConfig config;

	public SetLinkOrRefWizardPage() {
		super("aurora.wizard.setting.Page"); //$NON-NLS-1$
		setTitle(Messages.SettingWizardPage_Title);
		setDescription("Setting"); //$NON-NLS-1$
		setPageComplete(true);
	}

	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		setControl(composite);
	}

	public void createCustom(ScreenBody v,TemplateConfig config) {
		this.viewDiagram = v;
		//config = TemplateHelper.getInstance().getConfig();
		for (Control c : composite.getChildren()) {
			if (!c.isDisposed()) {
				c.dispose();
			}
		}

		if (config.get(TemplateHelper.LINK).size() > 0) {
			Group gr = new Group(composite, SWT.None);
			gr.setLayout(new GridLayout(4, false));
			gr.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			gr.setText("Set tabref"); //$NON-NLS-1$
			for (Object ti : config.get(TemplateHelper.TAB_ITEM)) {
				createRefField((TabItem) ti, gr);
			}
		}

		if (config.get(TemplateHandle.GRID).size() > 0) {
			createGridSetting(config.get(TemplateHandle.GRID));
		}

		composite.layout();
	}

	private void createGridSetting(List<Object> grids) {

		final List<GridColumn> gridColumns = new ArrayList<GridColumn>();

		Group gl = new Group(composite, SWT.None);
		gl.setLayout(new GridLayout(2, false));
		gl.setLayoutData(new GridData(GridData.FILL_BOTH));
		gl.setText("Set grid"); //$NON-NLS-1$
		GridData gd = new GridData(GridData.FILL_BOTH);

		final TreeViewer treeViewer = new TreeViewer(gl, SWT.BORDER | SWT.FULL_SELECTION);
		gd.verticalSpan = 4;
		treeViewer.getTree().setLayoutData(gd);

		TreeColumn treeColumn = new TreeColumn(treeViewer.getTree(), SWT.NONE);
		treeColumn.setMoveable(true);
		treeColumn.setResizable(true);
		treeColumn.setText("Grid"); //$NON-NLS-1$
		treeColumn = new TreeColumn(treeViewer.getTree(), SWT.NONE);
		treeColumn.setMoveable(true);
		treeColumn.setResizable(true);
		treeColumn.setText("GridColumn"); //$NON-NLS-1$
		treeColumn = new TreeColumn(treeViewer.getTree(), SWT.NONE);
		treeColumn.setMoveable(true);
		treeColumn.setResizable(true);
		treeColumn.setText("Editor"); //$NON-NLS-1$
		treeColumn = new TreeColumn(treeViewer.getTree(), SWT.NONE);
		treeColumn.setMoveable(true);
		treeColumn.setResizable(true);
		treeColumn.setText("Renderer"); //$NON-NLS-1$

		treeViewer.getTree().setLinesVisible(true);
		treeViewer.getTree().setHeaderVisible(true);

		treeViewer.setLabelProvider(new TreeLabelProvider());
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.addTreeListener(new TreeViewerAutoFitListener());
		treeViewer.setInput(grids);
		for (TreeColumn t : treeViewer.getTree().getColumns()) {
			t.pack();
		}

		final Button btnAdd = new Button(gl, SWT.None);
		btnAdd.setText(Messages.SetLinkOrRefWizardPage_AddLink);
		btnAdd.setEnabled(false);
		gd = new GridData();
		gd.widthHint = 80;
		gd.verticalAlignment = SWT.TOP;
		btnAdd.setLayoutData(gd);

		final Button btnDel = new Button(gl, SWT.None);
		btnDel.setText(Messages.SetLinkOrRefWizardPage_DeleteLink);
		btnDel.setEnabled(false);
		gd = new GridData();
		gd.widthHint = 80;
		gd.verticalAlignment = SWT.TOP;
		btnDel.setLayoutData(gd);

		final Button btnUP = new Button(gl, SWT.None);
		btnUP.setText(Messages.SetLinkOrRefWizardPage_UP);
		btnUP.setEnabled(false);
		gd = new GridData();
		gd.widthHint = 80;
		gd.verticalAlignment = SWT.TOP;
		btnUP.setLayoutData(gd);

		final Button btnDown = new Button(gl, SWT.None);
		btnDown.setText(Messages.SetLinkOrRefWizardPage_Down);
		btnDown.setEnabled(false);
		gd = new GridData();
		gd.widthHint = 80;
		gd.verticalAlignment = SWT.TOP;
		btnDown.setLayoutData(gd);

		btnAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CridColumnDialog dialog = new CridColumnDialog(SetLinkOrRefWizardPage.this);
				if (dialog.open() == Dialog.OK) {
					Grid grid = (Grid) ((TreeSelection) treeViewer.getSelection()).getFirstElement();
					GridColumn gc = dialog.getGridColumn();
					gridColumns.add(gc);
					grid.addCol(gc);
					treeViewer.refresh(grid);
				}
			}
		});

		btnDel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				GridColumn gridColumn = (GridColumn) ((TreeSelection) treeViewer.getSelection()).getFirstElement();
				if (DialogUtil.showConfirmDialogBox(Messages.SetLinkOrRefWizardPage_IsDeleteGridColumn) == SWT.OK) {
					Grid grid = (Grid) gridColumn.getParent();
					grid.removeChild(gridColumn);
					grid.getChildren().remove(gridColumn);
					gridColumns.remove(gridColumn);
					treeViewer.refresh(grid);
				}
			}
		});

		btnUP.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				GridColumn gridColumn = (GridColumn) ((TreeSelection) treeViewer.getSelection()).getFirstElement();
				int index = getCridColumnIndex(gridColumn);
				modifyGridColumnIndex(gridColumn, index, -1);
				treeViewer.refresh(gridColumn.getParent());
				btnDown.setEnabled(true);
				if (index - 1 <= 0) {
					btnUP.setEnabled(false);
				}
			}
		});

		btnDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				GridColumn gridColumn = (GridColumn) ((TreeSelection) treeViewer.getSelection()).getFirstElement();
				int index = getCridColumnIndex(gridColumn);
				modifyGridColumnIndex(gridColumn, index, 1);
				treeViewer.refresh(gridColumn.getParent());
				btnUP.setEnabled(true);
				if (index + 2 >= ((Grid) gridColumn.getParent()).getChildren().size()) {
					btnDown.setEnabled(false);
				}
			}
		});

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object obj = ((TreeSelection) event.getSelection()).getFirstElement();
				if (obj instanceof Grid) {
					btnAdd.setEnabled(true);
					btnDel.setEnabled(false);
					btnDown.setEnabled(false);
					btnUP.setEnabled(false);
				} else if (obj instanceof GridColumn) {
					btnAdd.setEnabled(false);
					if (gridColumns.contains(obj)) {
						btnDel.setEnabled(true);
					} else {
						btnDel.setEnabled(false);
					}
					if (getCridColumnIndex((GridColumn) obj) + 1 < ((Grid) ((GridColumn) obj).getParent()).getChildren()
							.size()) {
						btnDown.setEnabled(true);
					} else {
						btnDown.setEnabled(false);
					}
					if (getCridColumnIndex((GridColumn) obj) - 1 >= 0) {
						btnUP.setEnabled(true);
					} else {
						btnUP.setEnabled(false);
					}
				} else {
					btnAdd.setEnabled(false);
					btnDel.setEnabled(false);
					btnDown.setEnabled(false);
					btnUP.setEnabled(false);
				}
			}
		});
	}

	private void createRefField(TabItem ti, Group gr) {
		Label lbl = new Label(gr, SWT.None);
		lbl.setText(ti.getName() + Messages.SetLinkOrRefWizardPage_Colon);
		Text txt = new Text(gr, SWT.BORDER);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button btnSelect = new Button(gr, SWT.None);
		btnSelect.setText(Messages.SetLinkOrRefWizardPage_SelectFile);

		final Button btnParam = new Button(gr, SWT.None);
		btnParam.setText(Messages.SetLinkOrRefWizardPage_AddPar);
		btnParam.setEnabled(false);

		btnSelect.addSelectionListener(new TabRefSelect(txt, btnParam, ti));
		btnParam.addSelectionListener(new TabRefParamSelect(ti));
		txt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Text t = (Text) e.getSource();
				if (null == t.getText() || "".equals(t.getText())) { //$NON-NLS-1$
					btnParam.setEnabled(false);
				}
			}
		});
	}

	public IProject getMetaProject() {
		for (IWizardPage page = this; page.getPreviousPage() != null; page = page.getPreviousPage()) {
			if (page instanceof NewWizardPage) {
				return ((NewWizardPage) page).getMetaProject();
			}
		}
		return null;
	}

	public IProject getAuroraProject() {
		try {
			return new AuroraMetaProject(getMetaProject()).getAuroraProject();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object fileSelect(IContainer[] containers, String[] extFilter) {
		MutilInputResourceSelector fss = new MutilInputResourceSelector(getShell());
		fss.setExtFilter(extFilter);
		fss.setInputs(containers);
		Object obj = fss.getSelection();
		return obj;
	}

	public ScreenBody getViewDiagram() {
		return viewDiagram;
	}

	public void setViewDiagram(ScreenBody viewDiagram) {
		this.viewDiagram = viewDiagram;
	}

	private int getCridColumnIndex(GridColumn gridColumn) {
		Grid grid = (Grid) gridColumn.getParent();
		return grid.getChildren().indexOf(gridColumn);
	}

	private void modifyGridColumnIndex(GridColumn gridColumn, int index, int offset) {
		Grid grid = (Grid) gridColumn.getParent();
		grid.getChildren().remove(gridColumn);
		grid.getChildren().add(index + offset, gridColumn);
		int idx = grid.getChildren().indexOf(gridColumn);
		grid.getChildren().remove(gridColumn);
		grid.getChildren().add(idx + offset, gridColumn);
	}

	class TabRefSelect extends SelectionAdapter {
		private Text txt;
		private TabItem ti;
		private Button btn;

		public TabRefSelect(Text txt, Button btn, TabItem ti) {
			this.txt = txt;
			this.ti = ti;
			this.btn = btn;
		}

		public void widgetSelected(SelectionEvent e) {
			String webHome = ResourceUtil.getWebHome(getAuroraProject());
			IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(webHome);
			IContainer uipFolder = getMetaProject().getFolder("ui_prototype"); //$NON-NLS-1$
			Object obj = fileSelect(new IContainer[] { (IContainer) res, uipFolder }, new String[] { "screen", "uip" }); //$NON-NLS-1$ //$NON-NLS-2$
			if (!(obj instanceof IFile)) {
				txt.setText(""); //$NON-NLS-1$
				btn.setEnabled(false);
			} else {
				String path = ((IFile) obj).getFullPath().toString();
				txt.setText(path);
				if (path.toLowerCase().endsWith("uip")) { //$NON-NLS-1$
					path = path.substring(path.indexOf("ui_prototype") + "ui_prototype".length()); //$NON-NLS-1$ //$NON-NLS-2$
				} else if (path.endsWith("screen")) { //$NON-NLS-1$
					path = path.substring(path.indexOf(webHome) + webHome.length());
				}
//				ti.getTabRef().setOpenPath(path);
				btn.setEnabled(true);
			}
		}
	}

	class TabRefParamSelect extends SelectionAdapter {
		private TabItem ti;

		public TabRefParamSelect(TabItem ti) {
			this.ti = ti;
		}

		public void widgetSelected(SelectionEvent e) {
			StyleSettingDialog dialog = new StyleSettingDialog(getShell(),
//					ti.getTabRef().getParameters()
					null);
			dialog.open();
		}
	}

	class TreeLabelProvider implements ITableLabelProvider {

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				if (element instanceof Grid) {
					String prompt = ((Grid) element).getPrompt();
					if ("".equals(prompt) || prompt == null) { //$NON-NLS-1$
						prompt = "Grid"; //$NON-NLS-1$
					}
					return prompt;
				}
				break;
			case 1:
				if (!(element instanceof Grid) && (element instanceof GridColumn)) {
					String prompt = ((GridColumn) element).getPrompt();
					if ("".equals(prompt) || prompt == null) { //$NON-NLS-1$
						prompt = "GridColumn"; //$NON-NLS-1$
					}
					return prompt;
				}
				break;
			case 2:
				if (element instanceof GridColumn) {
					return ((GridColumn) element).getEditor();
				}
				break;
			case 3:
				if (element instanceof GridColumn) {
					Renderer r = ((GridColumn) element).getRenderer();
					if (Renderer.NONE_RENDERER.equals(r.getRendererType())) {
						return null;
					}
					if (Renderer.INNER_FUNCTION.equals(r.getRendererType())) {
						return r.getFunctionName();
					}
					if (Renderer.PAGE_REDIRECT.equals(r.getRendererType())) {
						return r.getLabelText();
					}
				}
				break;
			default:
				return null;
			}
			return null;
		}

	}

	class TreeContentProvider implements ITreeContentProvider {
		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				return ((List<?>) inputElement).toArray();
			}
			return null;
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof Grid) {
				Grid grid = (Grid) parentElement;
				return grid.getChildren().toArray();
			}
			return null;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof Grid) {
				Grid grid = (Grid) element;
				return grid.getChildren().size() > 0;
			}
			return false;
		}
	}

	class TreeViewerAutoFitListener implements ITreeViewerListener {

		public void treeExpanded(TreeExpansionEvent event) {
			packColumns((TreeViewer) event.getSource());
		}

		public void treeCollapsed(TreeExpansionEvent event) {
			packColumns((TreeViewer) event.getSource());
		}

		private void packColumns(final TreeViewer treeViewer) {
			treeViewer.getControl().getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					TreeColumn[] treeColumns = treeViewer.getTree().getColumns();
					for (TreeColumn treeColumn : treeColumns) {
						if (treeColumn.getWidth() == 0) {
							continue;
						}
						treeColumn.pack();
					}
				}
			});
		}
	}
}

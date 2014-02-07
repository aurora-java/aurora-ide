package aurora.ide.prototype.consultant.product.fsd.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.PageModel;
import aurora.ide.swt.util.TableContentProvider;
import aurora.ide.swt.util.TableLabelProvider;
import aurora.ide.swt.util.TextField;
import aurora.ide.swt.util.WidgetFactory;

public class FSDContentControl extends FSDComposite {

	public static final String FSD_TABLE_INPUT = "fsd_table_input";
	public static final String ONLY_SAVE_LOGIC = "only_save_logic";
	public static final String FSD_DOCX_PATH = "fsd_docx_path";
	private IPath base;

	public FSDContentControl(PageModel model, IPath base) {
		super(model);
		this.base = base;
	}

	public void createFSDContentControl(final Composite root) {

		Composite parent = WidgetFactory.composite(root);
		parent.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite pathComposite = WidgetFactory.composite(parent);
		pathComposite.setLayout(GridLayoutUtil.COLUMN_LAYOUT_3);
		pathComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final TextField tf = WidgetFactory.createTextButtonField(pathComposite,
				Messages.ContentDescPage_0, Messages.ContentDescPage_1);
		tf.addModifyListener(new TextModifyListener(FSD_DOCX_PATH, tf.getText()));
		tf.setText(this.getModel().getStringPropertyValue(FSD_DOCX_PATH));
		tf.addButtonClickListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				clickBrowseButton(root.getShell(), tf);
			}
		});
		//
		final Button checked = new Button(parent, SWT.CHECK);
		checked.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		checked.setText(Messages.ContentDescPage_3);
		checked.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getModel().setPropertyValue(ONLY_SAVE_LOGIC,
						checked.getSelection());
			}
		});

		checked.setSelection(this.getModel().getBooleanPropertyValue(
				ONLY_SAVE_LOGIC));
		createContentTable(WidgetFactory.composite(parent));
	}

	protected void createContentTable(final Composite tableComposite) {

		tableComposite.setLayout(GridLayoutUtil.COLUMN_LAYOUT_2);
		tableComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final TableViewer tv = new TableViewer(tableComposite, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		Table table = tv.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		createTableColumn(table);

		tv.setContentProvider(getContentProvider());
		tv.setLabelProvider(getLabelProvider());

		Composite buttonComposite = WidgetFactory.composite(tableComposite);
		buttonComposite.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		Button add = WidgetFactory.button(buttonComposite,
				Messages.ContentDescPage_8);
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				clickAddButton(tableComposite.getShell(), tv);
			}
		});
		Button del = WidgetFactory.button(buttonComposite,
				Messages.ContentDescPage_10);
		del.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				clickDelButton(tv);
			}
		});
		Button up = WidgetFactory.button(buttonComposite,
				Messages.ContentDescPage_11);
		up.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				moveElement(tv, -1);
			}

		});
		Button down = WidgetFactory.button(buttonComposite,
				Messages.ContentDescPage_12);
		down.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				moveElement(tv, 1);
			}
		});
		tv.setInput(getTableInput());
	}

	protected TableContentProvider getContentProvider() {
		return new TableContentProvider();
	}

	protected TableLabelProvider getLabelProvider() {
		return new TableLabelProvider() {
			public String getColumnText(Object element, int i) {

				if (element instanceof String) {
					if (i == 0) {
						Path p = new Path(element.toString());
						return p.lastSegment();
					}
					if (i == 1) {
						return element.toString();
					}
				}
				return ""; //$NON-NLS-1$
			}
		};
	}

	protected void createTableColumn(Table table) {
		TableColumn column1 = new TableColumn(table, SWT.LEFT);
		column1.setWidth(128);
		column1.setText(Messages.ContentDescPage_5);

		TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setWidth(193);
		column2.setText(Messages.ContentDescPage_6);
	}

	public void moveElement(final TableViewer tv, int i) {
		ISelection s = tv.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			String r1 = (String) ss.getFirstElement();
			int idx = getTableInput().indexOf(r1);
			if (idx == -1)
				return;
			int idx2 = idx + i;
			if (idx2 < 0 || idx2 == getTableInput().size())
				return;
			String r2 = getTableInput().get(idx2);
			getTableInput().set(idx2, r1);
			getTableInput().set(idx, r2);
			tv.setInput(getTableInput());
		}
	}

	@SuppressWarnings("unchecked")
	protected List<String> getTableInput() {
		List<String> propertyValue = (List<String>) this.getModel()
				.getPropertyValue(FSD_TABLE_INPUT);
		return propertyValue;
	}

	protected String getDefaultFileName() {
		PageModel model = getModel();
		return model.getPropertyValue(FunctionDesc.fun_code) + "_" //$NON-NLS-1$
				+ model.getPropertyValue(FunctionDesc.fun_name);
	}

	protected void clickBrowseButton(Shell shell, final TextField tf) {
		FileDialog sd = new FileDialog(shell, SWT.SAVE);
		sd.setFileName(getDefaultFileName());
		sd.setFilterExtensions(new String[] { "*.docx" }); //$NON-NLS-1$
		sd.setOverwrite(true);
		String open = sd.open();
		if (open == null || open.length() < 1) {
			return;
		}
		tf.setText(open);
	}

	protected void clickAddButton(Shell shell, final TableViewer tv) {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText("Open File"); //$NON-NLS-1$
		dialog.setFilterExtensions(new String[] { "*.uip" }); //$NON-NLS-1$
		String path = dialog.open();
		if (path != null && path.length() > 0) {
			getTableInput().add(path);
		}
		tv.setInput(getTableInput());
	}

	protected void clickDelButton(final TableViewer tv) {
		ISelection s = tv.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			List<?> list = ss.toList();
			getTableInput().removeAll(list);
			tv.setInput(getTableInput());
		}
	}

	public void saveToMap(CompositeMap map) {
		map.put(ONLY_SAVE_LOGIC,
				this.getModel().getBooleanPropertyValue(ONLY_SAVE_LOGIC));
		String docPath = this.getModel().getStringPropertyValue(FSD_DOCX_PATH);
		map.createChild(FSD_DOCX_PATH).setText(makeRelative(docPath));
		@SuppressWarnings("unchecked")
		List<String> propertyValue = (List<String>) (this.getModel()
				.getPropertyValue(FSD_TABLE_INPUT));
		String s = "";
		for (String string : propertyValue) {
			s = s + "," + makeRelative(string);
		}
		s = s.replaceFirst(",", "");
		map.createChild(FSD_TABLE_INPUT).setText(s);
	}

	public String makeRelative(String path) {
		if (path == null || "".equals(path))
			return "";
		IPath p = new Path(path);
		IPath makeRelativeTo = p.makeRelativeTo(base);
		return makeRelativeTo.toString();
	}

	public String makeAbsolute(String path) {
		if (path == null || "".equals(path))
			return "";
		IPath p = new Path(path);
		return base.append(p).toString();
	}

	public void loadFromMap(CompositeMap map) {
		this.updateModel(ONLY_SAVE_LOGIC,
				Boolean.TRUE.equals(map.getBoolean(ONLY_SAVE_LOGIC, true)));
		String docPath = this.getMapCData(FSD_DOCX_PATH, map);
		this.updateModel(FSD_DOCX_PATH, makeAbsolute(docPath));
		String t = this.getMapCData(FSD_TABLE_INPUT, map);
		String[] split = t.split(",");
		List<String> ss = new ArrayList<String>();
		for (String s : split) {
			if (s != null && "".equals(s) == false) {
				ss.add(makeAbsolute(s));
			}
		}
		this.updateModel(FSD_TABLE_INPUT, ss);
	}

}

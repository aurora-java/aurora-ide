package aurora.ide.excel.bank.format.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
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

public class CTableViewer {

	private List<Object> input = new ArrayList<Object>();

	private List<Tl> columns = new ArrayList<Tl>();
	private TableContentProvider tableContentProvider;
	private TableLabelProvider tableLabelProvider;

	public TableViewer createContentTable(final Composite tableComposite) {

		final TableViewer tv = new TableViewer(tableComposite, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		tv.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				handleDoubleClick(event);
			}
		});
		Table table = tv.getTable();
		configure(tv);
		createTableColumn(table);
		Composite buttonComposite = WidgetFactory.composite(tableComposite);
		buttonComposite.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		createButton(tableComposite, tv, buttonComposite);
		setInput(tv);
		return tv;
	}


	protected void createButton(final Composite tableComposite,
			final TableViewer tv, Composite buttonComposite) {
		Button add = WidgetFactory.button(buttonComposite,
				Messages.CTableViewer_0);
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				clickAddButton(tableComposite.getShell(), tv);
			}
		});
		Button del = WidgetFactory.button(buttonComposite,
				Messages.CTableViewer_1);
		del.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				clickDelButton(tv);
			}
		});
		Button up = WidgetFactory.button(buttonComposite,
				Messages.CTableViewer_2);
		up.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				moveElement(tv, -1);
			}

		});
		Button down = WidgetFactory.button(buttonComposite,
				Messages.CTableViewer_3);
		down.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				moveElement(tv, 1);
			}
		});
	}


	protected void handleDoubleClick(DoubleClickEvent event) {

	}

	protected void setInput(final TableViewer tv) {
		tv.setInput(getTableInput());
	}

	protected void configure(TableViewer tv) {
		Table table = tv.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tv.setContentProvider(getContentProvider());
		tv.setLabelProvider(getLabelProvider());
	}

	protected TableContentProvider getContentProvider() {
		return tableContentProvider == null ? new TableContentProvider()
				: tableContentProvider;
	}

	protected TableLabelProvider getLabelProvider() {
		return tableLabelProvider == null ? new TableLabelProvider()
				: tableLabelProvider;
	}

	protected void createTableColumn(Table table) {
		for (Tl t : columns) {
			TableColumn column1 = new TableColumn(table, t.style);
			column1.setWidth(t.width);
			column1.setText(t.text);
		}
	}

	public void moveElement(final TableViewer tv, int i) {
		ISelection s = tv.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			Object r1 = ss.getFirstElement();
			int idx = getTableInput().indexOf(r1);
			if (idx == -1)
				return;
			int idx2 = idx + i;
			if (idx2 < 0 || idx2 == getTableInput().size())
				return;
			Object r2 = getTableInput().get(idx2);
			getTableInput().set(idx2, r1);
			getTableInput().set(idx, r2);
			setInput(tv);
		}
	}

	protected List<Object> getTableInput() {
		return getInput();
	}

	protected void clickAddButton(Shell shell, final TableViewer tv) {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText("Open File"); //$NON-NLS-1$
		dialog.setFilterExtensions(new String[] { "*.uip" }); //$NON-NLS-1$
		String path = dialog.open();
		if (path != null && path.length() > 0) {
			getTableInput().add(path);
		}
		setInput(tv);
	}

	protected void clickDelButton(final TableViewer tv) {
		ISelection s = tv.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			List<?> list = ss.toList();
			getTableInput().removeAll(list);
			setInput(tv);
		}
	}

	public List<Object> getInput() {
		return input;
	}

	public void setInput(List<Object> input) {
		this.input = input;
	}

	public void addColumn(String text, int width) {
		columns.add(new Tl(text, width));
	}

	public TableContentProvider getTableContentProvider() {
		return tableContentProvider;
	}

	public void setTableContentProvider(
			TableContentProvider tableContentProvider) {
		this.tableContentProvider = tableContentProvider;
	}

	public TableLabelProvider getTableLabelProvider() {
		return tableLabelProvider;
	}

	public void setTableLabelProvider(TableLabelProvider tableLabelProvider) {
		this.tableLabelProvider = tableLabelProvider;
	}

	public class Tl {
		String text;

		public Tl(String text, int width) {
			super();
			this.text = text;
			this.width = width;
		}

		int width;
		int style = SWT.NONE;
	}

}

package aurora.ide.dialog;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import aurora.ide.AuroraPlugin;

public class ParamQueryDialog extends Dialog {
	private final int MAX_PARAMETERS = 5;
	private IDialogSettings settings = AuroraPlugin.getDefault().getDialogSettingsSection("queryParam");
	private List<Parameter> repeatParameter = new ArrayList<Parameter>(10);
	private List<Parameter> unRepeatParameter = new ArrayList<Parameter>(5);
	private List<Parameter> parameter;
	private ComboBoxCellEditor cboCellEditor;
	private TableViewer tableViewer;
	private boolean repeat;

	public ParamQueryDialog(Shell parent, String sql) {
		super(parent);
		initDialog(sql);
	}

	private void initDialog(String sql) {
		String[] ps = sql.split("\\$");
		Set<String> unRepeat = new LinkedHashSet<String>();
		for (int i = 1; i < ps.length; i++) {
			Parameter p = new Parameter();
			p.setIndex(i);
			p.setName(ps[i].replaceAll("(.*\\{)|(\\}.*)", ""));
			unRepeat.add(p.getName());
			repeatParameter.add(p);
		}
		int index = 1;
		for (String s : unRepeat) {
			Parameter p = new Parameter();
			p.setIndex(index);
			p.setName(s);
			unRepeatParameter.add(p);
			index++;
		}
		parameter = unRepeatParameter;
		repeat = false;
	}

	public String[] getValues() {
		String[] values = new String[repeatParameter.size()];
		if (repeat) {
			for (int i = 0; i < repeatParameter.size(); i++) {
				if (repeatParameter.get(i).getName().indexOf(":") != -1) {
					values[i] = "~" + repeatParameter.get(i).getValue();
				} else {
					values[i] = repeatParameter.get(i).getValue();
				}
			}
		} else {
			for (int i = 0; i < repeatParameter.size(); i++) {
				Parameter p = repeatParameter.get(i);
				for (Parameter pp : unRepeatParameter) {
					if (pp.getName().equals(p.getName())) {
						if (pp.getName().indexOf(":") != -1) {
							values[i] = "~" + pp.getValue();
						} else {
							values[i] = pp.getValue();
						}
						break;
					}
				}
			}
		}
		return values;
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.HELP;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 300);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 0;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());

		layout.numColumns++;
		final Button button = new Button(composite, SWT.CHECK);
		button.setText("允许重复参数名");
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (button.getSelection()) {
					parameter = repeatParameter;
					repeat = true;
				} else {
					parameter = unRepeatParameter;
					repeat = false;
				}
				tableViewer.setInput(parameter);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		createButtonsForButtonBar(composite);
		return composite;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("变量");
		TableColumnLayout tclayout = new TableColumnLayout();
		container.setLayout(tclayout);
		tableViewer = new TableViewer(container, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setEnabled(true);

		TableColumn colIndex = new TableColumn(table, SWT.NONE);
		colIndex.setText("");
		tclayout.setColumnData(colIndex, new ColumnWeightData(25, 25, false));
		colIndex.setResizable(false);

		TableColumn colName = new TableColumn(table, SWT.NONE);
		colName.setText("参数名");
		colName.pack();
		tclayout.setColumnData(colName, new ColumnWeightData(100, 100, false));

		TableColumn colValue = new TableColumn(table, SWT.NONE);
		colValue.setText("参数值");
		tclayout.setColumnData(colValue, new ColumnWeightData(235, 100, true));
		tableViewer.setColumnProperties(new String[] { "", "Name", "Value" });

		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setInput(parameter);
		for (TableColumn tColumn : tableViewer.getTable().getColumns()) {
			if (!tColumn.getText().equals("参数值")) {
				tColumn.pack();
			}
		}

		cboCellEditor = new ComboBoxCellEditor(tableViewer.getTable(), new String[] {});

		CellEditor[] cellEditors = new CellEditor[3];
		cellEditors[0] = null;
		cellEditors[1] = null;
		cellEditors[2] = cboCellEditor;
		tableViewer.setCellEditors(cellEditors);
		tableViewer.setCellModifier(new CellModifier());

		return container;

	}

	private String[] getOldParameters(String name) {
		return settings.getArray(name) == null ? new String[] {} : settings.getArray(name);
	}

	private void setParameter(String name, String parameter) {
		String[] oldParameters = getOldParameters(name);
		if (oldParameters.length >= 1 && oldParameters[0].equals(parameter)) {
			return;
		}
		String[] temp;
		if (oldParameters.length >= MAX_PARAMETERS) {
			temp = new String[MAX_PARAMETERS];
		} else if (oldParameters.length < 1) {
			temp = new String[1];
		} else {
			temp = new String[(oldParameters.length + 1)];
		}
		temp[0] = parameter;
		for (int i = 1; i < temp.length && oldParameters.length >= 1; i++) {
			temp[i] = oldParameters[i - 1];
		}
		settings.put(name, temp);
	}

	class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Parameter) {
				Parameter p = (Parameter) element;
				if (columnIndex == 0) {
					return Integer.toString(p.getIndex());
				} else if (columnIndex == 1) {
					return p.getName();
				} else if (columnIndex == 2) {
					return p.getValue();
				}
			}
			return null;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}

	class ContentProvider implements IStructuredContentProvider {
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				return ((List<String>) inputElement).toArray();
			} else {
				return new Object[0];
			}
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	class CellModifier implements ICellModifier {
		public boolean canModify(Object element, String property) {
			return true;
		}

		public Object getValue(Object element, String property) {
			Parameter p = (Parameter) element;
			if (property.equals("Value")) {
				cboCellEditor.setItems(getOldParameters(p.getName()));
				return (Integer) (-1);
			}
			return null;
		}

		public void modify(Object element, String property, Object value) {
			TableItem item = (TableItem) element;
			Parameter p = (Parameter) item.getData();
			if (property.equals("Value")) {
				int index = ((Integer) value).intValue();
				String sValue = "";
				if (index != -1) {
					sValue = (cboCellEditor.getItems()[index]);
				} else {
					CCombo combo = (CCombo) cboCellEditor.getControl();
					sValue = combo.getText();
				}
				if (!sValue.trim().equals("")) {
					setParameter(p.getName(), sValue);
				}
				p.setValue(sValue);
			}
			tableViewer.update(p, null);
		}
	}
}

class Parameter {
	private int index;
	private String name;
	private String value;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Parameter) {
			Parameter p = (Parameter) o;
			return p.name.equals(name) && p.index == index;
		} else {
			return false;
		}
	}
}

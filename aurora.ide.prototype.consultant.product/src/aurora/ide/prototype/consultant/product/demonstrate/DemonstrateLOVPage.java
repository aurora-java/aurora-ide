package aurora.ide.prototype.consultant.product.demonstrate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
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
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import aurora.ide.meta.gef.util.ComponentUtil;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.DemonstrateBind;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.Input;

public class DemonstrateLOVPage extends WizardPage {
	private List<Data> input = new ArrayList<Data>();

	private TableViewer tableViewer;

	private String[] items;

	private static final String[] editors = {"",Input.TEXT,Input.NUMBER,Input.Combo,Input.LOV,Input.DATE_PICKER};
	
	private List<AuroraComponent> bindComponents = new ArrayList<AuroraComponent>();

	private DemonstrateSettingManager sm;

	@SuppressWarnings("unchecked")
	public DemonstrateLOVPage(String pageName, DemonstrateSettingManager sm) {
		super(pageName);
		this.setTitle("演示配置");
		this.setMessage("演示LOV页面设置");
		this.sm = sm;
		List<DemonstrateBind> inputs = (List<DemonstrateBind>) sm
				.getDemonstrateData().getPropertyValue(
						DemonstrateBind.BIND_COMPONENT);
		if (inputs != null) {
			for (DemonstrateBind db : inputs) {
				input.add(new Data(db));
			}
		}
		items = getBindItems();
	}

	public List<DemonstrateBind> getBindModels() {
		List<DemonstrateBind> r = new ArrayList<DemonstrateBind>();
		for (Data d : input) {
			r.add(d.db);
		}
		return r;
	}

	@Override
	public void createControl(Composite parentComposite) {
		Composite container = new Composite(parentComposite, SWT.NONE);
		container.setLayout(new GridLayout());

		// Composite cptTablt = new Composite(container, SWT.NONE);
		// cptTablt.setLayoutData(new GridData(GridData.FILL_BOTH));
		// TableColumnLayout tclayout = new TableColumnLayout();
		// cptTablt.setLayout(tclayout);

		tableViewer = new TableViewer(container, SWT.FULL_SELECTION
				| SWT.BORDER | SWT.V_SCROLL);
		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		// ScrollBar hScrollBar = table.getHorizontalBar();
		// hScrollBar.setEnabled(false);

		// TableColumn tc0 = new TableColumn(table, SWT.NONE);
		// tc0.setResizable(false);
		// tc0.setText("");
		// tclayout.setColumnData(tc0, new ColumnWeightData(0, 0, false));

		TableColumn tc1 = new TableColumn(table, SWT.LEFT);
		tc1.setText("列名");
		tc1.setWidth(250);
		// tclayout.setColumnData(tc1, new ColumnWeightData(100, 100, true));

		// ColumnWeightData cData = new ColumnWeightData(20, 20, true);
		TableColumn tc22 = new TableColumn(table, SWT.CENTER);
		tc22.setText("用做显示");
		tc22.setWidth(80);

		TableColumn tc2 = new TableColumn(table, SWT.CENTER);
		tc2.setText("用做查询");
		tc2.setWidth(80);
		// tclayout.setColumnData(tc2, cData);

		TableColumn tc3 = new TableColumn(table, SWT.CENTER);
		tc3.setText("关联控件");
		tc3.setWidth(80);
		// tclayout.setColumnData(tc3, cData);

		tableViewer
				.setColumnProperties(new String[] { "11", "222", "22", "33" });
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setInput(input);

		CellEditor[] cellEditors = new CellEditor[4];
		cellEditors[0] = new TextCellEditor(tableViewer.getTable(), SWT.WRAP
				| SWT.SINGLE);
		cellEditors[1] = new CheckboxCellEditor(tableViewer.getTable());
//		cellEditors[2] = new CheckboxCellEditor(tableViewer.getTable());
		cellEditors[2] = new ComboBoxCellEditor(tableViewer.getTable(), editors,
				SWT.READ_ONLY);
		cellEditors[3] = new ComboBoxCellEditor(tableViewer.getTable(), items,
				SWT.READ_ONLY);
		tableViewer.setCellEditors(cellEditors);
		tableViewer.setCellModifier(new MyCellModifier());

		Composite btnBar = new Composite(container, SWT.NONE);
		btnBar.setLayout(new GridLayout(2, false));

		Button btnAdd = new Button(btnBar, SWT.NONE);
		btnAdd.setText("+");
		GridData gd = new GridData();
		gd.widthHint = 30;
		btnAdd.setLayoutData(gd);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Data d = new Data(new DemonstrateBind());
				d.setAttr1("");
				input.add(d);
				tableViewer.refresh();
			}
		});

		Button btnSub = new Button(btnBar, SWT.NONE);
		btnSub.setText("-");
		btnSub.setLayoutData(gd);
		btnSub.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection select = (StructuredSelection) tableViewer
						.getSelection();
				Data d = (Data) select.getFirstElement();
				if (d == null) {
					return;
				}
				input.remove(d);
				tableViewer.refresh();
			}
		});

		setControl(container);
	}

	class TableLabelProvider extends ColumnLabelProvider implements
			ITableLabelProvider {
		// public void update(ViewerCell cell) {
		// cell.setText("1");
		// }

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Data d = (Data) element;
			// if (columnIndex == 0) {
			// return "";
			// }
			if (columnIndex == 0) {
				return d.getAttr1();
			}
			if (columnIndex == 1) {
				return Boolean.toString(d.isAttr22());
			}
			if (columnIndex == 2) {
				return d.getAttr2();
			}
			if (columnIndex == 3) {
				AuroraComponent attr3 = d.getAttr3();
				return attr3 == null ? "" : attr3.getPrompt();
			}
			return null;
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			Data d = (Data) element;
			// if (columnIndex == 2) {
			// if (d.isAttr2()) {
			// return Activator.check;
			// } else {
			// return Activator.unCheck;
			// }
			// } else if (columnIndex == 3) {
			// if (d.isAttr3()) {
			// return Activator.check;
			// } else {
			// return Activator.unCheck;
			// }
			// }
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

	class MyCellModifier implements ICellModifier {

		@Override
		public boolean canModify(Object element, String property) {
			return true;
		}

		@Override
		public Object getValue(Object element, String property) {
			Data d = (Data) element;
			if (property.equals("11")) {
				return d.getAttr1();
			} else if (property.equals("222")) {
				return Boolean.valueOf(d.isAttr22());
			} else if (property.equals("22")) {
				return d.getEditorIndex();
			} else if (property.equals("33")) {
				return d.getBindIndex();
			}
			return -1;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			if (element instanceof Item) {
				element = ((Item) element).getData();
			}
			Data d = (Data) element;
			if (property.equals("11")) {
				d.setAttr1(value.toString());
			} else if (property.equals("222")) {
				d.setAttr22(Boolean.parseBoolean(value.toString()));
			} else if (property.equals("22")) {
				d.setEditorIndex((Integer) value);
			} else if (property.equals("33")) {
				d.setBindIndex((Integer) value);
			}
			tableViewer.refresh();
		}

	}

	class Data {
		private DemonstrateBind db;

		public Data(DemonstrateBind db) {
			this.db = db;
		}

		public String getAttr1() {
			return db.getColumnPrompt();
		}

		public void setAttr22(boolean attr22) {
			this.db.setForDisplay(attr22);
		}

		public boolean isAttr22() {
			return db.isForDisplay();
		}

		public void setAttr1(String attr1) {
			this.db.setColumnPrompt(attr1);
		}

		public String getAttr2() {
			return db.getForQueryEditor();
		}

		public void setAttr2(String attr2) {
			db.setForQueryEditor(attr2);
		}

		public AuroraComponent getAttr3() {
			AuroraComponent bindModel = db.getBindModel();
			for (AuroraComponent bc : bindComponents) {
				if (bc.equals(bindModel)) {
					return bindModel;
				}
			}
			return null;
		}

		public void setAttr3(AuroraComponent attr3) {
			db.setBindModel(attr3);
		}
		
		public int getEditorIndex() {
			for (int i = 0; i < editors.length; i++) {
				if (editors[i].equals(getAttr2())) {
					return i ;
				}
			}
			return 0;
		}

		public void setEditorIndex(int i) {
				setAttr2(editors[i]);
		}

		public int getBindIndex() {
			for (int i = 0; i < bindComponents.size(); i++) {
				if (bindComponents.get(i).equals(getAttr3())) {
					return i + 1;
				}
			}
			return 0;
		}

		public void setBindIndex(int i) {
			String[] bindItems = getBindItems();
			if (i > 0 && bindItems.length - i > 0) {
				setAttr3(bindComponents.get(i - 1));
			} else {
				setAttr3(null);
			}
		}
	}

	protected String[] getBindItems() {
		if (items == null) {
			AuroraComponent ac = sm.getAuroraComponent();
			if (GridColumn.GRIDCOLUMN.equals(ac.getComponentType())) {
				Grid grid = ComponentUtil.findParentGrid(ac);
				List<GridColumn> gridColumns = ComponentUtil
						.getGridColumns(grid);
				for (GridColumn gridColumn : gridColumns) {
					bindComponents.add(gridColumn);
				}
			} else {
				Container notHVBoxParent = ComponentUtil.getNotHVBoxParent(ac);
				List<AuroraComponent> allInputChildren = ComponentUtil
						.getAllInputChildren(notHVBoxParent);
				for (AuroraComponent auroraComponent : allInputChildren) {
					bindComponents.add(auroraComponent);
				}
			}
			items = new String[bindComponents.size() + 1];
			items[0] = "";
			for (int i = 1; i < items.length; i++) {
				items[i] = bindComponents.get(i - 1).getPrompt();
			}
		}
		return items;
	}
}

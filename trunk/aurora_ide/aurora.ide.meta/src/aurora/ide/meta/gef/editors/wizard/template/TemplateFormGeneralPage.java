package aurora.ide.meta.gef.editors.wizard.template;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.internal.ide.dialogs.OpenResourceDialog;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.presentation.component.std.TextField;

@SuppressWarnings("restriction")
public class TemplateFormGeneralPage extends WizardPage {

	private List<CompositeMap> queryFieldsMap = new ArrayList<CompositeMap>();
	private List<Param> queryFields = new ArrayList<Param>();

	private TableViewer tableViewer;
	private ComboBoxCellEditor cboCellEditor;

	public TemplateFormGeneralPage() {
		super("TemplateFormGeneralPage");
		setPageComplete(true);
		setTitle("新建");
		setDescription("选择BM文件");
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.None);
		setControl(composite);
		composite.setLayout(new GridLayout(2, false));

		GridData gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		Button btnBrower = new Button(composite, SWT.NONE);
		btnBrower.setText("选择BM文件");
		btnBrower.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		tableViewer = new TableViewer(composite, SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.getTable().setLayoutData(gd);

		Table table = tableViewer.getTable();
		TableLayout layout = new TableLayout();
		table.setLayout(layout);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setEnabled(true);

		layout.addColumnData(new ColumnWeightData(0));
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText("prompt");
		layout.addColumnData(new ColumnWeightData(0));
		col = new TableColumn(table, SWT.NONE);
		col.setText("type");

		tableViewer.setColumnProperties(new String[] { "prompt", "type" });
		tableViewer.setContentProvider(new IStructuredContentProvider() {
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
		});
		tableViewer.setLabelProvider(new TableLabelProvider());

		cboCellEditor = new ComboBoxCellEditor(tableViewer.getTable(), new String[] { Input.TEXT, Input.NUMBER, Input.Combo, Input.LOV, Input.CAL, Input.DATETIMEPICKER }, SWT.READ_ONLY);

		CellEditor[] cellEditors = new CellEditor[2];
		cellEditors[0] = new TextCellEditor(tableViewer.getTable());
		cellEditors[1] = cboCellEditor;
		tableViewer.setCellEditors(cellEditors);
		tableViewer.setCellModifier(new CellModifier());

		btnBrower.addSelectionListener(new buttonClick());
	}

	public List<Param> getQueryFields() {
		List<Param> list = new ArrayList<Param>();
		for (TableItem ti : tableViewer.getTable().getItems()) {
			if (ti.getChecked()) {
				list.add((Param) ti.getData());
			}
		}
		return list;
	}

	class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			Param ele = (Param) element;
			switch (columnIndex) {
			case 0:
				return ele.prompt;
			case 1:
				return ele.type;
			}
			return "";
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

	}

	class CellModifier implements ICellModifier {
		public boolean canModify(Object element, String property) {
			return true;
		}

		public Object getValue(Object element, String property) {
			if (property.equals("type")) {
				String t = ((Param) element).type;
				int i = 0;
				if (t.equals(Input.TEXT)) {
					i = 0;
				} else if (t.equals(Input.NUMBER)) {
					i = 1;
				} else if (t.equals(Input.Combo)) {
					i = 2;
				} else if (t.equals(Input.LOV)) {
					i = 3;
				} else if (t.equals(Input.CAL)) {
					i = 4;
				} else if (t.equals(Input.DATETIMEPICKER)) {
					i = 5;
				}
				return i;
			}
			return ((Param) element).prompt;
		}

		public void modify(Object element, String property, Object value) {
			Param p = (Param) ((TableItem) element).getData();
			if (property.equals("type")) {
				int index = ((Integer) value).intValue();
				p.setType(cboCellEditor.getItems()[index]);
			} else {
				p.setPrompt(value.toString());
			}
			tableViewer.update(p, null);
		}
	}

	class buttonClick implements SelectionListener {
		public void widgetSelected(SelectionEvent e) {
			OpenResourceDialog dialog = new OpenResourceDialog(getShell(), AuroraPlugin.getActiveIFile().getProject(), OpenResourceDialog.CARET_BEGINNING);
			dialog.setInitialPattern("*.bm");
			if (dialog.open() == OpenResourceDialog.OK) {
				queryFieldsMap.clear();
				for (Object obj : dialog.getResult()) {
					List<CompositeMap> qfs = GefModelAssist.getQueryFields(GefModelAssist.getModel(obj));
					if (qfs != null) {
						queryFieldsMap.addAll(qfs);
					}
				}
				initTableModel(queryFieldsMap);
				tableViewer.setInput(queryFields);
				for(TableItem ti:tableViewer.getTable().getItems()){
					ti.setChecked(true);
				}
			}
		}

		private List<Param> initTableModel(List<CompositeMap> getQueryFields) {
			queryFields.clear();
			for (CompositeMap qfs : getQueryFields) {
				Param p = new Param();
				String name = (String) qfs.get("prompt");
				name = name == null ? qfs.getString("field") : name;
				name = name == null ? qfs.getString("name") : name;
				name = name == null ? "prompt" : name;
				p.setPrompt(name);
				p.setType(GefModelAssist.getType(qfs));
				queryFields.add(p);
			}
			return queryFields;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			TextField t=new TextField();
			Form f=new Form();
		}
	}

	class Param {
		private String prompt;
		private String type;

		public String getPrompt() {
			return prompt;
		}

		public void setPrompt(String prompt) {
			this.prompt = prompt;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}
}

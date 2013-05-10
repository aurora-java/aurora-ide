package aurora.ide.meta.gef.designer.wizard;

import java.util.ArrayList;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import aurora.ide.meta.gef.designer.wizard.CreateTableWizard.ObjectDescriptor;
import aurora.ide.meta.gef.editors.PrototypeImagesUtils;

public class PreCreateTablePage extends WizardPage {
	private Table table;
	private ArrayList<ObjectDescriptor> model;
	private TableViewer tableViewer;

	/**
	 * Create the wizard.
	 */
	public PreCreateTablePage() {
		super(Messages.PreCreateTablePage_0);
		setTitle(Messages.PreCreateTablePage_1);
		setDescription(Messages.PreCreateTablePage_2);
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		tableViewer = new TableViewer(container, SWT.BORDER
				| SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 6);
		gd_table.heightHint = 87;
		table.setLayoutData(gd_table);

		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn tableColumn_1 = tableViewerColumn_4.getColumn();
		tableColumn_1.setResizable(false);
		tableColumn_1.setMoveable(true);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn = tableViewerColumn.getColumn();
		tblclmnNewColumn.setAlignment(SWT.CENTER);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText(Messages.PreCreateTablePage_3);

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_1 = tableViewerColumn_1.getColumn();
		tblclmnNewColumn_1.setAlignment(SWT.CENTER);
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText(Messages.PreCreateTablePage_4);

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_2 = tableViewerColumn_2.getColumn();
		tblclmnNewColumn_2.setAlignment(SWT.CENTER);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText(Messages.PreCreateTablePage_5);

		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn tableColumn = tableViewerColumn_3.getColumn();
		tableColumn.setAlignment(SWT.CENTER);
		tableColumn.setWidth(100);
		tableColumn.setText(Messages.PreCreateTablePage_6);
		tableViewer.setContentProvider(new ODContentProvider());
		tableViewer.setLabelProvider(new ODLabelProvider());
		Table table = tableViewer.getTable();
		tableViewer.setColumnProperties(new String[] { "", "name", "type", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"exists", "create" }); //$NON-NLS-1$ //$NON-NLS-2$
		tableViewer.setCellEditors(new CellEditor[] { null, null, null, null,
				new CheckboxCellEditor(table) });
		tableViewer.setCellModifier(new ODCellModifier());
		tableViewer.setInput(model);
		// ///////////////
		createButton(container);
	}

	private void createButton(Composite container) {
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (ObjectDescriptor od : model) {
					od.create = true;
				}
				tableViewer.refresh();
			}
		});
		btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnNewButton.setText("Select All"); //$NON-NLS-1$

		Button btnNewButton_1 = new Button(container, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (ObjectDescriptor od : model) {
					od.create = false;
				}
				tableViewer.refresh();
			}
		});
		btnNewButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnNewButton_1.setText("DeSelect All"); //$NON-NLS-1$

		Button btnNewButton_3 = new Button(container, SWT.NONE);
		btnNewButton_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (ObjectDescriptor od : model) {
					od.create = !od.exists;
				}
				tableViewer.refresh();
			}
		});
		btnNewButton_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnNewButton_3.setText("Default"); //$NON-NLS-1$
		new Label(container, SWT.NONE);
	}

	public void setModel(ArrayList<ObjectDescriptor> model) {
		this.model = model;
	}

	class ODContentProvider implements IStructuredContentProvider {

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		public Object[] getElements(Object inputElement) {
			@SuppressWarnings("unchecked")
			ArrayList<ObjectDescriptor> als = (ArrayList<ObjectDescriptor>) inputElement;
			ObjectDescriptor[] ods = new ObjectDescriptor[als.size()];
			als.toArray(ods);
			return ods;
		}
	}

	class ODLabelProvider extends BaseLabelProvider implements
			ITableLabelProvider, ITableColorProvider {
		Color red = new Color(null, 255, 0, 0);
		Color green = new Color(null, 0, 255, 0);
		Color orange = new Color(null, 255, 128, 0);

		public Color getForeground(Object element, int columnIndex) {
			ObjectDescriptor od = (ObjectDescriptor) element;
			if (columnIndex == 3 && od.exists)
				return orange;
			else if (columnIndex == 4 && od.create) {
				return od.exists ? red : green;
			}
			return null;
		}

		public Color getBackground(Object element, int columnIndex) {

			return null;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			ObjectDescriptor od = (ObjectDescriptor) element;
			Image img = null;
			if (columnIndex == 4)
				img = PrototypeImagesUtils.getImage(od.create ? "checked.gif" //$NON-NLS-1$
						: "unchecked.gif"); //$NON-NLS-1$
			return img;
		}

		public String getColumnText(Object element, int columnIndex) {
			ObjectDescriptor od = (ObjectDescriptor) element;
			switch (columnIndex) {
			case 1:
				return od.name;
			case 2:
				return od.type;
			case 3:
				return od.exists ? "YES" : "NO"; //$NON-NLS-1$ //$NON-NLS-2$
			case 4:
				return od.create ? (od.exists ? "override" : "create") : ""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return ""; //$NON-NLS-1$
		}

	}

	class ODCellModifier implements ICellModifier {

		public boolean canModify(Object element, String property) {
			return "create".equals(property); //$NON-NLS-1$
		}

		public Object getValue(Object element, String property) {
			ObjectDescriptor od = (ObjectDescriptor) element;
			if ("create".equals(property)) //$NON-NLS-1$
				return od.create;
			return null;
		}

		public void modify(Object element, String property, Object value) {
			ObjectDescriptor od = (ObjectDescriptor) ((TableItem) element)
					.getData();
			if ("create".equals(property)) { //$NON-NLS-1$
				od.create = (Boolean) value;
				tableViewer.refresh(od);
			}
		}
	}
}

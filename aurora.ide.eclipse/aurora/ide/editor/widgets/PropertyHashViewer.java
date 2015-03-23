package aurora.ide.editor.widgets;

import java.util.HashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.editor.AttributeValue;
import aurora.ide.celleditor.CellEditorFactory;
import aurora.ide.celleditor.ICellEditor;
import aurora.ide.editor.PropertyViewer;
import aurora.ide.editor.core.ICategoryTableViewer;
import aurora.ide.editor.core.IViewer;
import aurora.ide.editor.widgets.core.CategoryLabel;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.node.action.ActionListener;
import aurora.ide.node.action.AddPropertyAction;
import aurora.ide.node.action.CategroyAction;
import aurora.ide.node.action.CharSortAction;
import aurora.ide.node.action.RefreshAction;
import aurora.ide.node.action.RemovePropertyAction;

public class PropertyHashViewer extends PropertyViewer implements
		ICategoryTableViewer {
	public static final String COLUMN_PROPERTY = "PROPERTY";
	public static final String COLUMN_VALUE = "VALUE";
	public static final String COLUMN_DOCUMENT = "DOCUMENT";
	public static final String[] TABLE_COLUMN_PROPERTIES = { COLUMN_PROPERTY,
			COLUMN_VALUE, COLUMN_DOCUMENT };
	private boolean isCategory;

	private Composite parent;
	private TableViewer tableViewer;
	protected IViewer page;
	private SashForm sashForm;
	private HashMap customerEditors = new HashMap();

	private Label elementDocument;

	public PropertyHashViewer(IViewer page, Composite parent) {
		this.page = page;
		this.parent = parent;
	}

	public void setData(CompositeMap data) throws ApplicationException {
		Assert.isTrue(tableViewer != null, "The viewer has not been created!");
		tableViewer.setInput(data);
		int xWidth = parent.getBounds().width;
		if (xWidth < 100)
			xWidth = 800;
		// final int appendWidth = 100;
		// Table table = tableViewer.getTable();
		// int columnWidth = (xWidth - appendWidth) / table.getColumnCount();
		// for (int i = 0, n = table.getColumnCount(); i < n; i++) {
		// table.getColumn(i).setWidth(columnWidth);
		// }
		setCellEditors();

		// Element em = LoadSchemaManager.getSchemaManager().getElement(data);
		Element em = CompositeMapUtil.getElement(data);
		elementDocument.setText("");
		if (em != null) {
			String document = em.getDocument();
			if (document != null)
				elementDocument.setText(document);
		}
	}

	public String clear(boolean validation) {
		if (tableViewer != null) {
			String errorMessage = clearCellEditor(validation);
			if (errorMessage != null)
				return errorMessage;
			tableViewer.getTable().removeAll();
			elementDocument.setText("");
		}
		return null;
	}

	public void createEditor() {
		Assert.isTrue(tableViewer == null, "The viewer has been created!");
		sashForm = new SashForm(parent, SWT.VERTICAL);
		ViewForm viewForm = new ViewForm(sashForm, SWT.NONE);
		elementDocument = new Label(sashForm, SWT.LEFT);

		viewForm.setLayout(new FillLayout());
		createToolbar(viewForm);
		createMainContent(viewForm);
		sashForm.setWeights(new int[] { 92, 8 });
	}

	public void createEditor(boolean showElementDocument) {
		Assert.isTrue(tableViewer == null, "The viewer has been created!");
		sashForm = new SashForm(parent, SWT.VERTICAL);
		ViewForm viewForm = new ViewForm(sashForm, SWT.NONE);
		elementDocument = new Label(sashForm, SWT.LEFT);

		viewForm.setLayout(new FillLayout());
		createToolbar(viewForm);
		createMainContent(viewForm);
		if (!showElementDocument) {
			sashForm.SASH_WIDTH = 0;
			sashForm.setWeights(new int[] { 100, 0 });
		} else {
			sashForm.setWeights(new int[] { 92, 8 });
		}
	}

	private void createMainContent(ViewForm viewForm) {
		tableViewer = new TableViewer(viewForm, SWT.BORDER | SWT.FULL_SELECTION);
		Table mTable = tableViewer.getTable();
		tableViewer.setLabelProvider(new PropertyHashLabelProvider());

		PropertyHashContentProvider contentProvider = new PropertyHashContentProvider(
				this);
		tableViewer.setContentProvider(contentProvider);
		tableViewer.setCellModifier(new PropertyHashCellModifier(this));
		tableViewer.setColumnProperties(TABLE_COLUMN_PROPERTIES);
		// set default editor is textCellEditor
		tableViewer.setCellEditors(new CellEditor[] { null,
				new TextCellEditor(mTable), null });
		mTable.setLinesVisible(true);
		mTable.setHeaderVisible(true);

		TableColumn propertycolumn = new TableColumn(mTable, SWT.LEFT);
		propertycolumn.setWidth(150);
		propertycolumn.setText(LocaleMessage.getString("property.name"));
		TableColumn valueColumn = new TableColumn(mTable, SWT.LEFT);
		valueColumn.setText(LocaleMessage.getString("value"));
		valueColumn.setWidth(150);

		TableColumn descColumn = new TableColumn(mTable, SWT.LEFT);
		descColumn.setText(LocaleMessage.getString("description"));
		descColumn.setWidth(250);
		viewForm.setContent(tableViewer.getControl());
		addKeyListener();
		tableViewer.setSorter(new PropertyHashSorter(this, contentProvider));
		propertycolumn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				((PropertyHashSorter) tableViewer.getSorter()).doSort(0);
				repaint();
				refresh();
			}
		});
		refresh();
	}

	private void createToolbar(ViewForm viewForm) {
		ToolBar toolBar = new ToolBar(viewForm, SWT.RIGHT | SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		fillActionToolBars(toolBarManager);
		viewForm.setTopLeft(toolBar);
	}

	public void createEditor(CompositeMap data) throws ApplicationException {
		createEditor();
		setData(data);
	}

	public boolean isCategory() {
		return isCategory;
	}

	public void setCategory(boolean isCategory) {
		this.isCategory = isCategory;
		repaint();
	}

	private void repaint() {
		try {
			clear(false);
			setData(getInput());
		} catch (Exception e) {
			DialogUtil.showExceptionMessageBox(e);
		}
	}

	public void refresh() {
		if (tableViewer != null && !tableViewer.getTable().isDisposed()) {
			tableViewer.refresh();
		}
	}

	public void refresh(boolean dirty) {
		if (dirty) {
			repaint();
			page.refresh(dirty);
		} else
			refresh();
	}

	public CompositeMap getInput() {
		return (CompositeMap) tableViewer.getInput();

	}

	private void fillActionToolBars(ToolBarManager actionBarManager) {
		Action addAction = new AddPropertyAction(this,
				ActionListener.DefaultImage);
		Action removeAction = new RemovePropertyAction(this,
				ActionListener.DefaultImage);
		Action refreshAction = new RefreshAction(this,
				ActionListener.DefaultImage);

		CategroyAction categroyAction = new CategroyAction(this);
		CharSortAction charSortAction = new CharSortAction(this);

		actionBarManager.add(createActionContributionItem(addAction));
		actionBarManager.add(createActionContributionItem(refreshAction));
		actionBarManager.add(createActionContributionItem(removeAction));
		actionBarManager.add(createActionContributionItem(categroyAction));
		actionBarManager.add(createActionContributionItem(charSortAction));
		actionBarManager.update(true);
	}

	private ActionContributionItem createActionContributionItem(IAction action) {
		ActionContributionItem aci = new ActionContributionItem(action);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		return aci;
	}

	private void addKeyListener() {
		tableViewer.getTable().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					removePropertyAction();
				}
			}

			public void keyReleased(KeyEvent e) {

			}
		});

	}

	public Control getControl() {
		return sashForm;
	}

	public Object getFocus() {
		ISelection selection = tableViewer.getSelection();
		if (selection == null)
			return null;
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		AttributeValue av = (AttributeValue) obj;
		return av;
	}

	public TableViewer getViewer() {
		return tableViewer;
	}

	public void addEditor(String property, ICellEditor cellEditor) {
		customerEditors.put(property, cellEditor);
	}

	public String clearCellEditor(boolean validation) {
		Object[] editors = customerEditors.values().toArray();
		for (int i = 0; i < editors.length; i++) {
			ICellEditor ed = (ICellEditor) editors[i];
			if (validation) {
				if (!ed.validValue(ed.getSelection())) {
					return ed.getErrorMessage();
				}
			}
		}
		for (int i = 0; i < editors.length; i++) {
			ICellEditor ed = (ICellEditor) editors[i];
			ed.dispose();
		}
		customerEditors.clear();
		return null;
	}

	public void setCellEditors() throws ApplicationException {
		Table table = tableViewer.getTable();
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			AttributeValue av = (AttributeValue) item.getData();
			if (av instanceof CategoryLabel) {
				continue;
			}
			ICellEditor cellEditor = CellEditorFactory
					.getInstance()
					.createCellEditor(this, av.getAttribute(), getInput(), item);
			if (cellEditor != null) {
				addEditor(av.getAttribute().getLocalName(), cellEditor);
				fillTableCellEditor(table, item, av.getAttribute().getQName(),
						cellEditor);
			}
		}
	}

	private void fillTableCellEditor(Table table, TableItem item,
			QualifiedName attributeQName, ICellEditor cellEditor) {
		TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.setEditor(cellEditor.getCellControl(), item, 1);
	}

	public Attribute getSelection() {
		ISelection selection = tableViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj == null)
			return null;
		AttributeValue av = (AttributeValue) obj;
		return av.getAttribute();
	}
}
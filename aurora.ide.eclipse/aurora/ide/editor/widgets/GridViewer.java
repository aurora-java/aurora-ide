package aurora.ide.editor.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.celleditor.CellEditorFactory;
import aurora.ide.celleditor.ICellEditor;
import aurora.ide.editor.AbstractCMViewer;
import aurora.ide.editor.core.ITableViewer;
import aurora.ide.editor.core.IViewer;
import aurora.ide.editor.widgets.core.ICellModifierListener;
import aurora.ide.editor.widgets.core.IGridLabelProvider;
import aurora.ide.editor.widgets.core.IGridViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.node.action.ActionListener;
import aurora.ide.node.action.AddElementAction;
import aurora.ide.node.action.RefreshAction;
import aurora.ide.node.action.RemoveElementAction;

public class GridViewer extends AbstractCMViewer implements ITableViewer {

	protected CompositeMap data;
	protected String[] columnTitles;
	protected String[] columnNames;
	protected CellEditor[] cellEditors;
	protected Composite container;
	protected ViewForm viewForm;
	protected TableViewer tableViewer;
	protected GridCellModifier cellModifiers;
	protected IGridLabelProvider labelProvider;
	protected ToolBarManager toolBarManager;
	protected int gridStyle;
	protected String filterColumn;
	protected Text filterText;
	private boolean isColumnPacked;
	protected IViewer parent;
	protected CompositeMap selection;

	public HashMap columnEditors = new HashMap();

	public GridViewer() {
		super();
	}

	public GridViewer(int gridStyle) {
		super();
		this.gridStyle = gridStyle;
	}

	public GridViewer(String[] columnNames, int gridStyle) {
		super();
		this.columnNames = columnNames;
		this.gridStyle = gridStyle;
	}

	public GridViewer(CompositeMap data, String[] columnNames, int gridStyle) {
		super();
		this.data = data;
		this.columnNames = columnNames;
		this.gridStyle = gridStyle;
	}

	public void addActions(IAction[] actions) {
		if (actions == null)
			return;
		for (int i = 0; i < actions.length; i++) {
			toolBarManager.add(createActionContributionItem(actions[i]));
		}
		toolBarManager.update(true);
	}

	public void addCellModifierListener(ICellModifierListener listener) {
		cellModifiers.addCellModifierListener(listener);
	}

	public void addEditor(String property, ICellEditor cellEditor) {
		columnEditors.put(property, cellEditor);
	}

	public void addFilter(ViewerFilter filter) {
		labelProvider.refresh();
		tableViewer.addFilter(filter);
	}

	public int getGridStyle() {
		return gridStyle;
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		tableViewer.addSelectionChangedListener(listener);
	}

	public CompositeMap beforeDispose() {
		CompositeMap records = null;
		if ((gridStyle & IGridViewer.isMulti) == 0) {
			IStructuredSelection selection = (IStructuredSelection) getViewer()
					.getSelection();
			records = (CompositeMap) selection.getFirstElement();
			if (records == null)
				return null;
		} else {
			Object[] elements = getCheckedElements();
			records = new CommentCompositeMap("records");
			for (int j = 0; j < elements.length; j++) {
				CompositeMap record = (CompositeMap) elements[j];
				records.addChild(record);
			}
		}
		return records;
	}

	public void setColumnTitles(String[] columnTitles) {
		this.columnTitles = columnTitles;
	}

	public String clearAll(boolean validation) {
		if (tableViewer.getTable() != null) {
			// validate values;
			String errorMessage = clearEditors(validation);
			if (errorMessage != null)
				return errorMessage;
			columnNames = null;
			columnTitles = null;
			cellEditors = null;
			data = null;
			isColumnPacked = false;
			tableViewer.getTable().dispose();
			viewForm.dispose();
			container.dispose();
		}
		return null;
	}

	public String clearEditors(boolean validation) {
		Object[] editors = columnEditors.values().toArray();
		// Not validate the null grid properties.
		if (getInput() != null && getInput().getChildsNotNull().size() > 0) {
			for (int i = 0; i < editors.length; i++) {
				ICellEditor ed = (ICellEditor) editors[i];
				if (validation) {
					if (!ed.validValue(ed.getSelection())) {
						return ed.getErrorMessage();
					}
				}
			}
		}
		for (int i = 0; i < editors.length; i++) {
			ICellEditor ed = (ICellEditor) editors[i];
			ed.dispose();
		}
		columnEditors.clear();
		return null;

	}

	public Composite createViewer(Composite parent) throws ApplicationException {

		container = createContainer(parent);
		filterText = createSerchBar();
		viewForm = createViewForm();
		toolBarManager = createToolBar();
		tableViewer = createTableViewer();

		if ((gridStyle & IGridViewer.NoToolBar) == 0)
			viewForm.setTopLeft(toolBarManager.getControl());
		viewForm.setContent(tableViewer.getControl());
		container.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				selection = beforeDispose();

			}
		});
		if (data != null) {
			setData(data);
		}
		return container;
	}

	public void setTableSorter(ViewerSorter viewSorter) {
		tableViewer.setSorter(viewSorter);
	}

	public void createViewer(Composite parent, CompositeMap data)
			throws ApplicationException {
		createViewer(parent);
		setData(data);
	}

	public ICellEditor getCellEditor(String property) {
		Object editor = columnEditors.get(property);
		if (editor != null)
			return (ICellEditor) editor;
		return null;
	}

	public Object[] getCheckedElements() {
		TableItem[] children = tableViewer.getTable().getItems();
		ArrayList v = new ArrayList(children.length);
		for (int i = 0; i < children.length; i++) {
			TableItem item = children[i];
			if (item.getChecked()) {
				v.add(item.getData());
			}
		}
		return v.toArray();
	}

	public Control getControl() {
		return container;
	}

	public String getFilterColumn() {
		return filterColumn;
	}

	public String[] getColumnNames() throws ApplicationException {
		if (columnNames == null)
			columnNames = CompositeMapUtil.getArrayAttrNames(data);
		return columnNames;
	}

	public String[] getColumnTitles() throws ApplicationException {
		if (columnTitles == null)
			columnTitles = getColumnNames();
		return columnTitles;
	}

	public CompositeMap getInput() {
		return data;

	}

	public IViewer getParent() {
		return parent;
	}

	public CompositeMap getSelection() {
		if (!container.isDisposed()) {
			return beforeDispose();
		}
		return selection;
	}

	public CompositeMap getFocus() {
		IStructuredSelection selection = (IStructuredSelection) getViewer()
				.getSelection();
		CompositeMap record = (CompositeMap) selection.getFirstElement();
		return record;
	}

	public TableViewer getViewer() {
		return tableViewer;
	}

	public void packColumns() {
		if (tableViewer.getTable() == null || isColumnPacked)
			return;
		for (int i = 0; i < tableViewer.getTable().getColumnCount(); i++) {
			TableColumn column = tableViewer.getTable().getColumn(i);
			column.pack();
		}
		isColumnPacked = true;
	}

	public void refresh(boolean dirty) {
		if (dirty && parent != null)
			parent.refresh(true);
		else {
			if (tableViewer != null && !tableViewer.getTable().isDisposed()) {
				if (data != null && data.getChilds() != null) {
					labelProvider.refresh();
					tableViewer.setInput(data);
				} else {
					tableViewer.getTable().removeAll();
				}
			}
		}
	}

	public void removeCellModifierListener(ICellModifierListener listener) {
		cellModifiers.removeCellModifierListener(listener);
	}

	public void setActions(IAction[] actions) {
		toolBarManager.removeAll();
		if (actions == null)
			return;
		for (int i = 0; i < actions.length; i++) {
			toolBarManager.add(createActionContributionItem(actions[i]));
		}
		toolBarManager.update(true);
	}

	public void setCellEditors(CellEditor[] cellEditors) {
		this.cellEditors = cellEditors;
	}

	public void setData(CompositeMap data) throws ApplicationException {
		this.data = data;
		if (tableViewer != null) {
			createDefaultActions();
			if (tableViewer.getColumnProperties() == null)
				createTableColumns();
			if (data != null && data.getChilds() != null) {
				tableViewer.setInput(data);
				if (isAllChecked())
					setAllChecked(tableViewer.getTable(), true);
			}
		}
	}

	public void refreshData() {
		tableViewer.setInput(data);
	}

	public void setFilterColumn(String filterColumn) {
		this.filterColumn = filterColumn;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	public void setGridStyle(int gridStyle) {
		this.gridStyle = gridStyle;
	}

	public void setParent(IViewer parent) {
		this.parent = parent;
	}

	protected void createDefaultActions() {
		toolBarManager.removeAll();
		if ((gridStyle & IGridViewer.NoToolBar) == 0) {
			if ((gridStyle & IGridViewer.fullEditable) != 0) {
				Element element = null;
				try {
					// element =
					// LoadSchemaManager.getSchemaManager().getElement(
					// data);
					element = CompositeMapUtil.getElement(data);
				} catch (Exception e) {
					// do nothing
				}
				if (element != null && element.isArray()) {
					final QualifiedName qName = element.getElementType()
							.getQName();
					Action addAction = new AddElementAction(this, data, qName,
							ActionListener.NONE);
					addAction.setText("");
					addAction.setHoverImageDescriptor(AuroraPlugin
							.getImageDescriptor(LocaleMessage
									.getString("add.icon")));

					Action removeAction = new RemoveElementAction(this,
							ActionListener.DefaultImage);
					Action refreshAction = new RefreshAction(this,
							ActionListener.DefaultImage);
					toolBarManager.add(createActionContributionItem(addAction));
					toolBarManager
							.add(createActionContributionItem(refreshAction));
					toolBarManager
							.add(createActionContributionItem(removeAction));
					toolBarManager.update(true);
					tableViewer.getTable().addKeyListener(new KeyListener() {
						public void keyPressed(KeyEvent e) {
							if (e.keyCode == SWT.DEL) {
								removeElement();
							}
						}

						public void keyReleased(KeyEvent e) {
						}
					});
				}
			}

		}
		if ((gridStyle & IGridViewer.isMulti) != 0) {

			Action allCheckAction = new Action(
					LocaleMessage.getString("all.checed"),
					AuroraPlugin.getImageDescriptor(LocaleMessage
							.getString("checked.icon"))) {
				public void run() {
					setAllChecked(tableViewer.getTable(), true);
				}
			};
			Action unAllCheckAction = new Action(
					LocaleMessage.getString("non.checed"),
					AuroraPlugin.getImageDescriptor(LocaleMessage
							.getString("unchecked.icon"))) {
				public void run() {
					setAllChecked(tableViewer.getTable(), false);
				}
			};
			toolBarManager.add(createActionContributionItem(allCheckAction));
			toolBarManager.add(createActionContributionItem(unAllCheckAction));
			toolBarManager.update(true);

		}
	}

	protected CellEditor[] getCellEditors() throws ApplicationException {
		if (cellEditors != null)
			return cellEditors;
		if ((gridStyle & IGridViewer.fullEditable) == 0) {
			String[] columnNames = getColumnNames();
			cellEditors = new CellEditor[columnNames.length];
			for (int i = 0; i < columnNames.length; i++) {
				TextCellEditor tce = new TextCellEditor(tableViewer.getTable());
				Text text = (Text) tce.getControl();
				text.setEditable(false);
				cellEditors[i] = tce;
			}
		} else {
			List attrib_list = CompositeMapUtil.getArrayAttrs(data);
			cellEditors = new CellEditor[attrib_list.size()];
			int id = 0;
			for (Iterator it = attrib_list.iterator(); it.hasNext();) {
				Attribute attrib = (Attribute) it.next();
				ICellEditor cellEditor = CellEditorFactory.getInstance()
						.createCellEditor(this, attrib, null, null);
				if (cellEditor != null) {
					cellEditors[id++] = cellEditor.getCellEditor();
					addEditor(attrib.getLocalName(), cellEditor);
				} else {
					cellEditors[id++] = new TextCellEditor(
							tableViewer.getTable());
				}
			}
		}
		return cellEditors;
	}

	protected Composite createContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		container.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		return container;
	}

	protected void createTableCompoment() throws ApplicationException {
		if ((gridStyle & IGridViewer.fullEditable) == 0
				&& (gridStyle & IGridViewer.isOnlyUpdate) == 0) {
			labelProvider = new PlainCompositeMapLabelProvider(getColumnNames());
			tableViewer.setLabelProvider(labelProvider);
			tableViewer.setCellModifier(new PlainCompositeMapCellModifier());
		} else {
			labelProvider = new PropertyGridLabelProvider(getColumnNames(),
					this);
			tableViewer.setLabelProvider(labelProvider);
			cellModifiers = new GridCellModifier(this);
			tableViewer.setCellModifier(cellModifiers);

		}
	}

	protected ToolBarManager createToolBar() {
		ToolBar toolBar = new ToolBar(viewForm, SWT.RIGHT | SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);

		return toolBarManager;
	}

	protected ToolBar createToolBar(ViewForm viewForm) {
		ToolBar toolBar = new ToolBar(viewForm, SWT.RIGHT | SWT.FLAT);
		toolBarManager = new ToolBarManager(toolBar);
		return toolBar;
	}

	private ActionContributionItem createActionContributionItem(IAction action) {
		ActionContributionItem aci = new ActionContributionItem(action);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		return aci;
	}

	private Text createSerchBar() {
		Text filterText = null;
		if ((gridStyle & IGridViewer.filterBar) != 0) {
			Label headerLabel = new Label(container, SWT.NONE);
			String columnText = filterColumn;
			if (columnNames != null && columnTitles != null) {
				for (int i = 0; i < columnNames.length; i++) {
					if (filterColumn.equals(columnNames[i])) {
						columnText = columnTitles[i];
						break;
					}
				}
			}
			headerLabel.setText(LocaleMessage.getString("please.input")
					+ columnText + LocaleMessage.getString("prefix.of.column"));

			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			headerLabel.setLayoutData(gd);

			filterText = new Text(container, SWT.SINGLE | SWT.BORDER
					| SWT.SEARCH);
			gd.heightHint = 15;
			filterText.setLayoutData(gd);
		}
		return filterText;
	}

	private void createTableColumn(TableViewer tableViewer)
			throws ApplicationException {
		// String seq_imagePath = LocaleMessage.getString("property.icon");
		// Image idp =
		// AuroraPlugin.getImageDescriptor(seq_imagePath).createImage();
		// attribute.gif
		Image idp = ImagesUtils.getImage("attribute.gif");
		String[] fullColumnTitles = getfullColumnTitles();
		for (int i = 0; i < fullColumnTitles.length; i++) {
			TableColumn column = new TableColumn(tableViewer.getTable(),
					SWT.LEFT);
			column.setText(fullColumnTitles[i]);
			column.setImage(idp);
			if (i == 0) {
				column.pack();
			} else {
				setColumnWidth(column, tableViewer, fullColumnTitles.length - 1);
			}
		}
	}

	private void setColumnWidth(TableColumn column, TableViewer tableViewer,
			int propertyLength) {
		final int defaultScreenWidth = 800;
		final int defaultColumnWidth = 120;
		final int appendWidth = 100;
		int width = tableViewer.getTable().getParent().getBounds().width;
		width = width == 0 ? defaultScreenWidth : width;
		int columnWidth = (width - appendWidth) / (propertyLength);
		if (columnWidth > defaultColumnWidth) {
			column.setWidth(columnWidth);
			return;
		}
		if ((IGridViewer.isColumnPacked & gridStyle) != 0) {
			column.pack();
		} else {
			column.setWidth(columnWidth);
		}
	}

	protected void createTableColumns() throws ApplicationException {
		columnNames = getColumnNames();
		tableViewer.setColumnProperties(getfullColumnNames());
		createDefaultActions();
		createTableCompoment();
		createTableColumn(tableViewer);
		CellEditor[] editors = getfullCellEditor();
		tableViewer.setCellEditors(editors);

	}

	private String[] getfullColumnNames() throws ApplicationException {
		String[] columnNames = getColumnNames();
		String[] fullColumnNames = new String[columnNames.length + 1];
		fullColumnNames[0] = "seq";
		System.arraycopy(columnNames, 0, fullColumnNames, 1, columnNames.length);
		return fullColumnNames;
	}

	private String[] getfullColumnTitles() throws ApplicationException {
		String[] columnTitles = getColumnTitles();
		String[] fullColumnTitles = new String[columnTitles.length + 1];
		if ((gridStyle & IGridViewer.NoSeqColumn) == 0) {
			fullColumnTitles[0] = "序号";
		} else {
			fullColumnTitles[0] = "";
		}
		System.arraycopy(columnTitles, 0, fullColumnTitles, 1,
				columnTitles.length);
		return fullColumnTitles;
	}

	private CellEditor[] getfullCellEditor() throws ApplicationException {
		CellEditor[] cellEditor = getCellEditors();
		CellEditor[] fullCellEditor = new CellEditor[cellEditor.length + 1];
		System.arraycopy(cellEditor, 0, fullCellEditor, 1, cellEditor.length);
		return fullCellEditor;
	}

	protected TableViewer createTableViewer() {
		int style = 0;
		if ((gridStyle & IGridViewer.isMulti) == 0) {
			style = SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL
					| SWT.H_SCROLL;
		} else
			style = SWT.CHECK | SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER
					| SWT.V_SCROLL | SWT.H_SCROLL;
		final TableViewer tableViewer = new TableViewer(viewForm, style);

		tableViewer.setContentProvider(new PropertyGridContentProvider());
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		if (isAllChecked())
			setAllChecked(tableViewer.getTable(), true);

		if (isSerchable()) {
			final CompositeMapFilter filter = new CompositeMapFilter(
					filterColumn);
			tableViewer.addFilter(filter);
			filterText.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					String filterChars = filterText.getText();
					filterChars = filterChars.toUpperCase();
					filter.setFilterString(filterChars);
					refresh(false);

				}
			});
		}
		return tableViewer;
	}

	protected ViewForm createViewForm() {
		ViewForm viewForm = new ViewForm(container, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		viewForm.setLayoutData(gd);
		viewForm.setLayout(new FillLayout());
		return viewForm;
	}

	private boolean isAllChecked() {
		return (gridStyle & IGridViewer.isAllChecked) != 0;
	}

	private boolean isSerchable() {
		return (gridStyle & IGridViewer.filterBar) != 0;
	}

	public void setAllChecked(Table table, boolean state) {
		TableItem[] children = table.getItems();
		for (int i = 0; i < children.length; i++) {
			TableItem item = children[i];
			item.setChecked(state);
		}
	}

}
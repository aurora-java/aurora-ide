package aurora.ide.component.wizard;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.composite.XMLOutputter;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;
import uncertain.schema.editor.AttributeValue;
import uncertain.schema.editor.CompositeMapEditor;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.bm.BMUtil;
import aurora.ide.bm.editor.GridDialog;
import aurora.ide.celleditor.AuroraCellEditor;
import aurora.ide.celleditor.CellInfo;
import aurora.ide.celleditor.ComboxCellEditor;
import aurora.ide.celleditor.ICellEditor;
import aurora.ide.editor.core.IViewer;
import aurora.ide.editor.widgets.GridViewer;
import aurora.ide.editor.widgets.PropertyHashViewer;
import aurora.ide.editor.widgets.core.ICellModifierListener;
import aurora.ide.editor.widgets.core.IGridViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.node.action.AddElementAction;

public class CreateFormFromDataSetAction extends AddElementAction {
	final static String idColumn = "id";
	final static String modelColumn = "model";
	final static String queryDataSetColumn = "queryDataSet";
	final static String title = LocaleMessage
			.getString("create.form.from.dataset");
	private final static String lineSeparator = System
			.getProperty("line.separator");
	private final static String screenBodyColumn = "screenBody";

	public CreateFormFromDataSetAction(IViewer viewer, CompositeMap parentCM,
			QualifiedName qName, int actionStyle) {
		super(viewer, parentCM, qName, actionStyle);

	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage
				.getString("wizard.icon"));
	}

	public void run() {
		if (currentNode == null
				|| !AuroraConstant.DataSetQN.equals(currentNode.getQName())) {
			DialogUtil.showErrorMessageBox("It is not a dataSet element!");
			return;
		}
		if (currentNode.getString(idColumn) == null){
			DialogUtil.showErrorMessageBox("请先设置ID属性.");
			return;
		}
		try {
			if (BMUtil.getFieldsFromDS(currentNode)== null) {
				DialogUtil.showErrorMessageBox("此dataSet没有可用字段.");
				return;
			}
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return;
		}
		CompositeMap view = currentNode.getParent().getParent();
		if (view == null || !AuroraConstant.ViewQN.equals(view.getQName())) {
			DialogUtil.showErrorMessageBox("Its parent's parent is not a view element!");
			return;
		}
		boolean successful = createForm(currentNode);
		if (viewer != null && successful) {
			viewer.refresh(true);
		}
	}

	private boolean createForm(CompositeMap dataSet) {
		FormWizard wizard = new FormWizard(dataSet);
		WizardDialog dialog = new WizardDialog(new Shell(), wizard);
		dialog.open();
		return wizard.isSuccessful();
	}

	class FormWizard extends Wizard implements IViewer {
		private boolean successful;
		private MainConfigPage mainConfigPage;
		private FieldPage fieldPage;
		private CompositeMap dataSet;

		public FormWizard(CompositeMap dataSet) {
			super();
			setText(title);
			this.dataSet = dataSet;
		}

		public void addPages() {
			mainConfigPage = new MainConfigPage(this, dataSet);
			mainConfigPage.setPageComplete(false);
			addPage(mainConfigPage);
			fieldPage = new FieldPage(this);
			addPage(fieldPage);
		}

		public boolean performFinish() {
			String prefix = dataSet.getPrefix();
			String uri = dataSet.getNamespaceURI();
			CompositeMap form = new CommentCompositeMap(prefix, uri, "form");

			int columnCount = mainConfigPage.getColumnCount();
			form.put("column", new Integer(columnCount));
			form.put("id", mainConfigPage.getId());

			HashMap columnFields = fieldPage.getColumnFields();
			HashMap changeData = fieldPage.getChangeData();
			HashMap allfields = getAllFields();
			HashMap fieldProperties = fieldPage.getFieldProperties();
			CompositeMap selectedDataSet = mainConfigPage.getDataSet();
			Set names = allfields.keySet();
			if (names.size() == 0)
				return true;
			CompositeMap view = dataSet.getParent().getParent();
			addToScreenBody(form);
			for (int i = 0; i < columnCount; i++) {
				CompositeMap vBox = new CommentCompositeMap(prefix, uri, "vBox");
				ArrayList fields = (ArrayList) columnFields.get(new Integer(
						i + 1));
				Iterator iterator = fields.iterator();
				for (; iterator.hasNext();) {
					String field = (String) iterator.next();
					if (names.contains(field)) {
						CompositeMap record = (CompositeMap) allfields
								.get(field);
						String editorString = record.getString("editor");
						CompositeMap editorMap = new CommentCompositeMap(prefix, uri,
								editorString);
						if (changeData.get(field) != null) {
							record = (CompositeMap) changeData.get(field);
						}
						editorMap.put("name", record.getString("name"));
						editorMap.put("bindTarget", selectedDataSet
								.getString("id"));
						editorMap.put("prompt", record.getString("prompt"));
						CompositeMap editorProperties = (CompositeMap) fieldProperties
								.get(field);
						if (editorProperties != null) {
							CompositeMapEditor cmEditor = new CompositeMapEditor(
									LoadSchemaManager.getSchemaManager(),
									editorProperties);
							AttributeValue[] abvs = cmEditor.getAttributeList();
							for (int k = 0; k < abvs.length; k++) {
								AttributeValue abv = abvs[k];
								if (abv.getValue() != null)
									editorMap.put(abv.getAttribute().getName(),
											abv.getValue());
							}
						}
						vBox.addChild(editorMap);
					}
				}
				form.addChild(vBox);
			}
			if (mainConfigPage.isCreateButton()) {
				String type = mainConfigPage.getType();
				CompositeMap js = view.getChild("script");
				if (js == null) {
					js = new CommentCompositeMap("script");
					view.addChild(0, js);
				}
				String jsString = js.getText();
				if (jsString == null)
					jsString = "";
				String functionName = selectedDataSet.getString("id") + "_"
						+ type;
				final int jsLevel = 3;
				String functionIndent = "";
				for (int i = 0; i < jsLevel; i++) {
//					functionIndent += XMLOutputter.DEFAULT_INDENT;
					functionIndent += CommentXMLOutputter.DEFAULT_INDENT;
				}

				jsString = jsString + lineSeparator + functionIndent
						+ "function  " + functionName + "()" + "{"
						+ lineSeparator;
				if ("query".equals(type)) {
					Object[] objs = getQueryDataSets(view, selectedDataSet
							.getString("id"));
					if (objs != null) {
						for (int i = 0; i < objs.length; i++) {
							jsString = jsString + functionIndent
									+ XMLOutputter.DEFAULT_INDENT + "$('"
									+ (String) objs[i] + "').query();"
									+ lineSeparator;
						}
					} else {
						jsString = null;
					}

				} else {
					jsString = jsString + functionIndent
							+ XMLOutputter.DEFAULT_INDENT + " $('"
							+ selectedDataSet.getString("id") + "').submit();"
							+ lineSeparator;
				}
				if (jsString != null) {
					jsString = jsString + functionIndent + "}";
					js.setText(jsString);
					CompositeMap buttons = new CommentCompositeMap(prefix, uri, "hBox");
					CompositeMap button = new CommentCompositeMap(prefix, uri,
							"button");
					button.put("click", functionName);
					buttons.addChild(button);
					addToScreenBody(buttons);
				}

			}
			successful = true;
			return true;
		}

		private void addToScreenBody(CompositeMap grid) {
			CompositeMap view = currentNode.getParent().getParent();
			CompositeMap screenBody = view.getChild(screenBodyColumn);
			if (screenBody == null) {
				screenBody = new CommentCompositeMap(currentNode.getPrefix(), currentNode.getNamespaceURI(), screenBodyColumn);
				view.addChild(screenBody);
			}
			screenBody.addChild(grid);
		}

		private Object[] getQueryDataSets(CompositeMap viewer,
				String dataSetId) {
			List dataSetUsed = new ArrayList();
			CompositeMap dataSets = viewer.getChild("dataSets");
			if (dataSets == null || dataSets.getChildsNotNull().size() == 0)
				return null;
			Iterator childs = dataSets.getChildsNotNull().iterator();
			for (; childs.hasNext();) {
				CompositeMap child = (CompositeMap) childs.next();
				if (dataSetId.equals(child.getString(queryDataSetColumn))) {
					dataSetUsed.add(child.getString("id"));
				}
			}
			return dataSetUsed.toArray();
		}

		public HashMap getAllFields() {
			HashMap allFields = new HashMap();
			CompositeMap selection = fieldPage.getSelection();
			Iterator iterator = selection.getChildIterator();
			if (iterator != null) {
				for (; iterator.hasNext();) {
					CompositeMap record = (CompositeMap) iterator.next();
					allFields.put(record.getString("name"), record);

				}
			}
			return allFields;
		}

		public CompositeMap getFields() {
			return mainConfigPage.getFields();
		}

		public boolean isSuccessful() {
			return successful;
		}

		public void createPageControls(Composite pageContainer) {
		}

		public void refresh(boolean isDirty) {
			try {
				fieldPage.repaint();
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
		}

		public int getColumnCount() {
			return mainConfigPage.getColumnCount();
		}

	}

	class MainConfigPage extends WizardPage {
		public static final String PAGE_NAME = "mainPage";
		private CompositeMap dataSets;
		private String bindTarget;
		private String id;
		private boolean submit;
		private Set idSet;
		private IViewer parentViewer;
		private CompositeMap fields;
		private int columnCount;
		private String type;
		private CompositeMap dataSet;

		protected MainConfigPage(IViewer parent, CompositeMap dataSet) {
			super(PAGE_NAME);
			setTitle(title + "--" + LocaleMessage.getString("mainpage"));
			this.parentViewer = parent;
			this.dataSet = dataSet;
		}

		public String getType() {
			return type;
		}

		public boolean canFlipToNextPage() {
			try {
				fields=BMUtil.getFieldsFromDS(dataSet);
				if (fields == null) {
					DialogUtil.showErrorMessageBox("此dataSet没有可用字段.");
					return false;
				}
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
				return false;
			}
			return super.canFlipToNextPage();
		}

		public void createControl(Composite parent) {
			final Composite content = new Composite(parent, SWT.NONE);
			content.setLayout(new GridLayout(4, false));

			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

			Group dataSetGroup = new Group(content, SWT.NONE);
			gridData.horizontalSpan = 4;
			dataSetGroup.setLayoutData(gridData);
			dataSetGroup.setText(LocaleMessage.getString("bindtarget"));
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			dataSetGroup.setLayout(layout);

			final Text bindTargetText = new Text(dataSetGroup, SWT.NONE);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			bindTargetText.setLayoutData(gridData);

			Button browseButton = new Button(dataSetGroup, SWT.PUSH);
			browseButton.setText(LocaleMessage.getString("openBrowse"));
			browseButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						dataSet = selectDataSet();
					} catch (ApplicationException e) {
						DialogUtil.showExceptionMessageBox(e);
					}
					if (dataSet != null) {
						bindTargetText.setText(dataSet.getString("id"));
						parentViewer.refresh(true);
					}
				}
			});

			Label label = new Label(content, SWT.CANCEL);
			label.setText(LocaleMessage.getString("please.input.id"));

			final Text idText = new Text(content, SWT.NONE);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 3;
			idText.setLayoutData(gridData);

			bindTargetText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (bindTargetText.getText() != null
							&& !(bindTargetText.getText().equals(""))) {
						bindTarget = bindTargetText.getText();
						if (idText.getText() == null
								|| idText.getText().equals("")) {
							idText.setText(bindTarget + "_form");
						}
					}
					checkDialog();
				}
			});

			idText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (idText.getText() != null
							&& !(idText.getText().equals(""))) {
						id = idText.getText();
					}
					checkDialog();
				}
			});

			Label columnLabel = new Label(content, SWT.CANCEL);
			columnLabel.setText(LocaleMessage
					.getString("please.input.column.num"));

			final Text columnText = new Text(content, SWT.NONE);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 3;
			columnText.setLayoutData(gridData);

			columnText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (columnText.getText() != null
							&& !(columnText.getText().equals(""))) {
						try {
							columnCount = Integer
									.parseInt(columnText.getText());
						} catch (NumberFormatException e2) {
							DialogUtil
									.showErrorMessageBox("please.input.number");
							columnText.setText("");
							return;
						}
					}
					checkDialog();
					parentViewer.refresh(true);
				}
			});
			// set Default
			columnText.setText("1");

			final Button submitButton = new Button(content, SWT.CHECK);
			submitButton.setText(LocaleMessage.getString("auto.sumbit.button"));

			Label formTypeLabel = new Label(content, SWT.NONE);
			final Button queryFormButton = new Button(content, SWT.RADIO);
			final Button sumbitFormButton = new Button(content, SWT.RADIO);

			submitButton.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					if (submitButton.getSelection()) {
						submit = true;
						queryFormButton.setEnabled(true);
						sumbitFormButton.setEnabled(true);
						content.redraw();
					} else {
						submit = false;
						queryFormButton.setEnabled(false);
						sumbitFormButton.setEnabled(false);
						content.redraw();
					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});

			formTypeLabel.setText(LocaleMessage.getString("form.type"));

			sumbitFormButton.setText(LocaleMessage.getString("save"));
			sumbitFormButton.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					if (sumbitFormButton.getSelection()) {
						type = "save";
					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});

			queryFormButton.setText(LocaleMessage.getString("query"));

			queryFormButton.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					if (queryFormButton.getSelection()) {
						type = "query";
					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
			queryFormButton.setSelection(true);
			type = "query";
			queryFormButton.setEnabled(false);
			sumbitFormButton.setEnabled(false);
			setControl(content);
			bindTargetText.setText(dataSet.getString(idColumn));
		}

		public CompositeMap getFields() {
			return fields;
		}

		public int getColumnCount() {
			return columnCount;
		}

		public boolean isCreateButton() {
			return submit;
		}

		private CompositeMap selectDataSet() throws ApplicationException {
			if (dataSets == null) {
				CompositeMap view = dataSet.getParent().getParent();
				dataSets = getQualifyDataSetList(view);
				if (dataSets == null || dataSets.getChildsNotNull().size() == 0) {
					DialogUtil.showErrorMessageBox("no.dataSet.available");
					return null;
				}
			}
			String[] columnProperties = { "id", "model" };
			GridViewer grid = new GridViewer(dataSets, columnProperties,
					IGridViewer.NONE);
			grid.setFilterColumn("id");
			GridDialog dialog = new GridDialog(new Shell(), grid);
			if (dialog.open() == Window.OK) {
				return dialog.getSelected();
			}
			return null;
		}

		private CompositeMap getQualifyDataSetList(CompositeMap parentCM) throws ApplicationException {
			CompositeMap dataSets = parentCM.getChild("dataSets");
			if (dataSets == null || dataSets.getChildsNotNull().size() == 0)
				return null;
			CompositeMap qualifyDataSetList = new CommentCompositeMap(dataSets
					.getPrefix(), dataSets.getNamespaceURI(), "dataSets");
			Iterator childs = dataSets.getChildsNotNull().iterator();
			for (; childs.hasNext();) {
				CompositeMap child = (CompositeMap) childs.next();
				String id = child.getString(idColumn);
				if (id != null && !("".equals(id))&&BMUtil.getFieldsFromDS(child)!=null) {
					qualifyDataSetList.addChild(child);
				}
			}
			return qualifyDataSetList;
		}

		public String getId() {
			return id;
		}

		public CompositeMap getDataSet() {
			return dataSet;
		}

		private String outputErrorMessage() {
			if (idSet == null) {
				idSet = new HashSet();
				CompositeMapUtil.collectAttribueValues(idSet, "id", dataSet
						.getRoot());
			}

			if (bindTarget == null || bindTarget.equals("")) {
				return LocaleMessage
						.getString("DataSet.selection.can.not.be.null");

			}
			if (id == null || id.equals("")) {
				return LocaleMessage.getString("id.can.not.be.null");
			}
			if (idSet.contains(id)) {
				return LocaleMessage
						.getString("This.id.has.exists.please.change.it");
			}
			return null;
		}

		private void checkDialog() {
			String errorMessage = outputErrorMessage();
			setErrorMessage(errorMessage);
			if (errorMessage != null) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		}

	}

	class FieldPage extends WizardPage implements IViewer {
		public static final String PAGE_NAME = "FiledPage";
		public static final String uri = "http://www.aurora-framework.org/application";
		private FormWizard wizard;
		private GridViewer grid;
		ModifyCompositeMapListener newCompositeMap;
		GridColumnSEQSorter sorter;
		PropertyHashViewer hashViewer;
		HashMap field_properties = new HashMap();

		protected FieldPage(FormWizard wizard) {
			super(PAGE_NAME);
			setTitle(title + "--" + LocaleMessage.getString("filed.page"));
			this.wizard = wizard;
		}

		public void createControl(Composite parent) {
			Composite content = new Composite(parent, SWT.NONE);
			content.setLayout(new FillLayout());
			SashForm sashForm = new SashForm(content, SWT.VERTICAL);
			try {
				createGridViewer(sashForm);
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
				return;
			}
			createHashViewer(sashForm);
			grid.addSelectionChangedListener(new ElementSelectionListener());
			sashForm.setWeights(new int[] { 5, 5 });
			setControl(content);
		}

		private void createHashViewer(Composite content) {
			hashViewer = new PropertyHashViewer(this, content);
			hashViewer.createEditor();

		}

		public CompositeMap getSelection() {
			return grid.getSelection();
		}

		private void createGridViewer(Composite parent) throws ApplicationException {
			Composite content = new Composite(parent, SWT.NONE);

			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			content.setLayout(gridLayout);

			CompositeMap fields = wizard.getFields();
			CompositeMap filedNames = new CommentCompositeMap();
			Iterator it = fields.getChildsNotNull().iterator();
			for (; it.hasNext();) {
				CompositeMap child = (CompositeMap) it.next();
				String targetNode = child.getString("name");
				if (targetNode == null)
					continue;
				CompositeMap newChild = new CommentCompositeMap();
				newChild.put("name", targetNode);
				newChild.put("prompt", child.getString("prompt"));
				newChild.put("editor", AuroraCellEditor.getInstance()
						.getEditorName(child));
				newChild.put("columnIndex", "1");
				filedNames.addChild(newChild);

			}
			String[] columnProperties = { "name", "prompt", "editor",
					"columnIndex" };

			grid = new GridViewer(columnProperties, IGridViewer.isMulti
					| IGridViewer.isOnlyUpdate | IGridViewer.NoSeqColumn);
			grid.setParent(this);
			grid.createViewer(content);

			int columnCount = wizard.getColumnCount();
			sorter = new GridColumnSEQSorter(this, columnCount, filedNames);
			grid.setTableSorter(sorter);

			TableViewer tableView = grid.getViewer();
			CellEditor[] celleditors = new CellEditor[columnProperties.length];
			for (int i = 0; i < columnProperties.length; i++) {
				celleditors[i] = new TextCellEditor(tableView.getTable());
			}
			// CompositeMap editors = wizard.getEditors();
			QualifiedName qn = new QualifiedName(uri, "Field");
			ComplexType type = LoadSchemaManager.getSchemaManager()
					.getComplexType(qn);
			List editors = LoadSchemaManager.getSchemaManager()
					.getElementsOfType(type);
			Iterator editorIt = editors.iterator();
			String[] items = new String[editors.size()];
			for (int i = 0; editorIt.hasNext(); i++) {
				Element editor = (Element) editorIt.next();
				items[i] = editor.getLocalName();
			}
			CellInfo cellProperties = new CellInfo(grid, "editor",
					false);
			cellProperties.setItems(items);
			ICellEditor cellEditor = new ComboxCellEditor(cellProperties);
			cellEditor.init();
			celleditors[columnProperties.length - 2] = cellEditor
					.getCellEditor();
			grid.addEditor("editor", cellEditor);
			grid.setCellEditors(celleditors);
			grid.setData(filedNames);
			newCompositeMap = new ModifyCompositeMapListener();
			grid.addCellModifierListener(newCompositeMap);

			// Create a composite for the add and remove buttons.
			Composite buttonGroup = new Composite(content, SWT.NONE);
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			buttonGroup.setLayoutData(gridData);

			RowLayout rowLayout = new RowLayout();
			rowLayout.type = SWT.VERTICAL;
			rowLayout.marginTop = 30;
			rowLayout.pack = false;
			buttonGroup.setLayout(rowLayout);

			Button add = new Button(buttonGroup, SWT.PUSH);
			add.setText(LocaleMessage.getString("up"));
			add.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					sorter.up(grid.getFocus());
				}
			});
			Button edit = new Button(buttonGroup, SWT.PUSH);
			edit.setText(LocaleMessage.getString("down"));
			edit.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					sorter.down(grid.getFocus());
				}
			});

		}

		public HashMap getChangeData() {
			return newCompositeMap.getChangeData();
		}

		public HashMap getFieldProperties() {
			return field_properties;
		}

		public void refresh(boolean isDirty) {
			grid.refresh(false);
		}

		public void repaint() throws ApplicationException {

			// if this page not init
			if (newCompositeMap == null)
				return;

			CompositeMap fields = wizard.getFields();
			CompositeMap filedNames = new CommentCompositeMap();
			for (Iterator it = fields.getChildsNotNull().iterator(); it
					.hasNext();) {
				CompositeMap child = (CompositeMap) it.next();
				String targetNode = child.getString("name");
				if (targetNode == null)
					continue;
				CompositeMap newChild = new CommentCompositeMap();
				newChild.put("name", targetNode);
				newChild.put("prompt", child.getString("prompt"));
				newChild.put("editor", AuroraCellEditor.getInstance()
						.getEditorName(child));
				newChild.put("columnIndex", "1");
				filedNames.addChild(newChild);

			}

			int columnCount = wizard.getColumnCount();
			sorter = new GridColumnSEQSorter(this, columnCount, filedNames);
			grid.setTableSorter(sorter);
			grid.setData(filedNames);
			newCompositeMap.clear();
			field_properties.clear();
			// grid.refresh(false);
		}

		public HashMap getColumnFields() {
			return sorter.getColumnFields();
		}

		class ModifyCompositeMapListener implements ICellModifierListener {
			HashMap records = new HashMap();

			public void modify(CompositeMap record, String property,
					String value) {
				String name = record.getString("name");
				CompositeMap newRecord = null;
				if (records.get(name) == null) {
					newRecord = new CommentCompositeMap("column");
					newRecord.put("name", name);
					records.put(name, newRecord);
				} else {
					newRecord = (CompositeMap) records.get(name);
				}
				if ("columnIndex".equals(property)) {
					int column = 0;
					if (value != null && !value.equals("")) {
						try {
							column = Integer.parseInt(value);
						} catch (NumberFormatException e2) {
							DialogUtil
									.showErrorMessageBox("please.input.number");
							return;
						}
						if (column > sorter.getColumnCount() || column < 1) {
							DialogUtil.showErrorMessageBox(LocaleMessage
									.getString("column.number.warning")
									+ sorter.getColumnCount()
									+ LocaleMessage.getString("between"));
							return;
						}
					}
					if (column == 0)
						column = 1;
					sorter.changeColumn(record, column);
				} else if ("editor".equals(property)) {

					String field = record.getString("name");
					Object proObject = field_properties.get(field);
					if (proObject != null) {
						CompositeMap editor = (CompositeMap) proObject;
						if (value.equals(editor.getName())) {
							return;

						}
					}
					try {
						hashViewer.setData(getEditorData(record, value));
					} catch (ApplicationException e) {
						DialogUtil.showExceptionMessageBox(e);
					}
				}
				newRecord.put(property, value);
			}

			private CompositeMap getEditorData(CompositeMap data,
					String editorName) {
				String field = data.getString("name");
				QualifiedName editorQualified = new QualifiedName(
						AuroraCellEditor.aurorUri, editorName);
				String prefix = CompositeMapUtil.getContextPrefix(data,
						editorQualified);
				CompositeMap editor = new CommentCompositeMap(prefix, editorQualified
						.getNameSpace(), editorQualified.getLocalName());
				field_properties.put(field, editor);
				return editor;
			}

			public HashMap getChangeData() {
				return records;
			}

			public void clear() {
				records.clear();
			}

		}

		class GridColumnSEQSorter extends ViewerSorter {
			private HashMap field_column = new HashMap();
			private HashMap column_fields = new HashMap();
			private IViewer viewer;
			private int columnCount;

			public GridColumnSEQSorter(IViewer viewer, int columnCount,
					CompositeMap allFields) {
				this.viewer = viewer;
				this.columnCount = columnCount;
				initArrays(allFields);
			}

			public int getColumnCount() {
				return columnCount;
			}

			private void initArrays(CompositeMap allFields) {
				for (int i = 0; i < columnCount; i++) {
					column_fields.put(new Integer(i + 1), new ArrayList());
				}
				ArrayList list = (ArrayList) column_fields.get(new Integer(1));
				Iterator iterator = allFields.getChildsNotNull().iterator();
				for (; iterator.hasNext();) {
					CompositeMap field = (CompositeMap) iterator.next();
					String fieldName = field.getString("name");
					list.add(fieldName);
					field_column.put(fieldName, new Integer(1));
				}

			}

			public void up(CompositeMap field) {
				String fieldName = field.getString("name");
				Integer columnIndex = (Integer) field_column.get(fieldName);
				ArrayList list = (ArrayList) column_fields.get(columnIndex);
				int index = list.indexOf(fieldName);
				if (index <= 0)
					return;
				list.remove(index);
				list.add(index - 1, fieldName);
				viewer.refresh(true);
			}

			public void down(CompositeMap field) {
				String fieldName = field.getString("name");
				Integer columnIndex = (Integer) field_column.get(fieldName);
				ArrayList list = (ArrayList) column_fields.get(columnIndex);
				int index = list.indexOf(fieldName);
				if (index == list.size() - 1)
					return;
				list.remove(index);
				list.add(index + 1, fieldName);
				viewer.refresh(true);
			}

			public void changeColumn(CompositeMap field, int targetColumn) {

				String fieldName = field.getString("name");
				Integer columnIndex = (Integer) field_column.get(fieldName);
				if (columnIndex.intValue() == targetColumn) {
					return;
				}
				ArrayList list = (ArrayList) column_fields.get(columnIndex);
				list.remove(fieldName);
				ArrayList moveTo = (ArrayList) column_fields.get(new Integer(
						targetColumn));
				moveTo.add(fieldName);
				field_column.put(fieldName, new Integer(targetColumn));
			}

			public int category(Object element) {
				CompositeMap field = (CompositeMap) element;
				String fieldName = field.getString("name");
				Object object = field_column.get(fieldName);
				if (object == null) {
					return columnCount + 1;
				}
				return ((Integer) object).intValue();
			}

			public int compare(Viewer viewer, Object e1, Object e2) {
				int result;
				int cat1 = category(e1);
				int cat2 = category(e2);
				if (cat1 != cat2) {
					return cat1 - cat2;
				}
				String field1 = ((CompositeMap) e1).getString("name");
				String field2 = ((CompositeMap) e2).getString("name");

				ArrayList list = (ArrayList) column_fields
						.get(new Integer(cat2));
				int index1 = list.indexOf(field1);
				int index2 = list.indexOf(field2);

				if (index1 != index2) {
					if (index2 == -1)
						return -1;
					else {
						return index1 - index2;
					}
				}
				result = getComparator().compare(field1, field2);
				return result;
			}

			public HashMap getColumnFields() {
				return column_fields;
			}
		}

		class ElementSelectionListener implements ISelectionChangedListener {
			private boolean validError = false;

			public void selectionChanged(SelectionChangedEvent event) {
				if (validError) {
					validError = false;
					return;
				}
				String errorMessage = hashViewer.clear(true);
				if (errorMessage != null) {
					validError = true;
					grid.getViewer().setSelection(
							new StructuredSelection(grid.getFocus()));
					return;
				}
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				CompositeMap data = (CompositeMap) selection.getFirstElement();
				if (data == null)
					return;
				grid.setFocus(data);
				try {
					hashViewer.setData(getEditorData(data));
				} catch (ApplicationException e) {
					DialogUtil.showExceptionMessageBox(e);
				}
			}

			private CompositeMap getEditorData(CompositeMap data) {
				String field = data.getString("name");
				Object proObject = field_properties.get(field);
				if (proObject != null) {
					return (CompositeMap) proObject;
				}
				QualifiedName editorQualified = new QualifiedName(
						AuroraCellEditor.aurorUri, data.getString("editor"));
				String prefix = CompositeMapUtil.getContextPrefix(data,
						editorQualified);
				CompositeMap editor = new CommentCompositeMap(prefix, editorQualified
						.getNameSpace(), editorQualified.getLocalName());
				field_properties.put(field, editor);
				return editor;
			}
		}
	}
}

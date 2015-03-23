package aurora.ide.component.wizard;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.bm.BMUtil;
import aurora.ide.bm.editor.GridDialog;
import aurora.ide.celleditor.CellInfo;
import aurora.ide.celleditor.ComboxCellEditor;
import aurora.ide.celleditor.ICellEditor;
import aurora.ide.editor.core.IViewer;
import aurora.ide.editor.widgets.GridViewer;
import aurora.ide.editor.widgets.core.ICellModifierListener;
import aurora.ide.editor.widgets.core.IGridViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.node.action.AddElementAction;

public class CreateGridFromDataSetAction extends AddElementAction {
	
	private final static String idColumn = "id";
	private final static String modelColumn = "model";
	private final static String screenBodyColumn = "screenBody";
	final static String title = LocaleMessage.getString("create.grid.from.dataset");
	public CreateGridFromDataSetAction(IViewer viewer, CompositeMap dataSet,
			QualifiedName qName,int actionStyle) {
		super(viewer, dataSet, qName,actionStyle);

	}
	public ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("wizard.icon"));
	}
	public void run() {
		if(currentNode == null || !AuroraConstant.DataSetQN.equals(currentNode.getQName())){
			DialogUtil.showErrorMessageBox("It is not a dataSet element!");
			return;
		}
		if (currentNode.getString(idColumn) == null){
			DialogUtil.showErrorMessageBox("请先设置ID属性.");
			return;
		}
		try {
			if (getFieldsFromDS(currentNode)== null) {
				DialogUtil.showErrorMessageBox("此dataSet没有可用字段.");
				return;
			}
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return;
		}
		CompositeMap view = currentNode.getParent().getParent();
		if(view == null || !AuroraConstant.ViewQN.equals(view.getQName())){
			DialogUtil.showErrorMessageBox("Its parent's parent is not a view element!");
			return;
		}
		boolean successful = createGrid(currentNode);
		if (viewer != null && successful) {
			viewer.refresh(true);
		}
	}

	private boolean createGrid(CompositeMap dataSet) {
		GridWizard wizard = new GridWizard(dataSet);
		WizardDialog dialog = new WizardDialog(new Shell(), wizard);
		dialog.open();
		return wizard.isSuccessful();
	}

	class GridWizard extends Wizard implements IViewer {
		private boolean successful;
		private MainConfigPage mainConfigPage;
		private FieldPage fieldPage;
		private CompositeMap dataSet;
		public GridWizard(CompositeMap dataSet) {
			super();
			this.dataSet = dataSet;
		}

		public void addPages() {
			mainConfigPage = new MainConfigPage(this, dataSet);
			addPage(mainConfigPage);
			fieldPage = new FieldPage(this);
			addPage(fieldPage);
			fieldPage.setPageComplete(false);
		}

		public boolean performFinish() {
			String prefix = dataSet.getPrefix();
			String uri = dataSet.getNamespaceURI();
			CompositeMap grid = mainConfigPage.getGrid();
			CompositeMap columns = new CommentCompositeMap(prefix, uri, "columns");
			CompositeMap editors = new CommentCompositeMap(prefix, uri, "editors");
			grid.addChild(columns);
			grid.addChild(editors);
			Iterator selection = fieldPage.getSelection().getChildsNotNull()
					.iterator();
			HashMap hash = fieldPage.getMap();

			HashMap useEditor = new HashMap();
			for (; selection.hasNext();) {
				CompositeMap column = (CompositeMap) selection.next();
				String name = column.getString("name");
				CompositeMap record = null;
				if (hash.get(name) == null) {
					record = new CommentCompositeMap(prefix,uri,"column");
					record.put("name", name);
				} else {
					record = (CompositeMap) hash.get(name);
					record.setNameSpace(prefix, uri);
					String editorType = record.getString("editor");
					record.put("editor", getEditorId(useEditor, grid
							.getString("id"), editorType));
				}
				columns.addChild(record);
			}
			Iterator it = useEditor.keySet().iterator();
			if (it != null) {
				for (; it.hasNext();) {
					String type = (String) it.next();
					CompositeMap newRecord = new CommentCompositeMap(prefix,uri,type);
					newRecord.put("id", useEditor.get(type));
					editors.addChild(newRecord);
				}
			}
			addGrid(grid);
			successful = true;
			return true;
		}
		private void addGrid(CompositeMap grid){
			CompositeMap view = currentNode.getParent().getParent();
			CompositeMap screenBody = view.getChild(screenBodyColumn);
			if(screenBody == null){
				screenBody = new CommentCompositeMap(currentNode.getPrefix(),currentNode.getNamespaceURI(),screenBodyColumn);
				view.addChild(screenBody);
			}
			screenBody.addChild(grid);
		}

		public CompositeMap getFields() {
			return mainConfigPage.getFields();
		}

		public boolean isSuccessful() {
			return successful;
		}
		public void createPageControls(Composite pageContainer) {
		}

		private String getEditorId(HashMap editors, String gridId, String type) {
			String editorId = null;
			if (type != null && editors.get(type) == null) {
				editorId = gridId + "_" + type;
				editors.put(type, editorId);
			} else {
				editorId = (String) editors.get(type);
			}
			return editorId;
		}

		public void refresh(boolean isDirty) {
			try {
				fieldPage.repaint();
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
		}

	}

	class MainConfigPage extends WizardPage {
		public static final String PAGE_NAME = "mainPage";
		private CompositeMap dataSets;
		private String bindTarget;
		private String id;
		private boolean navBar;
		private String width;
		private String height;
		private Set allIds;

		private boolean addButton;
		private boolean deleteButton;
		private boolean saveButton;
		IViewer parentViewer;
		private CompositeMap fields;
		private CompositeMap dataSet;
		protected MainConfigPage(IViewer parentView, CompositeMap dataSet) {
			super(PAGE_NAME);
			setTitle(title+"--"+LocaleMessage.getString("mainpage"));
			this.parentViewer = parentView;
			this.dataSet = dataSet;
		}
		
		public boolean canFlipToNextPage() {
			try {
				fields=getFieldsFromDS(dataSet);
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
			Composite content = new Composite(parent, SWT.NONE);
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
							idText.setText(bindTarget + "_grid");
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

			Label widthLabel = new Label(content, SWT.CANCEL);
			widthLabel.setText(LocaleMessage.getString("please.input.width"));
			final Text widthText = new Text(content, SWT.NONE);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 1;
			gridData.grabExcessHorizontalSpace = true;
			widthText.setLayoutData(gridData);
			widthText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (widthText.getText() != null
							&& !(widthText.getText().equals(""))) {
						width = widthText.getText();
					}
					checkDialog();
				}
			});

			Label heightLabel = new Label(content, SWT.CANCEL);
			heightLabel.setText(LocaleMessage.getString("please.input.height"));
			final Text heightText = new Text(content, SWT.NONE);
			heightText.setLayoutData(gridData);
			heightText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (heightText.getText() != null
							&& !(heightText.getText().equals(""))) {
						height = heightText.getText();
					}
					checkDialog();
				}
			});
			final Button navBarButton = new Button(content, SWT.CHECK);
			navBarButton.setText(LocaleMessage.getString("enbaleNavBar"));
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 4;
			navBarButton.setLayoutData(gridData);
			navBarButton.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					if (navBarButton.getSelection()) {
						navBar = true;
					} else
						navBar = false;
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});

			Group buttonGroup = new Group(content, SWT.NONE);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 4;
			buttonGroup.setLayoutData(gridData);
			buttonGroup.setText(LocaleMessage.getString("selectbuttons"));
			layout = new GridLayout();
			layout.numColumns = 3;
			buttonGroup.setLayout(layout);

			final Button add = new Button(buttonGroup, SWT.CHECK);
			add.setText(LocaleMessage.getString("addbutton"));
			add.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					if (add.getSelection()) {
						addButton = true;
					} else
						addButton = false;
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});

			final Button delete = new Button(buttonGroup, SWT.CHECK);
			delete.setText(LocaleMessage.getString("deletebutton"));
			delete.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					if (delete.getSelection()) {
						deleteButton = true;
					} else
						deleteButton = false;
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});

			final Button save = new Button(buttonGroup, SWT.CHECK);
			save.setText(LocaleMessage.getString("savebutton"));
			save.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					if (save.getSelection()) {
						saveButton = true;
					} else
						saveButton = false;
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
			setControl(content);
			bindTargetText.setText(dataSet.getString(idColumn));
		}

		public CompositeMap getFields() {
			return fields;
		}

		public CompositeMap getGrid() {
			String prefix = dataSet.getPrefix();
			String uri = dataSet.getNamespaceURI();
			CompositeMap grid = new CommentCompositeMap(prefix, uri, "grid");
			grid.put("id", id);
			grid.put("bindTarget", bindTarget);
			grid.put("width", width);
			grid.put("height", height);
			grid.put("navBar", String.valueOf(navBar));
			if (addButton || deleteButton || saveButton) {
				CompositeMap toolBar = new CommentCompositeMap(prefix, uri, "toolBar");
				grid.addChild(toolBar);
				createButton(toolBar, addButton, "add");
				createButton(toolBar, deleteButton, "delete");
				createButton(toolBar, saveButton, "save");
			}
			return grid;
		}

		private void createButton(CompositeMap toobar, boolean create,
				String type) {
			if (create) {
				CompositeMap button = new CommentCompositeMap(childQN.getPrefix(), childQN.getNameSpace(), "button");
				button.put("type", type);
				toobar.addChild(button);
			}
		}

		private CompositeMap selectDataSet() throws ApplicationException {
			if(dataSets == null){
				CompositeMap view = dataSet.getParent().getParent();
				dataSets = getAvailableDataSets(view);
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

		private String outputErrorMessage() {
			if (allIds == null) {
				allIds = new HashSet();
				CompositeMapUtil.collectAttribueValues(allIds, "id", dataSet.getRoot());
			}

			if (bindTarget == null || bindTarget.equals("")) {
				return LocaleMessage
						.getString("DataSet.selection.can.not.be.null");

			}
			if (id == null || id.equals("")) {
				return LocaleMessage.getString("id.can.not.be.null");
			}
			if (allIds.contains(id)) {
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
		private CompositeMap getAvailableDataSets(CompositeMap parentCM) throws ApplicationException {
			CompositeMap dataSets = parentCM.getChild("dataSets");
			if (dataSets == null || dataSets.getChildsNotNull().size() == 0)
				return null;
			CompositeMap qualifyDataSetList = new CommentCompositeMap(dataSets
					.getPrefix(), dataSets.getNamespaceURI(), "dataSets");
			Iterator childs = dataSets.getChildsNotNull().iterator();
			for (; childs.hasNext();) {
				CompositeMap child = (CompositeMap) childs.next();
				String id = child.getString(idColumn);
				if (id != null && !("".equals(id))&&getFieldsFromDS(child)!=null) {
					qualifyDataSetList.addChild(child);
				}
			}
			return qualifyDataSetList;
		}

	}

	class FieldPage extends WizardPage implements IViewer {
		public static final String PAGE_NAME = "FiledPage";
		public static final String uri = "http://www.aurora-framework.org/application";
		private GridWizard wizard;
		private GridViewer grid;
		ModifyCompositeMapListener newCompositeMap;

		protected FieldPage(GridWizard wizard) {
			super(PAGE_NAME);
			setTitle(title+"--"+LocaleMessage.getString("filed.page"));
			this.wizard = wizard;
		}

		public void createControl(Composite parent) {
			Composite content = new Composite(parent, SWT.NONE);
			content.setLayout(new GridLayout());
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
				filedNames.addChild(newChild);

			}
			String[] columnProperties = { "name", "prompt", "editor" };
			grid = new GridViewer(columnProperties, IGridViewer.isMulti
					| IGridViewer.fullEditable | IGridViewer.isAllChecked);
			grid.setParent(this);
			try {
				grid.createViewer(content);
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}

			TableViewer tableView = grid.getViewer();
			CellEditor[] celleditors = new CellEditor[columnProperties.length];
			for (int i = 0; i < columnProperties.length-1; i++) {
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
			celleditors[columnProperties.length-1] = cellEditor.getCellEditor();
			grid.addEditor("editor", cellEditor);
			grid.setCellEditors(celleditors);
			try {
				grid.setData(filedNames);
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
			newCompositeMap = new ModifyCompositeMapListener();
			grid.addCellModifierListener(newCompositeMap);
			setControl(content);
			setPageComplete(true);
		}

		public CompositeMap getSelection() {
			return grid.getSelection();
		}

		public HashMap getMap() {
			return newCompositeMap.getSelection();
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
				filedNames.addChild(newChild);

			}
			grid.setData(filedNames);
			newCompositeMap.clear();
		}

		class ModifyCompositeMapListener implements ICellModifierListener {
			HashMap records = new HashMap();

			public void modify(CompositeMap record, String property,
					String value) {
				String name = record.getString("name");
				CompositeMap newRecord = null;
				if (records.get(name) == null) {
					newRecord = new CommentCompositeMap(record.getPrefix(),record.getNamespaceURI(),"column");
					newRecord.put("name", name);
					records.put(name, newRecord);
				} else {
					newRecord = (CompositeMap) records.get(name);
				}
				newRecord.put(property, value);
			}

			public HashMap getSelection() {
				return records;
			}

			public void clear() {
				records.clear();
			}
		}

	}
	public CompositeMap getFieldsFromDS(CompositeMap dataSet) throws ApplicationException{
		if(dataSet == null)
			return null;
		CompositeMap fields = dataSet.getChild("fields");
		if(fields != null && fields.getChilds() != null && fields.getChilds().size()>0){
			return fields;
		}
		String classPath = dataSet.getString(modelColumn);
		CompositeMap model = AuroraResourceUtil.loadFromResource(BMUtil.getBMResourceFromClassPath(classPath));
		if(model == null)
			return null;
		fields = model.getChild("fields");
		if(fields != null && fields.getChilds() != null && fields.getChilds().size()>0){
			return fields;
		}
		return null;
	}
}

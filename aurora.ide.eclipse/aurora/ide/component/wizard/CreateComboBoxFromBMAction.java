package aurora.ide.component.wizard;


import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.bm.BMUtil;
import aurora.ide.bm.editor.GridDialog;
import aurora.ide.celleditor.BoolCellEditor;
import aurora.ide.celleditor.CellInfo;
import aurora.ide.celleditor.ICellEditor;
import aurora.ide.editor.core.IViewer;
import aurora.ide.editor.widgets.GridViewer;
import aurora.ide.editor.widgets.PropertyHashViewer;
import aurora.ide.editor.widgets.WizardPageRefreshable;
import aurora.ide.editor.widgets.core.IGridViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.node.action.AddElementAction;

public class CreateComboBoxFromBMAction extends AddElementAction {
	final static String title = "创建ComboBox";
	public CreateComboBoxFromBMAction(IViewer viewer, CompositeMap currentNode, QualifiedName childQN, int actionStyle) {
		super(viewer, currentNode, childQN, actionStyle);
	}
	public ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("wizard.icon"));
	}
	public void run() {
		if (currentNode == null || !AuroraConstant.FieldsQN.equals(currentNode.getQName())) {
			DialogUtil.showErrorMessageBox("当前选中的不是Fields节点!");
			return;
		}
		ComboBoxWizard wizard = new ComboBoxWizard(currentNode);
		WizardDialog dialog = new WizardDialog(new Shell(), wizard);
		dialog.open();
		boolean successful = wizard.isSuccessful();
		if (viewer != null && successful) {
			viewer.refresh(true);
		}
	}
	class ComboBoxWizard extends Wizard implements IViewer {
		private boolean successful;
		private MainConfigPage mainConfigPage;
		private FieldPage fieldPage;
		private CompositeMap fieldsNode;

		public ComboBoxWizard(CompositeMap fieldsNode) {
			super();
			setText(title);
			this.fieldsNode = fieldsNode;
		}

		public void addPages() {
			mainConfigPage = new MainConfigPage(this, fieldsNode);
			mainConfigPage.setPageComplete(false);
			addPage(mainConfigPage);
			fieldPage = new FieldPage(this);
			addPage(fieldPage);
		}

		public boolean performFinish() {
			String prefix = CompositeMapUtil.getContextPrefix(fieldsNode, childQN);
			childQN.setPrefix(prefix);
			CompositeMap fieldNode = new CommentCompositeMap(childQN.getPrefix(), childQN.getNameSpace(), childQN
					.getLocalName());
			fieldNode.put("name", mainConfigPage.getFieldNameText().getText());
			fieldNode.put("options", mainConfigPage.getDataSetText().getText());
			String[] fields = mainConfigPage.getFields();
			fieldNode.put("displayField", fields[mainConfigPage.getDisplayFieldCombo().getSelectionIndex()]);
			fieldNode.put("valueField", fields[mainConfigPage.getValueFieldCombo().getSelectionIndex()]);
			fieldNode.put("returnField", fields[mainConfigPage.getReturnFieldCombo().getSelectionIndex()]);
			CompositeMap mappingNode = new CommentCompositeMap(childQN.getPrefix(), childQN.getNameSpace(), "mapping");
			CompositeMap fieldsSelection = fieldPage.getSelection();
			if (fieldsSelection != null && fieldsSelection.getChildIterator() != null) {
				for (Iterator it = fieldsSelection.getChildIterator(); it.hasNext();) {
					CompositeMap field = (CompositeMap) it.next();
					CompositeMap mapNode = new CommentCompositeMap(childQN.getPrefix(), childQN.getNameSpace(), "map");
					String fieldName = field.getString("name");
					mapNode.put("from", fieldName);
					mapNode.put("to", fieldName);
					mappingNode.addChild(mapNode);
				}
				fieldNode.addChild(mappingNode);
			}
			CompositeMapUtil.addElement(fieldsNode, fieldNode);
			successful = true;
			return true;
		}

		public boolean isSuccessful() {
			return successful;
		}

		public void createPageControls(Composite pageContainer) {
		}
		public void refresh(boolean isDirty) {

		}
		public CompositeMap getDataSetFields() {
			return mainConfigPage.getDataSetFields();
		}
	}

	class MainConfigPage extends WizardPageRefreshable {
		public static final String PAGE_NAME = "mainPage";
		private CompositeMap dataSetFields;
		private Text dataSetText;
		private Text fieldNameText;
		private Combo displayFieldCombo;
		private Combo valueFieldCombo;
		private Combo returnFieldCombo;
		private CompositeMap srcDataSet;
		private String[] fileds;
		public String[] getFields() {
			return fileds;
		}
		public CompositeMap getDataSetFields() {
			return dataSetFields;
		}
		public Text getDataSetText() {
			return dataSetText;
		}
		public Text getFieldNameText() {
			return fieldNameText;
		}
		public Combo getDisplayFieldCombo() {
			return displayFieldCombo;
		}
		public Combo getValueFieldCombo() {
			return valueFieldCombo;
		}
		public Combo getReturnFieldCombo() {
			return returnFieldCombo;
		}
		public CompositeMap getSrcDataSet() {
			return srcDataSet;
		}
		protected MainConfigPage(IViewer parent, CompositeMap fieldsNode) {
			super(PAGE_NAME);
			setTitle(title + "--" + LocaleMessage.getString("mainpage"));
		}
		public boolean canFlipToNextPage() {
			if (dataSetText != null && !("".equals(dataSetText.getText())) && dataSetFields == null) {
				DialogUtil.showErrorMessageBox("此dataSet没有可用字段.");
				return false;
			}
			return super.canFlipToNextPage();
		}
		public void checkPageValues() {
			String dateSetId = dataSetText.getText();
			if (dateSetId == null || "".equals(dateSetId)) {
				updatePageStatus("必须指定DataSet!");
				return;
			}
			try {
				dataSetFields = BMUtil.getFieldsFromDS(srcDataSet);
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
			if (dataSetFields == null) {
				updatePageStatus("此DataSet没有定义field字段.");
				return;
			}
			if (dataSetFields == null) {
				updatePageStatus("此BM没有定义field字段.");
				return;
			}
			if ((fileds = getFieldNames(dataSetFields)) == null) {
				updatePageStatus("此BM没有定义field字段.");
				return;
			}
			String fieldName = fieldNameText.getText();
			if (fieldName == null || fieldName.equals("")) {
				updatePageStatus("请输入字段名");
				return;
			}
			if (displayFieldCombo.getSelectionIndex() == -1) {
				updatePageStatus("显示字段必输.");
				return;
			}

			if (valueFieldCombo.getSelectionIndex() == -1) {
				updatePageStatus("值字段必输.");
				return;
			}
			if (returnFieldCombo.getSelectionIndex() == -1) {
				updatePageStatus("返回字段必输.");
				return;
			}
			updatePageStatus(null);
		}
		public void createControl(Composite parent) {
			Composite container = new Composite(parent, SWT.NULL);
			container.setLayout(new GridLayout(3, false));
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			Label label = new Label(container, SWT.NULL);
			label.setText("选择DataSet");
			dataSetText = new Text(container, SWT.BORDER | SWT.SINGLE);
			dataSetText.setLayoutData(gd);
			dataSetText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					checkPageValues();
					if (fileds != null) {
						displayFieldCombo.clearSelection();
						displayFieldCombo.setItems(fileds);
						valueFieldCombo.clearSelection();
						valueFieldCombo.setItems(fileds);
						returnFieldCombo.clearSelection();
						returnFieldCombo.setItems(fileds);
					}
				}
			});
			Button pickBMButton = new Button(container, SWT.PUSH);
			pickBMButton.setText(LocaleMessage.getString("openBrowse"));
			pickBMButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						pickDataSet();
					} catch (ApplicationException e) {
						DialogUtil.showExceptionMessageBox(e);
					}
				}
				private void pickDataSet() throws ApplicationException {
					CompositeMap dataSets = getDataSets(currentNode);
					GridViewer grid = new GridViewer(null, IGridViewer.filterBar | IGridViewer.NoToolBar);
					grid.setData(dataSets);
					grid.setFilterColumn("id");
					grid.setColumnNames(new String[]{"id"});
					GridDialog dialog = new GridDialog(new Shell(), grid);
					if (dialog.open() == Window.OK) {
						srcDataSet = dialog.getSelected();
						if (srcDataSet != null && srcDataSet.getString("id") != null)
							dataSetText.setText(srcDataSet.getString("id"));
					}
				}
			});
			label = new Label(container, SWT.NULL);
			label.setText("字段名");
			fieldNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			fieldNameText.setLayoutData(gd);
			fieldNameText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					checkPageValues();
				}
			});
			label = new Label(container, SWT.NULL);
			label.setText("显示字段");
			displayFieldCombo = new Combo(container, SWT.BORDER | SWT.LEAD | SWT.DROP_DOWN);
			displayFieldCombo.setLayoutData(gd);
			displayFieldCombo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					checkPageValues();
				}
			});
			label = new Label(container, SWT.NULL);
			label.setText("值字段");
			valueFieldCombo = new Combo(container, SWT.BORDER | SWT.LEAD | SWT.DROP_DOWN);
			valueFieldCombo.setLayoutData(gd);
			valueFieldCombo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					checkPageValues();
				}
			});
			label = new Label(container, SWT.NULL);
			label.setText("返回字段");
			returnFieldCombo = new Combo(container, SWT.BORDER | SWT.LEAD | SWT.DROP_DOWN);
			returnFieldCombo.setLayoutData(gd);
			returnFieldCombo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					checkPageValues();
				}
			});
			setControl(container);
		}
	}

	class FieldPage extends WizardPageRefreshable {
		public static final String PAGE_NAME = "FiledPage";
		private ComboBoxWizard wizard;
		private GridViewer gridViewer;
		PropertyHashViewer hashViewer;
		HashMap field_properties = new HashMap();

		protected FieldPage(ComboBoxWizard wizard) {
			super(PAGE_NAME);
			setTitle(title + "--" + LocaleMessage.getString("filed.page"));
			this.wizard = wizard;
		}
		public void createControl(Composite parent) {
			Composite content = new Composite(parent, SWT.NONE);
			content.setLayout(new GridLayout());
			// String[] columnNames = {"name", "query", "display", "return"};
			// String[] columnTitles = {"字段", "筛选", "显示", "返回"};
			String[] columnNames = {"name"};
			String[] columnTitles = {"字段"};
			gridViewer = new GridViewer(columnNames, IGridViewer.isMulti | IGridViewer.isAllChecked
					| IGridViewer.isOnlyUpdate | IGridViewer.NoSeqColumn);
			gridViewer.setColumnTitles(columnTitles);
			try {
				gridViewer.createViewer(content);
				CellEditor[] celleditors = new CellEditor[columnNames.length];
				for (int i = 1; i < columnNames.length; i++) {
					CellInfo cellProperties = new CellInfo(gridViewer, columnNames[i], false);
					ICellEditor cellEditor = new BoolCellEditor(cellProperties);
					cellEditor.init();
					celleditors[i] = cellEditor.getCellEditor();
					gridViewer.addEditor(columnNames[i], cellEditor);
				}
				gridViewer.setCellEditors(celleditors);
				gridViewer.setData(wizard.getDataSetFields());
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
			setPageComplete(true);
			initPageValues();
			checkPageValues();
			setControl(content);
		}
		public void refreshPage() {
			if (!isInit()) {
				return;
			}
		}
		public CompositeMap getSelection() {
			if (gridViewer == null)
				return null;
			return gridViewer.getSelection();
		}
		public void checkPageValues() {
		}
	}
	private CompositeMap getDataSets(CompositeMap currentNode) {
		if (currentNode == null)
			return null;
		CompositeMap childNode = currentNode;
		CompositeMap parentNode = null;
		while ((parentNode = childNode.getParent()) != null) {
			if (AuroraConstant.DataSetSQN.equals(parentNode.getQName())) {
				break;
			}
			childNode = parentNode;
		}
		return parentNode;
	}
	private String[] getFieldNames(CompositeMap fieldsNode) {
		if (fieldsNode == null || fieldsNode.getChildIterator() == null)
			return null;
		String[] fileds = new String[fieldsNode.getChilds().size()];
		int index = 0;
		for (Iterator it = fieldsNode.getChildIterator(); it.hasNext();) {
			CompositeMap filed = (CompositeMap) it.next();
			fileds[index++] = filed.getString("name");
		}
		return fileds;
	}
}

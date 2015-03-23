package aurora.ide.bm.editor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.schema.Array;
import uncertain.schema.Element;
import uncertain.schema.IType;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.bm.BMUtil;
import aurora.ide.editor.BaseCompositeMapViewer;
import aurora.ide.editor.CompositeMapPage;
import aurora.ide.editor.FixedSizeControlListener;
import aurora.ide.editor.core.IViewer;
import aurora.ide.editor.widgets.GridViewer;
import aurora.ide.editor.widgets.PropertyHashViewer;
import aurora.ide.editor.widgets.core.IGridViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.node.action.ActionListener;
import aurora.ide.node.action.RefreshAction;
import aurora.ide.node.action.RemoveElementAction;

public class BusinessModelPage extends CompositeMapPage {
	private static final String PageId = "BusinessModelPage";
	private static final String PageTitle = LocaleMessage
			.getString("business.model.file");
	private CTabFolder mTabFolder;
	private CompositeMap data;
	private SashForm sashForm;
	private Composite shell;
	private ArrayList childViews;

	private static final String[] customTabs = new String[] { "primary-key",
			"order-by", "ref-fields" };
	private static final String ref_fields = "ref-fields";
	private List tabFolerNameList;
	private static Map customerTitles = new HashMap();
	static {
		customerTitles.put("fields", "字段");
		customerTitles.put("ref-fields", "引用字段");
		customerTitles.put("primary-key", "主键");
		customerTitles.put("order-by", "排序");
		customerTitles.put("relations", "关联");
		customerTitles.put("data-filters", "强制查询条件");
		customerTitles.put("query-fields", "可选查询条件");
		customerTitles.put("features", "特性");
		customerTitles.put("operations", "自定义操作");
	}

	public BusinessModelPage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		shell = form.getBody();
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);
		Element schemaElement = LoadSchemaManager.getSchemaManager()
				.getElement(AuroraConstant.ModelQN);
		if (schemaElement == null) {
			DialogUtil.showErrorMessageBox(LocaleMessage
					.getString("please.add.bm.schema.file"));
			return;
		}
		if (data == null) {
			String filePath = getFile().getAbsolutePath();
			try {
				CompositeLoader loader = AuroraResourceUtil.getCompsiteLoader();
				data = loader.loadByFile(filePath);
			} catch (IOException e) {
				DialogUtil.logErrorException(e);
				return;
			} catch (SAXException e) {
				String emptyExcption = "Premature end of file";
				if (e.getMessage() != null
						&& e.getMessage().indexOf(emptyExcption) != -1) {
					data = BMUtil.createBMTopNode();
					((CommentCompositeMap) data)
							.setComment("本文件为空,现在内容为系统自动创建,请修改并保存");
				} else {
					DialogUtil.showExceptionMessageBox(e);
					return;
				}
			}
			if (!data.getQName().equals(AuroraConstant.ModelQN)) {
				DialogUtil.showErrorMessageBox("文件" + filePath + "的"
						+ LocaleMessage.getString("this.root.element.is.not")
						+ AuroraConstant.ModelQN + " !");
				return;
			}
		}
		try {
			createContent(shell);
		} catch (ApplicationException e) {
			DialogUtil.logErrorException(e);
		}
	}

	protected void createContent(Composite shell) throws ApplicationException {
		Control[] childs = shell.getChildren();
		for (int i = 0; i < childs.length; i++) {
			Control temp = childs[i];
			if (!temp.isDisposed()) {
				temp.dispose();
			}
		}
		sashForm = new SashForm(shell, SWT.VERTICAL);
		initChildViews();
		createMasterContent(sashForm);
		createDetailContent(sashForm);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		sashForm.setLayout(new GridLayout());
		shell.layout(true);
		sashForm.setWeights(new int[] { 2, 4 });
		if (this.getSelectionTab() == null) {
			CompositeMap child = data.getChild("operations");
			String init_tab = child == null ? "fields" : "operations";
			this.setSelectionTab(init_tab);
		}
	}

	private void initChildViews() {
		tabFolerNameList = new ArrayList();
		if (childViews != null)
			childViews.clear();
		else
			childViews = new ArrayList();
	}

	protected void createMasterContent(Composite parent)
			throws ApplicationException {
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sashForm.setLayout(new GridLayout(2, false));
		PropertyHashViewer mPropertyEditor = new PropertyHashViewer(this,
				sashForm);
		childViews.add(mPropertyEditor);
		mPropertyEditor.createEditor(false);
		String errorMessage = mPropertyEditor.clear(true);
		if (errorMessage != null) {
			DialogUtil.showErrorMessageBox(errorMessage);
		}
		mPropertyEditor.setData(data);
		Group bmDescGroup = new Group(sashForm, SWT.NONE);
		bmDescGroup.setLayout(new FillLayout());
		bmDescGroup.setText("本BM功能描述");
		final StyledText bmDescSt = new StyledText(bmDescGroup, SWT.MULTI
				| SWT.WRAP | SWT.BORDER);
		final String bmDesc = "description";
		CompositeMap bmCm = data.getChild(bmDesc);
		if (bmCm != null) {
			String desc = bmCm.getText();
			if (desc != null) {
				bmDescSt.setText(desc);
			}
		}
		bmDescSt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				CompositeMap bmCm = data.getChild(bmDesc);
				if (bmCm == null) {
					bmCm = new CommentCompositeMap(data.getPrefix(), data
							.getNamespaceURI(), bmDesc);
					data.addChild(bmCm);
				}
				bmCm.setText(bmDescSt.getText());
				refresh(true);
			}
		});
		sashForm.addControlListener(new FixedSizeControlListener(700));
		// sashForm.setWeights(new int[] { 80, 20 });
	}

	private void registerTabFolder(int i, String tabFolerName) {
		tabFolerNameList.add(i, tabFolerName);
	}

	private void createDetailContent(Composite parent)
			throws ApplicationException {
		mTabFolder = createTabFolder(parent);
		mTabFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Element model_em = LoadSchemaManager.getSchemaManager().getElement(
				AuroraConstant.ModelQN);
		Iterator arrays = model_em.getAllArrays().iterator();
		for (int i = 0; arrays.hasNext(); i++) {
			Array array = (Array) arrays.next();
			CompositeMap array_data = data.getChild(array.getLocalName());

			if (array_data == null) {
				String name = array.getLocalName();
				array_data = new CommentCompositeMap(data.getPrefix(),
						data.getNamespaceURI(), name);
				array_data.setParent(data);
			}
			IType type = array.getElementType();
			if (!(type instanceof Element)) {
				childViews.add(createBaseViewer(i, array, array_data));
				registerTabFolder(i, array.getLocalName());
				continue;
			}
			Element arrayType = LoadSchemaManager.getSchemaManager()
					.getElement(type.getQName());
			if (arrayType.getAllElements().size() > 0) {
				childViews.add(createBaseViewer(i, array, array_data));
				registerTabFolder(i, array.getLocalName());
				continue;
			} else {
				final GridViewer gridViewer = new GridViewer(null,
						IGridViewer.fullEditable);
				gridViewer.setParent(this);
				gridViewer.createViewer(mTabFolder, array_data);

				for (int j = 0; j < customTabs.length; j++) {
					if (customTabs[j].equals(array.getLocalName())) {
						createCustomerActions(gridViewer);
					}
				}

				mTabFolder.getItem(i).setText(
						customTabTitle(array.getLocalName()));
				mTabFolder.getItem(i).setControl(gridViewer.getControl());
				childViews.add(gridViewer);
				registerTabFolder(i, array.getLocalName());
				final int itemIndex = i;
				mTabFolder.addSelectionListener(new SelectionListener() {

					public void widgetSelected(SelectionEvent e) {
						if (mTabFolder.getSelectionIndex() == itemIndex) {
							gridViewer.packColumns();
						}
					}

					public void widgetDefaultSelected(SelectionEvent e) {
						widgetSelected(e);
					}
				});
			}
		}
		mTabFolder.layout(true);

	}

	private BaseCompositeMapViewer createBaseViewer(int i, Array array,
			CompositeMap array_data) throws ApplicationException {
		BaseCompositeMapViewer baseViewer = new BaseCompositeMapViewer(this,
				array_data);
		baseViewer.createFormContent(mTabFolder);
		mTabFolder.getItem(i).setText(customTabTitle(array.getLocalName()));
		mTabFolder.getItem(i).setControl(baseViewer.getControl());
		return baseViewer;
	}

	public void createCustomerActions(GridViewer pae) {
		// Element element =
		// LoadSchemaManager.getSchemaManager().getElement(pae.getInput());
		Element element = CompositeMapUtil.getElement(pae.getInput());
		if (element == null) {
			return;
		}
		Action[] actions = new Action[3];
		if (element.isArray()) {
			Action addAction = null;
			if (ref_fields.equals(element.getLocalName())) {
				addAction = new AddRefFieldAction(pae, pae.getInput()
						.getParent(), ActionListener.DefaultImage);
			} else {
				addAction = new AddFieldAction(pae, data.getChild("fields"),
						pae.getInput());
			}
			actions[0] = addAction;
			actions[1] = new RefreshAction(pae, ActionListener.DefaultImage);
			actions[2] = new RemoveElementAction(pae,
					ActionListener.DefaultImage);
			pae.setActions(actions);
		}
	}

	private CTabFolder createTabFolder(final Composite parent) {
		final CTabFolder tabFolder = new CTabFolder(parent, SWT.NONE
				| SWT.BORDER);
		tabFolder.setMaximizeVisible(true);
		tabFolder.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseDoubleClick(MouseEvent e) {
				if (tabFolder.getMaximized()) {
					tabFolder.setMaximized(false);
					sashForm.setMaximizedControl(null);
					parent.layout(true);
				} else {
					tabFolder.setMaximized(true);
					sashForm.setMaximizedControl(tabFolder);
					parent.layout(true);
				}
			}
		});
		tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void minimize(CTabFolderEvent event) {
				tabFolder.setMinimized(true);
				tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
						false));
				parent.layout(true);
			}

			public void maximize(CTabFolderEvent event) {
				tabFolder.setMaximized(true);
				sashForm.setMaximizedControl(tabFolder);
				parent.layout(true);
			}

			public void restore(CTabFolderEvent event) {
				tabFolder.setMaximized(false);
				sashForm.setMaximizedControl(null);
				parent.layout(true);
			}
		});
		tabFolder.setSimple(false);
		tabFolder.setTabHeight(23);

		Element model_em = LoadSchemaManager.getSchemaManager().getElement(
				AuroraConstant.ModelQN);
		for (int i = 0; i < model_em.getAllArrays().size(); i++) {
			new CTabItem(tabFolder, SWT.None | SWT.MULTI | SWT.V_SCROLL);
		}
		return tabFolder;
	}

	public void doSave(IProgressMonitor monitor) {
		try {
			File file = getFile();
			// XMLOutputter.saveToFile(file, data);
			CommentXMLOutputter.saveToFile(file, data);
			super.doSave(monitor);
		} catch (IOException e) {
			DialogUtil.logErrorException(e);
		}
	}

	public void refresh(boolean dirty) {
		try {
			refreshChildViewData();
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return;
		}
		for (Iterator iterator = childViews.iterator(); iterator.hasNext();) {
			Object childViewer = iterator.next();
			if (childViewer instanceof IViewer) {
				IViewer iViewer = (IViewer) childViewer;
				iViewer.refresh(false);
			}
		}
		super.refresh(dirty);
	}

	private void refreshChildViewData() throws ApplicationException {
		if (tabFolerNameList == null || data == null || childViews == null)
			return;
		Iterator tabIt = tabFolerNameList.iterator();
		int index = 0;
		while (tabIt.hasNext()) {
			String tabFolderName = (String) tabIt.next();
			CompositeMap array_data = data.getChild(tabFolderName);
			if (array_data == null) {
				array_data = new CommentCompositeMap(data.getPrefix(),
						data.getNamespaceURI(), tabFolderName);
				array_data.setParent(data);
				Object childViewer = childViews.get(index + 1);
				if (childViewer == null)
					continue;
				if (childViewer instanceof BaseCompositeMapViewer) {
					BaseCompositeMapViewer iViewer = (BaseCompositeMapViewer) childViewer;
					iViewer.refresh(array_data);
				} else if (childViewer instanceof GridViewer) {
					GridViewer iViewer = (GridViewer) childViewer;
					iViewer.setData(array_data);
				}
			}
			index++;
		}
	}

	public CompositeMap getData() {
		return data;
	}

	public void setData(CompositeMap data) {
		this.data = data;
	}

	public void refreshFormContent(CompositeMap content) {
		this.data = content;
		try {
			createContent(shell);
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
		}
	}

	public CompositeMap getSelectionTab() {
		int SelectionIndex = mTabFolder.getSelectionIndex();
		if (SelectionIndex == -1)
			return null;
		Object nameObject = tabFolerNameList.get(SelectionIndex);
		if (nameObject == null)
			return null;
		String tabFolderName = (String) nameObject;
		CompositeMap tabFoler = data.getChild(tabFolderName);
		return tabFoler;
	}

	public void setSelectionTab(String tabName) {
		if (tabName == null)
			return;
		int tabIndex = tabFolerNameList.indexOf(tabName);
		if (tabIndex == -1 || tabIndex > mTabFolder.getItemCount())
			return;
		Object view = childViews.get(tabIndex + 1);
		if (view == null)
			return;
		if (view instanceof GridViewer) {
			((GridViewer) view).packColumns();
		}
		mTabFolder.setSelection(tabIndex);
	}

	private String customTabTitle(String tabName) {
		String tabHeighGrab = "      ";
		String tabTitle = tabName;
		Object customTtile = customerTitles.get(tabName);
		if (customTtile != null)
			tabTitle = (String) customTtile;
		return tabHeighGrab + tabTitle + tabHeighGrab;
	}

	public boolean isFormContendCreated() {
		if (sashForm != null)
			return true;
		return false;
	}

}
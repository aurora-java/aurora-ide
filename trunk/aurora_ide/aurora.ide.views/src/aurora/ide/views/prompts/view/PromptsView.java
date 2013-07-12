package aurora.ide.views.prompts.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.editor.editorInput.StringEditorInput;
import aurora.ide.freemarker.FreeMarkerGenerator;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.prompt.PromptManager;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.view.IPromptsViewer;
import aurora.ide.view.ViewNode;
import aurora.ide.views.dialog.ResourceSelector;
import aurora.ide.views.editor.PromptsEditor;
import aurora.ide.views.prompts.preference.PromptsRegisterSqlConfigration;
import aurora.ide.views.prompts.util.PromptsFinder;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class PromptsView extends ViewPart implements IPromptsViewer {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "aurora.ide.views.prompts.view.PromptsView";

	private TableViewer viewer;
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Table table;

	private Action selectALL;
	private Action deselectAll;
	private Action doubleClickAction;

	private IFile file;
	// file change listener
	// 提示 ：关闭未保存／保存 的编辑器
	// 换掉内容后打开。
	// 允许ctrl＋z撤销。
	private CompositeMap compositeMap;

	private ViewNode[] result;

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent instanceof ViewNode[]) {
				return (ViewNode[]) parent;
			}
			return null;
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			if (!(obj instanceof ViewNode)) {
				return "";
			}
			ViewNode vn = (ViewNode) obj;
			switch (index) {
			case 0: {
				return vn.getElementRawName();
			}
			case 1: {
				return vn.getNameAttribute();
			}
			case 2: {
				return vn.getZhsPrompt();
			}
			case 3: {
				return vn.getUsPrompt();
			}
			case 4: {
				return vn.getPromptsCode();
			}

			}

			return "";
		}

		public Image getColumnImage(Object obj, int index) {
			if (index != 0)
				return null;
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * The constructor.
	 */
	public PromptsView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		viewer = new TableViewer(container, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.CHECK);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());

		GridLayout gl = new GridLayout();
		container.setLayout(gl);

		toolkit.paintBordersFor(container);

		table = viewer.getTable();

		GridData gd = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(gd);
		toolkit.adapt(table);
		toolkit.paintBordersFor(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(100);
		tableColumn.setText("标签名");

		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(100);
		tableColumn_1.setText("属性名");

		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(150);
		tableColumn_2.setText("中文");

		TableColumn tableColumn_3 = new TableColumn(table, SWT.NONE);
		tableColumn_3.setWidth(150);
		tableColumn_3.setText("英文");

		TableColumn tblclmnPromptscode = new TableColumn(table, SWT.NONE);
		tblclmnPromptscode.setWidth(200);
		tblclmnPromptscode.setText("Prompts_Code");

		gd = new GridData(GridData.FILL_HORIZONTAL);
		Composite c = toolkit.createComposite(container, SWT.NONE);
		gl = new GridLayout();
		c.setLayoutData(gd);
		c.setLayout(gl);
		gl.numColumns = 4;
		toolkit.adapt(c);

		Button dbScript = new Button(c, SWT.NONE);
		dbScript.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> promptsCode = new ArrayList<String>();

				if (result == null || compositeMap == null)
					return;
				// StringBuilder sb = new StringBuilder();
				String result = "";
				List<ViewNode> sNodes = getSelectedViewNodes();
				List<TemplateModel> prompts = new ArrayList<TemplateModel>();
				for (ViewNode n : sNodes) {
					String code = n.getPromptsCode();
					if (promptsCode.contains(code)) {
						continue;
					} else {
						promptsCode.add(code);
					}

					Map root = new HashMap();
					try {
						DefaultObjectWrapper dow = new DefaultObjectWrapper();
						Map prompt = new HashMap();
						prompt.put("code", code);
						prompt.put("zhs", n.getZhsPrompt());
						prompt.put("us", n.getUsPrompt());
						prompts.add(dow.wrap(prompt));
						root.put("prompts", dow.wrap(prompts));

					} catch (TemplateModelException e1) {
						DialogUtil.logErrorException(e1);
					}
					try {
						PromptsRegisterSqlConfigration config = new PromptsRegisterSqlConfigration();
						Template template;
						template = config.getTemplate();
						FreeMarkerGenerator fg = new FreeMarkerGenerator();
						result = fg.gen(template, root);
					} catch (IOException e1) {
						DialogUtil.logErrorException(e1);
						e1.printStackTrace();
					} catch (SAXException e1) {
						DialogUtil.logErrorException(e1);
						e1.printStackTrace();
					} catch (TemplateException e1) {
						DialogUtil.logErrorException(e1);
						e1.printStackTrace();
					}

					// String d = Script.DeletePromptCode.replace(
					// Script.PROMPT_CODE, code);
					// String i = Script.InsertZHSPrompt.replace(
					// Script.PROMPT_CODE, code);
					// i = i.replace(Script.TEXT, n.getZhsPrompt());
					// sb.append(d);
					// sb.append("\n");
					// sb.append(i);
					// sb.append("\n");
					// sb.append("\n");
				}

				try {
					IDE.openEditor(getSite().getPage(), new StringEditorInput(
							result, "utf-8"),
							"org.eclipse.ui.DefaultTextEditor");
				} catch (PartInitException e1) {
					e1.printStackTrace();
				}
			}
		});
		toolkit.adapt(dbScript, true, true);
		dbScript.setText("中英文脚本");

		Button newFile = new Button(c, SWT.NONE);
		toolkit.adapt(newFile, true, true);
		newFile.setText("新文件内容");
		newFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (result == null || compositeMap == null)
					return;
				List<ViewNode> sNodes = getSelectedViewNodes();
				for (ViewNode n : sNodes) {
					n.applyCode();
				}
				try {
					IDE.openEditor(getSite().getPage(), new StringEditorInput(
							compositeMap.toXML(), "utf-8"),
							"org.eclipse.ui.DefaultTextEditor");
				} catch (PartInitException e1) {
					e1.printStackTrace();
				}
			}
		});

		Button openButton = new Button(c, SWT.NONE);
		toolkit.adapt(openButton, true, true);
		openButton.setText("打开原文件");
		openButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if (file == null || !file.exists())
						return;
					IDE.openEditor(getSite().getPage(), file);
				} catch (PartInitException e1) {
					e1.printStackTrace();
				}
			}
		});

		Button linkFile = new Button(c, SWT.NONE);
		toolkit.adapt(linkFile, true, true);
		linkFile.setText("连接文件");
		linkFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				linkFile();
			}
		});
		makeActions();
		contributeToActionBars();
	}

	protected List<ViewNode> getSelectedViewNodes() {
		List<ViewNode> nodes = new ArrayList<ViewNode>();
		TableItem[] items = table.getItems();
		if (items != null) {
			for (TableItem i : items) {
				if (i.getChecked()) {
					nodes.add((ViewNode) i.getData());
				}
			}
		}

		return nodes;

	}

	public IFile openResourceSelector(Shell shell, String[] exts) {
		ResourceSelector fss = new ResourceSelector(shell);
		IResource res = ResourcesPlugin.getWorkspace().getRoot();
		fss.setExtFilter(exts);
		fss.setInput((IContainer) res);
		Object obj = fss.getSelection();
		if (!(obj instanceof IFile)) {
			return null;
		}
		return (IFile) obj;
	}

	public void linkFile(IFile file) {
		if (file == null)
			return;
		try {
			this.file = file;
			compositeMap = (CompositeMap) CacheManager.getCompositeMap(file)
					.clone();
		} catch (CoreException e) {
			// 加载失败
			e.printStackTrace();
		} catch (ApplicationException e) {
			// 加载失败
			e.printStackTrace();
		}
		if (compositeMap == null)
			return;

		table.removeAll();

		String promptPrefix = CompositeMapUtil.getValueIgnoreCase(compositeMap,
				"promptprefix");

		promptPrefix = promptPrefix == null ? "" : promptPrefix;

		PromptsFinder pf = new PromptsFinder(promptPrefix);

		compositeMap.iterate(pf, true);
		result = pf.getResult();

		synWithDB(result, file);

		this.viewer.setInput(result);

		updateCellEditor();

		if (selectALL != null)
			selectALL.run();
	}

	private void synWithDB(ViewNode[] result, IFile file) {
		CompositeMap existPrompts = getExistPrompts(result,file);
		CompositeMap columnComments = getColumnComments(file);
		for (ViewNode n : result) {
			String c = n.getPromptAttribute();
			boolean promptsCode = PromptManager.isPromptsCode(c);
			if(promptsCode){
				n.setPromptsCode(c);
				CompositeMap code = existPrompts.getChildByAttrib("prompt_code", c);
				String zhs="";
				String us="";
				if(code!=null){
					 zhs = code.getString("ZHS", "");
					 us = code.getString("US", "");
				}
				if("".equals(zhs)){
					String name = n.getCompositeMap().getString("name", "");
					if("".equals(name) ==false&&columnComments!=null){
						CompositeMap cn = columnComments.getChildByAttrib("column_name", name.toUpperCase());	
						if(cn!=null){
							zhs = cn.getString("comments", "");
						}
					}
				}
				if("".equals(zhs)==false){n.setZhsPrompt(zhs);}
				if("".equals(us) == false){ n.setUsPrompt(us);}
			}
		}
		
		// result[0].getPromptAttribute()
	}
	
	private CompositeMap getExistPrompts(ViewNode[] result ,IFile file){
		List<String> codes = new ArrayList<String>();
		for (ViewNode n : result) {
			String c = n.getPromptAttribute();
			boolean promptsCode = PromptManager.isPromptsCode(c);
			if(promptsCode){
				codes.add(c);
			}
		}
		CompositeMap prompts = PromptManager.getPrompts(codes, file);
		return prompts;
	}

	private CompositeMap getColumnComments(IFile file){
		CompositeMap columnComments = null;
		try {
			CompositeMap cmap = CacheManager.getWholeBMCompositeMap(file);
			String baseTable = CompositeMapUtil.getValueIgnoreCase(cmap,
					"baseTable");
			if (baseTable != null && "".equals(baseTable)==false) {
				columnComments = PromptManager.getColumnComments(
						baseTable, file);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return columnComments;
	}
	
	protected void linkFile() {
		String[] as = { "screen", "bm" };
		file = openResourceSelector(this.getSite().getShell(), as);
		if (file == null)
			return;
		this.linkFile(file);
	}

	private void updateCellEditor() {
		TableItem[] items = table.getItems();
		for (TableItem item : items) {
			createCHEditor(item);
			createUSEditor(item);
			createCodesEditor(item);
		}
	}
	private void createUSEditor(TableItem item) {
		final PromptsEditor pe = new PromptsEditor(table, item);
		pe.createEditor(3);
		final ViewNode viewNode = (ViewNode) item.getData();
		pe.setText(viewNode.getUsPrompt());
		pe.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				viewNode.setUsPrompt(pe.getText());
			}
		});
	}
	
	private void createCHEditor(TableItem item) {
		final PromptsEditor pe = new PromptsEditor(table, item);
		pe.createEditor(2);
		final ViewNode viewNode = (ViewNode) item.getData();
		pe.setText(viewNode.getZhsPrompt());
		pe.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				viewNode.setZhsPrompt(pe.getText());
			}
		});
	}

	private void createCodesEditor(TableItem item) {
		final PromptsEditor pe = new PromptsEditor(table, item);
		pe.createEditor(4);
		final ViewNode viewNode = (ViewNode) item.getData();
		pe.setText(viewNode.getPromptsCode());
		pe.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				viewNode.setPromptsCode(pe.getText());
			}
		});
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				PromptsView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		// fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(selectALL);
		manager.add(new Separator());
		manager.add(deselectAll);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(selectALL);
		manager.add(deselectAll);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(selectALL);
		manager.add(deselectAll);
	}

	private void makeActions() {
		selectALL = new Action() {
			public void run() {
				TableItem[] items = table.getItems();
				if (items == null)
					return;
				for (TableItem i : items) {
					i.setChecked(true);
				}
			}
		};
		selectALL.setText("全选");
		selectALL.setToolTipText("全选");
		selectALL.setImageDescriptor(AuroraPlugin
				.getImageDescriptor(LocaleMessage.getString("checked.icon"))
		// PlatformUI.getWorkbench()
		// .getSharedImages()
		// .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK)
				);

		deselectAll = new Action() {
			public void run() {
				TableItem[] items = table.getItems();
				if (items == null)
					return;
				for (TableItem i : items) {
					i.setChecked(false);
				}
			}
		};
		deselectAll.setText("取消全选");
		deselectAll.setToolTipText("取消全选");
		deselectAll.setImageDescriptor(AuroraPlugin
				.getImageDescriptor(LocaleMessage.getString("unchecked.icon"))
		// PlatformUI.getWorkbench()
		// .getSharedImages()
		// .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK)
				);
		// doubleClickAction = new Action() {
		// public void run() {
		// ISelection selection = viewer.getSelection();
		// Object obj = ((IStructuredSelection) selection)
		// .getFirstElement();
		// showMessage("Double-click detected on " + obj.toString());
		// }
		// };
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Prompt View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
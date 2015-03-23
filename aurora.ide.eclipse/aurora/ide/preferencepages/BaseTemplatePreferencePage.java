package aurora.ide.preferencepages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.freemarker.FreeMarkerGenerator;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.FileCopyer;
import aurora.ide.helpers.FileDeleter;
import aurora.ide.helpers.StatusUtil;

public abstract class BaseTemplatePreferencePage extends PreferencePage
		implements IWorkbenchPreferencePage {
	public static String config_file_name = "config.xml";

	/** The table presenting the templates. */
	private CheckboxTableViewer fTableViewer;

	/* buttons */
	private Button fAddButton;
	private Button fEditButton;
	private Button fExportButton;
	private Button fRemoveButton;
	private Button fRestoreButton;
	private Button fRevertButton;

	/** The viewer displays the pattern of selected template. */
	private SourceViewer fPatternViewer;
	private File templateDir;
	private Config config;

	/**
	 * Creates a new template preference page.
	 */
	public BaseTemplatePreferencePage() {
		super();
	}

	/*
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		templateDir = getTemplateDir(getTemplateDirName());
		if (!templateDir.exists()) {
			reset(templateDir);
		}
		initConfig();
	}

	static File getTemplateDir(String dirName) {
		IPath path = AuroraPlugin.getDefault().getStateLocation()
				.append("templates").append(dirName);
		return new File(path.toString());
	}

	static File getConfigFile(File dir) {
		return new File(dir, config_file_name);
	}

	static Config loadConfig(File cfgFile) throws IOException, SAXException {
		CompositeMap map = new CompositeLoader().loadByFullFilePath(cfgFile
				.getAbsolutePath());
		Config cfg = new Config(map);
		return cfg;
	}

	public static InputStream getTemplateContent(String dirname)
			throws IOException, SAXException {
		File tplFile = getTemplateFile(dirname);
		if (tplFile != null)
			return new FileInputStream(tplFile);
		return null;
	}

	public static File getTemplateFile(String dirname) throws IOException,
			SAXException {
		File dir = getTemplateDir(dirname);
		if (!dir.exists())
			reset(dir);
		File cfgFile = getConfigFile(dir);
		Config cfg = loadConfig(cfgFile);
		String def = cfg.map.getString("default");
		CompositeMap root = cfg.getTemplatesRoot();
		@SuppressWarnings("unchecked")
		List<CompositeMap> list = root.getChildsNotNull();
		String fn = null;
		if (def != null)
			for (CompositeMap m : list) {
				if (def.equals(m.getString("name"))) {
					fn = m.getString("file");
					break;
				}
			}
		if (fn != null) {
			return new File(dir, fn);
		}
		return null;
	}

	protected abstract String getTemplateDirName();

	private void initConfig() {
		File configFile = getConfigFile(templateDir);
		try {
			config = loadConfig(configFile);
		} catch (Exception e) {
			config = new Config(new CompositeMap());
			StatusUtil.showExceptionDialog(null, "Error", "Failed to load "
					+ config_file_name, false, e);
			return;
		}
		for (Template tpl : config.getTemplates()) {
			File f = new File(templateDir, tpl.getFile());
			tpl.templateStr = getFileContent(f);// not affect dirty flag
		}
	}

	protected abstract Map<?, ?> getSimpleModel();

	private String getFileContent(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] bs = new byte[(int) (file.length() + 10)];
			int len = fis.read(bs);
			fis.close();
			return new String(bs, 0, len, AuroraConstant.ENCODING);
		} catch (Exception e) {
			return "";
		}
	}

	private void setFileContent(File f, String content) {
		if (content == null)
			return;
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(content.getBytes(AuroraConstant.ENCODING));
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void reset(File templateDir) {
		URL ts = FileLocator.find(Platform.getBundle(AuroraPlugin.PLUGIN_ID),
				new Path("templates/" + templateDir.getName()), null); //$NON-NLS-1$
		try {
			ts = FileLocator.toFileURL(ts);
			File initTplDir = new Path(ts.getPath()).toFile();
			FileCopyer.copyDirectory(initTplDir, templateDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite ancestor) {
		Composite parent = new Composite(ancestor, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);

		Composite innerParent = new Composite(parent, SWT.NONE);
		GridLayout innerLayout = new GridLayout();
		innerLayout.numColumns = 2;
		innerLayout.marginHeight = 0;
		innerLayout.marginWidth = 0;
		innerParent.setLayout(innerLayout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		innerParent.setLayoutData(gd);

		Composite tableComposite = new Composite(innerParent, SWT.NONE);
		tableComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 360;
		data.heightHint = convertHeightInCharsToPixels(10);
		tableComposite.setLayoutData(data);
		Table table = new Table(tableComposite, SWT.CHECK | SWT.BORDER
				| SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		GC gc = new GC(getShell());
		gc.setFont(JFaceResources.getDialogFont());

		TableColumn column1 = new TableColumn(table, SWT.LEFT);
		column1.setWidth(128);
		column1.setText("Name");
		// int minWidth= computeMinimumColumnWidth(gc,
		// TemplatesMessages.TemplatePreferencePage_column_name);
		// columnLayout.addColumnData(new ColumnWeightData(2, minWidth, true));

		TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setWidth(193);
		column2.setText("Description");
		// minWidth= computeMinimumContextColumnWidth(gc);
		// columnLayout.addColumnData(new ColumnWeightData(1, minWidth, true));

		gc.dispose();

		fTableViewer = new CheckboxTableViewer(table);
		// fTableViewer.setLabelProvider(new TemplateLabelProvider());
		// fTableViewer.setContentProvider(new TemplateContentProvider());

		// fTableViewer.setComparator(new ViewerComparator() {
		// public int compare(Viewer viewer, Object object1, Object object2) {
		// if ((object1 instanceof TemplatePersistenceData)
		// && (object2 instanceof TemplatePersistenceData)) {
		// Template left = ((TemplatePersistenceData) object1)
		// .getTemplate();
		// Template right = ((TemplatePersistenceData) object2)
		// .getTemplate();
		// int result = Collator.getInstance().compare(left.getName(),
		// right.getName());
		// if (result != 0)
		// return result;
		// return Collator.getInstance().compare(
		// left.getDescription(), right.getDescription());
		// }
		// return super.compare(viewer, object1, object2);
		// }
		//
		// public boolean isSorterProperty(Object element, String property) {
		// return true;
		// }
		// });
		fTableViewer.setContentProvider(new ConfigContentProvider());
		fTableViewer.setLabelProvider(new TemplateLabelProvider());
		fTableViewer.setInput(config);
		selectDefault();

		fTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				// edit();
			}
		});

		fTableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent e) {
						selectionChanged1();
					}
				});

		fTableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object data = event.getElement();
				Object[] objs = fTableViewer.getCheckedElements();
				for (Object o : objs) {
					if (o != data)
						fTableViewer.setChecked(o, false);
				}
				objs = fTableViewer.getCheckedElements();
				if (objs.length == 1) {
					config.setDefault((Template) objs[0]);
				}
			}
		});

		Composite buttons = new Composite(innerParent, SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);

		fAddButton = new Button(buttons, SWT.PUSH);
		fAddButton.setText("New...");
		fAddButton.setLayoutData(getButtonGridData(fAddButton));
		fAddButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				add();
			}
		});

		fEditButton = new Button(buttons, SWT.PUSH);
		fEditButton.setText("Edit...");
		fEditButton.setEnabled(false);
		fEditButton.setLayoutData(getButtonGridData(fEditButton));
		fEditButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				edit();
			}
		});

		fRemoveButton = new Button(buttons, SWT.PUSH);
		fRemoveButton.setText("Remove");
		fRemoveButton.setEnabled(false);
		fRemoveButton.setLayoutData(getButtonGridData(fRemoveButton));
		fRemoveButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				remove();
			}
		});

		createSeparator(buttons);

		fPatternViewer = doCreateViewer(parent);

		// fTableViewer.setInput(fTemplateStore);
		// fTableViewer.setAllChecked(false);
		// fTableViewer.setCheckedElements(getEnabledTemplates());

		// updateButtons();
		Dialog.applyDialogFont(parent);
		new Label(parent, SWT.NONE);
		innerParent.layout();

		return parent;
	}

	/**
	 * Creates a separator between buttons.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return a separator
	 */
	private Label createSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.NONE);
		separator.setVisible(false);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.BEGINNING;
		gd.heightHint = 4;
		separator.setLayoutData(gd);
		return separator;
	}

	/**
	 * Returns whether the formatter preference checkbox should be shown.
	 * 
	 * @return <code>true</code> if the formatter preference checkbox should be
	 *         shown, <code>false</code> otherwise
	 */
	protected boolean isShowFormatterSetting() {
		return true;
	}

	private SourceViewer doCreateViewer(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Preview:");
		GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		SourceViewer viewer = createViewer(parent);
		Cursor arrowCursor = viewer.getTextWidget().getDisplay()
				.getSystemCursor(SWT.CURSOR_ARROW);
		viewer.getTextWidget().setCursor(arrowCursor);

		// Don't set caret to 'null' as this causes
		// https://bugs.eclipse.org/293263
		// viewer.getTextWidget().setCaret(null);

		Control control = viewer.getControl();
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		data.heightHint = convertHeightInCharsToPixels(5);
		control.setLayoutData(data);

		return viewer;
	}

	/**
	 * Creates, configures and returns a source viewer to present the template
	 * pattern on the preference page. Clients may override to provide a custom
	 * source viewer featuring e.g. syntax coloring.
	 * 
	 * @param parent
	 *            the parent control
	 * @return a configured source viewer
	 */
	protected SourceViewer createViewer(Composite parent) {
		SourceViewer viewer = new SourceViewer(parent, null, null, false,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		StyledText styledText = viewer.getTextWidget();
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		viewer.setEditable(false);
		SourceViewerConfiguration configuration = new SourceViewerConfiguration();
		viewer.configure(configuration);
		IDocument document = new Document();
		viewer.setDocument(document);
		return viewer;
	}

	/**
	 * Return the grid data for the button.
	 * 
	 * @param button
	 *            the button
	 * @return the grid data
	 */
	private static GridData getButtonGridData(Button button) {
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		// TODO replace SWTUtil
		// data.widthHint= SWTUtil.getButtonWidthHint(button);
		// data.heightHint= SWTUtil.getButtonHeightHint(button);

		return data;
	}

	private void selectionChanged1() {
		updateViewerInput();
		updateButtons();
	}

	/**
	 * Updates the pattern viewer.
	 */
	protected void updateViewerInput() {
		IStructuredSelection selection = (IStructuredSelection) fTableViewer
				.getSelection();

		if (selection.size() == 1) {
			Template data = (Template) selection.getFirstElement();
			String template = data.getTemplateString();
			fPatternViewer.getDocument().set(template);
		} else {
			fPatternViewer.getDocument().set(""); //$NON-NLS-1$
		}
	}

	/**
	 * Updates the buttons.
	 */
	protected void updateButtons() {
		IStructuredSelection selection = (IStructuredSelection) fTableViewer
				.getSelection();
		int selectionCount = selection.size();
		int itemCount = fTableViewer.getTable().getItemCount();
		// boolean canRestore = fTemplateStore.getTemplateData(true).length !=
		// fTemplateStore
		// .getTemplateData(false).length;
		// boolean canRevert = false;
		// for (Iterator<?> it = selection.iterator(); it.hasNext();) {
		// TemplatePersistenceData data = (TemplatePersistenceData) it.next();
		// if (data.isModified()) {
		// canRevert = true;
		// break;
		// }
		// }

		fEditButton.setEnabled(selectionCount == 1);
		fRemoveButton.setEnabled(selectionCount > 0
				&& selectionCount <= itemCount);
		// fRestoreButton.setEnabled(canRestore);
		// fRevertButton.setEnabled(canRevert);
	}

	boolean checkValid() {
		if (config == null || config.map == null
				|| config.map.getName() == null) {
			DialogUtil.showErrorMessageBox("当前配置对象无效");
			return false;
		}

		return true;
	}

	private void add() {
		if (!checkValid())
			return;
		EditTemplateDialog ctd = new EditTemplateDialog(getShell());
		ctd.setPreferencePage(this);
		if (ctd.open() == IDialogConstants.OK_ID) {
			Template tpl = ctd.getTemplate();
			config.add(tpl);
			fTableViewer.refresh();
		}

	}

	private void edit() {
		if (!checkValid())
			return;
		EditTemplateDialog ctd = new EditTemplateDialog(getShell(),
				getCurrentTemplate());
		ctd.setPreferencePage(this);
		if (ctd.open() == IDialogConstants.OK_ID) {
			Template tpl = ctd.getTemplate();
			fTableViewer.refresh(tpl);
			updateViewerInput();
		}
	}

	private void remove() {
		if (!checkValid())
			return;
		IStructuredSelection selection = (IStructuredSelection) fTableViewer
				.getSelection();
		Object[] objs = selection.toArray();
		for (Object o : objs) {
			Template t = (Template) o;
			if (t.isNew)
				config.remove(t);
			else
				t.markDelete = true;
		}
		fTableViewer.refresh();
	}

	private Template getCurrentTemplate() {
		IStructuredSelection selection = (IStructuredSelection) fTableViewer
				.getSelection();
		return (Template) selection.getFirstElement();
	}

	/*
	 * @see Control#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}

	/*
	 * @see PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		
		int showConfirmDialogBox = DialogUtil.showConfirmDialogBox("default", "是否重置？");
		
		if(showConfirmDialogBox == Dialog.OK){
			FileDeleter.deleteDirectory(templateDir);
			reset(templateDir);
			initConfig();
			fTableViewer.setInput(config);
			fTableViewer.refresh();
			selectDefault();
			// fTableViewer.setAllChecked(false);
			// fTableViewer.setCheckedElements(getEnabledTemplates());
		}
		
	}

	private void selectDefault() {
		Template t = config.getDefaultTemplate();
		if (t != null)
			fTableViewer.setChecked(t, true);
	}

	/*
	 * @see PreferencePage#performOk()
	 */
	public boolean performOk() {
		if (!checkValid())
			return false;
		// FIXME performOk
		boolean has_change = false;
		// 1st,delete template that should be delete
		for (int i = 0; i < config.list.size(); i++) {
			Template tpl = config.list.get(i);
			if (!tpl.isNew && tpl.markDelete) {
				debug("delete:" + tpl.getName());
				has_change = true;
				File f = new File(templateDir, tpl.getFile());
				f.delete();
				config.list.remove(i--);
				config.getTemplatesRoot().removeChild(tpl.map);
			}
		}
		// 2nd,add or update
		for (Template tpl : config.list) {
			if (tpl.isNew) {
				debug("create:" + tpl.getName());
				has_change = true;
				tpl.setFile(getNewFileName(tpl) + ".tpl");
				File f = new File(templateDir, tpl.getFile());
				setFileContent(f, tpl.getTemplateString());
				config.getTemplatesRoot().addChild(tpl.map);
			} else if (tpl.dirty) {
				debug("update:" + tpl.getName());
				has_change = true;
				File f = new File(templateDir, tpl.getFile());
				setFileContent(f, tpl.getTemplateString());
			}
		}
		if (has_change || config.dirty) {
			debug("update " + config_file_name);
			File f = new File(templateDir, config_file_name);
			String xml = AuroraResourceUtil.xml_decl + config.map.toXML();
			setFileContent(f, xml);
		}
		config.dirty = false;
		for (Template t : config.list) {
			t.resetFlag();
		}
		return super.performOk();
	}

	@SuppressWarnings("unused")
	void debug(Object obj) {
		if (false) {
			System.out.println(obj);
		}
	}

	protected String getNewFileName(Template tpl) {
		// return UUID.randomUUID().toString();
		return tpl.getName();
	}

	/**
	 * return null means no error,else return error message
	 * 
	 * @param conent
	 * @return
	 */
	protected String validateTemplate(String content) {
		Map<?, ?> model = getSimpleModel();
		if (model == null)
			return null;
		try {
			@SuppressWarnings("deprecation")
			freemarker.template.Template template = new freemarker.template.Template(
					"test", new StringReader(content));
			FreeMarkerGenerator fg = new FreeMarkerGenerator();
			fg.gen(template, model);
		} catch (Exception e) {
			return e.getMessage();
		}
		return null;
	}

	Config getConfig() {
		return config;
	}

	/*
	 * @see PreferencePage#performCancel()
	 */
	public boolean performCancel() {

		return super.performCancel();
	}

	protected SourceViewer getViewer() {
		return fPatternViewer;
	}

	protected TableViewer getTableViewer() {
		return fTableViewer;
	}

	static class Config {
		CompositeMap map;
		ArrayList<Template> list = new ArrayList<Template>();
		boolean dirty = false;

		Config(CompositeMap configMap) {
			this.map = configMap;
			CompositeMap tplRoot = getTemplatesRoot();
			if (tplRoot != null) {
				@SuppressWarnings("unchecked")
				List<CompositeMap> childs = tplRoot.getChildsNotNull();
				for (CompositeMap m : childs) {
					list.add(new Template(m));
				}
			}
		}

		Template getDefaultTemplate() {
			String def = map.getString("default");
			for (Template tpl : getTemplates()) {
				if (def.equalsIgnoreCase(tpl.getName()))
					return tpl;
			}
			return null;
		}

		void setDefault(Template tpl) {
			if (!tpl.eq(map.getString("default"), tpl.getName())) {
				dirty = true;
				map.put("default", tpl.getName());
			}
		}

		Template[] getTemplates() {
			ArrayList<Template> tmpList = new ArrayList<Template>();
			for (Template tpl : list) {
				if (!tpl.markDelete)
					tmpList.add(tpl);
			}
			Template[] ts = new Template[tmpList.size()];
			return tmpList.toArray(ts);
		}

		void add(Template tpl) {
			list.add(tpl);
		}

		void remove(Template tpl) {
			// if(tpl.isNew)
			list.remove(tpl);
		}

		CompositeMap getTemplatesRoot() {
			return map.getChild("templates");
		}
	}

	static class Template {
		CompositeMap map;
		boolean isNew = false;
		boolean dirty = false;
		boolean markDelete = false;
		String templateStr = "";

		Template(CompositeMap map) {
			this.map = map;
		}

		void resetFlag() {
			isNew = false;
			dirty = false;
			markDelete = false;
		}

		String getName() {
			return map.getString("name");
		}

		void setName(String name) {
			if (!eq(getName(), name)) {
				dirty = true;
				map.put("name", name);
			}
		}

		String getFile() {
			return map.getString("file");
		}

		void setFile(String string) {
			map.put("file", string);
		}

		String getDescription() {
			return map.getString("description");
		}

		void setDescription(String desc) {
			if (!eq(getDescription(), desc)) {
				dirty = true;
				map.put("description", desc);
			}
		}

		String getTemplateString() {
			return templateStr;
		}

		void setTemplateString(String tpl) {
			if (!eq(getTemplateString(), tpl)) {
				dirty = true;
				templateStr = tpl;
			}
		}

		boolean eq(Object o1, Object o2) {
			if (o1 == null)
				return o2 == null;
			return o1.equals(o2);
		}
	}

	class ConfigContentProvider implements IStructuredContentProvider {

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Config)
				return ((Config) inputElement).getTemplates();
			return null;
		}

	}

	class TemplateLabelProvider extends BaseLabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Template) {
				Template tpl = (Template) element;
				if (columnIndex == 0)
					return tpl.getName();
				if (columnIndex == 1)
					return tpl.getDescription();
			}
			return null;
		}
	}
}
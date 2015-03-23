package aurora.ide.bm.editor;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import uncertain.composite.CaseInsensitiveMap;
import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.event.Configuration;
import uncertain.logging.LoggerProvider;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import aurora.bm.IModelFactory;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.bm.AuroraDataBase;
import aurora.ide.bm.editor.toolbar.action.ExecuteSqlAction;
import aurora.ide.editor.core.ISqlViewer;
import aurora.ide.editor.textpage.ColorManager;
import aurora.ide.editor.textpage.SQLConfiguration;
import aurora.ide.editor.textpage.format.SQLFormat;
import aurora.ide.editor.widgets.GridViewer;
import aurora.ide.fake.uncertain.engine.FakeUncertainEngine;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DBConnectionUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ExceptionUtil;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.helpers.SystemException;
import aurora.ide.search.cache.CacheManager;

public class SQLExecutePage extends FormPage implements ISqlViewer {
	private static final String PageId = "SQLExecutePage";
	private static final String PageTitle = LocaleMessage
			.getString("auto.sql.test");
	private CTabFolder tabFolder;
	private SashForm sashForm;

	private Connection connection;

	private ToolBarManager toolBarManager;
	private static final String[] tabs = { "query", "insert", "update",
			"delete" };

	private FakeUncertainEngine uncertainEngine;
	private BusinessModelService modelService;
	private ViewForm viewForm;
	private GridViewer tableViewer;
	private boolean modify = false;

	public SQLExecutePage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}

	public SQLExecutePage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	public SQLExecutePage(String id, String title) {
		super(id, title);
	}

	public void addActions(IAction[] actions) {
		if (actions == null)
			return;
		for (int i = 0; i < actions.length; i++) {
			toolBarManager.add(createActionContributionItem(actions[i]));
		}
		toolBarManager.update(true);
	}

	public Connection getConnection() {
		return connection;
	}

	public String getSql() {
		if (tabFolder.getSelection() != null) {
			StyledText st = (StyledText) tabFolder.getSelection().getControl();
			return st.getText();
		}
		return null;
	}

	public boolean isModify() {
		return modify;
	}

	public void refresh(ResultSet resultSet, int resultCount) {
		if (resultSet != null) {
			try {
				creatTableViewer(sashForm, resultSet);
				sashForm.layout();
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
				return;
			}
		} else if (resultCount != 0) {
			if (tableViewer != null && tableViewer.getControl() != null) {
				tableViewer.getControl().setVisible(false);
				sashForm.layout();
			}
			String sql = getSql().split(" ")[0];
			int message = DialogUtil.showConfirmDialogBox(LocaleMessage
					.getString("are.you.sure.want.to")
					+ sql
					+ " "
					+ resultCount + LocaleMessage.getString("records") + "?");
			try {
				if (message == SWT.OK) {
					connection.commit();
				} else {
					connection.rollback();
				}
			} catch (SQLException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
		}
	}

	public void refresh(String content) throws ApplicationException {
		if (uncertainEngine == null || !isModify()) {
			return;
		}
		modelService = makeBusinessModelService(uncertainEngine, connection,
				content);
		for (int i = 0; i < tabs.length; i++) {
			StyledText st = (StyledText) tabFolder.getItem(i).getControl();
			try {
				SQLFormat sf = new SQLFormat();
				st.setText(sf.format(modelService.getSql(tabs[i]).toString()));
			} catch (Throwable e) {
				st.setText(ExceptionUtil.getExceptionTraceMessage(e));
			}
		}

		if (tableViewer != null && tableViewer.getControl() != null) {
			tableViewer.getControl().setVisible(false);
			sashForm.layout();
		}
		setModify(false);
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

	public void setModify(boolean modify) {
		this.modify = modify;
	}

	protected void createContent(Composite shell, FormToolkit toolkit)
			throws ApplicationException {
		viewForm = new ViewForm(shell, SWT.NONE);
		viewForm.setLayout(new FillLayout());
		createToolbar(viewForm);

		sashForm = new SashForm(viewForm, SWT.VERTICAL);
		createSqlContent(sashForm);
		createResultContent(sashForm);
		sashForm.setWeights(new int[] { 30, 70 });
		viewForm.setContent(sashForm);
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite shell = form.getBody();
		FillLayout layout = new FillLayout();
		shell.setLayout(layout);
		try {
			initConnection();
			createContent(shell, toolkit);
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
		}

	}

	protected void createResultContent(Composite parent)
			throws ApplicationException {
		tableViewer = new GridViewer();
		tableViewer.createViewer(parent);
		tableViewer.getControl().setVisible(false);
	}

	protected File getFile() {
		IFile ifile = ((IFileEditorInput) getEditor().getEditorInput())
				.getFile();
		String fileName = AuroraResourceUtil.getIfileLocalPath(ifile);
		return new File(fileName);
	}

	protected void initConnection() throws ApplicationException {
		IFile file = ((IFileEditorInput) getEditor().getEditorInput())
				.getFile();
		IProject project = file.getProject();
		AuroraDataBase ad = new AuroraDataBase(project);
		// dataSourceName 对应的数据源的名称
		String datasourceName = null;
		try {
			CompositeMap cMap = CacheManager.getWholeBMCompositeMap(file);
			datasourceName = CompositeMapUtil.getValueIgnoreCase(cMap,
					"dataSourceName");
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		connection = ad.getDBConnection(datasourceName);
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new SystemException(e);
		}
		uncertainEngine = DBConnectionUtil.getFakeUncertainEngine(project);
		if (!uncertainEngine.isRunning()) {
			uncertainEngine.startup();
		}
		String content;
		try {

			// content =
			// XMLOutputter.defaultInstance().toXML(AuroraResourceUtil.getCompsiteLoader().loadByFullFilePath(getFile().getAbsolutePath()),
			// true);
			content = CommentXMLOutputter.defaultInstance().toXML(
					AuroraResourceUtil.getCompsiteLoader().loadByFullFilePath(
							getFile().getAbsolutePath()), true);

		} catch (Throwable e) {
			throw new SystemException(e);
		}
		modelService = makeBusinessModelService(uncertainEngine, connection,
				content);
	}

	private ActionContributionItem createActionContributionItem(IAction action) {
		ActionContributionItem aci = new ActionContributionItem(action);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		return aci;
	}

	private String[] createColumnProperties(ResultSet resultSet)
			throws SystemException {
		ResultSetMetaData resultSetMetaData;
		String[] column_index = null;
		try {
			resultSetMetaData = resultSet.getMetaData();

			column_index = new String[resultSetMetaData.getColumnCount()];
			for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
				column_index[i - 1] = resultSetMetaData.getColumnName(i);
			}
		} catch (SQLException e) {
			throw new SystemException(e);
		}
		return column_index;
	}

	private BusinessModelServiceContext createContext(
			FakeUncertainEngine uncertainEngine, Connection connection) {
		Configuration rootConfig = uncertainEngine.createConfig();
		rootConfig.addParticipant(this);
		CompositeMap context = new CommentCompositeMap("root");
		BusinessModelServiceContext bc = (BusinessModelServiceContext) DynamicObject
				.cast(context, BusinessModelServiceContext.class);
		bc.setConfig(rootConfig);
		bc.setConnection(connection);
		LoggerProvider lp = LoggerProvider.createInstance(Level.FINE,
				System.out);
		LoggingContext.setLoggerProvider(context, lp);
		SqlServiceContext sc = SqlServiceContext
				.createSqlServiceContext(context);
		sc.setTrace(true);
		return bc;
	}

	private void createSqlContent(Composite parent) throws SystemException {
		tabFolder = createTabFolder(parent);
		final String TabHeighGrab = "           ";
		for (int i = 0; i < tabs.length; i++) {
			tabFolder.getItem(i).setText(TabHeighGrab + tabs[i] + TabHeighGrab);
			SourceViewer textSection = new SourceViewer(tabFolder, null,
					SWT.WRAP | SWT.V_SCROLL);
			textSection.configure(new SQLConfiguration(new ColorManager()));
			Document document = new Document();
			textSection.setDocument(document);
			final StyledText st = textSection.getTextWidget();
			tabFolder.getItem(i).setControl(textSection.getControl());
			final int itemIndex = i;
			tabFolder.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				public void widgetSelected(SelectionEvent e) {
					if (tabFolder.getSelectionIndex() == itemIndex
							&& (st.getText() != null && !st.getText()
									.equals(""))) {
						return;
					}
					try {
						StringBuffer sqlbf = modelService
								.getSql(tabs[itemIndex]);
						String sql = sqlbf.toString();
						if (sqlbf != null) {
							SQLFormat sf = new SQLFormat();
							st.setText(sf.format(sql));
						}
					} catch (Throwable ex) {
						ex.printStackTrace();
						st.setText(ExceptionUtil.getExceptionTraceMessage(ex));
					}
				}
			});
		}
		tabFolder.layout(true);
	}

	private CTabFolder createTabFolder(final Composite parent) {
		final CTabFolder tabFolder = new CTabFolder(parent, SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
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
			public void maximize(CTabFolderEvent event) {
				tabFolder.setMaximized(true);
				sashForm.setMaximizedControl(tabFolder);
				parent.layout(true);
			}

			public void minimize(CTabFolderEvent event) {
				tabFolder.setMinimized(true);
				tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
						false));
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

		for (int i = 0; i < tabs.length; i++) {
			new CTabItem(tabFolder, SWT.H_SCROLL);
		}
		return tabFolder;
	}

	private void createToolbar(ViewForm viewForm) {
		ToolBar toolBar = new ToolBar(viewForm, SWT.RIGHT | SWT.FLAT);
		toolBarManager = new ToolBarManager(toolBar);
		ExecuteSqlAction action = new ExecuteSqlAction(this,
				ExecuteSqlAction.getDefaultImageDescriptor(), null);
		addActions(new Action[] { action });
		viewForm.setTopLeft(toolBar);
	}

	private void creatTableViewer(Composite parent, ResultSet resultSet)
			throws ApplicationException {
		tableViewer.clearAll(false);
		tableViewer.createViewer(parent);
		tableViewer.getControl().setVisible(true);
		String[] ColumnProperties = createColumnProperties(resultSet);
		tableViewer.setColumnNames(ColumnProperties);
		CompositeMap input = getInput(resultSet, ColumnProperties);
		tableViewer.setData(input);
		tableViewer.packColumns();
	}

	private CompositeMap getInput(ResultSet tableRet, String[] ColumnProperties)
			throws SystemException {
		CompositeMap input = new CommentCompositeMap();
		try {
			while (tableRet.next()) {
				CompositeMap element = new CommentCompositeMap();
				for (int i = 0; i < ColumnProperties.length; i++) {
					element.put(ColumnProperties[i],
							tableRet.getObject(ColumnProperties[i]));
				}
				input.addChild(element);
			}
		} catch (SQLException e) {
			throw new SystemException(e);
		}
		return input;
	}

	private BusinessModelService makeBusinessModelService(
			FakeUncertainEngine uncertainEngine, Connection connection,
			String content) throws ApplicationException {
		IObjectRegistry reg = uncertainEngine.getObjectRegistry();
		DatabaseServiceFactory svcFactory = (DatabaseServiceFactory) reg
				.getInstanceOfType(DatabaseServiceFactory.class);

		BusinessModelServiceContext bc = createContext(uncertainEngine,
				connection);
		CompositeMap context = bc.getObjectContext();
		IDEModelFactory modelFactory = new IDEModelFactory(
				uncertainEngine.getOc_manager(),
				((IFileEditorInput) getEditor().getEditorInput()).getFile());
		uncertainEngine.getObjectRegistry().registerInstanceOnce(
				IModelFactory.class, modelFactory);
		svcFactory.setModelFactory(modelFactory);
		svcFactory.updateSqlCreator(modelFactory);
		try {
			CompositeMap bm_model = svcFactory.getModelFactory()
					.getCompositeLoader()
					.loadFromString(content, AuroraConstant.ENCODING);
			CompositeMap cim = lowerCaseDeepClone(bm_model);
			BusinessModelService service = svcFactory.getModelService(cim,
					context);
			return service;
		} catch (Throwable e) {
			throw new SystemException(e);
		}
	}

	private CompositeMap lowerCaseDeepClone(CompositeMap map) {
		CaseInsensitiveMap cim = new CaseInsensitiveMap(map);
		List<CompositeMap> list = cim.getChilds();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				list.set(i, lowerCaseDeepClone(list.get(i)));
			}
		}
		return cim;
	}
}
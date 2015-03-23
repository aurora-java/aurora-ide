package aurora.ide.bm.wizard.procedure;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.editor.widgets.GridViewer;
import aurora.ide.editor.widgets.core.IGridViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DBConnectionUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.helpers.SystemException;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (bm).
 */

public class BMFromProcedurePage extends WizardPage {
	private final String[] columnNames = { "object_name", "procedure_name",
			"subprogram_id", "overload", "object_type" };
	private final String[] columnTitles = { "对象", "过程", "顺序", "重载", "类型" };
	public static final String FILE_EXT = "bm";
	private Button overwriteButton;
	private CTabFolder tabFolder;
	private OracleProcedureObject focusObject;
	private IProject project;

	private GridViewer gridViewer;
	private CompositeMap data;

	/**
	 * Constructor for BMFromProcedurePage.
	 * 
	 * @param pageName
	 */
	public BMFromProcedurePage(IProject project) {
		super("wizardPage");
		this.project = project;
		setTitle(LocaleMessage.getString("business.model.editor.file"));
		setDescription(LocaleMessage.getString("bm.wizard.desc"));
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		container.setLayout(layout);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		tabFolder = new CTabFolder(container, SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		gd.heightHint = 400;
		tabFolder.setLayoutData(gd);
		gridViewer = new GridViewer(columnNames, IGridViewer.filterBar
				| IGridViewer.isMulti);
		try {
			gridViewer.setFilterColumn("object_name");
			gridViewer.setColumnTitles(columnTitles);
			gridViewer.createViewer(tabFolder);
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException {
					try {
						monitor.beginTask(
								LocaleMessage.getString("正在获取数据库过程,请稍等.."),
								IProgressMonitor.UNKNOWN);
						data = getProcedures();
					} catch (ApplicationException e) {
						DialogUtil.showExceptionMessageBox(e);
						// setErrorMessage(e.getCause().getMessage());
						// return;
					} finally {
						monitor.done();
					}
				}
			};
			AuroraPlugin.getDefault().getWorkbench().getProgressService()
			.busyCursorWhile(op);
//			getContainer().run(true, true, op);
			gridViewer.setData(data);
		} catch (Throwable e) {
			DialogUtil.showExceptionMessageBox(e);
			return;
		}

		CTabItem list = new CTabItem(tabFolder, SWT.H_SCROLL);
		list.setText("   对象列表   ");
		list.setControl(gridViewer.getControl());
		final CTabItem deteil = new CTabItem(tabFolder, SWT.H_SCROLL);
		deteil.setText("   明细   ");
		deteil.setControl(gridViewer.getControl());
		final StyledText content = new StyledText(tabFolder, SWT.WRAP
				| SWT.V_SCROLL);
		content.setFont(new Font(tabFolder.getDisplay(), "Courier New", 10,
				SWT.NORMAL));
		content.setEditable(false);
		deteil.setControl(content);
		gridViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				CompositeMap record = gridViewer.getFocus();
				if (record != null) {
					focusObject = new OracleProcedureObject(record
							.getString("object_name"), record
							.getString("procedure_name"), record.getInt(
							"subprogram_id").intValue(), record
							.getString("object_type"), project);
				} else {
					focusObject = null;

				}
			}
		});
		tabFolder.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (deteil.equals(e.item)) {
					if (focusObject != null) {
						try {
							content.setText(focusObject.toText());
						} catch (ApplicationException e1) {
							DialogUtil.showExceptionMessageBox(e1);
						}
					} else {
						content.setText("没有选择对象");
					}
				}

			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		tabFolder.setSelection(0);
		overwriteButton = new Button(container, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		overwriteButton.setLayoutData(gd);
		overwriteButton.setText("重名时,是否覆盖?");
		dialogChanged();
		setControl(container);
		validate();
	}

	public OracleProcedureObject getSelectionObject() {
		return focusObject;
	}

	public CompositeMap getSelection() {
		return gridViewer.getSelection();
	}

	public void setSelectionObject(OracleProcedureObject selectionObject) {
		this.focusObject = selectionObject;
	}

	private void dialogChanged() {
		if (project != null && !project.isAccessible()) {
			updateStatus(LocaleMessage.getString("project.must.be.writable"));
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public boolean isOverwrite() {
		return overwriteButton.getSelection();
	}

	public CompositeMap getProcedures() throws ApplicationException {
		if(project == null)
			return new CompositeMap();
		Connection connection = DBConnectionUtil
				.getDBConnectionSyncExec(project);
		Statement st = null;
		ResultSet rs = null;
		try {
			st = connection.createStatement();
			String select_sql = "select t.object_name, t.procedure_name, t.subprogram_id,t.overload, t.object_type"
					+ " from user_procedures t "
					+ " where t.subprogram_id <> 0 "
					+ " order by t.object_name, t.subprogram_id ";
			rs = st.executeQuery(select_sql);
			CompositeMap records = new CommentCompositeMap("records");
			while (rs.next()) {
				CompositeMap record = new CommentCompositeMap("record");
				record.put("object_name", rs.getString(1));
				record.put("procedure_name", rs.getString(2));
				record.put("subprogram_id", new Integer(rs.getInt(3)));
				record.put("overload", rs.getString(4));
				record.put("object_type", rs.getString(5));
				records.addChild(record);
			}
			return records;
		} catch (SQLException e) {
			throw new SystemException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
					st.close();
				} catch (SQLException e) {
					throw new SystemException(e);
				}
			}
		}
	}

	private void validate() {
		if (project == null) {
			this.updateStatus("无法找到Aurora工程");
		} else {
			try {
				Connection dbConnection = DBConnectionUtil
						.getDBConnection(project);
				if (dbConnection == null) {
					this.updateStatus("无法获取数据库连接");
				}
			} catch (ApplicationException e) {
				this.updateStatus("无法获取数据库连接");
			}
		}
	}
}
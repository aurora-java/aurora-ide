package aurora.ide.bm.wizard.table;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.bm.BMUtil;
import aurora.ide.celleditor.CellInfo;
import aurora.ide.celleditor.ICellEditor;
import aurora.ide.celleditor.StringTextCellEditor;
import aurora.ide.editor.widgets.GridViewer;
import aurora.ide.editor.widgets.WizardPageRefreshable;
import aurora.ide.editor.widgets.core.IGridViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (bm).
 */

public class BMTablePage extends WizardPageRefreshable {
	public static final String FILE_EXT = "bm";
	private BMFromDBWizard wizard;
	private String tableName;
	private String tableRemarks;
	private final String[] columnNames = { "TABLE_NAME", "REMARKS",
			"TABLE_TYPE", "TABLE_SCHEM", "TABLE_CAT" };
	private final int REMARKS_INDEX = 1;
	private final String[] columnTitles = { "表名", "描述", "类型", "模式", "编目" };
	DatabaseMetaData dbMetaData;
	GridViewer gridViewer;
	private CompositeMap input = new CompositeMap();
	private Text fileText;

	public BMTablePage(ISelection selection, BMFromDBWizard bmWizard) {
		super("wizardPage");
		setTitle(LocaleMessage.getString("business.model.editor.file"));
		setDescription(LocaleMessage.getString("bm.wizard.desc"));
		this.wizard = bmWizard;
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
		Composite gridComposite = new Composite(container, SWT.NULL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		gd.heightHint = 400;
		gridComposite.setLayoutData(gd);
		gridComposite.setLayout(new GridLayout());
		try {
			final Connection dbConnection = wizard.getConnection();
			AuroraPlugin.getDefault().getWorkbench().getProgressService()
					.busyCursorWhile(new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								dbMetaData = dbConnection.getMetaData();
								input = getInput(dbMetaData, "%");
							} catch (SQLException e) {
								DialogUtil.showExceptionMessageBox(e);
							}
						}
					});
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
		} catch (InvocationTargetException e) {
		} catch (InterruptedException e) {
		}

		gridViewer = new GridViewer(columnNames, IGridViewer.filterBar
				| IGridViewer.NoToolBar | IGridViewer.isOnlyUpdate);
		try {
			gridViewer.setFilterColumn("TABLE_NAME");
			gridViewer.setColumnTitles(columnTitles);
			gridViewer.createViewer(gridComposite);
			CellEditor[] celleditors = new CellEditor[columnNames.length];
			CellInfo cellProperties = new CellInfo(gridViewer, "REMARKS", false);
			ICellEditor cellEditor = new StringTextCellEditor(cellProperties);
			celleditors[REMARKS_INDEX] = cellEditor.getCellEditor();
			cellEditor.init();
			gridViewer.addEditor("REMARKS", cellEditor);
			gridViewer.setCellEditors(celleditors);
			gridViewer.setData(input);
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return;
		}
		gridViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (getTableName() == null)
					setPageComplete(false);
				else {
					setPageComplete(true);
					fileText.setText(getTableName().toLowerCase() + "."
							+ FILE_EXT);
				}
				try {
					wizard.refresh();
				} catch (ApplicationException e) {
					DialogUtil.showExceptionMessageBox(e);
					return;
				}

			}
		});
		Label label = new Label(container, SWT.NULL);
		label.setText(LocaleMessage.getString("file.name"));
		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkPageValues();
			}
		});
		setControl(container);
		setPageComplete(false);

	}

	public String getContainerName() {
		return wizard.getContainerName();
	}

	public DatabaseMetaData getDBMetaData() {
		return dbMetaData;
	}

	public CompositeMap getPrimaryKeys() throws SQLException {
		CompositeMap primaryKeyArray = new CommentCompositeMap(BMUtil.BMPrefix,
				AuroraConstant.BMUri, "primary-key");
		String tableName = getTableName();
		if (tableName == null)
			return primaryKeyArray;
		ResultSet tableRet = dbMetaData.getPrimaryKeys(null,
				dbMetaData.getUserName(), tableName);
		while (tableRet.next()) {
			CompositeMap field = new CommentCompositeMap(BMUtil.BMPrefix,
					AuroraConstant.BMUri, "pk-field");
			field.put("name", tableRet.getString("COLUMN_NAME").toLowerCase());
			primaryKeyArray.addChild(field);
		}
		return primaryKeyArray;
	}

	public String getTableName() {
		CompositeMap record = gridViewer.getSelection();
		if (record == null) {
			return null;
		}
		tableName = record.getString("TABLE_NAME");
		return tableName;
	}

	public String getTableRemarks() {
		CompositeMap record = gridViewer.getSelection();
		if (record == null) {
			return null;
		}
		tableRemarks = record.getString("REMARKS");
		return tableRemarks;
	}

	private CompositeMap getInput(DatabaseMetaData DBMetaData,
			String tableNamePattern) throws SQLException {
		CompositeMap input = new CommentCompositeMap();
		ResultSet tableRet = DBMetaData.getTables(null,
				DBMetaData.getUserName(), tableNamePattern, new String[] {
						"TABLE", "VIEW" });
		while (tableRet.next()) {
			int seq = 0;
			CompositeMap element = new CommentCompositeMap();
			element.put(columnNames[seq],
					tableRet.getString(columnNames[seq++]));
			element.put(columnNames[seq],
					tableRet.getString(columnNames[seq++]));
			element.put(columnNames[seq],
					tableRet.getString(columnNames[seq++]));
			element.put(columnNames[seq],
					tableRet.getString(columnNames[seq++]));
			element.put(columnNames[seq],
					tableRet.getString(columnNames[seq++]));
			input.addChild(element);
		}
		return input;
	}

	public String getFileName() {
		String fileName = fileText.getText();
		if (fileName.indexOf(".") == -1) {
			fileName = fileName + "." + FILE_EXT;
		}
		return fileName.toLowerCase();
	}

	public void checkPageValues() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		String fileName = getFileName();

		if (getContainerName().length() == 0) {
			updatePageStatus(LocaleMessage
					.getString("file.container.must.be.specified"));
			return;
		}
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updatePageStatus(LocaleMessage
					.getString("file.container.must.exist"));
			return;
		}
		if (fileName != null
				&& !fileName.equals("")
				&& ((IContainer) container).getFile(new Path(fileName))
						.exists()) {
			updatePageStatus(LocaleMessage.getString("filename.used"));
			return;
		}
		if (!container.isAccessible()) {
			updatePageStatus(LocaleMessage
					.getString("project.must.be.writable"));
			return;
		}
		if (fileName.length() == 0) {
			updatePageStatus(LocaleMessage
					.getString("file.name.must.be.specified"));
			return;
		}

		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updatePageStatus(LocaleMessage.getString("file.name.must.be.valid"));
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("bm") == false) {
				updatePageStatus(LocaleMessage
						.getString("file.extension.must.be.bm"));
				return;
			}
		}
		updatePageStatus(null);
	}
}
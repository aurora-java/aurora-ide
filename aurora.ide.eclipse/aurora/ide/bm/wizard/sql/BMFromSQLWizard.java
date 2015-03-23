package aurora.ide.bm.wizard.sql;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import uncertain.composite.CompositeMap;
import uncertain.datatype.DataType;
import uncertain.datatype.DataTypeRegistry;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.bm.AuroraDataBase;
import aurora.ide.bm.BMUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.helpers.SystemException;

/**
 * This is a sample new wizard. Its role is to create a new file resource in the
 * provided container. If the container resource (a folder or a project) is
 * selected in the workspace when the wizard is opened, it will accept it as the
 * target container. The wizard creates one file with the extension "bm". If a
 * sample multi-page editor (also available as a template) is registered for the
 * same extension, it will be able to open it.
 */

public class BMFromSQLWizard extends Wizard implements INewWizard {

	private final static String bm_pre = "bm";

	private BMFromSQLWizardPage mainPage;
	private ISelection selection;
	private CompositeMap initContent;

	/**
	 * Constructor for BmNewWizard.
	 */
	public BMFromSQLWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		mainPage = new BMFromSQLWizardPage(selection);
		addPage(mainPage);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		final String containerName = mainPage.getContainerName();
		final String fileName = mainPage.getFileName();
		initContent = createInitContent();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * The worker method. It will find the container, create the file if missing
	 * or just replace its contents, and open the editor on the newly created
	 * file.
	 */

	private void doFinish(String containerName, String fileName,
			IProgressMonitor monitor) throws CoreException {

		if (fileName.indexOf(".") == -1) {
			fileName = fileName + ".bm";
		}
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			DialogUtil.showErrorMessageBox("Container \"" + containerName
					+ "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try {
			InputStream stream = openContentStream();
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (Throwable e) {
			DialogUtil.showExceptionMessageBox(e);
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}

	/**
	 * We will initialize file contents with a sample text.
	 * 
	 * @throws SystemException
	 */

	private InputStream openContentStream() throws SystemException {
		String contents = AuroraResourceUtil.xml_decl
				+ AuroraResourceUtil.LineSeparator
				+ AuroraResourceUtil.getSign()
				// + XMLOutputter.defaultInstance().toXML(initContent, true);
				+ CommentXMLOutputter.defaultInstance()
						.toXML(initContent, true);

		try {
			return new ByteArrayInputStream(
					contents.getBytes(AuroraConstant.ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e);
		}
	}

	private CompositeMap createInitContent() {
		CompositeMap model = new CommentCompositeMap(BMFromSQLWizard.bm_pre,
				AuroraConstant.BMUri, "model");
		CompositeMap operations = new CommentCompositeMap(bm_pre,
				AuroraConstant.BMUri, "operations");
		CompositeMap operation = new CommentCompositeMap(bm_pre, AuroraConstant.BMUri,
				"operation");
		operations.addChild(operation);
		model.addChild(operations);
		String sql = mainPage.getSQL();
		String operationName = "update";
		if (sql.toLowerCase().trim().startsWith("select")) {
			operationName = "query";
		}
		operation.put("name", operationName);
		if (operationName.equals("query")) {
			CompositeMap query = new CommentCompositeMap(bm_pre, AuroraConstant.BMUri,
					"query-sql");
			query.setText(sql);
			operation.addChild(query);
		} else {
			CompositeMap update = new CommentCompositeMap(bm_pre,
					AuroraConstant.BMUri, "update-sql");
			update.setText(sql);
			operation.addChild(update);
		}
		CompositeMapTagParser tagParser = new CompositeMapTagParser();
		CompositeMap parameters = tagParser.parse(sql);
		if (parameters.getChildsNotNull().size() > 0) {
			operation.addChild(parameters);
		}

		Connection dbConnection = null;
		try {
			dbConnection = getConnection();
		} catch (Exception e) {
			// do nothing
		}
		if (dbConnection == null || !operationName.equals("query"))
			return model;
		String fromClause = getSelectClause(sql);
		if (fromClause == null)
			return model;
		String test = fromClause + " where 1<>1";
		CompositeMap selectedFields = null;
		try {
			Statement stmt = dbConnection.createStatement();
			ResultSet result = stmt.executeQuery(test);
			ResultSetMetaData rsMetaData = result.getMetaData();
			selectedFields = getSelectedFields(rsMetaData);
		} catch (SQLException e) {
			DialogUtil.showExceptionMessageBox(e);
		}
		if (selectedFields != null
				&& selectedFields.getChildsNotNull().size() > 0) {
			model.addChild(selectedFields);
		}
		return model;
	}

	public Connection getConnection() throws ApplicationException {
		String containerName = mainPage.getContainerName();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throw new ApplicationException(LocaleMessage.getString("container")
					+ " \"" + containerName + "\""
					+ LocaleMessage.getString("not.exist"));
		}
		AuroraDataBase ad = new AuroraDataBase(resource.getProject());
		Connection conn = ad.getDBConnection();
		return conn;
	}

	private static String getSelectClause(String sql) {
		if (sql == null)
			return null;
		String upperCase = sql.toUpperCase();
		int fromIndex = upperCase.indexOf("FROM");
		if (fromIndex == -1)
			return null;
		int whereIndex = upperCase.indexOf("#WHERE_CLAUSE#", fromIndex);
		if (whereIndex == -1) {
			whereIndex = upperCase.indexOf("WHERE", fromIndex);
			if (whereIndex == -1)
				whereIndex = sql.length();
		}
		return sql.substring(0, whereIndex);
	}

	public static void main(String[] args) {
		String sql = "select empno, ename, deptno, hiredate from emp  e #WHERE_CLAUSE#";
		sql = getSelectClause(sql);
		System.out.println(sql);

	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	public CompositeMap getSelectedFields(ResultSetMetaData resultSetMetaData)
			throws SQLException {
		CompositeMap fieldsArray = new CommentCompositeMap(BMUtil.BMPrefix,
				AuroraConstant.BMUri, "fields");
		for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
			CompositeMap field = new CommentCompositeMap(BMUtil.BMPrefix,
					AuroraConstant.BMUri, "field");
			String columnName = resultSetMetaData.getColumnName(i);
			field.put("name", columnName.toLowerCase());
			field.put("physicalName", columnName);
			if (resultSetMetaData.isNullable(i) == ResultSetMetaData.columnNoNulls)
				field.put("required", "true");
			String dataType = resultSetMetaData.getColumnTypeName(i);
			field.put("databaseType", dataType);
			Integer db_data_type = new Integer(
					resultSetMetaData.getColumnType(i));
			DataTypeRegistry dtr = DataTypeRegistry.getInstance();
			DataType dt = dtr.getType(db_data_type.intValue());
			if (dt == null) {
				DialogUtil.showErrorMessageBox("field:" + columnName
						+ " have a " + dataType + " which dataBase Type is "
						+ db_data_type + " is not registried!");
			} else
				field.put("datatype", dt.getJavaType().getName());

			fieldsArray.addChild(field);
		}
		return fieldsArray;
	}
}
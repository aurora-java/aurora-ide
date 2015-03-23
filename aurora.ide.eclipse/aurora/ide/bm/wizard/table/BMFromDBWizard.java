package aurora.ide.bm.wizard.table;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import oracle.jdbc.driver.OracleConnection;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.bm.BMUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.DBConnectionUtil;
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

public class BMFromDBWizard extends Wizard implements INewWizard {

	private BMMainConfigPage mainConfigPage;
	private BMTablePage tablePage;
	private BMFieldsPage fieldsPage;
	private ISelection selection;
	private CompositeMap initContent;
	private Connection connnect;
	private HashMap promptList = new HashMap();

	/**
	 * Constructor for BMFromDBWizard
	 */
	public BMFromDBWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		mainConfigPage = new BMMainConfigPage(selection, this);
		tablePage = new BMTablePage(selection, this);
		fieldsPage = new BMFieldsPage(selection, this);
		fieldsPage.setPageComplete(false);
		addPage(mainConfigPage);
		addPage(tablePage);
		addPage(fieldsPage);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		final String containerName = mainConfigPage.getContainerName();
		final String fileName = tablePage.getFileName();
		try {
			initContent = createInitContent();
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return true;
		}
		final boolean registerPrompt = isAutoRegisterPrompt();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, monitor);
					if (registerPrompt) {
						autoRegisterPrompt();
					}
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
			DialogUtil.showErrorMessageBox(LocaleMessage.getString("container")
					+ " \"" + containerName + "\""
					+ LocaleMessage.getString("not.exist"));
			return;
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

	public void autoRegisterPrompt() {
		try {
			RegisterDescription rd = new RegisterDescription(getConnection());
			rd.setPromptList(promptList);
			rd.run();
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
		}
	}

	/**
	 * We will initialize file contents with a sample text.
	 * 
	 * @throws SystemException
	 */

	private InputStream openContentStream() throws SystemException {
		String xmlHint = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String contents = xmlHint + AuroraResourceUtil.LineSeparator
				+ AuroraResourceUtil.getSign()
				// + XMLOutputter.defaultInstance().toXML(initContent);
				+ CommentXMLOutputter.defaultInstance().toXML(initContent);

		try {
			return new ByteArrayInputStream(
					contents.getBytes(AuroraConstant.ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e);
		}
	}

	private CompositeMap createInitContent() throws ApplicationException {

		CompositeMap model = new CommentCompositeMap(BMUtil.BMPrefix,
				AuroraConstant.BMUri, "model");
		model.put("baseTable", getTableName());
		model.put("alias", "t1");
		addFieldsAndFeatures(model);
		try {
			CompositeMap pks = getPrimaryKeys();
			if (pks != null && pks.getChilds() != null) {
				model.addChild(pks);
			}
		} catch (SQLException e) {
			DialogUtil.showExceptionMessageBox(e);
		}
		return model;
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

	public String getTableName() {
		return tablePage.getTableName();
	}

	public String getTableRemarks() {
		return tablePage.getTableRemarks();
	}

	public DatabaseMetaData getDBMetaData() {
		return tablePage.getDBMetaData();
	}

	public CompositeMap getPrimaryKeys() throws SQLException {
		return tablePage.getPrimaryKeys();
	}

	public void createPageControls(Composite pageContainer) {
		// super.createPageControls(pageContainer);
	}

	private CompositeMap addFieldsAndFeatures(CompositeMap model)
			throws ApplicationException {
		if (model == null)
			return null;
		CompositeMap features = new CommentCompositeMap(BMUtil.BMPrefix,
				AuroraConstant.BMUri, "features");
		CompositeMap standardWho = new CommentCompositeMap(BMUtil.FeaturesPrefix,
				BMUtil.FeaturesUri, "standard-who");
		features.addChild(standardWho);
		CompositeMap fields = fieldsPage.getSelectedFields();
		// handle multi language
		String descIdFieldName = "description_id";
		if (fields == null)
			return model;
		model.addChild(fields);
		model.addChild(features);
		CompositeMap descIdField = fields.getChildByAttrib("name",
				descIdFieldName);
		if (descIdField == null)
			return model;
		descIdField.put("multiLanguage", "true");
		descIdField.put("multiLanguageDescField", "description");
		CompositeMap descField = new CommentCompositeMap(fields.getPrefix(),
				fields.getNamespaceURI(), "field");
		descField.put("name", "description");
		descField.put("databaseType", "VARCHAR");
		descField.put("datatype", "java.lang.String");
		String prompt_code = getTableName() + ".DESCRIPTION";
		descField.put("prompt", prompt_code);
		if (getTableRemarks() != null) {
			String tableRemarks = getTableRemarks();
			int endIndex = tableRemarks.indexOf("表");
			if (endIndex == -1)
				endIndex = tableRemarks.length();
			String descFieldPrompt = tableRemarks.substring(0, endIndex) + "描述";
			promptList.put(prompt_code, descFieldPrompt);

		}

		fields.addChild(descField);
		CompositeMap multiLanguage = new CommentCompositeMap(BMUtil.FeaturesPrefix,
				BMUtil.FeaturesUri, "multi-language-storage");
		features.addChild(multiLanguage);
		return model;
	}

	public Connection getConnection() throws ApplicationException {
		if (connnect == null) {
			IProject project = mainConfigPage.getProject();
			if(project == null)
				return connnect;
			connnect = DBConnectionUtil.getDBConnection(project );
			if (connnect instanceof OracleConnection) {
				((OracleConnection) connnect).setRemarksReporting(true);
			}
		}
		return connnect;
	}

	public void refresh() throws ApplicationException {
		if (fieldsPage.getControl() != null)
			fieldsPage.refresh();
	}

	public boolean isAutoRegisterPrompt() {
		return mainConfigPage.getAutoRegisterPromptButton().getSelection();
	}

	public void addPrompt(String prompt_code, String description) {
		promptList.put(prompt_code, description);
	}

	public String getContainerName() {
		return mainConfigPage.getContainerName();
	}
}
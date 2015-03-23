package aurora.ide.bm.wizard.procedure;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
import aurora.ide.AuroraProjectNature;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.DBConnectionUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ProjectUtil;
import aurora.ide.helpers.SystemException;
import aurora.ide.project.AuroraProject;

/**
 * This is a sample new wizard. Its role is to create a new file resource in the
 * provided container. If the container resource (a folder or a project) is
 * selected in the workspace when the wizard is opened, it will accept it as the
 * target container. The wizard creates one file with the extension "bm". If a
 * sample multi-page editor (also available as a template) is registered for the
 * same extension, it will be able to open it.
 */

public class BMFromProcedure extends Wizard implements INewWizard {

	private BMFromProcedurePage mainPage;
	private IProject project;
	private boolean isOverwrite;
	private String errMsg;

	/**
	 * Constructor for BmNewWizard.
	 */
	public BMFromProcedure() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		mainPage = new BMFromProcedurePage(project);
		addPage(mainPage);
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		CompositeMap selection = mainPage.getSelection();
		isOverwrite = mainPage.isOverwrite();
		if (selection == null || selection.getChilds() == null
				&& selection.getChilds().size() < 1)
			return true;
		for (Iterator it = selection.getChildIterator(); it.hasNext();) {
			final CompositeMap record = (CompositeMap) it.next();
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException {
					try {
						doFinish(record, monitor);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			};
			try {
				getContainer().run(true, false, op);
			} catch (Throwable e) {
				DialogUtil.showExceptionMessageBox(e);
			}
		}
		return true;
	}

	/**
	 * The worker method. It will find the container, create the file if missing
	 * or just replace its contents, and open the editor on the newly created
	 * file.
	 */

	private void doFinish(CompositeMap record, IProgressMonitor monitor)
			throws CoreException {
		String fileName = getFullFilePath(record);
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		String bmHome = "";
		try {
			bmHome = ProjectUtil.getBMHome(project);
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return;
		}
		final IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(new Path(bmHome + File.separator + fileName));
		try {
			InputStream stream = openContentStream(record);
			if (file.exists()) {
				if (isOverwrite) {
					file.setContents(stream, true, true, monitor);
				} else {
					stream.close();
					return;
				}
			} else {
				createParentFolder(file.getParent());
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (Throwable e) {
			e.printStackTrace();
			DialogUtil.logErrorException(e);
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

	private String getFullFilePath(CompositeMap record) {
		String fullPath = "db" + File.separator;
		String object_name = record.getString("object_name");
		String procedure_name = record.getString("procedure_name");
		fullPath = fullPath + object_name;
		if (procedure_name != null && !"".equals(procedure_name)) {
			fullPath = fullPath + File.separator + procedure_name;
		}
		String overload = record.getString("overload");
		if (overload != null && !"".equals(overload)) {
			fullPath = fullPath + "_" + overload;
		}
		fullPath = fullPath + ".bm";
		return fullPath.toLowerCase();
	}

	private void createParentFolder(IContainer foler) throws CoreException {
		IContainer parent = foler.getParent();
		if (parent != null && !parent.exists()) {
			createParentFolder(parent);
		}
		if (!foler.exists())
			((IFolder) foler).create(IResource.NONE, true, null);
	}

	/**
	 * We will initialize file contents with a sample text.
	 * 
	 * @throws ApplicationException
	 */

	private InputStream openContentStream(CompositeMap record)
			throws ApplicationException {
		String xmlHint = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		OracleProcedureObject Object = new OracleProcedureObject(
				record.getString("object_name"),
				record.getString("procedure_name"), record.getInt(
						"subprogram_id").intValue(),
				record.getString("object_type"), project);
		String contents = xmlHint + AuroraResourceUtil.LineSeparator
				+ AuroraResourceUtil.getSign()
				// +
				// XMLOutputter.defaultInstance().toXML(Object.toCompositeMap(),
				// true);
				+ CommentXMLOutputter.defaultInstance().toXML(
						Object.toCompositeMap(), true);
		try {
			return new ByteArrayInputStream(
					contents.getBytes(AuroraConstant.ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e);
		}
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		project = ProjectUtil.getIProjectFromSelection();
		try {
			if (project == null
					|| (AuroraProjectNature.hasAuroraNature(project) == false)) {
				IProject project = AuroraProject
						.openProjectSelectionDialog(this.getShell());
				this.project = project;
			}
		} catch (CoreException e) {
		}
	}
}
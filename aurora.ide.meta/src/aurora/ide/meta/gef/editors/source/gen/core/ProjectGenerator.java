package aurora.ide.meta.gef.editors.source.gen.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.ResourceDescription;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.LogUtil;
import aurora.ide.meta.exception.GeneratorException;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.exception.TemplateNotBindedException;
import aurora.ide.meta.gef.FileFinder;
import aurora.ide.meta.gef.i18n.Messages;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.prototype.freemarker.FMConfigration;
import aurora.ide.search.core.Message;
import aurora.ide.search.core.Util;
import aurora.ide.search.ui.MessageFormater;
import aurora.plugin.source.gen.BuilderSession;
import aurora.plugin.source.gen.ModelMapParser;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.IProperties;
import freemarker.template.TemplateException;

public class ProjectGenerator {
	private IProject project;
	private boolean isOverlap;
	private int fNumberOfFilesToScan;
	private IFile fCurrentFile;
	private int fNumberOfScannedFiles;
	private Shell shell;
	private IProject auroraProject;
	private IFolder screenFolder;
	private IContainer auroraWebFolder;
	private String errorMessage;
	// private String header;
	private GeneratorManager gm;

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public ProjectGenerator(IProject project, boolean isOverlap, Shell shell) {
		super();
		this.project = project;
		this.isOverlap = isOverlap;
		this.shell = shell;
		this.gm = GeneratorManager.createNewInstance(getAuroraProject());

	}

	public boolean isOverlap() {
		return isOverlap;
	}

	public void setOverlap(boolean isOverlap) {
		this.isOverlap = isOverlap;
	}

	public void go(final IProgressMonitor monitor)
			throws InvocationTargetException {

		boolean validate = validate();
		if (validate == false)
			throw new InvocationTargetException(new GeneratorException());

		FileFinder fileFinder = new FileFinder();
		try {
			project.accept(fileFinder);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}

		// header = createHeader();

		List<IResource> files = fileFinder.getResult();
		fNumberOfFilesToScan = files.size();
		Job monitorUpdateJob = new Job("source generator") { //$NON-NLS-1$
			private int fLastNumberOfScannedFiles = 0;

			public IStatus run(final IProgressMonitor inner) {
				while (!inner.isCanceled()) {
					final IFile file = fCurrentFile;
					if (file != null) {
						updateMonitor(monitor, file);
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						return Status.OK_STATUS;
					}
				}
				return Status.OK_STATUS;
			}

			private void updateMonitor(final IProgressMonitor monitor,
					final IFile file) {
				String fileName = file.getName();
				final Object[] args = { fileName,
						new Integer(fNumberOfScannedFiles),
						new Integer(fNumberOfFilesToScan) };
				monitor.subTask(MessageFormater.format(Message._scanning, args));
				int steps = fNumberOfScannedFiles - fLastNumberOfScannedFiles;
				monitor.worked(steps);
				fLastNumberOfScannedFiles += steps;
			}
		};

		monitor.beginTask(Messages.ProjectGenerator_Gen_source, files.size());
		monitorUpdateJob.setSystem(true);
		monitorUpdateJob.schedule();
		try {
			if (files != null) {
				for (int i = 0; i < files.size(); i++) {
					if (monitor.isCanceled())
						return;
					fCurrentFile = (IFile) files.get(i);
					fNumberOfScannedFiles++;
					try {
						processFile(fCurrentFile, monitor);
					} catch (IOException e) {
						// 没有找到摸版，代码生成终止。
						break;
					} catch (TemplateException e) {
						// 摸版格式异常，代码生成终止。
						LogUtil.getInstance().logError("摸版格式异常，代码生成终止。", e);
						errorMessage = "摸版格式异常，代码生成终止。查看log获得详细信息。";
						throw new InvocationTargetException(
								new GeneratorException());
					} catch (TemplateNotBindedException e) {
						// 文件未绑定摸版，忽略此文件。
						continue;
					} catch (SAXException e) {
						// 生成文件格式不正确，忽律此文件。
						LogUtil.getInstance().logError(
								"生成文件格式不正确，忽律此文件 : " + fCurrentFile.getName(),
								e);
						continue;
					}
				}
				genMasterDetailSVC();
			}
		} finally {
			monitorUpdateJob.cancel();
			monitor.done();
		}
	}

	private void genMasterDetailSVC() throws InvocationTargetException {
		IFile newFile = auroraWebFolder.getFile(new Path(
				"master_detail_auto_save.svc"));
		if (newFile.exists())
			return;
		InputStream is = ProjectGenerator.class
				.getResourceAsStream("master_detail_auto_save.svc");
		CreateFileOperation cfo = new CreateFileOperation(newFile, null, is,
				"create file.") {
			@Override
			protected void setResourceDescriptions(
					ResourceDescription[] descriptions) {
				super.setResourceDescriptions(descriptions);
			}

			public IStatus computeExecutionStatus(IProgressMonitor monitor) {
				IStatus status = super.computeExecutionStatus(monitor);
				if (status.isOK()) {
					status = computeCreateStatus(false);
				}
				return status;
			}
		};

		try {
			cfo.execute(null, WorkspaceUndoUtil.getUIInfoAdapter(shell));
		} catch (ExecutionException e) {
			throw new InvocationTargetException(e);
		}

	}

	// private String createHeader() {
	//		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"; //$NON-NLS-1$
	// String date = DateFormat.getDateInstance().format(new java.util.Date());
	//		String user = System.getProperty("user.name"); //$NON-NLS-1$
	//		String comment = "<!-- \n  $Author: " + user + " \n  $Date: " + date //$NON-NLS-1$ //$NON-NLS-2$
	//				+ " \n" //$NON-NLS-1$
	//				+ "  $Revision: 1.0 \n  $add by aurora_ide team.\n-->\n\r "; //$NON-NLS-1$
	//
	// return s + comment;
	// }

	public boolean validate() {
		auroraProject = this.getAuroraProject();
		screenFolder = this.getScreenFolder();
		auroraWebFolder = this.getAuroraWebFolder();
		if (auroraProject == null) {
			errorMessage = Messages.ProjectGenerator_Project_error;
			return false;
		}
		if (auroraWebFolder == null) {
			errorMessage = Messages.ProjectGenerator_web_error;
			return false;
		}
		if (screenFolder == null) {
			errorMessage = Messages.ProjectGenerator__folder_erroe;
			return false;
		}
//		try {
//			FMConfigration.Instance().getTemplate("");
//		} catch (IOException e) {
//			errorMessage = "没有找到摸版，代码生成终止。";
//			return false;
//		}
		return true;
	}

	private void genNewFile(IFile newFile, String content)
			throws InvocationTargetException {
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(content.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if (is == null) {
			return;
		}
		if (newFile.exists() && isOverlap) {
			try {
				// newFile.delete(true, monitor);
				newFile.setContents(is, true, false, null);
				return;
			} catch (CoreException e) {
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

			}
		}
		CreateFileOperation cfo = new CreateFileOperation(newFile, null, is,
				"create file.") { //$NON-NLS-1$
			@Override
			protected void setResourceDescriptions(
					ResourceDescription[] descriptions) {
				super.setResourceDescriptions(descriptions);
			}

			public IStatus computeExecutionStatus(IProgressMonitor monitor) {
				IStatus status = super.computeExecutionStatus(monitor);
				if (status.isOK()) {
					// Overwrite is not allowed when we are creating a new
					// file
					status = computeCreateStatus(false);
				}
				return status;
			}
		};

		try {
			cfo.execute(null, WorkspaceUndoUtil.getUIInfoAdapter(shell));
		} catch (ExecutionException e) {
			throw new InvocationTargetException(e);
		}
	}

	private void processFile(IFile fCurrentFile, IProgressMonitor monitor)
			throws InvocationTargetException, IOException, TemplateException,
			TemplateNotBindedException, SAXException {

		if (fCurrentFile.getName().contains("grid_grid")) {
			System.out.println();
		}

		IFile newFile = getNewFile(fCurrentFile);
		if (newFile.exists() && !isOverlap) {
			return;
		}
		CompositeMap loadFile = CompositeMapUtil.loadFile(fCurrentFile);
		BuilderSession session = gm.createBuilderSession();
		String fileName = getFileName();
		session.addConfig(IProperties.FILE_NAME, fileName);
		String genFile = gm.buildScreen(loadFile, session).toXML();
		genNewFile(newFile, genFile);
		genRelation(gm, loadFile, 0);
	}

	private String getFileName() {
		return this.getFileName(this.fCurrentFile);
	}

	private String getFileName(IFile file) {
		IPath filePath = file.getProjectRelativePath();
		String fileName = filePath.removeFileExtension().lastSegment();
		return fileName;
	}

	private void genRelation(GeneratorManager sgm, CompositeMap loadFile, int i) {
		ModelMapParser mmp = new IDEModelMapParser(loadFile, getAuroraProject());
		List<CompositeMap> renderers = mmp.getComponents(IProperties.renderer);
		for (CompositeMap renderer : renderers) {
			String type = renderer.getString(
					ComponentInnerProperties.RENDERER_TYPE, "");
			if (ComponentInnerProperties.PAGE_REDIRECT.equals(type)) {
				String openpath = renderer.getString(
						ComponentInnerProperties.OPEN_PATH, "");
				if ("".equals(openpath) || openpath.toLowerCase().endsWith("uip") == false) {
					continue;
				}
				CompositeMap inner_paramerter = renderer.getChildByAttrib(
						ComponentInnerProperties.PROPERTYE_ID,
						ComponentInnerProperties.RENDERER_PARAMETERS)
						.getChildByAttrib(
								ComponentInnerProperties.COMPONENT_TYPE,
								ComponentInnerProperties.INNER_PARAMERTER);
				String value = inner_paramerter.getString(
						ComponentInnerProperties.PARAMETER_VALUE, "");
				String name = inner_paramerter.getString(
						ComponentInnerProperties.PARAMETER_NAME, "");
				genRelationFile(sgm, i, openpath, name, value);
			}
		}
	}

	private void genRelationFile(GeneratorManager sgm, int i, String openPath,
			String para_name, String para_value) {
		if (i > 10) {
			// circle protected
			// a-->b--a
			return;
		}
		i++;
		if (null == openPath || "".equals(openPath))
			return;
		IPath p = new Path(openPath);
		if ("uip".equalsIgnoreCase(p.getFileExtension())) {
			IFile fCurrentFile = this.screenFolder.getFile(p);
			String fileName = getFileName();
			openPath = aurora.plugin.source.gen.Util.getNewLinkFilePath(
					openPath, fileName);
			p = new Path(openPath);
			IFile newFile = this.getNewFile(p);
			CompositeMap loadFile = CompositeMapUtil.loadFile(fCurrentFile);
			if (loadFile == null)
				return;
			String genFile;
			try {
				BuilderSession session = gm.createBuilderSession();
				session.addConfig(
						ComponentInnerProperties.BE_OPENED_FROM_ANOTHER, true);
				session.addConfig("para_name", para_name);
				session.addConfig("para_value", para_value);
				session.addConfig(IProperties.FILE_NAME,
						getFileName(fCurrentFile));
				genFile = sgm.buildScreen(loadFile, session).toXML();
				genNewFile(newFile, genFile);
				genRelation(sgm, loadFile, i);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private IProject getAuroraProject() {
		AuroraMetaProject amp = new AuroraMetaProject(project);
		try {
			return amp.getAuroraProject();
		} catch (ResourceNotFoundException e) {
		}
		return null;
	}

	private IFolder getScreenFolder() {
		AuroraMetaProject amp = new AuroraMetaProject(project);
		try {
			return amp.getScreenFolder();
		} catch (ResourceNotFoundException e) {
		}
		return null;
	}

	private IContainer getAuroraWebFolder() {
		IContainer findWebInf = Util.findWebInf(auroraProject);
		return (IContainer) (findWebInf == null ? null : findWebInf.getParent());
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	private IFile getNewFile(IFile file) {
		IPath makeRelativeTo = file.getProjectRelativePath().makeRelativeTo(
				screenFolder.getProjectRelativePath());
		return getNewFile(makeRelativeTo);
	}

	private IFile getNewFile(IPath makeRelativeTo) {
		makeRelativeTo = makeRelativeTo.removeFileExtension();
		makeRelativeTo = makeRelativeTo.addFileExtension("screen"); //$NON-NLS-1$
		return auroraWebFolder.getFile(makeRelativeTo);
	}
}

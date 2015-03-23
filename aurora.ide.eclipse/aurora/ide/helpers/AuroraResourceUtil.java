/**
 * 
 */
package aurora.ide.helpers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentCompositeLoader;
import aurora.ide.preferencepages.AuroraTemplateContextType;
import aurora.ide.preferencepages.AuroraTemplateManager;

public class AuroraResourceUtil {

	public static final String LineSeparator = System
			.getProperty("line.separator");
	public static final String xml_decl = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

	public static String getIfileLocalPath(IFile ifile) {
		String fileFullPath = ifile.getLocation().toOSString();
		return fileFullPath;
	}

	public static String getLocalPathFromIPath(IPath path) {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(path);
		return resource.getLocation().toOSString();

	}

	public static IResource getIResourceSelection() {
		return getIResourceFromIStructuredSelection(getStructuredSelection());
	}

	public static IResource getIResourceFromIStructuredSelection(
			IStructuredSelection selection) {
		if (selection == null)
			return null;
		StructuredSelection currentSelection = new StructuredSelection(
				IDE.computeSelectedResources(selection));
		Iterator it = currentSelection.iterator();
		if (it.hasNext()) {
			Object object = it.next();
			IResource selectedResource = null;
			if (object instanceof IResource) {
				selectedResource = (IResource) object;
			} else if (object instanceof IAdaptable) {
				selectedResource = (IResource) ((IAdaptable) object)
						.getAdapter(IResource.class);
			}
			if (selectedResource != null) {
				if (selectedResource.getType() == IResource.FILE) {
					selectedResource = selectedResource.getParent();
				}
				if (selectedResource.isAccessible()) {
					return selectedResource;
				}
			}
		}
		return null;
	}

	// public static IStructuredSelection getStructuredSelection() {
	// IStructuredSelection selectionToPass =
	// AuroraPlugin.getDefault().getStructuredSelection();
	// IWorkbenchWindow window =
	// PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	// if (window == null)
	// return selectionToPass;
	// ISelection selection = window.getSelectionService().getSelection();
	// if (selection instanceof IStructuredSelection) {
	// selectionToPass = (IStructuredSelection) selection;
	// } else {
	// // Build the selection from the IFile of the editor
	// IWorkbenchPart part = window.getPartService().getActivePart();
	// if (part instanceof IEditorPart) {
	// IEditorInput input = ((IEditorPart) part).getEditorInput();
	// Class fileClass = IFile.class;
	// if (input != null && fileClass != null) {
	// Object file = Platform.getAdapterManager().getAdapter(input, fileClass);
	// if (file != null) {
	// selectionToPass = new StructuredSelection(file);
	// }
	// }
	// }
	// }
	// return selectionToPass;
	// }
	public static IStructuredSelection getStructuredSelection() {
		IStructuredSelection selectionToPass = AuroraPlugin.getDefault()
				.getStructuredSelection();
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window == null)
			return selectionToPass;
		// Build the selection from the IFile of the editor

		IWorkbenchPart part = window.getPartService().getActivePart();
		if (part instanceof IEditorPart) {
			IEditorInput input = ((IEditorPart) part).getEditorInput();
			Class fileClass = IFile.class;
			if (input != null && fileClass != null) {
				Object file = Platform.getAdapterManager().getAdapter(input,
						fileClass);
				if (file != null) {
					return selectionToPass = new StructuredSelection(file);
				}
			}
		}

		ISelection selection = window.getSelectionService().getSelection();
		if (selection instanceof IStructuredSelection) {
			selectionToPass = (IStructuredSelection) selection;
		}
		return selectionToPass;
	}

	public static String getRegisterPath(IFile file)
			throws ApplicationException {
		if (file == null)
			return null;
		char fileSeparatorChar = '/';
		if (file.getName().endsWith("." + AuroraConstant.ScreenFileExtension)
				|| file.getName().endsWith(
						"." + AuroraConstant.SvcFileExtension)) {
			String fileName = AuroraResourceUtil.getIfileLocalPath(file);
			String rootDir = ProjectUtil.getWebHomeLocalPath(file.getProject());
			int webLocation = fileName.indexOf(rootDir);
			if (webLocation == -1) {
				return "";
			}
			String registerPath = fileName.substring(webLocation
					+ rootDir.length() + 1);
			registerPath = registerPath.replace(File.separatorChar,
					fileSeparatorChar);
			return registerPath;
		} else if (file.getName()
				.endsWith("." + AuroraConstant.BMFileExtension)) {
			String fileName = AuroraResourceUtil.getIfileLocalPath(file);
			String rootDir = ProjectUtil.getBMHomeLocalPath(file.getProject());
			int webLocation = fileName.indexOf(rootDir);
			int endIndex = fileName.lastIndexOf(".");
			if (webLocation == -1) {
				return "";
			}
			String registerPath = fileName.substring(
					webLocation + rootDir.length() + 1, endIndex);
			registerPath = registerPath.replace(File.separatorChar, '.');
			return registerPath;
		}
		return file.getName();

	}

	public static CompositeMap loadFromResource(IResource file)
			throws ApplicationException {
		if (file == null || !file.exists()) {
			return null;
		}
		String fullLocationPath = file.getLocation().toOSString();
		CompositeLoader cl = CommentCompositeLoader.createInstanceForOCM();

		cl.setSaveNamespaceMapping(true);
		CompositeMap bmData;
		try {
			bmData = cl.loadByFile(fullLocationPath);
		} catch (IOException e) {
			throw new ApplicationException("文件路径" + fullLocationPath + "不存在!",
					e);
		} catch (SAXException e) {
			throw new ApplicationException("文件" + fullLocationPath + "格式不正确!",
					e);
		}
		return bmData;
	}

	static void iteratorResource(IContainer parent, List bmList) {
		try {
			IResource[] childs = parent.members();
			for (int i = 0; i < childs.length; i++) {
				IResource child = childs[i];
				if (child.exists()
						&& child.getName().toLowerCase()
								.endsWith("." + AuroraConstant.BMFileExtension)) {
					bmList.add(child);
				}
				if (child instanceof IContainer) {
					iteratorResource((IContainer) child, bmList);
				}
			}
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		}
	}

	public static CompositeLoader getCompsiteLoader() {
		CompositeLoader cl = new CommentCompositeLoader();
		cl.setSaveNamespaceMapping(true);
		cl.setSupportXInclude(false);

		CompositeLoader projectCl = new CommentCompositeLoader();
		projectCl.setSaveNamespaceMapping(true);
		projectCl.setSupportXInclude(false);
		IProject project = ProjectUtil.getIProjectFromSelection();
		if (project != null) {
			projectCl.setBaseDir(project.getLocation().toFile().getParent()
					.toString()
					+ File.separator);
			cl.addExtraLoader(projectCl);
		}
		CompositeLoader curentDircl = new CommentCompositeLoader();
		curentDircl.setSaveNamespaceMapping(true);
		curentDircl.setSupportXInclude(false);
		IFile currentFile = getFileFromSelection();
		if (currentFile != null) {
			curentDircl.setBaseDir(currentFile.getLocation().toOSString());
			cl.addExtraLoader(curentDircl);
		}
		return cl;
	}

	public static IFile getFileFromSelection() {
		IStructuredSelection selection = AuroraPlugin.getDefault()
				.getStructuredSelection();
		if (selection == null || !(selection instanceof IFile))
			return null;
		return (IFile) selection;
	}

	public static IResource getResource(IContainer parent, String resourceName)
			throws SystemException {
		IResource[] childs;
		try {
			childs = parent.members();
		} catch (CoreException e) {
			throw new SystemException(e);
		}
		for (int i = 0; i < childs.length; i++) {
			IResource child = childs[i];
			if (resourceName.equals(child.getName().toLowerCase())) {
				return child;
			}
			if (child instanceof IContainer) {
				IResource result = getResource((IContainer) child, resourceName);
				if (result != null)
					return result;
			}
		}
		return null;
	}

	public static String getSign() {
		String templateString = "";
		Template template = null;
		try {
			template = AuroraTemplateManager.getInstance().getTemplateStore()
					.findTemplateById(AuroraTemplateContextType.SIGN);
		} catch (SystemException e1) {
			DialogUtil.showExceptionMessageBox(e1);
		}
		;
		if (template != null) {
			TemplateContextType contextType = AuroraTemplateManager
					.getInstance().getContextTypeRegistry()
					.getContextType(AuroraTemplateContextType.SIGN);
			IDocument document = new Document();
			TemplateContext context = new DocumentTemplateContext(contextType,
					document, 0, 0);
			try {
				TemplateBuffer buffer = context.evaluate(template);
				templateString = buffer.getString();
			} catch (Exception e) {
				DialogUtil.showExceptionMessageBox(e);
			}
		}
		return templateString;

	}

	public static File getClassPathFile(String fileName) throws IOException {
		ClassLoader loader = UncertainEngine.class.getClassLoader();
		URL url = loader.getResource(fileName);
		if (url == null) {
			loader = Thread.currentThread().getContextClassLoader();
			url = loader.getResource(fileName);
		}
		if (url == null)
			throw new IOException("Can't find " + fileName
					+ " from current classpath");
		url = FileLocator.toFileURL(url);
		return new File(url.getFile());
	}
}

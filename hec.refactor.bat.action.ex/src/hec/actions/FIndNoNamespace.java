package hec.actions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.search.cache.CacheManager;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 555555555555555555555555
 * @see IWorkbenchWindowActionDelegate
 */
public class FIndNoNamespace implements IWorkbenchWindowActionDelegate {

	static private List<IFile> files = new ArrayList<IFile>();

	static private List<IFile> noNSfiles = new ArrayList<IFile>();

	@Override
	public void run(IAction action) {
		clear();
		files();
		try {
			noNsFiles();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		System.out.println("****************无namespace********************");
		for (IFile f : noNSfiles) {
			sb.append(f.getProjectRelativePath());
			sb.append("\n");
		}
		System.out.println("****************无namespace********************");
		try {
			genFile(sb.toString());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
	}

	private void clear() {
		noNSfiles.clear();
		files.clear();
	}

	static String[] ss = { "script", "div", "table","style" };

	private void noNsFiles() throws CoreException, ApplicationException {
		for (final IFile f : files) {
			CompositeMap map = CacheManager.getCompositeMap(f);
			map.iterate(new IterationHandle() {

				@Override
				public int process(CompositeMap map) {
					String prefix = map.getPrefix();
					String namespaceURI = map.getNamespaceURI();
					if (Arrays.asList(ss).contains(map.getName().toLowerCase())) {
						return IterationHandle.IT_CONTINUE;
					}
					if ("".equals(namespaceURI) || null == namespaceURI) {
						noNSfiles.add(f);
						return IterationHandle.IT_BREAK;
					}
					return IterationHandle.IT_CONTINUE;
				}

			}, true);
		}

	}
	private static void genFile(String content)
			throws CoreException, ApplicationException {
		IProject files = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("files");
		IFile newFile = files.getFile("no_namespace.txt");
		InputStream is = new ByteArrayInputStream(
				(content).getBytes());
		System.out.println("生成文件：" + newFile.getProjectRelativePath());
		CreateFileOperation op = new CreateFileOperation(newFile, null, is,
				"Create New File");
		try {
			PlatformUI
					.getWorkbench()
					.getOperationSupport()
					.getOperationHistory()
					.execute(op, null,
							WorkspaceUndoUtil.getUIInfoAdapter(new Shell()));
		} catch (final ExecutionException e) {
			// handle exceptions
			e.printStackTrace();
		}
	}
	public void files() {
		IProject hec = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("web");

		try {
			hec.accept(new IResourceVisitor() {

				@Override
				public boolean visit(IResource resource) throws CoreException {
					if ("bm".equalsIgnoreCase(resource.getFileExtension())) {
						files.add((IFile) resource);
					}
					if ("screen".equalsIgnoreCase(resource.getFileExtension())) {
						files.add((IFile) resource);
					}
					if ("svc".equalsIgnoreCase(resource.getFileExtension())) {
						files.add((IFile) resource);
					}
					return true;
				}
			});
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(IWorkbenchWindow window) {
	}
}
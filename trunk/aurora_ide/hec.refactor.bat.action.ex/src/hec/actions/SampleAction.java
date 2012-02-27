package hec.actions;

import hec.actions.refactor.bm.move.MoveRefactoring;
import hec.actions.refactor.bm.move.NewFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.MoveResourcesOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.refactoring.ui.AuroraRefactoringWizard;
import aurora.ide.search.cache.CacheManager;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 4444444444444444444444444444
 * @see IWorkbenchWindowActionDelegate
 */
public class SampleAction implements IWorkbenchWindowActionDelegate {

	static Map<String, NewFile> newFiles;

	@Override
	public void run(IAction action) {
		// ResourcesPlugin.getWorkspace();
		clear();
		readconfig();
		// try {
		// genFile();
		// } catch (CoreException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ApplicationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// pathmapping
		// moverefactoring

		runRefactoring();

	}

	private static void genFile() throws CoreException, ApplicationException {

		StringBuilder sb = new StringBuilder();
		sb.append("****************BM MOVE********************");

		Collection<NewFile> values = newFiles.values();
		for (NewFile newFile : values) {
			sb.append(newFile.oldPath + "=>" + newFile.getNewPath());
			sb.append("\n");
		}
		sb.append("****************BM MOVE********************");

		IProject files = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("files");
		IFile newFile = files.getFile("BM_Function_Mapping.txt");
		InputStream is = new ByteArrayInputStream((sb.toString()).getBytes());
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

	void runRefactoring() {
		MoveRefactoring refactor = new MoveRefactoring();
		refactor.newFiles = newFiles.values();
		refactor.init();
		AuroraRefactoringWizard wizard = new AuroraRefactoringWizard(refactor);
		RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(
				wizard);
		try {
			op.run(new Shell(), "Move BM");
		} catch (InterruptedException e) {
		}

		Collection<IFile> values = refactor.ft.keySet();
		int size = values.size();
		int i = 0;
		for (IFile iFile : values) {
			System.out.println("移动文件 " + i + "/" + size + " ："
					+ iFile.getProjectRelativePath());
			i++;
			if(!iFile.exists()){
				System.out.println("文件不存在 : "+iFile.getProjectRelativePath());
				continue;
			}
			MoveResourcesOperation mro = new MoveResourcesOperation(iFile,
					refactor.ft.get(iFile).getFullPath(), "move screen");
			try {
				PlatformUI
						.getWorkbench()
						.getOperationSupport()
						.getOperationHistory()
						.execute(mro, null,
								WorkspaceUndoUtil.getUIInfoAdapter(new Shell()));
			} catch (final ExecutionException e) {
				// handle exceptions
				e.printStackTrace();
			}

			// try {
			//
			// // iFile.move(refactor.ft.get(iFile).getFullPath(), true, null);
			// genFile(refactor.ft.get(iFile), iFile);
			// } catch (CoreException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (ApplicationException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// try {
			// System.out.println("删除文件：" + iFile.getProjectRelativePath());
			// if (!refactor.ft.get(iFile).equals(iFile))
			// iFile.delete(true, null);
			// else {
			// System.out.println("不删除文件："
			// + iFile.getProjectRelativePath());
			// }
			// } catch (CoreException e) {
			// e.printStackTrace();
			// }
		}
		System.out.println("============END===================");
	}

	private static void genFile(IFile newFile, IFile oldFile)
			throws CoreException, ApplicationException {
		CompositeMap cm = CacheManager.getCompositeMap(oldFile);
		String xml_decl = "<?xml version=\"1.0\" encoding=\"" + "UTF-8"
				+ "\"?>\n";
		// System.out.println(cm.toXML());
		InputStream is = new ByteArrayInputStream(
				(xml_decl + cm.toXML()).getBytes());
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

	private void clear() {
		newFiles = new HashMap<String, NewFile>();
	}

	private void readconfig() {

		InputStream resourceAsStream = null;

		try {
			resourceAsStream = SampleAction.class
					.getResourceAsStream("FunctionBm.txt");
			InputStreamReader osw = new InputStreamReader(resourceAsStream);

			BufferedReader r = new BufferedReader(osw);
			String s = r.readLine();
			while (s != null) {
				// System.out.println(s);

				String[] split = s.split(":");
				NewFile nf = new NewFile();
				nf.functionName = split[0];
				nf.oldPath = split[1];
				NewFile newFile = newFiles.get(split[1]);
				if (newFile != null) {
					newFile.isPublic = true;
				} else {
					newFiles.put(split[1], nf);
				}
				s = r.readLine();
			}

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (resourceAsStream != null)
				try {
					resourceAsStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}
}
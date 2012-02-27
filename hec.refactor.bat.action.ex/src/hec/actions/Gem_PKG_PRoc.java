package hec.actions;

import hec.actions.refactor.pkg_proc.move.MoveRefactoring;
import hec.actions.refactor.pkg_proc.move.NewFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.refactoring.ui.AuroraRefactoringWizard;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.Util;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 2222222222222222222222222222222
 * @see IWorkbenchWindowActionDelegate
 */
public class Gem_PKG_PRoc implements IWorkbenchWindowActionDelegate {

	static private List<IFile> bmList = new ArrayList<IFile>();
	static private List<IFile> targetBmList = new ArrayList<IFile>();

	static private List<NewFile> newFiles = new ArrayList<NewFile>();

	static private List<NewFile> _inFiles = new ArrayList<NewFile>();
	static private List<List<NewFile>> sames = new ArrayList<List<NewFile>>();

	

	static private StringBuilder sb = new StringBuilder();

	@Override
	public void run(IAction action) {
		clear();
		bms();
		targetBms();
		findSame();

//		for (List<NewFile> files : sames) {
//			System.out.println();
//			System.out.println();
//			System.out
//					.println("****************************************************");
//
//			for (NewFile newFile : files) {
//				System.out.println("=========================================");
//				System.out.println("File PKG : "
//						+ Util.toBMPKG(newFile.oldFile));
//				System.out.println("pkg  : " + newFile.pkgName);
//				System.out.println("produce : " + newFile.produceName);
//			}
//			System.out
//					.println("****************************************************");
//
//		}

		System.out.println();
		runRefactoring();
	}

	boolean isSame(NewFile nf) {
		for (List<NewFile> nfs : sames) {
			if (nfs.contains(nf))
				return true;
		}
		return false;

	}

	List<NewFile> getNewList() {
		List<NewFile> nfs = new ArrayList<NewFile>();
		for (NewFile newFile : newFiles) {
			if (!isSame(newFile)) {
				nfs.add(newFile);
			}
		}
		return nfs;
	}

	void runRefactoring() {
		MoveRefactoring refactor = new MoveRefactoring();
		refactor.newFiles = getNewList();
		refactor.init();
		AuroraRefactoringWizard wizard = new AuroraRefactoringWizard(refactor);
		RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(
				wizard);
		try {
			op.run(new Shell(), "Screen Custom");
		} catch (InterruptedException e) {
		}
		Collection<IFile> values = refactor.ft.keySet();
		for (IFile iFile : values) {
			try {
				genFile(refactor.ft.get(iFile), iFile);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				
//				/db/wbc_core_pkg/doc_process_refuse_return.bm
//
//				/db/exp_report_pkg/delete_exp_report_lines.bm
//				db/wbc_core_pkg/doc_process_confirm_return.bm
//				db/wbc_core_pkg/dispatch_doc_to_operator.bm

				System.out.println("删除文件：" + iFile.getProjectRelativePath());
				if (!refactor.ft.get(iFile).equals(iFile))
					iFile.delete(true, null);
				else{
					System.out.println(iFile.getProjectRelativePath());
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// newfile
		// deletefile

	}

	private static void genFile(IFile newFile, IFile oldFile)
			throws CoreException, ApplicationException {
		CompositeMap cm = CacheManager.getCompositeMap(oldFile);
		String xml_decl = "<?xml version=\"1.0\" encoding=\"" + "UTF-8"
				+ "\"?>\n";
//		System.out.println(cm.toXML());
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

	private void findSame() {
		for (NewFile nfo : newFiles) {
			if (_inFiles.contains(nfo))
				continue;
			List<NewFile> _newFiles = new ArrayList<NewFile>();
			_newFiles.add(nfo);
			for (NewFile nfi : newFiles) {
				if (nfi.oldFile.equals(nfo.oldFile))
					continue;
				if (!nfi.pkgName.equals(nfo.pkgName))
					continue;
				if (!nfi.produceName.equals(nfo.produceName))
					continue;
				_newFiles.add(nfi);
				_inFiles.add(nfi);
			}
			if (_newFiles.size() > 1) {
				// for (NewFile newFile : _newFiles) {
				// System.out
				// .println("=========================================");
				// System.out.println("File PKG : "
				// + Util.toBMPKG(newFile.oldFile));
				// System.out.println("pkg  : " + newFile.pkgName);
				// System.out.println("produce : " + newFile.produceName);
				//
				// }
				sames.add(_newFiles);
			}
		}

	}

	public void targetBms() {
		try {
			for (IFile file : bmList) {
				CompositeMap map = (CompositeMap) CacheManager
						.getCompositeMap(file);
				List childs = map.getChilds();
				CompositeMap operations = map.getChild("operations");
				if (childs.size() == 1 && operations != null) {
					CompositeMap operation = operations.getChild("operation");
					if (operations.getChilds().size() == 1 && operation != null) {
						CompositeMap uSql = operation.getChild("update-sql");
						CompositeMap qSql = operation.getChild("query-sql");
						if (uSql != null && qSql == null
								&& matches(uSql.getText())) {
							String sql = uSql.getText();
							sb.append(sql);
							sb.append("\n");
							sb.append("============================"
									+ Util.toBMPKG(file)
									+ "=============================================");
							NewFile nf = new NewFile();
							int start = sql.toLowerCase().indexOf("begin")
									+ "begin".length();
							int end = sql.toLowerCase().indexOf("(");
							String s = sql.substring(start, end);
							String[] sp = s.trim().split("\\.");
							nf.pkgName = sp[0];
							nf.produceName = sp[1];
							nf.oldFile = file;
							newFiles.add(nf);
							targetBmList.add(file);
						}
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}

	public void clear() {
		bmList.clear();
		targetBmList.clear();
		sb = new StringBuilder();
		newFiles.clear();
		sames.clear();
		_inFiles.clear();
	}

	boolean matches(String sql) {
		String str = "begin\n"
				+ "                    WBC_CORE_PKG.DOC_PROCESS_REFUSE_RETURN\n"
				+ "                    (\n"
				+ "                        p_dispatching_header_id=>${@dispatching_header_id},\n"
				+ "                        p_dispatching_line_id=>${@dispatching_line_id},\n"
				+ "                        p_process_opinion=>${@process_opinion},\n"
				+ "                        p_user_id=>${ssion/@user_id}\n"
				+ "                    );\n" + "                end;";
		Pattern p = Pattern
				.compile(
						"\\s*begin\\s*[a-zA-Z0-9_]{1,30}\\.[a-zA-Z0-9_]{1,30}\\s*\\([^)]+\\)\\s*;\\s*end\\s*;\\s*",
						Pattern.DOTALL);
		// Pattern p = Pattern
		// .compile(
		// "\\s*begin\\s*[a-zA-Z0-9_]{1,30}\\.[a-zA-Z0-9_]{1,30}\\s*\\(.+\\).*end\\s*;\\s*",
		// Pattern.DOTALL);
		return p.matcher(sql).matches();
	}

	public void bms() {
		IProject hec = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("web");
		IFolder bmFolder = hec.getFolder(new Path("/WEB-INF/classes"));
		try {
			bmFolder.accept(new IResourceVisitor() {

				@Override
				public boolean visit(IResource resource) throws CoreException {
					if ("bm".equalsIgnoreCase(resource.getFileExtension())) {
						bmList.add((IFile) resource);
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
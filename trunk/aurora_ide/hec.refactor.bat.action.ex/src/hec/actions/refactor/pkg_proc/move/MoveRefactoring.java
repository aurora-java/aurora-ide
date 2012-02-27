package hec.actions.refactor.pkg_proc.move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import aurora.ide.refactoring.TextFileChangeManager;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.Util;
import aurora.ide.search.reference.MultiSourceReferenceSearchService;
import aurora.ide.search.reference.ReferenceSearchService;

public class MoveRefactoring extends Refactoring {

	public List<NewFile> newFiles = new ArrayList<NewFile>();

	public Map<IFile, IFile> ft = new HashMap<IFile, IFile>();
	IProject hec = ResourcesPlugin.getWorkspace().getRoot().getProject("web");
	IFolder bmFolder = hec.getFolder(new Path("/WEB-INF/classes"));
	private Map<String, String> pkgMap;
	private TextFileChangeManager changeManager;
	private List<AbstractMatch> relations;

	@Override
	public String getName() {
		return "MoveRefactoring";
	}

	public void init() {
		changeManager = new TextFileChangeManager();
		for (NewFile nf : newFiles) {
			ft.put(nf.oldFile,
					bmFolder.getFile(new Path("db").append(nf.pkgName.toLowerCase()).append(
							nf.produceName.toLowerCase() + ".bm")));
		}
		
	}

	private List<AbstractMatch> getRelations(IProgressMonitor pm) {
		Set<IFile> result = ft.keySet();
		IResource scope = hec;
		if (scope == null) {
			return null;
		}
		ReferenceSearchService seachService = new MultiSourceReferenceSearchService(
				scope, result.toArray(new IFile[result.size()]), null, true);
		seachService.setPostException(false);
		seachService.setRunInUI(true);
		relations = seachService.service(pm);
		return relations;
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		getRelations(pm);
		createPKGMap();
		Change createBMChange = createBMChange(pm);
		return createBMChange;
	}
	private void createPKGMap() {
		pkgMap = new HashMap<String, String>();
		Set<IFile> result = this.ft.keySet();
		System.out.println("********************pkgMapping************************");
		for (IFile file : result) {
			String oldPkg = Util.toBMPKG(file);
			String newPkg = Util.toBMPKG(ft.get(file));
			System.out.println(oldPkg+ "=>"+newPkg);
			pkgMap.put(oldPkg, newPkg);
		}
		System.out.println("********************pkgMapping************************");
	}

	private Change createBMChange(IProgressMonitor pm) throws CoreException {
		List relations = getRelations(pm);
		CompositeChange changes = new CompositeChange("aurora changes");
		changes.markAsSynthetic();
		for (int i = 0; i < relations.size(); i++) {
			AbstractMatch object = (AbstractMatch) relations.get(i);
			IFile file = (IFile) object.getElement();
			IDocument document = getDocument(file);
			TextFileChange textFileChange = null;
			if (textFileChange == null)
				textFileChange = changeManager.getTextFileChange(file);

			int offset = object.getOriginalOffset();
			int length = object.getOriginalLength();
			if (changeManager.isOverlapping(file, offset, length)) {
				continue;
			}
			try {
				String string = document.get(offset, length);
				ReplaceEdit edit = new ReplaceEdit(offset, length,
						this.pkgMap.get(string));
				textFileChange.addEdit(edit);
			} catch (BadLocationException e) {
				continue;
			}
		}
		changes.addAll(changeManager.getAllChanges());
		return changes;
	}
	public IDocument getDocument(IFile file) throws CoreException {
		return CacheManager.getDocumentCacher().getDocument(file);
	}
}

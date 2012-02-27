package hec.actions.refactor.screen.move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.text.edits.TextEdit;

import aurora.ide.refactoring.ScreenRefactoring;
import aurora.ide.refactoring.TextFileChangeManager;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.Util;
import aurora.ide.search.reference.MultiSourceReferenceSearchService;
import aurora.ide.search.reference.ReferenceSearchService;

public class MoveRefactoring extends Refactoring {

	public Collection<NewFile> newFiles = new ArrayList<NewFile>();

	public Map<IFile, IFile> ft = new HashMap<IFile, IFile>();
	IProject hec = ResourcesPlugin.getWorkspace().getRoot().getProject("web");
	private Map<String, String> pkgMap;
	private TextFileChangeManager changeManager;
	private List<AbstractMatch> relations;

	@Override
	public String getName() {
		return "MoveRefactoring";
	}

	public void init() {
		changeManager = new TextFileChangeManager();
		System.out
				.println("********************screenMapping************************");
		for (NewFile nf : newFiles) {
			System.out.println(nf.oldPath + "=>" + nf.getNewPath());
			ft.put(hec.getFile(new Path(nf.oldPath)),
					hec.getFile(new Path(nf.getNewPath())));
		}
		System.out
				.println("********************screenMapping************************");

	}

	private List<AbstractMatch> getRelations(IProgressMonitor pm) {
		Set<IFile> result = ft.keySet();
		IResource scope = hec;
		if (scope == null) {
			return null;
		}
		ReferenceSearchService seachService = new MultiSourceReferenceSearchService(
				scope, result.toArray(new IFile[result.size()]), null, false);
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
		return createScreenChange(pm);
	}

	private Change createScreenChange(IProgressMonitor pm) throws CoreException {
		List relations = getRelations(pm);
		CompositeChange changes = new CompositeChange("aurora changes");
		changes.markAsSynthetic();
		for (int i = 0; i < relations.size(); i++) {
			AbstractMatch object = (AbstractMatch) relations.get(i);
			IFile file = (IFile) object.getElement();
			if("exp_report_maintain_read_only.screen".equals(file.getName())){
				System.out.println();
			}
			TextFileChange textFileChange = null;
			if (textFileChange == null)
				textFileChange = changeManager.getTextFileChange(file);
			TextEdit edit;
			try {
				edit = createScreenEdit(object);
				if (edit != null)
					textFileChange.addEdit(edit);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		changes.addAll(changeManager.getAllChangesHasEdit());
		return changes;
	}

	private TextEdit createScreenEdit(AbstractMatch match)
			throws CoreException, BadLocationException {
		IFile file = (IFile) match.getElement();
		int offset = match.getOriginalOffset();
		int length = match.getOriginalLength();
		if (changeManager.isOverlapping(file, offset, length)) {
			return null;
		}
		String string = ScreenRefactoring.findAttributeValue(match);
		IFile findScreenFile = Util.findScreenFile(file, string);

		if (findScreenFile == null) {
			return null;
		}
//		if (findScreenFile.getParent().equals(file.getParent())) {
//			return null;
//		}
		IFile iFile = ft.get(findScreenFile);
		if(iFile == null){
			System.out.println(findScreenFile.getProjectRelativePath());
			return null;
		}
		IPath newPath = iFile.getProjectRelativePath();
		return ScreenRefactoring.createMoveTOScreenTextEdit(match, newPath);
	}

	public IDocument getDocument(IFile file) throws CoreException {
		return CacheManager.getDocumentCacher().getDocument(file);
	}
}

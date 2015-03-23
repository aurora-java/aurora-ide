package aurora.ide.refactoring;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;

import aurora.ide.search.core.Util;
import aurora.ide.search.reference.MultiSourceReferenceSearchService;
import aurora.ide.search.reference.ReferenceSearchService;

public class FolderDeleteParticipant extends DeleteParticipant {

	private FolderRefactorParticipant folderRefactorParticipant;

	public FolderDeleteParticipant() {
	}

	protected boolean initialize(Object element) {
		folderRefactorParticipant = new FolderRefactorParticipant();
		return folderRefactorParticipant.initialize(element);
	}

	public String getName() {
		return "Folder Delete Participant";
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		RefactoringStatus result = new RefactoringStatus();
		//TODO 
//		List findRelations = this.findRelations(pm);
//		if (findRelations.size() > 0) {
//			result.merge(RefactoringStatus.createInfoStatus("删除的文件会影响其他文件。"));
//		}
		return result;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		return null;
	}

	private List findRelations(IProgressMonitor pm) {
		List<IFile> result = folderRefactorParticipant.getFiles();
		IResource scope = Util.getScope(folderRefactorParticipant
				.getCurrentFolder());
		if(scope == null){
			return Collections.EMPTY_LIST;
		}
		ReferenceSearchService seachService = new MultiSourceReferenceSearchService(
				scope, result.toArray(new IFile[result.size()]), null,
				folderRefactorParticipant.isBMFolder());
		seachService.setPostException(false);
		List relations = seachService.service(pm);
		return relations;
	}
}

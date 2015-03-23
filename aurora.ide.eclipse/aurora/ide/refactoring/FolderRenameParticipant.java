package aurora.ide.refactoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import aurora.ide.preferencepages.RefactorSettingPreferencePage;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.Util;
import aurora.ide.search.reference.MultiSourceReferenceSearchService;
import aurora.ide.search.reference.ReferenceSearchService;

public class FolderRenameParticipant extends RenameParticipant {
	private FolderRefactorParticipant folderRefactorParticipant;
	private Map<String, String> pkgMap;
	private TextFileChangeManager changeManager;
	private boolean check;

	public FolderRenameParticipant() {
	}

	protected boolean initialize(Object element) {
		String newName = this.getArguments().getNewName();
		if ("web-inf".equalsIgnoreCase(newName)
				|| "classes".equalsIgnoreCase(newName)) {
			return false;
		}
		folderRefactorParticipant = new FolderRefactorParticipant();
		changeManager = new TextFileChangeManager();
		return folderRefactorParticipant.initialize(element);
	}

	public String getName() {
		return "Folder Rename Participant";
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		check = true;
		RefactoringStatus result = new RefactoringStatus();
		
		boolean refactorStatus = RefactorSettingPreferencePage.getRefactorStatus(RefactorSettingPreferencePage.REFACTOR_SETTING_FOLDER_RENAME);
		if(refactorStatus == false){
			this.check = false;
			result.merge(RefactoringStatus
					.createInfoStatus("IDE设置修改目录名不启用重构."));
			return result;
		}
		
		IFolder currentFolder = folderRefactorParticipant.getCurrentFolder();
		if (currentFolder.equals(Util.findWebInf(currentFolder).getParent())) {
			this.check = false;
			result.merge(RefactoringStatus
					.createInfoStatus("重构目录是Web主目录，Aurora重构不会进行。"));
		}
		if (currentFolder.equals(Util.findBMHome(currentFolder))) {
			this.check = false;
			result.merge(RefactoringStatus
					.createInfoStatus("重构目录是BM主目录，Aurora重构不会进行。"));
		}
		if (this.getArguments().getNewName().contains(".")) {
			this.check = false;
			result.merge(RefactoringStatus
					.createInfoStatus("新目录名字错误，Aurora重构不会进行。"));
		}

		return result;
	}

	@Override
	public Change createPreChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		if (!this.check)
			return null;

		if (folderRefactorParticipant.isBMFolder()) {
			createPKGMap();
			return createBMChange(pm);
		} else {
			return createScreenChange(pm);
		}
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {

		return null;
	}

	private Change createScreenChange(IProgressMonitor pm) throws CoreException {
		List<IFile> result = folderRefactorParticipant.getFiles();
		IResource scope = Util.getScope(folderRefactorParticipant
				.getCurrentFolder());
		if (scope == null) {
			return null;
		}
		ReferenceSearchService seachService = new MultiSourceReferenceSearchService(
				scope, result.toArray(new IFile[result.size()]), null,
				folderRefactorParticipant.isBMFolder());
		seachService.setPostException(false);
		List relations = seachService.service(pm);
		CompositeChange changes = new CompositeChange("aurora changes");
		changes.markAsSynthetic();
		for (int i = 0; i < relations.size(); i++) {
			AbstractMatch object = (AbstractMatch) relations.get(i);
			IFile file = (IFile) object.getElement();
			TextFileChange textFileChange = changeManager
					.getTextFileChangeInProcessor(this.getProcessor(), file);
			if (textFileChange == null)
				textFileChange = changeManager.getTextFileChange(file);
			TextEdit edit;
			try {
				edit = createScreenEdit(object);
				if (edit != null)
					textFileChange.addEdit(edit);
			} catch (BadLocationException e) {
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
		if (findScreenFile.getParent().equals(file.getParent())) {
			return null;
		}
		IPath folderPath = this.folderRefactorParticipant.getCurrentFolder()
				.getProjectRelativePath();

		IPath newPath = folderPath
				.removeLastSegments(1)
				.append(this.getArguments().getNewName())
				.append(findScreenFile.getProjectRelativePath().makeRelativeTo(
						folderPath));
		return ScreenRefactoring.createMoveTOScreenTextEdit(match, newPath);
	}

	private void createPKGMap() {
		pkgMap = new HashMap<String, String>();
		List<IFile> result = folderRefactorParticipant.getFiles();
		for (IFile file : result) {
			String oldPkg = Util.toBMPKG(file);
			IPath folderPath = this.folderRefactorParticipant
					.getCurrentFolder().getProjectRelativePath();
			IPath filePath = file.getProjectRelativePath();
			IPath raelativeTo = filePath.makeRelativeTo(folderPath);
			IPath newPath = folderPath.removeLastSegments(1).append(
					this.getArguments().getNewName());
			IPath oo = newPath.append(raelativeTo).removeFileExtension();
			String newPkg = Util.toPKG(oo);
			pkgMap.put(oldPkg, newPkg);
		}
	}

	private Change createBMChange(IProgressMonitor pm) throws CoreException {
		List<IFile> result = folderRefactorParticipant.getFiles();
		IResource scope = Util.getScope(folderRefactorParticipant
				.getCurrentFolder());
		if (scope == null) {
			return null;
		}
		ReferenceSearchService seachService = new MultiSourceReferenceSearchService(
				scope, result.toArray(new IFile[result.size()]), null,
				folderRefactorParticipant.isBMFolder());
		seachService.setPostException(false);
		List relations = seachService.service(pm);
		CompositeChange changes = new CompositeChange("aurora changes");
		changes.markAsSynthetic();
		for (int i = 0; i < relations.size(); i++) {
			AbstractMatch object = (AbstractMatch) relations.get(i);
			IFile file = (IFile) object.getElement();
			IDocument document = getDocument(file);
			TextFileChange textFileChange = changeManager
					.getTextFileChangeInProcessor(this.getProcessor(), file);
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
		return CacheManager.getDocument(file);
	}
}

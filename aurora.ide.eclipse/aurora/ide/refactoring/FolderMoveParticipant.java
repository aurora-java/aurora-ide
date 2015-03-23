package aurora.ide.refactoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
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
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import aurora.ide.preferencepages.RefactorSettingPreferencePage;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.Util;
import aurora.ide.search.reference.MultiSourceReferenceSearchService;
import aurora.ide.search.reference.ReferenceSearchService;

public class FolderMoveParticipant extends MoveParticipant {
	private FolderRefactorParticipant folderRefactorParticipant;

	private Map<String, String> pkgMap;
	private TextFileChangeManager changeManager;

	private IFolder moveTO;
	private boolean check;

	public FolderMoveParticipant() {
	}

	protected boolean initialize(Object element) {

		Object destination = this.getArguments().getDestination();
		// ifnature in
		if (element instanceof IFolder && destination instanceof IFolder) {
			folderRefactorParticipant = new FolderRefactorParticipant();
			changeManager = new TextFileChangeManager();
			moveTO = (IFolder) destination;
			if (((IFolder) element).getParent().equals(moveTO)
					|| !((IFolder) element).getProject().equals(
							moveTO.getProject())) {
				// 目标目录相同，不参与
				// 工程不同不参与
				return false;
			}
			return folderRefactorParticipant.initialize(element);
		}
		return false;
	}

	public String getName() {
		return "Folder Move Participant";
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		this.check = true;
		RefactoringStatus result = new RefactoringStatus();

		boolean refactorStatus = RefactorSettingPreferencePage.getRefactorStatus(RefactorSettingPreferencePage.REFACTOR_SETTING_FOLDER_MOVE);
		if(refactorStatus == false){
			this.check = false;
			result.merge(RefactoringStatus
					.createInfoStatus("IDE设置移动目录不启用重构."));
			return result;
		}
		
		Object destination = this.getArguments().getDestination();
		// bm move
		if (destination instanceof IResource
				&& (folderRefactorParticipant.isBMFolder())) {
			IPath path = ((IResource) destination).getProjectRelativePath();
			String pkg = Util.toRelativeClassesPKG(path);
			if (pkg.length() == 0) {
				this.check = false;
				result.merge(RefactoringStatus
						.createInfoStatus("目标目录不属于classes,Aurora重构不会进行,请Cancel后重新选择."));
			}
		}
		// screen move
		if (destination instanceof IResource
				&& (!folderRefactorParticipant.isBMFolder())) {

			IContainer webInf = Util.findWebInf(folderRefactorParticipant
					.getCurrentFolder());
			if (webInf == null) {
				this.check = false;
				result.merge(RefactoringStatus
						.createInfoStatus("WEB-INF未发现,Aurora重构不会进行,请Cancel后重新选择."));
			}
			if (webInf != null) {
				IPath path = ((IResource) destination).getProjectRelativePath();
				boolean inWeb = webInf.getParent().getProjectRelativePath()
						.isPrefixOf(path);
				boolean inWebInf = webInf.getProjectRelativePath().isPrefixOf(
						path);
				if (!inWeb || inWebInf) {
					this.check = false;
					result.merge(RefactoringStatus
							.createInfoStatus("目标目录无效,Aurora重构不会进行,请Cancel后重新选择."));
				}
			}
		}
		return result;
	}

	@Override
	public Change createPreChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		// bug:select->preview->back->select another->preview
		// getArguments().getDestination() did nont changed
		if (check) {
			if (folderRefactorParticipant.isBMFolder()) {
				createPKGMap();
				Change createBMChange = createBMChange(pm);
				return createBMChange;
			} else {
				return createScreenChange(pm);
			}
		}
		return null;
	}

	private List getRelations(IProgressMonitor pm) {
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
		seachService.setRunInUI(true);
		List relations = seachService.service(pm);
		return relations;
	}

	private Change createScreenChange(IProgressMonitor pm) throws CoreException {
		List relations = getRelations(pm);
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
		}if(findScreenFile.getParent().equals(file.getParent())){
			return null;
		}
		IPath folderPath = this.folderRefactorParticipant.getCurrentFolder()
				.getParent().getProjectRelativePath();
		IPath newPath = moveTO.getProjectRelativePath().append(
				findScreenFile.getProjectRelativePath().makeRelativeTo(
						folderPath));
		return ScreenRefactoring.createMoveTOScreenTextEdit(match, newPath);
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {

		return null;
	}

	private void createPKGMap() {
		pkgMap = new HashMap<String, String>();
		List<IFile> result = folderRefactorParticipant.getFiles();
		for (IFile file : result) {
			String oldPkg = Util.toBMPKG(file);
			IPath folderPath = folderRefactorParticipant.getCurrentFolder()
					.getParent().getProjectRelativePath();
			IPath filePath = file.getProjectRelativePath();
			IPath raelativeTo = filePath.makeRelativeTo(folderPath)
					.removeFileExtension();
			IPath toPath = this.moveTO.getProjectRelativePath();
			String newPkg = Util.toPKG(toPath) + "." + Util.toPKG(raelativeTo);
			pkgMap.put(oldPkg, newPkg);
		}
	}

	private Change createBMChange(IProgressMonitor pm) throws CoreException {
		List relations = getRelations(pm);
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

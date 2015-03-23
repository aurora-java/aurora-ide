package aurora.ide.refactoring;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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

import aurora.ide.preferencepages.RefactorSettingPreferencePage;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.Util;
import aurora.ide.search.reference.ReferenceSearchService;

public class FileRenameParticipant extends RenameParticipant {

	private IFile currentSourcefile;
	private String fileExtension;
	private TextFileChangeManager changeManager;
	private boolean check;

	public FileRenameParticipant() {
	}

	protected boolean initialize(Object element) {

		if (element instanceof IFile) {
			this.currentSourcefile = (IFile) element;
			fileExtension = ((IFile) element).getFileExtension();
			changeManager = new TextFileChangeManager();
			return "bm".equalsIgnoreCase(fileExtension)
					|| "screen".equalsIgnoreCase(fileExtension)
					|| "svc".equalsIgnoreCase(fileExtension);
		}
		return false;
	}

	public String getName() {
		return "Aurora File Rename Participant";
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		check = true;
		RefactoringStatus result = new RefactoringStatus();
		
		boolean refactorStatus = RefactorSettingPreferencePage.getRefactorStatus(RefactorSettingPreferencePage.REFACTOR_SETTING_FILE_RENAME);
		if(refactorStatus == false){
			this.check = false;
			result.merge(RefactoringStatus
					.createInfoStatus("IDE设置修改文件名不启用重构."));
			return result;
		}
		
		String newName = this.getArguments().getNewName();
		if (!newName.toLowerCase().endsWith(fileExtension.toLowerCase())) {
			check = false;
			result.merge(RefactoringStatus.createErrorStatus("文件扩展名错误 : "
					+ newName));
		}

		return result;
	}

	public Change createPreChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		if (check) {
			if ("bm".equalsIgnoreCase(fileExtension)) {
				return createBMChange(pm);
			} else if ("screen".equalsIgnoreCase(fileExtension)
					|| "svc".equalsIgnoreCase(fileExtension)) {
				return createScreenChange(pm);
			}
		}
		return null;
	}

	private Change createScreenChange(IProgressMonitor pm) throws CoreException {
		IResource scope = Util.getScope(currentSourcefile);
		if (scope == null) {
			return null;
		}
		ReferenceSearchService seachService = new ReferenceSearchService(scope,
				currentSourcefile, null);
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
			int offset = object.getOriginalOffset();
			int length = object.getOriginalLength();
			String newName = this.getArguments().getNewName();
			boolean overlapping = changeManager.isOverlapping(file, offset,
					length);
			if (overlapping) {
				continue;
			}
			ReplaceEdit edit = new ReplaceEdit(offset, length, newName);
			textFileChange.addEdit(edit);
		}
		changes.addAll(changeManager.getAllChanges());
		return changes;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		return null;
	}

	private Change createBMChange(IProgressMonitor pm) throws CoreException {
		IResource scope = Util.getScope(currentSourcefile);
		if (scope == null) {
			return null;
		}
		ReferenceSearchService seachService = new ReferenceSearchService(scope,
				currentSourcefile, null);
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
			try {
				String text = document.get(offset, length);
				String oldName = currentSourcefile.getProjectRelativePath()
						.removeFileExtension().lastSegment();
				String _inputName = this.getArguments().getNewName();
				String newName = _inputName.substring(0,
						_inputName.length() - 3);
				newName = text.substring(0, text.length() - oldName.length())
						+ newName;
				boolean overlapping = changeManager.isOverlapping(file, offset,
						length);
				if (overlapping) {
					continue;
				}
				ReplaceEdit edit = new ReplaceEdit(offset, length, newName);
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

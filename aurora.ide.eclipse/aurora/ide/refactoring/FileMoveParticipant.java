package aurora.ide.refactoring;

import java.util.List;

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
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import aurora.ide.preferencepages.RefactorSettingPreferencePage;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.Util;
import aurora.ide.search.reference.ReferenceSearchService;

public class FileMoveParticipant extends MoveParticipant {
	private IFile currentSourcefile;
	private String fileExtension;
	private TextFileChangeManager changeManager;
	private IFolder moveTO;
	private boolean check;

	public FileMoveParticipant() {
	}

	@Override
	protected boolean initialize(Object element) {
		Object destination = this.getArguments().getDestination();
		if (element instanceof IFile && destination instanceof IFolder) {
			this.currentSourcefile = (IFile) element;
			fileExtension = ((IFile) element).getFileExtension();
			changeManager = new TextFileChangeManager();
			moveTO = (IFolder) destination;
			if (currentSourcefile.getParent().equals(moveTO)
					|| !currentSourcefile.getProject().equals(
							moveTO.getProject())) {
				// 目标目录相同，不参与
				// 工程不同不参与
				return false;
			}
			return "bm".equalsIgnoreCase(fileExtension)
					|| "screen".equalsIgnoreCase(fileExtension)
					|| "svc".equalsIgnoreCase(fileExtension);
		}
		return false;
	}

	@Override
	public String getName() {
		return "Aurora File Move Participant";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		this.check = true;
		RefactoringStatus result = new RefactoringStatus();
		
		boolean refactorStatus = RefactorSettingPreferencePage.getRefactorStatus(RefactorSettingPreferencePage.REFACTOR_SETTING_FILE_MOVE);
		if(refactorStatus == false){
			this.check = false;
			result.merge(RefactoringStatus
					.createInfoStatus("IDE设置移动文件不启用重构."));
			return result;
		}
		
		
		Object destination = this.getArguments().getDestination();
		// bm move
		if (destination instanceof IResource
				&& "bm".equalsIgnoreCase(fileExtension)) {
			IPath path = ((IResource) destination).getProjectRelativePath();
			String pkg = Util.toRelativeClassesPKG(path);
			if (pkg.length() == 0) {
				this.check = false;
				result.merge(RefactoringStatus
						.createInfoStatus("目标目录不属于classes,Aurora重构不会进行,请Cancel后重新选择。"));
			}
		}
		// screen move
		if (destination instanceof IResource
				&& ("screen".equalsIgnoreCase(fileExtension) || "svc"
						.equalsIgnoreCase(fileExtension))) {

			IContainer webInf = Util.findWebInf(currentSourcefile);
			if (webInf == null) {
				this.check = false;
				result.merge(RefactoringStatus
						.createInfoStatus("WEB-INF未发现,Aurora重构不会进行,请Cancel后重新选择。"));
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
							.createInfoStatus("目标目录无效,Aurora重构不会进行,请Cancel后重新选择。"));
				}
			}
		}
		return result;
	}

	public Change createPreChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		// bug:select->preview->back->select another->preview
		// getArguments().getDestination() did nont changed
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
			
			try {
				textFileChange.addEdit(createScreenTextEdit(object));
			} catch (MalformedTreeException e) {
				e.printStackTrace();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		changes.addAll(changeManager.getAllChanges());
		return changes;
	}

	private TextEdit createScreenTextEdit(AbstractMatch match)
			throws CoreException, BadLocationException {
		// String fileName = currentSourcefile.getProjectRelativePath()
		// .removeFileExtension().lastSegment();
		IPath newPath = moveTO.getProjectRelativePath().append(
				currentSourcefile.getName());
		return ScreenRefactoring.createMoveTOScreenTextEdit(match, newPath);
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {

		return null;
	}

	private Change createBMChange(IProgressMonitor pm) throws CoreException {
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
			textFileChange.addEdit(createBMTextEdit(object));
		}
		changes.addAll(changeManager.getAllChanges());
		return changes;
	}

	private List getRelations(IProgressMonitor pm) {
		IResource scope = Util.getScope(currentSourcefile);
		if (scope == null) {
			return null;
		}
		ReferenceSearchService seachService = new ReferenceSearchService(scope,
				currentSourcefile, null);
		seachService.setRunInUI(true);
		seachService.setPostException(false);
		List relations = seachService.service(pm);
		return relations;
	}

	public TextEdit createBMTextEdit(AbstractMatch match) throws CoreException {
		int offset = match.getOriginalOffset();
		int length = match.getOriginalLength();
		String fileName = currentSourcefile.getProjectRelativePath()
				.removeFileExtension().lastSegment();
		IPath newPath = this.moveTO.getProjectRelativePath().append(fileName);
		String newPkg = Util.toRelativeClassesPKG(newPath);
		ReplaceEdit edit = new ReplaceEdit(offset, length, newPkg);
		return edit;
	}

	public IDocument getDocument(IFile file) throws CoreException {
		return CacheManager.getDocument(file);
	}

}

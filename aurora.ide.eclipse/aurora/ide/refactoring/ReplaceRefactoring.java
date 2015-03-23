package aurora.ide.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.ReplaceEdit;

public class ReplaceRefactoring extends Refactoring {

	private RefactoringReplaceInfo[] infos;

	private TextFileChangeManager changeManager;

	public ReplaceRefactoring(RefactoringReplaceInfo[] infos) {
		this.infos = infos;
		changeManager = new TextFileChangeManager();
		init(infos);
	}

	private void init(RefactoringReplaceInfo[] lines) {
		// lines[0].g
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {

		CompositeChange changes = new CompositeChange("aurora changes");
		changes.markAsSynthetic();
		for (RefactoringReplaceInfo info : infos) {

			IFile file = info.getFile();
			TextFileChange textFileChange = this.getTextFileChange(file);
			IRegion region = info.getRegion();

			boolean overlapping = changeManager.isOverlapping(file,
					region.getOffset(), region.getLength());
			if (overlapping) {
				continue;
			}
			ReplaceEdit child = new ReplaceEdit(region.getOffset(),
					region.getLength(), info.getReplaceWith());
			try {
				textFileChange.addEdit(child);
			} catch (MalformedTreeException e) {
				// TextEdit.overlapping
				// TextEdit.deleted_edit
				// TextEdit.range_outside
			}

		}

		changes.addAll(changeManager.getAllChanges());

		return changes;
	}

	private TextFileChange getTextFileChange(IFile file) {
		return changeManager.getTextFileChange(file);
	}

	@Override
	public String getName() {
		return "Replace Refactoring";
	}

}

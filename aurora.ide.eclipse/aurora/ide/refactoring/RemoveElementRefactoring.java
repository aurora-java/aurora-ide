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

import aurora.ide.search.core.CompositeMapInDocument;

public class RemoveElementRefactoring extends Refactoring {

	private CompositeMapInDocument[] lines;

	private TextFileChangeManager changeManager;

	// private Map<IFile, TextFileChange> changeMap = new HashMap<IFile,
	// TextFileChange>();

	public RemoveElementRefactoring(CompositeMapInDocument[] lines) {
		this.lines = lines;
		init(lines);
		changeManager = new TextFileChangeManager();
	}

	private void init(CompositeMapInDocument[] lines) {
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
		for (CompositeMapInDocument line : lines) {

			IFile file = line.getFile();
			TextFileChange textFileChange = this.getTextFileChange(file);
			IRegion start = line.getStart();
			IRegion end = line.getEnd();
			int length = end.getOffset() - start.getOffset() + end.getLength();
			boolean overlapping = changeManager.isOverlapping(file,
					start.getOffset(), length);
			if (overlapping) {
				continue;
			}
			ReplaceEdit child = new ReplaceEdit(start.getOffset(), length, "");
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
		return "Remove ELement";
	}

}

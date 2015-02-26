package aurora.sql.java.ide.refactoring;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class MoveSQLJEFileContribution extends RefactoringContribution {

	@Override
	public RefactoringDescriptor createDescriptor(String id, String project,
			String description, String comment, Map arguments, int flags)
			throws IllegalArgumentException {

		RefactoringDescriptor descriptor = new RefactoringDescriptor(id,
				project, description, comment, flags) {

			@Override
			public Refactoring createRefactoring(RefactoringStatus status)
					throws CoreException {
				return new RenameMoveRefactoring();
			}

		};
		return descriptor;

	}

}

package aurora.ide.refactoring.ui;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class AuroraRefactoringWizard extends RefactoringWizard {

	public AuroraRefactoringWizard(Refactoring refactoring, int flags) {
		super(refactoring, flags);
	}

	public AuroraRefactoringWizard(Refactoring refactoring) {
		this(refactoring, WIZARD_BASED_USER_INTERFACE);
	}

	@Override
	protected void addUserInputPages() {
		
	}

}

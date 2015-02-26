package aurora.sql.java.ide.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.refactoring.descriptors.MoveDescriptor;
import org.eclipse.jdt.internal.core.refactoring.descriptors.RefactoringSignatureDescriptorFactory;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveArguments;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;

public class FileMoveParticipant extends MoveParticipant {

	private IJavaElement javaElement;
	private Refactoring createRefactoring;

	public FileMoveParticipant() {
	}

	@Override
	protected boolean initialize(Object element) {
		// if (true)
		// return false;
		System.out.println(element.getClass());
		MoveArguments arguments = this.getArguments();
		Object destination = arguments.getDestination();

		if (element instanceof IFile) {
			IPath javaPath = ((IFile) element).getFullPath()
					.removeFileExtension().addFileExtension("java");
			IWorkspaceRoot root = ((IResource) element).getWorkspace()
					.getRoot();
			IFile file = root.getFile(javaPath);
			if (file.exists()
					&& destination != null
					&& "sqlje".equalsIgnoreCase(((IFile) element)
							.getFileExtension())) {
				javaElement = JavaCore.create(file);
				// ((ICompilationUnit)javaElement).
				MoveDescriptor javaDescriptor = RefactoringSignatureDescriptorFactory
						.createMoveDescriptor();
//				javaDescriptor.setFlags(RenameSupport.UPDATE_REFERENCES);
				if (destination instanceof IJavaElement)
					javaDescriptor.setDestination((IJavaElement) destination);
				if (destination instanceof IResource) {
					javaDescriptor.setDestination((IResource) destination);
				}
//				javaDescriptor.setFlags(flags);
				javaDescriptor.setUpdateReferences(true);
				javaDescriptor.setProject(((IFile) element).getProject()
						.getName());
				javaDescriptor
						.setMoveResources(
								new IFile[] {},
								new IFolder[] {},
								new ICompilationUnit[] { (ICompilationUnit) javaElement });
				try {
					createRefactoring = javaDescriptor
							.createRefactoring(new RefactoringStatus());
					// checkInitialConditions
					return true;
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return "File Move";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		try {
			return createRefactoring.checkAllConditions(pm);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		Change createChange = createRefactoring.createChange(pm);
		createChange.initializeValidationData(pm);
		boolean enabled = createChange.isEnabled();
		RefactoringStatus valid = createChange.isValid(pm);
		System.out.println(enabled + "+++" + valid);
		return createChange;
	}
}

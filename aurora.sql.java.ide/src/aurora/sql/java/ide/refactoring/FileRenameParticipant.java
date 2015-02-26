package aurora.sql.java.ide.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.RenameJavaElementDescriptor;
import org.eclipse.jdt.internal.core.refactoring.descriptors.RefactoringSignatureDescriptorFactory;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import aurora.sql.java.ide.Activator;

public class FileRenameParticipant extends RenameParticipant {

	private IJavaElement javaElement;
	private String newName;
	private Refactoring createRefactoring;

	public FileRenameParticipant() {
	}

	@Override
	protected boolean initialize(Object element) {
		System.out.println(element.getClass());
		RenameArguments arguments = this.getArguments();

		if (element instanceof IFile) {
			IPath javaPath = ((IFile) element).getFullPath()
					.removeFileExtension().addFileExtension("java");
			IWorkspaceRoot root = ((IResource) element).getWorkspace()
					.getRoot();
			IFile file = root.getFile(javaPath);
			newName = arguments.getNewName();
			if (file.exists()
					&& newName.endsWith(".sqlje")
					&& "sqlje".equalsIgnoreCase(((IFile) element)
							.getFileExtension())) {
				javaElement = JavaCore.create(file);
				// ((ICompilationUnit)javaElement).
				newName = newName.substring(0,
						newName.length() - ".sqlje".length());
				RenameJavaElementDescriptor javaDescriptor = RefactoringSignatureDescriptorFactory
						.createRenameJavaElementDescriptor(IJavaRefactorings.RENAME_COMPILATION_UNIT);
				javaDescriptor.setFlags(RenameSupport.UPDATE_REFERENCES);
				javaDescriptor.setNewName(newName);
				javaDescriptor.setUpdateReferences(true);
				javaDescriptor.setProject(((IFile) element).getProject()
						.getName());
				javaDescriptor.setJavaElement(javaElement);
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

	private boolean isEmptySelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection)
					.getFirstElement();
			return firstElement == null;
		}
		return true;
	}

	@Override
	public String getName() {
		return "File Rename";
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

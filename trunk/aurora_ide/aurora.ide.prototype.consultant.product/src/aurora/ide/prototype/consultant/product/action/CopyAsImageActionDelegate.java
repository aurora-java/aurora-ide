package aurora.ide.prototype.consultant.product.action;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.PartEventAction;

import aurora.ide.editor.editorInput.PathEditorInput;
import aurora.ide.prototype.consultant.product.ICommandIds;

public class CopyAsImageActionDelegate extends PartEventAction implements
		IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow fWindow;

	public CopyAsImageActionDelegate(IWorkbenchWindow window, String label) {
		super(label);
		this.fWindow = window;
		setText(label);
		setImageDescriptor(aurora.ide.prototype.consultant.product.Activator
				.getImageDescriptor("/icons/full/obj16/image_obj.gif"));
		this.setToolTipText(label);
		this.setId("aurora.ide.prototype.consultant.product.action.CopyAsImageActionDelegate");
	}

	public void dispose() {
		fWindow = null;
	}

	public void init(IWorkbenchWindow window) {
		fWindow = window;
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	private File queryFile() {
		FileDialog dialog = new FileDialog(fWindow.getShell(), SWT.OPEN);
		dialog.setText("Open File"); //$NON-NLS-1$
		dialog.setFilterExtensions(new String[] { "*.uip" });
		String path = dialog.open();
		if (path != null && path.length() > 0)
			return new File(path);
		return null;
	}

	@Override
	public void run() {
		File file = queryFile();
		if (file != null) {
			IEditorInput input = createEditorInput(file);
			String editorId = getEditorId(file);
			IWorkbenchPage page = fWindow.getActivePage();
			try {
				page.openEditor(input, editorId);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		} else {
			//			MessageDialog.openWarning(fWindow.getShell(), "Problem", "File is 'null'"); //$NON-NLS-1$
		}
	}

	private String getEditorId(File file) {
		IWorkbench workbench = fWindow.getWorkbench();
		IEditorRegistry editorRegistry = workbench.getEditorRegistry();
		IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(file
				.getName());
		if (descriptor != null)
			return descriptor.getId();
		return ICommandIds.EDITOR_ID;
	}

	private IEditorInput createEditorInput(File file) {
		IPath location = new Path(file.getAbsolutePath());
		PathEditorInput input = new PathEditorInput(location);
		return input;
	}
}
package aurora.ide.prototype.consultant.listener;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import aurora.ide.editor.editorInput.PathEditorInput;
import aurora.ide.prototype.consultant.product.ICommandIds;

public class DelayedEventsProcessor implements Listener {
	private ArrayList<String> filesToOpen = new ArrayList<String>(1);

	/**
	 * Constructor.
	 * http://www.eclipse.org/forums/index.php/t/168412/
	 * @param display
	 *            display used as a source of event
	 */
	public DelayedEventsProcessor(Display display) {
		display.addListener(SWT.OpenDocument, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.
	 * Event)
	 */
	public void handleEvent(Event event) {
		final String path = event.text;
		if (path == null)
			return;
		// If we start supporting events that can arrive on a non-UI thread, the
		// following
		// line will need to be in a "synchronized" block:
		System.out.println("handleEvent adding " + path.toString());
		filesToOpen.add(path);
	}

	/**
	 * Process delayed events.
	 * 
	 * @param display
	 *            display associated with the workbench
	 */
	public void catchUp(Display display) {
		if (filesToOpen.isEmpty())
			return;

		// If we start supporting events that can arrive on a non-UI thread, the
		// following
		// lines will need to be in a "synchronized" block:
		String[] filePaths = new String[filesToOpen.size()];
		filesToOpen.toArray(filePaths);
		filesToOpen.clear();

		for (int i = 0; i < filePaths.length; i++) {
			openFile(display, filePaths[i]);
		}
	}

	private void openFile(Display display, final String path) {
		display.asyncExec(new Runnable() {
			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				if (window == null)
					return;

				if (true) {
					openFile(path);
				} else {
					String msg = "File not found: " + path.toString();
					MessageDialog.open(MessageDialog.ERROR, window.getShell(),
							"Initial Open", msg, SWT.SHEET);
				}
			}
		});
	}

	public void openFile(String path) {
		File file = new File(path);
		IEditorInput input = createEditorInput(file);
		String editorId = getEditorId(file);
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		try {
			page.openEditor(input, editorId);
		} catch (PartInitException e) {

			String msg = "Error on open of: " + path;
			MessageDialog.open(MessageDialog.ERROR, window.getShell(),
					"Initial Open", msg, SWT.SHEET);

		}
	}

	private String getEditorId(File file) {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbench workbench = window.getWorkbench();
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

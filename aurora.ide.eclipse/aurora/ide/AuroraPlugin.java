package aurora.ide;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import aurora.ide.helpers.DialogUtil;

/**
 * The activator class controls the plug-in life cycle
 */
public class AuroraPlugin extends AbstractUIPlugin implements
		ISelectionListener {

	// The plug-in ID
	public static final String PLUGIN_ID = "aurora.ide";

	// The shared instance
	private static AuroraPlugin plugin;

	private IStructuredSelection selection;

	/**
	 * The constructor
	 */
	public AuroraPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		IWorkbench workbench = getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

		if (window == null && workbench.getWorkbenchWindows().length > 0)
			window = workbench.getWorkbenchWindows()[0];
		if (window != null) {
			ISelectionService selectionService = window.getSelectionService();

			selectionService.addSelectionListener(this);
		}
//		StartJob sj = new StartJob();
//		sj.schedule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static AuroraPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static void openFileInEditor(IFile file, String id) {
		IWorkbenchWindow iwb = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (iwb == null)
			iwb = PlatformUI.getWorkbench().getWorkbenchWindows()[0];
		IWorkbenchPage wp = iwb.getActivePage();
		if (wp == null)
			return;
		try {
			wp.openEditor(new FileEditorInput(file), id);
		} catch (PartInitException e) {
			DialogUtil.logErrorException(e);
		}
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static IFile getActiveIFile() {
		IWorkbenchPage workbenchPage = getActivePage();
		if (workbenchPage == null)
			return null;
		IEditorPart editorPart = workbenchPage.getActiveEditor();
		if (editorPart == null) {
			return null;
		}
		IEditorInput input = editorPart.getEditorInput();
		if(input instanceof IFileEditorInput){
			IFile ifile = ((IFileEditorInput) input).getFile();
			return ifile;
		}
		return null;
	}

	public static IWorkbenchPage getActivePage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return;
		Object element = ((IStructuredSelection) selection).getFirstElement();
		if (element instanceof IResource) {
			this.selection = (IStructuredSelection) selection;
		}
//		else{
//			selection = null;
//		}
	}
	

	public IStructuredSelection getStructuredSelection() {
		return selection;
	}

	public static void logToStatusLine(String message, boolean isError) {
		WorkbenchWindow workbenchWindow = (WorkbenchWindow) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow();
		IActionBars bars = workbenchWindow.getActionBars();
		IStatusLineManager lineManager = bars.getStatusLineManager();
		if (isError)
			lineManager.setErrorMessage(message);
		else {
			lineManager.setMessage(message);
		}
	}

	public IDialogSettings getDialogSettingsSection(String name) {
		IDialogSettings dialogSettings = getDialogSettings();
		IDialogSettings section = dialogSettings.getSection(name);
		if (section == null) {
			section = dialogSettings.addNewSection(name);
		}
		return section;
	}

	public static InputStream openFileStream(String path) throws IOException {
		return FileLocator.openStream(Platform.getBundle(PLUGIN_ID), new Path(
				path), false);
	}
}

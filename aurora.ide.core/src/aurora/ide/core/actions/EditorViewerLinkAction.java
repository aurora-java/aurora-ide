package aurora.ide.core.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import aurora.ide.core.Activator;
import aurora.ide.core.debug.ITestViewerPart;

public class EditorViewerLinkAction extends Action implements IPropertyListener {

	private IPartListener partListener;

	// private final NavigationView commonNavigator;

	// private final TreeViewer commonViewer;

	// private NodeLinkHelper linkHelper ;

	private boolean ignoreSelectionChanged;
	private boolean ignoreEditorActivation;

	private ViewPart viewerPart;
	private UIJob activateEditorJob = new UIJob(
			"Update viewer info with current editor") {
		public IStatus runInUIThread(IProgressMonitor monitor) {

			// if (!commonViewer.getControl().isDisposed()) {
			// ISelection selection = commonViewer.getSelection();
			// if (selection != null && !selection.isEmpty()
			// && selection instanceof IStructuredSelection) {
			//
			// final IStructuredSelection sSelection = (IStructuredSelection)
			// selection;
			// if (sSelection.size() == 1) {
			// ignoreEditorActivation = true;
			// SafeRunner.run(new SafeRunnable() {
			// public void run() throws Exception {
			// // linkHelper.activateEditor(commonNavigator
			// // .getSite().getPage(), sSelection);
			// }
			// });
			// ignoreEditorActivation = false;
			// }
			// }
			// }
			return Status.OK_STATUS;
		}
	};

	private UIJob updateSelectionJob = new UIJob(
			"Update viewer info with current editor") {
		public IStatus runInUIThread(IProgressMonitor monitor) {

			if (viewerPart instanceof ITestViewerPart) {
				IWorkbenchPage page = viewerPart.getSite().getPage();
				if (page != null) {
					// ignoreSelectionChanged = true;
					IEditorPart activeEditor = page.getActiveEditor();
					((ITestViewerPart) viewerPart).editorChanged(activeEditor);
					// ignoreSelectionChanged = false;
				}
			}
			// if (!commonViewer.getControl().isDisposed()) {
			// SafeRunner.run(new SafeRunnable() {
			// public void run() throws Exception {
			// // IWorkbenchPage page = commonNavigator.getSite()
			// // .getPage();
			// // if (page != null) {
			// // IEditorPart editor = page.getActiveEditor();
			// // if (editor != null) {
			// // IEditorInput input = editor.getEditorInput();
			// // IStructuredSelection newSelection =
			// linkHelper.findSelection(input);
			// // if (!newSelection.isEmpty()) {
			// // ignoreSelectionChanged = true;
			// // commonNavigator.selectReveal(newSelection);
			// // ignoreSelectionChanged = false;
			// // }
			// // }
			// // }
			// }
			// });
			// }

			return Status.OK_STATUS;
		}
	};

	/**
	 * Create a LinkEditorAction for the given navigator and viewer.
	 * 
	 * @param aNavigator
	 *            The navigator which defines whether linking is enabled and
	 *            implements {@link ISetSelectionTarget}.
	 * @param aViewer
	 *            The common viewer instance with a
	 *            {@link INavigatorContentService}.
	 * @param linkHelperService
	 */
	public EditorViewerLinkAction(ViewPart viewerPart) {
		super("&Link with Editor");
		setToolTipText("Link with Editor");
		this.viewerPart = viewerPart;
		// commonNavigator = aNavigator;
		// commonViewer = aViewer;
		// linkHelper = new NodeLinkHelper(commonNavigator);
		init();
	}

	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	protected void init() {
		partListener = new IPartListener() {
			private IWorkbenchPart activepPart;

			public void partActivated(IWorkbenchPart part) {
				if (part instanceof IEditorPart && !ignoreEditorActivation) {
					part.addPropertyListener(EditorViewerLinkAction.this);
					if (part.equals(activepPart))
						return;
					activepPart = part;
					updateSelectionJob.schedule(120);
				}
			}

			public void partBroughtToTop(IWorkbenchPart part) {
				if (part instanceof IEditorPart && !ignoreEditorActivation) {
					// updateSelectionJob.schedule(120);
					// System.out.println("partBroughtToTop");
				}
			}

			public void partClosed(IWorkbenchPart part) {

			}

			public void partDeactivated(IWorkbenchPart part) {
				if (part instanceof IEditorPart && !ignoreEditorActivation) {
					part.removePropertyListener(EditorViewerLinkAction.this);
				}
			}

			public void partOpened(IWorkbenchPart part) {
			}
		};

	}

	public void activate() {
		IPartService partService = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getPartService();
		partService.addPartListener(partListener);
	}

	public void deactivated() {
		IPartService partService = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getPartService();
		partService.removePartListener(partListener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPropertyListener#propertyChanged(java.lang.Object,
	 * int)
	 */
	public void propertyChanged(Object aSource, int aPropertyId) {
		switch (aPropertyId) {
		case IEditorPart.PROP_DIRTY: {
			if (aSource instanceof IEditorPart && !ignoreEditorActivation) {
				IEditorPart p = (IEditorPart) aSource;
				if (p.isDirty() == false)
					updateSelectionJob.schedule(120);
			}
		}
		}
	}

}
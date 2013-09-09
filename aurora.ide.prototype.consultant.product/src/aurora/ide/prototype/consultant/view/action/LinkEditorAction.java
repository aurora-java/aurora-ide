package aurora.ide.prototype.consultant.view.action;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.progress.UIJob;

import aurora.ide.prototype.consultant.view.NavigationView;
import aurora.ide.prototype.consultant.view.NodeLinkHelper;

public class LinkEditorAction extends Action implements
		ISelectionChangedListener, IPropertyListener {

	private IPartListener partListener;

	private final NavigationView commonNavigator;

	private final TreeViewer commonViewer;

	
	private NodeLinkHelper linkHelper ;

	private boolean ignoreSelectionChanged;
	private boolean ignoreEditorActivation;

	private UIJob activateEditorJob = new UIJob("Linking viewer selection with current editor") {
		public IStatus runInUIThread(IProgressMonitor monitor) {

			if (!commonViewer.getControl().isDisposed()) {
				ISelection selection = commonViewer.getSelection();
				if (selection != null && !selection.isEmpty()
						&& selection instanceof IStructuredSelection) {

					final IStructuredSelection sSelection = (IStructuredSelection) selection;
					if (sSelection.size() == 1) {
							ignoreEditorActivation = true;
							SafeRunner.run(new SafeRunnable() {
								public void run() throws Exception {
									linkHelper.activateEditor(commonNavigator
											.getSite().getPage(), sSelection);
								}
							});
							ignoreEditorActivation = false;
						}
				}
			}
			return Status.OK_STATUS;
		}
	};

	private UIJob updateSelectionJob = new UIJob("Linking viewer selection with current editor") {
		public IStatus runInUIThread(IProgressMonitor monitor) {

			if (!commonViewer.getControl().isDisposed()) {
				SafeRunner.run(new SafeRunnable() {
					public void run() throws Exception {
						IWorkbenchPage page = commonNavigator.getSite()
								.getPage();
						if (page != null) {
							IEditorPart editor = page.getActiveEditor();
							if (editor != null) {
								IEditorInput input = editor.getEditorInput();
								IStructuredSelection newSelection = linkHelper.findSelection(input);
								if (!newSelection.isEmpty()) {
									ignoreSelectionChanged = true;
									commonNavigator.selectReveal(newSelection);
									ignoreSelectionChanged = false;
								}
							}
						}
					}
				});
			}

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
	public LinkEditorAction(NavigationView aNavigator, TreeViewer aViewer) {
		super("&Link with Editor");
		setToolTipText("Link with Editor");
		commonNavigator = aNavigator;
		commonViewer = aViewer;
		linkHelper = new NodeLinkHelper(commonNavigator);
		init();
	}

	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	protected void init() {
		partListener = new IPartListener() {

			public void partActivated(IWorkbenchPart part) {
				if (part instanceof IEditorPart && !ignoreEditorActivation) {
					updateSelectionJob
							.schedule(120);
				}
			}

			public void partBroughtToTop(IWorkbenchPart part) {
				if (part instanceof IEditorPart && !ignoreEditorActivation) {
					updateSelectionJob
							.schedule(120);
				}
			}

			public void partClosed(IWorkbenchPart part) {

			}

			public void partDeactivated(IWorkbenchPart part) {
			}

			public void partOpened(IWorkbenchPart part) {
			}
		};

		updateLinkingEnabled(commonNavigator.isLinkingEnabled());

		commonNavigator.addPropertyListener(this);

		// linkHelperRegistry = new
		// LinkHelperManager(commonViewer.getNavigatorContentService());
	}

	/**
	 * 
	 */
	public void dispose() {
		commonNavigator.removePropertyListener(this);
		if (isChecked()) {
			commonViewer.removePostSelectionChangedListener(this);
			commonNavigator.getSite().getPage()
					.removePartListener(partListener);
		}

	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run() {
		commonNavigator.setLinkingEnabled(!commonNavigator.isLinkingEnabled());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedList
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		if (commonNavigator.isLinkingEnabled() && !ignoreSelectionChanged) {
			activateEditor();
		}
	}

	/**
	 * Update the active editor based on the current selection in the Navigator.
	 */
	protected void activateEditor() {
		ISelection selection = commonViewer.getSelection();
		if (selection != null && !selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			/*
			 * Create and schedule a UI Job to activate the editor in a valid
			 * Display thread
			 */
			activateEditorJob.schedule(120);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPropertyListener#propertyChanged(java.lang.Object,
	 * int)
	 */
	public void propertyChanged(Object aSource, int aPropertyId) {
		switch (aPropertyId) {
		case NavigationView.IS_LINKING_ENABLED_PROPERTY:
			updateLinkingEnabled(((NavigationView) aSource).isLinkingEnabled());
		}
	}

	/**
	 * @param toEnableLinking
	 */
	private void updateLinkingEnabled(boolean toEnableLinking) {
		setChecked(toEnableLinking);

		if (toEnableLinking) {

			updateSelectionJob.schedule(120);

			commonViewer.addPostSelectionChangedListener(this);
			commonNavigator.getSite().getPage().addPartListener(partListener);
		} else {
			commonViewer.removePostSelectionChangedListener(this);
			commonNavigator.getSite().getPage()
					.removePartListener(partListener);
		}
	}

}

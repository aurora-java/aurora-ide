package aurora.ide.search.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.AuroraPlugin;
import aurora.ide.refactoring.ui.AuroraRefactoringWizard;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.AuroraSearchResult;
import aurora.ide.search.core.Message;
import aurora.ide.search.ui.LineElement;
import aurora.ide.search.ui.MessageFormater;

abstract public class AbstractSearchResultPageAction implements
		ISearchResultPageAction {
	private TreeSelection treeSelection;
	private TreeViewer treeViewer;
	private Shell shell;
	private int lastSize;
	private List<IActionChangedListener> listeners;

	public void addActionChangedListener(IActionChangedListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<IActionChangedListener>();
		}
		listeners.add(listener);
	}

	protected void notifyActionChanged() {
		if (listeners != null) {
			for (IActionChangedListener l : listeners) {
				l.actionChanged(this);
			}
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
		treeSelection = null;
		treeViewer = null;
		ISelection selection = event.getSelection();
		if (selection instanceof TreeSelection) {
			treeSelection = (TreeSelection) selection;
		}
		Object source = event.getSource();
		if (source instanceof TreeViewer) {
			treeViewer = (TreeViewer) source;
		}
	}

	public AbstractSearchResultPageAction(Shell shell) {
		super();
		this.shell = shell;
	}

	protected void excuteRefactoring(final Refactoring refactor) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				AuroraRefactoringWizard wizard = new AuroraRefactoringWizard(
						refactor);
				RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(
						wizard);
				try {
					op.run(shell, "Aurora Refactoring");
				} catch (InterruptedException e) {
				}
			}
		});

	}

	public boolean isRefactorSelectionEnabled() {
		boolean isEmpty = treeSelection != null && !treeSelection.isEmpty();
		if (isEmpty) {
			TreeSelection treeSelection = this.getTreeSelection();
			for (Iterator iterator = treeSelection.iterator(); iterator
					.hasNext();) {
				Object next = iterator.next();
				if (!(next instanceof LineElement)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean isRefactorAllEnabled() {

		return getSearchResult() != null;
	}

	public void runAll() {
		try {
			AuroraPlugin.getDefault().getWorkbench().getProgressService()
					.busyCursorWhile(new IRunnableWithProgress() {

						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								runAllInThread(monitor);
							} finally {
								monitor.done();
							}
						}
					});
		} catch (InvocationTargetException e) {
		} catch (InterruptedException e) {
		}
	}

	protected void runAllInThread(IProgressMonitor monitor) {
		if (isRefactorAllEnabled()) {
			monitor.beginTask("scaning changes count", IProgressMonitor.UNKNOWN);
			final List<LineElement> lines = geAllLineElement();
			this.runInThread(lines, monitor);
		}
	}

	protected void runSelectionInThread(IProgressMonitor monitor) {
		if (isRefactorSelectionEnabled()) {
			monitor.beginTask("scaning changes count", IProgressMonitor.UNKNOWN);
			List lines = getSelectionLineElement();
			runInThread(lines, monitor);
		}
	}

	public void runSelection() {
		try {
			AuroraPlugin.getDefault().getWorkbench().getProgressService()
					.busyCursorWhile(new IRunnableWithProgress() {

						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								runSelectionInThread(monitor);
							} finally {
								monitor.done();
							}
						}
					});
		} catch (InvocationTargetException e) {
		} catch (InterruptedException e) {
		}
	}

	public TreeSelection getTreeSelection() {
		return treeSelection;
	}

	public void setTreeSelection(TreeSelection treeSelection) {
		this.treeSelection = treeSelection;
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	public AuroraSearchResult getSearchResult() {
		if (null != treeViewer) {
			Object input = treeViewer.getInput();
			if (input instanceof AuroraSearchResult)
				return (AuroraSearchResult) input;
		}
		return null;
	}

	public Shell getShell() {
		return shell;
	}

	public void setShell(Shell shell) {
		this.shell = shell;
	}

	protected List<LineElement> geAllLineElement() {
		List<LineElement> lines = new ArrayList<LineElement>();
		AuroraSearchResult searchResult = this.getSearchResult();
		Object[] elements = searchResult.getElements();
		for (Object o : elements) {
			Match[] matches = searchResult.getMatches(o);
			for (Match m : matches) {
				if (m instanceof AbstractMatch) {
					LineElement lineElement = ((AbstractMatch) m)
							.getLineElement();
					if (lineElement != null && !lines.contains(lineElement)) {
						lines.add(lineElement);
					}
				}
			}
		}
		return lines;
	}

	protected IDocument getDocument(IFile file) throws CoreException {
		return CacheManager.getDocument(file);
	}

	protected List getSelectionLineElement() {
		TreeSelection treeSelection = this.getTreeSelection();
		return treeSelection.toList();
	}

	protected void runInThread(final List lines, final IProgressMonitor monitor) {
		lastSize = 0;
		Job monitorUpdateJob = new Job("Aurora Search progress") {
			public IStatus run(IProgressMonitor inner) {
				monitor.beginTask("scaning changes", lines.size());
				while (!inner.isCanceled()) {
					int infoSize = getInfoSize();
					if (infoSize != 0) {
						monitor.worked(infoSize - lastSize);
						lastSize = infoSize;
						Object[] args = { getSubTaskName(),
								new Integer(lastSize),
								new Integer(lines.size()) };
						monitor.subTask(MessageFormater.format(
								Message._scanning, args));
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						return Status.OK_STATUS;
					}
				}
				return Status.OK_STATUS;
			}
		};
		monitorUpdateJob.setSystem(true);
		monitorUpdateJob.schedule();
		try {

			Refactoring refactor = createRefactoring(lines, monitor);
			this.excuteRefactoring(refactor);
		} finally {
			monitorUpdateJob.cancel();
		}
	}

	abstract protected int getInfoSize();

	abstract protected String getSubTaskName();

	abstract protected Refactoring createRefactoring(List lines,
			IProgressMonitor monitor);

}

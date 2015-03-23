package aurora.ide.search.ui;

import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import aurora.ide.search.core.AbstractMatch;

public class SearchReferenceResultPage extends AbstractTextSearchViewPage {

	private static final int DEFAULT_ELEMENT_LIMIT = 1000;
	private static final String limited_format_matches = "{0} (showing {1} of {2} matches)";
	private static final String limited_format_files = "{0} (showing {1} of {2} files)";
	private EditorOpener opener = new EditorOpener();

	protected void elementsChanged(Object[] objects) {
		provider.elementsChanged(objects);
	}

	public static class DecoratorIgnoringViewerSorter extends ViewerComparator {
		private final ILabelProvider fLabelProvider;

		public DecoratorIgnoringViewerSorter(ILabelProvider labelProvider) {
			fLabelProvider = labelProvider;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
		 */
		public int category(Object element) {
			if (element instanceof IContainer) {
				return 1;
			}
			return 2;
		}

		public int compare(Viewer viewer, Object e1, Object e2) {
			int cat1 = category(e1);
			int cat2 = category(e2);

			if (cat1 != cat2) {
				return cat1 - cat2;
			}

			if (e1 instanceof LineElement && e2 instanceof LineElement) {
				LineElement m1 = (LineElement) e1;
				LineElement m2 = (LineElement) e2;
				return m1.getOffset() - m2.getOffset();
			}

			String name1 = fLabelProvider.getText(e1);
			String name2 = fLabelProvider.getText(e2);
			if (name1 == null)
				name1 = "";//$NON-NLS-1$
			if (name2 == null)
				name2 = "";//$NON-NLS-1$
			return getComparator().compare(name1, name2);
		}
	}

	protected void evaluateChangedElements(Match[] matches, Set changedElements) {
		if (showLineMatches()) {
			for (int i = 0; i < matches.length; i++) {
				changedElements.add(((AbstractMatch) matches[i])
						.getLineElement());
			}
		} else {
			super.evaluateChangedElements(matches, changedElements);
		}
	}

	protected void clear() {
		provider.clear();

	}

	public SearchReferenceResultPage() {
		setElementLimit(new Integer(DEFAULT_ELEMENT_LIMIT));

	}

	private ISearchContentProvider provider;

	protected void configureTreeViewer(TreeViewer viewer) {
		TreeContentProvider provider = new TreeContentProvider(this, viewer);

		SearchLabelProvider lp = new SearchLabelProvider(this,
				SearchLabelProvider.SHOW_LABEL);
		viewer.setLabelProvider(new DecoratingSearchLabelProvider(lp));
		viewer.setComparator(new DecoratorIgnoringViewerSorter(lp));
		viewer.setContentProvider(provider);
		this.provider = provider;
	}

	protected void configureTableViewer(TableViewer viewer) {
		SearchLabelProvider lp = new SearchLabelProvider(this,
				SearchLabelProvider.SHOW_LABEL_PATH);
		viewer.setLabelProvider(new DecoratingSearchLabelProvider(lp));
		viewer.setComparator(new DecoratorIgnoringViewerSorter(lp));
		TableContentProvider provider = new TableContentProvider(this, viewer);
		viewer.setContentProvider(provider);
		this.provider = provider;
	}

	protected void showMatch(Match match, int offset, int length,
			boolean activate) throws PartInitException {
		IFile file = (IFile) match.getElement();
		IWorkbenchPage page = getSite().getPage();
		if (offset >= 0 && length != 0) {
			// openAndSelect(page, file, offset, length, activate);
			opener.openAndSelect(page, file, offset, length, activate);

		} else {
			// open(page, file, activate);
			opener.open(page, file, activate);
		}
	}

	protected void handleOpen(OpenEvent event) {
		if (showLineMatches()) {
			Object firstElement = ((IStructuredSelection) event.getSelection())
					.getFirstElement();
			if (firstElement instanceof IFile) {
				if (getDisplayedMatchCount(firstElement) == 0) {
					try {
//						open(getSite().getPage(), (IFile) firstElement, false);
						opener.open(getSite().getPage(), (IFile) firstElement, false);
					} catch (PartInitException e) {
						ErrorDialog.openError(getSite().getShell(),
								"Open File", "Opening the file failed.",
								e.getStatus());
					}
					return;
				}
			}
		}
		super.handleOpen(event);
	}

	public String getLabel() {
		String label = super.getLabel();
		StructuredViewer viewer = getViewer();
		if (viewer instanceof TableViewer) {
			TableViewer tv = (TableViewer) viewer;

			AbstractTextSearchResult result = getInput();
			if (result != null) {
				int itemCount = ((IStructuredContentProvider) tv
						.getContentProvider()).getElements(getInput()).length;
				if (showLineMatches()) {
					int matchCount = getInput().getMatchCount();
					if (itemCount < matchCount) {
						return MessageFormater.format(limited_format_matches,
								new Object[] { label, new Integer(itemCount),
										new Integer(matchCount) });
					}
				} else {
					int fileCount = getInput().getElements().length;
					if (itemCount < fileCount) {
						return MessageFormater.format(limited_format_files,
								new Object[] { label, new Integer(itemCount),
										new Integer(fileCount) });
					}
				}
			}
		}
		return label;
	}

	public int getDisplayedMatchCount(Object element) {
		if (showLineMatches()) {
			if (element instanceof LineElement) {
				LineElement lineEntry = (LineElement) element;
				return lineEntry.getNumberOfMatches(getInput());
			}
			return 0;
		}
		return super.getDisplayedMatchCount(element);
	}

	public Match[] getDisplayedMatches(Object element) {
		if (showLineMatches()) {
			if (element instanceof LineElement) {
				LineElement lineEntry = (LineElement) element;
				return lineEntry.getMatches(getInput());
			}
			return new Match[0];
		}
		return super.getDisplayedMatches(element);
	}

	private boolean showLineMatches() {
		return getLayout() == FLAG_LAYOUT_TREE && getInput() != null;
	}

}

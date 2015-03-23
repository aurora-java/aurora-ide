package aurora.ide.search.ui;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;

public class TableContentProvider implements IStructuredContentProvider,
		ISearchContentProvider {

	private final Object[] EMPTY_ARR = new Object[0];

	private AbstractTextSearchViewPage fPage;
	private AbstractTextSearchResult fResult;

	private TableViewer viewer;

	public TableContentProvider(AbstractTextSearchViewPage page,
			TableViewer viewer) {
		fPage = page;
		this.viewer = viewer;
	}

	public void dispose() {
		// nothing to do
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof AbstractTextSearchResult) {
			int elementLimit = getElementLimit();
			Object[] elements = ((AbstractTextSearchResult) inputElement)
					.getElements();
			if (elementLimit != -1 && elements.length > elementLimit) {
				Object[] shownElements = new Object[elementLimit];
				System.arraycopy(elements, 0, shownElements, 0, elementLimit);
				return shownElements;
			}
			return elements;
		}
		return EMPTY_ARR;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof AbstractTextSearchResult) {
			fResult = (AbstractTextSearchResult) newInput;
		}
	}

	public void elementsChanged(Object[] updatedElements) {
		TableViewer viewer = getViewer();
		int elementLimit = getElementLimit();
		boolean tableLimited = elementLimit != -1;
		for (int i = 0; i < updatedElements.length; i++) {
			if (fResult.getMatchCount(updatedElements[i]) > 0) {
				if (viewer.testFindItem(updatedElements[i]) != null)
					viewer.update(updatedElements[i], null);
				else {
					if (!tableLimited
							|| viewer.getTable().getItemCount() < elementLimit)
						viewer.add(updatedElements[i]);
				}
			} else
				viewer.remove(updatedElements[i]);
		}
	}

	private int getElementLimit() {
		Integer elementLimit = fPage == null ? null : fPage.getElementLimit();
		if (elementLimit == null)
			return -1;
		return elementLimit.intValue();
	}

	private TableViewer getViewer() {
		return this.viewer;
	}

	public void clear() {
		getViewer().refresh();
	}
}

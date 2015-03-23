package aurora.ide.search.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

public abstract class AbstractSearchQuery implements ISearchQuery {

	final public ISearchResult getSearchResult() {

		return getAruroraSearchResult();
	}

	public IStatus run(IProgressMonitor monitor)
			throws OperationCanceledException {
		AbstractTextSearchResult textResult = (AbstractTextSearchResult) getSearchResult();
		textResult.removeAll();
		setSearchService(null);
		SearchEngine engine = new SearchEngine(this);
		engine.execute(monitor);
		return Status.OK_STATUS;
	}

	protected abstract void setSearchService(ISearchService service);

	protected abstract AbstractSearchResult getAruroraSearchResult();

	protected abstract ISearchService getSearchService();

	protected abstract IResource[] getRoots();

	protected abstract IResource getSourceFile();

	protected abstract Object getPattern();
	
	protected abstract String getSearchInLabel();

}

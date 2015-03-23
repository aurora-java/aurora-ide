package aurora.ide.search.core;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

public interface ISearchService {
	List<AbstractMatch> service(IProgressMonitor monitor);
}

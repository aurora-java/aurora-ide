package aurora.ide.search.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.search.ui.text.FileTextSearchScope;

import aurora.ide.search.condition.AuroraSearchPattern;

public class AuroraSearchQuery extends AbstractSearchQuery {

	private AuroraSearchResult fResult;
	private IResource[] roots;

	private ISearchService service;
	private AuroraSearchPattern pattern;

	public AuroraSearchQuery(IResource[] roots, AuroraSearchPattern pattern) {
		super();
		this.roots = roots;
		this.pattern = pattern;
	}

	public AuroraSearchQuery(FileTextSearchScope createTextSearchScope,
			AuroraSearchPattern pattern) {
		this(createTextSearchScope.getRoots(), pattern);
	}

	public IStatus run(IProgressMonitor monitor)
			throws OperationCanceledException {
		return super.run(monitor);
	}

	public String getLabel() {
		return "Aurora Search Query";
	}

	public boolean canRerun() {
		return true;
	}

	public boolean canRunInBackground() {
		return true;
	}

	protected AbstractSearchResult getAruroraSearchResult() {
		if (fResult == null) {
			AuroraSearchResult result = new AuroraSearchResult(this);
			fResult = result;
		}
		return fResult;
	}

	protected ISearchService getSearchService() {
		if (service == null)
			service = new AuroraSearchService(this.roots, pattern, this);
		return service;
	}

	protected void setSearchService(ISearchService service) {
		this.service = service;
	}

	protected IResource[] getRoots() {
		return this.roots;
	}

	protected IResource getSourceFile() {
		return null;
	}

	protected Object getPattern() {
		return service == null ? null : ((AbstractSearchService) service)
				.getSearchPattern(roots, null);
	}

	@Override
	protected String getSearchInLabel() {
		StringBuilder builder = new StringBuilder();
		if (roots != null) {
			for (IResource root : roots) {
				if (root.getType() == IResource.ROOT) {
					builder.append("workspace");
				} else {
					builder.append(root.getName());

				}
				builder.append(",");
			}
			int lastIndexOf = builder.lastIndexOf(",");
			if (-1 != lastIndexOf)
				builder.deleteCharAt(lastIndexOf);
		}
		return builder.toString();
	}
}

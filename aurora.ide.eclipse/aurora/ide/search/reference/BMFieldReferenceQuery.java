package aurora.ide.search.reference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import aurora.ide.search.core.AbstractSearchQuery;
import aurora.ide.search.core.AbstractSearchResult;
import aurora.ide.search.core.AbstractSearchService;
import aurora.ide.search.core.AuroraSearchReferenceResult;
import aurora.ide.search.core.ISearchService;

public class BMFieldReferenceQuery extends AbstractSearchQuery {

	private AuroraSearchReferenceResult fResult;
	private IResource scope;
	private IFile sourceFile;
	private String fieldName;
	private ISearchService service;

	public BMFieldReferenceQuery(IResource scope, IFile sourceFile,
			String fieldName) {
		super();
		this.scope = scope;
		this.sourceFile = sourceFile;
		this.fieldName = fieldName;
	}

	public String getLabel() {
		return "BM Reference : " + sourceFile.getName();
	}

	public boolean canRerun() {
		return true;
	}

	public boolean canRunInBackground() {
		return true;
	}

	protected AbstractSearchResult getAruroraSearchResult() {
		if (fResult == null) {
			AuroraSearchReferenceResult result = new AuroraSearchReferenceResult(this);
			fResult = result;
		}
		return fResult;
	}

	protected ISearchService getSearchService() {
		if (service == null)
			service = new BMFieldReferenceService(this.scope, this.sourceFile,
					this, this.fieldName);
		return service;
	}

	protected void setSearchService(ISearchService service) {
		this.service = service;
	}

	protected IResource getScope() {
		return scope;
	}

	protected IResource getSourceFile() {
		return sourceFile;
	}

	protected Object getPattern() {

		return service == null ? null : ((AbstractSearchService) service)
				.getSearchPattern(new IResource[] { scope }, sourceFile);
	}

	@Override
	protected IResource[] getRoots() {
		return new IResource[] { scope };
	}

	@Override
	protected String getSearchInLabel() {
		return scope.getProject().getName();
	}

}

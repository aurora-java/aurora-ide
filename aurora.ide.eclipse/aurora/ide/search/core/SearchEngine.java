package aurora.ide.search.core;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

public class SearchEngine {

	private AbstractSearchQuery query;

	public SearchEngine() {

	}

	public SearchEngine(AbstractSearchQuery query) {
		this.query = query;
	}

	public List<AbstractMatch> execute(IProgressMonitor monitor) {
		ISearchService searchService = query.getSearchService();
		return searchService.service(monitor);
	}

//	public IStatus findReference(IResource scope, IFile sourceFile,
//			IProgressMonitor monitor) {
//		ReferenceSearchService service = new ReferenceSearchService(scope,
//				sourceFile, query);
//		service.service(monitor);
//		return Status.OK_STATUS;
//	}
//
//	public IStatus findBMFieldReference(IResource scope, IFile sourceFile,
//			String fieldName, IProgressMonitor monitor) {
//		ReferenceSearchService service = new BMFieldReferenceService(scope,
//				sourceFile, query, fieldName);
//		service.service(monitor);
//		return Status.OK_STATUS;
//	}
//
//	public IStatus findDSReference(IResource scope, IFile sourceFile,
//			String datasetName, IProgressMonitor monitor) {
//		ScreenDSReferenceService service = new ScreenDSReferenceService(scope,
//				sourceFile, query, datasetName);
//		service.service(monitor);
//		return Status.OK_STATUS;
//	}

}

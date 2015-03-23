package aurora.ide.javascript.search;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;

import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.CompositeMapIteator;
import aurora.ide.search.reference.MapFinderResult;

public class JavascriptSearchService {
	public static final String SCRIPT = "script";

	public Collection<? extends AbstractMatch> buildMatchLines(IFile file,
			List<MapFinderResult> r, Object pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addSource(IFile source) {
		// TODO Auto-generated method stub
		
	}

	public CompositeMapIteator createIterationHandle(IFile resource) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IFile> getSources() {
		// TODO Auto-generated method stub
		return null;
	}
}

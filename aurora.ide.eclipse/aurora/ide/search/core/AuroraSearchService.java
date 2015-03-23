package aurora.ide.search.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import aurora.ide.search.condition.AuroraSearchPattern;
import aurora.ide.search.condition.MatchInfo;
import aurora.ide.search.reference.IDataFilter;
import aurora.ide.search.reference.MapFinderResult;
import aurora.ide.search.ui.LineElement;

public class AuroraSearchService extends AbstractSearchService {

	private AuroraSearchPattern pattern;

	public AuroraSearchService(IResource[] roots, AuroraSearchPattern pattern) {
		super(roots, null);
		this.pattern = pattern;
	}

	public AuroraSearchService(IResource[] roots, AuroraSearchPattern pattern,
			AuroraSearchQuery auroraSearchQuery) {
		super(roots, null, auroraSearchQuery);
		this.pattern = pattern;
	}

	@Override
	protected CompositeMapIteator createIterationHandle(IFile resource) {
		return new SearchForMapIteator(pattern.getSearchFor());
	}

	@Override
	protected IDataFilter getDataFilter(IResource[] roots, Object source) {
		return null;
	}

	@Override
	protected List<AbstractMatch> createLineMatches(MapFinderResult r, LineElement l,
			IFile file, Object _pattern) throws CoreException {
		List<AbstractMatch> matches = new ArrayList<AbstractMatch>();
		CompositeMap map = r.getMap();
		IDocument document = (IDocument) getDocument(file);
		MatchInfo matchInfo = pattern.match(map, document, l);
		if (matchInfo.isMatch()) {
			List<IRegion> regions = matchInfo.getRegions();
			for (IRegion region : regions) {
				AuroraMatch match = new AuroraMatch(file, region.getOffset(),
						region.getLength(), l);
				match.setMatchs(r);
				matches.add(match);
			}
		}
		return matches;
	}

	@Override
	protected Object createPattern(IResource[] roots, Object source) {
		return "Aurora Search";
	}

}

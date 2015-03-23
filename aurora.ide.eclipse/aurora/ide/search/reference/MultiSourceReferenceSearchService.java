package aurora.ide.search.reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.search.ui.ISearchQuery;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.CompositeMapIteator;
import aurora.ide.search.ui.LineElement;

public class MultiSourceReferenceSearchService extends ReferenceSearchService {

	private List<String> patterns;

	private Map<CompositeMap, List<String>> patternMap = new HashMap<CompositeMap, List<String>>();

	private boolean isBM;

	private IFile[] sources;

	public MultiSourceReferenceSearchService(IResource scope, IFile[] sources,
			ISearchQuery query, boolean isBM) {
		super(scope, null, query);
		this.sources = sources;
		this.patterns = this.createPatterns(this.getRoots(), sources);
		this.isBM = isBM;
		this.getJsService().getSources().addAll(Arrays.asList(sources));
	}

	private List<String> createPatterns(IResource[] roots, IFile[] sources) {
		List<String> patterns = new ArrayList<String>();
		for (IFile source : sources) {
			Object p = this.createPattern(roots, source);
			if (p instanceof String) {
				patterns.add((String) p);
			}
		}
		return patterns;
	}

	@Override
	protected CompositeMapIteator createIterationHandle(IFile file) {
		return isBM ? new MultiReferenceTypeFinder(bmReference)
				.addReferenceType(urlReference) : new MultiReferenceTypeFinder(
				screenReference).addReferenceType(urlReference);
	}

	@Override
	protected boolean bmRefMatch(CompositeMap map, Attribute attrib) {
		for (String s : patterns) {
			boolean contains = super.bmRefMatch(map, attrib, s);
			if (contains) {
				putInPatternMap(map, s);
				return true;
			}
		}
		return false;
	}

	private void putInPatternMap(CompositeMap map, String s) {
		List<String> list = patternMap.get(map);
		if (list == null) {
			list = new ArrayList<String>();
		}
		if (!list.contains(s)) {
			list.add(s);
		}
		patternMap.put(map, list);
	}

	@Override
	protected boolean screenRefMatch(CompositeMap map, Attribute attrib) {
		IFile findScreenFile = this.findScreenFile(map, attrib);
		int indexOf = Arrays.asList(sources).indexOf(findScreenFile);

		if (indexOf != -1) {
			putInPatternMap(map,
					createPattern(this.getRoots(), sources[indexOf]).toString());
			return true;
		}
		return false;
	}

	@Override
	public boolean found(CompositeMap map, Attribute attrib) {
		if (attrib == null) {
			return false;
		}
		return isBM ? bmRefMatch(map, attrib) : this
				.screenRefMatch(map, attrib);
	}

	@Override
	protected List<AbstractMatch> createLineMatches(MapFinderResult r,
			LineElement l, IFile file, Object pattern) throws CoreException {
		List<String> ps = this.patternMap.get(r.getMap());
		if (ps != null) {
			List<AbstractMatch> result = new ArrayList<AbstractMatch>();
			for (String s : ps) {
				result.addAll(super.createLineMatches(r, l, file, s));
			}
			return result;
		}
		return Collections.emptyList();
	}

	@Override
	protected Object createPattern(IResource[] roots, Object source) {
		if (source == null) {
			return "Aurora Multi-References";
		}
		return super.createPattern(roots, source);
	}

}

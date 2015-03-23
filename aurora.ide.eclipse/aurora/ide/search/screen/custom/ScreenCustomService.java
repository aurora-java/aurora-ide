package aurora.ide.search.screen.custom;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.AbstractSearchService;
import aurora.ide.search.core.CompositeMapIteator;
import aurora.ide.search.core.CompositeMapMatch;
import aurora.ide.search.core.Util;
import aurora.ide.search.reference.IDataFilter;
import aurora.ide.search.reference.MapFinderResult;
import aurora.ide.search.reference.ReferenceTypeFinder;
import aurora.ide.search.ui.LineElement;

public class ScreenCustomService extends AbstractSearchService {

	public ScreenCustomService(IResource scope) {
		super(new IResource[] { scope }, null, null);
	}

	protected CompositeMapIteator createIterationHandle(IFile resource) {
		return new ReferenceTypeFinder(datasetReference);
	}

	protected IDataFilter getDataFilter(IResource[] roots, Object source) {
		IDataFilter filter = new IDataFilter() {
			public boolean found(CompositeMap map, Attribute attrib) {
				Object object = map.get("name");
				return "bindTarget".equalsIgnoreCase(attrib.getName()) && object != null
						&& !"".equals(object) && map.get("id") == null;
			}

		};
		return filter;
	}

	protected Object createPattern(IResource[] roots, Object source) {
		return "";
	}

	@Override
	protected List<AbstractMatch> createLineMatches(MapFinderResult r, LineElement l,
			IFile file, Object pattern) throws CoreException {
		IDocument document = getDocument(file);
		int startOffset = l.getOffset();
		List<AbstractMatch> matches = new ArrayList<AbstractMatch>();
		try {
			IRegion whitespaceRegion = Util.getFirstWhitespaceRegion(
					startOffset, l.getLength(), document);
			if (whitespaceRegion == null) {
				IRegion attributeRegion = Util.getAttributeRegion(startOffset,
						l.getLength(), "bindTarget", document);
				whitespaceRegion = new Region(attributeRegion.getOffset() - 1,
						0);
			}

			CompositeMapMatch match = new CompositeMapMatch(file,
					whitespaceRegion.getOffset(), whitespaceRegion.getLength(),
					r.getMap(), l);
			matches.add(match);
		} catch (BadLocationException e) {
		}
		return matches;
	}
}

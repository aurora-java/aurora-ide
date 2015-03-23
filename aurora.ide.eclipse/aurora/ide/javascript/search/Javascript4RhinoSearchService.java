package aurora.ide.javascript.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.mozilla.javascript.ast.StringLiteral;

import uncertain.composite.CompositeMap;
import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;
import aurora.ide.javascript.Javascript4Rhino;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractMatch;
import aurora.ide.search.core.AuroraMatch;
import aurora.ide.search.core.CompositeMapIteator;
import aurora.ide.search.core.Util;
import aurora.ide.search.reference.MapFinderResult;
import aurora.ide.search.reference.NamedMapFinder;
import aurora.ide.search.ui.LineElement;

public class Javascript4RhinoSearchService extends JavascriptSearchService {
	private List<LineElement> lines = new LinkedList<LineElement>();

	private List<IFile> sources;

	public void setSources(List<IFile> sources) {
		this.sources = sources;
	}

	public List<IFile> getSources() {
		return sources == null ? sources = new ArrayList<IFile>() : sources;
	}

	public void addSource(IFile source) {
		this.getSources().add(source);
	}

	public CompositeMapIteator createIterationHandle(IFile file) {
		return new NamedMapFinder(SCRIPT);
	}

	public Collection<? extends AbstractMatch> buildMatchLines(IFile file,
			List<MapFinderResult> r, Object pattern) {
		return buildMatchLines(file, r);
	}

	private Collection<? extends AbstractMatch> buildMatchLines(IFile file,
			List<MapFinderResult> r) {
		List<Javascript4Rhino> js = new ArrayList<Javascript4Rhino>();
		for (MapFinderResult result : r) {
			CompositeMap map = result.getMap();
			if (SCRIPT.equalsIgnoreCase(map.getName()) && map.getText() != null) {
				js.add(new Javascript4Rhino(file,map));
			}
		}
		return buildMatchLines(file,
				js.toArray(new Javascript4Rhino[js.size()]));
	}

	private Collection<? extends AbstractMatch> buildMatchLines(IFile file,
			Javascript4Rhino[] js) {
		List<AbstractMatch> matchs = new ArrayList<AbstractMatch>();
		for (Javascript4Rhino j : js) {
			matchs.addAll(buildMatchLines(file, j));
		}

		return matchs;
	}

	private List<AbstractMatch> buildMatchLines(IFile file, Javascript4Rhino j) {
		List<AbstractMatch> matchs = new ArrayList<AbstractMatch>();
		List<StringLiteral> stringLiteralNodes = j.getStringLiteralNodes(null);
		for (StringLiteral sl : stringLiteralNodes) {
			String literalString = j.getLiteralValue(sl);
			String pattern = isMatch(file, literalString);
			if (pattern == null)
				continue;

			int mapStartLine = j.getMap().getLocation().getStartLine();
			int ln = sl.getLineno() + mapStartLine - 1;
			String content = "";
			IRegion lineInformation = null;
			IDocument document = null;
			ITypedRegion jsRegion = null;
			try {
				document = CacheManager.getDocument(file);
				jsRegion = findJavascriptPartitioning(document, j.getMap());
				if (jsRegion == null) {
					continue;
				}
				lineInformation = document.getLineInformation(ln - 1);
				content = document.get(lineInformation.getOffset(),
						lineInformation.getLength());
				LineElement l = getLineElement(file, ln,
						lineInformation.getOffset(), content);

				int valueOffset;

				valueOffset = jsRegion.getOffset() + sl.getAbsolutePosition()
						+ "<![CDATA[".length() + +1
						+ literalString.indexOf(pattern);
				AuroraMatch match = new AuroraMatch(file, valueOffset,
						pattern.length(), l);
				matchs.add(match);
			} catch (CoreException e) {
				continue;
			} catch (BadLocationException e) {
				continue;
			}

		}
		return matchs;
	}

	private ITypedRegion findJavascriptPartitioning(IDocument document,
			CompositeMap map) throws BadLocationException {
		int mapStartLine = map.getLocation().getStartLine();
		int mapEndLine = map.getLocation().getEndLine();
		ITypedRegion[] computePartitioning = document.computePartitioning(
				document.getLineOffset(mapStartLine - 1),
				document.getLineOffset(mapEndLine - 1)
						- document.getLineOffset(mapStartLine - 1));
		if (computePartitioning != null) {
			for (ITypedRegion r : computePartitioning) {
				if (XMLPartitionScanner.XML_CDATA.equals(r.getType())) {
					return r;
				}
			}
		}
		return null;

	}

	private String isMatch(IFile file, String subString) {
		IFile findScreenFile = Util.findScreenFile(file, subString);
		boolean isScreen = this.getSources().contains(findScreenFile);
		if (isScreen) {
			return (String) this.createPattern(null, findScreenFile);
		}
		for (IFile f : this.getSources()) {
			if ("bm".equalsIgnoreCase(f.getFileExtension())) {
				Object createPattern = this.createPattern(null, f);
				boolean bmRefMatch = Util.bmRefMatch(createPattern, subString);
				if (bmRefMatch) {
					return createPattern.toString();
				}
			}
		}
		return null;
	}

	private LineElement getLineElement(IFile file, int ln, int offset,
			String content) {
		for (LineElement l : lines) {
			if (file.equals(l.getParent()) && l.getLine() == ln)
				return l;
		}
		LineElement le = new LineElement(file, ln, offset, content);
		lines.add(le);
		return le;
	}

	protected Object createPattern(IResource scope, Object source) {
		if (source instanceof IFile) {
			IFile file = (IFile) source;
			String fileExtension = file.getFileExtension();
			if ("bm".equalsIgnoreCase(fileExtension)) {
				return getBMPKG(scope, file);
			}
			if ("screen".equalsIgnoreCase(fileExtension)
					|| "svc".equalsIgnoreCase(fileExtension)) {
				return getScreenPKG(scope, file);
			}
		}
		return null;
	}

	private Object getScreenPKG(IResource scope, IFile file) {
		return file.getName();
	}

	private Object getBMPKG(IResource scope, IFile file) {
		return Util.toBMPKG(file);
	}
}

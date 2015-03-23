package aurora.ide.search.core;

import org.eclipse.core.resources.IFile;

import aurora.ide.search.reference.MapFinderResult;
import aurora.ide.search.ui.LineElement;

public class AuroraMatch extends AbstractMatch {

	private IFile file;
	private MapFinderResult matchs;
	private LineElement line;

	public IFile getFile() {
		return file;
	}

	public void setFile(IFile file) {
		this.file = file;
	}

	public MapFinderResult getMatchs() {
		return matchs;
	}

	public void setMatchs(MapFinderResult matchs) {
		this.matchs = matchs;
	}

	public AuroraMatch(Object element, int offset, int length) {
		super(element, offset, length);
		this.file = (IFile) element;
	}

	public AuroraMatch(IFile resource, int offset, int length, LineElement line) {
		this(resource, offset, length);
		this.line = line;
	}

	public LineElement getLineElement() {
		return line;
	}

}

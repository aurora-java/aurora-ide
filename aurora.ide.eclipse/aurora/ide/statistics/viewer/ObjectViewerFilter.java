package aurora.ide.statistics.viewer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import aurora.ide.search.core.Util;

public class ObjectViewerFilter extends ViewerFilter {

	private String fileName;

	public ObjectViewerFilter(String fileName) {
		this.fileName = fileName + "*";
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof ObjectNode) {
			ObjectNode o = (ObjectNode) element;
			if ("*".equals(fileName)) {
				return true;
			} else {
				return Util.stringMatch(fileName, o.fileName, false, false);
			}
		}
		return true;
	}

}

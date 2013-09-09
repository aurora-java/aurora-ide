package aurora.ide.prototype.consultant.view;

import java.io.File;
import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class CNFViewerSorter extends ViewerSorter {
	public int compare(Viewer viewer, Object o1, Object o2) {
		if (o1 instanceof Node && o2 instanceof Node)
			return comparator.compare((Node) o1, (Node) o2);
		return 0;

	}

	private Comparator<Node> comparator = new Comparator<Node>() {

		@Override
		public int compare(Node an, Node bn) {

			File a = an.getFile();
			File b = bn.getFile();
			boolean aIsDir = a.isDirectory();
			boolean bIsDir = b.isDirectory();
			if (aIsDir && !bIsDir)
				return -1;
			if (bIsDir && !aIsDir)
				return 1;
			// sort case-sensitive files in a case-insensitive manner
			int compare = a.getName().compareToIgnoreCase(b.getName());
			if (compare == 0)
				compare = a.getName().compareTo(b.getName());
			return compare;

			// return 0;
		}
	};
}

package aurora.ide.statistics.viewer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class ObjectViewerSorter extends ViewerSorter {
	// 1
	private static final int FILENAME = 1;
	// 2
	private static final int PATH = 2;
	// 3
	private static final int FILESIZE = 3;
	// 4
	private static final int SCRIPTSIZE = 4;
	// 5
	private static final int TAGCOUNT = 5;
	// 6
	private static final int REFINCOUNT = 6;
	// 7
	private static final int REFOUTCOUNT = 7;

	public static final ObjectViewerSorter FILENAME_ASC = new ObjectViewerSorter(FILENAME);
	public static final ObjectViewerSorter FILENAME_DESC = new ObjectViewerSorter(-FILENAME);
	public static final ObjectViewerSorter PATH_ASC = new ObjectViewerSorter(PATH);
	public static final ObjectViewerSorter PATH_DESC = new ObjectViewerSorter(-PATH);
	public static final ObjectViewerSorter FILESIZE_ASC = new ObjectViewerSorter(FILESIZE);
	public static final ObjectViewerSorter FILESIZE_DESC = new ObjectViewerSorter(-FILESIZE);
	public static final ObjectViewerSorter SCRIPTSIZE_ASC = new ObjectViewerSorter(SCRIPTSIZE);
	public static final ObjectViewerSorter SCRIPTSIZE_DESC = new ObjectViewerSorter(-SCRIPTSIZE);
	public static final ObjectViewerSorter TAGCOUNT_ASC = new ObjectViewerSorter(TAGCOUNT);
	public static final ObjectViewerSorter TAGCOUNT_DESC = new ObjectViewerSorter(-TAGCOUNT);
	public static final ObjectViewerSorter REFINCOUNT_ASC = new ObjectViewerSorter(REFINCOUNT);
	public static final ObjectViewerSorter REFINCOUNT_DESC = new ObjectViewerSorter(-REFINCOUNT);
	public static final ObjectViewerSorter REFOUTCOUNT_ASC = new ObjectViewerSorter(REFOUTCOUNT);
	public static final ObjectViewerSorter REFOUTCOUNT_DESC = new ObjectViewerSorter(-REFOUTCOUNT);

	private int sortType;

	private ObjectViewerSorter(int sortType) {
		this.sortType = sortType;
	}

	public int compare(Viewer viewer, Object e1, Object e2) {
		if (!(e1 instanceof ObjectNode) || !(e2 instanceof ObjectNode)) {
			return 0;
		}
		ObjectNode o1 = (ObjectNode) e1;
		ObjectNode o2 = (ObjectNode) e2;
		switch (sortType) {
		case FILENAME: {
			String s1 = o1.fileName;
			String s2 = o2.fileName;
			if (s1 != null && s2 != null) {
				return s1.compareTo(s2);
			}
		}
		case -FILENAME: {
			String s1 = o1.fileName;
			String s2 = o2.fileName;
			if (s1 != null && s2 != null) {
				return s2.compareTo(s1);
			}
		}
		case PATH: {
			String s1 = o1.path;
			String s2 = o2.path;
			if (s1 != null && s2 != null) {
				return s1.compareTo(s2);
			}
		}
		case -PATH: {
			String s1 = o1.path;
			String s2 = o2.path;
			if (s1 != null && s2 != null) {
				return s2.compareTo(s1);
			}
		}
		case FILESIZE: {
			String s1 = o1.fileSize;
			String s2 = o2.fileSize;
			if (s1 != null && s2 != null) {
				if (Integer.parseInt(s1) > Integer.parseInt(s2)) {
					return 1;
				} else if (Integer.parseInt(s1) < Integer.parseInt(s2)) {
					return -1;
				} else {
					return 0;
				}
			}
		}
		case -FILESIZE: {
			String s1 = o1.fileSize;
			String s2 = o2.fileSize;
			if (s1 != null && s2 != null) {
				if (Integer.parseInt(s2) > Integer.parseInt(s1)) {
					return 1;
				} else if (Integer.parseInt(s2) < Integer.parseInt(s1)) {
					return -1;
				} else {
					return 0;
				}
			}
		}
		case SCRIPTSIZE: {
			String s1 = o1.scriptSize;
			String s2 = o2.scriptSize;
			if (s1 != null && s2 != null) {
				if (Integer.parseInt(s1) > Integer.parseInt(s2)) {
					return 1;
				} else if (Integer.parseInt(s1) < Integer.parseInt(s2)) {
					return -1;
				} else {
					return 0;
				}
			}
		}
		case -SCRIPTSIZE: {
			String s1 = o1.scriptSize;
			String s2 = o2.scriptSize;
			if (s1 != null && s2 != null) {
				if (Integer.parseInt(s2) > Integer.parseInt(s1)) {
					return 1;
				} else if (Integer.parseInt(s2) < Integer.parseInt(s1)) {
					return -1;
				} else {
					return 0;
				}
			}
		}
		case TAGCOUNT: {
			String s1 = o1.tagCount;
			String s2 = o2.tagCount;
			if (s1 != null && s2 != null) {
				if (Integer.parseInt(s1) > Integer.parseInt(s2)) {
					return 1;
				} else if (Integer.parseInt(s1) < Integer.parseInt(s2)) {
					return -1;
				} else {
					return 0;
				}
			}
		}
		case -TAGCOUNT: {
			String s1 = o1.tagCount;
			String s2 = o2.tagCount;
			if (s1 != null && s2 != null) {
				if (Integer.parseInt(s2) > Integer.parseInt(s1)) {
					return 1;
				} else if (Integer.parseInt(s2) < Integer.parseInt(s1)) {
					return -1;
				} else {
					return 0;
				}
			}
		}
		case REFINCOUNT: {
			String s1 = o1.refInCount;
			String s2 = o2.refInCount;
			if (s1 != null && s2 != null) {
				if (Integer.parseInt(s1) > Integer.parseInt(s2)) {
					return 1;
				} else if (Integer.parseInt(s1) < Integer.parseInt(s2)) {
					return -1;
				} else {
					return 0;
				}
			}
		}
		case -REFINCOUNT: {
			String s1 = o1.refInCount;
			String s2 = o2.refInCount;
			if (s1 != null && s2 != null) {
				if (Integer.parseInt(s2) > Integer.parseInt(s1)) {
					return 1;
				} else if (Integer.parseInt(s2) < Integer.parseInt(s1)) {
					return -1;
				} else {
					return 0;
				}
			}
		}
		case REFOUTCOUNT: {
			String s1 = o1.refOutCount;
			String s2 = o2.refOutCount;
			if (s1 != null && s2 != null) {
				if (Integer.parseInt(s1) > Integer.parseInt(s2)) {
					return 1;
				} else if (Integer.parseInt(s1) < Integer.parseInt(s2)) {
					return -1;
				} else {
					return 0;
				}
			}
		}
		case -REFOUTCOUNT: {
			String s1 = o1.refOutCount;
			String s2 = o2.refOutCount;
			if (s1 != null && s2 != null) {
				if (Integer.parseInt(s2) > Integer.parseInt(s1)) {
					return 1;
				} else if (Integer.parseInt(s2) < Integer.parseInt(s1)) {
					return -1;
				} else {
					return 0;
				}
			}
		}
		}
		return 0;
	}
}

package aurora.ide.prototype.consultant.view;

import java.io.File;
import java.util.Vector;

public class CNFContentHelper {
	private final static String DRIVE_A = "a:" + File.separator;
	private final static String DRIVE_B = "b:" + File.separator;
	private boolean initial = true;
	private File currentDirectory;

	/**
	 * Gets filesystem root entries
	 * 
	 * @return an array of Files corresponding to the root directories on the
	 *         platform, may be empty but not null
	 */
	File[] getRoots() {
		/*
		 * On JDK 1.22 only...
		 */
		// return File.listRoots();

		/*
		 * On JDK 1.1.7 and beyond... -- PORTABILITY ISSUES HERE --
		 */
		if (System.getProperty("os.name").indexOf("Windows") != -1) {
			Vector /* of File */list = new Vector();
			list.add(new File(DRIVE_A));
			list.add(new File(DRIVE_B));
			for (char i = 'c'; i <= 'z'; ++i) {
				File drive = new File(i + ":" + File.separator);
				if (drive.isDirectory() && drive.exists()) {
					list.add(drive);
					if (initial && i == 'c') {
						currentDirectory = drive;
						initial = false;
					}
				}
			}
			File[] roots = (File[]) list.toArray(new File[list.size()]);
			sortFiles(roots);
			return roots;
		}
		File root = new File(File.separator);
		if (initial) {
			currentDirectory = root;
			initial = false;
		}
		return new File[] { root };
	}

	/**
	 * Gets a directory listing
	 * 
	 * @param file
	 *            the directory to be listed
	 * @return an array of files this directory contains, may be empty but not
	 *         null
	 */
	public static File[] getDirectoryList(File file) {
		File[] list = file.listFiles();
		if (list == null)
			return new File[0];
		sortFiles(list);
		return list;
	}

	/**
	 * Sorts files lexicographically by name.
	 * 
	 * @param files
	 *            the array of Files to be sorted
	 */
	static void sortFiles(File[] files) {
		/* Very lazy merge sort algorithm */
		sortBlock(files, 0, files.length - 1, new File[files.length]);
	}

	private static void sortBlock(File[] files, int start, int end,
			File[] mergeTemp) {
		final int length = end - start + 1;
		if (length < 8) {
			for (int i = end; i > start; --i) {
				for (int j = end; j > start; --j) {
					if (compareFiles(files[j - 1], files[j]) > 0) {
						final File temp = files[j];
						files[j] = files[j - 1];
						files[j - 1] = temp;
					}
				}
			}
			return;
		}
		final int mid = (start + end) / 2;
		sortBlock(files, start, mid, mergeTemp);
		sortBlock(files, mid + 1, end, mergeTemp);
		int x = start;
		int y = mid + 1;
		for (int i = 0; i < length; ++i) {
			if ((x > mid)
					|| ((y <= end) && compareFiles(files[x], files[y]) > 0)) {
				mergeTemp[i] = files[y++];
			} else {
				mergeTemp[i] = files[x++];
			}
		}
		for (int i = 0; i < length; ++i)
			files[i + start] = mergeTemp[i];
	}

	private static int compareFiles(File a, File b) {
		// boolean aIsDir = a.isDirectory();
		// boolean bIsDir = b.isDirectory();
		// if (aIsDir && !bIsDir)
		// return -1;
		// if (bIsDir && !aIsDir)
		// return 1;

		// sort case-sensitive files in a case-insensitive manner
		int compare = a.getName().compareToIgnoreCase(b.getName());
		if (compare == 0)
			compare = a.getName().compareTo(b.getName());
		return compare;
	}

}

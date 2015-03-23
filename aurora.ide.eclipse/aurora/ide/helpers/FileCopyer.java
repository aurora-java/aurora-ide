package aurora.ide.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCopyer {

	private static final int BUFFER_SIZE = 4096 * 4;

	/**
	 * Copy a directory and all of its contents.
	 */
	public static boolean copyDirectory(File from, File to) {
		return copyDirectory(from, to, (byte[]) null, (String[]) null);
	}

	public static boolean copyDirectory(String from, String to) {
		return copyDirectory(new File(from), new File(to));
	}

	/**
	 * @param filter
	 *            - array of names to not copy.
	 */
	public static boolean copyDirectory(File from, File to, byte[] buffer,
			String[] filter) {
		//
		// System.out.println("copyDirectory("+from+","+to+")");

		if (from == null)
			return false;
		if (!from.exists())
			return true;
		if (!from.isDirectory())
			return false;

		if (to.exists()) {
			// System.out.println(to + " exists");
			return false;
		}
		if (!to.mkdirs()) {
			// System.out.println("can't make" + to);
			return false;
		}

		String[] list = from.list();

		// Some JVMs return null for File.list() when the
		// directory is empty.
		if (list != null) {

			if (buffer == null)
				buffer = new byte[BUFFER_SIZE]; // reuse this buffer to copy
												// files

			nextFile: for (int i = 0; i < list.length; i++) {

				String fileName = list[i];

				if (filter != null) {
					for (int j = 0; j < filter.length; j++) {
						if (fileName.equals(filter[j]))
							continue nextFile;
					}
				}

				File entry = new File(from, fileName);

				// System.out.println("\tcopying entry " + entry);

				if (entry.isDirectory()) {
					if (!copyDirectory(entry, new File(to, fileName), buffer,
							filter))
						return false;
				} else {
					if (!copyFile(entry, new File(to, fileName), buffer))
						return false;
				}
			}
		}
		return true;
	}

	public static boolean copyFile(File from, File to) {
		return copyFile(from, to, (byte[]) null);
	}

	public static boolean copyFile(File from, File to, byte[] buf) {
		if (buf == null)
			buf = new byte[BUFFER_SIZE];

		//
		// System.out.println("Copy file ("+from+","+to+")");
		FileInputStream from_s = null;
		FileOutputStream to_s = null;

		try {
			from_s = new FileInputStream(from);
			to_s = new FileOutputStream(to);

			for (int bytesRead = from_s.read(buf); bytesRead != -1; bytesRead = from_s
					.read(buf))
				to_s.write(buf, 0, bytesRead);

			from_s.close();
			from_s = null;

			to_s.getFD().sync(); // RESOLVE: sync or no sync?
			to_s.close();
			to_s = null;
		} catch (IOException ioe) {
			return false;
		} finally {
			if (from_s != null) {
				try {
					from_s.close();
				} catch (IOException ioe) {
				}
			}
			if (to_s != null) {
				try {
					to_s.close();
				} catch (IOException ioe) {
				}
			}
		}

		return true;
	}
}

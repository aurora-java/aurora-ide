package aurora.ide.libs;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class AuroraImagesUtils {

	// The plugin registry
	private final static ImageRegistry PLUGIN_REGISTRY = AuroraResourcesPlugin
			.getDefault().getImageRegistry();

	public static final IPath ICONS_PATH = new Path("$nl$/icons/"); //$NON-NLS-1$

	private static Map<String, String> keyPathMap = new HashMap<String, String>() {
		{
			this.put("toolbar_bg", "toolbar_bg.gif");
			this.put("itembar", "itembar.gif");
			this.put("btn", "btn.gif");
			this.put("grid_bg", "grid_bg.gif");
			this.put("navigation", "navigation.gif");
			this.put("toolbar_sep", "toolbar_sep.gif");
			this.put("nav1", "nav1.png");
			this.put("nav2", "nav2.png");
			this.put("label", "label.png");
		}
	};

	public static Image getImage(String key) {
		Image image = PLUGIN_REGISTRY.get(key);
		if (image == null) {
			IPath append = ICONS_PATH.append(getPath(key));
			ImageDescriptor imageDescriptor = AuroraResourcesPlugin
					.getImageDescriptor(append.toString());
			PLUGIN_REGISTRY.put(key, imageDescriptor);
			image = PLUGIN_REGISTRY.get(key);
		}
		return image;
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		ImageDescriptor image = PLUGIN_REGISTRY.getDescriptor(key);
		if (image == null) {
			IPath append = ICONS_PATH.append(getPath(key));
			ImageDescriptor imageDescriptor = AuroraResourcesPlugin
					.getImageDescriptor(append.toString());
			PLUGIN_REGISTRY.put(key, imageDescriptor);
			image = PLUGIN_REGISTRY.getDescriptor(key);
		}

		return image;
	}

	private static String getPath(String key) {
		String path = keyPathMap.get(key);
		return path == null ? key : path;
	}

	public static byte[] toBytes(String string) {
		String[] split = string.split(", ");
		byte[] _byteArray = new byte[split.length];
		for (int i = 0; i < split.length; i++) {
			String s = split[i];
			// if (s == null||"".equals(s)){
			// System.out.println(s);
			// // break;
			// }
			_byteArray[i] = Byte.valueOf(s);
		}
		return _byteArray;
	}

	public static byte[] toBytes(ImageData imageData, int format) {
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[1];
		imageLoader.data[0] = imageData;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		imageLoader.save(outputStream, format);
		byte[] byteArray = outputStream.toByteArray();
		return byteArray;
	}

	public static String toString(byte[] a) {
		if (a == null)
			return "";
		int iMax = a.length - 1;
		if (iMax == -1)
			return "[]";

		StringBuilder b = new StringBuilder();
		// b.append('[');
		for (int i = 0;; i++) {
			b.append(a[i]);
			if (i == iMax)
				return b.toString();
			b.append(", ");
		}
	}

	static public ImageData toImageData(byte[] iconByteData) {
		InputStream is = new ByteArrayInputStream(iconByteData);
		ImageLoader imageLoader = new ImageLoader();
		ImageData idd = imageLoader.load(is)[0];
		return idd;
	}

	public static RGB toRGB(String color) {
		String[] split = color.split(",");
		return new RGB(Integer.valueOf(split[0]), Integer.valueOf(split[1]),
				Integer.valueOf(split[2]));
	}

	public static String toString(RGB color) {
		return color.red + "," + color.green + "," + color.blue;
	}
	public static  ImageData loadImageData(Path path) throws FileNotFoundException {
		ImageLoader loader = new ImageLoader();
		ImageData[] load = loader.load(new FileInputStream(path.toFile()));
		ImageData imageData = load[0];
		return imageData;
	}

	public static  String queryFile(Shell shell) {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText("Open File"); //$NON-NLS-1$
		dialog.setFilterExtensions(new String[] { "*.png", "*.gif", "*.jpg",
				"*.jpeg", "*.bmp", "*.tiff", });
		String path = dialog.open();
		if (path != null && path.length() > 0) {
			return path;
		}
		return null;
	}

	public static  int getIconType(String ext) {
		if ("png".equalsIgnoreCase(ext)) {
			return SWT.IMAGE_PNG;
		}
		if ("gif".equalsIgnoreCase(ext)) {
			return SWT.IMAGE_GIF;
		}
		if ("jpg".equalsIgnoreCase(ext)) {
			return SWT.IMAGE_JPEG;
		}
		if ("jpeg".equalsIgnoreCase(ext)) {
			return SWT.IMAGE_JPEG;
		}
		if ("bmp".equalsIgnoreCase(ext)) {
			return SWT.IMAGE_BMP;
		}
		if ("tiff".equalsIgnoreCase(ext)) {
			return SWT.IMAGE_TIFF;
		}
		return -1;
	}

}

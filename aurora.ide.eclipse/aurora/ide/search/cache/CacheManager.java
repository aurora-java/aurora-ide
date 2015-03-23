package aurora.ide.search.cache;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.ApplicationException;

public class CacheManager {
	private static CompositeMapCacher mapCacher;

	private static final String POINT_ID = "aurora.ide.compositeMapCacher";

	private static List<String> exs = new ArrayList<String>();

	static {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(POINT_ID);
		if (point != null) {
			IExtension[] extensions = point.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] configurationElements = extensions[i]
						.getConfigurationElements();
				for (IConfigurationElement ice : configurationElements) {
					if ("files".equals(ice.getName())) {
						String attribute = ice.getAttribute("extentionNames");
						if (attribute != null) {
							String[] split = attribute.split(",");
							for (String s : split) {
								exs.add(s.trim().toLowerCase());
							}
						}
					}
				}
			}
		}
	}

	public static boolean isSupport(String ext) {
		return exs.contains(ext.toLowerCase());
	}

	public static boolean isSupport(IFile file) {
		return isSupport(file.getFileExtension());
	}

	public static final CompositeMapCacher getCompositeMapCacher() {
		if (mapCacher == null) {
			mapCacher = new CompositeMapCacher();
		}
		return mapCacher;
	}

	public static CompositeMap getCompositeMap(IFile file)
			throws CoreException, ApplicationException {
		return getCompositeMapCacher().getCompositeMap(file);
	}

	public static CompositeMap getWholeBMCompositeMap(IFile file)
			throws CoreException, ApplicationException {
		return getCompositeMapCacher().getWholeCompositeMap(file);
	}

	public static IDocument getDocument(IFile file) throws CoreException {
		return getCompositeMapCacher().getDocument(file);
	}

	public static String getTOXML(IFile file) throws CoreException,
			ApplicationException {
		return getCompositeMapCacher().getTOXML(file);
	}

	public static String getString(IFile file) throws CoreException,
			ApplicationException {
		return getCompositeMapCacher().getString(file);
	}

}

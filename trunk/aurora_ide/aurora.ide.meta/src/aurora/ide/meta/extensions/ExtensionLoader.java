package aurora.ide.meta.extensions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ExtensionLoader {
	private static final String EDITOR_POINT_ID = "aurora.ide.meta.uipEditor";
	private static final String COMPONENT_POINT_ID = "aurora.ide.meta.auroraComponent";

	private static List<PaletteCategory> paletteCategories = new ArrayList<PaletteCategory>();
	private static List<ExtensionComponent> extensionComponents = new ArrayList<ExtensionComponent>();

	static {
		editorPointExtensions();
		componentPointExtensions();
	}

	private static void editorPointExtensions() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(EDITOR_POINT_ID);
		if (point != null) {
			IExtension[] extensions = point.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] configurationElements = extensions[i]
						.getConfigurationElements();
				for (IConfigurationElement ice : configurationElements) {
					if ("palette".equals(ice.getName())) {
						IConfigurationElement[] children = ice
								.getChildren("category");
						for (IConfigurationElement c : children) {
							String label = c.getAttribute("label");
							String filter = c.getAttribute("filter");
							String id = c.getAttribute("id");
							PaletteCategory pc = new PaletteCategory(label,
									filter, id);
							paletteCategories.add(pc);
						}
					}
				}
			}
		}
	}

	// <component
	// categoryId="aurora.ide.meta.palette.category.button"
	// creator="aurora.ide.meta.component.ButtonCreator"
	// descriptor="aurora.ide.meta.component.ButtonDesc"
	// id="aurora.ide.meta.component.button"
	// name="button">
	// </component>

	private static void componentPointExtensions() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(COMPONENT_POINT_ID);
		if (point != null) {
			IExtension[] extensions = point.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] configurationElements = extensions[i]
						.getConfigurationElements();
				for (IConfigurationElement ice : configurationElements) {
					if ("component".equals(ice.getName())) {
						String categoryId = ice.getAttribute("categoryId");
						String creator = ice.getAttribute("creator");
						String descriptor = ice.getAttribute("descriptor");
						String id = ice.getAttribute("id");
						String name = ice.getAttribute("name");
						String ioHandler = ice.getAttribute("ioHandler");
						ExtensionComponent ec = new ExtensionComponent(
								categoryId, creator, descriptor, id, name,
								ioHandler);
						extensionComponents.add(ec);
					}
				}
			}
		}
	}

	public static List<PaletteCategory> getPaletteCategories() {
		return paletteCategories;
	}

	public static List<ExtensionComponent> getExtensionComponents() {
		return extensionComponents;
	}

}

package aurora.ide.meta.extensions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ExtensionLoader {

	private static final String META_PLUGIN_ID = "aurora.ide.meta";
	private static final String ACONSULTANT_PRODUCT_PLUGIN_ID = "aurora.ide.prototype.consultant.product";

	private static final String META_EDITOR_POINT_ID = META_PLUGIN_ID
			+ ".uipEditor";
	private static final String META_COMPONENT_POINT_ID = META_PLUGIN_ID
			+ ".auroraComponent";
	private static final String PRODUCT_EDITOR_POINT_ID = ACONSULTANT_PRODUCT_PLUGIN_ID
			+ ".uipEditor";
	private static final String PRODUCT_COMPONENT_POINT_ID = ACONSULTANT_PRODUCT_PLUGIN_ID
			+ ".auroraComponent";

	private static List<PaletteCategory> paletteCategories = new ArrayList<PaletteCategory>();
	private static List<ExtensionComponent> extensionComponents = new ArrayList<ExtensionComponent>();

	static {
		editorPointExtensions();
		componentPointExtensions();
	}

	private static void editorPointExtensions() {
		loadEditorExtension(META_EDITOR_POINT_ID);
		loadEditorExtension(PRODUCT_EDITOR_POINT_ID);
	}

	private static void loadEditorExtension(String point_id) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(point_id);
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
		loadComponentExtension(META_COMPONENT_POINT_ID);
		loadComponentExtension(PRODUCT_COMPONENT_POINT_ID);
	}

	private static void loadComponentExtension(String pointID) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(pointID);
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
						String types = ice.getAttribute("types");
						ExtensionComponent ec = new ExtensionComponent(
								categoryId, creator, descriptor, id, name,
								ioHandler);
						ec.setTypes(types);
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

	public static List<String> getTypesByCategoryId(String categoryId) {
		List<String> types = new ArrayList<String>();
		List<ExtensionComponent> extensionComponents = ExtensionLoader
				.getExtensionComponents();
		for (ExtensionComponent ec : extensionComponents) {
			if (categoryId.equals(ec.getCategoryId())) {
				List<String> _types = ec.getTypes();
				for (String t : _types) {
					types.add(t.toLowerCase());
				}
			}
		}
		return types;
	}

	public static List<String> getComponentNamesByCategoryId(String categoryId) {
		List<String> names = new ArrayList<String>();
		List<ExtensionComponent> extensionComponents = ExtensionLoader
				.getExtensionComponents();
		for (ExtensionComponent ec : extensionComponents) {
			if (categoryId.equals(ec.getCategoryId())) {
				names.add(ec.getName().toLowerCase());
			}
		}
		return names;
	}
}

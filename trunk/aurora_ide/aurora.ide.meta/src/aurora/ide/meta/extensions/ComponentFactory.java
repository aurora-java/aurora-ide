package aurora.ide.meta.extensions;

import java.util.List;

import org.eclipse.gef.EditPart;

import aurora.ide.meta.gef.editors.models.io.DefaultIOHandler;
import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class ComponentFactory {
	public static AuroraComponent createComponent(String type) {
		if (type == null || "".equals(type.trim()))
			return null;
		List<ExtensionComponent> extensionComponents = ExtensionLoader
				.getExtensionComponents();
		for (ExtensionComponent ec : extensionComponents) {
			if (ec.getTypes().contains(type.toLowerCase())) {
				return ec.getCreator().createComponent(type);
			}
		}
		return null;
	}

	public static EditPart createEditPart(AuroraComponent model) {
		if (model == null)
			return null;
		String type = model.getComponentType();
		if (type == null || "".equals(type.trim()))
			return null;
		List<ExtensionComponent> extensionComponents = ExtensionLoader
				.getExtensionComponents();
		for (ExtensionComponent ec : extensionComponents) {
			if (ec.getTypes().contains(type.toLowerCase())) {
				return ec.getCreator().createEditPart(model);
			}
		}
		return null;
	}
	public static DefaultIOHandler getIOHandler(AuroraComponent model) {
		if (model == null)
			return null;
		String type = model.getComponentType();
		DefaultIOHandler ioHandler = getIOHandler(type);
		return ioHandler;
	}

	public static DefaultIOHandler getIOHandler(String type) {
		if (type == null || "".equals(type.trim()))
			return null;
		List<ExtensionComponent> extensionComponents = ExtensionLoader
				.getExtensionComponents();
		for (ExtensionComponent ec : extensionComponents) {
			if (ec.getTypes().contains(type.toLowerCase())) {
				return ec.getIoHandler(type);
			}
		}
		return null;
	}
}

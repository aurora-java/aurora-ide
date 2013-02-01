package aurora.ide.meta.gef.editors.models.io;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.extensions.ExtensionComponent;
import aurora.ide.meta.extensions.ExtensionLoader;
import aurora.ide.meta.gef.editors.models.AuroraComponent;

public class ExtIOHandlerUtil {

	public static IOHandler getHandler(AuroraComponent ac) {

		List<ExtensionComponent> extensionComponents = ExtensionLoader
				.getExtensionComponents();
		for (ExtensionComponent ec : extensionComponents) {
			if (ac.getClass().equals(ec.getCreator().clazz())) {
				DefaultIOHandler ioHandler = ec.getIoHandler();
				if (ioHandler != null)
					return ioHandler;
			}
		}
		return new ErrorIOHandler();
	}

	public static IOHandler getHandler(CompositeMap map) {

		List<ExtensionComponent> extensionComponents = ExtensionLoader
				.getExtensionComponents();
		for (ExtensionComponent ec : extensionComponents) {
			if (map.getName().equals(ec.getCreator().clazz().getSimpleName())) {
				DefaultIOHandler ioHandler = ec.getIoHandler();
				if (ioHandler != null)
					return ioHandler;
			}
		}
		return new ErrorIOHandler();
	}
}

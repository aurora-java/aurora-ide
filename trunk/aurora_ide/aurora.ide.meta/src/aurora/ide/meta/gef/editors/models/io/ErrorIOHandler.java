package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;

public class ErrorIOHandler implements IOHandler {

	public CompositeMap toCompositeMap(AuroraComponent ac, ModelIOContext mic) {
		CompositeMap map = new CompositeMap();
		map.setName("ERROR");
		map.put("model", ac.getClass().getName());
		return map;
	}

	public AuroraComponent fromCompositeMap(CompositeMap map, ModelIOContext mic) {
		return new AuroraComponent();
	}

}

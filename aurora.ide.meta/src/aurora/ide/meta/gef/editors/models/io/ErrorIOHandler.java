package aurora.ide.meta.gef.editors.models.io;

import aurora.ide.api.composite.map.CommentCompositeMap;
import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;

public class ErrorIOHandler implements IOHandler {

	public CompositeMap toCompositeMap(AuroraComponent ac, ModelIOContext mic) {
		CompositeMap map = new CommentCompositeMap();
		map.setName("ERROR");
		map.put("model", ac.getClass().getName());
		return map;
	}

	public AuroraComponent fromCompositeMap(CompositeMap map, ModelIOContext mic) {
		return new AuroraComponent();
	}

}

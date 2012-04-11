package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.InitModel;

public class InitModelHandler extends DefaultIOHandler {
	public static final String PATH = "path";

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new InitModel();
	}

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeSimpleAttribute(map, ac);
		map.put(PATH, ((InitModel) ac).getPath());
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		((InitModel) ac).setPath(map.getString(PATH));
	}
}

package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.ModelQuery;

public class ModelQueryHandler extends DefaultIOHandler {
	public static final String PATH = "path";

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new ModelQuery();
	}

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeSimpleAttribute(map, ac);
		map.put(PATH, ((ModelQuery) ac).getPath());
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		((ModelQuery) ac).setPath(map.getString(PATH));
	}
}

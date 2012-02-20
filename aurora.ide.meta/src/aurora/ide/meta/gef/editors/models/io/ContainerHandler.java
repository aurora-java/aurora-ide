package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;

/**
 * auto process Dataset
 * 
 * @author jessen
 * 
 */
public abstract class ContainerHandler extends DefaultIOHandler {
	@Override
	/**
	 * store Dataset
	 */
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeComplexAttribute(map, ac);
		Container cont = (Container) ac;
		Dataset ds = cont.getDataset();
		CompositeMap dsMap = new DataSetHandler().toCompositeMap(ds, mic);
		map.addChild(dsMap);
	}

	@Override
	/**
	 * restore Dataset
	 */
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreComplexAttribute(ac, map);
		Container cont = (Container) ac;
		CompositeMap dsMap = map.getChild(Dataset.class.getSimpleName());
		if (dsMap == null)
			return;
		Dataset ds = (Dataset) new DataSetHandler()
				.fromCompositeMap(dsMap, mic);
		cont.setDataset(ds);
	}
}

package aurora.ide.meta.gef.editors.models.io;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.TabBody;
import aurora.ide.meta.gef.editors.models.TabItem;

public class TabItemHandler extends DefaultIOHandler {

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		TabItem ti = (TabItem) ac;
		map.put(TabItem.PROMPT, ti.getPrompt());
		map.put(TabItem.WIDTH, ti.getWidth());
		map.put(TabItem.CURRENT, ti.isCurrent());
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		TabItem ti = (TabItem) ac;
		ti.setPrompt(map.getString(TabItem.PROMPT));
		ti.setWidth(map.getInt(TabItem.WIDTH));
		// ti.setCurrent(map.getBoolean(TabItem.CURRENT));
		// setCurrent will be called in TabHandler.restoreChildren(..)
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		TabItem ti = (TabItem) ac;
		CompositeMap bodyMap = new CompositeMap();
		bodyMap.setName(TabBody.class.getSimpleName());
		for (AuroraComponent a : ti.getBody().getChildren()) {
			IOHandler ioh = IOHandlerUtil.getHandler(a);
			bodyMap.addChild(ioh.toCompositeMap(a, mic));
		}
		map.addChild(bodyMap);
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		TabItem ti = (TabItem) ac;
		TabBody tb = ti.getBody();
		CompositeMap bodyMap = map.getChild(TabBody.class.getSimpleName());
		if (bodyMap == null)
			return;
		@SuppressWarnings("unchecked")
		List<CompositeMap> list = bodyMap.getChildsNotNull();
		for (CompositeMap m : list) {
			IOHandler ioh = IOHandlerUtil.getHandler(m);
			tb.addChild(ioh.fromCompositeMap(m, mic));
		}
	}

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new TabItem();
	}

}

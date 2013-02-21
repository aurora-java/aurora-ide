package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.link.TabRef;

public class TabItemHandler extends DefaultIOHandler {

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeSimpleAttribute(map, ac);
		TabItem ti = (TabItem) ac;
		map.put(TabItem.PROMPT, ti.getPrompt());
		map.put(TabItem.WIDTH, ti.getWidth());
		map.put(TabItem.CURRENT, ti.isCurrent());
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		TabItem ti = (TabItem) ac;
		ti.setPrompt(map.getString(TabItem.PROMPT));
		ti.setWidth(map.getInt(TabItem.WIDTH));
		// ti.setCurrent(map.getBoolean(TabItem.CURRENT));
		// setCurrent will be called in TabHandler.restoreChildren(..)
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		TabItem ti = (TabItem) ac;
		// CompositeMap bodyMap = new CommentCompositeMap();
		// bodyMap.setName(TabBody.class.getSimpleName());
		// for (AuroraComponent a : ti.getBody().getChildren()) {
		// IOHandler ioh = IOHandlerUtil.getHandler(a);
		// bodyMap.addChild(ioh.toCompositeMap(a, mic));
		// }
		// map.addChild(bodyMap);
		TabRef tr = ti.getTabRef();
		if (tr != null)
			map.addChild(new TabRefHandler().toCompositeMap(tr, mic));
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		TabItem ti = (TabItem) ac;
		// TabBody tb = ti.getBody();
		// CompositeMap bodyMap = map.getChild(TabBody.class.getSimpleName());
		// if (bodyMap == null)
		// return;
		// @SuppressWarnings("unchecked")
		// List<CompositeMap> list = bodyMap.getChildsNotNull();
		// for (CompositeMap m : list) {
		// IOHandler ioh = IOHandlerUtil.getHandler(m);
		// tb.addChild(ioh.fromCompositeMap(m, mic));
		// }
		CompositeMap m = map.getChild(TabRef.class.getSimpleName());
		if (m != null) {
			ti.setTabRef((TabRef) new TabRefHandler().fromCompositeMap(m, mic));
		}
	}

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new TabItem();
	}

}
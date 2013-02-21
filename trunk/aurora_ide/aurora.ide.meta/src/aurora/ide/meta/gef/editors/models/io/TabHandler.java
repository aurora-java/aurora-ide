package aurora.ide.meta.gef.editors.models.io;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.TabBody;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.models.TabItem;

public class TabHandler extends DefaultIOHandler {

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeSimpleAttribute(map, ac);
		TabFolder tf = (TabFolder) ac;
		map.put(TabFolder.PROMPT, tf.getPrompt());
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		TabFolder tf = (TabFolder) ac;
		tf.setPrompt(map.getString(TabFolder.PROMPT));
	}

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new TabFolder();
	}

	@Override
	protected boolean isStoreable(AuroraComponent ac) {
		if (ac instanceof TabBody)
			return false;
		return super.isStoreable(ac);
	}

	@Override
	protected void restoreChildren(Container container, CompositeMap map) {
		super.restoreChildren(container, map);
		// now we reset which tabitem is current selected
		CompositeMap childList = map.getChild(CHILD_LIST);
		if (childList == null)
			return;
		@SuppressWarnings("unchecked")
		List<CompositeMap> list = childList.getChildsNotNull();
		for (int i = 0; i < list.size(); i++) {
			CompositeMap m = list.get(i);
			TabItem ti = (TabItem) container.getChildren().get(i);
			ti.setCurrent(m.getBoolean(TabItem.CURRENT));
		}
	}
}

package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.TabBody;
import aurora.ide.meta.gef.editors.models.TabFolder;

public class TabHandler extends DefaultIOHandler {

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		TabFolder tf = (TabFolder) ac;
		map.put(TabFolder.PROMPT, tf.getPrompt());
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
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

}

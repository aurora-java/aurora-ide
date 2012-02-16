package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.VBox;

public class BoxHandler extends DefaultIOHandler {

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		BOX box = (BOX) ac;
		map.put(BOX.TITLE, box.getTitle());
		map.put(BOX.PROMPT, box.getPrompt());
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		BOX box = (BOX) ac;
		box.setTitle(map.getString(BOX.TITLE));
		box.setPrompt(map.getString(BOX.PROMPT));
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
	}

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		if (HBox.class.getSimpleName().equalsIgnoreCase(map.getName()))
			return new HBox();
		return new VBox();
	}

}

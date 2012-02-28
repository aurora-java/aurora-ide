package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.VBox;

public class BoxHandler extends ContainerHandler {

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		BOX box = (BOX) ac;
		map.put(BOX.TITLE, box.getTitle());
		map.put(BOX.PROMPT, box.getPrompt());
		map.put(BOX.LABELWIDTH, box.getLabelWidth());
		if (!(box instanceof HBox || box instanceof VBox)) {
			map.put(BOX.ROW, box.getRow());
			map.put(BOX.COL, box.getCol());
		}
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		BOX box = (BOX) ac;
		box.setTitle(map.getString(BOX.TITLE));
		box.setPrompt(map.getString(BOX.PROMPT));
		box.setLabelWidth(map.getInt(BOX.LABELWIDTH));
		if (!(box instanceof HBox || box instanceof VBox)) {
			box.setRow(map.getInt(BOX.ROW));
			box.setCol(map.getInt(BOX.COL));
		}
	}

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		if (HBox.class.getSimpleName().equalsIgnoreCase(map.getName()))
			return new HBox();
		else if (VBox.class.getSimpleName().equalsIgnoreCase(map.getName()))
			return new VBox();
		else if (Form.class.getSimpleName().equalsIgnoreCase(map.getName()))
			return new Form();
		return new FieldSet();
	}

}

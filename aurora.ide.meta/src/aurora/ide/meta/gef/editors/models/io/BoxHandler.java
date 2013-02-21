package aurora.ide.meta.gef.editors.models.io;

import org.eclipse.draw2d.geometry.Dimension;

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
		super.storeSimpleAttribute(map, ac);
		BOX box = (BOX) ac;
		map.put(BOX.TITLE, box.getTitle());
		map.put(BOX.PROMPT, box.getPrompt());
		map.put(BOX.LABELWIDTH, box.getLabelWidth());
		map.put(BOX.WIDTH, box.getSize().width);
		map.put(BOX.HEIGHT, box.getSize().height);
		if (!(box instanceof HBox || box instanceof VBox)) {
			map.put(BOX.ROW, box.getRow());
			map.put(BOX.COL, box.getCol());
		}
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		BOX box = (BOX) ac;
		box.setTitle(map.getString(BOX.TITLE));
		box.setPrompt(map.getString(BOX.PROMPT));
		box.setLabelWidth(map.getInt(BOX.LABELWIDTH));
		Integer w = map.getInt(BOX.WIDTH);
		Integer h = map.getInt(BOX.HEIGHT);
		if (w != null && h != null) {
			box.setSize(new Dimension(w, h));
		}
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

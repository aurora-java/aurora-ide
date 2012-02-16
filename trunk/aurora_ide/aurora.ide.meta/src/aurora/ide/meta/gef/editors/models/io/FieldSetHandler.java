package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.FieldSet;

public class FieldSetHandler extends DefaultIOHandler {

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		FieldSet fieldSet = (FieldSet) ac;
		map.put(AuroraComponent.PROMPT, fieldSet.getPrompt());
		map.put(AuroraComponent.ROW, fieldSet.getRow());
		map.put(AuroraComponent.COL, fieldSet.getCol());
		map.put(AuroraComponent.TITLE, fieldSet.getTitle());
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		FieldSet fieldSet = (FieldSet) ac;
		fieldSet.setPrompt(map.getString(AuroraComponent.PROMPT));
		fieldSet.setTitle(map.getString(AuroraComponent.TITLE));
		fieldSet.setRow(map.getInt(AuroraComponent.ROW));
		fieldSet.setCol(map.getInt(AuroraComponent.COL));
	}

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new FieldSet();
	}
}

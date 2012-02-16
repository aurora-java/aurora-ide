package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Form;

public class FormHandler extends DefaultIOHandler {

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		Form form = (Form) ac;
		map.put(AuroraComponent.PROMPT, form.getPrompt());
		map.put(AuroraComponent.ROW, form.getRow());
		map.put(AuroraComponent.COL, form.getCol());
		map.put(AuroraComponent.TITLE, form.getTitle());
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		Form form = (Form) ac;
		form.setPrompt(map.getString(AuroraComponent.PROMPT));
		form.setTitle(map.getString(AuroraComponent.TITLE));
		form.setRow(map.getInt(AuroraComponent.ROW));
		form.setCol(map.getInt(AuroraComponent.COL));
	}

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new Form();
	}
}

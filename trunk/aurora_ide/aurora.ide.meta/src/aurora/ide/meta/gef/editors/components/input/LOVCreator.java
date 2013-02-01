package aurora.ide.meta.gef.editors.components.input;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.parts.InputPart;

public class LOVCreator extends ComponentCreator {

	public LOVCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"LOV",
				"Create a new Lov",
				LOV.class,
				new SimpleFactory(LOV.class)
				// {
				// public Object getNewObject() {
				// Input newObject = (Input) super.getNewObject();
				// newObject.setType(Input.LOV);
				// return newObject;
				// }
				// }
				, ImagesUtils.getImageDescriptor("palette/itembar_03.png"),
				ImagesUtils.getImageDescriptor("palette/itembar_03.png"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof LOV) {
			return new InputPart();
		}
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return LOV.class;
	}

}

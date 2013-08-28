package aurora.ide.meta.gef.editors.components;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.parts.RadioItemPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.RadioItem;

public class RadioItemCreator extends ComponentCreator {

	public RadioItemCreator() {
	}
	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Radio", "Create a new Radio Item", RadioItem.class,
				new SimpleFactory(RadioItem.class),
				aurora.ide.prototype.consultant.product.Activator
						.getImageDescriptor("/icons/radio_01.png"),
				aurora.ide.prototype.consultant.product.Activator
						.getImageDescriptor("/icons/radio_01.png"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof RadioItem) {
			return new RadioItemPart();
		}
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return RadioItem.class;
	}

	public AuroraComponent createComponent(String type) {
		String t = RadioItem.RADIO_ITEM;
		if (t.equalsIgnoreCase(type)) {
			RadioItem c = new RadioItem();
			c.setComponentType(t);
			return c;
		}
		return null;
	}
}

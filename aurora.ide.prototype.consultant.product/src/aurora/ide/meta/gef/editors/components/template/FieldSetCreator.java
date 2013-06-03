package aurora.ide.meta.gef.editors.components.template;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.plugin.source.gen.screen.model.Combox;
import aurora.plugin.source.gen.screen.model.DatePicker;
import aurora.plugin.source.gen.screen.model.FieldSet;
import aurora.plugin.source.gen.screen.model.LOV;
import aurora.plugin.source.gen.screen.model.NumberField;
import aurora.plugin.source.gen.screen.model.TextField;

public class FieldSetCreator extends ComponentCreator {

	public FieldSetCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"FieldSet",
				"Create a  FieldSet Sample",
				FieldSet.class,
				new SimpleFactory(FieldSet.class) {
					public Object getNewObject() {
						return createForm();
					}
				},
				PrototypeImagesUtils.getImageDescriptor("palette/fieldset.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/fieldset.png"));
		return combined;
	}

	public FieldSet createForm() {
		FieldSet form = new FieldSet();
		form.addChild(new TextField());
		form.addChild(new NumberField());
		form.addChild(new DatePicker());
		form.addChild(new Combox());
		form.addChild(new LOV());
		return form;
	}
}

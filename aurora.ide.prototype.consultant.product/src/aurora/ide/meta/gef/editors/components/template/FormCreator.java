package aurora.ide.meta.gef.editors.components.template;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.plugin.source.gen.screen.model.Combox;
import aurora.plugin.source.gen.screen.model.DatePicker;
import aurora.plugin.source.gen.screen.model.Form;
import aurora.plugin.source.gen.screen.model.LOV;
import aurora.plugin.source.gen.screen.model.NumberField;
import aurora.plugin.source.gen.screen.model.TextField;

public class FormCreator extends ComponentCreator {

	public FormCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Form", "Create a  Form Sample", Form.class, new SimpleFactory(
						Form.class) {
					public Object getNewObject() {
						return createForm();
					}

				}, PrototypeImagesUtils.getImageDescriptor("palette/form.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/form.png"));
		return combined;
	}

	public Form createForm() {
		Form form = new Form();
		form.addChild(new TextField());
		form.addChild(new NumberField());
		form.addChild(new DatePicker());
		form.addChild(new Combox());
		form.addChild(new LOV());
		return form;
	}
}

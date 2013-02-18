package aurora.ide.meta.gef.editors.components.box;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.parts.BoxPart;

public class FormCreator extends ComponentCreator {

	public FormCreator() {
	}
	
	public PaletteEntry createPaletteEntry(){
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Form", "Create a  Form", Form.class, new SimpleFactory(
						Form.class),
				ImagesUtils.getImageDescriptor("palette/form.png"),
				ImagesUtils.getImageDescriptor("palette/form.png"));
		return combined;
	}
	public EditPart createEditPart(Object model){
		if(model instanceof Form)
			return new BoxPart();
		return null;
	}
	public Class<? extends AuroraComponent> clazz() {
		return Form.class;
	}
	public AuroraComponent createComponent(String type) {
		String t = Form.FORM;
		if (t.equalsIgnoreCase(type)) {
			Form c = new Form();
			c.setType(t);
			return c;
		}
		return null;
	}
}

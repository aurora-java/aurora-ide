package aurora.ide.meta.gef.editors.components.input;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.InputPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.TextField;

public class TextFieldCreator extends ComponentCreator {

	public TextFieldCreator() {
	}
	public PaletteEntry createPaletteEntry(){
		CombinedTemplateCreationEntry	combined = new CombinedTemplateCreationEntry(
				"TextField", "Create a new TextField", TextField.class,
				new SimpleFactory(TextField.class) 
//				{
//					public Object getNewObject() {
//						Input newObject = (Input) super.getNewObject();
//						newObject.setType(Input.TEXT);
//						return newObject;
//					}
//				}
				, PrototypeImagesUtils.getImageDescriptor("palette/input_edit.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/input_edit.png"));
		return combined;
	}
	public EditPart createEditPart(Object model){
		if(model instanceof TextField){
			return new InputPart();
		}
//		if(model instanceof Input){
//			return new InputPart();
//		}
		return null;
	}
	public Class<? extends AuroraComponent> clazz() {
		return TextField.class;
	}
	public AuroraComponent createComponent(String type) {
		String t = TextField.TEXT;
		if (t.equalsIgnoreCase(type)) {
			TextField c = new TextField();
			c.setComponentType(t);
			return c;
		}
		return null;
	}
}

package aurora.ide.meta.gef.editors.components.button;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.parts.ButtonPart;

public class ButtonCreator extends ComponentCreator {

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Button", "Create a Button", clazz(),
				new SimpleFactory(clazz()),
				ImagesUtils.getImageDescriptor("palette/toolbar_btn_01.png"),
				ImagesUtils.getImageDescriptor("palette/toolbar_btn_01.png"));
		return combined;

	}

	public Class<? extends AuroraComponent> clazz() {
		return Button.class;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof Button)
			return new ButtonPart();
		return null;
	}
	public AuroraComponent createComponent(String type){
		if(Button.BUTTON.equalsIgnoreCase(type)) {
			Button button = new Button();
			button.setType(Button.BUTTON);
			return button;
		}
		return null;
	}

}

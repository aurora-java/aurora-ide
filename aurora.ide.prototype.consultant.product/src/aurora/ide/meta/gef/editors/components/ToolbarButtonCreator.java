package aurora.ide.meta.gef.editors.components;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.parts.ToolbarButtonPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.ToolbarButton;

public class ToolbarButtonCreator extends ComponentCreator {

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Toolbar Button", "Create a Toolbar Button", clazz(),
				new SimpleFactory(clazz()),
				PrototypeImagesUtils.getImageDescriptor("palette/toolbar_btn_01.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/toolbar_btn_01.png"));
		return combined;

	}

	public Class<? extends AuroraComponent> clazz() {
		return ToolbarButton.class;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof ToolbarButton)
			return new ToolbarButtonPart();
		return null;
	}
	public AuroraComponent createComponent(String type){
		if(ToolbarButton.TOOLBAR_BUTTON.equalsIgnoreCase(type)) {
			ToolbarButton button = new ToolbarButton();
			button.setComponentType(ToolbarButton.TOOLBAR_BUTTON);
			return button;
		}
		return null;
	}

}

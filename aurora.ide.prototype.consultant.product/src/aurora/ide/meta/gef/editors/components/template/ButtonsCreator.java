package aurora.ide.meta.gef.editors.components.template;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.message.Messages;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.HBox;

public class ButtonsCreator extends ComponentCreator {

	public ButtonsCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Buttons", "Create Buttons", clazz(), //$NON-NLS-1$ //$NON-NLS-2$
				new SimpleFactory(clazz()) {
					public Object getNewObject() {
						return createButtons();
					}
				},
				PrototypeImagesUtils
						.getImageDescriptor("palette/toolbar_btn_01.png"), //$NON-NLS-1$
				PrototypeImagesUtils
						.getImageDescriptor("palette/toolbar_btn_01.png")); //$NON-NLS-1$
		return combined;
	}

	protected Object createButtons() {
		HBox hb = new HBox();
		Button child = new Button();
		child.setText(Messages.ButtonsCreator_4);
		hb.addChild(child);
		child = new Button();
		child.setText(Messages.ButtonsCreator_5);
		hb.addChild(child);
		return hb;
	}

	public Class<? extends AuroraComponent> clazz() {
		return Button.class;
	}
}

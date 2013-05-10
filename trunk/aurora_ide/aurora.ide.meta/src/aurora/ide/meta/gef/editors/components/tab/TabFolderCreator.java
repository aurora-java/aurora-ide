package aurora.ide.meta.gef.editors.components.tab;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.TabFolderPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.TabFolder;

public class TabFolderCreator extends ComponentCreator {

	public TabFolderCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Tab Folder", "Create a  TabFolder", TabFolder.class,
				new SimpleFactory(TabFolder.class),
				PrototypeImagesUtils.getImageDescriptor("palette/tabfolder.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/tabfolder.png"));
		return combined;
	}

	public Class<? extends AuroraComponent> clazz() {
		return TabFolder.class;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof TabFolder)
			return new TabFolderPart();
		return null;
	}
	public AuroraComponent createComponent(String type) {
		String t = TabFolder.TAB_PANEL;
		if (t.equalsIgnoreCase(type)) {
			TabFolder c = new TabFolder();
			c.setComponentType(t);
			return c;
		}
		return null;
	}

}

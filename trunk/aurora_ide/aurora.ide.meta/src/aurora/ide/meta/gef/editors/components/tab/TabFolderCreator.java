package aurora.ide.meta.gef.editors.components.tab;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.parts.TabFolderPart;

public class TabFolderCreator extends ComponentCreator {

	public TabFolderCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Tab Folder", "Create a  TabFolder", TabFolder.class,
				new SimpleFactory(TabFolder.class),
				ImagesUtils.getImageDescriptor("palette/tabfolder.png"),
				ImagesUtils.getImageDescriptor("palette/tabfolder.png"));
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

}

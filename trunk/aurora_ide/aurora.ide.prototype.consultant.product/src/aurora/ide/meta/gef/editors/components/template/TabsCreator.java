package aurora.ide.meta.gef.editors.components.template;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.plugin.source.gen.screen.model.TabFolder;
import aurora.plugin.source.gen.screen.model.TabItem;

public class TabsCreator extends ComponentCreator {

	public TabsCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Tabs ", "Create  Tabs", TabFolder.class, new SimpleFactory(
						TabFolder.class) {

					@Override
					public Object getNewObject() {
						return createTabs();
					}

				},
				PrototypeImagesUtils
						.getImageDescriptor("palette/tabfolder.png"),
				PrototypeImagesUtils
						.getImageDescriptor("palette/tabfolder.png"));
		return combined;
	}

	public TabFolder createTabs() {
		TabFolder tf = new TabFolder();
		TabItem child = new TabItem();
		tf.addChild(child);
		child = new TabItem();
		tf.addChild(child);
		return tf;
	}
}

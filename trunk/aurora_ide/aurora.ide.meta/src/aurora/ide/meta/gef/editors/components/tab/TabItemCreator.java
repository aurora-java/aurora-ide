package aurora.ide.meta.gef.editors.components.tab;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.TabBody;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.link.TabRef;
import aurora.ide.meta.gef.editors.parts.TabBodyPart;
import aurora.ide.meta.gef.editors.parts.TabFolderPart;
import aurora.ide.meta.gef.editors.parts.TabItemPart;

public class TabItemCreator extends ComponentCreator {

	public TabItemCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Tab Item", "Create a  TabItem", TabItem.class,
				new SimpleFactory(TabItem.class),
				ImagesUtils.getImageDescriptor("palette/tabitem.png"),
				ImagesUtils.getImageDescriptor("palette/tabitem.png"));
		return combined;
	}

	public Class<? extends AuroraComponent> clazz() {
		return TabItem.class;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof TabItem)
			return new TabItemPart();
		if (model instanceof TabBody)
			return new TabBodyPart();
		return null;
	}
	public AuroraComponent createComponent(String type) {
		String t = TabItem.TAB;
		if (t.equalsIgnoreCase(type)) {
			TabItem c = new TabItem();
			c.setType(t);
			return c;
		}
		t = TabBody.TAB_BODY;
		if (t.equalsIgnoreCase(type)) {
			TabBody c = new TabBody();
			c.setType(t);
			return c;
		}
		t = TabRef.TABREF;
		if (t.equalsIgnoreCase(type)) {
			TabRef c = new TabRef();
			c.setType(t);
			return c;
		}
		return null;
	}

}

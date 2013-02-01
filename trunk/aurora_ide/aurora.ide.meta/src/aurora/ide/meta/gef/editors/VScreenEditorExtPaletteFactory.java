package aurora.ide.meta.gef.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;

import aurora.ide.meta.extensions.ExtensionComponent;
import aurora.ide.meta.extensions.ExtensionLoader;
import aurora.ide.meta.extensions.PaletteCategory;

public class VScreenEditorExtPaletteFactory {

	private static List<PaletteContainer> createCategories(PaletteRoot root,
			EditorMode editorMode) {

		List<PaletteContainer> categories = new ArrayList<PaletteContainer>();
		categories.add(createControlGroup(root));
		List<PaletteCategory> paletteCategories = ExtensionLoader
				.getPaletteCategories();
		for (PaletteCategory pc : paletteCategories) {
			if (pc.getFilter().isShowCategory(editorMode)) {
				PaletteContainer createDrawer = createDrawer(pc);
				if (createDrawer != null) {
					categories.add(createDrawer);
				}
			}
		}
		return categories;
	}

	private static PaletteContainer createDrawer(PaletteCategory pc) {
		PaletteDrawer drawer = new PaletteDrawer(pc.getLabel(), null);
		List<PaletteEntry> entries = new ArrayList<PaletteEntry>();
		List<ExtensionComponent> extensionComponents = ExtensionLoader
				.getExtensionComponents();
		for (ExtensionComponent ec : extensionComponents) {
			if (pc.getId().equalsIgnoreCase(ec.getCategoryId())) {
				PaletteEntry paletteEntry = ec.getCreator()
						.createPaletteEntry();
				if (paletteEntry != null)
					entries.add(paletteEntry);
			}
		}
		drawer.addAll(entries);
		return drawer;
	}

	private static PaletteContainer createControlGroup(PaletteRoot root) {
		PaletteGroup controlGroup = new PaletteGroup("Control Group");

		List<PaletteEntry> entries = new ArrayList<PaletteEntry>();

		ToolEntry tool = new SelectionToolEntry();
		entries.add(tool);
		root.setDefaultEntry(tool);

		// tool = new MarqueeToolEntry();
		// entries.add(tool);

		PaletteSeparator sep = new PaletteSeparator(
				"aurora.ide.meta.gef.editors");
		sep.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
		entries.add(sep);

		controlGroup.addAll(entries);
		return controlGroup;
	}

	public static PaletteRoot createPalette(EditorMode editorMode) {
		PaletteRoot flowPalette = new PaletteRoot();
		flowPalette.addAll(createCategories(flowPalette, editorMode));
		return flowPalette;
	}

}

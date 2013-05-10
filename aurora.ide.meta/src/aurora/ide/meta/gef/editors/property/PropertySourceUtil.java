package aurora.ide.meta.gef.editors.property;

import java.util.HashMap;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.FieldSet;
import aurora.plugin.source.gen.screen.model.Form;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.HBox;
import aurora.plugin.source.gen.screen.model.QueryForm;
import aurora.plugin.source.gen.screen.model.TabFolder;
import aurora.plugin.source.gen.screen.model.TabItem;
import aurora.plugin.source.gen.screen.model.VBox;

public class PropertySourceUtil {
	private static final HashMap<IPropertySource2, IPropertySource> map = new HashMap<IPropertySource2, IPropertySource>(
			128);

	// public static IPropertySource translate(final IPropertySource2 ps2,
	// CommandStack cmdStack) {
	// IPropertySource ps = map.get(ps2);
	// if (ps == null) {
	// ps = new ChangePropertyCommand(ps2, cmdStack);
	// map.put(ps2, ps);
	// }
	// return ps;
	// }

	public static Image getImageOf(AuroraComponent ac) {
		if (ac instanceof Form)
			return PrototypeImagesUtils.getImage("palette/form.png");
		else if (ac instanceof VBox)
			return PrototypeImagesUtils.getImage("palette/vbox.png");
		else if (ac instanceof HBox)
			return PrototypeImagesUtils.getImage("palette/hbox.png");
		else if (ac instanceof FieldSet)
			return PrototypeImagesUtils.getImage("palette/fieldset.png");
		else if (ac instanceof Grid)
			return PrototypeImagesUtils.getImage("palette/grid.png");
		else if (ac instanceof TabFolder)
			return PrototypeImagesUtils.getImage("palette/tabfolder.png");
		else if (ac instanceof TabItem)
			return PrototypeImagesUtils.getImage("palette/tabitem.png");
		if (ac instanceof QueryForm)
			return PrototypeImagesUtils.getImage("palette/form.png");
		return null;
	}

	public static IPropertySource translate(AuroraComponent component,
			CommandStack commandStack) {
		return new DefaultPropertySource(component, commandStack);
	}

}

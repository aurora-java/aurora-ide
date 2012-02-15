package aurora.ide.meta.gef.editors.property;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.VBox;
import aurora.ide.meta.gef.editors.models.commands.ChangePropertyCommand;

import java.util.HashMap;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;

public class PropertySourceUtil {
	private static final HashMap<IPropertySource2, IPropertySource> map = new HashMap<IPropertySource2, IPropertySource>(
			128);

	public static IPropertySource translate(final IPropertySource2 ps2,
			CommandStack cmdStack) {
		IPropertySource ps = map.get(ps2);
		if (ps == null) {
			ps = new ChangePropertyCommand(ps2, cmdStack);
			map.put(ps2, ps);
		}
		return ps;
	}

	public static Image getImageOf(AuroraComponent ac) {
		if (ac instanceof Form)
			return ImagesUtils.getImage("palette/form.png");
		else if (ac instanceof VBox)
			return ImagesUtils.getImage("palette/vbox.png");
		else if (ac instanceof HBox)
			return ImagesUtils.getImage("palette/hbox.png");
		else if (ac instanceof FieldSet)
			return ImagesUtils.getImage("palette/fieldset.png");
		else if (ac instanceof Grid)
			return ImagesUtils.getImage("palette/grid.png");
		else if (ac instanceof TabFolder)
			return ImagesUtils.getImage("palette/tabfolder.png");
		else if (ac instanceof TabItem)
			return ImagesUtils.getImage("palette/tabitem.png");
		return null;
	}

}

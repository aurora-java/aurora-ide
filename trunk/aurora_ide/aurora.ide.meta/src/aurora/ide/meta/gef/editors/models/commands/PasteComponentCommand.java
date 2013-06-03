package aurora.ide.meta.gef.editors.models.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.ContainerPart;
import aurora.ide.meta.gef.editors.parts.GridColumnPart;
import aurora.ide.meta.gef.editors.request.PasteComponentRequest;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.io.CompositeMap2Object;
import aurora.plugin.source.gen.screen.model.io.Object2CompositeMap;

public class PasteComponentCommand extends Command {

	private PasteComponentRequest req;

	private Container target;

	private List<AuroraComponent> pasteComponents;

	public PasteComponentCommand(PasteComponentRequest req, Container target) {
		super();
		this.req = req;
		this.target = target;
		this.setLabel("Paste Components");
	}

	@Override
	public boolean canExecute() {
		List<AuroraComponent> pasteParts = req.getPasteAuroraComponent();
		for (AuroraComponent ac : pasteParts) {
			if (target.isResponsibleChild(ac) == false)
				return false;
			if (ac instanceof ScreenBody) {
				return false;
			}
		}
		return pasteParts.size() > 0;
	}

	@Override
	public void execute() {
		pasteComponents = new ArrayList<AuroraComponent>();
		List<AuroraComponent> pasteAuroraComponent = req
				.getPasteAuroraComponent();
		ComponentPart selectionComponentPart = req.getSelectionComponentPart();
		int insertIDX = -1;
		if (selectionComponentPart instanceof GridColumnPart
				|| (selectionComponentPart instanceof ContainerPart) == false) {
			AuroraComponent component = selectionComponentPart.getComponent();
			Container parent = component.getParent();
			insertIDX = parent.getChildren().indexOf(component);
		}
		for (AuroraComponent c : pasteAuroraComponent) {
			AuroraComponent cloneObject = cloneObject(c);
			pasteComponents.add(cloneObject);
			if (insertIDX == -1) {
				target.addChild(cloneObject);
			} else {
				insertIDX++;
				target.addChild(cloneObject, insertIDX);
			}
		}
	}

	private AuroraComponent cloneObject(AuroraComponent ac) {
		Object2CompositeMap o2c = new Object2CompositeMap();
		CompositeMap map = o2c.createCompositeMap(ac);
		CompositeMap2Object c2o = new CompositeMap2Object();
		AuroraComponent createObject = c2o.createObject(map);
		return createObject;
	}

	@Override
	public void redo() {
		super.redo();
	}

	@Override
	public void undo() {
		if (pasteComponents != null) {
			for (AuroraComponent ac : pasteComponents) {
				target.removeChild(ac);
			}
		}
	}

}

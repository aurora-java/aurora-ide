package aurora.ide.meta.gef.editors.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.actions.ActionFactory;

import uncertain.composite.CompositeMap;

import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.ViewDiagramPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.io.CompositeMap2Object;
import aurora.plugin.source.gen.screen.model.io.Object2CompositeMap;

public class CopyComponentsAction extends SelectionAction {

	private GraphicalEditor editor;

	public CopyComponentsAction(GraphicalEditor part) {
		super(part);
		editor = part;
	}

	protected boolean calculateEnabled() {
		return this.getSelectedObjects().isEmpty() == false;
	}

	protected void init() {
		setId(ActionFactory.COPY.getId());
		setText("Copy");
	}

	/**
	 * Sets the default {@link Clipboard Clipboard's} contents to be the
	 * currently selected template.
	 */
	public void run() {

		List<ComponentPart> pasteParts = getPasteParts();
		List<AuroraComponent> pasteComponents = new ArrayList<AuroraComponent>();
		for (ComponentPart cp : pasteParts) {
			AuroraComponent c = cp.getComponent();
			AuroraComponent cloneObject = this.cloneObject(c);
			pasteComponents.add(cloneObject);
		}
		
		Clipboard.getDefault().setContents(pasteComponents);
	}

	private AuroraComponent cloneObject(AuroraComponent ac) {
		Object2CompositeMap o2c = new Object2CompositeMap();
		CompositeMap map = o2c.createCompositeMap(ac);
		CompositeMap2Object c2o = new CompositeMap2Object();
		AuroraComponent createObject = c2o.createObject(map);
		return createObject;
	}

	
	
	private List<ComponentPart> getPasteParts() {
		List<ComponentPart> pasteParts = new ArrayList<ComponentPart>();
		List clipboardContents = getSelectedObjects();
		for (Object object : clipboardContents) {
			if (object instanceof ComponentPart) {
				ComponentPart part = (ComponentPart) object;
				if ((part instanceof ViewDiagramPart) == false
						&& isOverlap(part) == false) {
					pasteParts.add(part);
				}
			}
		}
		return pasteParts;
	}

	private boolean isOverlap(ComponentPart part) {
		EditPart parent = part.getParent();
		if (parent instanceof ViewDiagramPart) {
			return false;
		}
		List clipboardContents = getSelectedObjects();
		if (clipboardContents.contains(parent)) {
			return true;
		}
		if ((parent instanceof ComponentPart) == false) {
			return false;
		}
		return isOverlap((ComponentPart) parent);
	}

}

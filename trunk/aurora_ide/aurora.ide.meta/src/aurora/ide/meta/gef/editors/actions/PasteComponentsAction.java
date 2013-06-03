package aurora.ide.meta.gef.editors.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionFactory;

import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.ContainerPart;
import aurora.ide.meta.gef.editors.request.PasteComponentRequest;
import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class PasteComponentsAction extends SelectionAction {

	private GraphicalEditor editor;

	public PasteComponentsAction(GraphicalEditor part) {
		super(part);
		editor = part;
	}

	protected boolean calculateEnabled() {
		return this.getSelectedObjects().size() == 1
				&& getClipboardContents() != null;
	}

	protected void init() {
		setId(ActionFactory.PASTE.getId());
		setText("Paste");
	}

	private List<AuroraComponent> getPasteAuroraComponent() {
		List<AuroraComponent> pasteParts = new ArrayList<AuroraComponent>();
		List clipboardContents = getClipboardContents();
		if (clipboardContents != null){
			for (Object object : clipboardContents) {
				if (object instanceof AuroraComponent) {
					pasteParts.add((AuroraComponent) object);
				}
			}
		}
		return pasteParts;
	}

	public void run() {
		if (calculateEnabled() == false)
			return;
		Object object = this.getSelectedObjects().get(0);
		if (object instanceof ComponentPart) {
			ComponentPart part = (ComponentPart) object;
			if ((object instanceof ContainerPart) == false) {
				part = (ComponentPart) ((ComponentPart) object).getParent();
			}
			PasteComponentRequest pcr = new PasteComponentRequest();
			org.eclipse.swt.graphics.Point p = getGraphicalControl().toControl(
					Display.getCurrent().getCursorLocation());
			pcr.setLocation(new Point(p.x, p.y));
			pcr.setSelectionComponentPart((ComponentPart) object);
			pcr.setPasteAuroraComponent(this.getPasteAuroraComponent());
			Command command = part.getCommand(pcr);
			if (command != null && command.canExecute()) {
				this.getCommandStack().execute(command);
			}
		}
		// List<ComponentPart> pasteParts = this.getPasteParts();
		// for (ComponentPart componentPart : pasteParts) {
		// AuroraComponent component = componentPart.getComponent();
		// cloneObject(component);
		// }
		// Object2CompositeMap o2c = new Object2CompositeMap();
		// CompositeMap map = o2c.createCompositeMap(diagram);
		// XMLOutputter.saveToFile(file, map);
		// getCommandStack().markSaveLocation();

		// CompositeMap loadFile = CompositeMapUtil.loadFile(file);
		// if (loadFile != null) {
		// CompositeMap2Object c2o = new CompositeMap2Object();
		// diagram = c2o.createScreenBody(loadFile);
		// } else {
		// diagram = new ScreenBody();
		// }

		// System.out.println("paste : " + clipboardContents);
		// org.eclipse.swt.graphics.Point p = getGraphicalControl().toControl(
		// Display.getCurrent().getCursorLocation());
		// System.out.println("selection : " + this.getSelection());
		// System.out.println("paste Location :  " + p);
	}

	private Control getGraphicalControl() {
		GraphicalViewer viewer = (GraphicalViewer) editor
				.getAdapter(GraphicalViewer.class);
		return viewer.getControl();
	}

	protected List getClipboardContents() {
		Object contents = Clipboard.getDefault().getContents();
		if (contents instanceof List)
			return (List) contents;
		return null;
	}

}

package aurora.ide.prototype.consultant.product.action;

import java.util.List;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.prototype.consultant.product.demonstrate.DemonstrateSettingManager;
import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class DemonstrateSettingAction extends SelectionAction {

	public static final String ID = "aurora.ide.meta.gef.editors.actions.DemonstrateSettingAction"; //$NON-NLS-1$

	private GraphicalEditor editor;

	public DemonstrateSettingAction(GraphicalEditor part) {
		super(part);
		editor = part;
		this.setId(ID);
		this.setText(Messages.DemonstrateSettingAction_0);
	}

	public void run() {
		if (this.getAuroraComponent() == null)
			return;
		DemonstrateSettingManager m = new DemonstrateSettingManager(
				this.getAuroraComponent());
		m.setCommandStack((CommandStack) editor.getAdapter(CommandStack.class));
		Shell shell = editor.getSite().getShell();
		m.openSettingWizard(shell);
	}

	private AuroraComponent getAuroraComponent() {
		List selectedObjects = this.getSelectedObjects();
		Object object = selectedObjects.get(0);
		if (object instanceof ComponentPart) {
			return ((ComponentPart) object).getComponent();
		}
		return null;
	}

	@Override
	protected boolean calculateEnabled() {
		List selectedObjects = this.getSelectedObjects();
		if (selectedObjects.size() != 1) {
			return false;
		}
		Object object = selectedObjects.get(0);
		if (object instanceof ComponentPart) {
			return new DemonstrateSettingManager(
					((ComponentPart) object).getComponent())
					.isWillDemonstrate();
		}
		return false;
	}

}

package aurora.ide.prototype.consultant.product.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.ConsultantVScreenEditor;
import aurora.ide.meta.gef.editors.consultant.property.ConsultantPropertyManager;
import aurora.ide.meta.gef.editors.consultant.property.FSDPropertyWizard;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class FSDPropertyEditAction extends SelectionAction {

	public static final String ID = "aurora.ide.meta.gef.editors.actions.FSDPropertyEditAction"; //$NON-NLS-1$

	private GraphicalEditor editor;

	private ConsultantPropertyManager propertyManager;

	public FSDPropertyEditAction(GraphicalEditor part) {
		super(part);
		editor = part;
		if (editor instanceof ConsultantVScreenEditor) {

			propertyManager = ((ConsultantVScreenEditor) editor)
					.getPropertyManager();
		}
		this.setId(ID);
		this.setText(Messages.FSDPropertyEditAction_1);
	}

	public void run() {
		Shell shell = editor.getSite().getShell();
		FSDPropertyWizard wd = new FSDPropertyWizard(shell,
				getValues(getPropertyDescs()));
		int open = wd.open();
		if (open == WizardDialog.OK) {
			Map<IPropertyDescriptor, String> values = wd.getValues();
			Set<IPropertyDescriptor> keySet = values.keySet();
			AuroraComponent ac = getAuroraComponent();
			if (ac == null)
				return;
			for (IPropertyDescriptor pd : keySet) {
				propertyManager.setPropertyValue(pd.getId(), values.get(pd),
						ac);
			}
		}
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
		if (object instanceof ComponentPart && propertyManager != null) {
			IPropertyDescriptor[] fds = propertyManager
					.getFSDPropertyDescriptors((ComponentPart) object);
			if (fds.length > 0) {
				return true;
			}
		}
		return false;
	}

	private Map<IPropertyDescriptor, String> getValues(IPropertyDescriptor[] pds) {
		Map<IPropertyDescriptor, String> values = new HashMap<IPropertyDescriptor, String>();
		List selectedObjects = this.getSelectedObjects();
		Object object = selectedObjects.get(0);
		if (object instanceof ComponentPart) {
			for (IPropertyDescriptor pd : pds) {
				String s = ((ComponentPart) object).getComponent()
						.getStringPropertyValue("" + pd.getId()); //$NON-NLS-1$
				values.put(pd, s);
			}
		}
		return values;
	}

	private IPropertyDescriptor[] getPropertyDescs() {
		List selectedObjects = this.getSelectedObjects();
		Object object = selectedObjects.get(0);
		if (object instanceof ComponentPart && propertyManager != null) {
			IPropertyDescriptor[] fds = propertyManager
					.getFSDPropertyDescriptors((ComponentPart) object);
			return fds;
		}
		return new IPropertyDescriptor[0];
	}

}

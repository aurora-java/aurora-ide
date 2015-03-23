package aurora.ide.component.wizard;

import aurora.ide.editor.core.IViewer;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.node.action.ActionInfo;
import aurora.ide.node.action.ActionListener;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;

public class DataSetsWizardAction implements IWizardAction{

	public ActionListener[] createActions(ActionInfo actionProperties) {
		ActionListener[] actions = new ActionListener[1];
		IViewer viewer = actionProperties.getViewer();
		CompositeMap parent = actionProperties.getCurrentNode();
		QualifiedName  gridQN = new QualifiedName(AuroraConstant.ApplicationUri,"dataSet");
		actions[0] =new AddDataSetAction(viewer, parent,gridQN,ActionListener.DefaultImage);
		actions[0].setText(LocaleMessage.getString("dataset.wizard"));
		return actions;
	}

}

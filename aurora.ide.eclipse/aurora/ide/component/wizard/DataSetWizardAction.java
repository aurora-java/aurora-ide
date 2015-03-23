package aurora.ide.component.wizard;

import aurora.ide.editor.core.IViewer;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.node.action.ActionInfo;
import aurora.ide.node.action.ActionListener;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;

public class DataSetWizardAction implements IWizardAction{

	public ActionListener[] createActions(ActionInfo actionInfo) {
		ActionListener[] actions = new ActionListener[2];
		IViewer viewer = actionInfo.getViewer();
		CompositeMap currentNode = actionInfo.getCurrentNode();
		QualifiedName  gridQN = new QualifiedName(AuroraConstant.ApplicationUri,"grid");
		
		actions[0] =new CreateGridFromDataSetAction(viewer, currentNode,gridQN,ActionListener.DefaultImage);
		actions[0].setText(LocaleMessage.getString("create.grid.from.dataset"));
		
		QualifiedName  formQN = new QualifiedName(AuroraConstant.ApplicationUri,"form");
		actions[1] = new CreateFormFromDataSetAction(viewer, currentNode,formQN,ActionListener.DefaultImage);
		actions[1].setText(LocaleMessage.getString("create.form.from.dataset"));
		return actions;
	}

}

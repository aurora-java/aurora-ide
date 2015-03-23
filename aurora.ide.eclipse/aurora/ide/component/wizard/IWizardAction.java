/**
 * 
 */
package aurora.ide.component.wizard;

import aurora.ide.node.action.ActionInfo;
import aurora.ide.node.action.ActionListener;


/**
 * @author linjinxiao
 *
 */
public interface IWizardAction {

	public ActionListener[] createActions(ActionInfo actionProperties);
}

/**
 * 
 */
package aurora.ide.component.wizard;


import java.util.Iterator;
import java.util.List;


import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.node.action.ActionInfo;
import aurora.ide.node.action.ActionListener;
import aurora.ide.node.action.AddElementAction;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Element;
import uncertain.schema.Wizard;

/**
 * @author linjinxiao
 * 
 */
public class ActionsFactory {

	private static ActionsFactory instance;

	public synchronized static ActionsFactory getInstance() {
		if (instance == null)
			instance = new ActionsFactory();
		return instance;
	}

	public ActionListener[] createActions(ActionInfo actionInfo) throws ApplicationException {
		CompositeMap currentNode = actionInfo.getCurrentNode();
//		Element element = LoadSchemaManager.getSchemaManager().getElement(
//				currentNode);
		Element element = CompositeMapUtil.getElement(currentNode);
		if (element == null) {
			DialogUtil.showWarningMessageBox(LocaleMessage
					.getString("undefined.self.element"));
			return null;
		}
		ActionListener[] wizardActionListeners = getWizardActions(
				actionInfo, element);

		List childElements = CompositeMapUtil
				.getAvailableChildElements(currentNode);
		ActionListener[] childActionListeners = null;
		if (childElements != null) {
			childActionListeners = new ActionListener[childElements.size()];
			Iterator ite = childElements.iterator();
			for (int i = 0; ite.hasNext(); i++) {
				Object object = ite.next();
				if (!(object instanceof Element))
					continue;
				Element ele = (Element) object;
				final QualifiedName qName = ele.getQName();
				childActionListeners[i] = new AddElementAction(actionInfo
						.getViewer(), currentNode, qName,
						ActionListener.DefaultImage
								| ActionListener.DefaultTitle);

			}
		}
		return collectActionListeners(wizardActionListeners,
				childActionListeners);
	}

	private ActionListener[] getWizardActions(
			ActionInfo actionProperties, Element element) throws ApplicationException {
		QualifiedName wizardQualifiedName = element.getWizardQName();
		Wizard wizard = LoadSchemaManager.getSchemaManager().getWizard(
				wizardQualifiedName);
		if (wizard == null)
			return null;
		String cls_name = wizard.getInstanceClass();
		Object wizardObject;
		Class cls;
		try {
			cls = Class.forName(cls_name);
		} catch (ClassNotFoundException e) {
			throw new ApplicationException("找不到类,请检查类名:",e);
		}
		try {
			wizardObject = cls.newInstance();
		} catch (Exception e) {
			throw new ApplicationException("实例化错误:",e);
		} 
		IWizardAction wizardAction = (IWizardAction) wizardObject;
		ActionListener[] wizardActionListeners = wizardAction
				.createActions(actionProperties);
		return wizardActionListeners;
	}

	private ActionListener[] collectActionListeners(
			ActionListener[] wizardActionListeners,
			ActionListener[] childActionListeners) {
		int count = 0;
		int wizardCount = 0;
		int childCount = 0;
		if (wizardActionListeners != null && wizardActionListeners.length > 0) {
			wizardCount = wizardActionListeners.length;
		}
		if (childActionListeners != null && childActionListeners.length > 0) {
			childCount = childActionListeners.length;
		}
		count = wizardCount + childCount;
		ActionListener[] totalActionListeners = new ActionListener[count];
		if (wizardCount > 0) {
			System.arraycopy(wizardActionListeners, 0, totalActionListeners, 0,
					wizardCount);
		}
		if (childCount > 0) {
			System.arraycopy(childActionListeners, 0, totalActionListeners,
					wizardCount, childCount);
		}
		return totalActionListeners;
	}

	public void addActionsToMenu(Menu menu, ActionInfo actionProperties) throws ApplicationException{
		ActionListener[] actionListeners = createActions(actionProperties);
		if(actionListeners == null)
			return;
		for (int i = 0; i < actionListeners.length; i++) {
			ActionListener action = actionListeners[i];
			MenuItem itemPush = new MenuItem(menu, SWT.PUSH);
			itemPush.addListener(SWT.Selection, action);
			itemPush.setText(action.getText());
			itemPush.setImage(action.getDefaultImage());
		}
	}

	public void addActionsToMenuManager(MenuManager menuManager,
			ActionInfo actionProperties) throws ApplicationException {
		ActionListener[] actionListeners = createActions(actionProperties);
		if (actionListeners == null)
			return;
		for (int i = 0; i < actionListeners.length; i++) {
			ActionListener action = actionListeners[i];
			menuManager.add(action);
		}
	}

}

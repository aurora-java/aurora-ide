package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.models.link.Parameter;
import aurora.ide.meta.gef.editors.property.DialogEditableObject;
import aurora.ide.meta.gef.editors.property.PropertySourceUtil;
import aurora.ide.meta.gef.i18n.Messages;

public class ButtonClicker extends AuroraComponent implements
		DialogEditableObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1177281488586303137L;

	public static final String DEFAULT = ""; //$NON-NLS-1$

	static final public String B_SEARCH = "query"; //$NON-NLS-1$
	static final public String B_RESET = "reset"; //$NON-NLS-1$
	static final public String B_SAVE = "save";
	static final public String B_CLOSE = "close"; //$NON-NLS-1$
	static final public String B_OPEN = "open"; //$NON-NLS-1$
	static final public String B_CUSTOM = "custom"; //$NON-NLS-1$

	public static final String[] action_ids = { B_SEARCH, B_RESET, B_SAVE,
			B_OPEN, B_CLOSE, B_CUSTOM };

	public static final String[] action_texts = { Messages.ButtonClicker_Query,
			Messages.ButtonClicker_Reset, Messages.ButtonClicker_Save,
			Messages.ButtonClicker_Open, Messages.ButtonClicker_Close,
			Messages.ButtonClicker_Custom };

	private String actionID = DEFAULT;
	// b_open
	private String openPath;
	private List<Parameter> paras = new ArrayList<Parameter>();
	// b_close
	private String closeWindowID;
	// b_run
	private String function;

	private Button button;

	// b_save,b_search,b_reset
	private AuroraComponent targetComponent;

	public ButtonClicker() {

	}

	public AuroraComponent getTargetComponent() {
		return targetComponent;
	}

	public void setTargetComponent(AuroraComponent targetComponent) {
		this.targetComponent = targetComponent;
	}

	public String getActionID() {
		return actionID;
	}

	public void setActionID(String actionID) {
		this.actionID = actionID;
	}

	public String getActionText() {
		for (int i = 0; i < action_ids.length; i++)
			if (action_ids[i].equals(actionID))
				return action_texts[i];
		return ""; //$NON-NLS-1$
	}

	public String getOpenPath() {
		return openPath;
	}

	public void setOpenPath(String openPath) {
		this.openPath = openPath;
	}

	public String getCloseWindowID() {
		return closeWindowID;
	}

	public void setCloseWindowID(String closeWindowID) {
		this.closeWindowID = closeWindowID;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String func) {
		this.function = func;
	}

	public String getDescripition() {
		return getActionText();
	}

	public Object getContextInfo() {
		return button;
	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}

	@Override
	public ButtonClicker clone() {
		ButtonClicker bc = new ButtonClicker();
		bc.actionID = actionID;
		bc.button = button;
		bc.closeWindowID = closeWindowID;
		bc.openPath = openPath;
		bc.paras = new ArrayList<Parameter>();
		for (Parameter p : paras) {
			bc.paras.add(p.clone());
		}
		bc.function = function;
		bc.targetComponent = targetComponent;
		return bc;
	}

	public Image getDisplayImage() {
		if (targetComponent != null
				&& (B_SEARCH.equals(actionID) || B_SAVE.equals(actionID) || B_RESET
						.equals(actionID)))
			return PropertySourceUtil.getImageOf(targetComponent);
		return null;
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	public List<Parameter> getParameters() {
		return paras;
	}

	public void addParameter(Parameter para) {
		paras.add(para);
	}
}

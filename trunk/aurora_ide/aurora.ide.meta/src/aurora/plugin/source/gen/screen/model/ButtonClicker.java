package aurora.plugin.source.gen.screen.model;

import java.util.ArrayList;
import java.util.List;

import aurora.ide.meta.gef.editors.property.IDialogEditableObject;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

public class ButtonClicker extends AuroraComponent implements
		IDialogEditableObject {
	/**
	 * 
	 */
	public static final String DEFAULT = ""; //$NON-NLS-1$

	static final public String B_SEARCH = "query"; //$NON-NLS-1$
	static final public String B_RESET = "reset"; //$NON-NLS-1$
	static final public String B_SAVE = "save";
	static final public String B_CLOSE = "close"; //$NON-NLS-1$
	static final public String B_OPEN = "open"; //$NON-NLS-1$
	static final public String B_CUSTOM = "custom"; //$NON-NLS-1$

	public static final String[] action_ids = { B_SEARCH, B_RESET, B_SAVE,
			B_OPEN, B_CLOSE, B_CUSTOM };

	private List<Parameter> paras = new ArrayList<Parameter>();

	private Button button;

	public ButtonClicker() {
		this.setActionID(DEFAULT);
		this.setComponentType("inner_buttonclicker");
	}

	public AuroraComponent getTargetComponent() {
		return this
				.getAuroraComponentPropertyValue(ComponentInnerProperties.BUTTON_CLICK_TARGET_COMPONENT);
	}

	public void setTargetComponent(AuroraComponent targetComponent) {
		this.setPropertyValue(
				ComponentInnerProperties.BUTTON_CLICK_TARGET_COMPONENT,
				targetComponent);
	}

	public String getActionID() {
		return this
				.getStringPropertyValue(ComponentInnerProperties.BUTTON_CLICK_ACTIONID);
	}

	public void setActionID(String actionID) {
		this.setPropertyValue(ComponentInnerProperties.BUTTON_CLICK_ACTIONID,
				actionID);
	}

	public String getOpenPath() {
		return this
				.getStringPropertyValue(ComponentInnerProperties.BUTTON_CLICK_OPENPATH);
	}

	public void setOpenPath(String openPath) {
		this.setPropertyValue(ComponentInnerProperties.BUTTON_CLICK_OPENPATH,
				openPath);
	}

	public String getCloseWindowID() {
		return this
				.getStringPropertyValue(ComponentInnerProperties.BUTTON_CLICK_CLOSEWINDOWID);
	}

	public void setCloseWindowID(String closeWindowID) {
		this.setPropertyValue(
				ComponentInnerProperties.BUTTON_CLICK_CLOSEWINDOWID,
				closeWindowID);
	}

	public String getFunction() {
		return this
				.getStringPropertyValue(ComponentInnerProperties.BUTTON_CLICK_FUNCTION);
	}

	public void setFunction(String func) {
		this.setPropertyValue(ComponentInnerProperties.BUTTON_CLICK_FUNCTION,
				func);
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
		bc.setActionID(this.getActionID());
		// bc.actionID = actionID;
		bc.setButton(this.getButton());
		// bc.button = button;
		// bc.closeWindowID = closeWindowID;
		bc.setCloseWindowID(this.getCloseWindowID());
		// bc.openPath = openPath;
		bc.setOpenPath(this.getOpenPath());
		bc.paras = new ArrayList<Parameter>();
		for (Parameter p : paras) {
			bc.paras.add(p.clone());
		}
		// bc.function = function;
		bc.setFunction(this.getFunction());
		// bc.targetComponent = targetComponent;
		bc.setTargetComponent(this.getTargetComponent());
		// return bc;
		return bc;
	}

	public List<Parameter> getParameters() {
		return paras;
	}

	public void addParameter(Parameter para) {
		paras.add(para);
	}

	public Object getPropertyValue(String propId) {
		if (ComponentInnerProperties.BUTTON_CLICK_PARAMETERS.equals(propId)) {
			return paras;
		}
		Object propertyValue = super.getPropertyValue(propId);
		return propertyValue;
	}

	public void setPropertyValue(String propId, Object val) {
		if (ComponentInnerProperties.BUTTON_CLICK_PARAMETERS.equals(propId)
				&& val instanceof List) {
			paras = (List<Parameter>) val;
			return;
		}
		super.setPropertyValue(propId, val);
	}

	@Override
	public String getDescripition() {
		return getActionID();
	}

}

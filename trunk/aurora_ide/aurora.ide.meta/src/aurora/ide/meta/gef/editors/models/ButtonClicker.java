package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.DialogEditableObject;
import aurora.ide.meta.gef.editors.property.PropertySourceUtil;

public class ButtonClicker extends AuroraComponent implements
		DialogEditableObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1177281488586303137L;

	public static final String DEFAULT = "";

	static final public String B_SEARCH = "b_search";
	static final public String B_RESET = "b_reset";
	static final public String B_SAVE = "b_save";
	static final public String B_CLOSE = "b_close";
	static final public String B_OPEN = "b_open";

	public static final String[] action_ids = { B_SEARCH, B_RESET, B_SAVE,
			B_OPEN, B_CLOSE, DEFAULT };

	public static final String[] action_texts = { "查询", "重置", "保存", "打开", "关闭",
			"自定义" };

	private String actionID = action_ids[5];
	private String actionText = action_texts[5];

	// b_open
	private String openPath;
	// b_close
	private String closeWindowID;
	// b_run
	private String function;

	private Button button;

	// b_save,b_search,b_reset
	private AuroraComponent targetComponent;

	public ButtonClicker() {

	}

	@Override
	public void setSize(Dimension dim) {

	}

	@Override
	public void setBounds(Rectangle bounds) {
	}

	public AuroraComponent getTargetComponent() {
		return targetComponent;
	}

	public void setTargetComponent(AuroraComponent targetComponent) {
		this.targetComponent = targetComponent;
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return null;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		return null;
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
	}

	public String getActionID() {
		return actionID;
	}

	public void setActionID(String actionID) {
		this.actionID = actionID;
	}

	public String getActionText() {
		return actionText;
	}

	public void setActionText(String actionText) {
		this.actionText = actionText;
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
		bc.actionText = actionText;
		bc.button = button;
		bc.closeWindowID = closeWindowID;
		bc.openPath = openPath;
		bc.function = function;
		bc.targetComponent = targetComponent;
		return bc;
	}

	public Image getDisplayImage() {
		if (targetComponent == null || DEFAULT.equals(actionID))
			return null;
		return PropertySourceUtil.getImageOf(targetComponent);
	}
}

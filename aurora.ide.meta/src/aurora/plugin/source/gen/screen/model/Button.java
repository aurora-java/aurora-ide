package aurora.plugin.source.gen.screen.model;

import java.util.Arrays;

import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;
import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;

//import org.eclipse.draw2d.geometry.Dimension;
//import org.eclipse.draw2d.geometry.Rectangle;
//
//import aurora.ide.meta.gef.editors.property.ButtonClickEditDialog;
//import aurora.ide.meta.gef.editors.property.ComboPropertyDescriptor;
//import aurora.ide.meta.gef.editors.property.DialogPropertyDescriptor;
//import aurora.ide.meta.gef.editors.property.IntegerPropertyDescriptor;
//import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class Button extends AuroraComponent {
	public static final String BUTTON = "button";
	/**
	 * 
	 */
	public static final String ADD = "add"; //$NON-NLS-1$
	public static final String SAVE = "save"; //$NON-NLS-1$
	public static final String DELETE = "delete"; //$NON-NLS-1$
	public static final String CLEAR = "clear"; //$NON-NLS-1$
	public static final String EXCEL = "excel"; //$NON-NLS-1$
	public static final String DEFAULT = ""; //$NON-NLS-1$
	public static final String[] std_types = { DEFAULT, ADD, SAVE, DELETE,
			CLEAR, EXCEL };
	//	private static final String[] std_type_names = { "", Messages.Button_7, Messages.Button_8, Messages.Button_9, //$NON-NLS-1$
	// Messages.Button_10, Messages.Button_11 };
	public static final String BUTTON_TYPE = "type"; //$NON-NLS-1$
	public static final String BUTTON_TEXT = "text"; //$NON-NLS-1$
	public static final String BUTTON_CLICKER = "click"; //$NON-NLS-1$
	public static final String TOOLTIP = "title"; //$NON-NLS-1$

	// private static final IPropertyDescriptor[] std_pds = new
	// IPropertyDescriptor[] {
	//			new StringPropertyDescriptor(BUTTON_TEXT, "Text"), PD_NAME, //$NON-NLS-1$
	//			new IntegerPropertyDescriptor(WIDTH, "Width"), //$NON-NLS-1$
	//			new IntegerPropertyDescriptor(HEIGHT, "Height"), //$NON-NLS-1$
	//			new StringPropertyDescriptor(TOOLTIP, "Title"), //$NON-NLS-1$
	//			new ComboPropertyDescriptor(BUTTON_TYPE, "Type", std_type_names) }; //$NON-NLS-1$
	// private static final IPropertyDescriptor[] inner_pds = new
	// IPropertyDescriptor[] {
	//			new StringPropertyDescriptor(BUTTON_TEXT, "Text"), //$NON-NLS-1$
	// PD_NAME,
	//			new IntegerPropertyDescriptor(WIDTH, "Width"), //$NON-NLS-1$
	//			new IntegerPropertyDescriptor(HEIGHT, "Height"), //$NON-NLS-1$
	//			new StringPropertyDescriptor(TOOLTIP, "Title"), //$NON-NLS-1$
	//			new DialogPropertyDescriptor(BUTTON_CLICKER, "Click", //$NON-NLS-1$
	// ButtonClickEditDialog.class) };

	// private String buttonType = DEFAULT;
	//	private String text = BUTTON; //$NON-NLS-1$
	//	private String icon = ""; //$NON-NLS-1$
	//	private String title = ""; //$NON-NLS-1$
	// private AuroraComponent targetComponent;

	private ButtonClicker buttonClicker;

	public Button() {
		setSize(80, 20);
		this.setComponentType(BUTTON); //$NON-NLS-1$
		this.setButtonClicker(new ButtonClicker());
		this.setButtonType(DEFAULT);
		this.setText(BUTTON);
	}

	public void setSize(int width, int height) {
		if (isOnToolBar()) {
			height = 20;
			if (isStdButton())
				width = 48;
		}
		super.setSize(width, height);
	}

	// public void setBounds(Rectangle bounds) {
	// if (isOnToolBar()) {
	// bounds.height = 20;
	// if (isStdButton())
	// bounds.width = 48;
	// }
	// super.setBounds(bounds);
	// }

	public boolean isStdButton() {
		return Arrays.asList(std_types).indexOf(getButtonType()) > 0;
	}

	public String getButtonType() {
		// return buttonType;
		return this.getStringPropertyValue(ComponentProperties.type);
	}

	public String getIcon() {
		return this.getStringPropertyValue(ComponentProperties.icon);
		// return icon;
	}

	// public String getText() {
	// if (CLEAR.equals(buttonType))
	// return Messages.Button_30;
	// else if (ADD.equals(buttonType))
	// return Messages.Button_31;
	// else if (DELETE.equals(buttonType))
	// return Messages.Button_32;
	// else if (SAVE.equals(buttonType))
	// return Messages.Button_33;
	// else if (EXCEL.equals(buttonType))
	// return Messages.Button_34;
	// return text;
	// }

	public String getTitle() {
		// return title;
		return this.getStringPropertyValue(ComponentProperties.title);
	}

	// /**
	// *
	// * @deprecated
	// * @return
	// */
	// public AuroraComponent getTargetComponent() {
	// return targetComponent;
	// }

	public void setButtonType(String buttonType) {
		// if (eq(this.buttonType, buttonType))
		// return;
		// String oldV = this.buttonType;
		// this.buttonType = buttonType;
		// firePropertyChange(BUTTON_TYPE, oldV, buttonType);
		this.setPropertyValue(ComponentProperties.type, buttonType);

	}

	public void setIcon(String i) {
		// if (eq(this.icon, icon))
		// return;
		// String oldV = this.icon;
		// this.icon = icon;
		//		firePropertyChange("ICON", oldV, icon); //$NON-NLS-1$
		this.setPropertyValue(ComponentProperties.icon, i);
	}

	public void setText(String text) {
		// if (eq(this.text, text))
		// return;
		// String oldV = this.text;
		// this.text = text;
		// firePropertyChange(BUTTON_TEXT, oldV, text);
		this.setPropertyValue(ComponentProperties.text, text);
	}

	public void setTitle(String title) {
		// if (eq(this.title, title))
		// return;
		// String oldV = this.title;
		// this.title = title;
		// firePropertyChange(TOOLTIP, oldV, title);
		this.setPropertyValue(ComponentProperties.title, title);
	}

	// public Object getPropertyValue(Object propName) {
	// if (TOOLTIP.equals(propName))
	// return getTitle();
	// else if (BUTTON_TEXT.equals(propName))
	// return getText();
	// else if (BUTTON_CLICKER.equals(propName))
	// return buttonClicker;
	// else if (BUTTON_TYPE.equals(propName))
	// return Arrays.asList(std_types).indexOf(getButtonType());
	// return super.getPropertyValue(propName);
	// }

	// public void setPropertyValue(Object propName, Object val) {
	// if (TOOLTIP.equals(propName))
	// setTitle((String) val);
	// else if (BUTTON_TEXT.equals(propName))
	// setText((String) val);
	// else if (BUTTON_CLICKER.equals(propName))
	// buttonClicker = (ButtonClicker) val;
	// else if (BUTTON_TYPE.equals(propName))
	// setButtonType(std_types[(Integer) val]);
	// super.setPropertyValue(propName, val);
	// }

	public boolean isOnToolBar() {
		return getParent() instanceof Toolbar;
	}

	public ButtonClicker getButtonClicker() {
		return buttonClicker;
	}

	public void setButtonClicker(ButtonClicker buttonClicker) {
		buttonClicker.setButton(this);
		this.buttonClicker = buttonClicker;
	}

	public Object getPropertyValue(String propId) {
		if (ComponentInnerProperties.BUTTON_CLICKER.equals(propId)) {
			return buttonClicker;
		}
		Object propertyValue = super.getPropertyValue(propId);
		return propertyValue;
	}

	public void setPropertyValue(String propId, Object val) {
		if (ComponentInnerProperties.BUTTON_CLICKER.equals(propId)
				&& val instanceof ButtonClicker) {
			this.setButtonClicker((ButtonClicker) val);
			return;
		}
		if (ComponentProperties.type.equals(propId)) {
			if (isStdButton()) {
				super.setSize(48, 20);
			}
		}

		super.setPropertyValue(propId, val);
	}

}

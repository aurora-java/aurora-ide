package aurora.ide.meta.gef.editors.consultant.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.BooleanPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.ComboPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.IntegerPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StylePropertyDescriptor;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.CheckBox;
import aurora.plugin.source.gen.screen.model.Input;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class ConsultantPropertyFactory implements ComponentInnerProperties,
		ComponentProperties {
	public IPropertyDescriptor[] createPropertyDescriptors(
			AuroraComponent component) {
		String componentType = component.getComponentType();
		if ("button".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.button(component);
		}
		if ("checkbox".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.checkbox();
		}
		if ("combobox".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.combox();
		}

		if ("datepicker".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.datepicker();
		}
		if ("datetimepicker".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.datepicker();
		}
		if ("fieldset".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.form();
		}

		if ("form".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.form();
		}
		if ("grid".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.grid();
		}
		if ("gridcolumn".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.gridcolumn();
		}
		if ("hbox".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.hbox();
		}
		if ("label".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.label();
		}
		if ("lov".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.lov();
		}

		if ("numberfield".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.numberfield();
		}
		if ("tabPanel".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.tabfolder();
		}
		if ("tab".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.tabitem();
		}
		if ("textfield".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.textfield();
		}
		if ("textarea".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.textarea();
		}
		if ("toolbar".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return NONE_PROPS;
		}
		if ("vbox".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.vbox(component);
		}
		if ("screenbody".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.screenbody();
		}
		if ("formBody".equalsIgnoreCase(componentType)) {
			return this.formBody();
		}
		if ("queryForm".equalsIgnoreCase(componentType)) {
			return this.queryForm();
		}
		return NONE_PROPS;

	}

	private IPropertyDescriptor[] textarea() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_HEIGHT,
				PD_REQUIRED, PD_READONLY };
	}

	private IPropertyDescriptor[] queryForm() {
		// return NONE_PROPS;
		// IntegerPropertyDescriptor PD_LABELWIDTH = new
		// IntegerPropertyDescriptor(
		// labelWidth,
		// Messages.ConsultantPropertyFactory_LabelWidth,IntegerPropertyDescriptor.component_child);
		// PD_LABELWIDTH.setChildPropertyId(ComponentInnerProperties.QUERY_FORM_TOOLBAR);
		return new IPropertyDescriptor[] { PD_WIDTH, PD_LABELWIDTH };
	}

	private IPropertyDescriptor[] formBody() {
		return new IPropertyDescriptor[] { PD_COL, PD_LABELWIDTH };
	}

	private IPropertyDescriptor[] button(AuroraComponent component) {
		IPropertyDescriptor[] std_pds = new IPropertyDescriptor[] { PD_TOOLBAR_BUTTON_TYPES };
		IPropertyDescriptor[] inner_pds = new IPropertyDescriptor[] { PD_TEXT,
				PD_WIDTH, PD_HEIGHT };
		if (((Button) component).isOnToolBar()) {
			return std_pds;
		}
		return inner_pds;
	}

	private IPropertyDescriptor[] checkbox() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_TEXT };
	}

	private IPropertyDescriptor[] gridcolumn() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH,
				PD_COLUMN_EDITOR, PD_READONLY, PD_REQUIRED };

	}

	private IPropertyDescriptor[] form() {
		return new IPropertyDescriptor[] { PD_TITLE, PD_WIDTH, PD_HEIGHT,
				PD_COL, PD_LABELWIDTH };
	}

	private IPropertyDescriptor[] grid() {
		return new IPropertyDescriptor[] { PD_WIDTH, PD_HEIGHT, PD_NAVBAR_TYPE,
				PD_SELECTION_MODE };
	}

	private IPropertyDescriptor[] hbox() {
		return new IPropertyDescriptor[] { PD_LABELWIDTH };
	}

	private IPropertyDescriptor[] combox() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_EMPYTEXT,
				PD_REQUIRED, PD_READONLY };
	}

	private IPropertyDescriptor[] datepicker() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_REQUIRED,
				PD_READONLY };
	}

	private IPropertyDescriptor[] lov() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_EMPYTEXT,
				PD_REQUIRED, PD_READONLY };
	}

	private IPropertyDescriptor[] numberfield() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_EMPYTEXT,
				PD_REQUIRED, PD_READONLY };
	}

	private IPropertyDescriptor[] textfield() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_EMPYTEXT,
				PD_REQUIRED, PD_READONLY };
	}

	private IPropertyDescriptor[] label() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH };
	}

	private IPropertyDescriptor[] tabfolder() {
		return new IPropertyDescriptor[] { PD_WIDTH, PD_HEIGHT };
	}

	private IPropertyDescriptor[] tabitem() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH };
	}

	private IPropertyDescriptor[] vbox(AuroraComponent component) {
		return new IPropertyDescriptor[] { PD_LABELWIDTH };

	}

	private static final String SELECT_NONE = ""; //$NON-NLS-1$
	private static final String SELECT_MULTI = "multiple"; //$NON-NLS-1$
	private static final String SELECT_SINGLE = "single"; //$NON-NLS-1$
	private static final String[] selectionModes = { SELECT_NONE, SELECT_MULTI,
			SELECT_SINGLE };
	private static final String NAVBAR_NONE = ""; //$NON-NLS-1$
	private static final String NAVBAR_SIMPLE = "simple"; //$NON-NLS-1$
	private static final String NAVBAR_COMPLEX = "complex"; //$NON-NLS-1$
	private static final String[] navBarTypes = { NAVBAR_NONE, NAVBAR_SIMPLE,
			NAVBAR_COMPLEX };

	private static final String[] editors = { "", Input.TEXT, Input.NUMBER, //$NON-NLS-1$
			Input.Combo, Input.LOV, CheckBox.CHECKBOX, Input.DATE_PICKER,
			Input.DATETIMEPICKER };

	private static final IPropertyDescriptor PD_TEXT = new StringPropertyDescriptor(
			text, Messages.ConsultantPropertyFactory_Text);
	private static final ComboPropertyDescriptor PD_COLUMN_EDITOR = new ComboPropertyDescriptor(
			editor, Messages.ConsultantPropertyFactory_editor, editors);
	public static final IPropertyDescriptor PD_READONLY = new BooleanPropertyDescriptor(
			readOnly, "*" + Messages.ConsultantPropertyFactory_readonly,
			BooleanPropertyDescriptor.component
					| BooleanPropertyDescriptor.datasetfield);
	public static final IPropertyDescriptor PD_REQUIRED = new BooleanPropertyDescriptor(
			required, "*" + Messages.ConsultantPropertyFactory_required,
			BooleanPropertyDescriptor.component
					| BooleanPropertyDescriptor.datasetfield);
	private static final IPropertyDescriptor PD_EMPYTEXT = new StringPropertyDescriptor(
			emptyText, Messages.ConsultantPropertyFactory_empty_text);
	private static final IPropertyDescriptor PD_COL = new IntegerPropertyDescriptor(
			column, Messages.ConsultantPropertyFactory_Column, 1, 100, 1, 2);
	private static final IPropertyDescriptor PD_TITLE = new StringPropertyDescriptor(
			title, Messages.ConsultantPropertyFactory_Title);
	private static final IPropertyDescriptor PD_LABELWIDTH = new IntegerPropertyDescriptor(
			labelWidth, Messages.ConsultantPropertyFactory_LabelWidth);
	private static final IPropertyDescriptor PD_PROMPT = new StringPropertyDescriptor(
			prompt, Messages.ConsultantPropertyFactory_Prompt);
	private static final IPropertyDescriptor PD_WIDTH = new IntegerPropertyDescriptor(
			width, Messages.ConsultantPropertyFactory_Width);
	private static final IPropertyDescriptor PD_HEIGHT = new IntegerPropertyDescriptor(
			height, Messages.ConsultantPropertyFactory_Height);
	private static final IPropertyDescriptor[] NONE_PROPS = new IPropertyDescriptor[0];
	private static final IPropertyDescriptor PD_SELECTION_MODE = new ComboPropertyDescriptor(
			selectionModel, "*"
					+ Messages.ConsultantPropertyFactory_SelectionModel,
			selectionModes, StylePropertyDescriptor.component
					| StylePropertyDescriptor.dataset);
	private static final ComboPropertyDescriptor PD_TOOLBAR_BUTTON_TYPES = new ComboPropertyDescriptor(
			type, Messages.ConsultantPropertyFactory_Type, Button.std_types);
	private static final IPropertyDescriptor PD_NAVBAR_TYPE = new ComboPropertyDescriptor(
			navBarType, Messages.ConsultantPropertyFactory_NavBarType,
			navBarTypes);

	private IPropertyDescriptor[] screenbody() {
		return NONE_PROPS;
	}

	public static IPropertyDescriptor[] mergePropertyDescriptor(
			IPropertyDescriptor[] pd1, IPropertyDescriptor[] pd2) {
		IPropertyDescriptor[] descs = new IPropertyDescriptor[pd1.length
				+ pd2.length];
		System.arraycopy(pd1, 0, descs, 0, pd1.length);
		System.arraycopy(pd2, 0, descs, pd1.length, pd2.length);
		return descs;
	}
}

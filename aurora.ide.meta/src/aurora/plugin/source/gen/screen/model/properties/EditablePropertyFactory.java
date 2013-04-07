package aurora.plugin.source.gen.screen.model.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.DatasetField;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.Navbar;
import aurora.ide.meta.gef.editors.property.BooleanPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.ButtonClickEditDialog;
import aurora.ide.meta.gef.editors.property.ComboPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.DialogPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.FootRendererEditDialog;
import aurora.ide.meta.gef.editors.property.IntegerPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.RendererEditDialog;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StylePropertyDescriptor;
import aurora.ide.meta.gef.editors.property.TabRefPropertyDescriptor;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.VBox;

public class EditablePropertyFactory implements ComponentInnerProperties,
		ComponentProperties {
	public IPropertyDescriptor[] createPropertyDescriptors(
			AuroraComponent component) {
		String componentType = component.getComponentType();
		if ("button".equalsIgnoreCase(componentType)) {
			return this.button(component);
		}
		if ("checkbox".equalsIgnoreCase(componentType)) {
			return this.checkbox();
		}
		if ("combobox".equalsIgnoreCase(componentType)) {
			return this.combox();
		}

		if ("datepicker".equalsIgnoreCase(componentType)) {
			return this.datepicker();
		}
		if ("datetimepicker".equalsIgnoreCase(componentType)) {
			return this.datepicker();
		}
		if ("fieldset".equalsIgnoreCase(componentType)) {
			return this.form();
		}

		if ("form".equalsIgnoreCase(componentType)) {
			return this.form();
		}
		if ("grid".equalsIgnoreCase(componentType)) {
			return this.grid();
		}
		if ("gridcolumn".equalsIgnoreCase(componentType)) {
			return this.gridcolumn();
		}
		if ("hbox".equalsIgnoreCase(componentType)) {
			return this.hbox();
		}
		if ("label".equalsIgnoreCase(componentType)) {
			return this.label();
		}
		if ("lov".equalsIgnoreCase(componentType)) {
			return this.lov();
		}

		if ("numberfield".equalsIgnoreCase(componentType)) {
			return this.numberfield();
		}
		if ("tabfolder".equalsIgnoreCase(componentType)) {
			return this.tabfolder();
		}
		if ("tabitem".equalsIgnoreCase(componentType)) {
			return this.tabitem();
		}
		if ("textfield".equalsIgnoreCase(componentType)) {
			return this.textfield();
		}
		if ("toolbar".equalsIgnoreCase(componentType)) {
			return NONE_PROPS;
		}
		if ("vbox".equalsIgnoreCase(componentType)) {
			return this.vbox(component);
		}
		if ("screenbody".equalsIgnoreCase(componentType)) {
			return this.screenbody();
		}
		return NONE_PROPS;

	}

	private IPropertyDescriptor[] button(AuroraComponent component) {

		IPropertyDescriptor[] std_pds = new IPropertyDescriptor[] {
				new StringPropertyDescriptor(text, "Text"), PD_NAME, //$NON-NLS-1$
				new IntegerPropertyDescriptor(width, "Width"), //$NON-NLS-1$
				new IntegerPropertyDescriptor(height, "Height"), //$NON-NLS-1$
				new StringPropertyDescriptor(title, "Title"), //$NON-NLS-1$
				new ComboPropertyDescriptor(type,
						"Type", new String[] { "新增", "删除", "保存", "导出", "清除" }) }; //$NON-NLS-1$
		IPropertyDescriptor[] inner_pds = new IPropertyDescriptor[] {
				new StringPropertyDescriptor(text, "Text"), //$NON-NLS-1$
				PD_NAME, new IntegerPropertyDescriptor(width, "Width"), //$NON-NLS-1$
				new IntegerPropertyDescriptor(height, "Height"), //$NON-NLS-1$
				new StringPropertyDescriptor(title, "Title"), //$NON-NLS-1$
				new DialogPropertyDescriptor(BUTTON_CLICKER, "Click", //$NON-NLS-1$
						ButtonClickEditDialog.class) };
		if (((Button) component).isOnToolBar()) {
			return std_pds;
		}
		return inner_pds;
	}

	private IPropertyDescriptor[] checkbox() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_NAME, PD_TEXT,
				PD_READONLY, PD_REQUIRED, PD_CHECKED_VALUE, PD_UNCHECKED_VALUE };
	}

	public static final IPropertyDescriptor PD_TEXT = new StringPropertyDescriptor(
			text, text);
	public static final IPropertyDescriptor PD_CHECKED_VALUE = new StringPropertyDescriptor(
			DatasetField.CHECKED_VALUE, "*" + DatasetField.CHECKED_VALUE,
			StylePropertyDescriptor.component
					| StylePropertyDescriptor.datasetfield);
	public static final IPropertyDescriptor PD_UNCHECKED_VALUE = new StringPropertyDescriptor(
			DatasetField.UNCHECKED_VALUE, "*" + DatasetField.UNCHECKED_VALUE,
			StylePropertyDescriptor.component
					| StylePropertyDescriptor.datasetfield);
	private static final String[] editors = { "", Input.TEXT, Input.NUMBER,
			Input.Combo, Input.LOV, CheckBox.CHECKBOX, Input.DATE_PICKER,
			Input.DATETIMEPICKER };

	private IPropertyDescriptor[] gridcolumn() {
		return new IPropertyDescriptor[] {
				PD_PROMPT,
				PD_WIDTH,
				PD_NAME,
				new ComboPropertyDescriptor(editor, "editor", editors),
				new DialogPropertyDescriptor(renderer, "renderer",
						RendererEditDialog.class),
				new DialogPropertyDescriptor(footerRenderer, "footRenderer",
						FootRendererEditDialog.class),
				new BooleanPropertyDescriptor(readOnly, "*readOnly",
						StylePropertyDescriptor.component
								| StylePropertyDescriptor.datasetfield),
				new BooleanPropertyDescriptor(required, "*required",
						StylePropertyDescriptor.component
								| StylePropertyDescriptor.datasetfield) };

	}

	private IPropertyDescriptor[] form() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_TITLE, PD_WIDTH,
				PD_HEIGHT, PD_COL, PD_LABELWIDTH };
	}

	private IPropertyDescriptor[] grid() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_HEIGHT,
				PD_NAVBAR_TYPE };
	}

	public static final String NAVBAR_NONE = "";
	public static final String NAVBAR_SIMPLE = "simple";
	public static final String NAVBAR_COMPLEX = "complex";
	public static final String NAVBAR = "navBar";
	private static final String[] navBarTypes = { NAVBAR_NONE, NAVBAR_SIMPLE,
			NAVBAR_COMPLEX };
	private Navbar navBar = new Navbar();

	private static final IPropertyDescriptor PD_NAVBAR_TYPE = new ComboPropertyDescriptor(
			navBarType, "NavBarType", navBarTypes);

	private IPropertyDescriptor[] hbox() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_LABELWIDTH };
	}

	private IPropertyDescriptor[] combox() {
		return new IPropertyDescriptor[] {
				PD_PROMPT,
				PD_NAME,
				PD_WIDTH,
				PD_EMPYTEXT,
				PD_TYPECASE,
				PD_REQUIRED,
				PD_READONLY,
				new StringPropertyDescriptor(
						options,
						"*options", StylePropertyDescriptor.component | StylePropertyDescriptor.datasetfield, true), //$NON-NLS-1$
				new StringPropertyDescriptor(
						displayField,
						"*displayField", StylePropertyDescriptor.component | StylePropertyDescriptor.datasetfield, true), //$NON-NLS-1$
				new StringPropertyDescriptor(
						valueField,
						"*valueField", StylePropertyDescriptor.component | StylePropertyDescriptor.datasetfield, true), //$NON-NLS-1$
				new StringPropertyDescriptor(returnField, "*returnField",
						StylePropertyDescriptor.component
								| StylePropertyDescriptor.datasetfield, true) };
	}

	private static final String[] CAL_ENABLES = { "pre", "next", "both", "none" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	private IPropertyDescriptor[] datepicker() {
		return new IPropertyDescriptor[] {
				PD_PROMPT,
				PD_WIDTH,
				PD_NAME,
				new ComboPropertyDescriptor(enableBesideDays,
						"EnableBesideDays", //$NON-NLS-1$
						CAL_ENABLES),
				new ComboPropertyDescriptor(enableMonthBtn, "EnableMonthBtn", //$NON-NLS-1$
						CAL_ENABLES), PD_REQUIRED, PD_READONLY };
	}

	private IPropertyDescriptor[] lov() {
		return new IPropertyDescriptor[] {
				PD_PROMPT,
				PD_NAME,
				PD_WIDTH,
				PD_EMPYTEXT,
				PD_TYPECASE,
				PD_REQUIRED,
				PD_READONLY,
				new StringPropertyDescriptor(
						lovService,
						"*lovService", StylePropertyDescriptor.component | StylePropertyDescriptor.datasetfield, true), //$NON-NLS-1$
				new StringPropertyDescriptor(title, "*title",
						StylePropertyDescriptor.component
								| StylePropertyDescriptor.datasetfield) };
	}

	private IPropertyDescriptor[] numberfield() {

		return new IPropertyDescriptor[] { PD_PROMPT, PD_NAME, PD_WIDTH,
				PD_EMPYTEXT,
				new BooleanPropertyDescriptor(allowDecimals, "AllowDecimals"), //$NON-NLS-1$
				new BooleanPropertyDescriptor(allowNegative, "AllowNegative"), //$NON-NLS-1$
				new BooleanPropertyDescriptor(allowFormat, "AllowFormat"), //$NON-NLS-1$
				PD_REQUIRED, PD_READONLY };
	}

	private IPropertyDescriptor[] textfield() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_NAME, PD_WIDTH,
				PD_EMPYTEXT, PD_TYPECASE, PD_REQUIRED, PD_READONLY };
	}

	public static final IPropertyDescriptor PD_READONLY = new BooleanPropertyDescriptor(
			readOnly, "*readOnly", BooleanPropertyDescriptor.component
					| BooleanPropertyDescriptor.datasetfield);
	public static final IPropertyDescriptor PD_REQUIRED = new BooleanPropertyDescriptor(
			required, "*required", BooleanPropertyDescriptor.component
					| BooleanPropertyDescriptor.datasetfield);
	public static final IPropertyDescriptor PD_DEFAULT_VALUE = new StringPropertyDescriptor(
			defaultValue, "*defaultValue");
	private static final IPropertyDescriptor PD_EMPYTEXT = new StringPropertyDescriptor(
			emptyText, "EmptyText"); //$NON-NLS-1$
	private static final IPropertyDescriptor PD_TYPECASE = new ComboPropertyDescriptor(
			typeCase, "TypeCase", new String[] { "任意", "大写", "小写" }); //$NON-NLS-1$

	private IPropertyDescriptor[] label() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_NAME, PD_WIDTH,
				PD_RENDERER };
	}

	private IPropertyDescriptor[] tabfolder() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_HEIGHT };
	}

	private IPropertyDescriptor[] tabitem() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH,
				new TabRefPropertyDescriptor(TAB_SCREEN_REF, "ref") };
	}

	private IPropertyDescriptor[] vbox(AuroraComponent component) {

		VBox vbox = (VBox) component;
		// String sectionType = vbox.getSectionType();
		// if (sectionType == null ||
		// sectionType.equals(VBox.SECTION_TYPE_BUTTON))
		return new IPropertyDescriptor[] { PD_PROMPT, PD_LABELWIDTH };
		// return mergePropertyDescriptor(pds, getDataset()
		// .getPropertyDescriptors());
		//
		// return null;

	}

	protected static final IPropertyDescriptor PD_RENDERER = new StringPropertyDescriptor(
			renderer, "renderer");

	protected static final IPropertyDescriptor PD_ROW = new IntegerPropertyDescriptor(
			row, "Row", 1, 100, 1, 2);
	protected static final IPropertyDescriptor PD_COL = new IntegerPropertyDescriptor(
			column, "Column", 1, 100, 1, 2);
	protected static final IPropertyDescriptor PD_TITLE = new StringPropertyDescriptor(
			title, "Title");

	protected static final IPropertyDescriptor PD_LABELWIDTH = new IntegerPropertyDescriptor(
			labelWidth, "LabelWidth");
	protected static final IPropertyDescriptor PD_PROMPT = new StringPropertyDescriptor(
			prompt, "Prompt");
	protected static final IPropertyDescriptor PD_WIDTH = new IntegerPropertyDescriptor(
			width, "Width");
	protected static final IPropertyDescriptor PD_HEIGHT = new IntegerPropertyDescriptor(
			height, "Height");
	protected static final IPropertyDescriptor PD_NAME = new StringPropertyDescriptor(
			name, "Name");
	private static final IPropertyDescriptor[] pds = { PD_PROMPT };
	public static final IPropertyDescriptor[] NONE_PROPS = new IPropertyDescriptor[0];

	public static final String SELECT_NONE = "";
	public static final String SELECT_MULTI = "multiple";
	public static final String SELECT_SINGLE = "single";
	public static final String SELECTION_MODE = "selectionModel";
	private static final String[] selectionModes = { SELECT_NONE, SELECT_MULTI,
			SELECT_SINGLE };
	private static final IPropertyDescriptor PD_SELECTION_MODE = new ComboPropertyDescriptor(
			SELECTION_MODE, "*SelectionModel", selectionModes,
			StylePropertyDescriptor.component
					| StylePropertyDescriptor.datasetfield);

	// new IntegerPropertyDescriptor(PAGE_SIZE, "*pageSize"),

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
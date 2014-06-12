package aurora.ide.meta.gef.editors.consultant.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.BooleanPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.ComboPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.DialogPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.IconSelectionDialog;
import aurora.ide.meta.gef.editors.property.IntegerPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StylePropertyDescriptor;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.CheckBox;
import aurora.plugin.source.gen.screen.model.CustomTree;
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
		if ("toolbar_button".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.toolbar_button(component);
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
		if ("formBody".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.formBody();
		}
		if ("queryForm".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.queryForm();
		}
		if ("custom_icon".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.customIcon();
		}
		if ("radio_item".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.radioItem();
		}

		if ("custom_tree".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.customTree();
		}
		if ("custom_tree_container_node".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.cTreeCN();
		}
		if ("custom_tree_node".equalsIgnoreCase(componentType)) { //$NON-NLS-1$
			return this.cTreeCN();
		}

		return NONE_PROPS;

	}

	private IPropertyDescriptor[] cTreeCN() {
		return new IPropertyDescriptor[] { PD_PROMPT};
	}

	private IPropertyDescriptor[] customTree() {
		return new IPropertyDescriptor[] { PD_WIDTH, PD_HEIGHT, PD_ROWSPAN,
				PD_COLSPAN,PD_CHECKED_TREE };
	}

	protected static final IPropertyDescriptor PD_ROWSPAN = new IntegerPropertyDescriptor(
			rowspan, Messages.ConsultantPropertyFactory_0, 1, 1000, 1, 1);
	protected static final IPropertyDescriptor PD_COLSPAN = new IntegerPropertyDescriptor(
			colspan, Messages.ConsultantPropertyFactory_1, 1, 1000, 1, 1);

	private IPropertyDescriptor[] customIcon() {
		return new IPropertyDescriptor[] { PD_WIDTH, PD_HEIGHT, PD_IMAGE_WIDTH,
				PD_IMAGE_HEIGHT, PD_ROWSPAN, PD_COLSPAN };
	}

	private IPropertyDescriptor[] textarea() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_HEIGHT,
				PD_REQUIRED, PD_READONLY, PD_ROWSPAN, PD_COLSPAN };
	}

	private IPropertyDescriptor[] queryForm() {
		// return NONE_PROPS;
		// IntegerPropertyDescriptor PD_LABELWIDTH = new
		// IntegerPropertyDescriptor(
		// labelWidth,
		// Messages.ConsultantPropertyFactory_LabelWidth,IntegerPropertyDescriptor.component_child);
		// PD_LABELWIDTH.setChildPropertyId(ComponentInnerProperties.QUERY_FORM_TOOLBAR);
		return new IPropertyDescriptor[] { PD_WIDTH, PD_LABELWIDTH, PD_ROWSPAN,
				PD_COLSPAN };
	}

	private IPropertyDescriptor[] formBody() {
		return new IPropertyDescriptor[] { PD_COL, PD_LABELWIDTH,
				PD_MIN_ROW_HEIGHT, PD_MIN_COL_WIDTH };
	}

	private IPropertyDescriptor[] button(AuroraComponent component) {
		IPropertyDescriptor[] std_pds = new IPropertyDescriptor[] {
				PD_TOOLBAR_BUTTON_TYPES, PD_ROWSPAN, PD_COLSPAN };
		IPropertyDescriptor[] inner_pds = new IPropertyDescriptor[] { PD_TEXT,
				PD_WIDTH, PD_HEIGHT, PD_ROWSPAN, PD_COLSPAN };
		if (((Button) component).isOnToolBar()) {
			return std_pds;
		}
		return inner_pds;
	}

	private static final IPropertyDescriptor PD_ICON_SELECTION = new DialogPropertyDescriptor(
			ComponentInnerProperties.ICON_BYTES_DATA_DEO,
			Messages.ConsultantPropertyFactory_5, IconSelectionDialog.class);

	private IPropertyDescriptor[] toolbar_button(AuroraComponent component) {
		IPropertyDescriptor[] inner_pds = new IPropertyDescriptor[] {
				PD_TOOLBAR_BUTTON_TYPES, PD_TEXT, PD_WIDTH, PD_ICON_SELECTION };
		if (((Button) component).isOnToolBar()) {
			return inner_pds;
		}
		return inner_pds;
	}

	private IPropertyDescriptor[] radioItem() {
		return new IPropertyDescriptor[] { PD_TEXT, PD_WIDTH, PD_ROWSPAN,
				PD_COLSPAN };
	}

	private IPropertyDescriptor[] checkbox() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_TEXT, PD_WIDTH,
				PD_ROWSPAN, PD_COLSPAN };
	}

	private IPropertyDescriptor[] gridcolumn() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH,
				PD_COLUMN_EDITOR, PD_READONLY, PD_REQUIRED,
				PD_GRID_COLUMN_SORTABLE, PD_COLUMN_ALIGNMENT };

	}

	private IPropertyDescriptor[] form() {
		return new IPropertyDescriptor[] { PD_TITLE, PD_WIDTH, PD_HEIGHT,
				PD_COL, PD_LABELWIDTH, PD_ROWSPAN, PD_COLSPAN,
				PD_MIN_ROW_HEIGHT, PD_MIN_COL_WIDTH };
	}

	private IPropertyDescriptor[] grid() {
		return new IPropertyDescriptor[] { PD_WIDTH, PD_HEIGHT, PD_NAVBAR_TYPE,
				PD_SELECTION_MODE, PD_ROWSPAN, PD_COLSPAN };
	}

	private IPropertyDescriptor[] hbox() {
		return new IPropertyDescriptor[] { PD_LABELWIDTH, PD_ROWSPAN,
				PD_COLSPAN, PD_MIN_COL_WIDTH };
	}

	private IPropertyDescriptor[] combox() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_EMPYTEXT,
				PD_REQUIRED, PD_READONLY, PD_ROWSPAN, PD_COLSPAN };
	}

	private IPropertyDescriptor[] datepicker() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_REQUIRED,
				PD_READONLY, PD_ROWSPAN, PD_COLSPAN };
	}

	private IPropertyDescriptor[] lov() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_EMPYTEXT,
				PD_REQUIRED, PD_READONLY, PD_ROWSPAN, PD_COLSPAN };
	}

	private IPropertyDescriptor[] numberfield() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_EMPYTEXT,
				PD_REQUIRED, PD_READONLY, PD_ROWSPAN, PD_COLSPAN };
	}

	private IPropertyDescriptor[] textfield() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_EMPYTEXT,
				PD_REQUIRED, PD_READONLY, PD_ROWSPAN, PD_COLSPAN };
	}

	private IPropertyDescriptor[] label() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH, PD_ROWSPAN,
				PD_COLSPAN };
	}

	private IPropertyDescriptor[] tabfolder() {
		return new IPropertyDescriptor[] { PD_WIDTH, PD_HEIGHT };
	}

	private IPropertyDescriptor[] tabitem() {
		return new IPropertyDescriptor[] { PD_PROMPT, PD_WIDTH };
	}

	private IPropertyDescriptor[] vbox(AuroraComponent component) {
		return new IPropertyDescriptor[] { PD_LABELWIDTH, PD_ROWSPAN,
				PD_COLSPAN, PD_MIN_ROW_HEIGHT };

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
			readOnly, "*" + Messages.ConsultantPropertyFactory_readonly, //$NON-NLS-1$
			BooleanPropertyDescriptor.component
					| BooleanPropertyDescriptor.datasetfield);

	public static final IPropertyDescriptor PD_REQUIRED = new BooleanPropertyDescriptor(
			required, "*" + Messages.ConsultantPropertyFactory_required, //$NON-NLS-1$
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
			selectionModel, "*" //$NON-NLS-1$
					+ Messages.ConsultantPropertyFactory_SelectionModel,
			selectionModes, StylePropertyDescriptor.component
					| StylePropertyDescriptor.dataset);
	private static final ComboPropertyDescriptor PD_TOOLBAR_BUTTON_TYPES = new ComboPropertyDescriptor(
			type, Messages.ConsultantPropertyFactory_Type, Button.std_types);
	private static final IPropertyDescriptor PD_NAVBAR_TYPE = new ComboPropertyDescriptor(
			navBarType, Messages.ConsultantPropertyFactory_NavBarType,
			navBarTypes);

	private static final IPropertyDescriptor PD_IMAGE_WIDTH = new IntegerPropertyDescriptor(
			IMAGE_WIDTH, Messages.ConsultantPropertyFactory_6, true);
	private static final IPropertyDescriptor PD_IMAGE_HEIGHT = new IntegerPropertyDescriptor(
			IMAGE_HEIGHT, Messages.ConsultantPropertyFactory_7, true);

	protected static final IPropertyDescriptor PD_MIN_ROW_HEIGHT = new IntegerPropertyDescriptor(
			minRowHeight, Messages.ConsultantPropertyFactory_2);
	protected static final IPropertyDescriptor PD_MIN_COL_WIDTH = new IntegerPropertyDescriptor(
			minColWidth, Messages.ConsultantPropertyFactory_3);
	protected static final IPropertyDescriptor PD_GRID_COLUMN_SORTABLE = new BooleanPropertyDescriptor(
			GRID_COLUMN_SORTABLE, Messages.ConsultantPropertyFactory_4);
	
	protected static final IPropertyDescriptor PD_CHECKED_TREE = new BooleanPropertyDescriptor(
			CustomTree.CHECKED_TREE, "CHECKED");

	public static final String[] aligns = { "left", "center", "right" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final ComboPropertyDescriptor PD_COLUMN_ALIGNMENT = new ComboPropertyDescriptor(
			GRID_COLUMN_ALIGNMENT, Messages.ConsultantPropertyFactory_12,
			aligns); //$NON-NLS-1$

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

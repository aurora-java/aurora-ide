package aurora.plugin.source.gen.screen.model.properties;

import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class TestPropertyFactory implements ComponentInnerProperties,
		ComponentProperties {
	public IPropertyDescriptor[] createPropertyDescriptors(
			AuroraComponent component) {
		return createPropertyDescriptors(component.getComponentType());
	}

	public IPropertyDescriptor[] createPropertyDescriptors(String componentType) {
		if ("button".equalsIgnoreCase(componentType)) {
			return this.button();
		}
		if ("box".equalsIgnoreCase(componentType)) {
			return this.box();
		}
		if ("inner_buttonclicker".equalsIgnoreCase(componentType)) {
			return this.buttonClick();
		}
		if ("checkbox".equalsIgnoreCase(componentType)) {
			return this.checkbox();
		}
		if ("combox".equalsIgnoreCase(componentType)) {
			return this.combox();
		}

		if ("dataset".equalsIgnoreCase(componentType)) {
			return this.dataset();
		}
		if ("datasetfield".equalsIgnoreCase(componentType)) {
			return this.datasetfield();
		}
		if ("datepicker".equalsIgnoreCase(componentType)) {
			return this.datepicker();
		}
		if ("datetimepicker".equalsIgnoreCase(componentType)) {
			return this.datetimepicker();
		}
		if ("fieldset".equalsIgnoreCase(componentType)) {
			return this.fieldset();
		}
		if ("footrenderer".equalsIgnoreCase(componentType)) {
			return this.footrenderer();
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
		if ("gridselectioncol".equalsIgnoreCase(componentType)) {
			return this.gridselectioncol();
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
		if ("renderer".equalsIgnoreCase(componentType)) {
			return this.renderer();
		}
		if ("tabitem".equalsIgnoreCase(componentType)) {
			return this.tabitem();
		}
		if ("textfield".equalsIgnoreCase(componentType)) {
			return this.textfield();
		}
		if ("vbox".equalsIgnoreCase(componentType)) {
			return this.vbox();
		}
		if ("screenbody".equalsIgnoreCase(componentType)) {
			return this.screenbody();
		}

		return this.defaultpPD();
	}

	protected DefaultPropertyDescriptor d(String id, int style) {
		DefaultPropertyDescriptor dpd = new DefaultPropertyDescriptor(id, style);
		return dpd;
	}

	protected DefaultPropertyDescriptor dss(String id) {
		DefaultPropertyDescriptor dpd = new DefaultPropertyDescriptor(id,
				IPropertyDescriptor.simple | IPropertyDescriptor.save);
		return dpd;
	}

	protected DefaultPropertyDescriptor dssb(String id) {
		DefaultPropertyDescriptor dpd = new DefaultPropertyDescriptor(id,
				IPropertyDescriptor.simple | IPropertyDescriptor.save
						| IPropertyDescriptor._boolean);
		return dpd;
	}

	protected DefaultPropertyDescriptor dssi(String id) {
		DefaultPropertyDescriptor dpd = new DefaultPropertyDescriptor(id,
				IPropertyDescriptor.simple | IPropertyDescriptor.save
						| IPropertyDescriptor._int);
		return dpd;
	}

	protected DefaultPropertyDescriptor dssf(String id) {
		DefaultPropertyDescriptor dpd = new DefaultPropertyDescriptor(id,
				IPropertyDescriptor.simple | IPropertyDescriptor.save
						| IPropertyDescriptor._float);
		return dpd;
	}

	protected DefaultPropertyDescriptor dss(String id, int style) {
		DefaultPropertyDescriptor dpd = new DefaultPropertyDescriptor(id,
				IPropertyDescriptor.simple | IPropertyDescriptor.save | style);
		return dpd;
	}

	private IPropertyDescriptor[] button() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(type),
				dss(icon),
				dss(title),
				dss(text),
				d(BUTTON_CLICKER, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save) };
	}

	private IPropertyDescriptor[] buttonClick() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(BUTTON_CLICK_TARGET_COMPONENT, IPropertyDescriptor.reference
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				d(BUTTON_CLICK_ACTIONID, IPropertyDescriptor.simple
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				d(BUTTON_CLICK_OPENPATH, IPropertyDescriptor.simple
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				d(BUTTON_CLICK_CLOSEWINDOWID, IPropertyDescriptor.simple
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				d(BUTTON_CLICK_FUNCTION, IPropertyDescriptor.simple
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				d(BUTTON_CLICK_PARAMETERS, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save
						| IPropertyDescriptor.list) };
	}

	private IPropertyDescriptor[] parameter() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(PARAMETER_NAME, IPropertyDescriptor.simple
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				d(PARAMETER_VALUE, IPropertyDescriptor.simple
						| IPropertyDescriptor.inner | IPropertyDescriptor.save) };
	}

	private IPropertyDescriptor[] box() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(CONTAINER_SECTION_TYPE, IPropertyDescriptor.inner),
				d(DATASET_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				dssi(row),
				dssi(column),
				dss(title),
				dssi(labelWidth),
				d(CHILDREN, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save
						| IPropertyDescriptor.list) };
	}

	private IPropertyDescriptor[] hbox() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dssi(width),
				dssi(height),
				dssi(labelWidth),
				d(CHILDREN, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save
						| IPropertyDescriptor.list) };
	}

	private IPropertyDescriptor[] vbox() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dssi(width),
				dssi(height),
				dssi(labelWidth),
				d(CHILDREN, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save
						| IPropertyDescriptor.list) };
	}

	private IPropertyDescriptor[] fieldset() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(CONTAINER_SECTION_TYPE, IPropertyDescriptor.inner),
				d(DATASET_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				dssi(row),
				dssi(column),
				dss(title),
				dssi(labelWidth),
				d(CHILDREN, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save
						| IPropertyDescriptor.list) };
	}

	private IPropertyDescriptor[] form() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(CONTAINER_SECTION_TYPE, IPropertyDescriptor.inner),
				d(DATASET_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				dssi(row),
				dssi(column),
				dss(title),
				dssi(labelWidth),
				d(CHILDREN, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save
						| IPropertyDescriptor.list) };
	}

	private IPropertyDescriptor[] dataset() {
		return new IPropertyDescriptor[] {
				dss(model),
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				dssi(pageSize),
				dss(selectionModel),
				d(DATASET_QUERY_CONTAINER, IPropertyDescriptor.inner
						| IPropertyDescriptor.reference
						| IPropertyDescriptor.save) };
	}

	private IPropertyDescriptor[] screenbody() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				dss(DIAGRAM_BIND_TEMPLATE, IPropertyDescriptor.inner),
				dss(DIAGRAM_BIND_TEMPLATE_TYPE, IPropertyDescriptor.inner),
				d(CHILDREN, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save
						| IPropertyDescriptor.list) };
	}

	private IPropertyDescriptor[] container() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(CONTAINER_SECTION_TYPE, IPropertyDescriptor.inner),
				d(DATASET_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				d(CHILDREN, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save
						| IPropertyDescriptor.list) };
	}

	private IPropertyDescriptor[] defaultpPD() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner), dss(prompt), dssi(width),
				dssi(height), dss(name) };
	}

	private IPropertyDescriptor[] input() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(emptyText),
				d(DATASET_FIELD_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save) };
	}

	private IPropertyDescriptor[] textfield() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(emptyText),
				dss(typeCase),
				d(DATASET_FIELD_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save) };
	}

	private IPropertyDescriptor[] label() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(renderer),
				d(DATASET_FIELD_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save) };
	}

	private IPropertyDescriptor[] datetimepicker() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(emptyText),
				dssb(enableBesideDays),
				dssb(enableMonthBtn),
				d(DATASET_FIELD_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save) };
	}

	private IPropertyDescriptor[] datepicker() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(emptyText),
				dssb(enableBesideDays),
				dssb(enableMonthBtn),
				d(DATASET_FIELD_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save) };
	}

	private IPropertyDescriptor[] combox() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(emptyText),
				d(DATASET_FIELD_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save) };
	}

	private IPropertyDescriptor[] lov() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(emptyText),
				d(DATASET_FIELD_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save) };
	}

	private IPropertyDescriptor[] numberfield() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dssi(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dssb(allowDecimals),
				dssb(allowNegative),
				dssb(allowFormat),
				dss(emptyText),
				d(DATASET_FIELD_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save) };
	}

	private IPropertyDescriptor[] checkbox() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(text),
				d(DATASET_FIELD_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save) };
	}

	private IPropertyDescriptor[] datasetfield() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner), dss(name),
				dss(checkedValue), dss(uncheckedValue), dss(displayField),
				dss(options), dss(valueField), dss(returnField),
				dssi(lovGridHeight), dssi(lovHeight), dss(lovService),
				dss(lovUrl), dssi(lovWidth), dss(title), dssb(required),
				dssb(readOnly), dss(defaultValue) };
	}

	private IPropertyDescriptor[] tabitem() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				dss(TAB_ITEM_CURRENT, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				d(CHILDREN, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save
						| IPropertyDescriptor.list) };
	}

	private IPropertyDescriptor[] grid() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				// dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(CONTAINER_SECTION_TYPE, IPropertyDescriptor.inner),
				d(DATASET_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				d(GRID_TOOLBAR, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				d(GRID_NAVBAR, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
//				dssi(row),
//				dssi(column),
//				dss(title),
//				dssi(labelWidth),
				d(CHILDREN, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save
						| IPropertyDescriptor.list) };
	}

	private IPropertyDescriptor[] gridcolumn() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner),
				dss(prompt),
				dssi(width),
				dssi(height),
				dss(name),
				dss(editor),
				d(GRID_COLUMN_RENDERER, IPropertyDescriptor.inner
						| IPropertyDescriptor.save
						| IPropertyDescriptor.containment),
				d(GRID_COLUMN_FOOTRENDERER, IPropertyDescriptor.inner
						| IPropertyDescriptor.save
						| IPropertyDescriptor.containment),
				d(CHILDREN, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save
						| IPropertyDescriptor.list),
				d(DATASET_FIELD_DELEGATE, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save) };
	}

	private IPropertyDescriptor[] gridselectioncol() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(LOCATION, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner), dss(width), dss(height),
				dss(GRID_SELECTION_MODE, IPropertyDescriptor.inner), };
	}

	private IPropertyDescriptor[] footrenderer() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				dss(FOOT_RENDERER_FUNCTION, IPropertyDescriptor.inner),
				dss(FOOT_RENDERER_TYPE, IPropertyDescriptor.inner) };
	}

	private IPropertyDescriptor[] renderer() {
		return new IPropertyDescriptor[] {
				dss(COMPONENT_MARKER_ID, IPropertyDescriptor.inner),
				dss(COMPONENT_TYPE, IPropertyDescriptor.inner),
				d(RENDERER_OPEN_PATH, IPropertyDescriptor.simple
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				d(RENDERER_LABELTEXT, IPropertyDescriptor.simple
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				d(RENDERER_FUNCTION_NAME, IPropertyDescriptor.simple
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				d(RENDERER_FUNCTION, IPropertyDescriptor.simple
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				d(RENDERER_TYPE, IPropertyDescriptor.simple
						| IPropertyDescriptor.inner | IPropertyDescriptor.save),
				d(BUTTON_CLICK_PARAMETERS, IPropertyDescriptor.containment
						| IPropertyDescriptor.inner | IPropertyDescriptor.save
						| IPropertyDescriptor.list) };
	}
}

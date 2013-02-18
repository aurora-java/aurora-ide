package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.BooleanPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.ComboPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;
import aurora.ide.meta.gef.editors.source.gen.DataSetFieldUtil;

abstract public class Input extends AuroraComponent implements IDatasetFieldDelegate,
		DatasetBinder {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1913613647491922330L;
	// TYPE
	public static final String TEXT = "textField"; //$NON-NLS-1$
	public static final String NUMBER = "numberField"; //$NON-NLS-1$
	public static final String Combo = "comboBox"; //$NON-NLS-1$
	public static final String LOV = "lov"; //$NON-NLS-1$
	public static final String DATE_PICKER = "datePicker"; //$NON-NLS-1$
	public static final String DATETIMEPICKER = "dateTimePicker"; //$NON-NLS-1$

	public static final String CASE_LOWER = "lower"; //$NON-NLS-1$
	public static final String CASE_UPPER = "upper"; //$NON-NLS-1$
	public static final String CASE_ANY = ""; //$NON-NLS-1$
	private static final String[] CASE_TYPES = { CASE_ANY, CASE_UPPER,
			CASE_LOWER };
	private static final String[] CAL_ENABLES = { "pre", "next", "both", "none" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	public static final String[] INPUT_TYPES = { TEXT, NUMBER, Combo, LOV, DATE_PICKER,
			DATETIMEPICKER, CheckBox.CHECKBOX };
	// property key
	// number
	public static final String ALLOWDECIMALS = "allowDecimals"; //$NON-NLS-1$
	public static final String ALLOWNEGATIVE = "allowNegative"; //$NON-NLS-1$
	public static final String ALLOWFORMAT = "allowFormat"; //$NON-NLS-1$
	// all
	public static final String EMPTYTEXT = "emptyText"; //$NON-NLS-1$
	// combo lov text
	public static final String TYPECASE = "typeCase"; //$NON-NLS-1$
	// cal
	public static final String ENABLE_BESIDE_DAYS = "enableBesideDays"; //$NON-NLS-1$
	public static final String ENABLE_MONTH_BTN = "enableMonthBtn"; //$NON-NLS-1$

	// /

	private boolean allowDecimals = true;
	private boolean allowNegative = true;
	private boolean allowFormat = false;
	private String emptyText = ""; //$NON-NLS-1$
	private String typeCase = CASE_ANY;
	private String enableBesideDays = CAL_ENABLES[3];
	private String enableMonthBtn = CAL_ENABLES[3];

	private DatasetField dsField = new DatasetField();

	// /

	private static final IPropertyDescriptor PD_EMPYTEXT = new StringPropertyDescriptor(
			EMPTYTEXT, "EmptyText"); //$NON-NLS-1$
	private static final IPropertyDescriptor PD_TYPECASE = new ComboPropertyDescriptor(
			TYPECASE, "TypeCase", new String[] { Messages.Input_23, Messages.Input_24, Messages.Input_25 }); //$NON-NLS-1$
	private static final IPropertyDescriptor[] pds_text = { PD_PROMPT, PD_NAME,
			PD_WIDTH, PD_EMPYTEXT, PD_TYPECASE, DatasetField.PD_REQUIRED,
			DatasetField.PD_READONLY };
	private static final IPropertyDescriptor[] pds_number = { PD_PROMPT,
			PD_NAME, PD_WIDTH, PD_EMPYTEXT,
			new BooleanPropertyDescriptor(ALLOWDECIMALS, "AllowDecimals"), //$NON-NLS-1$
			new BooleanPropertyDescriptor(ALLOWNEGATIVE, "AllowNegative"), //$NON-NLS-1$
			new BooleanPropertyDescriptor(ALLOWFORMAT, "AllowFormat"), //$NON-NLS-1$
			DatasetField.PD_REQUIRED, DatasetField.PD_READONLY };

	private static final IPropertyDescriptor[] pds_combo = {
			PD_PROMPT,
			PD_NAME,
			PD_WIDTH,
			PD_EMPYTEXT,
			PD_TYPECASE,
			DatasetField.PD_REQUIRED,
			DatasetField.PD_READONLY,
			new StringPropertyDescriptor(DatasetField.OPTIONS, "*options", true), //$NON-NLS-1$
			new StringPropertyDescriptor(DatasetField.DISPLAY_FIELD,
					"*displayField", true), //$NON-NLS-1$
			new StringPropertyDescriptor(DatasetField.VALUE_FIELD,
					"*valueField", true), //$NON-NLS-1$
			new StringPropertyDescriptor(DatasetField.RETURN_FIELD,
					"*returnField", true) }; //$NON-NLS-1$

	private static final IPropertyDescriptor[] pds_lov = {
			PD_PROMPT,
			PD_NAME,
			PD_WIDTH,
			PD_EMPYTEXT,
			PD_TYPECASE,
			DatasetField.PD_REQUIRED,
			DatasetField.PD_READONLY,
			new StringPropertyDescriptor(DatasetField.LOV_SERVICE,
					"*lovService", true), //$NON-NLS-1$
			new StringPropertyDescriptor(DatasetField.TITLE, "*title") }; //$NON-NLS-1$
	private static final IPropertyDescriptor[] pds_datepicker = new IPropertyDescriptor[] {
			PD_PROMPT,
			PD_WIDTH,
			PD_NAME,
			new ComboPropertyDescriptor(ENABLE_BESIDE_DAYS, "EnableBesideDays", //$NON-NLS-1$
					CAL_ENABLES),
			new ComboPropertyDescriptor(ENABLE_MONTH_BTN, "EnableMonthBtn", //$NON-NLS-1$
					CAL_ENABLES), DatasetField.PD_REQUIRED,
			DatasetField.PD_READONLY };

	public Input() {
		this.setSize(new Dimension(120, 20));
		this.setType(TEXT);
		setDatasetField(dsField);
	}

	public boolean isRequired() {
		return dsField.isRequired();
	}

	public void setRequired(boolean required) {
		if (this.getDatasetField().isRequired() == required)
			return;
		boolean oldV = this.getDatasetField().isRequired();
		dsField.setRequired(required);
		firePropertyChange(REQUIRED, oldV, required);
	}

	public boolean isReadOnly() {
		return this.getDatasetField().isReadOnly();
	}

	public void setReadOnly(boolean readOnly) {
		if (this.getDatasetField().isReadOnly() == readOnly)
			return;
		boolean oldV = this.getDatasetField().isReadOnly();
		this.getDatasetField().setReadOnly(readOnly);
		firePropertyChange(READONLY, oldV, readOnly);
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		String type = getType();
		if (NUMBER.equals(type))
			return pds_number;
		else if (DATE_PICKER.equals(type) || DATETIMEPICKER.equals(type))
			return pds_datepicker;
		else if (Combo.equals(type))
			return pds_combo;
		else if (LOV.equals(type))
			return pds_lov;
		return pds_text;
	}

	public Object getPropertyValue(Object propName) {
		if (REQUIRED.equals(propName))
			return this.isRequired();
		if (READONLY.equals(propName))
			return this.isReadOnly();
		else if (ALLOWDECIMALS.equals(propName))
			return isAllowDecimals();
		else if (ALLOWNEGATIVE.equals(propName))
			return isAllowNegative();
		else if (ALLOWFORMAT.equals(propName))
			return isAllowFormat();
		else if (EMPTYTEXT.equals(propName))
			return getEmptyText();
		else if (TYPECASE.equals(propName))
			return indexOf(CASE_TYPES, getTypeCase());
		else if (ENABLE_BESIDE_DAYS.equals(propName))
			return indexOf(CAL_ENABLES, getEnableBesideDays());
		else if (ENABLE_MONTH_BTN.equals(propName))
			return indexOf(CAL_ENABLES, getEnableMonthBtn());
		else if (DatasetField.DISPLAY_FIELD.equals(propName)
				|| DatasetField.VALUE_FIELD.equals(propName)
				|| DatasetField.RETURN_FIELD.equals(propName)
				|| DatasetField.OPTIONS.equals(propName)
				|| DatasetField.LOV_GRID_HEIGHT.equals(propName)
				|| DatasetField.LOV_HEIGHT.equals(propName)
				|| DatasetField.LOV_SERVICE.equals(propName)
				|| DatasetField.LOV_URL.equals(propName)
				|| DatasetField.LOV_WIDTH.equals(propName)
				|| DatasetField.TITLE.equals(propName)
				|| DatasetField.DEFAULT_VALUE.equals(propName))
			return dsField.getPropertyValue(propName);
		return super.getPropertyValue(propName);
	}

	/**
	 * NumberField ,是否允许小数
	 * 
	 * @return
	 */
	public boolean isAllowDecimals() {
		return allowDecimals;
	}

	/**
	 * NumberField ,是否允许小数
	 * 
	 * @return
	 */
	public void setAllowDecimals(boolean allowDecimals) {
		this.allowDecimals = allowDecimals;
	}

	/**
	 * NumberField ,是否允许负数
	 * 
	 * @return
	 */
	public boolean isAllowNegative() {
		return allowNegative;
	}

	/**
	 * NumberField ,是否允许负数
	 * 
	 * @return
	 */
	public void setAllowNegative(boolean allowNegative) {
		this.allowNegative = allowNegative;
	}

	public void setPropertyValue(Object propName, Object val) {
		if (REQUIRED.equals(propName))
			this.setRequired((Boolean) val);
		if (READONLY.equals(propName))
			this.setReadOnly((Boolean) val);
		else if (ALLOWDECIMALS.equals(propName))
			setAllowDecimals((Boolean) val);
		else if (ALLOWNEGATIVE.equals(propName))
			setAllowNegative((Boolean) val);
		else if (ALLOWFORMAT.equals(propName))
			setAllowFormat((Boolean) val);
		else if (EMPTYTEXT.equals(propName))
			setEmptyText((String) val);
		else if (TYPECASE.equals(propName))
			setTypeCase(CASE_TYPES[(Integer) val]);
		else if (ENABLE_BESIDE_DAYS.equals(propName))
			setEnableBesideDays(CAL_ENABLES[(Integer) val]);
		else if (ENABLE_MONTH_BTN.equals(propName))
			setEnableMonthBtn(CAL_ENABLES[(Integer) val]);
		else if (DatasetField.DISPLAY_FIELD.equals(propName)
				|| DatasetField.VALUE_FIELD.equals(propName)
				|| DatasetField.RETURN_FIELD.equals(propName)
				|| DatasetField.OPTIONS.equals(propName)
				|| DatasetField.LOV_GRID_HEIGHT.equals(propName)
				|| DatasetField.LOV_HEIGHT.equals(propName)
				|| DatasetField.LOV_SERVICE.equals(propName)
				|| DatasetField.LOV_URL.equals(propName)
				|| DatasetField.LOV_WIDTH.equals(propName)
				|| DatasetField.TITLE.equals(propName))
			dsField.setPropertyValue(propName, val);
		else
			super.setPropertyValue(propName, val);
	}

	/**
	 * NumberField ,是否允许千分位分隔
	 * 
	 * @return
	 */
	public boolean isAllowFormat() {
		return allowFormat;
	}

	/**
	 * NumberField ,是否允许千分位分隔
	 * 
	 * @return
	 */
	public void setAllowFormat(boolean allowFormat) {
		this.allowFormat = allowFormat;
	}

	public String getEmptyText() {
		return emptyText;
	}

	public void setEmptyText(String emptyText) {
		if (eq(this.emptyText, emptyText))
			return;
		String oldV = this.emptyText;
		this.emptyText = emptyText;
		firePropertyChange(EMPTYTEXT, oldV, emptyText);
	}

	/**
	 * TextField ,大小写限制
	 * 
	 * @return
	 */
	public String getTypeCase() {
		return typeCase;
	}

	/**
	 * TextField ,大小写限制
	 * 
	 * @return
	 */
	public void setTypeCase(String typeCase) {
		this.typeCase = typeCase;
	}

	/**
	 * DatePicker ,本月前后月份补齐<br/>
	 * 
	 * @return
	 */
	public String getEnableBesideDays() {
		return enableBesideDays;
	}

	/**
	 * DatePicker ,本月前后月份补齐<br/>
	 * none|both|pre|next
	 * 
	 * @return
	 */
	public void setEnableBesideDays(String enableBesideDays) {
		this.enableBesideDays = enableBesideDays;
	}

	/**
	 * DatePicker ,月份按钮显示方式
	 * 
	 * @return
	 */
	public String getEnableMonthBtn() {
		return enableMonthBtn;
	}

	/**
	 * DatePicker ,月份按钮显示方式<br/>
	 * none|both|pre|next
	 * 
	 * @return
	 */
	public void setEnableMonthBtn(String enableMonthBtn) {
		this.enableMonthBtn = enableMonthBtn;
	}

	public void setName(String name) {
		super.setName(name);
		getDatasetField().setName(name);
	}

	private int indexOf(Object[] objs, Object o) {
		for (int i = 0; i < objs.length; i++)
			if (objs[i].equals(o))
				return i;
		return -1;
	}

	public DatasetField getDatasetField() {
		return this.dsField;
	}

	public void setParent(Container part) {
		super.setParent(part);
		if (dsField != null)
			dsField.setDataset(DataSetFieldUtil.findDataset(getParent()));
	}

	public void setDatasetField(DatasetField field) {
		dsField = field;
		dsField.setName(getName());
		dsField.setDataset(DataSetFieldUtil.findDataset(getParent()));
	}

}

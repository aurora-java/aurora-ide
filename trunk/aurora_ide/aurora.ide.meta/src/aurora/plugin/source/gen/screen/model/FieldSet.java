package aurora.plugin.source.gen.screen.model;

public class FieldSet extends BOX {
	public static final String FIELD_SET = "fieldSet";
	/**
	 * 
	 */

	public FieldSet() {
		setTitle("Fieldset");
		this.setComponentType(FIELD_SET);
		this.setSectionType(BOX.SECTION_TYPE_QUERY);
	}

	public int getHeadHight() {
		return 16;
	}
}

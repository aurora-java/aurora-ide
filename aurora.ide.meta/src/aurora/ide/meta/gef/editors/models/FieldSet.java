package aurora.ide.meta.gef.editors.models;

public class FieldSet extends BOX {
	public static final String FIELD_SET = "fieldSet";
	/**
	 * 
	 */
	private static final long serialVersionUID = 3990396088428828805L;

	public FieldSet() {
		setTitle("Fieldset");
		this.setType(FIELD_SET);
//		this.getDataset().setUseParentBM(false);
		this.setSectionType(BOX.SECTION_TYPE_QUERY);
	}

	public int getHeadHight() {
		return 16;
	}
}

package aurora.plugin.source.gen.screen.model;

public class Form extends BOX {
	public static final String FORM = "form";
	/**
	 * 
	 */

	public Form() {
		setTitle("Form");
		this.setComponentType(FORM);
		this.setSectionType(BOX.SECTION_TYPE_QUERY);
	}

	public int getHeadHight() {
		return 20;
	}
}

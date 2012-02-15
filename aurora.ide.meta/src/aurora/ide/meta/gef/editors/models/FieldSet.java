package aurora.ide.meta.gef.editors.models;

public class FieldSet extends BOX {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3990396088428828805L;

	public FieldSet() {
		setTitle("Fieldset");
		this.setType("fieldSet");
		this.getDataset().setUseParentBM(false);
	}

	public int getHeadHight() {
		return 16;
	}
}

package aurora.ide.meta.gef.designer.gen;

import aurora.ide.meta.gef.designer.model.BMModel;

public class ExtendBmGenerator {
	private BMModel model;
	private String baseName;

	public ExtendBmGenerator(BMModel model, String baseName) {
		this.model = model;
		this.baseName = baseName;
	}

	public void gen() {
		String ae = model.getAutoExtends();
		String[] ss = ae.split("\\|");
		for (String ext : ss) {
			createExtendBm(ext);
		}
	}

	private void createExtendBm(String extType) {
		String name = baseName + "_for_" + extType;
	}
}

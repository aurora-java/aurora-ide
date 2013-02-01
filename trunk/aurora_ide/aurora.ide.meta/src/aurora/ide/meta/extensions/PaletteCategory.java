package aurora.ide.meta.extensions;

import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.gef.editors.PaletteCategoryFilter;

public class PaletteCategory {
	private String label;
	private String filter;
	private String id;
	private PaletteCategoryFilter pcf;

	public PaletteCategory(String label, String filter, String id) {
		super();
		this.label = label;
		this.filter = filter;
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PaletteCategoryFilter getFilter() {
		if (pcf == null) {
			try {
				pcf = (PaletteCategoryFilter) Class.forName(filter)
						.newInstance();
			} catch (InstantiationException e) {
				DialogUtil.logErrorException(e);
			} catch (IllegalAccessException e) {
				DialogUtil.logErrorException(e);
			} catch (ClassNotFoundException e) {
				DialogUtil.logErrorException(e);
			}
		}
		return pcf;
	}
}

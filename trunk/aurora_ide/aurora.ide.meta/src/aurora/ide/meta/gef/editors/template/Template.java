package aurora.ide.meta.gef.editors.template;

import java.util.ArrayList;
import java.util.List;

public class Template extends Component {

	private String path;
	
	private String icon;
	private String description;
	private List<BMReference> bms = new ArrayList<BMReference>();
	private List<Template> refTemplates = new ArrayList<Template>();
	private boolean isForDisplay = false;

	public void addModel(BMReference bm) {
		bms.add(bm);
	}

	public List<BMReference> getBms() {
		return bms;
	}

	public void setBms(List<BMReference> bms) {
		this.bms = bms;
	}

	public List<Template> getRefTemplates() {
		return refTemplates;
	}

	public void setRefTemplates(List<Template> refTemplates) {
		this.refTemplates = refTemplates;
	}

	public Template() {

	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isForDisplay() {
		return isForDisplay;
	}

	public void setForDisplay(boolean isForDisplay) {
		this.isForDisplay = isForDisplay;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}

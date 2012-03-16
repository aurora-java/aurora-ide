package aurora.ide.meta.gef.editors.template.n;

import java.util.ArrayList;
import java.util.List;

public class Template extends Component {

	private String desc;
	private List<BMReference> bms = new ArrayList<BMReference>();
	private List<Template> refTemplates = new ArrayList<Template>();

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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

}

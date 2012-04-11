package aurora.ide.meta.gef.editors.template;

import java.util.ArrayList;
import java.util.List;

import aurora.ide.meta.gef.editors.models.link.Parameter;

public class TabRef extends Component {
	private String initModel;
	private String url;
	private List<Parameter> paras = new ArrayList<Parameter>();

	public String getInitModel() {
		return initModel;
	}

	public void setInitModel(String initModel) {
		this.initModel = initModel;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Parameter> getParas() {
		return paras;
	}

	public void setParas(List<Parameter> paras) {
		this.paras = paras;
	}

}

package aurora.ide.meta.gef.editors.models.link;

import java.util.ArrayList;
import java.util.List;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.TabItem;

public class TabRef extends AuroraComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 589556644803144211L;

	private String url;

	// parameter
	// a.b.c.d model
	// TODO
	private String paraPath;

	private TabItem tabItem;
	private List<Parameter> paras = new ArrayList<Parameter>();

	public String getUrl() {
		return url;
	}

	public TabItem getTabItem() {
		return tabItem;
	}

	public void setTabItem(TabItem tabItem) {
		this.tabItem = tabItem;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Parameter> getParameters() {
		return paras;
	}

	public void addParameter(Parameter para) {
		paras.add(para);
	}

}

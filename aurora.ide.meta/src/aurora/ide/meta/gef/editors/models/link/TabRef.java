package aurora.ide.meta.gef.editors.models.link;

import java.util.ArrayList;
import java.util.List;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.ILink;
import aurora.ide.meta.gef.editors.models.InitModel;
import aurora.ide.meta.gef.editors.models.TabItem;

public class TabRef extends AuroraComponent implements ILink{

	/**
	 * 
	 */
	private static final long serialVersionUID = 589556644803144211L;

	private String openPath;

	private InitModel initModel;

	public TabRef(){
		
	}
	
	public InitModel getInitModel() {
		return initModel;
	}

	public void setInitModel(InitModel initModel) {
		this.initModel = initModel;
	}

	private TabItem tabItem;
	private List<Parameter> paras = new ArrayList<Parameter>();

	public String getOpenPath() {
		return openPath;
	}

	public TabItem getTabItem() {
		return tabItem;
	}

	public void setTabItem(TabItem tabItem) {
		this.tabItem = tabItem;
	}

	public void setOpenPath(String url) {
		this.openPath = url;
	}

	public List<Parameter> getParameters() {
		return paras;
	}

	public void addAllParameter(List<Parameter> paras) {
		this.paras.addAll(paras);
	}

	public void addParameter(Parameter para) {
		paras.add(para);
	}

}

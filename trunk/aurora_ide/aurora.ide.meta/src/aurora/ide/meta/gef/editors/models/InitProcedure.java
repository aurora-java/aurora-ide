package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.List;

public class InitProcedure extends AuroraComponent {

	/**
	 * 
	 */

	private static final long serialVersionUID = -6830395452687270864L;

	private List<ModelQuery> queryModels = new ArrayList<ModelQuery>();

	public InitProcedure() {
		this.setType("init-procedure");
	}

	public List<ModelQuery> getModelQuerys() {
		return queryModels;
	}

	public void addModelQuery(ModelQuery model) {
		this.queryModels.add(model);
	}

}

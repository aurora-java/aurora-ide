package aurora.ide.meta.gef.editors.models;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.eclipse.core.runtime.Path;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.BooleanPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;
import aurora.ide.search.core.Util;

public class Dataset extends AuroraComponent {
	/**
	 *  
	 */
	private static final long serialVersionUID = -4619018857153616914L;
	// model a.b.c形式
	private String model = "";

	private boolean autoQuery = false;
	// private String queryUrl;
	// 自动生成
	private String id = "";
	// 是否只是QueryDS,如果true，生成的代码将不设置model
	private boolean isUse4Query;
	// 是否使用父的BM
	private boolean isUseParentBM = true;

	public static final String AUTO_QUERY = "autoQuery";
	public static final String MODEL = "model";
	public static final String ID = "id";

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			new BooleanPropertyDescriptor(AUTO_QUERY, "autoQuery"),
			new StringPropertyDescriptor(MODEL, "Model", true) };

	// new BooleanPropertyDescriptor("USE4QUERY", "isUse4Query"),
	// new BooleanPropertyDescriptor("USEPARENTBM", "isUseParentBM")

	public Dataset() {
		// 暂时不显示
		// this.setSize(new Dimension(50, 20));
		this.setType("dataSet");
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {

		return pds;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (AUTO_QUERY.equals(propName)) {
			return this.isAutoQuery();
		} else if (MODEL.equals(propName)) {
			return this.getModel();
		} else if (ID.equals(propName)) {
			return this.getId();
		} else if ("USE4QUERY".equals(propName)) {
			return isUse4Query;
		} else if ("USEPARENTBM".equals(propName)) {
			return isUseParentBM;
		}
		return null;
	}

	public void setPropertyValue(Object propName, Object val) {
		if (AUTO_QUERY.equals(propName)) {
			setAutoQuery((Boolean) val);
		} else if (MODEL.equals(propName)) {
			setModel((String) val);
		} else if (ID.equals(propName)) {
			setId((String) val);
		} else if ("USE4QUERY".equals(propName)) {
			setUse4Query((Boolean) val);
		} else if ("USEPARENTBM".equals(propName)) {
			setUseParentBM((Boolean) val);
		}
	}

	private Object getModelPKG() {
		return Util.toPKG(new Path(this.getModel()));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isAutoQuery() {
		return autoQuery;
	}

	public void setAutoQuery(boolean autoQuery) {
		this.autoQuery = autoQuery;
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();

	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public boolean isUse4Query() {
		return isUse4Query;
	}

	public void setUse4Query(boolean isUse4Query) {
		this.isUse4Query = isUse4Query;
	}

	public boolean isUseParentBM() {
		return isUseParentBM;
	}

	public void setUseParentBM(boolean isUseParentBM) {
		this.isUseParentBM = isUseParentBM;
	}

}

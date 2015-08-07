package aurora.plugin.esb.model;

import uncertain.composite.CompositeMap;

public class BusinessModelConsumer {
	private CompositeMap dataMap = new CompositeMap();

	public void set(String id, String business_model_name, String name) {
		getDataMap().put("name", name);
		getDataMap().put("id", id);
		getDataMap().put("business_model_name", business_model_name);

	}

	public CompositeMap getDataMap() {
		return dataMap;
	}

	public void setDataMap(CompositeMap dataMap) {
		this.dataMap = dataMap;
	}

	public String getId() {
		return dataMap.getString("id", "");
	}

	public String getName() {
		return dataMap.getString("name", "");
	}

	public String getBusinessModelName() {
		return dataMap.getString("business_model_name", "");
	}

}

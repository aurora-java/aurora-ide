package aurora.plugin.esb.model;

import uncertain.composite.CompositeMap;

public class BusinessModelData {

	// private String name;
	// private String type;
	// private Object data;

	private CompositeMap dataMap = new CompositeMap();

	// name,create_time,id,producer_id,data

	public BusinessModelData() {
	}

	public void set(String name, String create_time, String id,
			String producer_id, String data) {
		getDataMap().put("name", name);
		getDataMap().put("create_time", create_time);
		getDataMap().put("producer_id", producer_id);
		getDataMap().put("id", id);
		getDataMap().put("data", data);
	}

	public CompositeMap getDataMap() {
		return dataMap;
	}

	public void setDataMap(CompositeMap dataMap) {
		this.dataMap = dataMap;
	}

}

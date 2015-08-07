package aurora.plugin.esb.model;

import uncertain.composite.CompositeMap;

public class BusinessModelConsumerHistory {
	private CompositeMap dataMap = new CompositeMap();

	public void set(String id, String business_model_data_id,
			String consumer_id, String consume_time, String consume_status) {
		dataMap.put("id", id);
		dataMap.put("business_model_data_id", business_model_data_id);
		dataMap.put("consumer_id", consumer_id);
		dataMap.put("consume_time", consume_time);
		dataMap.put("consume_status", consume_status);
	}

	public String getId() {
		return dataMap.getString("id", "");
	}

	public String getBusinessModelDataId() {
		return dataMap.getString("business_model_data_id", "");
	}

	public String getConsumerId() {
		return dataMap.getString("consumer_id", "");
	}

	public String getConsumeStatus() {
		return dataMap.getString("consume_status", "");
	}

}

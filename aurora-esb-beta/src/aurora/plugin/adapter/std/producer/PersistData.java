package aurora.plugin.adapter.std.producer;

import aurora.plugin.esb.model.BusinessModel;

public class PersistData {
	private BusinessModel businessModel;

	public PersistData(BusinessModel businessModel) {
		this.businessModel = businessModel;
	}

	public String d() {
		System.out.println("Dispatching");
		return "direct:db";
	}
}

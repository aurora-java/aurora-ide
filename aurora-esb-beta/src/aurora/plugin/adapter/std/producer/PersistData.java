package aurora.plugin.adapter.std.producer;

import aurora.plugin.esb.model.BusinessModel;

public class PersistData {


	public String d() {
		System.out.println("Dispatching");
		return "direct:db";
	}
}

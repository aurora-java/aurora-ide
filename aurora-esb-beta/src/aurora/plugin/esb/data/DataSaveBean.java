package aurora.plugin.esb.data;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.BusinessModel;
import aurora.plugin.esb.model.BusinessModelData;
import aurora.plugin.esb.model.BusinessModelProducer;
import aurora.plugin.esb.util.BusinessModelUtil;

public class DataSaveBean {

	private AuroraEsbContext esbContext;
	private BusinessModelProducer businessModel;

	public DataSaveBean(AuroraEsbContext esbContext,
			BusinessModelProducer businessModel) {
		super();
		this.esbContext = esbContext;
		this.businessModel = businessModel;
	}

	public void save2File(Exchange exchange) {
		System.out.println("save2File");
	}

	public void save2Local(Exchange exchange) {

		String body = exchange.getIn().getBody(String.class);
		BusinessModelData data = new BusinessModelData();
		data.set(businessModel.getBusinessModelName(), new Date().toString(),
				"001", businessModel.getId(), body);
		BusinessModelUtil.save(data);
	}

	public void save2DB(Exchange exchange) {
		save2Local(exchange);
		// CompositeMap header = new CompositeMap("result");
		//
		// header.put("businessModelName", businessModel.getName());
		// String body = exchange.getIn().getBody(String.class);
		// header.setText(body);
		//
		// try {
		// CompositeMap executeProc = esbContext.executeProc("save_data",
		// header);
		// System.out.println("save2DB");
		// Message inin = exchange.getIn();
		// exchange.getOut().setHeaders(inin.getHeaders());
		// exchange.getOut().setBody(body);
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// exchange.getOut().setFault(true);
		// }
	}
}

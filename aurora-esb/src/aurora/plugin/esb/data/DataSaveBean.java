package aurora.plugin.esb.data;

import org.apache.camel.Exchange;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.BusinessModel;
import uncertain.composite.CompositeMap;

public class DataSaveBean {

	private AuroraEsbContext esbContext;
	private BusinessModel businessModel;

	public DataSaveBean(AuroraEsbContext esbContext, BusinessModel businessModel) {
		super();
		this.esbContext = esbContext;
		this.businessModel = businessModel;
	}

	public void save2File(Exchange exchange) {
		System.out.println("save2File");
	}

	public void save2DB(Exchange exchange) {

		CompositeMap header = new CompositeMap("result");
		
		header.put("businessModelName", businessModel.getName());
		header.setText("abc");
		
		 try {
			CompositeMap executeProc = esbContext.executeProc("save_data", header);
			System.out.println("save2DB");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

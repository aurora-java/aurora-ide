package aurora.plugin.esb.util;

import java.util.ArrayList;
import java.util.List;

import aurora.plugin.esb.model.BusinessModelData;

public class BusinessModelUtil {

	static private List<BusinessModelData> businessModelDatas = new ArrayList<BusinessModelData>();

	static public void save(BusinessModelData data) {
		businessModelDatas.add(data);
	}
}

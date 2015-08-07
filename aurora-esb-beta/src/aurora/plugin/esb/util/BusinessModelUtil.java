package aurora.plugin.esb.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.BusinessModelConsumer;
import aurora.plugin.esb.model.BusinessModelConsumerHistory;
import aurora.plugin.esb.model.BusinessModelData;
import aurora.plugin.esb.model.BusinessModelProducer;

public class BusinessModelUtil {

	static private Map<String, BusinessModelData> businessModelDatas = new HashMap<String, BusinessModelData>();

	static private Map<String, List<BusinessModelConsumerHistory>> consumerHistory = new HashMap<String, List<BusinessModelConsumerHistory>>();

	static private Map<String, BusinessModelProducer> businessModelProducers = new HashMap<String, BusinessModelProducer>();

	static private Map<String, BusinessModelConsumer> businessModelConsumers = new HashMap<String, BusinessModelConsumer>();

	static public void add(BusinessModelProducer businessModelProducer) {
		businessModelProducers.put(businessModelProducer.getId(),
				businessModelProducer);
	}

	static public BusinessModelConsumer getBusinessModelConsumer(String id){
		return businessModelConsumers.get(id);
	}
	static public BusinessModelProducer getBusinessModelProducer(String id){
		return businessModelProducers.get(id);
	}
	
	
	static public void startup(AuroraEsbContext esbContext) {
		BusinessModelProducer businessModelProducer = new BusinessModelProducer();
		businessModelProducer.set("001", "test", "001_producer");
		add(businessModelProducer);
		esbContext.addProducerMap(businessModelProducer.getDataMap());
		
		BusinessModelConsumer businessModelConsumer = new BusinessModelConsumer();

		businessModelConsumer.set("001", "test", "001_consumer");
		add(businessModelConsumer);
		esbContext.addConsumerMap(businessModelConsumer.getDataMap());
		businessModelConsumer = new BusinessModelConsumer();

		businessModelConsumer.set("002", "test", "002_consumer");
		add(businessModelConsumer);
		esbContext.addConsumerMap(businessModelConsumer.getDataMap());
	}

	static public void add(BusinessModelConsumer businessModelConsumer) {
		businessModelConsumers.put(businessModelConsumer.getId(),
				businessModelConsumer);
	}

	static public void save(BusinessModelData data) {
		businessModelDatas.put(data.getId(), data);
	}

	static public List<BusinessModelData> consumer(
			BusinessModelConsumer businessModelConsumer) {
		List<BusinessModelData> businessModelDatas = new ArrayList<BusinessModelData>();

		List<BusinessModelConsumerHistory> list = consumerHistory
				.get(businessModelConsumer.getName());
		if (list == null) {
			list = new ArrayList<BusinessModelConsumerHistory>();
			consumerHistory.put(businessModelConsumer.getName(), list);
		}

		List<String> consumerList = new ArrayList<String>();

		for (BusinessModelConsumerHistory businessModelConsumerHistory : list) {
			String businessModelDataId = businessModelConsumerHistory
					.getBusinessModelDataId();
			// businessModelConsumerHistory.
			consumerList.add(businessModelDataId);
		}
		List<String> unConsumeList = new ArrayList<String>();

		Set<String> keySet = BusinessModelUtil.businessModelDatas.keySet();

		for (String string : keySet) {
			if (consumerList.contains(string) == false) {
				unConsumeList.add(string);
			}
		}

		for (String string : unConsumeList) {
			businessModelDatas.add(BusinessModelUtil.businessModelDatas
					.get(string));
			BusinessModelConsumerHistory his = new BusinessModelConsumerHistory();
			his.set("001", string, businessModelConsumer.getId(), ""
					+ new Date(), "S");
			list.add(his);
		}

		return businessModelDatas;
	}

	public static List<BusinessModelConsumer>  getBusinessModelConsumer(
			BusinessModelProducer businessModelProducer) {
		String businessModelName = businessModelProducer.getBusinessModelName();
		List<BusinessModelConsumer> result = new ArrayList<BusinessModelConsumer>();
		Collection<BusinessModelConsumer> values = businessModelConsumers.values();
		for (BusinessModelConsumer businessModelConsumer : values) {
			if(businessModelName.equals(businessModelConsumer.getBusinessModelName()))
				result.add(businessModelConsumer);
		}
		return result;
		
	}

}

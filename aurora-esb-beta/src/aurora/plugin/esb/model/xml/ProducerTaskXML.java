package aurora.plugin.esb.model.xml;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.model.ProducerTask;

public class ProducerTaskXML implements XMLBuilder {

	private ProducerTask task;

	private String xml;

	private CompositeMap map;

	public ProducerTaskXML(String xml) {
		super();
		this.xml = xml;
	}

	public ProducerTaskXML(ProducerTask task) {
		super();
		this.task = task;
	}

	public ProducerTaskXML(CompositeMap map) {
		this.map = map;
	}

	@Override
	public String toXML() {
		return toCompositeMap().toXML();
	}

	@Override
	public Object toObject() {
		if (map != null)
			return toTask(map);
		CompositeMap map = XMLHelper.toMap(xml);
		return toTask(map);
	}

	@Override
	public CompositeMap toCompositeMap() {

		CompositeMap map = new CompositeMap("producer_task");

		map.put("id", task.getId());
		map.put("name", task.getName());
		map.put("start_time", task.getStartTime());
		map.put("end_time", task.getEndTime());
		// task.getStartTime();
		// task.getEndTime();
		// task.getFeedbackTime();
		map.put("status", task.getStatus());
		map.addChild(XMLHelper.toCompositeMap(task.getFrom()));
		return map;

	}

	public ProducerTask getTask() {
		return task;
	}

	public void setTask(ProducerTask task) {
		this.task = task;
	}

	private ProducerTask toTask(CompositeMap map) {
		ProducerTask t = new ProducerTask();
		if ("producer_task".equals(map.getName())) {
			t.setId(map.getString("id", ""));
			t.setName(map.getString("name", ""));
			t.setStatus(map.getString("status", ""));
			t.setEndTime(map.getLong("end_time", -1));
			t.setStartTime(map.getLong("start_time", -1));
			t.setFrom(XMLHelper.toFrom(map.getChild("from")));
		}
		return t;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

}

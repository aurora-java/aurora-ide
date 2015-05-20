package aurora.plugin.esb.model.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.xml.sax.SAXException;

import aurora.plugin.esb.model.ConsumerTask;
import aurora.plugin.esb.model.DirectConfig;
import aurora.plugin.esb.model.From;
import aurora.plugin.esb.model.ProducerTask;
import aurora.plugin.esb.model.Router;
import aurora.plugin.esb.model.TO;
import aurora.plugin.esb.model.Task;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

public class XMLHelper {

	static public CompositeMap toMap(String xml) {

		try {
			CompositeMap map = CompositeLoader.createInstanceForOCM()
					.loadFromString(xml);
			return map;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		CompositeMap map = new CompositeMap();
		return map;
	}

	static public DirectConfig toDirectConfig(String xml) {
		CompositeMap map = toMap(xml);
		return toDirectConfig(map);
	}

	static public DirectConfig toDirectConfig(CompositeMap map) {
		DirectConfig t = new DirectConfig();
		if ("task".equals(map.getName())) {
			// t.setId(map.getString("id", ""));
			t.setName(map.getString("name", ""));
			// t.setStatus(map.getString("status", ""));
			t.setRouter(toRouter(map.getChild("router")));
		}
		return t;
	}

	static public Router toRouter(CompositeMap map) {
		Router r = new Router();
		if ("router".equals(map.getName())) {
			r.setName(map.getString("name", ""));
			r.setFrom(toFrom(map.getChild("from")));
			r.setTo(toTo(map.getChild("to")));
		}
		return r;
	}

	static public TO toTo(CompositeMap map) {
		TO t = new TO();
		if (map == null)
			return t;
		if ("to".equals(map.getName())) {
			t.setName(map.getString("name", ""));
			t.setEndpoint(map.getString("endpoint", ""));
			t.setUserName(map.getString("username", ""));
			t.setPsd(map.getString("password", ""));
			CompositeMap child = map.getChild("para");
			if (child != null)
				t.setParaText(child.getText());
			t.setExchangeID(map.getString("exchangeid", ""));
		}

		return t;
	}

	static public From toFrom(CompositeMap map) {

		From f = new From();
		if (map == null)
			return f;
		if ("from".equals(map.getName())) {
			f.setName(map.getString("name", ""));
			f.setEndpoint(map.getString("endpoint", ""));
			f.setUserName(map.getString("username", ""));
			f.setPsd(map.getString("password", ""));
			f.setFeedbackPoint(map.getString("feedbackpoint", ""));
			CompositeMap child = map.getChild("para");
			if (child != null)
				f.setParaText(child.getText());
			f.setExchangeID(map.getString("exchangeid", ""));
		}
		return f;
	}

	// static public String toXML(Task task) {
	// CompositeMap map = toCompositeMap(task);
	// // task.getId();
	// // task.getName();
	// // task.getRouter();
	//
	// return map.toXML();
	// }

	// public static CompositeMap toCompositeMap(Task task) {
	// CompositeMap map = new CompositeMap("task");
	//
	// map.put("id", task.getId());
	// map.put("name", task.getName());
	//
	// // task.getStartTime();
	// // task.getEndTime();
	// // task.getFeedbackTime();
	// // task.getStatus();
	// map.put("status", task.getStatus());
	// map.addChild(toCompositeMap(task.getRouter()));
	// return map;
	// }

	static public String toXML(Router router) {
		CompositeMap map = toCompositeMap(router);
		return map.toXML();
	}

	public static CompositeMap toCompositeMap(Router router) {
		CompositeMap map = new CompositeMap("router");
		if (router != null) {
			map.put("name", router.getName());
			map.addChild(toCompositeMap(router.getFrom()));
			map.addChild(toCompositeMap(router.getTo()));
		}
		// String name = router.getName();
		// From from = router.getFrom();
		// TO to = router.getTo();
		return map;
	}

	static public String toXML(From from) {
		CompositeMap map = toCompositeMap(from);
		return map.toXML();
	}

	public static CompositeMap toCompositeMap(From from) {
		CompositeMap map = new CompositeMap("from");
		if (from == null)
			return map;
		map.put("endpoint", from.getEndpoint());
		map.put("feedbackpoint", from.getFeedbackPoint());
		map.put("name", from.getName());
		map.put("password", from.getPsd());
		map.put("username", from.getUserName());
		map.put("exchangeid", from.getExchangeID());
		map.createChild("para").setText(from.getParaText());
		// from.getEndpoint();
		// from.getFeedbackPoint();
		// from.getName();
		// from.getParaText();
		// from.getPsd();
		// from.getUserName();
		return map;
	}

	static public String toXML(TO to) {
		CompositeMap map = toCompositeMap(to);
		return map.toXML();
	}

	public static CompositeMap toCompositeMap(TO to) {
		CompositeMap map = new CompositeMap("to");
		if (to == null)
			return map;
		// to.getEndpoint();
		// to.getName();
		// to.getParaText();
		// to.getPsd();
		// to.getUserName();
		map.put("endpoint", to.getEndpoint());
		map.put("name", to.getName());
		map.put("password", to.getPsd());
		map.put("username", to.getUserName());
		map.put("exchangeid", to.getExchangeID());
		map.createChild("para").setText(to.getParaText());
		return map;
	}

	public static String inputStream2String(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}

	public static CompositeMap toCompositeMap(Task task) {
		if (task instanceof ProducerTask) {
			ProducerTaskXML xml = new ProducerTaskXML((ProducerTask) task);
			return xml.toCompositeMap();
		}
		if (task instanceof ConsumerTask) {
			ConsumerTaskXML xml = new ConsumerTaskXML((ConsumerTask) task);
			return xml.toCompositeMap();
		}

		return new CompositeMap();
	}

	public static Task toTask(CompositeMap map) {
		if ("consumer_task".equals(map.getName())) {
			ConsumerTaskXML xml = new ConsumerTaskXML(map);
			return (Task) xml.toObject();
		}
		if ("producer_task".equals(map.getName())) {
			ProducerTaskXML xml = new ProducerTaskXML(map);
			return (Task) xml.toObject();
		}
		return null;
	}

	public static Router toRouter(String xml) {
		CompositeMap map = toMap(xml);
		return toRouter(map);
	}

	public static Task toTask(String xml) {
		return toTask(toMap(xml));
	}
}

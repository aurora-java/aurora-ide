package aurora.plugin.esb.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.xml.sax.SAXException;

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

	static public Task toTask(String xml) {
		CompositeMap map = toMap(xml);
		return toTask(map);
	}

	static public Task toTask(CompositeMap map) {
		Task t = new Task();
		if ("task".equals(map.getName())) {
			t.setId(map.getString("id", ""));
			t.setName(map.getString("name", ""));
			t.setStatus(map.getString("status", ""));
			t.setRouter(toRouter(map.getChild("router")));
		}
		return t;
	}

	static private Router toRouter(CompositeMap map) {
		Router r = new Router();
		if ("router".equals(map.getName())) {
			r.setName(map.getString("name", ""));
			r.setFrom(toFrom(map.getChild("from")));
			r.setTo(toTo(map.getChild("to")));
		}
		return r;
	}

	static private TO toTo(CompositeMap map) {
		TO t = new TO();
		if ("to".equals(map.getName())) {
			t.setName(map.getString("name", ""));
			t.setEndpoint(map.getString("endpoint", ""));
			t.setUserName(map.getString("username", ""));
			t.setPsd(map.getString("password", ""));
			t.setParaText(map.getChild("para").getText());
			t.setExchangeID(map.getString("exchangeid", ""));
		}

		return t;
	}

	static private From toFrom(CompositeMap map) {

		From f = new From();
		if ("from".equals(map.getName())) {
			f.setName(map.getString("name", ""));
			f.setEndpoint(map.getString("endpoint", ""));
			f.setUserName(map.getString("username", ""));
			f.setPsd(map.getString("password", ""));
			f.setFeedbackPoint(map.getString("feedbackpoint", ""));
			f.setParaText(map.getChild("para").getText());
			f.setExchangeID(map.getString("exchangeid", ""));
		}
		return f;
	}

	static public String toXML(Task task) {
		CompositeMap map = toCompositeMap(task);
		// task.getId();
		// task.getName();
		// task.getRouter();

		return map.toXML();
	}

	public static CompositeMap toCompositeMap(Task task) {
		CompositeMap map = new CompositeMap("task");

		map.put("id", task.getId());
		map.put("name", task.getName());

		// task.getStartTime();
		// task.getEndTime();
		// task.getFeedbackTime();
		// task.getStatus();
		map.put("status", task.getStatus());
		map.addChild(toCompositeMap(task.getRouter()));
		return map;
	}

	static public String toXML(Router router) {
		CompositeMap map = toCompositeMap(router);
		return map.toXML();
	}

	public static CompositeMap toCompositeMap(Router router) {
		CompositeMap map = new CompositeMap("router");
		map.put("name", router.getName());
		map.addChild(toCompositeMap(router.getFrom()));
		map.addChild(toCompositeMap(router.getTo()));
		// String name = router.getName();
		// From from = router.getFrom();
		// TO to = router.getTo();
		return map;
	}

	static public String toXML(From from) {
		CompositeMap map = toCompositeMap(from);
		return map.toXML();
	}

	private static CompositeMap toCompositeMap(From from) {
		CompositeMap map = new CompositeMap("from");

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

	private static CompositeMap toCompositeMap(TO to) {
		CompositeMap map = new CompositeMap("to");
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
}

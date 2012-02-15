package aurora.ide.meta.gef.editors.wizard.template;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Input;

public class TemplateParsing extends DefaultHandler {

	private Temlpate template = new Temlpate();
	private String qName;
	private String region;

	private AuroraComponent model;
	private Stack<String> stack = new Stack<String>();

	private String[] inputType = { Input.TEXT, Input.NUMBER, Input.Combo, Input.LOV, Input.CAL, Input.DATETIMEPICKER };

	public void startDocument() throws SAXException {
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("template")) {
			template.setName(attributes.getValue(attributes.getIndex("name")));
		} else if (qName.equals("area")) {
			region = attributes.getValue(attributes.getIndex("region"));
		} else if (qName.equals("model")) {
			String type = attributes.getValue(attributes.getIndex("type"));
			try {
				Object obj = null;
				for (String s : inputType) {
					if (s.equalsIgnoreCase(type)) {
						obj = Class.forName("aurora.ide.meta.gef.editors.models.Input").newInstance();
						((Input)obj).setType(s);
						break;
					}
				}
				if (obj == null) {
					obj = Class.forName("aurora.ide.meta.gef.editors.models." + type).newInstance();
				}
				if ("area".equals(stack.peek())) {
					template.getModels().add((AuroraComponent) obj);
				} else if (model != null && (model instanceof Container)) {
					((Container) model).addChild((AuroraComponent) obj);
				}
				model = (AuroraComponent) obj;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		this.qName = qName;
		stack.push(qName);
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		if ("description".equals(qName)) {
			String desc = new String(ch, start, length).trim();
			if (desc != "") {
				template.setDescription(desc);
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		this.qName = null;
		stack.pop();
		model = model == null ? null : model.getParent();
		if (qName.equals("area")) {
			this.region = null;
		}
	}

	public Temlpate getTemplate() {
		return template;
	}

}

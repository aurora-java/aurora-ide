package aurora.ide.meta.gef.editors.template.parse;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import aurora.ide.meta.gef.editors.template.BMBindComponent;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.ide.meta.gef.editors.template.Button;
import aurora.ide.meta.gef.editors.template.Component;
import aurora.ide.meta.gef.editors.template.Template;

public class TemplateParse extends DefaultHandler {
	private Template template;
	private String qName;
	private Attributes attributes;
	private Stack<Component> stack = new Stack<Component>();

	public void startDocument() throws SAXException {
		template = new Template();
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		this.qName = qName;
		this.attributes = attributes;
		if (qName.equals("template")) {
			template.setName(getValue("name"));
			template.setIcon(getValue("iconPath"));
			if("true".equals(getValue("isForDisplay"))){
				template.setForDisplay(true);
			}
			stack.push(template);
		} else if (qName.equals("model")) {
			BMReference bm = new BMReference();
			bm.setId(getValue("id"));
			bm.setName(getValue("name"));
			template.addModel(bm);
			stack.push(null);
		} else if (qName.equals("button")) {
			Button btn = new Button();
			btn.setComponentType(qName);
			btn.setId(getValue("id"));
			btn.setTarget(getValue("target"));
			btn.setText(getValue("text"));
			btn.setType(getValue("type"));
			if (!stack.empty() && stack.peek() != null) {
				stack.peek().addChild(btn);
			}
			stack.push(btn);
		} else if (AuroraModelFactory.isComponent(qName)) {
			Component cpt = new Component();
			if (!"".equals(getValue("model"))) {
				if (!(cpt instanceof BMBindComponent)) {
					cpt = new BMBindComponent();
				}
				((BMBindComponent) cpt).setBmReferenceID(getValue("model"));
			}
			if (!"".equals(getValue("query"))) {
				if (!(cpt instanceof BMBindComponent)) {
					cpt = new BMBindComponent();
				}
				((BMBindComponent) cpt).setQueryComponent(getValue("query"));
			}
			cpt.setComponentType(qName);
			cpt.setId(getValue("id"));
			cpt.setName(getValue("name"));
			if (!stack.empty() && stack.peek() != null) {
				stack.peek().addChild(cpt);
			}
			stack.push(cpt);
		} else {
			stack.push(null);
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		if (qName.equals("description")) {
			String desc = new String(ch, start, length).trim();
			if (!"".equals(desc)) {
				template.setDescription(desc);
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		this.qName = "";
		this.stack.pop();
	}

	public void endDocument() throws SAXException {
		// for (Model model : template.getModels()) {
		// for (Region region : template.getRegions()) {
		// if (region.getModel().equals(model)) {
		// region.setModel(model);
		// }
		// }
		// }
	}

	public Template getTemplate() {
		return template;
	}

	private String getValue(String name) {
		String value = attributes.getValue(attributes.getIndex(name));
		return value == null ? "" : value;
	}
}

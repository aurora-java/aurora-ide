package aurora.ide.meta.gef.editors.template.handle;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import aurora.ide.meta.gef.editors.template.BMBindComponent;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.ide.meta.gef.editors.template.ButtonComponent;
import aurora.ide.meta.gef.editors.template.Component;
import aurora.ide.meta.gef.editors.template.LinkComponent;
import aurora.ide.meta.gef.editors.template.TabComponent;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.gef.i18n.Messages;

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
		int loc = qName.indexOf(":");
		if (loc >= 0) {
			qName = qName.substring(loc + 1);
		}
		if ((!stack.isEmpty()) && stack.peek() != null && "models".equals(stack.peek().getName())) { //$NON-NLS-1$
			BMReference bm = new BMReference();
			bm.setId(getValue("id")); //$NON-NLS-1$
			bm.setName(getValue("name")); //$NON-NLS-1$
			if (qName.equals("model")) {
				template.addModel(bm);
			} else {
				template.addLinkModel(bm);
			}
			stack.push(null);
		} else if (qName.equals("button")) { //$NON-NLS-1$
			ButtonComponent btn = new ButtonComponent();
			btn.setComponentType(qName);
			btn.setId(getValue("id")); //$NON-NLS-1$
			btn.setTarget(getValue("target")); //$NON-NLS-1$
			btn.setText(getValue("text")); //$NON-NLS-1$
			btn.setType(getValue("type")); //$NON-NLS-1$
			if (!stack.empty() && stack.peek() != null) {
				stack.peek().addChild(btn);
			}
			stack.push(btn);
		} else if ("link".equals(qName)) { //$NON-NLS-1$
			LinkComponent link = new LinkComponent();
			link.setId(getValue("id"));
			template.addLink(link);
			stack.push(link);
		} else if (AuroraModelFactory.isComponent(qName)) {
			Component cpt = null;
			if (qName.equals("tab")) {
				cpt = new TabComponent();
				((TabComponent) cpt).setModelQuery(getValue("model"));
				((TabComponent) cpt).setRef(getValue("ref"));
				((BMBindComponent) cpt).setBmReferenceID(getValue("model")); //$NON-NLS-1$
				((BMBindComponent) cpt).setQueryComponent(getValue("query")); //$NON-NLS-1$
			} else if (isBMBindComponent()) {
				cpt = new BMBindComponent();
				((BMBindComponent) cpt).setBmReferenceID(getValue("model")); //$NON-NLS-1$
				((BMBindComponent) cpt).setQueryComponent(getValue("query")); //$NON-NLS-1$
			} else {
				cpt = new Component();
			}
			cpt.setComponentType(qName);
			cpt.setId(getValue("id")); //$NON-NLS-1$
			cpt.setName(getValue("name")); //$NON-NLS-1$
			if (!stack.empty() && stack.peek() != null) {
				stack.peek().addChild(cpt);
			}
			stack.push(cpt);
		} else if (qName.equals("template")) { //$NON-NLS-1$
			template.setName(getValue("name")); //$NON-NLS-1$
			template.setIcon(getValue("icon")); //$NON-NLS-1$
			String category = getValue("category"); //$NON-NLS-1$
			template.setCategory("".equals(category) ? Messages.TemplateParse_Custom : category); //$NON-NLS-1$
			template.setType(getValue("type")); //$NON-NLS-1$
			stack.push(template);
		} else {
			Component c = new Component();
			c.setName(qName);
			stack.push(c);
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		if (qName.equals("description")) { //$NON-NLS-1$
			String desc = new String(ch, start, length).trim();
			if (!"".equals(desc)) { //$NON-NLS-1$
				template.setDescription(desc);
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		this.qName = ""; //$NON-NLS-1$
		this.stack.pop();
	}

	public void endDocument() throws SAXException {
	}

	public Template getTemplate() {
		return template;
	}

	private String getValue(String name) {
		String value = attributes.getValue(attributes.getIndex(name));
		return value == null ? "" : value; //$NON-NLS-1$
	}

	private boolean isBMBindComponent() {
		for (int i = 0; i < attributes.getLength(); i++) {
			if ("model".equalsIgnoreCase(attributes.getQName(i))) {
				return true;
			}
			if ("query".equalsIgnoreCase(attributes.getQName(i))) {
				return true;
			}
		}
		return false;
	}
}

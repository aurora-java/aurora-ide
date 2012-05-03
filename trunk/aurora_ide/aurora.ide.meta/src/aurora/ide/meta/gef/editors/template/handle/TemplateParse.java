package aurora.ide.meta.gef.editors.template.handle;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import aurora.ide.meta.gef.editors.template.BMBindComponent;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.ide.meta.gef.editors.template.ButtonComponent;
import aurora.ide.meta.gef.editors.template.Component;
import aurora.ide.meta.gef.editors.template.TabRefComponent;
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
		if (qName.equals("template")) { //$NON-NLS-1$
			template.setName(getValue("name")); //$NON-NLS-1$
			template.setIcon(getValue("iconPath")); //$NON-NLS-1$
			String category = getValue("category"); //$NON-NLS-1$
			template.setCategory("".equals(category) ? Messages.TemplateParse_Custom : category); //$NON-NLS-1$
			template.setType(getValue("type")); //$NON-NLS-1$
			stack.push(template);
		} else if (qName.equals("model")) { //$NON-NLS-1$
			BMReference bm = new BMReference();
			bm.setId(getValue("id")); //$NON-NLS-1$
			bm.setName(getValue("name")); //$NON-NLS-1$
			template.addModel(bm);
			stack.push(null);
		} else if (qName.equals("initModel")) { //$NON-NLS-1$
			BMReference bm = new BMReference();
			bm.setId(getValue("id")); //$NON-NLS-1$
			bm.setName(getValue("name")); //$NON-NLS-1$
			template.addInitModel(bm);
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
			// if (ButtonClicker.B_OPEN.equals(btn.getType())) {
			// template.addLink(btn);
			// }
			stack.push(btn);
		} else if ("tabRef".equals(qName)) { //$NON-NLS-1$
			TabRefComponent ref = new TabRefComponent();
			ref.setInitModel(getValue("initModel")); //$NON-NLS-1$
			if (!stack.empty() && stack.peek() != null) {
				stack.peek().addChild(ref);
			}
			template.addRef(ref);
			stack.push(ref);
		} else if (AuroraModelFactory.isComponent(qName)) {
			Component cpt = new Component();
			if (!"".equals(getValue("model"))) { //$NON-NLS-1$ //$NON-NLS-2$
				if (!(cpt instanceof BMBindComponent)) {
					cpt = new BMBindComponent();
				}
				((BMBindComponent) cpt).setBmReferenceID(getValue("model")); //$NON-NLS-1$
			}
			if (!"".equals(getValue("query"))) { //$NON-NLS-1$ //$NON-NLS-2$
				if (!(cpt instanceof BMBindComponent)) {
					cpt = new BMBindComponent();
				}
				((BMBindComponent) cpt).setQueryComponent(getValue("query")); //$NON-NLS-1$
			}
			cpt.setComponentType(qName);
			cpt.setId(getValue("id")); //$NON-NLS-1$
			cpt.setName(getValue("name")); //$NON-NLS-1$
			if (!stack.empty() && stack.peek() != null) {
				stack.peek().addChild(cpt);
			}
			if ("grid".equals(qName)) { //$NON-NLS-1$
				template.addLink(cpt);
			}
			stack.push(cpt);
		} else {
			stack.push(null);
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

}

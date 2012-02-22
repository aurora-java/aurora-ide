package aurora.ide.meta.gef.editors.template.parse;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import aurora.ide.meta.gef.editors.template.Button;
import aurora.ide.meta.gef.editors.template.ButtonRegion;
import aurora.ide.meta.gef.editors.template.Model;
import aurora.ide.meta.gef.editors.template.QueryRegion;
import aurora.ide.meta.gef.editors.template.Region;
import aurora.ide.meta.gef.editors.template.ResultRegion;
import aurora.ide.meta.gef.editors.template.Template;

public class TemplateParse extends DefaultHandler {
	private Template template;
	private String qName;
	private Region currentRegion;

	public void startDocument() throws SAXException {
		template = new Template();
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		this.qName = qName;
		if (qName.equals("template")) {
			template.setName(attributes.getValue(attributes.getIndex("name")));
			template.setIcon(attributes.getValue(attributes.getIndex("iconPath")));
		} else if (qName.equals("model")) {
			Model model = new Model();
			model.setId(attributes.getValue(attributes.getIndex("id")));
			model.setName(attributes.getValue(attributes.getIndex("name")));
			template.getModels().add(model);
		} else if (qName.equals("query")) {
			QueryRegion region = new QueryRegion();
			template.getRegions().add(getRegion(region, attributes));
		} else if (qName.equals("buttons")) {
			ButtonRegion region = new ButtonRegion();
			template.getRegions().add(getRegion(region, attributes));
		} else if (qName.equals("result")) {
			ResultRegion region = new ResultRegion();
			region.setQueryRegion(attributes.getValue(attributes.getIndex("query")));
			template.getRegions().add(getRegion(region, attributes));
		} else if (qName.equals("button")) {
			Button btn = new Button();
			btn.setType(attributes.getValue(attributes.getIndex("type")));
			btn.setTarget(attributes.getValue(attributes.getIndex("target")));
			btn.setText(attributes.getValue(attributes.getIndex("text")));
			((ButtonRegion) currentRegion).getButtons().add(btn);
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
	}

	public void endDocument() throws SAXException {
		for (Model model : template.getModels()) {
			for (Region region : template.getRegions()) {
				if (region.getModel().equals(model)) {
					region.setModel(model);
				}
			}
		}
	}

	public Template getTemplate() {
		return template;
	}

	private Region getRegion(Region region, Attributes attributes) {
		region.setId(attributes.getValue(attributes.getIndex("id")));
		region.setName(attributes.getValue(attributes.getIndex("name")));
		String s = attributes.getValue(attributes.getIndex("model"));
		Model m = new Model();
		m.setId(s);
		region.setModel(m);
		region.setContainer(attributes.getValue(attributes.getIndex("container")));
		currentRegion = region;
		return region;
	}
}

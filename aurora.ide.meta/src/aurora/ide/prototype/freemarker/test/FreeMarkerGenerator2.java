package aurora.ide.prototype.freemarker.test;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.prototype.freemarker.FreeMarkerGenerator;
import aurora.ide.prototype.freemarker.UID;
import aurora.ide.prototype.freemarker.model.ColumnMethod;
import aurora.ide.prototype.freemarker.model.PropertiesMethod;
import aurora.ide.prototype.freemarker.model.TemplateModelWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarkerGenerator2 extends FreeMarkerGenerator{

	private String property = System.getProperty("user.name");

	private String format = DateFormat.getDateInstance().format(
			new java.util.Date());

	private UID uid = new UID();

	public FreeMarkerGenerator2() {
	}

	public String gen(CompositeMap cm) throws IOException, TemplateException,
			SAXException {

		Template temp = FMConfigration.Instance().getTemplate("");

		/* 创建数据模型 */
		Map root = new HashMap();
		root.put("properties", new PropertiesMethod(this));
		root.put("columns", new ColumnMethod(this));

		Map config = new HashMap();

		root.put("config", config);
		config.put("encoding", "UTF-8");
		config.put("date", format);
		config.put("author", property);
		config.put("revision", "1.0");
		config.put("copyright", "add by aurora_ide team");

		root.put("screen", new TemplateModelWrapper("none", cm, this));

		/* 将模板和数据模型合并 */
		// Writer out = new OutputStreamWriter(System.out);
		StringWriter out = new StringWriter();
		temp.process(root, out);
		out.flush();
		String string = out.toString();
		if(true)
			return string;
		CompositeMap loadFromString = AuroraResourceUtil.getCompsiteLoader()
				.loadFromString(string, "UTF-8");
		String xml = loadFromString.toXML();
		return xml;
	}

	public UID getUid() {
		return uid;
	}

}

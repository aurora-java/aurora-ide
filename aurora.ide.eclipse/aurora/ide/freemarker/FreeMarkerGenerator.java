package aurora.ide.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.xml.sax.SAXException;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarkerGenerator {

	public FreeMarkerGenerator() {
	}

	public String gen(Template template,Map config) throws IOException, TemplateException,
			SAXException {

//		Template temp = FMConfigration.Instance().getTemplate("");
//

//
		/* 将模板和数据模型合并 */
		// Writer out = new OutputStreamWriter(System.out);
		StringWriter out = new StringWriter();
		template.process(config, out);
//		template.setEncoding(encoding)
//		template.getConfiguration();
		out.flush();
		String string =  out.toString();
		return string;
	}
}

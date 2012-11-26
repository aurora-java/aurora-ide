package aurora.ide.views.prompts.preference;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import aurora.ide.freemarker.FMConfigration;
import aurora.ide.helpers.AuroraConstant;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class PromptsRegisterSqlConfigration extends FMConfigration {


	public static final String CONF_FOLDER = "functionRegisterSql";

	public PromptsRegisterSqlConfigration() {

	}


	public Template getTemplate() throws IOException, SAXException {
		File file = PromptsRegisterPreferencePage.getTemplateFile();
		Configuration cc = this.createConfigration(file.getParent());
		Template template = cc.getTemplate(file.getName(),
				AuroraConstant.ENCODING);
		return template;
	}

}

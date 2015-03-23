package aurora.ide.freemarker;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import aurora.ide.helpers.AuroraConstant;
import aurora.ide.preferencepages.FunctionRegisterPreferencePage;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class FMFunctionRegisterSqlConfigration extends FMConfigration {


	public static final String CONF_FOLDER = "functionRegisterSql";

	public FMFunctionRegisterSqlConfigration() {

	}


	public Template getTemplate() throws IOException, SAXException {
		File file = FunctionRegisterPreferencePage.getTemplateFile();
		Configuration cc = this.createConfigration(file.getParent());
		Template template = cc.getTemplate(file.getName(),
				AuroraConstant.ENCODING);
		return template;
	}
}

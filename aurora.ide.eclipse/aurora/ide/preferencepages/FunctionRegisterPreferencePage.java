package aurora.ide.preferencepages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXException;

import aurora.ide.editor.textpage.action.RegisterSql;
import freemarker.template.TemplateModelException;

public class FunctionRegisterPreferencePage extends BaseTemplatePreferencePage {

	public static String template_dir_name = "function.register"; //$NON-NLS-1$

	public FunctionRegisterPreferencePage() {
		super();
		setDescription(Messages.FunctionRegisterPreferencePage_1);
	}

	@Override
	protected String getTemplateDirName() {
		return template_dir_name;
	}

	@Override
	protected String getNewFileName(Template tpl) {
		return super.getNewFileName(tpl);
	}

	@Override
	protected Map<?, ?> getSimpleModel() {
		try {
			return RegisterSql.createTempConfig();
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
		return new HashMap();
	}

	public static InputStream getTemplateContent() throws IOException,
			SAXException {
		return BaseTemplatePreferencePage.getTemplateContent(template_dir_name);
	}

	public static File getTemplateFile() throws IOException, SAXException {
		return BaseTemplatePreferencePage.getTemplateFile(template_dir_name);
	}

}
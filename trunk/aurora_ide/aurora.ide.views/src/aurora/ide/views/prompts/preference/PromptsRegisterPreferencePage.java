package aurora.ide.views.prompts.preference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.xml.sax.SAXException;

import aurora.ide.editor.textpage.action.RegisterSql;
import aurora.ide.preferencepages.BaseTemplatePreferencePage;
import freemarker.template.TemplateModelException;

public class  PromptsRegisterPreferencePage extends BaseTemplatePreferencePage {

	public static final String template_dir_name = "prompts.register"; //$NON-NLS-1$

	public PromptsRegisterPreferencePage() {
		super();
		setDescription("prompts脚本摸版");
	}

	protected String getTemplateDirName() {
		return template_dir_name;
	}

	@Override
	protected Map<?, ?> getSimpleModel() {
		try {
			return RegisterSql.createTempConfig();
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
		return Collections.EMPTY_MAP;
	}

	public static InputStream getTemplateContent() throws IOException,
			SAXException {
		return BaseTemplatePreferencePage.getTemplateContent(template_dir_name);
	}

	public static File getTemplateFile() throws IOException, SAXException {
		return BaseTemplatePreferencePage.getTemplateFile(template_dir_name);
	}

}
package aurora.ide.prototype.freemarker;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;

import aurora.ide.meta.MetaPlugin;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class FMConfigration {

	private static final FMConfigration instance = new FMConfigration();
	
	private Template template;
	private Configuration cfg;
	private FMConfigration() {

	}

	public static FMConfigration Instance() {
		return instance;
	}
	
	private Configuration createConfigration() throws IOException{
		
		IPath append = MetaPlugin.getDefault().getStateLocation().append("template");
		/* 创建和调整配置。 */
		Configuration cfg = new Configuration();
		cfg.setDirectoryForTemplateLoading(append.toFile());
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		/* 在整个应用的生命周期中,这个工作你可以执行多次 */
		/* 获取或创建模板 */
		return cfg;
	}
	
	public Template getTemplate(String type) throws IOException{
		if(template == null){
			template = createTemplate();
		}
		return template;
	}

	private Template createTemplate() throws IOException {
		if(cfg==null){
			cfg = createConfigration();
		}
		return cfg.getTemplate("template.ftl");
	}
	

}

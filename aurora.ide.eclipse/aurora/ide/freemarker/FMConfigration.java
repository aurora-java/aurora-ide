package aurora.ide.freemarker;

import java.io.IOException;

import org.eclipse.core.runtime.Path;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

abstract public class FMConfigration {

	private Configuration cfg;

	protected Configuration createConfigration(String folder)
			throws IOException {

		
//		IPath append = AuroraPlugin.getDefault().getStateLocation()
//				.append(folder);
		
		/* 创建和调整配置。 */
		Configuration cfg = new Configuration();
		cfg.setDirectoryForTemplateLoading(new Path(folder).toFile());
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		/* 在整个应用的生命周期中,这个工作你可以执行多次 */
		/* 获取或创建模板 */
		return cfg;
	}

	protected Template createTemplate(String confFolder, String name)
			throws IOException {
		if (cfg == null) {
			cfg = createConfigration(confFolder);
		}
		return cfg.getTemplate(name);
	}

}

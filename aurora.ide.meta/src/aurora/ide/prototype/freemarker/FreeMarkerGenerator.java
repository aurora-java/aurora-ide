package aurora.ide.prototype.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.freemarker.model.ColumnMethod;
import aurora.ide.prototype.freemarker.model.PropertiesMethod;
import aurora.ide.prototype.freemarker.model.TemplateModelWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarkerGenerator {

	private String property = System.getProperty("user.name");

	private String format = DateFormat.getDateInstance().format(
			new java.util.Date());
	
	private UID uid = new UID();

	public FreeMarkerGenerator() {
	}

	public void gen(CompositeMap cm) throws IOException, TemplateException {

		/*
		 * 在整个应用的生命周期中, 这个工作你应该只做一次。
		 */
		/* 创建和调整配置。 */
		Configuration cfg = new Configuration();
		cfg.setDirectoryForTemplateLoading(new File(
				"/Users/shishiliyan/Desktop/work/aurora/workspace/aurora/freemarker_test/template"));
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		/* 在整个应用的生命周期中,这个工作你可以执行多次 */
		/* 获取或创建模板 */
		Template temp = cfg.getTemplate("test3.ftl");
		// TemplateSequenceModel childNodes = temp.getRootTreeNode()
		// .getChildNodes();
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
		
//		List childsNotNull = cm.getChildsNotNull();
//		for (Object object : childsNotNull) {
//			if(object instanceof CompositeMap){
//				
//			}
//		}
		
		root.put("screen", new TemplateModelWrapper("none",cm,this));

		/* 将模板和数据模型合并 */
		Writer out = new OutputStreamWriter(System.out);
		temp.process(root, out);
		out.flush();

	}

	public UID getUid() {
		return uid;
	}
	
}

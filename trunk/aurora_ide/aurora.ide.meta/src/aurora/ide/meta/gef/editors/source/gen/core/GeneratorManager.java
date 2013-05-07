package aurora.ide.meta.gef.editors.source.gen.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.preference.UIPrototypeGeneratorPreferencePage;
import aurora.plugin.source.gen.BuilderSession;
import aurora.plugin.source.gen.ModelMapParser;
import aurora.plugin.source.gen.SourceGenManager;
import aurora.plugin.source.gen.SourceTemplateProvider;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

public class GeneratorManager {
	private SourceGenManager sgm;
	private SourceTemplateProvider stp;

	private GeneratorManager(final IProject project){
		sgm = new SourceGenManager() {
			public ModelMapParser createModelMapParser(CompositeMap model) {
				ModelMapParser mmp = new IDEModelMapParser(model,
						project);
				return mmp;
			}

			protected void loadBuilders() {
				if (getBuilders() != null) {
					return;
				}
				setBuilders(new HashMap<String, String>());
				File component_file;
//				File f = new File(
//						"/Users/shiliyan/Desktop/work/aurora/workspace/aurora_runtime/hap/WebContent/WEB-INF/aurora.plugin.source.gen");
//				File f = getDefaultSourceGenTemplatePath().toFile();
				File f = new File(UIPrototypeGeneratorPreferencePage.getPath());
				File config = new File(f, "config");
				component_file = new File(config, "components.xml");
				CompositeLoader loader = new CompositeLoader();
				try {
					CompositeMap components = loader
							.loadByFullFilePath(component_file.getPath());
					components.iterate(new IterationHandle() {
						public int process(CompositeMap map) {
							String component_type = map
									.getString(
											ComponentInnerProperties.COMPONENT_TYPE,
											"");
							String builder = map.getString("builder", "");
							if ("".equals(component_type) == false) {
								getBuilders().put(component_type.toLowerCase(),
										builder);
							}
							return IterationHandle.IT_CONTINUE;
						}
					}, false);
				} catch (Exception ex) {
					// load builders false
					throw new RuntimeException(ex);
				}
			}
		};
		stp = new SourceTemplateProvider() {
			private File theme;

			protected File getTemplateTheme() {
				if (theme != null)
					return theme;
//				File f = new File(
//						"/Users/shiliyan/Desktop/work/aurora/workspace/aurora_runtime/hap/WebContent/WEB-INF/aurora.plugin.source.gen");
//				File f = getDefaultSourceGenTemplatePath().toFile();
				File f = new File(UIPrototypeGeneratorPreferencePage.getPath());
				File tFolder = new File(f, "template");
				theme = new File(tFolder, this.getTemplate());
				return theme;
			}

		};
		sgm.setTemplateProvider(stp);
		stp.setSourceGenManager(sgm);
		stp.setTemplate("default");
		stp.initialize();	
	}

	public static IPath getDefaultSourceGenTemplatePath(){
		IPath template = MetaPlugin.getDefault().getStateLocation().append("source.gen");
		return template;
	}
	
	public static GeneratorManager createNewInstance(IProject project){
		GeneratorManager generatorManager = new GeneratorManager(project);
		return generatorManager;
	}

	public CompositeMap buildScreen(CompositeMap modelMap,
			BuilderSession session) throws IOException, SAXException {
		return sgm.buildScreen(modelMap, session);
	}
	
	public BuilderSession createBuilderSession(){
		return  new BuilderSession(sgm);
	}
	
	
	
	
}

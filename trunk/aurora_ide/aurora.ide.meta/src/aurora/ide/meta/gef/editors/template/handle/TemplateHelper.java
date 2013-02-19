package aurora.ide.meta.gef.editors.template.handle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.xml.sax.SAXException;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.ButtonClicker;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.models.link.TabRef;
import aurora.ide.meta.gef.editors.template.BMBindComponent;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.ide.meta.gef.editors.template.ButtonComponent;
import aurora.ide.meta.gef.editors.template.Component;
import aurora.ide.meta.gef.editors.template.LinkComponent;
import aurora.ide.meta.gef.editors.template.TabComponent;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.project.AuroraMetaProjectNature;

public class TemplateHelper {

	private static Map<String, Template> templates = new HashMap<String, Template>();

	//private static TemplateHelper tph = null;

	private TemplateConfig config;
	private Map<String, String> queryRelated;
	private int tabItemIndex = 0;
	public static final String MODEL = "bm";
	public static final String TAB_ITEM = "tab";
	public static final String LINK = "link";
	public static final String INIT_MODEL = "initBm";

//	public static TemplateHelper getInstance() {
//		if (tph == null) {
//			tph = new TemplateHelper();
//		}
//		return tph;
//	}

	public TemplateHelper() {

	}

	public void clearTemplate() {
		templates.clear();
	}

	private void loadTemplate(IPath path) {
		if (path == null) {
			return;
		}
		templates.clear();
		List<File> files = getFiles(path.toString(), ".xml");
		SAXParser parser = null;
		try {
			parser = SAXParserFactory.newInstance().newSAXParser();
		} catch (ParserConfigurationException e) {
			return;
		} catch (SAXException e) {
			return;
		}
		TemplateParse tp = new TemplateParse();
		for (File f : files) {
			try {
				parser.parse(f, tp);
			} catch (SAXException e) {
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			Template tm = tp.getTemplate();
			tm.setIcon("template/thumbnails/" + tm.getIcon());
			String p = f.getPath();
			if (p.indexOf("template") > 0) {
				p = p.substring(p.indexOf("template"));
			}
			tm.setPath(p);
			templates.put(tm.getPath(), tm);
		}
	}

	private List<File> getFiles(String path, final String extension) {
		List<File> files = new ArrayList<File>();

		java.io.File file = new File(path.toString());
		if ((!file.exists()) || (!file.isDirectory())) {
			return files;
		}

		for (File f : file.listFiles()) {
			if (f.isFile() && f.getName().toLowerCase().endsWith(extension)) {
				files.add(f);
			}
		}
		return files;
	}

	public boolean isMetaProject(IResource container) {
		try {
			return container.getProject().hasNature(AuroraMetaProjectNature.ID);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Map<String, List<Template>> getTemplates(IPath path) {
		if (templates.size() <= 0) {
			loadTemplate(path);
		}
		Map<String, List<Template>> tempMap = new HashMap<String, List<Template>>();
		for (Template tm : templates.values()) {
			if (tempMap.get(tm.getCategory()) == null) {
				tempMap.put(tm.getCategory(), new ArrayList<Template>());
			}
			tempMap.get(tm.getCategory()).add(tm);
		}
		return tempMap;
	}

	public Template getTemplate(String key) {
		if (templates.size() <= 0) {
			URL ts = FileLocator.find(Platform.getBundle(MetaPlugin.PLUGIN_ID), new Path("template"), null);
			try {
				ts = FileLocator.toFileURL(ts);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			IPath path = new Path(ts.getPath());
			loadTemplate(path);
		}
		return templates.get(key);
	}

	public ViewDiagram createView(Template template) {
		initVariable(template);
		ViewDiagram viewDiagram = new ViewDiagram();
		for (Component c : template.getChildren()) {
			AuroraComponent ac = createAuroraComponent(c);
			if (ac instanceof AuroraComponent) {
				viewDiagram.addChild(ac);
			}
		}
		fillQueryRelated();
		viewDiagram.setTemplateType(template.getType());
		viewDiagram.setBindTemplate(template.getPath());
		if (config.get(TAB_ITEM) != null && config.get(TAB_ITEM).size() > 0) {
			((TabItem) config.get(TAB_ITEM).get(0)).setCurrent(true);
		}
		return viewDiagram;
	}

	private void initVariable(Template template) {
		template.clear();
		config = new TemplateConfig();
		config.put(MODEL, template.getBms());
		config.put(INIT_MODEL, template.getLinkBms());
		config.put(LINK, template.getLink());
		queryRelated = new HashMap<String, String>();
		tabItemIndex = 0;
	}

	private AuroraComponent createAuroraComponent(Component c) {
		AuroraComponent ac = AuroraModelFactory.createComponent(c.getComponentType());
		if (ac == null) {
			return null;
		}
		if (c.getName() != null) {
			ac.setName(c.getName());
		}
		if (null != c.getId() && !"".equals(c.getId())) {
			config.getAuroraComponents().put(c.getId(), ac);
		}
		if ((c instanceof BMBindComponent) && (ac instanceof Container)) {
			fillContainer((BMBindComponent) c, (Container) ac);
		}
		if (ac instanceof TabItem) {
			((TabItem) ac).setPrompt("tabItem" + tabItemIndex++);
			fillTabRef((TabItem) ac, (TabComponent) c);
		}
		if ((c instanceof ButtonComponent) && (ac instanceof Button)) {
			fillButton((ButtonComponent) c, (Button) ac);
		}
		if (c.getChildren() == null) {
			return ac;
		}
		for (Component c_child : c.getChildren()) {
			AuroraComponent ac_child = createAuroraComponent(c_child);
			if (ac instanceof Container) {
				((Container) ac).addChild(ac_child);
			}
		}
		return ac;
	}

	private void fillQueryRelated() {
		for (String s : queryRelated.keySet()) {
			Object obj = config.getAuroraComponents().get(s);
			if (obj instanceof Button) {
				Button btn = (Button) obj;
				AuroraComponent ac = config.getAuroraComponents().get(queryRelated.get(s));
				btn.getButtonClicker().setTargetComponent(ac);
			} else if (obj instanceof Container) {
				Container c = (Container) obj;
				ResultDataSet ds = (ResultDataSet) c.getDataset();
				AuroraComponent ac = config.getAuroraComponents().get(queryRelated.get(s));
				if (ac instanceof Container) {
					ds.setOwner(c);
					ds.setQueryContainer((Container) ac);
				}
			}
		}
	}

	private void fillButton(ButtonComponent btnc, Button btn) {
		if ("toolBar".equals(btnc.getParent().getComponentType()) && contains(Button.std_types, btnc.getType())) {
			btn.setButtonType(btnc.getType());
		} else if (contains(ButtonClicker.action_ids, btnc.getType())) {
			ButtonClicker bc = btn.getButtonClicker();
			if (bc == null) {
				bc = new ButtonClicker();
				bc.setButton(btn);
			}
			bc.setActionID(btnc.getType());
			btn.setButtonClicker(bc);
			btn.setText(btnc.getText());
			// bc.setOpenPath(btn.getUrl());
			queryRelated.put(btnc.getId(), btnc.getTarget());
		}
	}

	private void fillTabRef(TabItem ac, TabComponent c) {
		if (!isRefTab(c)) {
			return;
		}
		if (config.get(TAB_ITEM) == null) {
			config.put(TAB_ITEM, new ArrayList<Object>());
		}
		config.get(TAB_ITEM).add(ac);
		TabRef ref = ac.getTabRef();
		if (ref == null) {
			ref = new TabRef();
		}
		ac.setTabRef(ref);
		String ibmId = c.getModelQuery();
		BMReference bm = null;
		for (Object b : config.get(INIT_MODEL)) {
			if (!ibmId.equals(((BMReference) b).getId())) {
				continue;
			}
			bm = (BMReference) b;
			break;
		}
		if (bm == null) {
			return;
		}
		if (config.getInitModelRelated().get(bm) == null) {
			config.getInitModelRelated().put(bm, new ArrayList<TabItem>());
		}
		config.getInitModelRelated().get(bm).add(ac);
	}

	private boolean isRefTab(TabComponent c) {
		if (c.getRef() == null && "".equals(c.getRef().trim())) {
			return false;
		}
		for (Object obj : config.get(LINK)) {
			if (c.getRef().equals(((LinkComponent) obj).getId())) {
				return true;
			}
		}
		return false;
	}

	private boolean contains(String[] ss, String s) {
		if (ss == null || s == null) {
			return false;
		}
		for (String st : ss) {
			if (st.equals(s)) {
				return true;
			}
		}
		return false;
	}

	private void fillContainer(BMBindComponent c, Container ac) {
		String bmId = c.getBmReferenceID();
		if (bmId == null || "".equals(bmId)) {
			return;
		}
		BMReference bm = null;
		for (Object b : config.get(MODEL)) {
			if (!bmId.equals(((BMReference) b).getId())) {
				continue;
			}
			bm = (BMReference) b;
			break;
		}
		if (bm == null) {
			return;
		}
		ResultDataSet ds = new ResultDataSet();
		ds.setOwner(ac);
		ac.setDataset(ds);
		ac.setSectionType(Container.SECTION_TYPE_RESULT);
		if (config.getModelRelated().get(bm) == null) {
			config.getModelRelated().put(bm, new ArrayList<Container>());
		}
		config.getModelRelated().get(bm).add(ac);
		String qcId = c.getQueryComponent();
		if (qcId != null && (!"".equals(qcId))) {
			queryRelated.put(c.getId(), qcId);
			config.getQueryModelRelated().put(bm, qcId);
		}
	}

	public TemplateConfig getConfig() {
		return config;
	}

	public void setConfig(TemplateConfig config) {
		this.config = config;
	}
}

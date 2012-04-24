package aurora.ide.meta.gef.editors.template.handle;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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
import aurora.ide.meta.gef.editors.template.TabRefComponent;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.project.AuroraMetaProjectNature;

public class TemplateHelper {

	private static Map<String, Template> templates = new HashMap<String, Template>();

	private static TemplateHelper tph = null;

	private Map<String, String> queryRelated;
	private Map<BMReference, List<Container>> modelRelated;
	private Map<BMReference, List<TabItem>> initModelRelated;
	private Map<String, AuroraComponent> auroraComponents;
	private Map<BMReference, String> queryModelRelated;
	private List<BMReference> bms;
	private List<BMReference> initBms;
	private List<TabItem> tabItem;

	private int tabItemIndex = 0;

	public static TemplateHelper getInstance() {
		if (tph == null) {
			tph = new TemplateHelper();
		}
		return tph;
	}

	private TemplateHelper() {

	}

	private void loadTemplate() {
		IPath path = MetaPlugin.getDefault().getStateLocation().append("template");
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
				continue;
			}
			Template tm = tp.getTemplate();
			tm.setIcon(path.append("thumbnails/" + tm.getIcon()).toString());
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

		for (File f : file.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (dir.isDirectory()) {
					return true;
				} else if (name.toLowerCase().endsWith(extension)) {
					return true;
				}
				return false;
			}
		})) {
			if (f.isFile()) {
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

	public Map<String, List<Template>> getTemplates() {
		if (templates.size() <= 0) {
			loadTemplate();
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

	public Template getTemplates(String key) {
		if (templates.size() <= 0) {
			loadTemplate();
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
		if (tabItem.size() > 0) {
			tabItem.get(0).setCurrent(true);
			for (int i = 1; i < tabItem.size(); i++) {
				tabItem.get(i).setCurrent(false);
			}
		}
		return viewDiagram;
	}

	private void initVariable(Template template) {
		template.clear();
		bms = template.getBms();
		initBms = template.getInitBms();
		queryRelated = new HashMap<String, String>();
		auroraComponents = new HashMap<String, AuroraComponent>();
		modelRelated = new HashMap<BMReference, List<Container>>();
		initModelRelated = new HashMap<BMReference, List<TabItem>>();
		queryModelRelated = new HashMap<BMReference, String>();
		tabItem = new ArrayList<TabItem>();
		tabItemIndex = 0;
	}

	private AuroraComponent createAuroraComponent(Component c) {
		AuroraComponent ac = AuroraModelFactory.createComponent(c.getComponentType());
		if (ac == null) {
			return null;
		}
		if (null != c.getId() && !"".equals(c.getId())) {
			auroraComponents.put(c.getId(), ac);
		}
		if ((c instanceof BMBindComponent) && (ac instanceof Container)) {
			ac.setName(c.getName());
			fillContainer((BMBindComponent) c, (Container) ac);
		}
		if (ac instanceof TabItem) {
			((TabItem) ac).setPrompt("tabItem" + tabItemIndex++);
			tabItem.add((TabItem) ac);
		}
		if (c.getChildren() == null) {
			return ac;
		}
		for (Component c_child : c.getChildren()) {
			if ((ac instanceof TabItem) && (c_child instanceof TabRefComponent)) {
				fillTabRef((TabItem) ac, (TabRefComponent) c_child);
				continue;
			}
			AuroraComponent ac_child = createAuroraComponent(c_child);
			if ((c_child instanceof ButtonComponent) && (ac_child instanceof Button)) {
				fillButton((ButtonComponent) c_child, (Button) ac_child);
			}
			if (ac instanceof Container) {
				((Container) ac).addChild(ac_child);
			}
		}
		return ac;
	}

	private void fillQueryRelated() {
		for (String s : queryRelated.keySet()) {
			Object obj = auroraComponents.get(s);
			if (obj instanceof Button) {
				Button btn = (Button) obj;
				AuroraComponent ac = auroraComponents.get(queryRelated.get(s));
				btn.getButtonClicker().setTargetComponent(ac);
			} else if (obj instanceof Container) {
				Container c = (Container) obj;
				ResultDataSet ds = (ResultDataSet) c.getDataset();
				AuroraComponent ac = auroraComponents.get(queryRelated.get(s));
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

	private void fillTabRef(TabItem ac, TabRefComponent c) {
		TabRef ref = ac.getTabRef();
		if (ref == null) {
			ref = new TabRef();
		}
		ac.setTabRef(ref);
		String ibmId = c.getInitModel();
		BMReference bm = null;
		for (BMReference b : initBms) {
			if (!ibmId.equals(b.getId())) {
				continue;
			}
			bm = b;
			break;
		}
		if (bm == null) {
			return;
		}
		if (initModelRelated.get(bm) == null) {
			initModelRelated.put(bm, new ArrayList<TabItem>());
		}
		initModelRelated.get(bm).add(ac);
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
		for (BMReference b : bms) {
			if (!bmId.equals(b.getId())) {
				continue;
			}
			bm = b;
			break;
		}
		if (bm == null) {
			return;
		}
		ResultDataSet ds = new ResultDataSet();
		ds.setOwner(ac);
		ac.setDataset(ds);
		ac.setSectionType(Container.SECTION_TYPE_RESULT);
		if (modelRelated.get(bm) == null) {
			modelRelated.put(bm, new ArrayList<Container>());
		}
		modelRelated.get(bm).add(ac);
		String qcId = c.getQueryComponent();
		if (qcId != null && (!"".equals(qcId))) {
			queryRelated.put(c.getId(), qcId);
			queryModelRelated.put(bm, qcId);
		}
	}

	public List<BMReference> getBms() {
		return bms;
	}

	public List<BMReference> getInitBms() {
		return initBms;
	}

	public Map<String, String> getQueryRelated() {
		return queryRelated;
	}

	public Map<BMReference, List<Container>> getModelRelated() {
		return modelRelated;
	}

	public Map<BMReference, List<TabItem>> getInitModelRelated() {
		return initModelRelated;
	}

	public Map<String, AuroraComponent> getAuroraComponents() {
		return auroraComponents;
	}

	public Map<BMReference, String> getQueryModelRelated() {
		return queryModelRelated;
	}

	public List<TabItem> getTabItem() {
		return tabItem;
	}

}

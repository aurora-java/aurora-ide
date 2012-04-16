package aurora.ide.meta.gef.editors.template.parse;

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
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.project.AuroraMetaProjectNature;

public class TemplateHelper {

	private static Map<String, Template> templates = new HashMap<String, Template>();

	private static void loadTemplate() {
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

	private static List<File> getFiles(String path, final String extension) {
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

	public static boolean isMetaProject(IResource container) {
		try {
			return container.getProject().hasNature(AuroraMetaProjectNature.ID);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Map<String, java.util.List<Template>> getTemplates() {
		if (templates.size() <= 0) {
			loadTemplate();
		}
		Map<String, java.util.List<Template>> tempMap = new HashMap<String, java.util.List<Template>>();
		for (Template tm : templates.values()) {
			if (tempMap.get(tm.getCategory()) == null) {
				tempMap.put(tm.getCategory(), new ArrayList<Template>());
			}
			tempMap.get(tm.getCategory()).add(tm);
		}
		return tempMap;
	}

	public static Template getTemplates(String key) {
		if (templates.size() <= 0) {
			loadTemplate();
		}
		return templates.get(key);
	}
}

package aurora.ide.statistics.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;
import aurora.ide.api.statistics.IStatisticsManager;
import aurora.ide.api.statistics.map.StatisticsMap;
import aurora.ide.api.statistics.model.Dependency;
import aurora.ide.api.statistics.model.ProjectObject;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public class ObjectDependency implements IStatisticsManager {

	private static ObjectDependency objectDependency;

	private IFile sourceFile;
	private IProject project;
	private List<ProjectObject> poList;

	public static ObjectDependency getInstance() {
		if (objectDependency == null) {
			objectDependency = new ObjectDependency();
		}
		return objectDependency;
	}

	public List<Dependency> getDependency(ProjectObject po, List<ProjectObject> poList, StatisticsMap sm, QualifiedName qName) {
		List<Dependency> dependencys = new ArrayList<Dependency>();
		ISchemaManager schemaManager = LoadSchemaManager.getSchemaManager();
		Element element = schemaManager.getElement(sm.getMap());
		if (element == null) {
			return new ArrayList<Dependency>();
		}
		this.poList = poList;
		sourceFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(po.getPath()));
		project = sourceFile.getProject();
		for (Object o : element.getAllAttributes()) {
			Attribute attrib = (Attribute) o;
			IType attributeType = attrib.getAttributeType();
			if (!(attributeType instanceof SimpleType) || (!qName.equals(((SimpleType) attributeType).getReferenceTypeQName()))) {
				continue;
			}
			String s = CompositeMapUtil.getValueIgnoreCase(attrib, sm.getMap());
			if (s == null) {
				continue;
			}
			if (s.lastIndexOf("?") >= 0) {
				s = s.split("\\?")[0];
			}
			IFile df = null;
			if (qName.equals(IStatisticsManager.BmReference)) {
				df = getDependencyBm(s);
			} else if (qName.equals(IStatisticsManager.ScreenReference)) {
				df = getDependencyScreen(s);
			} else if (qName.equals(IStatisticsManager.UrlReference)) {
				df = getDependencyUrl(s);
			}
			IPath path = getFilePath(df);
			if (path != null) {
				ProjectObject p = findProjectObject(path.toString());
				if (p != null) {
					Dependency d = new Dependency();
					d.setDependencyObject(p);
					dependencys.add(d);
				}
			}
		}
		return dependencys;
	}

	private IPath getFilePath(IFile file) {
		if (file == null) {
			return null;
		}
		return new Path(file.getProject().getName()).append(file.getProjectRelativePath());
	}

	private ProjectObject findProjectObject(String path) {
		if (path == null) {
			return null;
		}
		for (ProjectObject p : poList) {
			if (p.getPath().equals(path)) {
				return p;
			}
		}
		return null;
	}

	private IFile getDependencyBm(String referenceValue) {
		IFile dependencyFile = ResourceUtil.getBMFile(project, referenceValue);
		if (dependencyFile != null && dependencyFile.exists()) {
			return dependencyFile;
		}
		return null;
	}

	private IFile getDependencyScreen(String referenceValue) {
		IFile dependencyFile = null;
		if (referenceValue.toLowerCase().endsWith(".screen")) {
			try {
				IPath webPath = new Path(project.getPersistentProperty(ProjectPropertyPage.WebQN));
				dependencyFile = ResourcesPlugin.getWorkspace().getRoot().getFile(webPath.append("/" + referenceValue));
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		if (dependencyFile != null && dependencyFile.exists()) {
			return dependencyFile;
		}
		return null;
	}

	private IFile getDependencyUrl(String referenceValue) {
		IFile dependencyFile = null;
		String special = "${/request/@context_path}";
		int loc = referenceValue.indexOf(special);
		if (loc >= 0) {
			referenceValue = referenceValue.substring(loc + special.length());
			int p = referenceValue.lastIndexOf(".");
			int q = referenceValue.lastIndexOf("/");
			if (p < q) {
				referenceValue = referenceValue.substring(0, q);
			}
		}
		if (referenceValue.toLowerCase().endsWith(".svc") || referenceValue.toLowerCase().endsWith(".screen")) {
			try {
				IPath webPath = new Path(project.getPersistentProperty(ProjectPropertyPage.WebQN));
				dependencyFile = ResourcesPlugin.getWorkspace().getRoot().getFile(webPath.append("/" + referenceValue));
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else {
			int q = referenceValue.lastIndexOf("/");
			if (q >= 0) {
				referenceValue = referenceValue.substring(q);
			}
			dependencyFile = ResourceUtil.getBMFile(project, referenceValue);
		}
		if (dependencyFile != null && dependencyFile.exists()) {
			return dependencyFile;
		}
		return null;
	}

	public String getValueIgnoreCase(Attribute a, CompositeMap cMap) {
		return CompositeMapUtil.getValueIgnoreCase(a, cMap);
	}
}

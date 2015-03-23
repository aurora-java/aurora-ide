package aurora.ide.statistics.viewer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import aurora.ide.api.statistics.map.ObjectStatisticsResult;
import aurora.ide.api.statistics.map.StatisticsResult;
import aurora.ide.api.statistics.model.StatisticsProject;

class ProjectNode {
	// 0
	String name;
	// 1
	String value;
	// 2
	String max;
	// 3
	String min;
	// 4
	String average;
	Object parent;
}

class ProjectViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof StatisticsProject) {
			String[] ps = StatisticsProject.PROPERTIES;
			ProjectNode[] nodes = new ProjectNode[ps.length];
			for (int i = 0; i < ps.length; i++) {
				nodes[i] = new ProjectNode();
				// TODO i18n
				nodes[i].name = ps[i];
				nodes[i].value = ((StatisticsProject) parentElement).getProperty(i);
				nodes[i].parent = parentElement;
			}
			return nodes;
		}
		if (parentElement instanceof ObjectStatisticsResult) {
			ObjectStatisticsResult osr = (ObjectStatisticsResult) parentElement;
			ProjectNode fileCount = createProjectNode("files", osr, osr.getFileCount(), osr.getFileCount(), osr.getFileCount(), osr.getFileCount());
			ProjectNode fileSize = createProjectNode("file size", osr, osr.getMaxFileSize(), osr.getMinFileSize(), osr.getTotalFileSize(), osr.getAverageFileSize());
			ProjectNode scriptSize = createProjectNode("script size", osr, osr.getMaxScriptSize(), osr.getMinScriptSize(), osr.getTotalScriptSize(), osr.getAverageScriptSize());
			ProjectNode tagCount = createProjectNode("tags", osr, osr.getMaxTagCount(), osr.getMinTagCount(), osr.getTotalTagCount(), osr.getAverageTagCount());
			return new ProjectNode[] { fileSize, scriptSize, fileCount, tagCount };
		}
		return null;
	}

	private ProjectNode createProjectNode(String nodeName, Object parent, int max, int min, int total, int average) {
		ProjectNode node = new ProjectNode();
		node.name = nodeName;
		node.parent = parent;
		node.max = toString(max);
		node.min = toString(min);
		node.value = toString(total);
		node.average = toString(average);
		return node;
	}

	private String toString(int i) {
		return String.valueOf(i);
	}

	public Object getParent(Object element) {
		if (element instanceof ProjectNode)
			return ((ProjectNode) element).parent;
		return null;
	}

	public boolean hasChildren(Object element) {
		return element instanceof StatisticsProject || element instanceof ObjectStatisticsResult;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof StatisticsResult) {
			ObjectStatisticsResult bmStatisticsResult = ((StatisticsResult) inputElement).getBMStatisticsResult();
			ObjectStatisticsResult sreenStatisticsResult = ((StatisticsResult) inputElement).getSreenStatisticsResult();
			ObjectStatisticsResult svcStatisticsResult = ((StatisticsResult) inputElement).getSVCStatisticsResult();

			StatisticsProject project = ((StatisticsResult) inputElement).getProject();
			List<Object> result = new ArrayList<Object>();
			if (project != null)
				result.add(project);
			if (bmStatisticsResult != null)
				result.add(bmStatisticsResult);
			if (sreenStatisticsResult != null)
				result.add(sreenStatisticsResult);
			if (svcStatisticsResult != null)
				result.add(svcStatisticsResult);
			return result.toArray(new Object[result.size()]);
		}
		return null;
	}

}

class ProjectViewLabelProvider implements ITableLabelProvider {

	public void addListener(ILabelProviderListener listener) {

	}

	public void dispose() {

	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {

	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof StatisticsProject && columnIndex == 0) {
			String s = ((StatisticsProject) element).getProjectName();
			if ("no project".equals(s)) {
				return "空项目";
			} else {
				return s;
			}
		}
		if (element instanceof ObjectStatisticsResult && columnIndex == 0) {
			return ((ObjectStatisticsResult) element).getType();
		}
		if (element instanceof ProjectNode) {
			ProjectNode node = (ProjectNode) element;
			switch (columnIndex) {
			case 0:
				if ("file size".equals(node.name)) {
					return "文件大小";
				} else if ("files".equals(node.name)) {
					return "文件数量";
				} else if ("script size".equals(node.name)) {
					return "脚本大小";
				} else if ("tags".equals(node.name)) {
					return "标签数量";
				} else if ("projectName".equals(node.name)) {
					return "项目名";
				} else if ("storer".equals(node.name)) {
					return "保存人";
				} else if ("storeDate".equals(node.name)) {
					return "保存时间";
				} else if ("repositoryType".equals(node.name)) {
					return "资源库类型";
				} else if ("repositoryRevesion".equals(node.name)) {
					return "资源库版本";
				} else if ("repositoryPath".equals(node.name)) {
					return "资源库路径";
				} else {
					return node.name;
				}
			case 1:
				if (node.name.indexOf("size") >= 0) {
					return conversion(node.value);
				} else if ("no project".equals(node.value)) {
					return "空项目";
				} else {
					return node.value;
				}
			case 2:
				if (node.name.indexOf("size") >= 0) {
					return conversion(node.max);
				}
				return node.max;
			case 3:
				if (node.name.indexOf("size") >= 0) {
					return conversion(node.min);
				}
				return node.min;
			case 4:
				if (node.name.indexOf("size") >= 0) {
					return conversion(node.average);
				}
				return node.average;
			}
		}
		return null;
	}

	private String conversion(String value) {
		if (value.matches("\\d+")) {
			DecimalFormat df = new DecimalFormat("0.00");
			double v = Double.parseDouble(value);
			if (value.length() > 3 && value.length() <= 6) {
				v /= 1024.0;
				return df.format(v) + " KB";
			} else if (value.length() > 6) {
				v /= (1024.0 * 1024.0);
				return df.format(v) + " MB";
			} else {
				return (int) v + " Byte";
			}
		}
		return value;
	}

}

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
import aurora.ide.api.statistics.model.ProjectObject;

class ObjectNode {
	// 0
	String category;
	// 1
	String fileName;
	// 2
	String path;
	// 3
	String fileSize;
	// 4
	String scriptSize;
	// 5
	String tagCount;
	// 6
	String refInCount;
	// 7
	String refOutCount;
	// 8
	String maxTagName;

	Object parent;
}

// static final private String[] oViewColTitles = { "类别", "文件名", "路径", "文件大小",
// "脚本大小", "标签数量", "引用次数", "被引用次数" };
class ObjectViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof ObjectStatisticsResult) {
			ObjectStatisticsResult osr = (ObjectStatisticsResult) parentElement;
			List<ObjectNode> nodes = new ArrayList<ObjectNode>();
			List<ProjectObject> objects = osr.getObjects();
			for (ProjectObject o : objects) {
				nodes.add(createObjectNode(o, osr));
			}
			return nodes.toArray(new ObjectNode[nodes.size()]);
		}
		return null;
	}

	private ObjectNode createObjectNode(ProjectObject o, ObjectStatisticsResult osr) {
		ObjectNode on = new ObjectNode();
		on.category = o.getType();
		on.fileName = o.getName();
		on.fileSize = toString(o.getFileSize());
		on.parent = osr;
		on.path = o.getPath();
		// TODO
		on.refInCount = Integer.toString(o.getDependencies().size());
		on.refOutCount = Integer.toString(o.getReferenced());
		on.scriptSize = toString(o.getScriptSize());
		on.tagCount = toString(o.getTags().size());
		return on;

	}

	private String toString(int i) {
		return String.valueOf(i);
	}

	public Object getParent(Object element) {
		if (element instanceof ObjectNode)
			return ((ObjectNode) element).parent;
		return null;
	}

	public boolean hasChildren(Object element) {
		return element instanceof ObjectStatisticsResult;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof StatisticsResult) {
			ObjectStatisticsResult bmStatisticsResult = ((StatisticsResult) inputElement).getBMStatisticsResult();
			ObjectStatisticsResult sreenStatisticsResult = ((StatisticsResult) inputElement).getSreenStatisticsResult();
			ObjectStatisticsResult svcStatisticsResult = ((StatisticsResult) inputElement).getSVCStatisticsResult();
			List<Object> result = new ArrayList<Object>();
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

class ObjectViewLabelProvider implements ITableLabelProvider {

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

		if (element instanceof ObjectStatisticsResult && columnIndex == 0) {
			return ((ObjectStatisticsResult) element).getType();
		}
		if (element instanceof ObjectNode) {
			ObjectNode node = (ObjectNode) element;
			switch (columnIndex) {
			case 0:
				return node.category;
			case 1:
				return node.fileName;
			case 2:
				return node.path;
			case 3:
				return conversion(node.fileSize);
			case 4:
				return conversion(node.scriptSize);
			case 5:
				return node.tagCount;
			case 6:
				return node.refInCount;
			case 7:
				return node.refOutCount;
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

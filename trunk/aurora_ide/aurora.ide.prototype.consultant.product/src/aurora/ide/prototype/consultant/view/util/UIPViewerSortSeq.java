package aurora.ide.prototype.consultant.view.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.consultant.view.Node;

public class UIPViewerSortSeq {

	private static final int _100 = 100;

	private static final int step = 10;

	public static final String QUICK_UI_SORT = "quick_ui.sort"; //$NON-NLS-1$

	private Node node;

	private boolean isNeedReload;

	private Map<String, Integer> map;

	private int max = _100;

	public UIPViewerSortSeq(Node node) {
		this.node = node;
		isNeedReload = true;
		load();
	}

	private void load() {
		if (isNeedReload == false) {
			return;
		}
		map = new HashMap<String, Integer>();
		CompositeMap loadProperties = loadProperties();
		isNeedReload = false;
		List<Node> children = node.getChildren();
		for (Node n : children) {
			int sortNum = getSortNum(n, loadProperties);
			map.put(n.getFile().getName(), sortNum);
		}
	}

	private int getSortNum(Node node, CompositeMap loadProperties) {
		List childsNotNull = loadProperties.getChildsNotNull();
		for (Object object : childsNotNull) {
			if (object instanceof CompositeMap) {
				if (node.getFile().getName()
						.equals(((CompositeMap) object).getText())) {
					int int1 = ((CompositeMap) object).getInt("num", -1);
					if (-1 != int1)
						return int1;
				}
			}
		}
		return getSortNum(node);
	}

	public int getSortNum(Node node) {
		load();
		Integer integer = map.get(node.getFile().getName());
		return integer == null ? max = max + step : integer;
	}

	public void changeSort(Node pre, Node next) {
		int sortNum1 = getSortNum(pre);
		int sortNum2 = getSortNum(next);
		setSortNum(pre, sortNum2);
		setSortNum(next, sortNum1);
		save();
	}

	public void setSortNum(Node node, int i) {
		if (map != null) {
			map.put(node.getFile().getName(), i);
		}
	}

	public void save() {

		CompositeMap sm = new CompositeMap("sorts");

		if (map != null) {
			Set<String> keySet = map.keySet();
			for (String s : keySet) {
				Integer integer = map.get(s);
				CompositeMap createChild = sm.createChild("sort");
				createChild.put("num", integer);
				createChild.setText(s);
			}
		}

		try {
			saveProperties(sm);
			isNeedReload = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected CompositeMap loadProperties() {
		if (node == null) {
			return new CompositeMap();
		}
		File file = node.getFile();
		CompositeMap pp = ResourceUtil.loadSortProperties(file);
		return pp == null ? new CompositeMap() : pp;
	}

	protected void saveProperties(CompositeMap map) throws IOException {
		File file = node.getFile();
		ResourceUtil.createFile(file, QUICK_UI_SORT, map);
	}

	private static final Map<Node, UIPViewerSortSeq> maps = new HashMap<Node, UIPViewerSortSeq>();

	public static UIPViewerSortSeq getUIPViewerSortSeq(Node node) {
		UIPViewerSortSeq uipViewerSortSeq = maps.get(node);
		if (uipViewerSortSeq == null) {
			uipViewerSortSeq = new UIPViewerSortSeq(node);
			maps.put(node, new UIPViewerSortSeq(node));
		}
		return uipViewerSortSeq;
	}
}

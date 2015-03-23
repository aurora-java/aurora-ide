package aurora.ide.refactor.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;

import uncertain.composite.CompositeMap;

public class ScreenCustomerIDGen {
	private Map<IFile, List<String>> idMap = new HashMap<IFile, List<String>>();

	public String createID(IFile file, CompositeMap map) {
		String bindTarget = (String) map.get("bindTarget") == null ? ""
				: (String) map.get("bindTarget");
		String name = (String) map.get("name") == null ? "" : (String) map
				.get("name");
		List<String> list = idMap.get(file);
		if (list == null) {
			list = new ArrayList<String>();
			idMap.put(file, list);
		}
		String id = name + "_" + bindTarget;
		int i = 1;
		while (list.contains(id)) {
			id = id + "_" + i;
			i++;
		}
		list.add(id);
		id = "id=\"" + id + "\"";

		return id;
	}
}

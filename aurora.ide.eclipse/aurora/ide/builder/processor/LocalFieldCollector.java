package aurora.ide.builder.processor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.schema.Attribute;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.search.cache.CacheManager;

public class LocalFieldCollector implements IterationHandle {
	/**
	 * contains all name of map,that they are local field define(not a reference
	 * to localfield)<br/>
	 * <b>e.g.</b><br/>
	 * {@code field} is a localfield define,but {@code query-field} is a
	 * reference to localfield
	 */
	public static final Set<String> localFieldDefine = new HashSet<String>() {
		{
			add("field");
			add("ref-field");
		}
	};
	private Set<String> set = new HashSet<String>();
	private CompositeMap map;

	public LocalFieldCollector(IFile file) {
		try {
			map = CacheManager.getCompositeMap(file);
			if (map.get("extend") != null)
				map = CacheManager.getWholeBMCompositeMap(file);
		} catch (Exception e) {
			AuroraBuilder.addMarker(file, e.getMessage(), 1,
					IMarker.SEVERITY_ERROR, AuroraBuilder.FATAL_ERROR);
		}
	}

	public LocalFieldCollector(CompositeMap rootMap) {
		this.map = rootMap;
	}

	public Set<String> collect() {
		if (map != null)
			map.iterate(this, true);
		Object obj = map.getObject("/features/standard-who");
		if (obj != null) {
			set.add("last_update_date");
			set.add("last_updated_by");
			set.add("creation_date");
			set.add("created_by");
		}
		return set;
	}

	public int process(CompositeMap map) {
		try {
			List<Attribute> list = SxsdUtil.getAttributesNotNull(map);
			if (list == null)
				return 0;
			for (Attribute a : list) {
				String name = a.getName();
				String value = map.getString(name);
				if (value == null)
					continue;
				if (SxsdUtil.isLocalFieldReference(a.getAttributeType())) {
					if (localFieldDefine.contains(map.getName())) {
						if (name.equalsIgnoreCase("name")) {
							set.add(value.toLowerCase());
							continue;
						}
					}
				}
			}
		} catch (Exception e) {
		}
		return 0;
	}
}
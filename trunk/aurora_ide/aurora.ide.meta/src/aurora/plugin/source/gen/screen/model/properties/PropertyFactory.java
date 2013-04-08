package aurora.plugin.source.gen.screen.model.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeLoader;
import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class PropertyFactory implements ComponentInnerProperties,
		ComponentProperties {

	public IPropertyDescriptor[] createPropertyDescriptors(
			AuroraComponent component) {
		return createPropertyDescriptors(component.getComponentType());
	}

	private IPropertyDescriptor[] createPropertyDescriptors(String componentType) {
		CompositeMap map = loadXML();
		propertyMap.clear();
		parseComponent(map);
		DefaultPropertyDescriptor[] defaultPropertyDescriptors = propertyMap
				.get(componentType.toLowerCase());
		return defaultPropertyDescriptors == null ? new IPropertyDescriptor[0]
				: defaultPropertyDescriptors;
	}

	static private void parseComponent(CompositeMap map) {
		List childsNotNull = map.getChildsNotNull();
		for (Object object : childsNotNull) {
			if (object instanceof CompositeMap) {
				String types = ((CompositeMap) object).getString("type", "");
				if ("".equals(types) == true)
					continue;
				DefaultPropertyDescriptor[] parseProperty = parseProperty((CompositeMap) object);
				String[] split = types.split(",");
				for (String type : split) {
					propertyMap.put(type.toLowerCase(), parseProperty);
				}
			}
		}
	}

	private final static Map<String, Integer> styleMap = new HashMap<String, Integer>();
	private final static Map<String, DefaultPropertyDescriptor[]> propertyMap = new HashMap<String, DefaultPropertyDescriptor[]>();

	static {
		styleMap.put("simple", IPropertyDescriptor.simple);
		styleMap.put("reference", IPropertyDescriptor.reference);
		styleMap.put("containment", IPropertyDescriptor.containment);
		styleMap.put("list", IPropertyDescriptor.list);
		styleMap.put("array", IPropertyDescriptor.array);
		styleMap.put("editable", IPropertyDescriptor.editable);
		styleMap.put("save", IPropertyDescriptor.save);
		styleMap.put("inner", IPropertyDescriptor.inner);
		styleMap.put("_boolean", IPropertyDescriptor._boolean);
		styleMap.put("_int", IPropertyDescriptor._int);
		styleMap.put("_float", IPropertyDescriptor._float);
		CompositeMap map = loadXML();
		parseComponent(map);
	}

	static private DefaultPropertyDescriptor[] parseProperty(CompositeMap map) {
		List childsNotNull = map.getChildsNotNull();
		List<DefaultPropertyDescriptor> result = new ArrayList<DefaultPropertyDescriptor>();
		for (Object o : childsNotNull) {
			if (o instanceof CompositeMap) {
				String id = ((CompositeMap) o).getString("id", "");
				String style = ((CompositeMap) o).getString("style", "");
				int s = getStyle(style);
				if ("".equals(id) == false && s != 0) {
					result.add(new DefaultPropertyDescriptor(id, s));
				}
			}
		}
		return result.toArray(new DefaultPropertyDescriptor[result.size()]);
	}

	static private int getStyle(String styles) {
		String[] split = styles.split(",");
		int i = IPropertyDescriptor.none;
		for (String s : split) {
			Integer integer = styleMap.get(s);
			if (integer != null)
				i = i | integer;
		}
		return i;
	}

	private static CompositeMap loadXML() {

		InputStream is = null;
		try {
			is = PropertyFactory.class.getResourceAsStream("components.xml");
			CompositeLoader parser = new CommentCompositeLoader();
			CompositeMap rootMap = parser.loadFromStream(is);
			return rootMap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new CompositeMap();

	}
}

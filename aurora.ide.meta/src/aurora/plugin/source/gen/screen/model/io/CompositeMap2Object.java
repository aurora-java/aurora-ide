package aurora.plugin.source.gen.screen.model.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.properties.DefaultPropertyDescriptor;
import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;
import aurora.plugin.source.gen.screen.model.properties.PropertyFactory;

public class CompositeMap2Object implements KEYS {
	private Map<String, AuroraComponent> objects = new HashMap<String, AuroraComponent>();

	public ScreenBody createScreenBody(CompositeMap rootMap) {
		AuroraComponent xml2Object = this.xml2Object(rootMap);
		if (xml2Object instanceof ScreenBody)
			return (ScreenBody) xml2Object;
		return new ScreenBody();
	}

	private class ComponentHelper {
		private AuroraComponent component;
		private CompositeMap cmap;

		ComponentHelper(AuroraComponent component, CompositeMap map) {
			this.component = component;
			this.cmap = map;
		}

		public void simple(IPropertyDescriptor pd) {
			if (pd instanceof DefaultPropertyDescriptor) {
				String propId = ((DefaultPropertyDescriptor) pd).getStringId();
				Object value = cmap.getString(propId, "");
				if ((((DefaultPropertyDescriptor) pd).getStyle() & IPropertyDescriptor._boolean) != 0) {
					value = cmap.getBoolean(propId);
				}
				if ((((DefaultPropertyDescriptor) pd).getStyle() & IPropertyDescriptor._float) != 0) {
					value = cmap.getFloat(propId);
				}
				if ((((DefaultPropertyDescriptor) pd).getStyle() & IPropertyDescriptor._int) != 0) {
					value = cmap.getInt(propId);
				}
				setPropertyValue(propId, value);
			}
		}

		private void setPropertyValue(String propId, Object value) {
			if (value == null || "".equals(value)) {
				return;
			}
			component.setPropertyValue(propId, value);
		}

		public void reference(IPropertyDescriptor pd) {
			String propId = ((DefaultPropertyDescriptor) pd).getStringId();
			if (pd instanceof DefaultPropertyDescriptor) {
				if ((((DefaultPropertyDescriptor) pd).getStyle() & IPropertyDescriptor.list) != 0) {
					this.referenceList(pd);
					return;
				}
				if ((((DefaultPropertyDescriptor) pd).getStyle() & IPropertyDescriptor.array) != 0) {
					this.referenceArray(pd);
					return;
				}
				CompositeMap child = cmap.getChildByAttrib(REFERENCE,
						PROPERTYE_ID, propId);
				if (child != null) {
					AuroraComponent value = createInstance(child);
					setPropertyValue(propId, value);
				}
			}
		}

		private void referenceArray(IPropertyDescriptor pd) {
			String propId = ((DefaultPropertyDescriptor) pd).getStringId();
			List<AuroraComponent> value = new ArrayList<AuroraComponent>();
			CompositeMap child = cmap.getChildByAttrib(REFERENCE_ARRAY,
					PROPERTYE_ID, propId);
			if (child != null) {
				List<?> childsNotNull = child.getChildsNotNull();
				for (Object m : childsNotNull) {
					if (m instanceof CompositeMap) {
						AuroraComponent c = createInstance((CompositeMap) m);
						value.add(c);
					}
				}
			}
			if (value.isEmpty() == false)
				setPropertyValue(propId,
						value.toArray(new AuroraComponent[value.size()]));
		}

		private void referenceList(IPropertyDescriptor pd) {
			String propId = ((DefaultPropertyDescriptor) pd).getStringId();
			List<AuroraComponent> value = new ArrayList<AuroraComponent>();
			CompositeMap child = cmap.getChildByAttrib(REFERENCE_LIST,
					PROPERTYE_ID, propId);
			if (child != null) {
				List<?> childsNotNull = child.getChildsNotNull();
				for (Object m : childsNotNull) {
					if (m instanceof CompositeMap) {
						AuroraComponent c = createInstance((CompositeMap) m);
						value.add(c);
					}
				}
			}
			if (value.isEmpty() == false)
				setPropertyValue(propId, value);
		}

		public void containment(IPropertyDescriptor pd) {

			if (pd instanceof DefaultPropertyDescriptor) {
				if ((((DefaultPropertyDescriptor) pd).getStyle() & IPropertyDescriptor.list) != 0) {
					this.containmentList(pd);
					return;
				}
				if ((((DefaultPropertyDescriptor) pd).getStyle() & IPropertyDescriptor.array) != 0) {
					this.containmentArray(pd);
					return;
				}
				String propId = ((DefaultPropertyDescriptor) pd).getStringId();
				CompositeMap child = cmap
						.getChildByAttrib(PROPERTYE_ID, propId);
				if (child != null) {
					AuroraComponent value = xml2Object(child);
					setPropertyValue(propId, value);
				}
			}
		}

		private void containmentArray(IPropertyDescriptor pd) {

			String propId = ((DefaultPropertyDescriptor) pd).getStringId();
			List<AuroraComponent> value = new ArrayList<AuroraComponent>();
			CompositeMap child = cmap.getChildByAttrib(CONTAINMENT_ARRAY,
					PROPERTYE_ID, propId);
			if (child != null) {
				List<?> childsNotNull = child.getChildsNotNull();
				for (Object m : childsNotNull) {
					if (m instanceof CompositeMap) {
						AuroraComponent c = xml2Object((CompositeMap) m);
						value.add(c);
					}
				}
			}
			if (value.isEmpty() == false)
				setPropertyValue(propId,
						value.toArray(new AuroraComponent[value.size()]));

		}

		private void containmentList(IPropertyDescriptor pd) {

			String propId = ((DefaultPropertyDescriptor) pd).getStringId();
			List<AuroraComponent> value = new ArrayList<AuroraComponent>();
			CompositeMap child = cmap.getChildByAttrib(CONTAINMENT_LIST,
					PROPERTYE_ID, propId);
			if (child != null) {
				List<?> childsNotNull = child.getChildsNotNull();
				for (Object m : childsNotNull) {
					if (m instanceof CompositeMap) {
						AuroraComponent c = xml2Object((CompositeMap) m);
						value.add(c);
					}
				}
			}
			if (value.isEmpty() == false)
				setPropertyValue(propId, value);

		}
	}

	private AuroraComponent xml2Object(CompositeMap map) {
		AuroraComponent createInstance = createInstance(map);
		ComponentHelper helper = new ComponentHelper(createInstance, map);
		IPropertyDescriptor[] pds = getPropertyDescriptor(createInstance);
		for (IPropertyDescriptor pd : pds) {
			if (pd instanceof DefaultPropertyDescriptor) {
				int style = ((DefaultPropertyDescriptor) pd).getStyle();
				if ((style & IPropertyDescriptor.save) == 0) {
					continue;
				}

				if ((style & IPropertyDescriptor.simple) != 0) {
					helper.simple(pd);
				}
				if ((style & IPropertyDescriptor.reference) != 0) {
					helper.reference(pd);
				}
				if ((style & IPropertyDescriptor.containment) != 0) {
					helper.containment(pd);
				}
			}
		}

		return createInstance;
	}

	private IPropertyDescriptor[] getPropertyDescriptor(
			AuroraComponent component) {
		PropertyFactory pf = new PropertyFactory();
		IPropertyDescriptor[] pds = pf.createPropertyDescriptors(component);
		return pds;
	}

	private AuroraComponent createInstance(CompositeMap map) {
		String id = map.getString(MARKID, "");
		AuroraComponent newInstance = objects.get(id);
		if (newInstance != null)
			return newInstance;
		try {
			String clazz = map.getString(CLASS_NAME, "");
			newInstance = (AuroraComponent) Class.forName(clazz).newInstance();
			String type = map.getString(COMPONENT_TYPE, "");
			newInstance.setComponentType(type);
			objects.put(id, newInstance);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return newInstance;
	}
}

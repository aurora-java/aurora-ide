package aurora.plugin.source.gen.screen.model.io;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.properties.DefaultPropertyDescriptor;
import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;
import aurora.plugin.source.gen.screen.model.properties.PropertyFactory;
import aurora.plugin.source.gen.screen.model.properties.TestPropertyFactory;

public class Object2CompositeMap implements KEYS {

	public String createXML(ScreenBody diagram) {
		CompositeMap object2xml = object2XML(diagram);
		return object2xml.toXML();
	}

	private class MapHelper {

		CompositeMap map;

		MapHelper(CompositeMap map) {
			this.map = map;
		}

		private void simple(IPropertyDescriptor pd, Object value) {
			if (value == null || "".equals(value))
				return;
			map.put("" + pd.getId(), value);
		}

		private void reference(IPropertyDescriptor pd, Object propertyValue) {
			if (pd instanceof DefaultPropertyDescriptor) {
				if ((((DefaultPropertyDescriptor) pd).getStyle() & IPropertyDescriptor.list) != 0) {
					this.referenceList(pd, propertyValue);
				}
				if ((((DefaultPropertyDescriptor) pd).getStyle() & IPropertyDescriptor.array) != 0) {
					this.referenceArray(pd, propertyValue);
				}
				if (propertyValue instanceof AuroraComponent) {
					CompositeMap refMap = refMap(pd,
							(AuroraComponent) propertyValue);
					map.addChild(refMap);
				}
			}
		}

		private CompositeMap refMap(IPropertyDescriptor pd,
				AuroraComponent propertyValue) {
			String type = ((AuroraComponent) propertyValue).getComponentType();
			CompositeMap refMap = createMap(REFERENCE, propertyValue);
			refMap.put(COMPONENT_TYPE, type);
			refMap.put(PROPERTYE_ID,
					((DefaultPropertyDescriptor) pd).getStringId());
			return refMap;
		}

		private void containment(IPropertyDescriptor pd, Object propertyValue) {
			if (pd instanceof DefaultPropertyDescriptor) {
				if ((((DefaultPropertyDescriptor) pd).getStyle() & IPropertyDescriptor.list) != 0) {
					this.containmentList(pd, propertyValue);
				}
				if ((((DefaultPropertyDescriptor) pd).getStyle() & IPropertyDescriptor.array) != 0) {
					this.containmentArray(pd, propertyValue);
				}
				if (propertyValue instanceof AuroraComponent) {
					CompositeMap cmap = object2XML((AuroraComponent) propertyValue);
					cmap.put(PROPERTYE_ID,
							((DefaultPropertyDescriptor) pd).getStringId());
					map.addChild(cmap);
				}
			}
		}

		private void referenceArray(IPropertyDescriptor pd, Object propertyValue) {
			if (propertyValue instanceof AuroraComponent[]) {
				CompositeMap arrayMap = new CompositeMap(REFERENCE_ARRAY);
				arrayMap.put(PROPERTYE_ID,
						((DefaultPropertyDescriptor) pd).getStringId());
				AuroraComponent[] acs = (AuroraComponent[]) propertyValue;
				if (acs.length == 0)
					return;
				for (AuroraComponent auroraComponent : acs) {
					arrayMap.addChild(refMap(pd, auroraComponent));
				}
				map.addChild(arrayMap);
			}
		}

		private void containmentArray(IPropertyDescriptor pd,
				Object propertyValue) {

			if (propertyValue instanceof AuroraComponent[]) {
				CompositeMap arrayMap = new CompositeMap(CONTAINMENT_ARRAY);
				arrayMap.put(PROPERTYE_ID,
						((DefaultPropertyDescriptor) pd).getStringId());
				AuroraComponent[] acs = (AuroraComponent[]) propertyValue;
				if (acs.length == 0)
					return;
				for (AuroraComponent auroraComponent : acs) {
					arrayMap.addChild(object2XML(auroraComponent));
				}
				map.addChild(arrayMap);
			}

		}

		private void referenceList(IPropertyDescriptor pd, Object propertyValue) {
			if (propertyValue instanceof List<?>) {
				CompositeMap arrayMap = new CompositeMap(REFERENCE_LIST);
				arrayMap.put(PROPERTYE_ID,
						((DefaultPropertyDescriptor) pd).getStringId());
				List<?> acs = (List<?>) propertyValue;
				if (acs.isEmpty())
					return;
				for (Object ac : acs) {
					if (ac instanceof AuroraComponent) {
						arrayMap.addChild(refMap(pd, (AuroraComponent) ac));
					}
				}
				map.addChild(arrayMap);
			}
		}

		private void containmentList(IPropertyDescriptor pd,
				Object propertyValue) {
			if (propertyValue instanceof List<?>) {
				CompositeMap arrayMap = new CompositeMap(CONTAINMENT_LIST);
				arrayMap.put(PROPERTYE_ID,
						((DefaultPropertyDescriptor) pd).getStringId());
				List<?> acs = (List<?>) propertyValue;
				if (acs.isEmpty())
					return;
				for (Object ac : acs) {
					if (ac instanceof AuroraComponent) {
						arrayMap.addChild(object2XML((AuroraComponent) ac));
					}
				}
				map.addChild(arrayMap);
			}
		}
	}

	private CompositeMap object2XML(AuroraComponent component) {

		CompositeMap map = createMap(component.getComponentType(), component);
		MapHelper helper = new MapHelper(map);
		IPropertyDescriptor[] pds = getPropertyDescriptor(component);
		for (IPropertyDescriptor pd : pds) {
			if (pd instanceof DefaultPropertyDescriptor) {
				int style = ((DefaultPropertyDescriptor) pd).getStyle();
				if ((style & IPropertyDescriptor.save) == 0) {
					continue;
				}
				Object propertyValue = component
						.getPropertyValue(((DefaultPropertyDescriptor) pd)
								.getStringId());
				if ((style & IPropertyDescriptor.simple) != 0) {
					helper.simple(pd, propertyValue);
				}
				if ((style & IPropertyDescriptor.reference) != 0) {
					helper.reference(pd, propertyValue);
				}
				if ((style & IPropertyDescriptor.containment) != 0) {
					helper.containment(pd, propertyValue);
				}
			}
		}
		return map;
	}

	private CompositeMap createMap(String name, AuroraComponent component) {
		CompositeMap compositeMap = new CompositeMap(name);
		compositeMap.put(MARKID, ((AuroraComponent) component).markid);
		compositeMap.put(CLASS_NAME, component.getClass().getCanonicalName());
		return compositeMap;
	}

	private IPropertyDescriptor[] getPropertyDescriptor(
			AuroraComponent component) {
		PropertyFactory pf = new PropertyFactory();
		IPropertyDescriptor[] pds = pf.createPropertyDescriptors(component);
		return pds;
	}

	public CompositeMap createCompositeMap(ScreenBody viewDiagram) {
		CompositeMap object2xml = this.object2XML(viewDiagram);
		return object2xml;
	}

}

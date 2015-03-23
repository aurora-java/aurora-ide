package aurora.ide.schema;

import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import aurora.ide.helpers.LoadSchemaManager;

public class SchemaTypeManager {
	private CompositeMap map;

	public SchemaTypeManager(CompositeMap map) {
		this.map = map;
	}

	public ITypeDelegate getAttributeTypeDelegate(String attr) {
		Element element = LoadSchemaManager.getSchemaManager().getElement(map);
		if(element == null)
			return null;
		List attributes = element.getAllAttributes();
		for (Object attribute : attributes) {
			if (attribute instanceof Attribute) {
				if (((Attribute) attribute).getName().equalsIgnoreCase(attr)) {
					return getDelegate((Attribute) attribute);
				}
			}

		}
		return null;
	}

	public ITypeDelegate getDelegate(Attribute attribute) {
		if (PromptsTypeDelegate.PromptsTypeName
				.equals(attribute.getTypeQName())) {
			return new PromptsTypeDelegate(map, attribute);
		}
		if(SysCodeTypeDelegate.LookUpCodeTypeName.equals(attribute.getTypeQName())){
			return new SysCodeTypeDelegate(map, attribute);
		}
		return null;
	}
}

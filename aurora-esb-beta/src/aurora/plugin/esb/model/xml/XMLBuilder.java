package aurora.plugin.esb.model.xml;

import uncertain.composite.CompositeMap;

public interface XMLBuilder {

	public String toXML();
	public Object toObject();
	public CompositeMap toCompositeMap();
}

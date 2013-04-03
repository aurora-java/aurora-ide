package aurora.ide.meta.gef.editors.template.handle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.TabItem;

public class TemplateConfig {
	private Map<BMReference, List<Container>> modelRelated;
	private Map<BMReference, List<TabItem>> initModelRelated;
	private Map<String, AuroraComponent> auroraComponents;
	private Map<BMReference, String> queryModelRelated;
	private Map<String, List<?>> elements;

	public TemplateConfig() {
		auroraComponents = new HashMap<String, AuroraComponent>();
		modelRelated = new HashMap<BMReference, List<Container>>();
		initModelRelated = new HashMap<BMReference, List<TabItem>>();
		queryModelRelated = new HashMap<BMReference, String>();
		elements = new HashMap<String, List<?>>();
	}

	public Map<BMReference, List<Container>> getModelRelated() {
		return modelRelated;
	}

	public void setModelRelated(Map<BMReference, List<Container>> modelRelated) {
		this.modelRelated = modelRelated;
	}

	public Map<BMReference, List<TabItem>> getInitModelRelated() {
		return initModelRelated;
	}

	public void setInitModelRelated(Map<BMReference, List<TabItem>> initModelRelated) {
		this.initModelRelated = initModelRelated;
	}

	public Map<String, AuroraComponent> getAuroraComponents() {
		return auroraComponents;
	}

	public void setAuroraComponents(Map<String, AuroraComponent> auroraComponents) {
		this.auroraComponents = auroraComponents;
	}

	public Map<BMReference, String> getQueryModelRelated() {
		return queryModelRelated;
	}

	public void setQueryModelRelated(Map<BMReference, String> queryModelRelated) {
		this.queryModelRelated = queryModelRelated;
	}

	public Map<String, List<?>> getElements() {
		return elements;
	}

	public void setElements(Map<String, List<?>> elements) {
		this.elements = elements;
	}

	@SuppressWarnings("unchecked")
	public List<Object> get(String key) {
		return (List<Object>) elements.get(key);
	}

	public void put(String key, List<?> value) {
		elements.put(key, value);
	}

}

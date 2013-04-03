package aurora.plugin.source.gen.screen.model;

import java.util.ArrayList;
import java.util.List;

import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

//import aurora.ide.meta.gef.editors.template.Template;

public class ScreenBody extends Container {
	public static final int DLabelWidth = 80;
	private static Class<?>[] unsupported = { Toolbar.class, Navbar.class,
			GridColumn.class, TabItem.class };

	private List<String> unBindModels = new ArrayList<String>();

	// private InitProcedure initProcedure;

	// private String bindTemplate = "";
	//
//	private String templateType;

	public static final String TYPE_UPDATE = "update";
	public static final String TYPE_DISPLAY = "display";
	public static final String TYPE_CREATE = "create";
	public static final String TYPE_SERACH = "serach";

	public ScreenBody() {
		this.setComponentType("screenBody");
	}

	@Override
	public boolean isResponsibleChild(AuroraComponent component) {
		Class<?> cls = component.getClass();
		for (Class<?> c : unsupported)
			if (c.equals(cls))
				return false;
		return super.isResponsibleChild(component);
	}

	// public InitProcedure getInitProcedure() {
	// return initProcedure;
	// }

	// public void addModelQuery(ModelQuery model) {
	// if (initProcedure == null)
	// initProcedure = new InitProcedure();
	// initProcedure.addModelQuery(model);
	// }

	public String getBindTemplate() {
		// return bindTemplate;
		return this
				.getStringPropertyValue(ComponentInnerProperties.DIAGRAM_BIND_TEMPLATE);
	}

	public void setBindTemplate(String bindTemplate) {
		// this.bindTemplate = bindTemplate;
		this.setPropertyValue(ComponentInnerProperties.DIAGRAM_BIND_TEMPLATE,
				bindTemplate);
	}

	public boolean isBindTemplate() {
		String bindTemplate = getBindTemplate();
		return bindTemplate != null && !"".equals(bindTemplate.trim());
	}

	public List<String> getModels() {
		List<String> models = new ArrayList<String>();
		List<Container> containers = getContainers(this);
		for (Container container : containers) {
			String sectionType = container.getSectionType();
			if (Container.SECTION_TYPE_QUERY.equals(sectionType)
					|| Container.SECTION_TYPE_RESULT.equals(sectionType)) {
				String model = container.getDataset().getModel();
				if (null != model) {
					models.add(model);
				}
			}
		}
		for (String m : unBindModels) {
			if (!models.contains(m)) {
				models.add(m);
			}
		}

		return models;
	}

	public List<Container> getSectionContainers(Container container,
			String[] types) {
		List<Container> containers = new ArrayList<Container>();
		List<AuroraComponent> children = container.getChildren();
		for (AuroraComponent ac : children) {
			if (ac instanceof Container) {
				String sectionType = ((Container) ac).getSectionType();
				if (contains(types, sectionType))
					containers.add((Container) ac);
				containers.addAll(getSectionContainers((Container) ac, types));
			}
			// if(ac instanceof TabItem){
			// TabBody body = ((TabItem) ac).getBody();
			// containers.addAll(getSectionContainers(body, types));
			// }
		}
		return containers;
	}

	private boolean contains(Object[] types, Object type) {
		for (Object string : types) {
			if (string.equals(type)) {
				return true;
			}
		}
		return false;
	}

	public List<Container> getContainers(Container container) {
		List<Container> containers = new ArrayList<Container>();
		List<AuroraComponent> children = container.getChildren();
		for (AuroraComponent ac : children) {
			if (ac instanceof Container) {
				containers.add((Container) ac);
				containers.addAll(getContainers((Container) ac));
			}
			// if(ac instanceof TabItem){
			// TabBody body = ((TabItem) ac).getBody();
			// containers.addAll(getContainers(body));
			// }
		}
		return containers;
	}

	public String getTemplateType() {
		// return templateType;
		return this
				.getStringPropertyValue(ComponentInnerProperties.DIAGRAM_BIND_TEMPLATE_TYPE);
	}

	public void setTemplateType(String templateType) {
		// this.templateType = templateType;
		this.setPropertyValue(
				ComponentInnerProperties.DIAGRAM_BIND_TEMPLATE_TYPE,
				templateType);
	}

	public boolean isForDisplay() {
		return TYPE_DISPLAY.equals(getTemplateType());
	}

	public boolean isForCreate() {
		return TYPE_CREATE.equals(getTemplateType());
	}

	public boolean isForUpdate() {
		return TYPE_UPDATE.equals(getTemplateType());
	}

	public boolean isForSearch() {
		return TYPE_SERACH.equals(getTemplateType());
	}

	public List<String> getUnBindModels() {
		return unBindModels;
	}

	public void addUnBindModel(String model) {
		if (model == null || "".equals(model))
			return;
		this.unBindModels.add(model);
		this.firePropertyChange("unBindModels", "", model);
	}

}

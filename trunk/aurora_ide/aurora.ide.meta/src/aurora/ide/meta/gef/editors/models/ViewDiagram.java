package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class ViewDiagram extends Container {
	private static final long serialVersionUID = -9196440587781890208L;
	public static final int DLabelWidth = 80;
	private static Class<?>[] unsupported = { Toolbar.class, Navbar.class,
			GridColumn.class, TabItem.class };

	// private List<Link> links = new ArrayList<Link>();
	private String bindTemplate = "";

	private boolean isForDisplay;
	private boolean isForCreate;
	private boolean isForUpdate;

	@Override
	public boolean isResponsibleChild(AuroraComponent component) {
		Class<?> cls = component.getClass();
		for (Class<?> c : unsupported)
			if (c.equals(cls))
				return false;
		return super.isResponsibleChild(component);
	}

	// public List<Link> getLinks() {
	// return links;
	// }
	//
	// /**
	// */
	// public void addLink(Link link) {
	// links.add(link);
	// }
	//
	// /**
	// */
	// public void removeLink(Link link) {
	// links.remove(link);
	// }

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		// IPropertyDescriptor[] pds = new IPropertyDescriptor[links.size() +
		// 1];
		// for (int i = 0; i < links.size(); i++) {
		// pds[i] = new LinkPropertyDescriptor(i, "Link");
		// }
		// pds[pds.length - 1] = new AddLinkPropertyDescriptor("add_link",
		// "ADD Link");
		// return pds;
		return new IPropertyDescriptor[0];
	}

	@Override
	public Object getPropertyValue(Object key) {
		// if (key instanceof Integer) {
		// return links.get(((Integer) key).intValue());
		// }
		// if ("add_link".equals(key))
		// return null;
		return super.getPropertyValue(key);
	}

	@Override
	public void setPropertyValue(Object key, Object val) {
		// if (key instanceof Integer && val instanceof Link) {
		// if (val instanceof DeadLink) {
		// links.remove(((Integer) key).intValue());
		// } else {
		// Link nVal = (Link) val;
		// Link link = links.get(((Integer) key).intValue());
		// link.setUrl(nVal.getUrl());
		// link.setModel(nVal.getModel());
		// link.setModelaction(nVal.getModelaction());
		// }
		// firePropertyChange("Link", null, val);
		// }
		// if ("add_link".equals(key)) {
		// links.add((Link) val);
		// firePropertyChange("Link", null, val);
		// }
		super.setPropertyValue(key, val);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getBindTemplate() {
		return bindTemplate;
	}

	public void setBindTemplate(String bindTemplate) {
		this.bindTemplate = bindTemplate;
	}

	public boolean isBindTemplate() {
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
		return models;
	}

	public List<Container> getSectionContainers(Container container) {
		List<Container> containers = new ArrayList<Container>();
		List<AuroraComponent> children = container.getChildren();
		for (AuroraComponent ac : children) {
			if (ac instanceof Container) {
				String sectionType = ((Container) ac).getSectionType();
				if (Container.SECTION_TYPE_QUERY.equals(sectionType)
						|| Container.SECTION_TYPE_RESULT.equals(sectionType)) {
					containers.add((Container) ac);
				}
				containers.addAll(getSectionContainers((Container) ac));
			}
		}
		return containers;
	}

	public List<Container> getContainers(Container container) {
		List<Container> containers = new ArrayList<Container>();
		List<AuroraComponent> children = container.getChildren();
		for (AuroraComponent ac : children) {
			if (ac instanceof Container) {
				containers.add((Container) ac);
				containers.addAll(getContainers((Container) ac));
			}
		}
		return containers;
	}

	public boolean isForDisplay() {
		return isForDisplay;
	}

	public void setForDisplay(boolean isForDisplay) {
		this.isForDisplay = isForDisplay;
	}

	public boolean isForCreate() {
		return isForCreate;
	}

	public void setForCreate(boolean isForCreate) {
		this.isForCreate = isForCreate;
	}

	public boolean isForUpdate() {
		return isForUpdate;
	}

	public void setForUpdate(boolean isForUpdate) {
		this.isForUpdate = isForUpdate;
	}

}

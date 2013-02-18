package aurora.ide.meta.extensions;

import java.util.ArrayList;
import java.util.List;

import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.models.io.DefaultIOHandler;

public class ExtensionComponent {
	private String categoryId;
	private String creator;
	private String descriptor;
	private String id;
	private String name;
	private List<String> types = new ArrayList<String>();
	private ComponentCreator cc;
	private String ioHandler;
	private DefaultIOHandler dio;

	public ExtensionComponent(String categoryId, String creator,
			String descriptor, String id, String name, String ioHandler) {
		super();
		this.categoryId = categoryId;
		this.creator = creator;
		this.descriptor = descriptor;
		this.id = id;
		this.name = name;
		this.ioHandler = ioHandler;
	}

	public ComponentCreator getCreator() {
		if (cc == null) {
			try {
				cc = (ComponentCreator) Class.forName(creator).newInstance();
			} catch (InstantiationException e) {
				DialogUtil.logErrorException(e);
			} catch (IllegalAccessException e) {
				DialogUtil.logErrorException(e);
			} catch (ClassNotFoundException e) {
				DialogUtil.logErrorException(e);
			}
		}
		return cc;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public DefaultIOHandler getIoHandler(String type) {
		if (dio == null) {
			try {
				dio = (DefaultIOHandler) Class.forName(ioHandler).newInstance();
			} catch (InstantiationException e) {
				DialogUtil.logErrorException(e);
			} catch (IllegalAccessException e) {
				DialogUtil.logErrorException(e);
			} catch (ClassNotFoundException e) {
				DialogUtil.logErrorException(e);
			}
		}
		return dio;
	}

	public void setIoHandler(String ioHandler) {
		this.ioHandler = ioHandler;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(String types) {
		if (types != null) {
			String[] split = types.split(",");
			for (String s : split) {
				this.types.add(s.trim().toLowerCase());
			}
		}
	}
}

package aurora.ide.meta.gef.editors.template;

import java.util.ArrayList;
import java.util.List;

public class Template extends Component {

	private String path;
	private String category;
	private String type = TYPE_SERACH;
	private String icon;
	private String description;
	private List<BMReference> bms = new ArrayList<BMReference>();
	private List<BMReference> initBms = new ArrayList<BMReference>();
	private List<Component> link = new ArrayList<Component>();
	private List<Component> ref = new ArrayList<Component>();

	public static final String TYPE_UPDATE = "update";
	public static final String TYPE_DISPLAY = "display";
	public static final String TYPE_CREATE = "create";
	public static final String TYPE_SERACH = "serach";
	private static String[] types = { TYPE_UPDATE, TYPE_DISPLAY, TYPE_CREATE, TYPE_SERACH };

	public Template() {

	}

	public List<BMReference> getInitBms() {
		return initBms;
	}

	public void setInitBms(List<BMReference> initBms) {
		this.initBms = initBms;
	}

	public void addModel(BMReference bm) {
		bms.add(bm);
	}

	public void addInitModel(BMReference bm) {
		initBms.add(bm);
	}

	public void addLink(Component c) {
		link.add(c);
	}

	public void addRef(Component c) {
		ref.add(c);
	}

	public List<BMReference> getBms() {
		return bms;
	}

	public void setBms(List<BMReference> bms) {
		this.bms = bms;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		for (String s : types) {
			if (s.equals(type)) {
				this.type = type;
				break;
			} else {
				this.type = TYPE_SERACH;
			}
		}

	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<Component> getLink() {
		return link;
	}

	public void setLink(List<Component> link) {
		this.link = link;
	}

	public List<Component> getRef() {
		return ref;
	}

	public void setRef(List<Component> ref) {
		this.ref = ref;
	}

}

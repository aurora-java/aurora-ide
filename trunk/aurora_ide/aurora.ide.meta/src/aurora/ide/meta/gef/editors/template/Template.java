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
	private List<BMReference> linkBms = new ArrayList<BMReference>();
//	private List<Component> grid = new ArrayList<Component>();
	private List<LinkComponent> link = new ArrayList<LinkComponent>();

	public static final String TYPE_UPDATE = "update";
	public static final String TYPE_DISPLAY = "display";
	public static final String TYPE_CREATE = "create";
	public static final String TYPE_SERACH = "serach";
	private static String[] types = { TYPE_UPDATE, TYPE_DISPLAY, TYPE_CREATE, TYPE_SERACH };

	public Template() {

	}

	public List<BMReference> getLinkBms() {
		return linkBms;
	}

	public void addLinkModel(BMReference linkBm) {
		this.linkBms.add(linkBm);
	}

	// public List<Component> getGrid() {
	// return grid;
	// }
	//
	// public void AddGrid(Component grid) {
	// this.grid.add(grid);
	// }

	public List<LinkComponent> getLink() {
		return link;
	}

	public void addLink(LinkComponent link) {
		this.link.add(link);
	}

	public void addModel(BMReference bm) {
		bms.add(bm);
	}

	public List<BMReference> getBms() {
		return bms;
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

	public void clear() {
		List<BMReference> allBms = new ArrayList<BMReference>();
		allBms.addAll(bms);
		allBms.addAll(linkBms);
		for (BMReference b : allBms) {
			b.setModel(null);
		}
	}
}

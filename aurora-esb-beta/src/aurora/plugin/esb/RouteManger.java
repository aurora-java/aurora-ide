package aurora.plugin.esb;

public class RouteManger {

	private int id_idx = 0;

	public String genID() {
		id_idx++;
		return "route" + id_idx;
	}
}

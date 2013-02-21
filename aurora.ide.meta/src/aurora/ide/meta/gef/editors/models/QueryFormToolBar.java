package aurora.ide.meta.gef.editors.models;

public class QueryFormToolBar extends HBox {
	private static final long serialVersionUID = 7471098302217318595L;

	private HBox hBox = new HBox();
	private Button btnQuery = new Button();
	private Button btnMore = new Button();

	public QueryFormToolBar() {
		super();
		setType("formToolbar");
		addChild(hBox);
		btnQuery.setText("HAP.QUERY");
		btnMore.setText("HAP.MORE");
		addChild(btnQuery);
		addChild(btnMore);
	}

}
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
		btnQuery.setText("查询");
		btnMore.setText("更多");
		addChild(btnQuery);
		// addChild(btnMore);
	}

	public HBox getHBox() {
		return hBox;
	}

	@Override
	public void setDataset(Dataset ds) {
		if (hBox != null)
			hBox.setDataset(ds);
	}

	public void setHasMore(boolean more) {
		if (more && !getChildren().contains(btnMore))
			addChild(btnMore);
		if (!more && getChildren().contains(btnMore))
			removeChild(btnMore);
	}

	public boolean hasMore() {
		return getChildren().contains(btnMore);
	}
}
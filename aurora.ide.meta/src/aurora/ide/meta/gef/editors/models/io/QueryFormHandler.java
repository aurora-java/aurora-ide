package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.ContainerHolder;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.QueryForm;
import aurora.ide.meta.gef.editors.models.QueryFormBody;
import aurora.ide.meta.gef.editors.models.QueryFormToolBar;

public class QueryFormHandler extends DefaultIOHandler {

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new QueryForm();
	}

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeSimpleAttribute(map, ac);
		QueryForm qf = (QueryForm) ac;
		map.put(QueryForm.DEFAULT_QUERY_FIELD_KEY, qf.getDefaultQueryField());
		map.put(QueryForm.DEFAULT_QUERY_HINT_KEY, qf.getDefaultQueryHint());
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		QueryForm qf = (QueryForm) ac;
		qf.setDefaultQueryField(map
				.getString(QueryForm.DEFAULT_QUERY_FIELD_KEY));
		qf.setDefaultQueryHint(map.getString(QueryForm.DEFAULT_QUERY_HINT_KEY));
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeComplexAttribute(map, ac);
		QueryForm qf = (QueryForm) ac;
		Dataset ds = qf.getDataset();
		if (ds != null) {
			DataSetHandler dsh = new DataSetHandler();
			CompositeMap dsMap = dsh.toCompositeMap(ds, mic);
			map.addChild(dsMap);
		}
		ContainerHolder holder = qf.getResultTargetContainer();
		if (holder != null) {
			CompositeMap holderMap = new ContainerHolderHandler()
					.toCompositeMap(holder, mic);
			holderMap.setName("ResultTarget");
			map.addChild(holderMap);
		}
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreComplexAttribute(ac, map);
		QueryForm qf = (QueryForm) ac;
		CompositeMap dsMap = map.getChild(Dataset.class.getSimpleName());
		if (dsMap != null) {
			Dataset ds = (Dataset) new DataSetHandler().fromCompositeMap(dsMap,
					mic);
			qf.setDataset(ds);
		}
		CompositeMap holderMap = map.getChild("ResultTarget");
		if (holderMap != null) {
			ContainerHolder qh = (ContainerHolder) new ContainerHolderHandler()
					.fromCompositeMap(holderMap, mic);
			qf.setResultTargetContainer(qh);
		}
	}

	@Override
	protected void storeChildren(CompositeMap map, Container container) {
		// super.storeChildren(map, container);
		QueryForm qf = (QueryForm) container;
		BoxHandler bh = new BoxHandler();
		if (qf.getToolBar().getHBox().getChildren().size() > 0) {
			CompositeMap toolBarMap = bh.toCompositeMap(qf.getToolBar()
					.getHBox(), mic);
			toolBarMap.removeChild(toolBarMap.getChild(Dataset.class
					.getSimpleName()));
			toolBarMap.setName(QueryFormToolBar.class.getSimpleName());
			toolBarMap.put(COMPONENT_TYPE, qf.getToolBar().getType());// override
			map.addChild(toolBarMap);
		}
		if (qf.getBody() != null) {
			CompositeMap bodyMap = bh.toCompositeMap(qf.getBody(), mic);
			bodyMap.removeChild(bodyMap.getChild(Dataset.class.getSimpleName()));
			bodyMap.setName(QueryFormBody.class.getSimpleName());
			map.addChild(bodyMap);
		}
	}

	@Override
	protected void restoreChildren(Container container, CompositeMap map) {
		// super.restoreChildren(container, map);
		QueryForm qf = (QueryForm) container;
		CompositeMap toolBarMap = map.getChild(QueryFormToolBar.class
				.getSimpleName());
		BoxHandler bh = new BoxHandler();
		if (toolBarMap != null) {
			toolBarMap.setName("HBox");
			HBox box = (HBox) bh.fromCompositeMap(toolBarMap, mic);
			box.setType(HBox.H_BOX);
			qf.getToolBar().getHBox().getChildren().addAll(box.getChildren());
		}
		CompositeMap bodyMap = map
				.getChild(QueryFormBody.class.getSimpleName());
		if (bodyMap != null) {
			bodyMap.setName("Form");
			Form box = (Form) bh.fromCompositeMap(bodyMap, mic);
			QueryFormBody body = new QueryFormBody();
			body.setLabelWidth(box.getLabelWidth());
			body.setCol(box.getCol());
			body.getChildren().addAll(box.getChildren());
			qf.addChild(body);
		}
	}

	@Override
	protected boolean isStoreable(AuroraComponent ac) {
		return super.isStoreable(ac);
	}
}

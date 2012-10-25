package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.FootRenderer;

public class FootRendererHandler extends DefaultIOHandler {
	public static final String RENDERER_TYPE = "renderertype";
	public static final String FUNCTION = "function";

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeSimpleAttribute(map, ac);
		FootRenderer renderer = (FootRenderer) ac;
		map.put(RENDERER_TYPE, renderer.getRendererType());
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeComplexAttribute(map, ac);
		FootRenderer renderer = (FootRenderer) ac;
		CompositeMap fMap = new CommentCompositeMap(FUNCTION);
		fMap.setText(renderer.getFunction());
		map.addChild(fMap);
	}

	@Override
	protected FootRenderer getNewObject(CompositeMap map) {
		return new FootRenderer();
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		FootRenderer renderer = (FootRenderer) ac;
		renderer.setRendererType(map.getString(RENDERER_TYPE));
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreComplexAttribute(ac, map);
		FootRenderer renderer = (FootRenderer) ac;
		CompositeMap fMap = map.getChild(FUNCTION);
		if (fMap != null) {
			renderer.setFunction(fMap.getText());
		}
	}
}

package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Renderer;

public class RendererHandler extends DefaultIOHandler {
	public static final String RENDERER_TYPE = "renderertype";
	public static final String DISPLAY_TEXT = "displaytext";
	public static final String OPEN_PATH = "openpath";
	public static final String FUNCTION_NAME = "functionname";
	public static final String FUNCTION = "function";

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeSimpleAttribute(map, ac);
		Renderer renderer = (Renderer) ac;
		map.put(RENDERER_TYPE, renderer.getRendererType());
		map.put(DISPLAY_TEXT, renderer.getLabelText());
		map.put(OPEN_PATH, renderer.getOpenPath());
		map.put(FUNCTION_NAME, renderer.getFunctionName());
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeComplexAttribute(map, ac);
		Renderer renderer = (Renderer) ac;
		CompositeMap fMap = new CompositeMap(FUNCTION);
		fMap.setText(renderer.getFunction());
		map.addChild(fMap);
	}

	@Override
	protected Renderer getNewObject(CompositeMap map) {
		return new Renderer();
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		Renderer renderer = (Renderer) ac;
		renderer.setRendererType(map.getString(RENDERER_TYPE));
		renderer.setLabelText(map.getString(DISPLAY_TEXT));
		renderer.setOpenPath(map.getString(OPEN_PATH));
		renderer.setFunctionName(map.getString(FUNCTION_NAME));
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreComplexAttribute(ac, map);
		Renderer renderer = (Renderer) ac;
		CompositeMap fMap = map.getChild(FUNCTION);
		if (fMap != null) {
			renderer.setFunction(fMap.getText());
		}
	}

}

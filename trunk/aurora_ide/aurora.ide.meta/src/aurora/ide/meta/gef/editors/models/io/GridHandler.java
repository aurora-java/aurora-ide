package aurora.ide.meta.gef.editors.models.io;

import org.eclipse.draw2d.geometry.Dimension;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridSelectionCol;
import aurora.ide.meta.gef.editors.models.Navbar;
import aurora.ide.meta.gef.editors.models.Toolbar;

public class GridHandler extends ContainerHandler {

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeSimpleAttribute(map, ac);
		Grid g = (Grid) ac;
		map.put(Grid.NAVBAR_TYPE, g.getNavBarType());
		map.put(Grid.WIDTH, g.getSize().width);
		map.put(Grid.HEIGHT, g.getSize().height);
	}

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new Grid();
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		Grid g = (Grid) ac;
		g.setNavbarType(map.getString(Grid.NAVBAR_TYPE));
		Integer w = map.getInt(Grid.WIDTH);
		Integer h = map.getInt(Grid.HEIGHT);
		if (w != null && h != null) {
			g.setSize(new Dimension(w, h));
		}
	}

	@Override
	protected boolean isStoreable(AuroraComponent ac) {
		if (ac instanceof GridSelectionCol)
			return false;
		if (ac instanceof Navbar)
			return false;
		if (ac instanceof Toolbar)
			return false;
		return true;
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeComplexAttribute(map, ac);
		Grid g = (Grid) ac;
		if (!g.hasToolbar())
			return;
		Toolbar toolbar = g.getToolbar();
		if (toolbar != null) {
			CompositeMap tbMap = new DefaultIOHandler() {

				@Override
				protected AuroraComponent getNewObject(CompositeMap map) {
					return new Toolbar();
				}
			}.toCompositeMap(toolbar, mic);
			map.addChild(tbMap);
		}
		// List<Button> btns = g.getToobarButtons();
		//
		// CompositeMap tbMap = new CommentCompositeMap(
		// Toolbar.class.getSimpleName());
		// for (Button b : btns) {
		// tbMap.addChild(new ButtonHandler().toCompositeMap(b, mic));
		// }
		// map.addChild(tbMap);
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreComplexAttribute(ac, map);
		Grid g = (Grid) ac;
		CompositeMap tbMap = map.getChild(Toolbar.class.getSimpleName());
		if (tbMap == null)
			return;
		Toolbar tb = (Toolbar) new DefaultIOHandler() {

			@Override
			protected AuroraComponent getNewObject(CompositeMap map) {
				return new Toolbar();
			}
		}.fromCompositeMap(tbMap, mic);
		// Toolbar tb = new Toolbar();
		// @SuppressWarnings("unchecked")
		// List<CompositeMap> list = tbMap.getChildsNotNull();
		// for (CompositeMap m : list) {
		// tb.addChild(new ButtonHandler().fromCompositeMap(m, mic));
		// }
		g.addChild(tb);
	}
}

package aurora.ide.meta.gef.editors.models.io;

import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;

import uncertain.composite.CommentCompositeMap;
import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridSelectionCol;
import aurora.ide.meta.gef.editors.models.Navbar;
import aurora.ide.meta.gef.editors.models.Toolbar;

public class GridHandler extends ContainerHandler {

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
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
		List<Button> btns = g.getToobarButtons();
		CompositeMap tbMap = new CommentCompositeMap(
				Toolbar.class.getSimpleName());
		for (Button b : btns) {
			tbMap.addChild(new ButtonHandler().toCompositeMap(b, mic));
		}
		map.addChild(tbMap);
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreComplexAttribute(ac, map);
		Grid g = (Grid) ac;
		CompositeMap tbMap = map.getChild(Toolbar.class.getSimpleName());
		if (tbMap == null)
			return;
		Toolbar tb = new Toolbar();
		@SuppressWarnings("unchecked")
		List<CompositeMap> list = tbMap.getChildsNotNull();
		for (CompositeMap m : list) {
			tb.addChild(new ButtonHandler().fromCompositeMap(m, mic));
		}
		g.addChild(tb);
	}
}

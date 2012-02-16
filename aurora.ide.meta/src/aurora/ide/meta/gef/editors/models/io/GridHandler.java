package aurora.ide.meta.gef.editors.models.io;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridSelectionCol;
import aurora.ide.meta.gef.editors.models.Navbar;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.Toolbar;

public class GridHandler extends DefaultIOHandler {

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		Grid g = (Grid) ac;
		map.put(Grid.NAVBAR_TYPE, g.getNavBarType());
	}

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new Grid();
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		Grid g = (Grid) ac;
		g.setNavbarType(map.getString(Grid.NAVBAR_TYPE));
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
		Grid g = (Grid) ac;
		scaDataset(map, g);
		if (!g.hasToolbar())
			return;
		List<Button> btns = g.getToobarButtons();
		CompositeMap tbMap = new CompositeMap(Toolbar.class.getSimpleName());
		for (Button b : btns) {
			tbMap.addChild(new ButtonHandler().toCompositeMap(b, mic));
		}
		map.addChild(tbMap);
	}

	private void scaDataset(CompositeMap map, Grid grid) {
		ResultDataSet rds = grid.getDataset();
		CompositeMap dsMap = new ResultDataSetHandler()
				.toCompositeMap(rds, mic);
		map.addChild(dsMap);
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		Grid g = (Grid) ac;
		rcaDataset(g, map);
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

	private void rcaDataset(Grid grid, CompositeMap map) {
		CompositeMap dsMap = map.getChild(ResultDataSet.class.getSimpleName());
		if (dsMap == null)
			return;
		ResultDataSetHandler rdsh = new ResultDataSetHandler();
		ResultDataSet rds = (ResultDataSet) rdsh.fromCompositeMap(dsMap, mic);
		grid.setDataset(rds);
	}
}

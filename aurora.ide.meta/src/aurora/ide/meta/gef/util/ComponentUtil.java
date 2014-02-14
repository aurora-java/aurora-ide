package aurora.ide.meta.gef.util;

import java.util.ArrayList;
import java.util.List;

import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.HBox;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.VBox;

public class ComponentUtil {

	static public Grid findParentGrid(AuroraComponent ac) {
		if (ac != null && GridColumn.GRIDCOLUMN.equals(ac.getComponentType())) {
			Container parent = ac.getParent();
			if (parent instanceof Grid) {
				return (Grid) parent;
			} else {
				return findParentGrid(parent);
			}
		}
		return null;
	}
	
	static public List<GridColumn> getGridColumns(GridColumn grid){
		List<GridColumn> r = new ArrayList<GridColumn>();
		List<AuroraComponent> children = grid.getChildren();
		for (AuroraComponent a : children) {
			if(GridColumn.GRIDCOLUMN.equals(a.getComponentType())){
				GridColumn gc = (GridColumn) a;
				r.add(gc);
				if(gc.getChildren().size()>0){
					r.addAll(getGridColumns(gc));
				}
			}
		}
		return r;
	}

	static public Container getNotHVBoxParent(AuroraComponent ac){
		if(ac ==null || ac.getParent() == null){
			return null;
		}
		Container parent = ac.getParent();
		String componentType = parent.getComponentType();
		if(HBox.H_BOX.equals(componentType) || VBox.V_BOX.equals(componentType)){
			return getNotHVBoxParent(parent);
		}
		return parent;
	}
	
	static public List<AuroraComponent> getAllInputChildren(Container container){
		List<AuroraComponent> r = new ArrayList<AuroraComponent>();
		List<AuroraComponent> children = container.getChildren();
		for (AuroraComponent a : children) {
			if(a instanceof Container){
				r.addAll(getAllInputChildren((Container) a));
			}else{
				r.add(a);
			}
		}
		return r;
	}
	
	
	static public ScreenBody findScreenBody(AuroraComponent ac) {
		if (ac != null) {
			Container parent = ac.getParent();
			if (parent instanceof ScreenBody) {
				return (ScreenBody) parent;
			} else {
				findScreenBody(parent);
			}
		}
		return null;
	}
}

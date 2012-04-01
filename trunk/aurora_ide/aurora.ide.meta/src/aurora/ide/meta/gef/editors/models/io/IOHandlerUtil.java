package aurora.ide.meta.gef.editors.models.io;

import java.util.HashMap;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.Label;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.VBox;

public class IOHandlerUtil {
	private static final HashMap<Class<? extends AuroraComponent>, Class<? extends IOHandler>> handlerMapper = new HashMap<Class<? extends AuroraComponent>, Class<? extends IOHandler>>();

	static {
		handlerMapper.put(Form.class, BoxHandler.class);
		handlerMapper.put(FieldSet.class, BoxHandler.class);
		handlerMapper.put(HBox.class, BoxHandler.class);
		handlerMapper.put(VBox.class, BoxHandler.class);
		handlerMapper.put(Input.class, InputHandler.class);
		handlerMapper.put(CheckBox.class, InputHandler.class);
		handlerMapper.put(TabFolder.class, TabHandler.class);
		handlerMapper.put(TabItem.class, TabItemHandler.class);
		handlerMapper.put(Grid.class, GridHandler.class);
		handlerMapper.put(Button.class, ButtonHandler.class);
		handlerMapper.put(GridColumn.class, GridColumnHandler.class);
		handlerMapper.put(Label.class, LabelHandler.class);
	}

	public static IOHandler getHandler(AuroraComponent ac) {
		try {
			Class<? extends IOHandler> ioh = handlerMapper.get(ac.getClass());
			if (ioh != null)
				return ioh.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return new ErrorIOHandler();
	}

	public static IOHandler getHandler(CompositeMap map) {
		for (Class<? extends AuroraComponent> clz : handlerMapper.keySet()) {
			if (clz.getSimpleName().equalsIgnoreCase(map.getName())) {
				try {
					return handlerMapper.get(clz).newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return new ErrorIOHandler();
	}
}

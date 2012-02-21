package aurora.ide.meta.gef.editors.models.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.commands.Command;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.search.cache.CacheManager;

public class DropBMCommand extends Command {
	private ViewDiagram diagram;

	// private Dataset rds;
	// private QueryDataSet qds;

	public ViewDiagram getDiagram() {
		return diagram;
	}

	public void setDiagram(ViewDiagram diagram) {
		this.diagram = diagram;
	}

	



	protected String getPrompt(CompositeMap field) {
		return field != null ? field.getString("prompt") : "prompt:";
	}

	protected String getType(CompositeMap field) {
		//TODO default editor
		if (field == null) {
			return Input.TEXT;
		}
		if ("java.lang.Long".equals(field.getString("datatype"))) {
			return Input.NUMBER;
		}
		if ("java.lang.String".equals(field.getString("datatype"))) {
			return Input.TEXT;
		}
		if ("java.util.Date".equals(field.getString("datatype"))) {
			return Input.CAL;
		}
		return Input.TEXT;
	}

	protected CompositeMap getField(String name, List<CompositeMap> fs) {
		for (CompositeMap f : fs) {
			if (name.equals(f.getString("name"))) {
				return f;
			}
		}
		return null;
	}

	public void redo() {
		this.execute();
	}

	public void undo() {
		// TODO
	}

}

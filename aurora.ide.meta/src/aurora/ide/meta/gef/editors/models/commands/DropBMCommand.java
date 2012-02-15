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
	private IFile bm;

	// private Dataset rds;
	// private QueryDataSet qds;

	public ViewDiagram getDiagram() {
		return diagram;
	}

	public void setDiagram(ViewDiagram diagram) {
		this.diagram = diagram;
	}

	public IFile getBm() {
		return bm;
	}

	public void setBm(IFile bm) {
		this.bm = bm;
	}

	protected void fillGrid(Grid grid) {
		Toolbar tb = new Toolbar();
		Button b = new Button();
		b.setButtonType(Button.ADD);
		tb.addChild(b);
		b = new Button();
		b.setButtonType(Button.SAVE);
		tb.addChild(b);
		b = new Button();
		b.setButtonType(Button.DELETE);
		tb.addChild(b);

		b = new Button();
		b.setButtonType(Button.CLEAR);
		tb.addChild(b);

		b = new Button();
		b.setButtonType(Button.EXCEL);
		tb.addChild(b);

		grid.addChild(tb);
		grid.setNavbarType(Grid.NAVBAR_COMPLEX);
		try {
			List<CompositeMap> fields = this.getFields();
			for (CompositeMap f : fields) {
				String string = this.getPrompt(f);
				GridColumn gc = new GridColumn();
				gc.setPrompt(string);
				String name = f.getString("name");
				name = name == null ? "" : name;
				gc.setName(name);
				grid.addChild(gc);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}

	protected List<CompositeMap> getFields() throws CoreException,
			ApplicationException {
		CompositeMap model = CacheManager.getCompositeMap(bm);
		List<CompositeMap> fs = new ArrayList<CompositeMap>();
		CompositeMap fields = model.getChild("fields");
		if (fields != null) {
			Iterator childIterator = fields.getChildIterator();
			while (childIterator != null && childIterator.hasNext()) {
				CompositeMap qf = (CompositeMap) childIterator.next();
				if ("field".equals(qf.getName())) {
					fs.add(qf);
				}
			}
		}
		return fs;
	}

	protected void fillForm(BOX form) {
		try {
			CompositeMap model = CacheManager.getCompositeMap(bm);
			List<CompositeMap> qfs = new ArrayList<CompositeMap>();
			List<CompositeMap> fs = getFields();
			CompositeMap child = model.getChild("query-fields");
			if (child != null) {
				Iterator childIterator = child.getChildIterator();
				while (childIterator != null && childIterator.hasNext()) {
					CompositeMap qf = (CompositeMap) childIterator.next();
					if ("query-field".equals(qf.getName())) {
						qfs.add(qf);
					}
				}
			}
			for (CompositeMap qf : qfs) {
				String name = (String) qf.get("field");
				name = name == null ? qf.getString("name") : name;
				name = name == null ? "" : name;
				Input input = new Input();
				input.setName(name);
				CompositeMap field = this.getField(name, fs);
				if (field == null) {
					// TODO model extend???
					// 或者根本不存在。需要页面定义。属性是name
					System.out.println();
					field = this.getField(name, fs);
				}
				input.setPrompt(getPrompt(field));
				input.setType(getType(field));
				form.addChild(input);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}

	protected String getPrompt(CompositeMap field) {
		return field != null ? field.getString("prompt") : "prompt:";
	}

	protected String getType(CompositeMap field) {
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

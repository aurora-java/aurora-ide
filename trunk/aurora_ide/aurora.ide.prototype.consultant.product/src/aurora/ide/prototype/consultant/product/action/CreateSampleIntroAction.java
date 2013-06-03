package aurora.ide.prototype.consultant.product.action;

import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

import aurora.ide.editor.editorInput.PathEditorInput;
import aurora.ide.meta.gef.editors.ConsultantVScreenEditor;
import aurora.ide.prototype.consultant.product.Activator;
import aurora.ide.prototype.consultant.product.ICommandIds;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.Combox;
import aurora.plugin.source.gen.screen.model.Dataset;
import aurora.plugin.source.gen.screen.model.Form;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.TabFolder;
import aurora.plugin.source.gen.screen.model.TabItem;
import aurora.plugin.source.gen.screen.model.TextField;
import aurora.plugin.source.gen.screen.model.Toolbar;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class CreateSampleIntroAction implements IIntroAction {

	@Override
	public void run(IIntroSite site, Properties params) {
		Object object = params.get("type");
		ScreenBody sb = new ScreenBody();
		if ("form".equals(object)||"form_grid".equals(object)) {
			Form form = createForm();
			sb.addChild(form);
		}
		if ("grid".equals(object)||"form_grid".equals(object)) {
			Grid grid = createGrid();
			sb.addChild(grid);
		}
		if ("tabs".equals(object)) {
//			AuroraModelFactory.createComponent("");
			TabFolder tf = createTabs();
			sb.addChild(tf);
		}
		if ("grid_grid".equals(object)) {
			sb.addChild(createGrid());
			sb.addChild(createGrid());
		}
		IWorkbenchWindow window = site.getWorkbenchWindow();
		try {
			PathEditorInput ei = new PathEditorInput(
					PathEditorInput.UNTITLED_PATH);
			IEditorPart openEditor = window.getActivePage().openEditor(ei,
					ICommandIds.EDITOR_ID, true);
			if(openEditor instanceof ConsultantVScreenEditor){
				((ConsultantVScreenEditor) openEditor).setDiagram(sb);
			}
		} catch (PartInitException e) {
			MessageDialog.openError(window.getShell(), "Error",
					"Error Create File:" + e.getMessage());
		}
		Activator.setIntroStandby(true);
//		.closeIntro();
	}

	public TabFolder createTabs() {
		TabFolder tf = new TabFolder();
		TabItem child = new TabItem();
		child.addChild(createForm());
		tf.addChild(child);
		Grid grid = createGrid();
		child = new TabItem();
		child.addChild(grid);
		tf.addChild(child);
		return tf;
	}

	public Grid createGrid() {
		Grid grid = new Grid();
		grid.addCol(new GridColumn());
		grid.addCol(new GridColumn());
		Toolbar tl = new Toolbar();
		Button child = new Button();
		child.setButtonType(Button.ADD);
		tl.addChild(child);
		child = new Button();
		child.setButtonType(Button.DELETE);
		tl.addChild(child);
		grid.setPropertyValue(ComponentInnerProperties.TOOLBAR, tl);
		grid.setPropertyValue(ComponentProperties.navBarType, Grid.NAVBAR_COMPLEX);
		grid.getDataset().setPropertyValue(ComponentProperties.selectionModel, Dataset.SELECT_MULTI);
		return grid;
	}

	public Form createForm() {
		Form form = new Form();
		form.addChild(new TextField());
		form.addChild(new Combox());
		return form;
	}

}

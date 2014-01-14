package aurora.ide.prototype.consultant.product.demonstrate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.meta.gef.control.ConsultantDemonstratingComposite;
import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.parts.ExtAuroraPartFactory;
import aurora.ide.meta.gef.editors.wizard.dialog.DemonstratingDialog;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.property.page.ProjectDemonstratePropertyPage;
import aurora.ide.prototype.consultant.view.property.page.ProjectDemonstratePropertyPage.F;
import aurora.ide.swt.util.PageModel;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.io.CompositeMap2Object;

public class DemonstratingMainPageDialog extends DemonstratingDialog {

	private DemonstratingDialog demonstratingDialog;
	private Shell parentShell;
	private ConsultantDemonstratingComposite vsEditor;
	private PageModel model;

	public DemonstratingMainPageDialog(Shell parentShell) {
		super(parentShell, null);
		this.parentShell = parentShell;
	}

	protected Point getInitialSize() {
		Rectangle bounds = Display.getCurrent().getBounds();
		return new Point(bounds.width, bounds.height);
	}

	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		return super.createButton(parent, id, label, defaultButton);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return super.createButtonBar(parent);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// super.createButtonsForButtonBar(parent);
	}

	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}

	protected Control createDialogArea(Composite parent) {
		loadModel();
		Composite container = (Composite) innerCreateDialogArea(parent);
		container.setLayout(new GridLayout());
		ToolBar textToolBar = new ToolBar (container, SWT.HORIZONTAL|SWT.FLAT|SWT.WRAP|SWT.BORDER);
		textToolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createFunctionMenu(textToolBar);
		
		vsEditor = new ConsultantDemonstratingComposite(
				this);
		vsEditor.setInput(loadXML( getWelcomUip()));
		vsEditor.createPartControl(container);
		vsEditor.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		vsEditor.setFocus();
		return container;
	}

	private void loadModel() {
		ProjectDemonstratePropertyPage ppd = new ProjectDemonstratePropertyPage();
		ppd.setElement(new Node(this.getProject()));
		this.model = ppd.getModel();
	}
	private File getWelcomUip(){
		String pp = model.getStringPropertyValue(ProjectDemonstratePropertyPage.WELCOME_UIP);
		IPath afp = new Path(this.getProject().getPath());
		IPath p = new Path(pp);
		File file = afp.append(p).toFile();
		return file;
	}

	protected void createFunctionMenu(ToolBar textToolBar) {
		
		List<ProjectDemonstratePropertyPage.F> functions = (List<ProjectDemonstratePropertyPage.F> )model.getPropertyValue(ProjectDemonstratePropertyPage.FUNCTIONS);
		if(functions == null)
			return;
		for (F f : functions) {
//			f.functionPath
//			f.functions
		}
		ToolItem item = new ToolItem (textToolBar, SWT.DROP_DOWN);
		item.setText ("Drop_Down");
		item.setToolTipText("SWT.DROP_DOWN");
		item.addSelectionListener(new DropDownSelectionListener());
		item = new ToolItem (textToolBar, SWT.DROP_DOWN);
		item.setText ("Drop_Down");
		item.setToolTipText("SWT.DROP_DOWN");
		item.addSelectionListener(new DropDownSelectionListener());
		item = new ToolItem (textToolBar, SWT.DROP_DOWN);
		item.setText ("Drop_Down");
		item.setToolTipText("SWT.DROP_DOWN");
		item.addSelectionListener(new DropDownSelectionListener());
		item = new ToolItem (textToolBar, SWT.DROP_DOWN);
		item.setText ("Drop_Down");
		item.setToolTipText("SWT.DROP_DOWN");
		item.addSelectionListener(new DropDownSelectionListener());
		item = new ToolItem (textToolBar, SWT.DROP_DOWN);
		item.setText ("Drop_Down");
		item.setToolTipText("SWT.DROP_DOWN");
		item.addSelectionListener(new DropDownSelectionListener());
	}

	protected Control innerCreateDialogArea(Composite parent) {
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		return composite;
	}

	public void setInput(Object input) {
	}

	private static ScreenBody loadXML(File file) {

		InputStream is = null;
		try {
			CompositeMap loadFile = CompositeMapUtil.loadFile(file);
			if (loadFile != null) {
				CompositeMap2Object c2o = new CompositeMap2Object();
				return c2o.createScreenBody(loadFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new ScreenBody();

	}

	public void applyValue(String value) {
		// demon.applyValue(value);
		this.close();
	}
	public IPath getActiveFilePath() {
		return new Path("");
	}
	public boolean close() {
		boolean close = super.close();
		if (demonstratingDialog != null) {
			demonstratingDialog.close();
		}
		return close;
	}

	public EditPartFactory getPartFactory(EditorMode editorMode) {
		return new ExtAuroraPartFactory(editorMode);
	}

	public void setLoginPage(DemonstratingDialog demonstratingDialog) {
		this.demonstratingDialog = demonstratingDialog;
	}

	class DropDownSelectionListener extends SelectionAdapter {
		private Menu    menu = null;
		
		public void widgetSelected(SelectionEvent event) {
			// Create the menu if it has not already been created
			if (menu == null) {
				// Lazy create the menu.
				ToolBar toolbar = ((ToolItem) event.widget).getParent();
				int style = toolbar.getStyle() & (SWT.RIGHT_TO_LEFT | SWT.LEFT_TO_RIGHT);
				menu = new Menu(parentShell, style | SWT.POP_UP);
				for (int i = 0; i < 9; ++i) {
					final String text = "Function A" + i;
					if (text.length() != 0) {
						MenuItem menuItem = new MenuItem(menu, SWT.NONE);
						menuItem.setText(text);
						menuItem.addSelectionListener(new SelectionAdapter(){
							public void widgetSelected(SelectionEvent event) {
//								vsEditor.setInput(loadXML("CARGO.uip"));
							}
						});
						
					} else {
						new MenuItem(menu, SWT.SEPARATOR);
					}
				}
			}
			
			/**
			 * A selection event will be fired when a drop down tool
			 * item is selected in the main area and in the drop
			 * down arrow.  Examine the event detail to determine
			 * where the widget was selected.
			 */		
			if (event.detail == SWT.ARROW) {
				/*
				 * The drop down arrow was selected.
				 */
				// Position the menu below and vertically aligned with the the drop down tool button.
				final ToolItem toolItem = (ToolItem) event.widget;
				final ToolBar  toolBar = toolItem.getParent();
				
				Point point = toolBar.toDisplay(new Point(event.x, event.y));
				menu.setLocation(point.x, point.y);
				menu.setVisible(true);
			} 
		}
	}
	
}

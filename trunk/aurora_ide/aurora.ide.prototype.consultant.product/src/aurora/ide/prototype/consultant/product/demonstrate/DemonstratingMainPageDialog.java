package aurora.ide.prototype.consultant.product.demonstrate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
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

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.meta.gef.control.ConsultantDemonstratingComposite;
import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.parts.ExtAuroraPartFactory;
import aurora.ide.meta.gef.editors.wizard.dialog.DemonstratingDialog;
import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.prototype.consultant.product.fsd.wizard.TitleControl;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.property.page.ProjectDemonstratePropertyPage;
import aurora.ide.prototype.consultant.view.property.page.ProjectDemonstratePropertyPage.F;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.PageModel;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.io.CompositeMap2Object;

public class DemonstratingMainPageDialog extends DemonstratingDialog {

	private DemonstratingDialog demonstratingDialog;
	private Shell parentShell;
	private ConsultantDemonstratingComposite vsEditor;
	private PageModel model;
	private File activeFile;
	private DrillDownAdapter drillDown;
	private UIPInput curentInput;

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
		drillDown = new DrillDownAdapter(this, getWelcomUip());
		Composite container = (Composite) innerCreateDialogArea(parent);
		container.setLayout(new GridLayout());
		ToolBar textToolBar = new ToolBar(container, SWT.HORIZONTAL | SWT.FLAT
				| SWT.WRAP | SWT.BORDER);
		textToolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		drillDown.addNavigationActions(textToolBar);
		createFunctionMenu(textToolBar);

		vsEditor = new ConsultantDemonstratingComposite(this);
		vsEditor.createPartControl(container);
		vsEditor.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		vsEditor.setFocus();
		goWelcome();
		return container;
	}

	public void goWelcome() {

		openFile(getWelcomUip());
	}

	protected void openFile(File f) {
		this.openFile(new UIPInput(this.curentInput, new Path(f.getPath())));
	}

	protected void openFile(UIPInput input) {
		this.curentInput = input;
		this.activeFile = input.getPath().toFile();
		drillDown.inputChanged(input);
		vsEditor.setInput(loadXML(activeFile));
	}

	private void loadModel() {
		ProjectDemonstratePropertyPage ppd = new ProjectDemonstratePropertyPage();
		ppd.setElement(new Node(this.getProject()));
		this.model = ppd.getModel();
	}

	private File getWelcomUip() {
		String pp = model
				.getStringPropertyValue(ProjectDemonstratePropertyPage.WELCOME_UIP);
		IPath p = new Path(pp);
		File file = p.toFile();
		return file;
	}

	private String loadFuncitonName(F f) {
		File file = new File(f.functionPath);
		if (file.isFile()) {
			CompositeMap loadFile = CompositeMapUtil.loadFile(file);
			PageModel mm = new PageModel();
			TitleControl tc = new TitleControl(mm);
			tc.loadFromMap(loadFile);
			return mm.getStringPropertyValue(FunctionDesc.fun_name);
		}
		return "NONE_NAME"; //$NON-NLS-1$
	}

	private Menu createMenu(F f) {
		final Menu menu = new Menu(parentShell, SWT.POP_UP);
		List<Object> uipFiles = f.uipFiles;
		for (Object o : uipFiles) {
			if (o instanceof String) {
				final MenuItem item = new MenuItem(menu, SWT.NONE);
				final IPath p = new Path((String) o);
				String fileName = p.removeFileExtension().lastSegment();
				item.setText(fileName);
				item.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						File file = p.toFile();
						if (file.isFile())
							openFile(file);
						else {
							MessageDialog.openError(parentShell,
									"ERROR", //$NON-NLS-1$
									Messages.DemonstratingMainPageDialog_2
											+ p.toString()
											+ Messages.DemonstratingMainPageDialog_3);
						}
					}
				});
			}
		}
		return menu;
	}

	private String getMenuName(Node node) {
		boolean module = ResourceUtil.isModule(node.getFile());
		if (module) {
			return node.getFile().getName();
		}

		boolean function = ResourceUtil.isFunction(node.getFile());
		if (function) {
			File file = new File(node.getPropertiesPath());
			if (file.isFile()) {
				CompositeMap loadFile = CompositeMapUtil.loadFile(file);
				PageModel mm = new PageModel();
				TitleControl tc = new TitleControl(mm);
				tc.loadFromMap(loadFile);
				return mm.getStringPropertyValue(FunctionDesc.fun_name);
			}
		}
		return "NONE_NAME"; //$NON-NLS-1$

	}

	protected void createFunctionMenu(ToolBar textToolBar) {

		Node root = (Node) model
				.getPropertyValue(ProjectDemonstratePropertyPage.ROOT_MENU);

		List<Node> children = root.getChildren();
		for (Node node : children) {
			if(node.isChecked() == false)
				continue;
			ToolItem item = new ToolItem(textToolBar, SWT.DROP_DOWN);
			String menuName = getMenuName(node);
			item.setText(menuName);
			item.setToolTipText(menuName);
			item.setData(node);
			Menu menu = createMenu(node);
			item.addSelectionListener(new DropDownSelectionListener(menu));
		}

		// List<ProjectDemonstratePropertyPage.F> functions =
		// (List<ProjectDemonstratePropertyPage.F>) model
		// .getPropertyValue(ProjectDemonstratePropertyPage.FUNCTIONS);
		// if (functions == null)
		// return;
		// for (F f : functions) {
		// ToolItem item = new ToolItem(textToolBar, SWT.DROP_DOWN);
		// String loadFuncitonName = loadFuncitonName(f);
		// item.setText(loadFuncitonName);
		// item.setToolTipText(loadFuncitonName);
		// item.setData(f);
		// Menu menu = createMenu(f);
		// item.addSelectionListener(new DropDownSelectionListener(menu));
		// }
	}

	private void fillMenu(Menu menu,Node node){
		List<Node> children = node.getChildren();
		for (Node n : children) {
			if(n.isChecked() == false)
				continue;
			if (n.hasChildren()) {
				MenuItem subMenu = new MenuItem(menu, SWT.CASCADE);
				subMenu.setText(getMenuName(n));
				Menu menu_1 = new Menu(subMenu);
				subMenu.setMenu(menu_1);
				fillMenu(menu_1,n);
			} else {
				final MenuItem item = new MenuItem(menu, SWT.NONE);
				final IPath p = n.getPath();
				String fileName = p.removeFileExtension().lastSegment();
				item.setText(fileName);
				item.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						File file = p.toFile();
						if (file.isFile())
							openFile(file);
						else {
							MessageDialog.openError(parentShell,
									"ERROR", //$NON-NLS-1$
									Messages.DemonstratingMainPageDialog_2
											+ p.toString()
											+ Messages.DemonstratingMainPageDialog_3);
						}
					}
				});
			}
		}	
	}
	
	private Menu createMenu(Node node) {
		Menu menu = new Menu(parentShell, SWT.POP_UP);
		
		fillMenu(menu,node);

		return menu;
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
		if (input instanceof File) {
			openFile((File) input);
		}
		if (input instanceof UIPInput) {
			this.openFile((UIPInput) input);
		}
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
		if (activeFile != null && activeFile.isFile())
			return new Path(activeFile.getPath());
		return new Path(""); //$NON-NLS-1$
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
		private Menu menu = null;

		public DropDownSelectionListener(Menu menu) {
			this.menu = menu;
		}

		public void widgetSelected(SelectionEvent event) {
			final ToolItem toolItem = (ToolItem) event.widget;
			final ToolBar toolBar = toolItem.getParent();
			Rectangle rect = toolItem.getBounds();
			Point pt = new Point(rect.x, rect.y + rect.height + 3);
			Point point = toolBar.toDisplay(pt);
			menu.setLocation(point.x, point.y);
			menu.setVisible(true);
		}
	}

}

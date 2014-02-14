package aurora.ide.prototype.consultant.view.property.page;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.libs.AuroraImagesUtils;
import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.prototype.consultant.view.FunctionSelectionDialog;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.PageModel;
import aurora.ide.swt.util.TableContentProvider;
import aurora.ide.swt.util.TableLabelProvider;
import aurora.ide.swt.util.TextField;
import aurora.ide.swt.util.WidgetFactory;
import aurora.ide.swt.util.viewer.CTableViewer;

public class ProjectDemonstratePropertyPage extends AbstractFSDPropertyPage {

	private static final String FILES = "files"; //$NON-NLS-1$
	public static final String DEMONSTRATE_SETTING = "demonstrate.setting"; //$NON-NLS-1$
	public static final String FUNCTION = "function"; //$NON-NLS-1$
	public static final String FUNCTIONS = "functions"; //$NON-NLS-1$
	public static final String WELCOME_UIP = "welcome_uip"; //$NON-NLS-1$
	public static final String LOGIN_IMG = "login_img"; //$NON-NLS-1$
	private PageModel model = new PageModel();

	protected Control createContents(final Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		root.setLayout(layout);
		final TextField loginBkr = WidgetFactory.createTextButtonField(root,
				Messages.ProjectDemonstratePropertyPage_6, Messages.ProjectDemonstratePropertyPage_7);
		loginBkr.getText().setEditable(false);
		loginBkr.addButtonClickListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				String path = AuroraImagesUtils.queryFile(parent.getShell());
				if (path != null) {
					Path p = new Path(path);
					String fileExtension = p.getFileExtension();
					int iconType = AuroraImagesUtils.getIconType(fileExtension);
					if (iconType == -1)
						return;
					ImageData loadImageData;
					try {
						loadImageData = AuroraImagesUtils.loadImageData(p);
						byte[] bytes = AuroraImagesUtils.toBytes(loadImageData,
								iconType);
						model.setPropertyValue(LOGIN_IMG,
								AuroraImagesUtils.toString(bytes));
						loginBkr.setText(Messages.ProjectDemonstratePropertyPage_8);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		final TextField welcomeUip = WidgetFactory.createTextButtonField(root,
				Messages.ProjectDemonstratePropertyPage_9, Messages.ProjectDemonstratePropertyPage_10);
		welcomeUip.getText().setEditable(false);
		welcomeUip.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				model.setPropertyValue(WELCOME_UIP, welcomeUip.getText()
						.getText());
			}
		});
		welcomeUip.addButtonClickListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.OPEN);
				dialog.setText("Open File"); //$NON-NLS-1$
				dialog.setFilterExtensions(new String[] { "*.uip" }); //$NON-NLS-1$
				String path = dialog.open();
				if (path != null && path.length() > 0) {
					welcomeUip.setText(path);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Group g = new Group(root, SWT.NONE);
		g.setText(Messages.ProjectDemonstratePropertyPage_11);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 3;
		g.setLayoutData(layoutData);
		g.setLayout(layout);

		CTableViewer ctv = new CTableViewer() {
			protected void clickAddButton(Shell shell, final TableViewer tv) {
				InnerDialog id = new InnerDialog(shell);
				int open = id.open();
				if (InnerDialog.OK == open) {
					this.getInput().add(id.f);
					this.setInput(tv);
				}
			}
		};

		ctv.addColumn(Messages.ProjectDemonstratePropertyPage_12, 128);
		ctv.addColumn(Messages.ProjectDemonstratePropertyPage_13, 193);
		ctv.addColumn(Messages.ProjectDemonstratePropertyPage_14, 293);
		ctv.setTableContentProvider(new TableContentProvider());
		ctv.setTableLabelProvider(new TableLabelProvider() {
			public String getColumnText(Object element, int i) {

				if (element instanceof F) {
					Path p = new Path(((F) element).functionPath);
					File file = p.toFile();
					if (file.exists() == false)
						return ""; //$NON-NLS-1$
					if (i == 0) {
						return file.getParentFile().getName();
					}
					if (i == 1) {
						CompositeMap loadFile = CompositeMapUtil.loadFile(file);
						CompositeMap child = loadFile
								.getChild(FunctionDesc.fun_name);
						String text = child == null ? "" : child.getText(); //$NON-NLS-1$
						return text == null ? "" : text; //$NON-NLS-1$
					}
					if (i == 2) {
						List<Object> uipFiles = ((F) element).uipFiles;
						String s = ""; //$NON-NLS-1$
						for (Object object : uipFiles) {
							Path path = new Path("" +object); //$NON-NLS-1$
							String name = path.removeFileExtension().lastSegment();
							s = s + "[ " + name + " ]  "; //$NON-NLS-1$ //$NON-NLS-2$
						}
						return s;
					}
				}
				return ""; //$NON-NLS-1$
			}
		});
		setInput(loginBkr, welcomeUip, ctv);
		ctv.createContentTable(g);
		return root;
	}

	protected void setInput(TextField loginBkr, TextField welcomeUip,
			CTableViewer ctv) {
		welcomeUip.setText(model.getStringPropertyValue(WELCOME_UIP));
		String stringPropertyValue = model.getStringPropertyValue(LOGIN_IMG);
		loginBkr.setText(stringPropertyValue == null
				|| "".equals(stringPropertyValue) ? Messages.ProjectDemonstratePropertyPage_23 : Messages.ProjectDemonstratePropertyPage_24); //$NON-NLS-1$
		List<Object> propertyValue = (List<Object>) model
				.getPropertyValue(FUNCTIONS);
		ctv.setInput(propertyValue);
	}

	private class InnerDialog extends Dialog {

		private F f = new F();
		private CTableViewer ctv;

		public InnerDialog(Shell parent) {
			super(parent);
		}

		public boolean close() {
			f.uipFiles = ctv.getInput();
			return super.close();
		}

		protected Control createContents(Composite parent) {
			Control createContents = super.createContents(parent);
			updateStatus();
			return createContents;
		}

		protected Control createDialogArea(final Composite parent) {
			Composite container = (Composite) super.createDialogArea(parent);

			Composite p = WidgetFactory.composite(container);
			GridLayout layout = new GridLayout();
			layout.numColumns = 3;
			p.setLayout(layout);
			p.setLayoutData(new GridData(GridData.FILL_BOTH));

			final TextField fn = WidgetFactory.createTextButtonField(p, Messages.ProjectDemonstratePropertyPage_25,
					Messages.ProjectDemonstratePropertyPage_26);
			fn.getText().setEditable(false);
			fn.addButtonClickListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					FunctionSelectionDialog fsd = new FunctionSelectionDialog();
					String path = fsd.openFolderSelectionDialog(
							Messages.ProjectDemonstratePropertyPage_27, parent.getShell(), getElement());
					if (path != null && path.length() > 0) {
						
						CompositeMap loadFile = CompositeMapUtil.loadFile(new File(path));
						CompositeMap child = loadFile
								.getChild(FunctionDesc.fun_name);
						String text = child == null ? "" : child.getText(); //$NON-NLS-1$
						f.functionPath = path;
						fn.setText(text);
					}
				}
			});

			fn.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					updateStatus();
				}
			}); 
			ctv = new CTableViewer(){
				protected void clickAddButton(Shell shell, final TableViewer tv) {
//					FileDialog dialog = new FileDialog(shell, SWT.OPEN);
//					dialog.setText("Open File"); //$NON-NLS-1$
//					dialog.setFilterExtensions(new String[] { "*.uip" }); //$NON-NLS-1$
//					String path = dialog.open();
//					if (path != null && path.length() > 0) {
//						getTableInput().add(path);
//					}
//					setInput(tv);
					FunctionSelectionDialog fsd = new FunctionSelectionDialog();
					String path = fsd.openUIPSelectionDialog(Messages.ProjectDemonstratePropertyPage_29, shell,
							getProjectNode());
					if (path != null && path.length() > 0) {
						getTableInput().add(path);
					}
					tv.setInput(getTableInput());
					
				}
			};
			ctv.addColumn(Messages.ProjectDemonstratePropertyPage_30, 128);
			ctv.addColumn(Messages.ProjectDemonstratePropertyPage_31, 193);
			ctv.setTableLabelProvider(new TableLabelProvider() {
				public String getColumnText(Object element, int i) {

					if (element instanceof String) {
						if (i == 0) {
							Path p = new Path(element.toString());
							return p.lastSegment();
						}
						if (i == 1) {
							return element.toString();
						}
					}
					return ""; //$NON-NLS-1$
				}
			});
			Composite composite = WidgetFactory.composite(p);
			GridData layoutData = new GridData(GridData.FILL_BOTH);
			layoutData.horizontalSpan = 3;
			composite.setLayoutData(layoutData);
			composite.setLayout(layout);
			ctv.createContentTable(composite);
			return container;
		}

		private void updateStatus() {
			Button ob = this.getButton(IDialogConstants.OK_ID);
			ob.setEnabled(f.functionPath != null);
		}
	}

	public class F {
		public String functionPath;
		public List<Object> uipFiles = new ArrayList<Object>();
	}

	protected void loadPageModel() {
		IAdaptable element = this.getElement();
		if (element instanceof Node) {
			File file = ((Node) element).getFile();
			CompositeMap pp = loadProperties(file);
			CompositeMap child = pp.getChild(WELCOME_UIP);
			if (child != null) {
				model.setPropertyValue(WELCOME_UIP,
						makeAbsolute(child.getText()));
			}
			child = pp.getChild(LOGIN_IMG);
			if (child != null) {
				model.setPropertyValue(LOGIN_IMG, child.getText());
			}
			child = pp.getChild(FUNCTIONS);

			List<F> fff = new ArrayList<F>();
			if (child != null) {
				List childsNotNull = child.getChildsNotNull();
				for (Object object : childsNotNull) {
					CompositeMap m = (CompositeMap) object;
					F f = new F();
					String ss = m.getString(FUNCTION, ""); //$NON-NLS-1$
					CompositeMap child2 = m.getChild(FILES);
					String t = child2 == null ? "" : child2.getText(); //$NON-NLS-1$
					if (t == null)
						t = ""; //$NON-NLS-1$
					String[] split = t.split(","); //$NON-NLS-1$
					f.uipFiles = new ArrayList<Object>();
					for (String s : split) {
						if (s != null && "".equals(s) == false) { //$NON-NLS-1$
							f.uipFiles.add(makeAbsolute(s));
						}
					}
					f.functionPath = makeAbsolute(ss);
					fff.add(f);
				}
			}
			model.setPropertyValue(FUNCTIONS, fff);
		}
	}

	public PageModel getModel() {
		return model;
	}

	protected CompositeMap loadProperties(File file) {
		File setting = new File(file, DEMONSTRATE_SETTING);
		if (setting.exists()) {
			CompositeMap loadFile = CompositeMapUtil.loadFile(setting);
			return loadFile;
		}
		return new CompositeMap();
	}

	protected void saveProperties(CompositeMap map) throws IOException {
		IAdaptable element = this.getElement();
		if (element instanceof Node) {
			File file = ((Node) element).getFile();
			ResourceUtil.createFile(file, DEMONSTRATE_SETTING, map);
		}

	}

	protected void saveTOMap(CompositeMap map) {
		String s = model.getStringPropertyValue(WELCOME_UIP);
		map.createChild(WELCOME_UIP).setText(makeRelative(s));
		s = model.getStringPropertyValue(LOGIN_IMG);
		map.createChild(LOGIN_IMG).setText(s);

		@SuppressWarnings("unchecked")
		List<F> propertyValue = (List<F>) (model.getPropertyValue(FUNCTIONS));

		if (propertyValue != null) {
			CompositeMap fff = map.createChild(FUNCTIONS);
			for (F f : propertyValue) {
				CompositeMap ff = fff.createChild(FUNCTION);
				ff.put(FUNCTION, makeRelative(f.functionPath));
				List<Object> uipFiles = f.uipFiles;
				String ss = ""; //$NON-NLS-1$
				for (Object string : uipFiles) {
					ss = ss + "," + makeRelative(string.toString()); //$NON-NLS-1$
				}
				ss = ss.replaceFirst(",", ""); //$NON-NLS-1$ //$NON-NLS-2$
				ff.createChild(FILES).setText(ss);
			}
		}
	}

	public String makeRelative(String path) {
		if (path == null || "".equals(path)) //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		IPath p = new Path(path);
		IPath makeRelativeTo = p.makeRelativeTo(getBasePath());
		return makeRelativeTo.toString();
	}

	public String makeAbsolute(String path) {
		if (path == null || "".equals(path)) //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		IPath p = new Path(path);
		return getBasePath().append(p).toString();
	}

	protected IPath getBasePath() {
		Node element = (Node) getElement();
		return element.getPath();
	}

}

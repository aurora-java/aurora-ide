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

	public static final String DEMONSTRATE_SETTING = "demonstrate.setting";
	public static final String FUNCTION = "function";
	public static final String FUNCTIONS = "functions";
	public static final String WELCOME_UIP = "welcome_uip";
	public static final String LOGIN_IMG = "login_img";
	private PageModel model = new PageModel();

	protected Control createContents(final Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		root.setLayout(layout);
		final TextField loginBkr = WidgetFactory.createTextButtonField(root,
				"登陆页面背景图片", "浏览");
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
						loginBkr.setText("已设置");
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		final TextField welcomeUip = WidgetFactory.createTextButtonField(root,
				"欢迎页面", "浏览");
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
					Node element = (Node) getElement();
					IPath afp = element.getPath();
					IPath p = new Path(path);
					IPath makeRelativeTo = p.makeRelativeTo(afp);
					welcomeUip.setText(makeRelativeTo.toString());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Group g = new Group(root, SWT.NONE);
		g.setText("功能列表");
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

		ctv.addColumn("功能号", 128);
		ctv.addColumn("功能名", 193);
		ctv.setTableContentProvider(new TableContentProvider());
		ctv.setTableLabelProvider(new TableLabelProvider() {
			public String getColumnText(Object element, int i) {

				if (element instanceof F) {
					Path p = new Path(((F) element).functionPath);
					File file = p.toFile();
					if (file.exists() == false)
						return "";
					if (i == 0) {
						return file.getParentFile().getName();
					}
					if (i == 1) {
						CompositeMap loadFile = CompositeMapUtil.loadFile(file);
						CompositeMap child = loadFile
								.getChild(FunctionDesc.fun_name);
						String text = child == null ? "" : child.getText();
						return text == null ? "" : text;
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
				|| "".equals(stringPropertyValue) ? "未设置" : "已设置");
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

		protected Control createDialogArea(final Composite parent) {
			Composite container = (Composite) super.createDialogArea(parent);

			Composite p = WidgetFactory.composite(container);
			GridLayout layout = new GridLayout();
			layout.numColumns = 3;
			p.setLayout(layout);
			p.setLayoutData(new GridData(GridData.FILL_BOTH));

			final TextField fn = WidgetFactory.createTextButtonField(p, "功能名",
					"浏览");
			fn.getText().setEditable(false);
			fn.addButtonClickListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					FunctionSelectionDialog fsd = new FunctionSelectionDialog();
					String path = fsd.openFolderSelectionDialog(
							"选择FSD Function", parent.getShell(), getElement());
					if (path != null && path.length() > 0) {
						fn.setText(path);
					}
				}
			});

			fn.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					f.functionPath = fn.getText().getText();
				}
			});
			ctv = new CTableViewer();
			ctv.addColumn("文件名", 128);
			ctv.addColumn("路径", 193);
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
				model.setPropertyValue(WELCOME_UIP, child.getText());
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
					String ss = m.getString(FUNCTION, "");
					String t = m.getText();
					String[] split = t.split(",");
					f.uipFiles = new ArrayList<Object>();
					for (String s : split) {
						if (s != null && "".equals(s) == false) {
							f.uipFiles.add(s);
						}
					}
					f.functionPath = ss;
					fff.add(f);
				}
			}
			model.setPropertyValue(FUNCTIONS, fff);
		}
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
		map.createChild(WELCOME_UIP).setText(s);
		s = model.getStringPropertyValue(LOGIN_IMG);
		map.createChild(LOGIN_IMG).setText(s);

		@SuppressWarnings("unchecked")
		List<F> propertyValue = (List<F>) (model.getPropertyValue(FUNCTIONS));

		if (propertyValue != null) {
			CompositeMap fff = map.createChild(FUNCTIONS);
			for (F f : propertyValue) {
				CompositeMap ff = fff.createChild(FUNCTION);
				ff.put(FUNCTION, f.functionPath);
				List<Object> uipFiles = f.uipFiles;
				String ss = "";
				for (Object string : uipFiles) {
					ss = ss + "," + string;
				}
				ss = ss.replaceFirst(",", "");
				ff.createChild("files").setText(ss);
			}
		}
	}

}

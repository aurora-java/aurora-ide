package aurora.ide.meta.gef.editors.property;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.internal.ide.dialogs.OpenResourceDialog;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.textpage.ColorManager;
import aurora.ide.editor.textpage.JavaScriptConfiguration;
import aurora.ide.meta.gef.editors.figures.ColorConstants;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.ButtonClicker;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.TabBody;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.search.core.Util;

public class ButtonClickEditDialog extends EditWizard {

	protected Shell shell;
	private static final Color SELECTION_BG = new Color(null, 109, 187, 242);
	private static final String[] descriptions = { "查询,选择一个带有查询功能的组件",
			"重置,选择一个带有重置功能的组件", "保存,选择一个带有保存功能的组件", "选择一个要打开的页面", "关闭", "自定义" };
	private Button[] radios = new Button[ButtonClicker.action_texts.length];
	private Composite[] stackComposites = new Composite[radios.length];
	private String section_type_filter = Container.SECTION_TYPE_QUERY;
	private Composite composite_right;
	private ButtonClicker clicker = null;
	private WizardPage page;
	protected Object tmpTargetCmp;
	private String tmpPath;
	private String tmpWindowID;
	private String tmpFunction;

	public ButtonClickEditDialog() {
		setWindowTitle("Click");
	}

	@Override
	public void setDialogEdiableObject(DialogEditableObject obj) {
		clicker = (ButtonClicker) obj;
	}

	public void addPages() {
		page = new InnerPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		if (tmpTargetCmp instanceof AuroraComponent)
			clicker.setTargetComponent((AuroraComponent) tmpTargetCmp);
		clicker.setOpenPath(tmpPath);
		clicker.setCloseWindowID(tmpWindowID);
		clicker.setFunction(tmpFunction);
		return true;
	}

	private class InnerPage extends WizardPage implements SelectionListener {
		private StackLayout slLayout = new StackLayout();

		public InnerPage() {
			super("button_click");
			setTitle("设置button的'click'");
		}

		public void createControl(Composite parent) {
			SashForm sashForm = new SashForm(parent, SWT.NONE);
			sashForm.setBackground(ColorConstants.GRID_COLUMN_GRAY);
			sashForm.setSashWidth(1);

			Composite composite_left = new Composite(sashForm, SWT.NONE);
			RowLayout rw = new RowLayout(SWT.VERTICAL);
			rw.fill = true;
			rw.spacing = 5;
			composite_left.setLayout(rw);
			composite_right = new Composite(sashForm, SWT.NONE);
			composite_right.setLayout(slLayout);

			for (int i = 0; i < ButtonClicker.action_texts.length; i++) {
				radios[i] = new Button(composite_left, SWT.RADIO);
				radios[i].setText(ButtonClicker.action_texts[i]);
				// radios[i].setBounds(10, 10 + 24 * i, 200, 24);
				radios[i].addSelectionListener(this);
				stackComposites[i] = new Composite(composite_right, SWT.NONE);
				if (ButtonClicker.action_ids[i].equals(clicker.getActionID())) {
					radios[i].setSelection(true);
					radios[i].setBackground(SELECTION_BG);
					slLayout.topControl = stackComposites[i];
					setDescription(descriptions[i]);
				}
			}

			create_query(0);// 0
			create_reset(1);// 1
			create_save(2);// 2
			create_open(3);// 3
			create_close(4);// 4
			create_userDefine(5);// 5
			composite_right.layout();
			if (slLayout.topControl != null) {
				slLayout.topControl.forceFocus();
			}

			sashForm.setWeights(new int[] { 1, 3 });
			setControl(sashForm);
		}

		private void create_query(int index) {
			AuroraComponent comp = (AuroraComponent) clicker.getContextInfo();
			ViewDiagram root = null;
			while (comp != null) {
				if (comp instanceof ViewDiagram) {
					root = (ViewDiagram) comp;
					break;
				}
				comp = comp.getParent();
			}
			if (root == null)
				throw new RuntimeException("Null root");
			stackComposites[index].setLayout(new FillLayout());
			final Tree tree = new Tree(stackComposites[index], SWT.BORDER);
			TreeItem rootItem = new TreeItem(tree, SWT.NONE);
			rootItem.setText("screenBody");
			rootItem.setForeground(new Color(null, 200, 200, 200));
			createSubTree(tree, rootItem, root);

			for (TreeItem ti : tree.getItems())
				ti.setExpanded(true);
			tree.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					TreeItem ti = tree.getSelection()[0];
					tmpTargetCmp = ti.getData();
				}

				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
		}

		private void createSubTree(Tree tree, TreeItem ti, Container container) {
			for (AuroraComponent ac : container.getChildren()) {
				if ((ac instanceof Container) && !(ac instanceof TabBody)) {
					Container cont = (Container) ac;
					if (!section_type_filter.equals(cont.getSectionType()))
						continue;
					TreeItem t = new TreeItem(ti, SWT.NONE);
					t.setData(ac);
					if (ac == clicker.getTargetComponent())
						tree.setSelection(t);
					t.setImage(PropertySourceUtil.getImageOf(ac));
					t.setText(getTextOf(ac));
					if (!(ac instanceof Grid))
						createSubTree(tree, t, (Container) ac);
				} else if (ac instanceof TabItem) {
					TreeItem t = new TreeItem(ti, SWT.NONE);
					t.setImage(PropertySourceUtil.getImageOf(ac));
					t.setText(getTextOf(ac));
					t.setForeground(new Color(null, 200, 200, 200));
					createSubTree(tree, t, ((TabItem) ac).getBody());
				}
			}
			for (TreeItem t : ti.getItems())
				t.setExpanded(true);
		}

		private void create_reset(int index) {
			section_type_filter = Container.SECTION_TYPE_QUERY;
			create_query(1);
		}

		private void create_save(int index) {
			section_type_filter = Container.SECTION_TYPE_RESULT;
			create_query(2);
		}

		private void create_open(final int index) {
			stackComposites[index].setLayout(new GridLayout(3, false));
			Label label = new Label(stackComposites[index], SWT.NONE);
			label.setText("screen : ");
			final Text text = new Text(stackComposites[index], SWT.SINGLE
					| SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
					1, 1));
			tmpPath = clicker.getOpenPath();
			if (tmpPath == null)
				tmpPath = "";
			text.setText(tmpPath);
			Button btn = new Button(stackComposites[index], SWT.FLAT);
			btn.setText("选择(&O)");
			btn.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					@SuppressWarnings("restriction")
					OpenResourceDialog ord = new OpenResourceDialog(
							stackComposites[index].getShell(), AuroraPlugin
									.getActiveIFile().getProject(),
							OpenResourceDialog.CARET_BEGINNING);
					ord.setInitialPattern("*.screen");
					ord.open();
					Object obj = ord.getFirstResult();
					if (!(obj instanceof IFile))
						return;
					IFile file = (IFile) obj;
					IPath path = file.getFullPath();
					IContainer web = Util.findWebInf(file).getParent();
					path = path.makeRelativeTo(web.getFullPath());
					tmpPath = path.toString();
					text.setText(tmpPath);
				}

				public void widgetDefaultSelected(SelectionEvent e) {

				}

			});
		}

		private void create_close(int index) {
			stackComposites[index].setLayout(new GridLayout(2, false));
			Label label = new Label(stackComposites[index], SWT.NONE);
			label.setText("windowID : ");
			tmpWindowID = clicker.getCloseWindowID();
			if (tmpWindowID == null)
				tmpWindowID = "";
			final Text text = new Text(stackComposites[index], SWT.SINGLE
					| SWT.BORDER);
			text.setText(tmpWindowID);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
					1, 1));
			text.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					tmpWindowID = text.getText();
				}
			});
		}

		public void widgetSelected(SelectionEvent e) {
			for (int i = 0; i < radios.length; i++) {
				if (radios[i] == e.getSource()) {
					if (radios[i].getSelection()) {
						slLayout.topControl = stackComposites[i];
						clicker.setActionText(ButtonClicker.action_texts[i]);
						clicker.setActionID(ButtonClicker.action_ids[i]);
						composite_right.layout();
						radios[i].setBackground(SELECTION_BG);
						setDescription(descriptions[i]);
					} else
						radios[i].setBackground(radios[i].getParent()
								.getBackground());
				}
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {

		}
	}

	private void create_userDefine(int index) {
		stackComposites[index].setLayout(new GridLayout(1, false));
		Label l = new Label(stackComposites[index], SWT.NONE);
		l.setText("在下面写一个函数");

		final SourceViewer jsEditor = new SourceViewer(stackComposites[index],
				null, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		jsEditor.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		jsEditor.configure(new JavaScriptConfiguration(new ColorManager()));
		jsEditor.getTextWidget().setFont(new Font(null, "Courier New", 10, 0));
		Document document = new Document();
		jsEditor.setDocument(document);
		tmpFunction = clicker.getFunction();
		if (tmpFunction == null) {
			tmpFunction = "function fun_alert(){\n\talert(\"hello\");\n}";
		}
		jsEditor.getTextWidget().setText(tmpFunction);
		jsEditor.getTextWidget().addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				tmpFunction = jsEditor.getTextWidget().getText();
			}
		});
	}

	private String getTextOf(AuroraComponent ac) {
		String prop = ac.getPrompt();
		String aType = ac.getType();
		return aType + " [" + prop + "]";
		// return ac.getClass().getSimpleName();
	}
}

package aurora.ide.meta.gef.editors.property;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import aurora.ide.AuroraPlugin;
import aurora.ide.builder.ResourceUtil;
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
import aurora.ide.meta.gef.editors.wizard.dialog.ParameterComposite;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.search.core.Util;

public class ButtonClickEditDialog extends EditWizard {

	protected Shell shell;
	private static final Color SELECTION_BG = new Color(null, 109, 187, 242);
	private static final String[] descriptions = {
			Messages.ButtonClickEditDialog_0, Messages.ButtonClickEditDialog_1,
			Messages.ButtonClickEditDialog_2, Messages.ButtonClickEditDialog_3,
			Messages.ButtonClickEditDialog_4, Messages.ButtonClickEditDialog_5 };
	private Button[] radios = new Button[ButtonClicker.action_texts.length];
	private String section_type_filter = Container.SECTION_TYPE_QUERY;
	private Composite composite_right;
	private ButtonClicker clicker = null;
	private WizardPage page;
	protected Object tmpTargetCmp;
	private String tmpPath;
	private String tmpWindowID;
	private String tmpFunction;
	private IProject auroraProject = null;
	private ParameterComposite pc;

	public ButtonClickEditDialog() {
		setWindowTitle("Click"); //$NON-NLS-1$
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
		if(pc!=null){
			clicker.setOpenPath(tmpPath);
			clicker.getParameters().clear();
			clicker.getParameters().addAll(pc.getParameters());
		}

		clicker.setCloseWindowID(tmpWindowID);
		clicker.setFunction(tmpFunction);
		return true;
	}

	private class InnerPage extends WizardPage implements SelectionListener {

		public InnerPage() {
			super(Messages.ButtonClickEditDialog_7);
			setTitle(Messages.ButtonClickEditDialog_8);
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

			boolean created = false;
			for (int i = 0; i < ButtonClicker.action_texts.length; i++) {
				radios[i] = new Button(composite_left, SWT.RADIO);
				radios[i].setText(ButtonClicker.action_texts[i]);
				radios[i].addSelectionListener(this);
				if (ButtonClicker.action_ids[i].equals(clicker.getActionID())) {
					radios[i].setSelection(true);
					radios[i].setBackground(SELECTION_BG);
					setDescription(descriptions[i]);
					createRight(i);
					created = true;
				}
			}
			if (!created) {
				radios[0].setSelection(true);
				radios[0].notifyListeners(SWT.Selection, null);
			}

			sashForm.setWeights(new int[] { 1, 3 });
			setControl(sashForm);
		}

		private void createRight(int index) {
			for (Control c : composite_right.getChildren())
				c.dispose();
			setErrorMessage(null);
			setPageComplete(true);
			if (index == 0) {
				section_type_filter = Container.SECTION_TYPE_RESULT;
				create_query();
			} else if (index == 1)
				create_reset();
			else if (index == 2)
				create_save();
			else if (index == 3)
				create_open();
			else if (index == 4)
				create_close();
			else if (index == 5)
				create_userDefine();
			composite_right.layout();
		}

		private void create_query() {
			AuroraComponent comp = (AuroraComponent) clicker.getContextInfo();
			ViewDiagram root = null;
			while (comp != null) {
				if (comp instanceof ViewDiagram) {
					root = (ViewDiagram) comp;
					break;
				}
				comp = comp.getParent();
			}
			if (root == null) {
				setErrorMessage(Messages.ButtonClickEditDialog_9);
				setPageComplete(false);
				return;
			}
			composite_right.setLayout(new FillLayout());
			final Tree tree = new Tree(composite_right, SWT.BORDER);
			TreeItem rootItem = new TreeItem(tree, SWT.NONE);
			rootItem.setText(Messages.ButtonClickEditDialog_10);
			rootItem.setForeground(new Color(null, 200, 200, 200));

			createSubTree(tree, rootItem, root);
			if (tree.getSelection().length == 0) {
				setErrorMessage(Messages.ButtonClickEditDialog_11);
				setPageComplete(false);
			}

			for (TreeItem ti : tree.getItems())
				ti.setExpanded(true);
			tree.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					TreeItem ti = tree.getSelection()[0];
					Object data = ti.getData();
					if (data == null) {
						setErrorMessage(Messages.ButtonClickEditDialog_12);
						setPageComplete(false);
					} else {
						setErrorMessage(null);
						setPageComplete(true);
					}
					tmpTargetCmp = data;
				}

				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
		}

		/**
		 * <i>section_type_filter</i> should be setted before call this method
		 * 
		 * @param tree
		 * @param ti
		 * @param container
		 */
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

		private void create_reset() {
			section_type_filter = Container.SECTION_TYPE_QUERY;
			create_query();
		}

		private void create_save() {
			section_type_filter = Container.SECTION_TYPE_RESULT;
			create_query();
		}

		private void create_open() {
			composite_right.setLayout(new GridLayout(3, false));
			Label label = new Label(composite_right, SWT.NONE);
			label.setText(Messages.ButtonClickEditDialog_13);
			final Text text = new Text(composite_right, SWT.SINGLE | SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
					1, 1));
			tmpPath = clicker.getOpenPath();
			if (tmpPath == null)
				tmpPath = ""; //$NON-NLS-1$
			text.setText(tmpPath);
			Button btn = new Button(composite_right, SWT.FLAT);
			btn.setText(Messages.ButtonClickEditDialog_15);

			IProject proj = AuroraPlugin.getActiveIFile().getProject();
			AuroraMetaProject mProj = new AuroraMetaProject(proj);
			try {
				auroraProject = mProj.getAuroraProject();
			} catch (Exception e1) {
				auroraProject = null;
			}
			if (auroraProject == null) {
				text.setEnabled(false);
				btn.setEnabled(false);
				setErrorMessage(Messages.ButtonClickEditDialog_16);
				setPageComplete(false);
				return;
			}

			btn.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					ResourceSelector fss = new ResourceSelector(composite_right
							.getShell());
					String webHome = ResourceUtil.getWebHome(auroraProject);
					IResource res = ResourcesPlugin.getWorkspace().getRoot()
							.findMember(webHome);
					fss.setInput((IContainer) res);
					Object obj = fss.getSelection();
					if (!(obj instanceof IFile)) {
						return;
					}
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

			createParaTable(composite_right);
		}

		private void createParaTable(Composite composite_right) {
			AuroraComponent comp = (AuroraComponent) clicker.getContextInfo();
			ViewDiagram root = null;
			while (comp != null) {
				if (comp instanceof ViewDiagram) {
					root = (ViewDiagram) comp;
					break;
				}
				comp = comp.getParent();
			}
			if (root == null) {
				setErrorMessage(Messages.ButtonClickEditDialog_9);
				setPageComplete(false);
				return;
			}
			GridData data = new GridData(GridData.FILL_BOTH);
			data.horizontalSpan = 3;
			pc = new ParameterComposite(root,composite_right,SWT.NONE);
			pc.setLayoutData(data);
			pc.setParameters(clicker.getParameters());
		}

		private void create_close() {
			composite_right.setLayout(new GridLayout(2, false));
			Label label = new Label(composite_right, SWT.NONE);
			label.setText(Messages.ButtonClickEditDialog_19);
			tmpWindowID = clicker.getCloseWindowID();
			if (tmpWindowID == null)
				tmpWindowID = ""; //$NON-NLS-1$
			final Text text = new Text(composite_right, SWT.SINGLE | SWT.BORDER);
			text.setText(tmpWindowID);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
					1, 1));
			text.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					tmpWindowID = text.getText();
				}
			});
		}

		private void create_userDefine() {
			composite_right.setLayout(new GridLayout(1, false));
			Label l = new Label(composite_right, SWT.NONE);
			l.setText(Messages.ButtonClickEditDialog_21);

			final SourceViewer jsEditor = new SourceViewer(composite_right,
					null, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
			jsEditor.getControl().setLayoutData(
					new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			jsEditor.configure(new JavaScriptConfiguration(new ColorManager()));
			jsEditor.getTextWidget().setFont(new Font(null, "Consolas", 10, 0)); //$NON-NLS-1$
			Document document = new Document();
			jsEditor.setDocument(document);
			tmpFunction = clicker.getFunction();
			if (tmpFunction == null) {
				tmpFunction = "function fun_alert(){\n\talert(\"hello\");\n}"; //$NON-NLS-1$
			}
			jsEditor.getTextWidget().setText(tmpFunction);
			jsEditor.getTextWidget().addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					tmpFunction = jsEditor.getTextWidget().getText();
					String msg = validateFunction(tmpFunction);
					setErrorMessage(msg.length() == 0 ? null
							: validateFunction(tmpFunction));
					setPageComplete(msg.length() == 0);
				}
			});
		}

		public void widgetSelected(SelectionEvent e) {
			for (int i = 0; i < radios.length; i++) {
				if (radios[i] == e.getSource()) {
					if (radios[i].getSelection()) {
						createRight(i);
						clicker.setActionID(ButtonClicker.action_ids[i]);
						radios[i].setBackground(SELECTION_BG);
						setDescription(descriptions[i]);
					} else
						radios[i].setBackground(radios[i].getParent()
								.getBackground());
				}
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			// never called on radio
		}
	}

	private String getTextOf(AuroraComponent ac) {
		String prop = ac.getPrompt();
		String aType = ac.getType();
		return aType + " [" + prop + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		// return ac.getClass().getSimpleName();
	}
}

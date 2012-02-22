package aurora.ide.meta.gef.editors.property;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.dialogs.OpenResourceDialog;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.textpage.ColorManager;
import aurora.ide.editor.textpage.JavaScriptConfiguration;
import aurora.ide.meta.gef.editors.figures.ColorConstants;
import aurora.ide.meta.gef.editors.models.Renderer;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.search.core.Util;

public class RendererEditDialog extends EditWizard {

	private static final Color SELECTION_BG = new Color(null, 109, 187, 242);
	private Button[] radios = new Button[Renderer.RENDERER_TYPES.length];
	private Composite composite_right;
	private Renderer renderer;
	private String tmpOpenPath;
	private String tmpLabelText;
	private String tmpFunctionName;
	private String tmpFunction;

	private String tmpRendererType;
	private InnerPage page;
	public IProject auroraProject;

	public RendererEditDialog() {
		super();
		setWindowTitle("renderer");
	}

	@Override
	public void setDialogEdiableObject(DialogEditableObject obj) {
		renderer = (Renderer) obj;
	}

	@Override
	public void addPages() {
		page = new InnerPage("renderer");
		addPage(page);
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		renderer.setRendererType(tmpRendererType);
		renderer.setOpenPath(tmpOpenPath);
		renderer.setLabelText(tmpLabelText);
		renderer.setFunctionName(tmpFunctionName);
		renderer.setFunction(tmpFunction);
		return true;
	}

	private class InnerPage extends WizardPage implements SelectionListener {
		private String[] displayTexts = { "页面跳转", "内置函数", "自定义函数" };

		protected InnerPage(String pageName) {
			super(pageName);
			setTitle("renderer 详细设置");
		}

		public void createControl(final Composite parent) {
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
			for (int i = 0; i < radios.length; i++) {
				radios[i] = new Button(composite_left, SWT.RADIO);
				radios[i].setText(displayTexts[i]);
				radios[i].addSelectionListener(this);
				tmpRendererType = renderer.getRendererType();
				if (Renderer.RENDERER_TYPES[i].equals(tmpRendererType)) {
					radios[i].setSelection(true);
					radios[i].setBackground(SELECTION_BG);
					createRight(i);
					created = true;
				}
			}
			if (!created) {
				radios[0].setSelection(true);
				radios[0].notifyListeners(SWT.Selection, new Event());
			}

			sashForm.setWeights(new int[] { 1, 3 });
			setControl(sashForm);
		}

		private void createRight(int idx) {
			for (Control c : composite_right.getChildren())
				c.dispose();
			setErrorMessage(null);
			setPageComplete(true);
			if (idx == 0)
				create_0();
			else if (idx == 1)
				create_1();
			else
				create_2();
			composite_right.layout();
		}

		private void create_0() {
			composite_right.setLayout(new GridLayout(3, false));
			Label label = new Label(composite_right, SWT.NONE);
			label.setText("显示文本 : ");
			final Text text1 = new Text(composite_right, SWT.BORDER);
			text1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
					1, 1));
			tmpLabelText = renderer.getLabelText();
			if (tmpLabelText == null)
				tmpLabelText = "";

			text1.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					tmpLabelText = text1.getText();
				}
			});
			text1.setText(tmpLabelText);
			new Label(composite_right, SWT.NONE);
			label = new Label(composite_right, SWT.NONE);
			label.setText("目标 : ");
			final Text text = new Text(composite_right, SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
					1, 1));
			tmpOpenPath = renderer.getOpenPath();
			if (tmpOpenPath == null || tmpOpenPath == "") {
				tmpOpenPath = "";
				setPageComplete(false);
				setErrorMessage("please select a screen file");
			}
			text.setText(tmpOpenPath);
			text.setEditable(false);
			Button btn = new Button(composite_right, SWT.FLAT);
			btn.setText("选择(&O)");

			IProject proj = AuroraPlugin.getActiveIFile().getProject();
			AuroraMetaProject mProj = new AuroraMetaProject(proj);
			try {
				auroraProject = mProj.getAuroraProject();
			} catch (Exception e1) {
				auroraProject = null;
			}
			if (auroraProject == null) {
				btn.setEnabled(false);
				setErrorMessage("当前工程没有正确设置关联Aurora工程.");
				setPageComplete(false);
				return;
			}
			btn.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					@SuppressWarnings("restriction")
					OpenResourceDialog ord = new OpenResourceDialog(
							composite_right.getShell(), auroraProject,
							OpenResourceDialog.CARET_BEGINNING);
					ord.setInitialPattern("*.screen");
					ord.open();
					Object obj = ord.getFirstResult();
					if (!(obj instanceof IFile)) {
						setPageComplete(false);
						setErrorMessage("the selection is not a valid screen file");
						return;
					}
					setPageComplete(true);
					setErrorMessage(null);
					IFile file = (IFile) obj;
					IPath path = file.getFullPath();
					IContainer web = Util.findWebInf(file).getParent();
					path = path.makeRelativeTo(web.getFullPath());
					tmpOpenPath = path.toString();
					text.setText(tmpOpenPath);
				}

				public void widgetDefaultSelected(SelectionEvent e) {

				}

			});
		}

		private void create_1() {
			composite_right.setLayout(new GridLayout(1, false));
			final Label l = new Label(composite_right, SWT.WRAP);
			l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
					false));
			final List list = new List(composite_right, SWT.BORDER);
			list.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
					true));
			list.setItems(Renderer.INNER_FUNCTIONS);

			list.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					int idx = list.getSelectionIndex();
					l.setText(Renderer.INNER_RENDERER_DESC[idx]);
					tmpFunctionName = Renderer.INNER_FUNCTIONS[idx];
					setErrorMessage(null);
					setPageComplete(true);
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			tmpFunctionName = renderer.getFunctionName();
			for (int i = 0; i < Renderer.INNER_FUNCTIONS.length; i++) {
				if (Renderer.INNER_FUNCTIONS[i].equals(tmpFunctionName)) {
					list.select(i);
					l.setText(Renderer.INNER_RENDERER_DESC[i]);
					break;
				}
			}
			if (list.getSelection().length == 0) {
				setErrorMessage("please select a renderer funtion");
				setPageComplete(false);
			}
		}

		private void create_2() {
			composite_right.setLayout(new GridLayout(1, false));
			Label l = new Label(composite_right, SWT.NONE);
			l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
					false));
			l.setText("在下面写一个函数");
			final SourceViewer jsEditor = new SourceViewer(composite_right,
					null, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
			jsEditor.getControl().setLayoutData(
					new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			jsEditor.configure(new JavaScriptConfiguration(new ColorManager()));
			jsEditor.getTextWidget().setFont(new Font(null, "Consolas", 10, 0));
			Document document = new Document();
			jsEditor.setDocument(document);
			tmpFunction = renderer.getFunction();
			if (tmpFunction == null || tmpFunction.trim().length() == 0) {
				tmpFunction = Renderer.FUNCTION_MODEL;
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
						tmpRendererType = Renderer.RENDERER_TYPES[i];
						radios[i].setBackground(SELECTION_BG);
						setDescription(displayTexts[i]);
					} else
						radios[i].setBackground(radios[i].getParent()
								.getBackground());
				}
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}
}

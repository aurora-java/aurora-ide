package aurora.ide.meta.gef.editors.property;

import org.eclipse.core.resources.IProject;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.textpage.ColorManager;
import aurora.ide.editor.textpage.JavaScriptConfiguration;
import aurora.ide.meta.gef.editors.composite.IPathChangeListener;
import aurora.ide.meta.gef.editors.composite.ScreenUIPBrowseButton;
import aurora.ide.meta.gef.editors.figures.ColorConstants;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Renderer;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.wizard.dialog.ParameterComposite;
import aurora.ide.meta.project.AuroraMetaProject;

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
	private ParameterComposite pc;

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
		page = new InnerPage("renderer"); //$NON-NLS-1$
		addPage(page);
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		renderer.setRendererType(tmpRendererType);
		renderer.setOpenPath(tmpOpenPath);
		renderer.getParameters().clear();
		renderer.getParameters().addAll(pc.getParameters());

		renderer.setLabelText(tmpLabelText);
		renderer.setFunctionName(tmpFunctionName);
		renderer.setFunction(tmpFunction);
		return true;
	}

	private class InnerPage extends WizardPage implements SelectionListener {
		private String[] displayTexts = { Messages.RendererEditDialog_2,
				Messages.RendererEditDialog_3, Messages.RendererEditDialog_4,
				Messages.RendererEditDialog_5 };

		protected InnerPage(String pageName) {
			super(pageName);
			setTitle(Messages.RendererEditDialog_6);
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
				radios[0].notifyListeners(SWT.Selection, null);
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
			else if (idx == 2)
				create_2();
			else
				create_3();
			composite_right.layout();
		}

		private void create_0() {
			composite_right.setLayout(new FillLayout());
			Label l = new Label(composite_right, SWT.CENTER);
			l.setForeground(ColorConstants.EDITOR_BORDER);
			l.setText(Messages.RendererEditDialog_7);
		}

		private void create_1() {
			composite_right.setLayout(new GridLayout(3, false));
			Label label = new Label(composite_right, SWT.NONE);
			label.setText(Messages.RendererEditDialog_8);
			final Text text1 = new Text(composite_right, SWT.BORDER);
			text1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
					1, 1));
			tmpLabelText = renderer.getLabelText();
			if (tmpLabelText == null)
				tmpLabelText = ""; //$NON-NLS-1$

			text1.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					tmpLabelText = text1.getText();
				}
			});
			text1.setText(tmpLabelText);
			new Label(composite_right, SWT.NONE);
			label = new Label(composite_right, SWT.NONE);
			label.setText(Messages.RendererEditDialog_10);
			final Text text = new Text(composite_right, SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
					1, 1));
			tmpOpenPath = renderer.getOpenPath();
			if (tmpOpenPath == null || tmpOpenPath == "") { //$NON-NLS-1$
				tmpOpenPath = ""; //$NON-NLS-1$
				setPageComplete(false);
				setErrorMessage(Messages.RendererEditDialog_13);
			}
			text.setText(tmpOpenPath);
			text.setEditable(false);
			ScreenUIPBrowseButton btn = new ScreenUIPBrowseButton(
					composite_right, SWT.FLAT);
			btn.setText(Messages.RendererEditDialog_14);

			IProject proj = AuroraPlugin.getActiveIFile().getProject();
			AuroraMetaProject mProj = new AuroraMetaProject(proj);
			try {
				auroraProject = mProj.getAuroraProject();
			} catch (Exception e1) {
				auroraProject = null;
			}
			if (auroraProject == null) {
				btn.setEnabled(false);
				setErrorMessage(Messages.RendererEditDialog_15);
				setPageComplete(false);
				return;
			}
			btn.setAuroraProject(auroraProject);
			btn.addListener(new IPathChangeListener() {
				public void pathChanged(String openPath) {
					if (openPath != null) {
						setPageComplete(true);
						setErrorMessage(null);
						tmpOpenPath = openPath;
						text.setText(tmpOpenPath);
					}
				}
			});

			// btn.addSelectionListener(new SelectionListener() {
			//
			// public void widgetSelected(SelectionEvent e) {
			// MutilInputResourceSelector fss = new
			// MutilInputResourceSelector(composite_right
			// .getShell());
			// String webHome = ResourceUtil.getWebHome(auroraProject);
			// IResource res = ResourcesPlugin.getWorkspace().getRoot()
			// .findMember(webHome);
			// fss.setExtFilter(new String[] { "screen","uip" });
			// IContainer uipFolder = getUIPFolder();
			// fss.setInputs(new IContainer[] { (IContainer) res, uipFolder });
			// Object obj = fss.getSelection();
			// if (!(obj instanceof IFile)) {
			// return;
			// }
			// setPageComplete(true);
			// setErrorMessage(null);
			// IFile file = (IFile) obj;
			// IPath path = file.getFullPath();
			// IContainer web = Util.findWebInf(file).getParent();
			// path = path.makeRelativeTo(web.getFullPath());
			// tmpOpenPath = path.toString();
			// text.setText(tmpOpenPath);
			// }
			//
			// public void widgetDefaultSelected(SelectionEvent e) {
			//
			// }
			//
			// });
			createParaTable(composite_right);
		}

		// public IContainer getUIPFolder() {
		// IFile activeIFile = AuroraPlugin.getActiveIFile();
		// IProject proj = activeIFile.getProject();
		// AuroraMetaProject mProj = new AuroraMetaProject(proj);
		// try {
		// return mProj.getScreenFolder();
		// } catch (ResourceNotFoundException e) {
		// e.printStackTrace();
		// }
		// return mProj.getProject();
		// }

		private void createParaTable(Composite composite_right) {
			AuroraComponent comp = (AuroraComponent) renderer.getContextInfo();
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
			pc = new ParameterComposite(root, composite_right, SWT.NONE,
					(AuroraComponent) renderer.getContextInfo());
			pc.setLayoutData(data);
			pc.setParameters(renderer.getParameters());
		}

		private void create_2() {
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
				setErrorMessage(Messages.RendererEditDialog_18);
				setPageComplete(false);
			}
		}

		private void create_3() {
			composite_right.setLayout(new GridLayout(1, false));
			Label l = new Label(composite_right, SWT.NONE);
			l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
					false));
			l.setText(Messages.RendererEditDialog_19);
			final SourceViewer jsEditor = new SourceViewer(composite_right,
					null, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
			jsEditor.getControl().setLayoutData(
					new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			jsEditor.configure(new JavaScriptConfiguration(new ColorManager()));
			jsEditor.getTextWidget().setFont(new Font(null, "Consolas", 10, 0)); //$NON-NLS-1$
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

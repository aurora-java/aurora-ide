package aurora.ide.meta.gef.editors.property;

import java.util.Arrays;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
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

import aurora.ide.editor.textpage.ColorManager;
import aurora.ide.editor.textpage.JavaScriptConfiguration;
import aurora.ide.meta.gef.editors.figures.ColorConstants;
import aurora.plugin.source.gen.screen.model.FootRenderer;

public class FootRendererEditDialog extends EditWizard {

	private static final Color SELECTION_BG = new Color(null, 109, 187, 242);
	private Button[] radios = new Button[FootRenderer.FOOTRENDERER_TYPES.length];
	private Composite composite_right;
	private FootRenderer renderer;
	private String tmpFunction;

	private String tmpRendererType;
	private InnerPage page;

	public FootRendererEditDialog() {
		super();
		setWindowTitle("footRenderer");
	}

	@Override
	public void setDialogEdiableObject(IDialogEditableObject obj) {
		renderer = (FootRenderer) obj;
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
		renderer.setFunction(tmpFunction);
		return true;
	}

	private class InnerPage extends WizardPage implements SelectionListener {
		private String[] displayTexts = { "不使用", "纯文本", "列求和", "自定义" };

		protected InnerPage(String pageName) {
			super(pageName);
			setTitle("footRenderer详细设置");
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

			for (int i = 0; i < radios.length; i++) {
				radios[i] = new Button(composite_left, SWT.RADIO);
				radios[i].setText(displayTexts[i]);
				radios[i].addSelectionListener(this);
				tmpRendererType = renderer.getRendererType();
				if (FootRenderer.FOOTRENDERER_TYPES[i].equals(tmpRendererType)) {
					radios[i].setSelection(true);
					radios[i].setBackground(SELECTION_BG);
					createRight(i);
				}
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
			else
				create_3(idx);
			composite_right.layout();
		}

		private void create_0() {
			composite_right.setLayout(new FillLayout());
			Label l = new Label(composite_right, SWT.CENTER);
			l.setForeground(ColorConstants.EDITOR_BORDER);
			l.setText("不使用footRenderer");
		}

		private void create_3(final int idx) {
			composite_right.setLayout(new GridLayout(2, false));
			Label l = new Label(composite_right, SWT.NONE);
			l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
					false));
			l.setText(Messages.RendererEditDialog_19);
			Button resetBtn = new Button(composite_right, SWT.NONE);
			resetBtn.setText("重置");

			resetBtn.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
					false, false));
			final SourceViewer jsEditor = new SourceViewer(composite_right,
					null, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
			jsEditor.getControl().setLayoutData(
					new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
			jsEditor.configure(new JavaScriptConfiguration(new ColorManager()));
			jsEditor.getTextWidget().setFont(new Font(null, "Consolas", 10, 0)); //$NON-NLS-1$
			resetBtn.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					tmpFunction = FootRenderer.function_models[idx];
					jsEditor.getTextWidget().setText(tmpFunction);
				}
			});
			Document document = new Document();
			jsEditor.setDocument(document);
			tmpFunction = renderer.getFunction();
			boolean isCurrentType = Arrays.asList(
					FootRenderer.FOOTRENDERER_TYPES).indexOf(
					renderer.getRendererType()) == idx;
			if (tmpFunction == null || tmpFunction.trim().length() == 0
					|| !isCurrentType) {
				tmpFunction = FootRenderer.function_models[idx];
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
						tmpRendererType = FootRenderer.FOOTRENDERER_TYPES[i];
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

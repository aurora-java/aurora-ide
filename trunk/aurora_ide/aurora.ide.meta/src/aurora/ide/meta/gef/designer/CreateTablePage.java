package aurora.ide.meta.gef.designer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import aurora.ide.editor.textpage.ColorManager;
import aurora.ide.editor.textpage.SQLConfiguration;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.designer.gen.SqlGenerator;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.project.AuroraMetaProject;

public class CreateTablePage extends FormPage {

	public static final String java_editor_font_key = "org.eclipse.jdt.ui.editors.textfont";

	private BMModel model;
	private IProject aProj;

	private StyledText styledText;

	/**
	 * Create the form page.
	 * 
	 * @param id
	 * @param title
	 */
	public CreateTablePage(String id, String title) {
		super(id, title);

	}

	/**
	 * Create the form page.
	 * 
	 * @param editor
	 * @param id
	 * @param title
	 */
	public CreateTablePage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	public void setModel(BMModel model) {
		this.model = model;
	}

	public void refresh() {
		IFile inputFile = (IFile) getEditor().getAdapter(IFile.class);
		String name = inputFile.getName();
		int idx = name.indexOf('.');
		if (idx != -1)
			name = name.substring(0, idx);
		String sql = new SqlGenerator(model, name).gen();
		if (!sql.equals(styledText.getText())) {
			styledText.setText(sql);
			ScrolledForm form = getManagedForm().getForm();
			Point size = form.getSize();
			form.pack();
			form.setSize(size);
		}
		styledText.forceFocus();
	}

	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		if (active)
			refresh();
	}

	/**
	 * Create contents of the form.
	 * 
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm sform = managedForm.getForm();
		sform.setLayout(new FillLayout(SWT.HORIZONTAL));
		sform.setText("SQL Source Code");
		toolkit.decorateFormHeading(sform.getForm());
		createActions(sform.getToolBarManager());

		Composite body = sform.getBody();
		body.setLayout(new FillLayout(SWT.HORIZONTAL));
		SourceViewer sourceViewer = new SourceViewer(body, null, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		sourceViewer.configure(new SQLConfiguration(new ColorManager()));
		Document document = new Document();
		sourceViewer.setDocument(document);
		styledText = sourceViewer.getTextWidget();
		toolkit.paintBordersFor(styledText);
		setFont();
	}

	private void setFont() {
		@SuppressWarnings("deprecation")
		IPreferenceStore pStore = PlatformUI.getWorkbench()
				.getPreferenceStore();
		pStore.addPropertyChangeListener(new IPropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(java_editor_font_key)) {
					String str = event.getNewValue().toString();
					FontData[] fds = PreferenceConverter.basicGetFontData(str);
					styledText.setFont(new Font(null, fds));
				}
			}
		});
		String str = pStore.getString(java_editor_font_key);
		FontData[] fds = PreferenceConverter.basicGetFontData(str);
		styledText.setFont(new Font(null, fds));
	}

	private void createActions(IToolBarManager tbm) {
		IFile file = (IFile) getEditor().getAdapter(IFile.class);
		try {
			AuroraMetaProject amp = new AuroraMetaProject(file.getProject());
			aProj = amp.getAuroraProject();
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		}
		final Action forceAction = new Action("force override",
				Action.AS_CHECK_BOX) {
			{
				setImageDescriptor(ImagesUtils
						.getImageDescriptor("lightning_plus.png"));
			}
		};
		tbm.add(new CreateTableAction(aProj) {

			@Override
			public String getSQL() {
				return styledText.getText();
			}

			@Override
			public boolean isForce() {
				return forceAction.isChecked();
			}

		});
		tbm.add(forceAction);
		tbm.update(true);
	}
}

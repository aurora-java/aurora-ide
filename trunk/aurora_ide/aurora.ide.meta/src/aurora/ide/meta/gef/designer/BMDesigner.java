package aurora.ide.meta.gef.designer;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import uncertain.composite.CompositeMap;
import aurora.ide.editor.InputFileListener;
import aurora.ide.meta.gef.designer.gen.BaseBmGenerator;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.ModelMerger;
import aurora.ide.meta.gef.designer.model.ModelUtil;

public class BMDesigner extends FormEditor {

	public static final String ID = "aurora.ide.meta.gef.editors.BMDesigner"; //$NON-NLS-1$
	private IFile inputFile;
	private BMModel model;
	private BMDesignPage dpage = new BMDesignPage(this,
			DesignerMessages.BMDesigner_0, DesignerMessages.BMDesigner_1);
	private CreateTablePage epage = new CreateTablePage(this,
			DesignerMessages.BMDesigner_2, DesignerMessages.BMDesigner_3);

	public BMDesigner() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				new InputFileListener(this));
	}

	@Override
	protected void addPages() {
		try {
			addPage(dpage);
			addPage(epage);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			CompositeMap map = ModelUtil.toCompositeMap(model);
			String xml = BaseBmGenerator.xml_header + map.toXML();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out,
					"UTF-8"));
			bw.write(xml);
			bw.close();
			inputFile.setContents(new ByteArrayInputStream(out.toByteArray()),
					true, false, monitor);
			out.close();
			dpage.setDirty(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs() {
		dpage.setDirty(false);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		IFileEditorInput fi = (IFileEditorInput) input;
		this.inputFile = fi.getFile();

		setPartName(inputFile.getName());
		open();
	}

	private void open() {
		model = new ModelMerger(inputFile).getMergedModel();
		if (model == null)
			model = new BMModel();
		model.setTitle(DesignerMessages.BMDesigner_5);
		dpage.setModel(model);
		epage.setModel(model);
	}

	public IFile getInputFile() {
		return inputFile;
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (IFile.class.equals(adapter))
			return getInputFile();
		return super.getAdapter(adapter);
	}
}

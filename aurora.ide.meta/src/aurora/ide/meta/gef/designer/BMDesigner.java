package aurora.ide.meta.gef.designer;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;

import uncertain.composite.CommentCompositeMap;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;
import aurora.ide.meta.gef.designer.gen.BmGenerator;
import aurora.ide.meta.gef.designer.gen.SqlGenerator;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.ModelUtil;

public class BMDesigner extends FormEditor {

	public static final String ID = "aurora.ide.meta.gef.editors.BMDesigner"; //$NON-NLS-1$
	private IFile inputFile;
	private BMModel model;
	private BMDesignPage dpage = new BMDesignPage(this, "111", "设计");
	private BMSourcePage spage = new BMSourcePage(this, "222", "代码");

	public BMDesigner() {
	}

	@Override
	protected void addPages() {
		try {
			addPage(dpage);
			addPage(spage);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			CompositeMap map = ModelUtil.toCompositeMap(model);
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
					+ map.toXML();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out,
					"UTF-8"));
			bw.write(xml);
			bw.close();
			inputFile.setContents(new ByteArrayInputStream(out.toByteArray()),
					true, false, monitor);
			out.close();
			String fn = inputFile.getName();
			fn = fn.split("\\.")[0];
			System.out.println(new SqlGenerator(model, fn).gen());
			new BmGenerator(model, fn).gen();
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
		InputStream is = null;
		CompositeMapParser parser = new CompositeMapParser(
				new CompositeLoader());
		CompositeMap map = null;
		try {
			is = inputFile.getContents();
			map = parser.parseStream(is);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (map == null)
				map = new CommentCompositeMap();
		}
		//
		model = getModel(map);
		if (model == null)
			model = new BMModel();
		model.setTitle("title");
		dpage.setModel(model);
	}

	private BMModel getModel(CompositeMap map) {
		return ModelUtil.fromCompositeMap(map);
	}

	public IFile getInputFile() {
		return inputFile;
	}

	@Override
	protected void createPages() {
		// TODO Auto-generated method stub
		super.createPages();
	}

	@Override
	public int addPage(IFormPage page) throws PartInitException {
		// TODO Auto-generated method stub
		return super.addPage(page);
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (IFile.class.equals(adapter))
			return getInputFile();
		return super.getAdapter(adapter);
	}

}

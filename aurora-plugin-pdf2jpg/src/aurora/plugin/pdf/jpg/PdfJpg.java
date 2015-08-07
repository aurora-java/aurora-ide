package aurora.plugin.pdf.jpg;

import java.util.List;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceContext;

public class PdfJpg extends AbstractEntry {
	private String savePath = null;

	private String pdfPath = null;

	private String jpgName = null;

	private IObjectRegistry registry;

	public PdfJpg(IObjectRegistry registry) {
		this.registry = registry;
		// uncertainEngine = (UncertainEngine)
		// registry.getInstanceOfType(UncertainEngine.class);
	}

	private String getValue(String s, CompositeMap model, String name) {
		String templateName = s;
		if (templateName != null)
			templateName = uncertain.composite.TextParser.parse(templateName,
					model);
		if (templateName == null)
			throw new IllegalArgumentException(name + " can not be null!");
		return templateName;
	}

	@Override
	public void run(ProcedureRunner runner) throws Exception {

		CompositeMap context = runner.getContext();
		ServiceContext service = ServiceContext.createServiceContext(context);
		CompositeMap model = service.getModel();
		String _pdfPath = getValue(pdfPath, model, "pdfPath");
		String _savePath = getValue(savePath, model, "savePath");
		String _jpgName = getValue(jpgName, model, "jpgName");
		

		List<String> jpgs = Pdf2Jpg.setup(_pdfPath, _savePath, _jpgName);

		CompositeMap JPG_S = model.createChild("JPG_S");
		JPG_S.put("size", jpgs.size());
		for (String string : jpgs) {
			JPG_S.createChild("JPG").put("path", string);
		}
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}

	public String getJpgName() {
		return jpgName;
	}

	public void setJpgName(String jpgName) {
		this.jpgName = jpgName;
	}

}

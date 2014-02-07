package aurora.ide.prototype.consultant.product.fsd;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.meta.docx4j.docx.util.Docx4jUtil;
import aurora.ide.meta.gef.util.PathUtil;
import aurora.ide.prototype.consultant.product.fsd.wizard.FSDContentControl;
import aurora.ide.swt.util.PageModel;

public class ExportProjectFSDProgress implements IRunnableWithProgress {

	private List<String> functions;
	private PageModel function;
	private String savePath;
	private boolean onlySaveLogic = false;

	public ExportProjectFSDProgress(String savePath, PageModel fun,
			List<String> functions) {
		this.savePath = savePath;
		this.function = fun;
		this.functions = functions;
	}

	public ExportProjectFSDProgress(String savePath, PageModel fun,
			List<String> functions, boolean onlySaveLogic) {
		this(savePath, fun, functions);
		this.onlySaveLogic = onlySaveLogic;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask("Export FSD", 150); //$NON-NLS-1$
		List<FunctionDesc> functions = createFunctions();
		boolean save = true;
		try {
			monitor.setTaskName(Messages.ExportFSDProgress_1);
			FSDDocumentPackage pkg = new FSDDocumentPackage();
			monitor.worked(10);
			pkg.create();
			monitor.worked(30);
			FirstPage page1 = new FirstPage(pkg, FunctionDesc.create(function));
			page1.create();
			monitor.worked(10);
			Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
			SecondPage page2 = new SecondPage(pkg);
			page2.create();
			monitor.worked(10);

			Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
			ContextPage page3 = new ContextPage(pkg);
			page3.create();

			monitor.worked(10);

			for (FunctionDesc fdd : functions) {
				List<String> propertyValue = (List<String>) fdd
						.getPropertyValue(FSDContentControl.FSD_TABLE_INPUT);
				FunctionFSDPackage fff = new FunctionFSDPackage(pkg, fdd,
						propertyValue, onlySaveLogic);
				fff.run(monitor);
			}

			// Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
			// FourthPage page4 = new FourthPage(function,pkg);
			// page4.create();
			// monitor.worked(10);
			// Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
			// FifthPage page5 = new FifthPage(pkg, function);
			// page5.create();
			// monitor.worked(10);
			// monitor.setTaskName(Messages.ExportFSDProgress_2);
			// Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
			// BlockedContentPage page6 = new BlockedContentPage(pkg, files);
			// if (onlySaveLogic)
			// page6.setOnlyLogic(onlySaveLogic);
			// page6.create();
			// monitor.worked(10);

			// System.out.println(XmlUtils.marshaltoString(pkg.getMainDocumentPart()
			// .getJaxbElement(), true, true));

			// Optionally save it'
			if (save) {
				// String filename = "/Users/shiliyan/Desktop"
				// + "/OUT_CopyStyles.docx";
				String filename = savePath;
				monitor.setTaskName(Messages.ExportFSDProgress_3 + filename);
				pkg.getWordMLPackage().save(new java.io.File(filename));
				System.out.println("Saved " + filename); //$NON-NLS-1$
				monitor.done();
			}
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		} finally {
			monitor.done();
		}

	}

	private List<String> getFunctionFiles(CompositeMap map,String base) {

		CompositeMap fileschild = map
				.getChild(FSDContentControl.FSD_TABLE_INPUT);
		String code = fileschild == null ? "" : fileschild.getText();
		code = code == null ? "" : code;
		String[] split = code.split(",");
		List<String> ss = new ArrayList<String>();
		for (String s : split) {
			if (s != null && "".equals(s) == false) {
				ss.add(PathUtil.makeAbsolute(s, base).toString());
			}
		}
		return ss;
	}

	private List<FunctionDesc> createFunctions() {
		List<FunctionDesc> r = new ArrayList<FunctionDesc>();

		for (String ff : functions) {
			Path p = new Path(ff);
			File file = p.toFile();
			if (file.exists() == false)
				continue;
			CompositeMap loadFile = CompositeMapUtil.loadFile(file);
			CompositeMap namechild = loadFile.getChild(FunctionDesc.fun_name);
			String name = namechild == null ? "" : namechild.getText();
			name = name == null ? "" : name;
			CompositeMap codechild = loadFile.getChild(FunctionDesc.fun_code);
			String code = codechild == null ? "" : codechild.getText();
			code = code == null ? "" : code;
			FunctionDesc create = FunctionDesc.create(function);
			create.setPropertyValue(FunctionDesc.fun_name, name);
			create.setPropertyValue(FunctionDesc.fun_code, code);
			create.setPropertyValue(FSDContentControl.FSD_TABLE_INPUT,
					getFunctionFiles(loadFile,p.removeLastSegments(1).toString()));
			r.add(create);
		}
		return r;
	}

}

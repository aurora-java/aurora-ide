package aurora.ide.prototype.consultant.product.fsd;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import aurora.ide.meta.docx4j.docx.util.Docx4jUtil;

public class ExportFSDProgress implements IRunnableWithProgress {

	private List<String> files;
	private FunctionDesc function;
	private String savePath;
	private boolean onlySaveLogic = false;

	public ExportFSDProgress(String savePath, FunctionDesc fun,
			List<String> files) {
		this.savePath = savePath;
		this.function = fun;
		this.files = files;
	}

	public ExportFSDProgress(String savePath, FunctionDesc fun,
			List<String> files, boolean onlySaveLogic) {
		this(savePath, fun, files);
		this.onlySaveLogic = onlySaveLogic;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask("Export FSD", 150); //$NON-NLS-1$

		boolean save = true;
		try {
			monitor.setTaskName(Messages.ExportFSDProgress_1);
			FSDDocumentPackage pkg = new FSDDocumentPackage();
			monitor.worked(10);
			pkg.create();
			monitor.worked(30);
			FirstPage page1 = new FirstPage(pkg, function);
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

			FunctionFSDPackage fff = new FunctionFSDPackage(pkg,function,files,onlySaveLogic);
			fff.run(monitor);
			
			
//			Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
//			FourthPage page4 = new FourthPage(function,pkg);
//			page4.create();
//			monitor.worked(10);
//			Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
//			FifthPage page5 = new FifthPage(pkg, function);
//			page5.create();
//			monitor.worked(10);
//			monitor.setTaskName(Messages.ExportFSDProgress_2);
//			Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
//			BlockedContentPage page6 = new BlockedContentPage(pkg, files);
//			if (onlySaveLogic)
//				page6.setOnlyLogic(onlySaveLogic);
//			page6.create();
//			monitor.worked(10);
			
			
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

}

package aurora.ide.prototype.consultant.product.fsd;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.docx4j.XmlUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import aurora.ide.meta.docx4j.docx.util.Docx4jUtil;

public class ExportFSDProgress implements IRunnableWithProgress {

	private List<String> files;
	private FunctionDesc function;
	private String savePath;

	public ExportFSDProgress(String savePath, FunctionDesc fun,
			List<String> files) {
		this.savePath = savePath;
		this.function = fun;
		this.files = files;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask("Export FSD", 150);

		boolean save = true;
		try {
			monitor.setTaskName("初始化");
			FSDDocumentPackage pkg = new FSDDocumentPackage();
			monitor.worked(10);
			pkg.create();
			monitor.worked(30);
			FirstPage page1 = new FirstPage(pkg,function);
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
			
			Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
			FourthPage page4 = new FourthPage(pkg);
			page4.create();
			monitor.worked(10);
			Docx4jUtil.createNewPage(pkg.getMainDocumentPart()); 
			FifthPage page5 = new FifthPage(pkg,function);
			page5.create();
			monitor.worked(10);
			monitor.setTaskName("生成图片");
			Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
			ContentPage page6 = new ContentPage(pkg,files);
			page6.create();
			monitor.worked(10);
//			System.out.println(XmlUtils.marshaltoString(pkg.getMainDocumentPart()
//					.getJaxbElement(), true, true));
			
			// Optionally save it'
			if (save) {
//				String filename = "/Users/shiliyan/Desktop"
//						+ "/OUT_CopyStyles.docx";
				String filename = savePath;
				monitor.setTaskName("保存:"+filename);
				pkg.getWordMLPackage().save(new java.io.File(filename));
				System.out.println("Saved " + filename);
				monitor.done();
			}
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}finally{
			monitor.done();
		}


	}

}

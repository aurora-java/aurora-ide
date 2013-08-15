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


		boolean save = true;
		try {
			FSDDocumentPackage pkg = new FSDDocumentPackage();
			pkg.create();
			FirstPage page1 = new FirstPage(pkg,function);
			page1.create();
			
			Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
			SecondPage page2 = new SecondPage(pkg);
			page2.create();
			
			Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
			FourthPage page4 = new FourthPage(pkg);
			page4.create();
			
			Docx4jUtil.createNewPage(pkg.getMainDocumentPart()); 
			FifthPage page5 = new FifthPage(pkg,function);
			page5.create();
			
			Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
			ContentPage page6 = new ContentPage(pkg,files);
			page6.create();
			
			System.out.println(XmlUtils.marshaltoString(pkg.getMainDocumentPart()
					.getJaxbElement(), true, true));
			
			// Optionally save it'
			if (save) {
//				String filename = "/Users/shiliyan/Desktop"
//						+ "/OUT_CopyStyles.docx";
				String filename = savePath;
				pkg.getWordMLPackage().save(new java.io.File(filename));
				System.out.println("Saved " + filename);
			}
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}


	}

}

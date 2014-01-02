package aurora.ide.prototype.consultant.product.fsd;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import aurora.ide.meta.docx4j.docx.util.Docx4jUtil;

public class FunctionFSDPackage  implements IRunnableWithProgress{
	private FSDDocumentPackage pkg;
	private FunctionDesc function;
	private boolean onlySaveLogic;
	private List<String> files;


	public FunctionFSDPackage(FSDDocumentPackage pkg,FunctionDesc function,List<String> files, boolean onlySaveLogic){
		this.pkg = pkg;
		this.function = function;
		this.files = files;
		this.onlySaveLogic = onlySaveLogic;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
		FourthPage page4 = new FourthPage(function,pkg);
		page4.create();
		monitor.worked(10);
		Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
		FifthPage page5 = new FifthPage(pkg, function);
		page5.create();
		monitor.worked(10);
		monitor.setTaskName(Messages.ExportFSDProgress_2);
		Docx4jUtil.createNewPage(pkg.getMainDocumentPart());
		BlockedContentPage page6 = new BlockedContentPage(pkg, files);
		if (onlySaveLogic)
			page6.setOnlyLogic(onlySaveLogic);
		page6.create();
		monitor.worked(10);
	}

}

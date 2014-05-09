package aurora.sql.java.ide.builder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.ide.undo.CreateFileOperation;

import sqlj.ast.AstTransform;
import sqlj.core.ParsedSource;
import sqlj.parser.SqljParser;

public class CreateJavaFileAction extends Action {

	public void run(IFile sqljFile,IFile javaFile) throws InvocationTargetException,
			InterruptedException {

		File source = sqljFile.getLocation().toFile();
		FileInputStream fis;
		try {
			fis = new FileInputStream(source);
			byte[] b = new byte[(int) source.length()];
			fis.read(b);
			fis.close();
			String sqlj = new String(b, "UTF-8");
			SqljParser parser = new SqljParser(sqlj);
			ParsedSource ps = parser.parse();
			AstTransform trans = new AstTransform(ps);
			String str = trans.tranform();
			InputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
			javaFile.delete(true, null);
			final CreateFileOperation cfo = new CreateFileOperation(javaFile, null, is,
					"Create Java File.");
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					try {
						cfo.execute(monitor
								, null);
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			};
			op.run(null);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}

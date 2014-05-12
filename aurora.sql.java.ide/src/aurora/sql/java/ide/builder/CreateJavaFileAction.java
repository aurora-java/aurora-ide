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
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.ide.undo.CreateFileOperation;

import sqlj.ast.AstTransform;
import sqlj.core.ParsedSource;
import sqlj.exception.ParserException;
import sqlj.exception.TransformException;
import sqlj.parser.SqljParser;

public class CreateJavaFileAction extends Action {
	private static String MARKER_TYPE = "org.eclipse.core.resources.problemmarker";

	public void run(IFile sqljFile, IFile javaFile)
			throws InvocationTargetException, InterruptedException {

		File source = sqljFile.getLocation().toFile();
		FileInputStream fis;
		try {
			sqljFile.deleteMarkers(MARKER_TYPE, true, 0);
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
			final CreateFileOperation cfo = new CreateFileOperation(javaFile,
					null, is, "Create Java File.");
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					try {
						cfo.execute(monitor, null);
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
		} catch (ParserException e) {
			try {
				IMarker m = sqljFile.createMarker(MARKER_TYPE);
				m.setAttribute(IMarker.CHAR_START, e.getStart());
				m.setAttribute(IMarker.CHAR_END, e.getEnd());
				m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				m.setAttribute(IMarker.MESSAGE, e.getMessage());
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		} catch (TransformException e) {
			try {
				for (IProblem p : e.problems) {
					IMarker m = sqljFile.createMarker(MARKER_TYPE);
					m.setAttribute(IMarker.CHAR_START, p.getSourceStart());
					m.setAttribute(IMarker.CHAR_END, p.getSourceEnd() + 1);
					m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					m.setAttribute(IMarker.MESSAGE, p.getMessage());
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

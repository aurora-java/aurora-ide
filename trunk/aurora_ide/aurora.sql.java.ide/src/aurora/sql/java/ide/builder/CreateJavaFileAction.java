package aurora.sql.java.ide.builder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import net.sf.jsqlparser.JSQLParserException;

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
import sqlj.core.SqljBlock;
import sqlj.exception.ParserException;
import sqlj.exception.TransformException;
import sqlj.parser.SqljParser;
import aurora.sql.java.sqlparser.SqlParser;

public class CreateJavaFileAction extends Action {
	private static String MARKER_TYPE = "aurora.sql.java.ide.xmlProblem";

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

			int m = ps.getSqljBlockSize();
			for (int i = 0; i < m; i++) {
				SqljBlock sqlBlock = ps.getSqlById(i);
				String s = sqlBlock.getParsedSql().toStringLiteral();
				try {
					checkSQL(s);
				} catch (JSQLParserException e) {
//					e.printStackTrace();
					createMarker(sqljFile,sqlBlock.getBodyStartIdx(),
							sqlBlock.getBodyEndIdx() + 1, e.getCause().getMessage());
				}
			}

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
			createMarker(sqljFile, e);
		} catch (TransformException e) {
			for (IProblem p : e.problems) {
				// IMarker m = sqljFile.createMarker(MARKER_TYPE);
				// m.setAttribute(IMarker.CHAR_START, p.getSourceStart());
				// m.setAttribute(IMarker.CHAR_END, p.getSourceEnd() + 1);
				// m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				// m.setAttribute(IMarker.MESSAGE, p.getMessage());
				createMarker(sqljFile, p.getSourceStart(),
						p.getSourceEnd() + 1, p.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createMarker(IFile sqljFile, ParserException e) {
		// IMarker m = sqljFile.createMarker(MARKER_TYPE);
		// m.setAttribute(IMarker.CHAR_START, e.getStart());
		// m.setAttribute(IMarker.CHAR_END, e.getEnd());
		// m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		// m.setAttribute(IMarker.MESSAGE, e.getMessage());
		createMarker(sqljFile, e.getStart(), e.getEnd(), e.getMessage());
	}

	public void createMarker(IFile f, int s, int e, String msg) {
		try {
			IMarker m = f.createMarker(MARKER_TYPE);
			m.setAttribute(IMarker.CHAR_START, s);
			m.setAttribute(IMarker.CHAR_END, s);
			m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			m.setAttribute(IMarker.MESSAGE, msg);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}

	private void checkSQL(String sql) throws JSQLParserException {
		SqlParser.parse(sql);
	}
}

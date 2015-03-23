package aurora.ide.javascript;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.StringLiteral;

import uncertain.composite.CompositeMap;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.editor.textpage.scanners.XMLTagScanner;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.search.core.Util;

public class Javascript4Rhino {
	private String source;
	private AstRoot cu;
	private CompositeMap map;

	private IFile file;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		// this.source = convertJS(source);
		this.source = Util.convertJS(source);
	}

	public String convertJS(String source) {
		Document document = new Document(source);
		XMLTagScanner scanner = Util.getXMLTagScanner();
		StringBuilder sb = new StringBuilder();
		try {
			IToken token = null;
			scanner.setRange(document, 0, document.getLength());
			while ((token = scanner.nextToken()) != Token.EOF) {
				int tokenOffset = scanner.getTokenOffset();
				int tokenLength = scanner.getTokenLength();
				String string = document.get(tokenOffset, tokenLength);
				if (token.getData() instanceof TextAttribute) {
					TextAttribute text = (TextAttribute) token.getData();
					if (text.getForeground().getRGB()
							.equals(IColorConstants.STRING)) {
					} else {
						sb.append(Util.convertJS(string));
						continue;
					}
				}
				sb.append(string);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public CompositeMap getMap() {
		return map;
	}

	public void setMap(CompositeMap map) {
		this.map = map;
	}

	public Javascript4Rhino(IFile file, CompositeMap map) {
		this.file = file;
		this.map = map;
		this.setSource(map.getText());

	}

	public AstRoot createAST(IProgressMonitor monitor) {
		CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
		// 1.73默认false，1.74默认true
		compilerEnvirons.setReservedKeywordAsIdentifier(true);
		Parser p = new Parser(compilerEnvirons);
		try {
			AstRoot parse = p.parse(source == null ? "" : source, "Aurora", 1);
			return parse;
		} catch (Exception e) {
			DialogUtil.logErrorException(e);
			// file.getProjectRelativePath();
			// System.out.println(file.getProjectRelativePath());
			// System.out.println(e.getClass());
			// e.printStackTrace();
		}
		// // modules/wfl/pad/wfl_deliver_for_pad.screen,
		// // L/web/modules/wfl/pad/wfl_notification_window_for_pad.screen
		// // /web/modules/sys/sys_customization_arrays.screen
		//
		// //org.mozilla.javascript.EvaluatorException: missing name after .
		// operator (Aurora#114)
		// e.printStackTrace();
		// if (file != null){
		// System.out.println(file.getProjectRelativePath());
		// if(file.getName().contains("approve")){
		// System.out.println();
		// }
		// }
		//
		// System.out.println(source);
		// System.out.println();
		// }
		return null;
	}

	public List<StringLiteral> getStringLiteralNodes(IProgressMonitor monitor) {
		final List<StringLiteral> nodes = new ArrayList<StringLiteral>();
		AstRoot cu = getJavaScriptUnit(monitor);
		if (cu == null)
			return nodes;
		cu.visitAll(new NodeVisitor() {

			public boolean visit(AstNode node) {
				if (node instanceof StringLiteral) {
					// String value = ((StringLiteral) node).getValue();
					nodes.add((StringLiteral) node);
				}
				return true;
			}
		});
		return nodes;
	}

	public AstRoot getJavaScriptUnit(IProgressMonitor monitor) {
		if (cu == null) {
			try {
				cu = createAST(monitor);
			} catch (Exception e) {

			}
		}
		return cu;
	}

	public String getLiteralValue(StringLiteral sl) {
		return sl.getValue();
	}

}

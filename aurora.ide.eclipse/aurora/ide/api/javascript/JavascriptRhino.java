package aurora.ide.api.javascript;

import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.StringLiteral;

public class JavascriptRhino {

	private class FunctionNodeVisitor implements NodeVisitor {
		FunctionNode fNode;

		public boolean visit(AstNode node) {
			if (node instanceof FunctionNode) {
				fNode = (FunctionNode) node;
				return false;
			}
			return true;
		}
	}

	private String source;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public JavascriptRhino(String source) {
		this.setSource(source);

	}

	public AstRoot createAST() {
		Parser p = new Parser();
		AstRoot parse = p.parse(source == null ? "" : source, "line", 1);
		return parse;
	}

	public String getLiteralValue(StringLiteral sl) {
		return sl.getValue();
	}

	public String getFirstFunctionName() {
		try {
			AstRoot ast = createAST();
			FunctionNodeVisitor v = new FunctionNodeVisitor();
			ast.visit(v);
			if (v.fNode != null) {
				return v.fNode.getFunctionName().getString();
			}
		} catch (Exception e) {
		}
		return "";
	}

}

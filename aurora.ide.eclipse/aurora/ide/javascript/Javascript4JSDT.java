package aurora.ide.javascript;

import uncertain.composite.CompositeMap;

//TODO for JSDT source no bug just save
public class Javascript4JSDT {

	private String source;
	// private JavaScriptUnit cu;
	private CompositeMap map;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public CompositeMap getMap() {
		return map;
	}

	public void setMap(CompositeMap map) {
		this.map = map;
	}

	//
	public Javascript4JSDT(CompositeMap map) {
		this.source = map.getText();
		this.map = map;

	}
	//
	// public JavaScriptUnit createAST(IProgressMonitor monitor) {
	// ASTParser parser = ASTParser.newParser(AST.JLS3);
	// parser.setSource(source.toCharArray());
	// JavaScriptUnit cu = (JavaScriptUnit) parser.createAST(monitor);
	// return cu;
	// }
	//
	// public List<StringLiteral> getStringLiteralNodes(IProgressMonitor
	// monitor) {
	// final List<StringLiteral> nodes = new ArrayList<StringLiteral>();
	// JavaScriptUnit cu = getJavaScriptUnit(monitor);
	// if (cu == null)
	// return nodes;
	// cu.accept(new ASTVisitor() {
	// @Override
	// public void preVisit(ASTNode node) {
	// if (node instanceof StringLiteral) {
	// String escapedValue = ((StringLiteral) node)
	// .getEscapedValue();
	// int length = escapedValue.length();
	// if (escapedValue == null || length < 3)
	// return;
	// nodes.add((StringLiteral) node);
	// }
	// }
	// });
	// return nodes;
	// }
	//
	// public JavaScriptUnit getJavaScriptUnit(IProgressMonitor monitor) {
	// if (cu == null) {
	// try {
	// cu = createAST(monitor);
	// } catch (Exception e) {
	//
	// }
	// }
	// return cu;
	// }
	//
	// public int getLineNumber(int startPosition) {
	// JavaScriptUnit javaScriptUnit = this.getJavaScriptUnit(null);
	// if (javaScriptUnit == null)
	// return -1;
	// return javaScriptUnit.getLineNumber(startPosition);
	// }
	//
	// public int getPosition(int line, int col) {
	// return getJavaScriptUnit(null).getPosition(line, col);
	// }
	//
	// public String getLiteralValue(StringLiteral sl) {
	// String escapedValue = sl.getEscapedValue();
	// String substring = escapedValue.substring(1, escapedValue.length() - 1);
	// return substring;
	// }
}

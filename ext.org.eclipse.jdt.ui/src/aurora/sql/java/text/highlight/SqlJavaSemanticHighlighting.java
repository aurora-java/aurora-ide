package aurora.sql.java.text.highlight;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.swt.graphics.RGB;

import ext.org.eclipse.jdt.internal.ui.javaeditor.JavaEditorMessages;
import ext.org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting;
import ext.org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlightings;
import ext.org.eclipse.jdt.internal.ui.javaeditor.SemanticToken;

public class SqlJavaSemanticHighlighting extends SemanticHighlighting {

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
	 */
	@Override
	public String getPreferenceKey() {
		return SemanticHighlightings.TYPE_VARIABLE;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
	 */
	@Override
	public RGB getDefaultDefaultTextColor() {
		return new RGB(100, 70, 50);
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
	 */
	@Override
	public boolean isBoldByDefault() {
		return true;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
	 */
	@Override
	public boolean isItalicByDefault() {
		return false;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
	 */
	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return JavaEditorMessages.SemanticHighlighting_typeVariables;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#consumes(org.eclipse.jdt.internal.ui.javaeditor.SemanticToken)
	 */
	@Override
	public boolean consumes(SemanticToken token) {

		// 1: match types in type parameter lists
		SimpleName name= token.getNode();
		ASTNode node= name.getParent();
		if (node.getNodeType() != ASTNode.SIMPLE_TYPE && node.getNodeType() != ASTNode.TYPE_PARAMETER)
			return false;

		// 2: match generic type variable references
		IBinding binding= token.getBinding();
		return binding instanceof ITypeBinding && ((ITypeBinding) binding).isTypeVariable();
	}
}

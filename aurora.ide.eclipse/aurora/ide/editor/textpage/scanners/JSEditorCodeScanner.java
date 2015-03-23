package aurora.ide.editor.textpage.scanners;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

import aurora.ide.editor.textpage.ColorManager;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.editor.textpage.XMLWhitespaceDetector;


/**
 * A Java code scanner.
 */
public class JSEditorCodeScanner extends RuleBasedScanner {

	private static String[] fgKeywords= { "function","var","abstract", "break", "case", "catch", "class", "continue", "default", "do", "else", "extends", "final", "finally", "for", "if", "implements", "import", "instanceof", "interface", "native", "new", "package", "private", "protected", "public", "return", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "volatile", "while" }; //$NON-NLS-36$ //$NON-NLS-35$ //$NON-NLS-34$ //$NON-NLS-33$ //$NON-NLS-32$ //$NON-NLS-31$ //$NON-NLS-30$ //$NON-NLS-29$ //$NON-NLS-28$ //$NON-NLS-27$ //$NON-NLS-26$ //$NON-NLS-25$ //$NON-NLS-24$ //$NON-NLS-23$ //$NON-NLS-22$ //$NON-NLS-21$ //$NON-NLS-20$ //$NON-NLS-19$ //$NON-NLS-18$ //$NON-NLS-17$ //$NON-NLS-16$ //$NON-NLS-15$ //$NON-NLS-14$ //$NON-NLS-13$ //$NON-NLS-12$ //$NON-NLS-11$ //$NON-NLS-10$ //$NON-NLS-9$ //$NON-NLS-8$ //$NON-NLS-7$ //$NON-NLS-6$ //$NON-NLS-5$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$

	private static String[] fgTypes= { "void", "boolean", "char", "byte", "short", "int", "long", "float", "double" }; //$NON-NLS-1$ //$NON-NLS-5$ //$NON-NLS-7$ //$NON-NLS-6$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-2$

	private static String[] fgConstants= { "false", "null", "true" }; //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$

	/**
	 * Creates a Java code scanner with the given color provider.
	 * 
	 * @param provider the color provider
	 */
	public JSEditorCodeScanner(ColorManager colorManager) {

		IToken keyword= new Token(new TextAttribute(colorManager.getColor(IColorConstants.KEYWORD), null, SWT.BOLD));
		IToken type= new Token(new TextAttribute(colorManager.getColor(IColorConstants.TYPE)));
		IToken string= new Token(new TextAttribute(colorManager.getColor(IColorConstants.STRING)));
		IToken comment= new Token(new TextAttribute(colorManager.getColor(IColorConstants.SINGLE_LINE_COMMENT)));
		IToken other= new Token(new TextAttribute(colorManager.getColor(IColorConstants.DEFAULT)));

		List rules= new ArrayList();

		// Add rule for single line comments.
		rules.add(new EndOfLineRule("//", comment)); //$NON-NLS-1$
		rules.add(new MultiLineRule("/*", "*/", comment)); //$NON-NLS-1$

		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
		rules.add(new SingleLineRule("'", "'", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new XMLWhitespaceDetector()));

		// Add word rule for keywords, types, and constants.
		WordRule wordRule= new WordRule(new JSEditorWordDetector(), other);
		for (int i= 0; i < fgKeywords.length; i++)
			wordRule.addWord(fgKeywords[i], keyword);
		for (int i= 0; i < fgTypes.length; i++)
			wordRule.addWord(fgTypes[i], type);
		for (int i= 0; i < fgConstants.length; i++)
			wordRule.addWord(fgConstants[i], type);
		rules.add(wordRule);

		IRule[] result= new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
	class JSEditorWordDetector implements IWordDetector {

		/* (non-Javadoc)
		 * Method declared on IWordDetector.
		 */
		public boolean isWordPart(char character) {
			return Character.isJavaIdentifierPart(character);
		}
		
		/* (non-Javadoc)
		 * Method declared on IWordDetector.
		 */
		public boolean isWordStart(char character) {
			return Character.isJavaIdentifierStart(character);
		}
	}
}

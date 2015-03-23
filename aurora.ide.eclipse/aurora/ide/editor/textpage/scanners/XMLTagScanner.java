package aurora.ide.editor.textpage.scanners;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

import aurora.ide.editor.textpage.ColorManager;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.editor.textpage.XMLWhitespaceDetector;
import aurora.ide.editor.textpage.rules.AttributeRule;
import aurora.ide.editor.textpage.rules.XMLTagNameRule;






public class XMLTagScanner extends RuleBasedScanner {

	public XMLTagScanner(ColorManager manager) {
		IToken string =
			new Token(
				new TextAttribute(manager.getColor(IColorConstants.STRING)));
		IToken tagName =
			new Token(
				new TextAttribute(manager.getColor(IColorConstants.TAG_NAME)));
		
		IToken attribute =
			new Token(
				new TextAttribute(manager.getColor(IColorConstants.ATTRIBUTE)));

		
		IRule[] rules = new IRule[5];

		rules[0] = new XMLTagNameRule(tagName, true);
		// Add rule for double quotes
		rules[1] = new MultiLineRule("\"", "\"", string, '\\');
		// Add a rule for single quotes
		rules[2] = new MultiLineRule("'", "'", string, '\\');
		// Add generic whitespace rule.
		rules[3] = new WhitespaceRule(new XMLWhitespaceDetector());
		
		rules[4] = new AttributeRule(attribute,true);
		
		setRules(rules);
	}
}

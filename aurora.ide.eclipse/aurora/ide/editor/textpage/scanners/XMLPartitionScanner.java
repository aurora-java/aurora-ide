package aurora.ide.editor.textpage.scanners;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

import aurora.ide.editor.textpage.rules.NonMatchingRule;
import aurora.ide.editor.textpage.rules.StartTagRule;





public class XMLPartitionScanner extends RuleBasedPartitionScanner
{

	public final static String XML_DEFAULT = "XML_DEFAULT";
	public final static String XML_COMMENT = "XML_COMMENT";
	public final static String XML_PI = "XML_PI";
	public final static String XML_DOCTYPE = "XML_DOCTYPE";
	public final static String XML_CDATA = "XML_CDATA";
	public final static String XML_START_TAG = "XML_START_TAG";
	public final static String XML_END_TAG = "XML_END_TAG";

public XMLPartitionScanner()
{

	IToken xmlComment = new Token(XML_COMMENT);
	IToken xmlPI = new Token(XML_PI);
	IToken startTag = new Token(XML_START_TAG);
	IToken endTag = new Token(XML_END_TAG);
	IToken docType = new Token(XML_DOCTYPE);
	IToken cData = new Token(XML_CDATA);

	IPredicateRule[] rules = new IPredicateRule[7];

	rules[0] = new NonMatchingRule();
	rules[1] = new MultiLineRule("<!--", "-->", xmlComment);
	rules[2] = new MultiLineRule("<?", "?>", xmlPI);
	rules[3] = new MultiLineRule("</", ">", endTag);
	rules[4] = new MultiLineRule("<!DOCTYPE", ">", docType);
	rules[5] = new MultiLineRule("<![CDATA[", "]]>", cData);
	rules[6] = new StartTagRule(startTag);

	setPredicateRules(rules);
}
}
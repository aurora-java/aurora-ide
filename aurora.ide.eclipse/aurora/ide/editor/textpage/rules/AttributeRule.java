package aurora.ide.editor.textpage.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * @author jinxiao.lin
 */
public class AttributeRule implements IRule {

	IToken thisToken;
	StringBuffer buffer = new StringBuffer();
	int charsRead = 0;
	boolean start;

	public AttributeRule(IToken token, boolean start) {
		super();
		this.thisToken = token;
		this.start = start;
	}

	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {

		buffer.setLength(0);
		charsRead = 0;
		int now = scanner.read();
		if(Character.isJavaIdentifierStart((char)now)){
			do {
				now = read(scanner);
			} while (isOK((char)now));
		}else{
			scanner.unread();
			return Token.UNDEFINED;
		}
		scanner.unread();
		return thisToken;
	}


	private int read(ICharacterScanner scanner) {
		int c = scanner.read();
		buffer.append((char) c);
		charsRead++;
		return c;
	}

	private boolean isOK(char c) {
		if(c==':')
			return true;
		return Character.isJavaIdentifierPart((char)c);
	}
}
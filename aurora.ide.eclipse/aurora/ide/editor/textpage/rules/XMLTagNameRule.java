package aurora.ide.editor.textpage.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * @author jinxiao.lin
 */
public class XMLTagNameRule implements IRule {

	IToken fToken;
	StringBuffer buffer = new StringBuffer();
	int charsRead = 0;
	boolean start;

	public XMLTagNameRule(IToken token, boolean start) {
		super();
		this.fToken = token;
		this.start = start;
	}

	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {

		buffer.setLength(0);
		charsRead = 0;
//		int now = 0;
		int now = scanner.read();
		if(!Character.isJavaIdentifierStart((char)now)){
			scanner.unread();
			return Token.UNDEFINED;
		}
		scanner.unread();
		
		scanner.unread();
		int pre = scanner.read();
		if (pre == '/') {
			scanner.unread();
			scanner.unread();
			int prepre = scanner.read();
			if (prepre != '<'){
				scanner.read();
				return Token.UNDEFINED;
			}
			scanner.read();
		} else if (pre == '<'){
			now = scanner.read();
			if (now == '/'){
				scanner.unread();
				return Token.UNDEFINED;
			}
		}else{
			return Token.UNDEFINED;
		}
		return getTagName(scanner);
		
	}

	private int read(ICharacterScanner scanner) {
		int c = scanner.read();
		buffer.append((char) c);
		charsRead++;
		return c;
	}
	public IToken getTagName(ICharacterScanner scanner){
		int now = 0;
		do {
			now = read(scanner);
		} while (isOK((char) now));
		scanner.unread();
		return fToken;
	}
	
	private boolean isOK(char c) {
		if(c==':'||c=='-')
			return true;
//		return !(Character.isWhitespace(c) || c == '>'|| c == '/' || c== ICharacterScanner.EOF||c== '<');
		return Character.isJavaIdentifierPart(c);
	}
}
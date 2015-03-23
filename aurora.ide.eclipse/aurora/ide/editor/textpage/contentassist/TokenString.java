/**
 * 
 */
package aurora.ide.editor.textpage.contentassist;

/**
 * @author linjinxiao
 * 
 */
public class TokenString {
	private String text;
	private int documentOffset;
	private int cursorOffset;

	TokenString(String text, int documentOffset, int cursorOffset) {
		this.text = text;
		this.documentOffset = documentOffset;
		this.cursorOffset = cursorOffset;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getDocumentOffset() {
		return documentOffset;
	}

	public void setDocumentOffset(int documentOffset) {
		this.documentOffset = documentOffset;
	}

	public int getCursorOffset() {
		return cursorOffset;
	}

	public void setCursorOffset(int cursorOffset) {
		this.cursorOffset = cursorOffset;
	}

	public boolean isWhiteSpace() {
		if (text == null || text.length() != 1)
			return false;
		return Character.isWhitespace(text.toCharArray()[0]);
	}

	public String getStrBeforeCursor() {
		if (text == null)
			return null;
		int index = cursorOffset - documentOffset;
		if (index > 0) {
			return text.substring(0, index);
		}
		return null;
	}

	public int getLength() {
		if (text == null)
			return 0;
		return text.length();
	}
}

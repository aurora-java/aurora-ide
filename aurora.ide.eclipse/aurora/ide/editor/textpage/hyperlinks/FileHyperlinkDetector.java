package aurora.ide.editor.textpage.hyperlinks;


import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import aurora.ide.editor.textpage.ColorManager;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;
import aurora.ide.editor.textpage.scanners.XMLTagScanner;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.SystemException;



public class FileHyperlinkDetector implements IHyperlinkDetector {

	private XMLTagScanner scanner;
	private static String[] buildinBMNames = new String[] { "model",
			"extend", "lovservice","service" };
	private static String[] buildinScreenFile = new String[] { "service",
			"screen", "svc" };

	public FileHyperlinkDetector() {
		ColorManager colorManager = new ColorManager();
		scanner = new XMLTagScanner(colorManager);
		scanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager
				.getColor(IColorConstants.TAG))));
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		
		// get doc
		IDocument doc = textViewer.getDocument();
		try {
			IRegion token = getTokenString(doc, region.getOffset());
			if (token == null) {
				return null;
			}
			String tokenString = doc.get(token.getOffset(), token.getLength());
			for (int i = 0; i < buildinScreenFile.length; i++) {
				String extension = "." + buildinScreenFile[i];
				if (tokenString.toLowerCase().endsWith(extension)) {
					return new IHyperlink[] { new ScreenFileHyperlink(token,
							textViewer) };
				}
			}
			String columnName = getColumnName(region.getOffset(), doc);
			if (columnName == null)
				return null;
			for (int i = 0; i < buildinBMNames.length; i++) {
				String cName = buildinBMNames[i];
				if ((columnName.toLowerCase().indexOf(cName) != -1)) {
					return new IHyperlink[] { new BMFileHyperlink(token,
							textViewer) };
				}
			}
		} catch (Exception e) {
			DialogUtil.showExceptionMessageBox(e);
		}
		return null;
	}

	private String getColumnName(int documentOffset, IDocument document) throws SystemException{
		String columnName = null;
		ITypedRegion region;
		try {
			region = document.getPartition(documentOffset);
		} catch (BadLocationException e) {
			throw new SystemException(e);
		}
		if (!XMLPartitionScanner.XML_START_TAG.equals(region.getType()))
			return null;
		int partitionOffset = region.getOffset();
		scanner.setRange(document, partitionOffset, region.getLength());
		IToken token = null;
		while ((token = scanner.nextToken()) != Token.EOF) {
			int offset = scanner.getTokenOffset();
			int length = scanner.getTokenLength();
			if (offset <= documentOffset && (offset + length) >= documentOffset) {
				break;
			}
			if (token.getData() instanceof TextAttribute) {
				TextAttribute text = (TextAttribute) token.getData();
				if (text.getForeground().getRGB().equals(
						IColorConstants.ATTRIBUTE)) {
					try {
						columnName = document.get(scanner.getTokenOffset(), scanner
								.getTokenLength());
					} catch (BadLocationException e) {
						throw new SystemException(e);
					}
				}

			}

		}
		return columnName;
	}

	private boolean isOK(char c) {
		if (c == '.')
			return true;
		return Character.isJavaIdentifierPart((char) c);
	}

	private IRegion getTokenString(IDocument document, int documentOffset)
			throws BadLocationException {
		ITypedRegion region = document.getPartition(documentOffset);
		int partitionOffset = region.getOffset();
		int partitionLength = region.getLength();
		int index = documentOffset - partitionOffset;
		String partitionText = document.get(partitionOffset, partitionLength);
		int start = index - 1;
		if(start>=0){
			char c = partitionText.charAt(start);
			while (isOK(c) && start > 0) {
				start--;
				c = partitionText.charAt(start);
			}
		}
		start++;
		int end = index;
		char c = partitionText.charAt(end);
		while (isOK(c) && (end < partitionLength - 1)) {
			end++;
			c = partitionText.charAt(end);
		}
		if (end - start == 0) {
			return null;
		}
		IRegion currentRegion = new Region(partitionOffset + start, end - start);
		return currentRegion;

	}
}

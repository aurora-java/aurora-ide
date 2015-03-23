package aurora.ide.editor.textpage.contentassist;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Image;
import org.xml.sax.SAXException;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.editor.textpage.scanners.XMLTagScanner;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapLocatorParser;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.helpers.SystemException;


import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.Element;

public class AttributeStrategy implements IContentAssistStrategy {

	private XMLTagScanner scanner;
	private TokenString tokenString;
	private ITextViewer viewer;
	private int cursorOffset;
	private IDocument document;
	public AttributeStrategy(XMLTagScanner scanner) {
		super();
		this.scanner = scanner;
	}
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int cursorOffset)
			throws BadLocationException {
		this.viewer = viewer;
		this.cursorOffset = cursorOffset;
		this.document = viewer.getDocument();
		try {
			tokenString = createTokenString();
			if (tokenString == null)
				return null;
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return null;
		}
		String content = document.get();
		CompositeMap parentCompositeMap = null;
		try {
			parentCompositeMap = locateCompositeMap(content, cursorOffset);
			if (parentCompositeMap != null) {
				if (tokenString.getLength() == 0)
					return computeNewAttr(parentCompositeMap);
				else
					return computeUpdateAttr(parentCompositeMap);
			}
		} catch (ApplicationException e) {
			String originalContent = document.get();
			String changedContent = originalContent.substring(0, tokenString.getDocumentOffset())
					+ originalContent.substring(tokenString.getDocumentOffset() + tokenString.getLength());
			try {
				parentCompositeMap = locateCompositeMap(changedContent, cursorOffset);
				if (parentCompositeMap != null) {
					return computeNewAttr(parentCompositeMap);
				}
			} catch (ApplicationException e1) {

			}

		}
		return getDefaultCompletionProposal();
	}

	private ICompletionProposal[] computeUpdateAttr(CompositeMap parentCompositeMap) {
//		Element element = LoadSchemaManager.getSchemaManager().getElement(parentCompositeMap.getQName());
		Element element =CompositeMapUtil.getElement(parentCompositeMap);
		if (element == null)
			return getDefaultCompletionProposal();
		List allAttributes = element.getAllAttributes();

		List avaliableList = new ArrayList();
		String preString = tokenString.getStrBeforeCursor();
		List existsList = null;
		try {
			existsList = getExistsAttrs(document);
		} catch (SystemException e) {
			DialogUtil.showExceptionMessageBox(e);
			return null;
		}
		for (Iterator iter = allAttributes.iterator(); iter.hasNext();) {
			Attribute attr = (Attribute) iter.next();
			String name = attr.getName();
			if (existsList.contains(name)) {
				continue;
			}
			if (preString != null && !name.startsWith(preString)) {
				continue;
			}
			String attributeDocument = attr.getDocument();
			String description = name;
			String replaceString = name;
			if (attributeDocument != null)
				description = formateAttributeName(name) + " - " + attributeDocument;
			avaliableList.add(new CompletionProposal(replaceString, tokenString.getDocumentOffset(), tokenString
					.getLength(), replaceString.length() + 2, getDefaultImage(), description, null, attributeDocument));

		}
		int allLength = avaliableList.size();
		if (allLength == 0)
			return getDefaultCompletionProposal();
		ICompletionProposal[] result = new ICompletionProposal[allLength];
		int i = 0;
		for (Iterator iter = avaliableList.iterator(); iter.hasNext();) {
			result[i] = (CompletionProposal) iter.next();
			i++;
		}
		return result;
	}
	private ICompletionProposal[] computeNewAttr(CompositeMap parentCompositeMap) {
//		Element element = LoadSchemaManager.getSchemaManager().getElement(parentCompositeMap.getQName());
		Element element =CompositeMapUtil.getElement(parentCompositeMap);
		if (element == null)
			return getDefaultCompletionProposal();
		List allAttributes = element.getAllAttributes();

		List avaliableList = new ArrayList();
		String preString = tokenString.getStrBeforeCursor();
		List existsList = null;
		try {
			existsList = getExistsAttrs(document);
		} catch (SystemException e) {
			DialogUtil.showExceptionMessageBox(e);
			return null;
		}
		for (Iterator iter = allAttributes.iterator(); iter.hasNext();) {
			Attribute attr = (Attribute) iter.next();
			String name = attr.getName();
			if (existsList.contains(name)) {
				continue;
			}
			if (preString != null && !name.startsWith(preString)) {
				continue;
			}
			String attributeDocument = attr.getDocument();
			String description = name;
			String replaceString = name + "=\"\" ";;
			if (attributeDocument != null)
				description = formateAttributeName(name) + " - " + attributeDocument;
			avaliableList.add(new CompletionProposal(replaceString, tokenString.getDocumentOffset(), tokenString
					.getLength(), replaceString.length() - 2, getDefaultImage(), description, null, attributeDocument));

		}
		int allLength = avaliableList.size();
		if (allLength == 0)
			return getDefaultCompletionProposal();
		ICompletionProposal[] result = new ICompletionProposal[allLength];
		int i = 0;
		for (Iterator iter = avaliableList.iterator(); iter.hasNext();) {
			result[i] = (CompletionProposal) iter.next();
			i++;
		}
		return result;
	}

	private String formateAttributeName(String attributeName) {
		int defaultLength = 20;
		StringBuffer newAttributeName = new StringBuffer(attributeName);
		int strLength = newAttributeName.length();
		if (strLength < defaultLength) {
			for (int i = 0; i < defaultLength - strLength; i++) {
				newAttributeName.append(" ");
			}
		}
		return newAttributeName.toString();
	}

	private List getExistsAttrs(IDocument document) throws SystemException {
		List existsAttrs = new ArrayList();
		IToken token = null;
		String attributeName = null;
		try {
			ITypedRegion region = document.getPartition(cursorOffset);
			int partitionOffset = region.getOffset();
			scanner.setRange(document, partitionOffset, region.getLength());
			while ((token = scanner.nextToken()) != Token.EOF) {
				if (token.getData() instanceof TextAttribute) {
					TextAttribute text = (TextAttribute) token.getData();
					if (text.getForeground().getRGB().equals(IColorConstants.ATTRIBUTE)) {

						attributeName = document.get(scanner.getTokenOffset(), scanner.getTokenLength());
						if (attributeName != null) {
							existsAttrs.add(attributeName);
						}

					}
				}
			}
		} catch (BadLocationException e) {
			throw new SystemException(e);
		}
		return existsAttrs;
	}

	private ICompletionProposal[] getDefaultCompletionProposal() {
		String text = tokenString.getText();
		if (text == null || text.equals(""))
			return null;
		String replaceString = tokenString.getText() + "=\"\" ";
		return new ICompletionProposal[]{new CompletionProposal(replaceString, tokenString.getDocumentOffset(),
				tokenString.getLength(), text.length() + 1, getDefaultImage(), null, null, null)};
	}

	private static Image getDefaultImage() {
		return ImagesUtils.getImage("contentassit.gif");
//		Image contentImage = AuroraPlugin.getImageDescriptor(LocaleMessage.getString("contentassit.icon")).createImage();
//		return contentImage;
	}
	private TokenString createTokenString() throws ApplicationException {
		IToken token = null;
		try {
			ITypedRegion region = document.getPartition(cursorOffset);
			int partitionOffset = region.getOffset();
			scanner.setRange(document, partitionOffset, region.getLength());
			while ((token = scanner.nextToken()) != Token.EOF) {
				if(scanner.getTokenOffset()<=cursorOffset&&(scanner.getTokenOffset()+scanner.getTokenLength())>=cursorOffset){
					if (token.getData() instanceof TextAttribute) {
						TextAttribute text = (TextAttribute) token.getData();
						if (text.getForeground().getRGB().equals(IColorConstants.STRING)) {
							return null;
						}
					}
				}
			}
		} catch (BadLocationException e) {
			throw new SystemException(e);
		}
		
		
		TokenString tokenString = null;
		IDocument document = viewer.getDocument();
		try {
			ITypedRegion partitionRegion = document.getPartition(cursorOffset);
			int attrStart = cursorOffset - partitionRegion.getOffset() - 1;
			int attrEnd = cursorOffset - partitionRegion.getOffset();
			String partitionText = document.get(partitionRegion.getOffset(), partitionRegion.getLength());
			int partitionLength = partitionRegion.getLength();

			char c = partitionText.charAt(attrStart);
			while (beginChar(c, attrStart)) {
				attrStart--;
				c = partitionText.charAt(attrStart);
			}
			attrStart++;
			char beginChar = c;

			c = partitionText.charAt(attrEnd);
			while (endChar(c, attrEnd, partitionLength)) {
				attrEnd++;
				c = partitionText.charAt(attrEnd);
			}
			if (beginChar == '"' || c == '"') {
				return null;
			}
			String attrName = partitionText.substring(attrStart, attrEnd);

			tokenString = new TokenString(attrName, partitionRegion.getOffset() + attrStart, cursorOffset);
		} catch (BadLocationException e) {
			throw new SystemException(e);
		}
		return tokenString;

	}
	private boolean beginChar(char c, int start) {
		return !Character.isWhitespace(c) && c != '<' && start >= 0 && c != '"';
	}

	private boolean endChar(char c, int end, int partitionLength) {
		return !Character.isWhitespace(c) && c != '>' && c != '/' && c != '=' && c != '<'
				&& (end < partitionLength - 1) && c != '"';
	}
	private CompositeMap locateCompositeMap(String content, int offset) throws ApplicationException {
		try {
			CompositeMapLocatorParser parser = new CompositeMapLocatorParser();
			InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
			CompositeMap cm = parser.getCompositeMapFromLine(is, getCursorLine(offset));
			return cm;
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e);
		} catch (SAXException e) {
			throw new ApplicationException("请检查此文件格式是否正确.", e);
		} catch (IOException e) {
			throw new ApplicationException("请检查此文件格式是否正确.", e);
		}
	}
	private int getCursorLine(int offset) {
		return viewer.getTextWidget().getLineAtOffset(offset);
	}
}
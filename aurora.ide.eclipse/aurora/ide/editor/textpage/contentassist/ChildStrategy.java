package aurora.ide.editor.textpage.contentassist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.xml.sax.SAXException;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;
import aurora.ide.editor.textpage.scanners.XMLTagScanner;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapLocatorParser;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ExceptionUtil;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.helpers.SystemException;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.QualifiedName;
import uncertain.schema.Element;

/**
 * @author linjinxiao
 * 
 */
public class ChildStrategy implements IContentAssistStrategy {

	private XMLTagScanner scanner;
	private TokenString tokenString;
	private ITextViewer viewer;
	private int cursorOffset;

	public ChildStrategy(XMLTagScanner scanner) {
		this.scanner = scanner;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int cursorOffset) throws BadLocationException {
		this.viewer = viewer;
		this.cursorOffset = cursorOffset;
		IDocument document = viewer.getDocument();
		ITypedRegion region = document.getPartition(cursorOffset);
		if (!XMLPartitionScanner.XML_START_TAG.equals(region.getType()))
			return null;
		try {
			tokenString = createTokenString();
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return null;
		}
		scanner.setRange(document, region.getOffset(), region.getLength());
		String content = document.get();
		try {
			CompositeMap currentCM = locateCompositeMap(content, cursorOffset);
			if (currentCM != null && currentCM.getParent() != null)
				return computeUpdateTag(currentCM.getParent());
			return getDefaultCompletionProposal();
		} catch (ApplicationException e) {
			String originalContent = document.get();
			String changedContent = originalContent.substring(0,
					region.getOffset())
					+ createSpace(tokenString.getLength())
					+ originalContent.substring(cursorOffset);
			try {
				CompositeMap parentCompositeMap = locateCompositeMap(
						changedContent, cursorOffset);
				if (parentCompositeMap != null) {
					return computeNewTag(parentCompositeMap);
				}
			} catch (ApplicationException e1) {
				Throwable rootCause = ExceptionUtil.getRootCause(e1);
				String errorMessage = ExceptionUtil
						.getExceptionTraceMessage(rootCause);
				if (errorMessage != null
						&& errorMessage.indexOf("end-tag") != -1)
					return computeEndTag(errorMessage);
				return getDefaultCompletionProposal();
			}
			return getDefaultCompletionProposal();
		}
	}

	private ICompletionProposal[] computeEndTag(String errorMessage) {
		if (errorMessage == null || errorMessage.indexOf("end-tag") < 0) {
			return getDefaultCompletionProposal();
		}
		int beginIndex = errorMessage.indexOf("element type");
		int endIndex = errorMessage.indexOf("must", beginIndex);
		String content = errorMessage.substring(beginIndex, endIndex);
		beginIndex = content.indexOf("\"");
		endIndex = content.lastIndexOf("\"");
		if (beginIndex == -1 || endIndex == beginIndex)
			return getDefaultCompletionProposal();
		content = "</" + content.substring(beginIndex + 1, endIndex) + ">";
		IDocument document = viewer.getDocument();
		ITypedRegion partitionRegion;
		try {
			partitionRegion = document.getPartition(cursorOffset);
			int length = cursorOffset - partitionRegion.getOffset();
			document.replace(partitionRegion.getOffset(), length, content);
			return null;
		} catch (BadLocationException e) {
			DialogUtil.logErrorException(e);
		}
		return null;
	}

	private ICompletionProposal[] computeNewTag(CompositeMap parent) {
		List childs = CompositeMapUtil.getAvailableChildElements(parent);
		if (childs == null)
			childs = new ArrayList();
		// Element ele =
		// LoadSchemaManager.getSchemaManager().getElement(parent);
		Element ele = CompositeMapUtil.getElement(parent);
		if (ele != null) {
			childs.addAll(ele.getAllArrays());
		} else {
			return getDefaultCompletionProposal();
		}
		List avaliableList = new ArrayList();
		String preString = tokenString.getStrBeforeCursor();
		for (Iterator iter = childs.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			String name = CompositeMapUtil.getContextFullName(parent,
					element.getQName());
			if (preString != null && !name.startsWith(preString)) {
				continue;
			}
			String attributeDocument = element.getDocument();
			String description = name;
			String replaceString = computeNewTagReplaceString(parent,
					element.getQName());
			if (attributeDocument != null)
				description = formateAttributeName(name) + " - "
						+ attributeDocument;
			Image contentImage = element.isArray() ? getArrayImage()
					: getElementImage();
			avaliableList.add(new CompletionProposal(replaceString, tokenString
					.getDocumentOffset(), tokenString.getLength(), name
					.length() + 1, contentImage, description, null,
					attributeDocument));
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

	private String computeNewTagReplaceString(CompositeMap context,
			QualifiedName childQN) {
		String replaceString = CompositeMapUtil.getContextFullName(context,
				childQN);
		Map prefix_mapping = CompositeUtil.getPrefixMapping(context);
		String nameSpace = childQN.getNameSpace();
		Object uri_obj = prefix_mapping.get(nameSpace);
		if (uri_obj == null) {
			if (childQN.getPrefix() != null)
				replaceString = replaceString + " xmlns:" + childQN.getPrefix()
						+ "=\"" + nameSpace + "\" ";
			else {

				String string = " xmlns=\"" + nameSpace + "\" ";
				string = nameSpace == null ? "" : string;
				replaceString = replaceString + string;
			}
		}
		replaceString = replaceString + " />";
		return replaceString;
	}

	private ICompletionProposal[] computeUpdateTag(CompositeMap parent) {
		List childs = CompositeMapUtil.getAvailableChildElements(parent);
		if (childs == null)
			childs = new ArrayList();
		// Element ele =
		// LoadSchemaManager.getSchemaManager().getElement(parent);
		Element ele = CompositeMapUtil.getElement(parent);
		if (ele != null) {
			childs.addAll(ele.getAllArrays());
		} else {
			return getDefaultCompletionProposal();
		}
		List avaliableList = new ArrayList();
		String preString = null;
		for (Iterator iter = childs.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			String name = CompositeMapUtil.getContextFullName(parent,
					element.getQName());
			if (preString != null && !name.startsWith(preString)) {
				continue;
			}
			String attributeDocument = element.getDocument();
			String description = name;
			String replaceString = name;
			if (attributeDocument != null)
				description = formateAttributeName(name) + " - "
						+ attributeDocument;
			Image contentImage = element.isArray() ? getArrayImage()
					: getElementImage();
			avaliableList.add(new CompletionProposal(replaceString, tokenString
					.getDocumentOffset(), tokenString.getLength(), name
					.length() + 1, contentImage, description, null,
					attributeDocument));
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

	private CompositeMap locateCompositeMap(String content, int offset)
			throws ApplicationException {
		try {
			CompositeMapLocatorParser parser = new CompositeMapLocatorParser();
			InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
			CompositeMap cm = parser.getCompositeMapFromLine(is,
					getCursorLine(offset));
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

	private static Image getArrayImage() {
		return ImagesUtils.getImage("array.gif");
		// Image contentImage =
		// AuroraPlugin.getImageDescriptor(LocaleMessage.getString("array.icon")).createImage();
		// return contentImage;
	}

	private static Image getElementImage() {

		// Image contentImage =
		// AuroraPlugin.getImageDescriptor(LocaleMessage.getString("element.icon")).createImage();
		// return contentImage;
		return ImagesUtils.getImage("element.gif");
	}

	private static Image getDefaultImage() {
		// Image contentImage = AuroraPlugin.getImageDescriptor(
		// LocaleMessage.getString("contentassit.icon")).createImage();
		// return contentImage;
		return ImagesUtils.getImage("contentassit.gif");
	}

	private ICompletionProposal[] getDefaultCompletionProposal() {
		String text = tokenString.getText();
		if (text == null || text.equals(""))
			return null;
		String replaceString = tokenString.getText() + " />";
		return new ICompletionProposal[] { new CompletionProposal(
				replaceString, tokenString.getDocumentOffset(),
				tokenString.getLength(), text.length() + 1, getDefaultImage(),
				null, null, null) };
	}

	private TokenString createTokenString() throws ApplicationException {
		TokenString tokenString = null;
		IDocument document = viewer.getDocument();
		try {
			ITypedRegion partitionRegion = document.getPartition(cursorOffset);
			int tagEnd = cursorOffset - partitionRegion.getOffset();
			String partitionText = document.get(partitionRegion.getOffset(),
					partitionRegion.getLength());
			int partitionLength = partitionRegion.getLength();
			char c = partitionText.charAt(tagEnd);
			while (endChar(c, tagEnd, partitionLength)) {
				tagEnd++;
				c = partitionText.charAt(tagEnd);
			}
			String tagName = partitionText.length() < 2 ? "" : partitionText
					.substring(1, tagEnd);
			tokenString = new TokenString(tagName,
					partitionRegion.getOffset() + 1, cursorOffset);
		} catch (BadLocationException e) {
			throw new SystemException(e);
		}
		return tokenString;

	}

	private boolean endChar(char c, int end, int partitionLength) {
		return !Character.isWhitespace(c) && c != '>' && c != '/' && c != '<'
				&& (end < partitionLength - 1) && c != '"';
	}

	private String createSpace(int length) {
		StringBuffer space = new StringBuffer("");
		for (int i = 0; i < length; i++) {
			space.append(" ");
		}
		return space.toString();
	}
}

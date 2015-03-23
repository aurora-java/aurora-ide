package aurora.ide.search.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Element;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;
import aurora.ide.AuroraPlugin;
import aurora.ide.editor.textpage.ColorManager;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.textpage.scanners.XMLTagScanner;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapLocatorParser;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.helpers.SystemException;
import aurora.ide.search.core.AbstractSearchQuery;
import aurora.ide.search.core.AbstractSearchService;
import aurora.ide.search.core.SearchQueryFactory;
import aurora.ide.search.core.Util;

public class FieldReferenceAction implements IEditorActionDelegate {

	private IFile sourceFile;
	private TextPage textPage;
	private XMLTagScanner tagScanner;
	private TextSelection selection;
	private AbstractSearchQuery query;

	private class Attribute {
		private String name;
		private String value;
	}

	public FieldReferenceAction() {
	}

	public void run(IAction action) {
		if (query != null)
			NewSearchUI.runQueryInBackground(query);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		boolean isEnable = checkSelection(selection);
		if (action != null)
			action.setEnabled(isEnable);
	}

	private boolean checkSelection(ISelection sel) {
		if (textPage == null) {
			return false;
		}
		if (!(sel instanceof TextSelection)) {
			return false;
		}
		selection = (TextSelection) sel;
		if (selection.getStartLine() != selection.getEndLine()) {
			return false;
		}
		Attribute attribute = getAttribute(selection);
		if (attribute == null) {
			return false;
		}
		query = createSearchQuery(attribute, selection);
		if (query == null) {
			return false;
		}

		return true;
	}

	private AbstractSearchQuery createSearchQuery(Attribute att,
			TextSelection selection) {
		String content = textPage.getContent();
		try {
			IDocument document = textPage.getInputDocument();
			int offset = selection.getOffset();
			CompositeMap map = locateCompositeMap(content,
					document.getLineOfOffset(offset));
			if (map == null)
				return null;
			Object value = map.get(att.name);
			if (att.value.equals(value)) {
//				Element element = LoadSchemaManager.getSchemaManager()
//						.getElement(map);
				Element element = CompositeMapUtil.getElement(map);
				if (element != null) {
					
					List attrib_list = element.getAllAttributes();
					for (Iterator it = attrib_list.iterator(); it.hasNext();) {
						uncertain.schema.Attribute attrib = (uncertain.schema.Attribute) it
								.next();
						if (att.name.equalsIgnoreCase(attrib.getName())) {
							IType attributeType = attrib.getAttributeType();
							IFile sourceFile = getSourceFile(map, attrib);
							IResource scope = Util.getScope(sourceFile);
							if (scope == null || sourceFile == null)
								return null;
							QualifiedName referenceTypeQName = ((SimpleType) attributeType)
									.getReferenceTypeQName();
							if (AbstractSearchService.datasetReference
									.equals(referenceTypeQName)) {
								scope = sourceFile;
							}
							return SearchQueryFactory.createSearchQuery(
									referenceTypeQName, scope, sourceFile,
									document.get(selection.getOffset(),
											selection.getLength()));
						}
					}
				}
			}
		} catch (ApplicationException e) {
		} catch (BadLocationException e) {
		}
		return null;
	}

	private IFile getSourceFile(CompositeMap map,
			uncertain.schema.Attribute attrib) {
		IType attributeType = attrib.getAttributeType();
		if (attributeType instanceof SimpleType) {
			QualifiedName referenceTypeQName = ((SimpleType) attributeType)
					.getReferenceTypeQName();
			if (AbstractSearchService.foreignFieldReference
					.equals(referenceTypeQName)) {
				return sourceFile = Util.findBMFile(map);
			}
			if (AbstractSearchService.localFieldReference
					.equals(referenceTypeQName)
					|| AbstractSearchService.datasetReference
							.equals(referenceTypeQName)) {
				return sourceFile = getFile();
			}
			if (AbstractSearchService.screenReference
					.equals(referenceTypeQName)) {
//				return sourceFile = Util.findScreenFile(getFile(),
//						map.get(attrib.getName()));
				String valueIgnoreCase = CompositeMapUtil.getValueIgnoreCase(attrib, map);
				return sourceFile = Util.findScreenFile(getFile(),valueIgnoreCase);
			}
			if (AbstractSearchService.bmReference.equals(referenceTypeQName)) {
//				return sourceFile = Util.findBMFileByPKG(map.get(attrib
//						.getName()));
				String valueIgnoreCase = CompositeMapUtil.getValueIgnoreCase(attrib, map);
				return sourceFile = Util.findBMFileByPKG(valueIgnoreCase);
			}
		}
		return null;
	}

	private Attribute getAttribute(TextSelection selection) {
		IDocument document = textPage.getInputDocument();
		int offset = selection.getOffset();
		int length = selection.getLength();

		String name = null;
		try {
			XMLTagScanner scanner = getXMLTagScanner();
			IToken token = null;
			ITypedRegion region = document.getPartition(offset);
			scanner.setRange(document, region.getOffset(), region.getLength());
			while ((token = scanner.nextToken()) != Token.EOF) {
				if (token.getData() instanceof TextAttribute) {
					TextAttribute text = (TextAttribute) token.getData();
					if (text.getForeground().getRGB()
							.equals(IColorConstants.ATTRIBUTE)) {
						name = document.get(scanner.getTokenOffset(),
								scanner.getTokenLength());
					}
				}
				if (scanner.getTokenOffset() == offset - 1
						&& (scanner.getTokenLength()) == length + 2) {
					if (token.getData() instanceof TextAttribute) {
						TextAttribute text = (TextAttribute) token.getData();
						if (text.getForeground().getRGB()
								.equals(IColorConstants.STRING)) {
							if (name == null)
								return null;
							Attribute attribute = new Attribute();
							attribute.name = name;
							attribute.value = document.get(offset, length);
							return attribute;
						}
					}
				}
			}
		} catch (BadLocationException e) {
		}

		return null;
	}

	private CompositeMap locateCompositeMap(String content, int line)
			throws ApplicationException {
		try {
			CompositeMapLocatorParser parser = new CompositeMapLocatorParser();
			InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
			CompositeMap cm = parser.getCompositeMapFromLine(is, line);
			return cm;
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e);
		} catch (SAXException e) {
			throw new ApplicationException("请检查此文件格式是否正确.", e);
		} catch (IOException e) {
			throw new ApplicationException("请检查此文件格式是否正确.", e);
		}
	}

	private XMLTagScanner getXMLTagScanner() {
		if (tagScanner == null) {
			ColorManager manager = new ColorManager();
			tagScanner = new XMLTagScanner(manager);
			tagScanner.setDefaultReturnToken(new Token(new TextAttribute(
					manager.getColor(IColorConstants.TAG))));
		}
		return tagScanner;
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof TextPage) {
			textPage = (TextPage) targetEditor;
		} else {
			textPage = null;
		}
	}

	public IFile getFile() {
		IFile file = textPage.getFile();
		if ("bm".equalsIgnoreCase(file.getFileExtension())
				|| "screen".equalsIgnoreCase(file.getFileExtension())) {
			return file;
		} else {
			return null;
		}
	}
}

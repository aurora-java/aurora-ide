package aurora.ide.search.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapLocatorParser;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.SystemException;

public class CompositeMapInDocumentManager {

	/**
	 * 
	 * 查找CompositeMap 在 document中的准确位置
	 * 
	 */
	public static CompositeMapInDocument getCompositeMapInDocument(
			CompositeMap map, IDocument document) {
		int startLine = map.getLocation().getStartLine() - 1;
		int endLine = map.getLocation().getEndLine() - 1;

		try {
			IRegion sInfo = document.getLineInformation(startLine);
			IRegion eInfo = document.getLineInformation(endLine);
			ITypedRegion[] sPartitions = document.computePartitioning(
					sInfo.getOffset(), sInfo.getLength());
			ITypedRegion[] ePartitions = document.computePartitioning(
					eInfo.getOffset(), eInfo.getLength());
			for (ITypedRegion p : sPartitions) {
				if (XMLPartitionScanner.XML_START_TAG.equals(p.getType())) {
					Map namspaceMapping = map.getRoot().equals(map) ? null
							: map.getRoot().getNamespaceMapping();
					CompositeMapInDocument _info = lookupCompositeMap(
							namspaceMapping, p, ePartitions, document);
					if (_info != null) {
						CompositeMap map2 = _info.getMap();
						((CommentCompositeMap) map2)
								.setComment(((CommentCompositeMap) map)
										.getComment());
						if (map.getName().equals(map2.getName())
								&& map.getName().equals(map.getRawName())
								&& map.getRawName().equals(map2.getRawName())
								&& map2.getNamespaceURI() == null) {
							map2.setNameSpaceURI(map.getNamespaceURI());
						}
						if (map.equals(map2)) {
							return _info;
						}
					}
				}
			}
		} catch (BadLocationException e) {
		}
		return null;
	}

	/**
	 * 
	 *
	 * 
	*/
	private static CompositeMapInDocument lookupCompositeMap(
			Map namespaceMapping, ITypedRegion start, ITypedRegion[] ends,
			IDocument document) {
		try {
			String _xml = document.get(start.getOffset(), start.getLength());
			CompositeMap map = loaderFromString(namespaceMapping, _xml);
			if (map != null) {
				CompositeMapInDocument info = new CompositeMapInDocument(map,
						document, start, start);
				return info;
			}
		} catch (BadLocationException e) {
		} catch (ApplicationException e) {

		}
		for (ITypedRegion p : ends) {
			if (XMLPartitionScanner.XML_END_TAG.equals(p.getType())) {
				try {
					String _xml = document.get(start.getOffset(), p.getOffset()
							+ p.getLength() - start.getOffset());
					CompositeMap map = loaderFromString(namespaceMapping, _xml);
					if (map != null) {
						CompositeMapInDocument info = new CompositeMapInDocument(
								map, document, start, p);
						return info;
					}
				} catch (BadLocationException e) {
				} catch (ApplicationException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 将一段没有namespace的xml字符串，转化为CompositeMap
	 * 
	 * 
	 */
	public static CompositeMap loaderFromString(Map namespaceMapping,
			String content) throws ApplicationException {
		if (namespaceMapping == null) {
			return CompositeMapUtil.loaderFromString(content);
		}
		if (content == null)
			return null;
		CompositeMap root = null;
		CompositeMapLocatorParser parser = new NoNamespaceMapParser(
				namespaceMapping);
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(content.getBytes("UTF-8"));
			root = parser.parseStream(is);
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e);
		} catch (SAXException e) {
			throw new ApplicationException("请检查内容格式" + content + "是否正确.", e);
		} catch (IOException e) {
			throw new ApplicationException("请检查内容格式" + content + "是否正确.", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					DialogUtil.logErrorException(e);
				}
			}
		}
		return root;
	}

}

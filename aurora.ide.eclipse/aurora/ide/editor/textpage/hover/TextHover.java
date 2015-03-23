package aurora.ide.editor.textpage.hover;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.builder.CompositeMapInfo;
import aurora.ide.builder.RegionUtil;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.editor.textpage.quickfix.QuickAssistUtil;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.schema.ITypeDelegate;
import aurora.ide.schema.SchemaTypeManager;

public class TextHover extends DefaultTextHover implements ITextHoverExtension {
	private ISourceViewer sourceViewer;
	private static String style = "<style>body,table{ font-family:sans-serif; font-size:9pt; background:#FFFFE1; } table,td,th {border:1px solid #888 ;border-collapse:collapse;}</style>";
	private MarkerAnnotation lastAnno = null;
	private IDocument doc;
	private CompositeMap map;

	public TextHover(ISourceViewer sourceViewer) {
		super(sourceViewer);
		this.sourceViewer = sourceViewer;
	}

	public IInformationControlCreator getHoverControlCreator() {
		return new HoverInformationControlCreator(lastAnno);
	}

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		String hover = getMarkerInfo(sourceViewer, hoverRegion);
		if (hover != null)
			return html(hover);
		doc = textViewer.getDocument();
		String word = null;
		try {
			word = doc.get(hoverRegion.getOffset(), hoverRegion.getLength());
		} catch (BadLocationException e1) {
		}
		if (word == null || word.trim().length() == 0)
			return null;
		try {
			map = CompositeMapUtil.loaderFromString(doc.get());
		} catch (Exception e) {
			return null;
		}
		CompositeMap cursorMap = QuickAssistUtil.findMap(map, doc,
				hoverRegion.getOffset());
		CompositeMapInfo info = new CompositeMapInfo(cursorMap, doc);
		if (hoverRegion.equals(info.getMapNameRegion())
				|| hoverRegion.equals(info.getMapEntTagNameRegion())) {
			// if hover region is Tag name...
			return html(SxsdUtil.getHtmlDocument(cursorMap));
		} else {
			// ////if hover region is attribute.....
			@SuppressWarnings("unchecked")
			Set<String> keySet = cursorMap.keySet();
			for (String key : keySet) {
				IRegion region = info.getAttrNameRegion(key);
				if (region == null)
					continue;
				if (hoverRegion.equals(region)) {
					return html(getAttrDocument(cursorMap, key));
				}
				region = info.getAttrValueRegion2(key);
				if (region == null)
					continue;
				if (RegionUtil.isSubRegion(region, hoverRegion)) {
					// return html(cursorMap.getString(key));
					SchemaTypeManager stm = new SchemaTypeManager(cursorMap);
					ITypeDelegate de = stm.getAttributeTypeDelegate(key);
					if (de != null) {
						return html(  de.getValue(cursorMap.getString(key)) );
					}
					return html("<pre>" + cursorMap.getString(key) + "</pre>");
				}
			}
			// ////if the hover region is namespace declare.....
			@SuppressWarnings("unchecked")
			Map<String, String> nsMap = cursorMap.getNamespaceMapping();
			Map<String, String> reverseNsMap = new HashMap<String, String>();
			if (nsMap != null)
				for (String key : nsMap.keySet()) {
					reverseNsMap.put(nsMap.get(key), key);
				}
			for (String key : reverseNsMap.keySet()) {
				String realKey = "xmlns:" + key;
				IRegion region = info.getAttrNameRegion(realKey);
				if (region == null)
					continue;
				if (RegionUtil.isSubRegion(region, hoverRegion)) {
					return html("XML Namespace : " + key + "   ");
				}
				region = info.getAttrValueRegion2(realKey);
				if (region == null)
					continue;
				if (RegionUtil.isSubRegion(region, hoverRegion)) {
					return html("<pre>" + info.getAttrRealValue(realKey)
							+ "</pre>");
				}
			}
		}
		return html(word);
	}

	private String getAttrDocument(CompositeMap map, String attrName) {
		StringBuilder sb = new StringBuilder(200);
		List<Attribute> list = null;
		try {
			list = SxsdUtil.getAttributesNotNull(map);
			for (Attribute a : list) {
				if (attrName.equalsIgnoreCase(a.getName())) {
					sb.append(a.getName() + "<br/>"
							+ SxsdUtil.notNull(a.getDocument()));
					if (SxsdUtil.getTypeNameNotNull(a).length() > 0)
						sb.append("<br/>Type : "
								+ SxsdUtil.getTypeNameNotNull(a));
					return sb.toString();
				}
			}
		} catch (Exception e) {
			sb.append(e.getMessage());
		}
		return attrName;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		IDocument doc = textViewer.getDocument();
		try {
			final int line = doc.getLineOfOffset(offset);
			int ls = doc.getLineOffset(line);
			int len = doc.getLineLength(line);

			String text = doc.get(ls, len);
			if (text == null || text.length() == 0)
				return super.getHoverRegion(textViewer, offset);
			int s = offset - ls, e = offset - ls;
			char c = text.charAt(s);
			if (isWordPart(c)) {
				while (s >= 0 && isWordPart(text.charAt(s)))
					s--;
				s++;
				while (e < text.length() && isWordPart(text.charAt(e)))
					e++;
				return new Region(ls + s, e - s);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return super.getHoverRegion(textViewer, offset);
	}

	private String getMarkerInfo(ISourceViewer sourceViewer, IRegion hoverRegion) {
		IAnnotationModel model = null;
		if (sourceViewer instanceof ISourceViewerExtension2) {
			ISourceViewerExtension2 extension = (ISourceViewerExtension2) sourceViewer;
			model = extension.getVisualAnnotationModel();
		} else
			model = sourceViewer.getAnnotationModel();
		if (model == null)
			return null;

		@SuppressWarnings("unchecked")
		Iterator<Annotation> e = model.getAnnotationIterator();
		while (e.hasNext()) {
			Annotation a = e.next();
			if (!(a instanceof MarkerAnnotation))
				continue;
			MarkerAnnotation ma = (MarkerAnnotation) a;
			lastAnno = ma;
			Position p = model.getPosition(ma);
			if (p != null
					&& p.overlapsWith(hoverRegion.getOffset(),
							hoverRegion.getLength())) {
				String msg = ma.getText();
				if (msg != null && msg.trim().length() > 0)
					return msg;
			}
		}
		lastAnno = null;
		return null;
	}

	public static String html(String str) {
		StringBuilder sb = new StringBuilder(5000);
		sb.append("<html><head>");
		sb.append(style);
		sb.append("</head><body>");
		sb.append(str.replace("\\n", "<br/>"));
		sb.append("</body></html>");
		return sb.toString();
	}

	private boolean isWordPart(char c) {
		return c == '-' || Character.isJavaIdentifierPart(c);
	}

}

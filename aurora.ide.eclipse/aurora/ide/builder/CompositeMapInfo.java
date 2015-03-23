package aurora.ide.builder;

import java.util.HashMap;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.RGB;

import uncertain.composite.CompositeMap;
import uncertain.util.resource.Location;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;
import aurora.ide.editor.textpage.scanners.XMLTagScanner;
import aurora.ide.search.core.Util;

public class CompositeMapInfo {
	private IDocument rootDoc;
	private CompositeMap currentMap;
	private IRegion startRegion;
	private IRegion endRegion;
	private IRegion mapRegion;
	private IRegion mapNameRegion;
	private IRegion mapEndTagNameRegion;
	private String prefix;
	/**
	 * Object[] 数组有3个元素:<br/>
	 * 1. 属性值(未经过转义,文件中存储的值)<br/>
	 * 2. 属性名的Regon<br/>
	 * 3. 属性值的Region
	 */
	private HashMap<String, Object[]> attrMap = new HashMap<String, Object[]>(
			10);
	private int startLine, endLine;

	public CompositeMapInfo(CompositeMap map, IDocument rootDoc) {
		this.currentMap = map;
		this.rootDoc = rootDoc;
		try {
			regionMapping();
		} catch (Exception e) {
			// throw new RuntimeException(e);
		}
	}

	/**
	 * 计算开始标签和结束标签的Region信息
	 * 
	 * @throws Exception
	 */
	private void regionMapping() throws Exception {
		Location loc = currentMap.getLocationNotNull();
		startLine = loc.getStartLine() - 1;
		endLine = loc.getEndLine() - 1;
		String mapName = currentMap.getName();
		String pre = currentMap.getPrefix();
		int startOffset = computeStartOffset(startLine,
				loc.getStartColumn() - 1);
		int endOffset = rootDoc.getLineOffset(endLine) + loc.getEndColumn() - 1;
		ITypedRegion[] typedRegion = rootDoc.computePartitioning(startOffset,
				endOffset - startOffset);
		boolean findStartTag = false;
		for (ITypedRegion tr : typedRegion) {
			String type = tr.getType();
			int ln = rootDoc.getLineOfOffset(tr.getOffset() + tr.getLength());
			int off = rootDoc.getLineOffset(ln);
			if (!findStartTag && XMLPartitionScanner.XML_START_TAG.equals(type)) {
				if (!(loc.getStartColumn() - 1 == tr.getOffset()
						+ tr.getLength() - off))
					continue;
				getPrefix(startOffset);
				// startOffset = tr.getOffset();
				mapRegion = new Region(startOffset, endOffset - startOffset);
				startRegion = new Region(startOffset, tr.getLength());
				int idx = startOffset + 1;
				if (pre != null)
					idx += pre.length() + 1;
				mapNameRegion = new Region(idx, mapName.length());
				attrMapping(startOffset, tr.getLength());
				findStartTag = true;
			} else if (XMLPartitionScanner.XML_END_TAG.equals(type)) {
				if (!(endOffset == tr.getOffset() + tr.getLength()))
					continue;
				endRegion = new Region(tr.getOffset(), tr.getLength());
				int idx = tr.getOffset() + 2;
				if (pre != null)
					idx += pre.length() + 1;
				mapEndTagNameRegion = new Region(idx, mapName.length());
				break;
			}
		}
		if (endRegion == null) {
			endRegion = startRegion;
			mapEndTagNameRegion = mapNameRegion;
		}
	}

	/**
	 * 查找当前CompositeMap的所有属性(包括namespace声明),记录相关的Region信息
	 * 
	 * @param offset
	 * @param length
	 * @throws Exception
	 */
	private void attrMapping(int offset, int length) throws Exception {
		XMLTagScanner scanner = Util.getXMLTagScanner();
		scanner.setRange(rootDoc, offset, length);
		IToken token = Token.EOF;
		String lastAttr = null;
		Object[] objs = null;
		while ((token = scanner.nextToken()) != Token.EOF) {
			Object data = token.getData();
			if (data instanceof TextAttribute) {
				TextAttribute text = (TextAttribute) data;
				RGB rgb = text.getForeground().getRGB();
				offset = scanner.getTokenOffset();
				length = scanner.getTokenLength();
				if (IColorConstants.ATTRIBUTE.equals(rgb)) {
					lastAttr = rootDoc.get(offset, length);
					objs = new Object[3];
					objs[1] = new Region(offset, length);
					continue;
				}
				if (IColorConstants.STRING.equals(rgb)) {
					objs[0] = rootDoc.get(offset + 1, length - 2);
					objs[2] = new Region(offset, length);
					attrMap.put(lastAttr, objs);
				}
			}
		}
	}

	private int computeStartOffset(int startLine, int startColumn)
			throws Exception {
		int lo = rootDoc.getLineOffset(startLine);
		int startOffset = lo + startColumn - 1;
		while (true) {
			char c = rootDoc.getChar(startOffset);
			if (c == '<')
				break;
			startOffset--;
		}
		return startOffset;
	}

	/**
	 * 返回整个CompositeMap在Document中的Region
	 * 
	 * @return
	 */
	public IRegion getMapRegion() {
		return mapRegion;
	}

	private void getPrefix(int offset) throws Exception {
		int line = rootDoc.getLineOfOffset(offset);
		int lineOffset = rootDoc.getLineOffset(line);
		String str = rootDoc.get(lineOffset, offset - lineOffset);
		char[] cs = str.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			if (!Character.isWhitespace(cs[i]))
				cs[i] = ' ';
		}
		prefix = new String(cs);
	}

	public String getLeadPrefix() {
		return prefix;
	}

	/**
	 * 返回CompositeMap的名字在Document中的Region<br/>
	 * 不包括namespace部分
	 * 
	 * @return
	 */
	public IRegion getMapNameRegion() {
		return mapNameRegion;
	}

	public IRegion getMapEntTagNameRegion() {
		return mapEndTagNameRegion;
	}

	/**
	 * 返回当前CompositeMap的一个属性的<b>属性名</b>的Region
	 * 
	 * @param attrName
	 * @return
	 */
	public IRegion getAttrNameRegion(String attrName) {
		Object[] objs = attrMap.get(attrName);
		if (objs == null)
			return null;
		return (Region) objs[1];
	}

	/**
	 * 返回当前CompositeMap的一个属性的<b>属性值</b>的Region<br/>
	 * 包括两端的引号
	 * 
	 * @param attrName
	 * @return
	 */
	public IRegion getAttrValueRegion(String attrName) {
		Object[] objs = attrMap.get(attrName);
		if (objs == null)
			return null;
		return (Region) objs[2];
	}

	/**
	 * 返回当前CompositeMap的一个属性的<b>属性值</b>的Region<br/>
	 * 当属性值不为空时,返回不包括两端引号的Region
	 * 
	 * @param attrName
	 * @return
	 */
	public IRegion getAttrValueRegion2(String attrName) {
		Object[] objs = attrMap.get(attrName);
		if (objs == null || objs[2] == null)
			return null;
		IRegion region = (Region) objs[2];
		if (region.getLength() == 2)
			return region;
		return new Region(region.getOffset() + 1, region.getLength() - 2);
	}

	/**
	 * 返回当前CompositeMap的一个属性所占的Region<br/>
	 * 从属性名开始到属性值结束(两端不包含多余的空白)
	 * 
	 * @param attrName
	 * @return
	 */
	public IRegion getAttrRegion(String attrName) {
		Object[] objs = attrMap.get(attrName);
		if (objs == null || objs[1] == null || objs[2] == null)
			return null;
		IRegion sr = (Region) objs[1];
		IRegion er = (Region) objs[2];
		return RegionUtil.union(sr, er);
	}

	/**
	 * 返回当前CompositeMap的开始结点标签的Region<br/>
	 * 
	 * @return
	 */
	public IRegion getStartTagRegion() {
		return startRegion;
	}

	/**
	 * 返回当前CompositeMap的结束结点标签的Region<br/>
	 * 
	 * @return
	 */
	public IRegion getEndTagRegion() {
		return endRegion;
	}

	/**
	 * 返回属性的真实的属性值
	 * 
	 * @param attrName
	 * @return
	 */
	public String getAttrRealValue(String attrName) {
		Object[] objs = attrMap.get(attrName);
		if (objs == null)
			return null;
		return (String) objs[0];
	}

	/**
	 * start from 0
	 * 
	 * @return
	 */
	public int getStartLine() {
		return startLine;
	}

	/**
	 * start from 0
	 * 
	 * @return
	 */
	public int getEndLine() {
		return endLine;
	}

	/**
	 * for test only
	 */
	public void print() {
		System.out.println("MapName     : " + currentMap.getName());
		System.out.println("MapRegion   : " + mapRegion);
		System.out.println("NameRegion  : " + mapNameRegion);
		System.out.println("StartRegion : " + startRegion);
		System.out.println("EndRegion   : " + endRegion);
		int maxKeyLength = 0, maxValueLength = 0;
		for (String s : attrMap.keySet()) {
			if (s.length() > maxKeyLength)
				maxKeyLength = s.length();
			String v = ((String) attrMap.get(s)[0]);
			if (v == null)
				continue;
			if (v.length() > maxValueLength)
				maxValueLength = v.length();
		}

		for (String s : attrMap.keySet()) {
			Object[] objs = attrMap.get(s);
			System.out.printf("%-" + (maxKeyLength + 1) + "s= %-"
					+ (maxValueLength + 1) + "s %s ~~~~~ %s \n", s, objs[0],
					objs[1], objs[2]);
		}
		System.out.println();
	}

	public int getLineOfRegion(IRegion region) {
		try {
			return rootDoc.getLineOfOffset(region.getOffset());
		} catch (Exception e) {
			return 0;
		}
	}

	public CompositeMap getCompositeMap() {
		return currentMap;
	}

	public IDocument getDocument() {
		return rootDoc;
	}

}

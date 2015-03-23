package aurora.ide.builder.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.BuildMessages;
import aurora.ide.builder.CompositeMapInfo;
import aurora.ide.builder.SxsdUtil;

public class SxsdProcessor extends AbstractProcessor {

	private void checkTag(BuildContext bc) {
		if (BuildContext.LEVEL_UNDEFINED_TAG == 0)
			return;
		List<Element> childs = SxsdUtil.getAvailableChildElements(bc.map);
		@SuppressWarnings("unchecked")
		List<CompositeMap> childMap = bc.map.getChildsNotNull();
		HashMap<String, Integer> countMap = new HashMap<String, Integer>(20);

		L: for (CompositeMap m : childMap) {
			// // ignore AnyElement
			Element elem = SxsdUtil.getMapElement(m);
			if (elem != null && SxsdUtil.isExtOfAnyElement(elem))
				continue;
			// ///
			String uri = m.getNamespaceURI();
			if (uri == null || !uri.startsWith("http:"))
				continue;
			String mapName = m.getName();
			Integer c = countMap.get(mapName);
			if (c == null)
				c = 0;
			countMap.put(mapName, c + 1);
			boolean reachMax = false;
			int mc = 0;
			for (int i = 0; i < childs.size(); i++) {
				Element e = childs.get(i);
				if (mapName.equalsIgnoreCase(e.getQName().getLocalName())) {
					String maxOccurs = e.getMaxOccurs();
					if (maxOccurs != null) {
						mc = Integer.parseInt(maxOccurs);
						if (mc < countMap.get(mapName)) {
							reachMax = true;
							break;
						}
					}
					continue L;
				}
			}
			CompositeMapInfo info = new CompositeMapInfo(m, bc.doc);
			IRegion region = info.getMapNameRegion();
			int line = info.getLineOfRegion(region);
			String msg = null;
			if (reachMax)
				msg = String.format(BuildMessages.get("build.reachmax"),
						mapName, mc);
			else
				msg = String.format(BuildMessages.get("build.shouldnotbehere"),
						mapName, bc.map.getName());
			AuroraBuilder.addMarker(bc.file, msg, line + 1, region,
					BuildContext.LEVEL_UNDEFINED_TAG,
					AuroraBuilder.UNDEFINED_TAG);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void processMap(BuildContext bc) {
		String uri = bc.map.getNamespaceURI();
		if (uri == null || !uri.startsWith("http:"))
			return;
		checkTag(bc);
		// 如果标签未在schema中定义过(比如拼写错误),则不检查其属性(肯定是未定义的,无意义)
		if (SxsdUtil.getMapElement(bc.map) == null)
			return;

		if (BuildContext.LEVEL_UNDEFINED_ATTRIBUTE == 0)
			return;

		// 特别处理record标签
		if (bc.map.getName().equals("record")
				&& bc.map.getParent().getName().equals("datas"))
			return;
		Set<String> nameSet = new HashSet<String>();
		if (bc.list == null) {
			AuroraBuilder.addMarker(bc.file, bc.nullListMsg,
					bc.info.getStartLine() + 1, IMarker.SEVERITY_ERROR,
					AuroraBuilder.FATAL_ERROR);
			return;
		}
		for (Attribute a : bc.list) {
			nameSet.add(a.getName().toLowerCase());
		}
		for (Map.Entry entry : (Set<Map.Entry>) bc.map.entrySet()) {
			String k = (String) entry.getKey();
			if (nameSet.contains(k.toLowerCase()))
				continue;
			IRegion region = bc.info.getAttrNameRegion(k);
			int line = bc.info.getLineOfRegion(region);
			String msg = String.format(
					BuildMessages.get("build.attribute.undefined"), k,
					bc.map.getName());
			IMarker marker = AuroraBuilder.addMarker(bc.file, msg, line + 1,
					region, BuildContext.LEVEL_UNDEFINED_ATTRIBUTE,
					AuroraBuilder.UNDEFINED_ATTRIBUTE);
			if (marker != null) {
				try {
					marker.setAttribute("ATTRIBUTE_NAME", k);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {

	}
}

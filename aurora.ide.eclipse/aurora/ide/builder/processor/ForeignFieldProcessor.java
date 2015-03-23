package aurora.ide.builder.processor;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.schema.Attribute;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.BuildMessages;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.preferencepages.BuildLevelPage;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.Util;

/**
 * check foreign field reference in bm or screen
 * 
 * @author jessen
 * 
 */
public class ForeignFieldProcessor extends AbstractProcessor {
	private int level;

	@Override
	public void processMap(BuildContext bc) {
		String ext = "_" + bc.file.getFileExtension().toUpperCase();
		level = BuildLevelPage
				.getBuildLevel(AuroraBuilder.UNDEFINED_FOREIGNFIELD + ext);
		if (level == 0)
			return;
		processAttribute(bc);
	}

	@Override
	public void visitAttribute(Attribute a, BuildContext bc) {
		if (!SxsdUtil.isForeignFieldReference(a.getAttributeType()))
			return;
		String name = a.getName();
		String value = bc.map.getString(name);
		IRegion region = bc.info.getAttrValueRegion2(name);
		int line = bc.info.getLineOfRegion(region);
		CompositeMap refModelDeclearMap = null;
		// bug fix 2013-10-29 13:44:39 jessen
		if ("ref-field".equalsIgnoreCase(bc.map.getName())) {
			// ref-field
			String relationName = bc.map.getString("relationName");
			if (relationName == null)
				relationName = bc.map.getString("relationname");
			refModelDeclearMap = CompositeUtil.findChild(bc.map.getRoot(),
					"relation", "name", relationName);
			if (refModelDeclearMap == null) {
				// relation maybe defined in parent bm
				try {
					refModelDeclearMap = CompositeUtil.findChild(
							CacheManager.getWholeBMCompositeMap(bc.file),
							"relation", "name", relationName);
				} catch (CoreException e) {
				} catch (ApplicationException e2) {
				}
			}
		} else {
			// reference
			refModelDeclearMap = bc.map.getParent();
		}
		String refModel = (String) Util
				.getReferenceModelPKG(refModelDeclearMap);
		if (refModel == null) {
			refModelDeclearMap = refModelDeclearMap.getParent();
			refModel = (String) Util.getReferenceModelPKG(refModelDeclearMap);
		}
		if (refModel == null) {
			return;
		}
		IFile bmfile = ResourceUtil.getBMFile(bc.file.getProject(), refModel);
		if (bmfile != null) {
			Set<String> locFields = new LocalFieldCollector(bmfile).collect();
			if (locFields.contains(value))
				return;
			String msg = String.format(
					BuildMessages.get("build.foreignfield.undefined"),
					a.getName(), value, refModel);
			AuroraBuilder.addMarker(bc.file, msg, line + 1, region, level,
					AuroraBuilder.UNDEFINED_FOREIGNFIELD);
		} else {
			// the refModel does not exists,this error will be handled in
			// BmProcessor
		}
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {

	}

}

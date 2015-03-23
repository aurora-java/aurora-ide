package aurora.ide.builder.processor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.BuildMessages;
import aurora.ide.builder.CompositeMapInfo;
import aurora.ide.builder.SxsdUtil;

public class DataSetProcessor extends AbstractProcessor {
	private Set<String> datasetSet = new HashSet<String>();
	private Set<Object[]> dataSetTask = new HashSet<Object[]>();

	@Override
	public void processMap(BuildContext bc) {
		if (BuildContext.LEVEL_UNDEFINED_DATASET == 0)
			return;
		processAttribute(bc);
	}

	@Override
	public void visitAttribute(Attribute a, BuildContext bc) {
		if (SxsdUtil.isDataSetReference(a.getAttributeType())) {
			String name = a.getName();
			String value = bc.map.getString(name);
			if (bc.map.getName().equalsIgnoreCase("dataSet")) {
				if (name.equalsIgnoreCase("id")) {
					datasetSet.add(value);
					return;
				}
			}
			dataSetTask.add(new Object[] { name, value, bc });
		}
	}

	@Override
	public void processComplete(IFile file, CompositeMap map1, IDocument doc) {
		if (BuildContext.LEVEL_UNDEFINED_DATASET == 0)
			return;
		for (Object[] objs : dataSetTask) {
			String name = (String) objs[0];
			String value = (String) objs[1];
			if (datasetSet.contains(value))
				continue;
			CompositeMapInfo info = ((BuildContext) objs[2]).info;
			IRegion region = info.getAttrValueRegion2(name);
			int line = info.getLineOfRegion(region);
			String msg = String.format(
					BuildMessages.get("build.dataset.undefined"), name, value);
			IMarker marker = AuroraBuilder.addMarker(file, msg, line + 1,
					region, BuildContext.LEVEL_UNDEFINED_DATASET,
					AuroraBuilder.UNDEFINED_DATASET);
			if (marker != null) {
				try {
					marker.setAttribute("ATTRIBUTE_NAME", name);
					marker.setAttribute("ATTRIBUTE_VALUE", value);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

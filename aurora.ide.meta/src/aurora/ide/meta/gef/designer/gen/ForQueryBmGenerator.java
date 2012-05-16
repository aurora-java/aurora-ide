package aurora.ide.meta.gef.designer.gen;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import uncertain.composite.CompositeMap;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Relation;
import aurora.ide.search.cache.CacheManager;

/**
 * override base bm , and add ref-fields ,query fields to it(feature from parent
 * class)
 * 
 * @see ForMaintainBmGenerator
 * @author jessen
 * 
 */
public class ForQueryBmGenerator extends ForMaintainBmGenerator {

	public ForQueryBmGenerator(BMModel model, IFile baseBMFile) {
		super(model, baseBMFile);
	}

	@Override
	protected void setUpModelMap(CompositeMap modelMap) {
		genRefFieldMap(modelMap);
	}

	protected void genRefFieldMap(CompositeMap modelMap) {
		CompositeMap refMap = genRefFieldsMap(getModel());
		if (refMap.getChildsNotNull().size() > 0)
			modelMap.addChild(refMap);
	}

	private CompositeMap genRefFieldsMap(BMModel model) {
		CompositeMap fieldsMap = newCompositeMap("ref-fields");
		ArrayList<String> reffName = new ArrayList<String>();
		for (Relation r : model.getRelationList()) {
			String bmpath = r.getRefTable();
			IFile file = ResourceUtil.getBMFile(getAuroraProject(), bmpath);
			CompositeMap refBmMap = null;
			try {
				refBmMap = CacheManager.getWholeBMCompositeMap(file);
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
			if (refBmMap == null)
				continue;
			BMCompositeMap bcm = new BMCompositeMap(refBmMap);
			for (String refp : r.getRefPromptsArray()) {
				CompositeMap remoteField = bcm.getFieldByPrompt(refp);
				if (remoteField == null)
					continue;
				CompositeMap m = newCompositeMap("ref-field");
				m.put("relationName", r.getName());
				String remoteDisplay = remoteField.getString("name");
				String ref_name = remoteDisplay + "_ref";
				int i = 1;
				while (reffName.indexOf(ref_name) != -1) {
					ref_name = remoteDisplay + "_ref_" + i++;
				}
				reffName.add(ref_name);
				m.put("name", ref_name);
				m.put("sourceField", remoteDisplay);
				fieldsMap.addChild(m);
			}
		}
		return fieldsMap;
	}

}

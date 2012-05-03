package aurora.ide.meta.gef.designer.gen;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;

public class ExtendBmGenerator extends BaseBmGenerator {
	private BMModel model;
	private IFile bmFile;

	public ExtendBmGenerator(BMModel model, IFile bmFile) {
		super();
		this.model = model;
		this.bmFile = bmFile;
	}

	public CompositeMap gen() {
		for (String type : model.getAutoExtendTypes()) {
			type = type.trim();
			IFile file = getExtFile(type);
			AbstractBmGenerator bmg = null;
			if (IDesignerConst.AE_LOV.equals(type)) {
				bmg = new ForLovBmGenerator(model, bmFile);
			} else if (IDesignerConst.AE_QUERY.equals(type)) {
				bmg = new ForQueryBmGenerator(model, bmFile);
			} else if (IDesignerConst.AE_MAINTAIN.equals(type)) {
				bmg = new ForMaintainBmGenerator(model, bmFile);
			} else if (IDesignerConst.AE_UPDATE.equals(type)) {
			} else {
				// System.out.println("unknown auto extend mode : " + type);
				continue;
			}
			if (bmg == null)
				continue;
			CompositeMap map = bmg.gen();
			try {
				if (map != null)
					createOrWriteFile(file, map);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private IFile getExtFile(String s) {
		IPath path = new Path(bmFile.getFullPath().removeFileExtension()
				.toString()
				+ "_for_" + s).addFileExtension("bm");
		IFile file = bmFile.getProject().getParent().getFile(path);
		return file;
	}

}

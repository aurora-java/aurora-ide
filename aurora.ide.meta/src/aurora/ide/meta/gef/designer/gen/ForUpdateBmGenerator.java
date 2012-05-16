package aurora.ide.meta.gef.designer.gen;

import org.eclipse.core.resources.IFile;

import aurora.ide.meta.gef.designer.model.BMModel;

public class ForUpdateBmGenerator extends ForMaintainBmGenerator {

	public ForUpdateBmGenerator(BMModel model, IFile baseBMFile) {
		super(model, baseBMFile);
	}

}

package aurora.ide.navigator;


import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import aurora.ide.AuroraProjectNature;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.DialogUtil;


public class BMHierarchyViewerTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (!(receiver instanceof IResource))
			return false;
		IResource resc = (IResource)receiver;
		IProject proejct = resc.getProject();
		try {
			if(proejct == null || !proejct.isOpen()|| !AuroraProjectNature.hasAuroraNature(proejct)){
				return false;
			}
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
			return false;
		}
		if (receiver instanceof BMFile)
			return true;
		if (receiver instanceof IFile){
			IFile file = (IFile)receiver;
			if(file.getName().toLowerCase().endsWith("."+AuroraConstant.BMFileExtension)){
				return true;
			}
		}
		if (!(receiver instanceof IContainer))
			return false;
		IContainer container = (IContainer) receiver;
		//check project is open
		

		try {
			return isValidDir(container);
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		}
		return false;
	}

	private  boolean isValidDir(IContainer container) throws CoreException {
		IResource[] childs = container.members();
		for(int i= 0;i<childs.length;i++){
			IResource child = childs[i];
			if(child instanceof IFile){
				if(child.getName().toLowerCase().endsWith("."+AuroraConstant.BMFileExtension)){
					return true;
				}
			}
//			else if(child instanceof IContainer){
//				if(isValidDir((IContainer)child))
//					return true;
//			}
		}
		return false;
	}
}

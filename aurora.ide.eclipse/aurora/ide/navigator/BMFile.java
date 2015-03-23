package aurora.ide.navigator;


import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import aurora.ide.bm.BMUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;

public class BMFile extends File{
	private IPath parentBMPath;
	private IPath bmPath;
	private List subBMFiles = new LinkedList();
	public BMFile(IPath parentBMPath,IPath bmPath){
		super(bmPath,(Workspace)ResourcesPlugin.getWorkspace());
		this.parentBMPath = parentBMPath;
		this.bmPath = bmPath;
	}
	public IPath getParentBMPath() {
		return parentBMPath;
	}
	public void setParentBMPath(IPath parentBMPath) {
		this.parentBMPath = parentBMPath;
	}
	public IPath getPath() {
		return bmPath;
	}
	public void setPath(IPath bmPath) {
		this.bmPath = bmPath;
	}
	public List getSubBMFiles() {
		return subBMFiles;
	}
	public void setSubBMFiles(List subBMFiles) {
		this.subBMFiles = subBMFiles;
	}
	public void addSubBMFile(BMFile subBMFile){
		subBMFiles.add(subBMFile);
	}
	public void removeSubBMFile(BMFile subBMFile){
		subBMFiles.remove(subBMFile);
	}
	public static BMFile createBMFileFromResource(IResource resource) throws ApplicationException{
		if(resource == null)
			return null;
		if(!resource.getName().toLowerCase().endsWith("."+AuroraConstant.BMFileExtension)){
			return null;
		}
		String extendValue = "";
		BMFile thisFile = null;
		extendValue = BMUtil.getExtendValue(resource);
		if (extendValue == null) {
			thisFile = new BMFile(null, resource.getFullPath());
			return thisFile;
		}
		IResource parent = BMUtil.getBMResourceFromClassPath(resource.getProject(), extendValue);
		if (parent == null) {
//			DialogUtil.showErrorMessageBox(resource.getLocation().toOSString() + "'s parent " + extendValue
//					+ " can not be found.");
			thisFile = new BMFile(null, resource.getFullPath());
			return thisFile;
		}
		thisFile = new BMFile(parent.getFullPath(), resource.getFullPath());
		return thisFile;
	}
}

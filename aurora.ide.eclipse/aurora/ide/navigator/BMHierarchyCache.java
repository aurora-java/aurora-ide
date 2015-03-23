package aurora.ide.navigator;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import aurora.ide.AuroraProjectNature;
import aurora.ide.bm.BMUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.ProjectUtil;
import aurora.ide.helpers.SystemException;

public class BMHierarchyCache {
	private Map projectBMFileMap = new HashMap();
	private static BMHierarchyCache instance;
	private BMHierarchyCache() {
	}
	public synchronized static BMHierarchyCache getInstance() {
		if (instance == null) {
			instance = new BMHierarchyCache();
		}
		return instance;
	}

	public void initCache(IProject project) throws ApplicationException {
		if (project == null) {
			throw new ApplicationException("paramter:project can not be null");
		}
		try {
			if (!AuroraProjectNature.hasAuroraNature(project))
				return;
		} catch (CoreException e) {
			throw new SystemException(e);
		}
		String bmBaseDir = ProjectUtil.getBMHome(project);
		if (bmBaseDir == null) {
			throw new ApplicationException("请检查BM目录设置！");
		}
		IResource bmDir = ResourcesPlugin.getWorkspace().getRoot().findMember(bmBaseDir);
		if (bmDir == null) {
			throw new ApplicationException(bmBaseDir + "资源不存在");
		}
		if (!(bmDir instanceof IContainer)) {
			throw new ApplicationException(bmBaseDir + "不是目录");
		}
		IContainer bmContainer = (IContainer) bmDir;
		iteratorResource(bmContainer);
	}
	private void iteratorResource(IContainer parent) throws ApplicationException {
		try {
			IResource[] members = parent.members();
			for (int i = 0; i < members.length; i++) {
				IResource child = members[i];
				if (child.getName().toLowerCase().endsWith("." + AuroraConstant.BMFileExtension)) {
					createThisBMFileHierachy(child);
				}
				if (child instanceof IContainer) {
					iteratorResource((IContainer) child);
				}
			}
		} catch (CoreException e) {
			throw new SystemException(e);
		}
	}
	public BMFile createThisBMFileHierachy(IPath bmFile) throws ApplicationException {
		return createThisBMFileHierachy(ResourcesPlugin.getWorkspace().getRoot().findMember(bmFile));
	}
	public BMFile createThisBMFileHierachy(IResource bmFile) throws ApplicationException {
		if(bmFile == null)
			return null;
		Map ifileMap = getBMFileMapNotNull(bmFile.getProject());
		IPath thisKey = bmFile.getFullPath();
		Object obj = ifileMap.get(thisKey);
		if (obj != null)
			return (BMFile) obj;
		BMFile thisFile = BMFile.createBMFileFromResource(bmFile);
		if(thisFile == null)
			return null;
		ifileMap.put(thisKey, thisFile);
		if(thisFile.getParentBMPath() == null)
			return thisFile;
		IResource parent = ResourcesPlugin.getWorkspace().getRoot().findMember(thisFile.getParentBMPath());
		BMFile parentFile = createThisBMFileHierachy(parent);
		if (parentFile != null)
			parentFile.addSubBMFile(thisFile);
		return thisFile;
	}
	public void removeProject(IProject project) {
		projectBMFileMap.remove(project);
	}
	public Map getBMFileMapNotNull(IProject project) {
		Object obj = projectBMFileMap.get(project);
		if (obj == null) {
			obj = new HashMap();
		}
		projectBMFileMap.put(project, obj);
		return (Map) obj;
	}
	public boolean isCached(IProject project) {
		return projectBMFileMap.containsKey(project);
	}
	public void updateBMFile(IResource resource) throws ApplicationException{
		if(resource == null)
			return;
		Map ifileMap = getBMFileMapNotNull(resource.getProject());
		IPath thisKey = resource.getFullPath();
		BMFile oldBMFile = (BMFile)ifileMap.get(thisKey);
		if(oldBMFile != null){
			IResource parentBMFile = BMUtil.getBMResourceFromClassPath(resource.getProject(),BMUtil.getExtendValue(resource));
			if(oldBMFile.getParentBMPath() == null && parentBMFile == null)
				return;
			if(parentBMFile.getFullPath() != null && parentBMFile.equals(oldBMFile.getParentBMPath())){
				return;
			}
			removeBMFile(oldBMFile);
		}
		createThisBMFileHierachy(resource);
	}
	public void addBMFile(IResource resource) throws ApplicationException{
		if(resource == null)
			return;
		createThisBMFileHierachy(resource);
	}
	public void removeBMFile(IResource resource){
		if(resource == null)
			return;
		Map ifileMap = getBMFileMapNotNull(resource.getProject());
		IPath thisKey = resource.getFullPath();
		BMFile oldBMFile = (BMFile)ifileMap.get(thisKey);
		if(oldBMFile == null)
			return ;
		ifileMap.remove(oldBMFile.getPath());
		if(oldBMFile.getParentBMPath() != null){
			BMFile parentBM = getBMFile(oldBMFile.getParentBMPath());
			if(parentBM != null){
				parentBM.removeSubBMFile(oldBMFile);
			}
		}
	}
	public BMFile getBMFile(IPath path){
		if(path == null)
			return null;
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if(resource == null)
			return null;
		Map ifileMap = getBMFileMapNotNull(resource.getProject());
		return (BMFile)ifileMap.get(resource.getFullPath());
	}
}

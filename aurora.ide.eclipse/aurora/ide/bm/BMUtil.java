package aurora.ide.bm;


import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.helpers.ProjectUtil;
import aurora.ide.search.cache.CacheManager;

public class BMUtil {
	public static final String ExtendAttrName = "extend";
	public static final String BMPrefix = "bm";
	public static final String FeaturesUri = "aurora.database.features";
	public static final String FeaturesPrefix = "f";
	public static final String OracleUri = "aurora.database.local.oracle";
	public static final String OraclePrefix = "ora";
	public static IResource getBMResourceFromClassPath(String classPath,String fileExt) throws ApplicationException{
		return getBMResourceFromClassPath(ProjectUtil.getIProjectFromSelection(),classPath,fileExt);
	}
	public static IResource getBMResourceFromClassPath(IProject project, String classPath,String fileExt) throws ApplicationException {
		if(classPath == null||project==null)
			return null;
		String path = classPath.replace('.', File.separatorChar) +'.' + fileExt;
		String fullPath = ProjectUtil.getBMHome(project)+File.separatorChar+path;
		IResource file = ResourcesPlugin.getWorkspace().getRoot().findMember(fullPath);
		return file;
	}
	public static IResource getBMResourceFromClassPath(String classPath) throws ApplicationException{
		return getBMResourceFromClassPath(ProjectUtil.getIProjectFromSelection(),classPath);
	}
	public static IResource getBMResourceFromClassPath(IProject project, String classPath) throws ApplicationException {
		if(classPath == null||project==null)
			return null;
		String path = classPath.replace('.', File.separatorChar) +'.' + AuroraConstant.BMFileExtension;
		String fullPath = ProjectUtil.getBMHome(project)+File.separatorChar+path;
		IResource file = ResourcesPlugin.getWorkspace().getRoot().findMember(fullPath);
		return file;
	}
	public static String getBMDescription(IResource file) throws ApplicationException{
		if(file == null)
			return null;
		CompositeMap bm = AuroraResourceUtil.loadFromResource(file);
		final String bmDescNodeName = "description";
		if(bm == null)
			return null;
		if (!bm.getQName().getLocalName().equals(AuroraConstant.ModelQN.getLocalName())){
			throw new ApplicationException("文件:"+file.getFullPath().toOSString()+"的"+LocaleMessage.getString("this.root.element.is.not") + AuroraConstant.ModelQN+ " !");
		}
		CompositeMap bmCm = bm.getChild(bmDescNodeName);
		if(bmCm != null){
			return bmCm.getText();
		}
		return null;
	}
	public static CompositeMap getFieldsFromBM(CompositeMap modelNode){
		if (modelNode == null)
			return null;
		BusinessModel model = BusinessModel.getInstance(modelNode);
		String prefix = CompositeMapUtil.getContextFullName(modelNode, AuroraConstant.FieldsQN);
		CompositeMap fieldsNode = new CommentCompositeMap(prefix,AuroraConstant.FieldsQN.getNameSpace(),AuroraConstant.FieldsQN.getLocalName());
		Field[] fields = model.getFields();
		if (fields == null || fields.length ==0) {
			return null;
		}
		for(int i=0;i<fields.length;i++){
			CompositeMap fieldNode = fields[i].getObjectContext();
			fieldsNode.addChild(fieldNode);
		}
		return fieldsNode;
	}
	public static CompositeMap getFieldsFromBMPath(String classPath) throws ApplicationException{
		CompositeMap modelNode = AuroraResourceUtil.loadFromResource(BMUtil.getBMResourceFromClassPath(classPath));
		if (modelNode == null)
			return null;
		return BMUtil.getFieldsFromBM(modelNode);
	}
	public static CompositeMap getFieldsFromDS(CompositeMap dataSet) throws ApplicationException{
		if(dataSet == null)
			return null;
		CompositeMap fields = dataSet.getChild("fields");
		if(fields != null && fields.getChilds() != null && fields.getChilds().size()>0){
			return fields;
		}
		String classPath = dataSet.getString("model");
		if(classPath == null)
			return null;
		return getFieldsFromBMPath(classPath);
	}
	public static CompositeMap createBMTopNode(){
		CompositeMap model = new CommentCompositeMap("bm",AuroraConstant.ModelQN.getNameSpace(),AuroraConstant.ModelQN.getLocalName());
		return model;
	}
	public static String getExtendValue(IResource bmFile) throws ApplicationException {
		if (bmFile == null) {
			throw new ApplicationException(bmFile + "文件不能为空！");
		}
//		CompositeLoader cl = AuroraResourceUtil.getCompsiteLoader();
//		String localPath = bmFile.getLocation().toOSString();
		CompositeMap bmData = null;
//		try {
//			bmData = cl.loadByFile(localPath);
//		} catch (IOException e) {
//			throw new ApplicationException("请查看" + localPath + "文件是否存在.");
//		} catch (SAXException e) {
//			throw new ApplicationException("请查看" + localPath + "文件格式是否正确！");
//		}
		try {
			bmData = CacheManager.getCompositeMap((IFile)bmFile);
			String extendValue = bmData.getString(ExtendAttrName);
			return extendValue;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
}

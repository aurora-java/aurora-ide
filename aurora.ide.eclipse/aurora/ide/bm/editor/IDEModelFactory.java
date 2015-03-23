/**
 * 
 */
package aurora.ide.bm.editor;


import org.eclipse.core.resources.IFile;

import uncertain.ocm.OCManager;
import aurora.ide.bm.ExtendModelFactory;


/**
 * @author linjinxiao
 *
 */
public class IDEModelFactory extends ExtendModelFactory {

	public IDEModelFactory(OCManager ocm, IFile file) {
		super(ocm,file);
	}

//	protected BusinessModel getNewModelInstance(String name, String ext){
//		BusinessModel model = null;
//		if (name == null)
//		    throw new IllegalArgumentException("model name is null");
//		try {
//			String filePath = convertResourcePath(name,ext);
//			CompositeLoader loader = CommentCompositeLoader.createInstanceForOCM();
//		    String fullPath = ProjectUtil.getBMHomeLocalPath(ProjectUtil.getIProjectFromSelection()) + File.separator + filePath;
//		    CompositeMap config = loader.loadByFullFilePath(fullPath);
//		    if (config == null)
//		        throw new IOException("Can't load resource " + name);
//		    model = createBusinessModelInternal(config);
//		    model.setName(name);
//		} catch (IOException e) {
//			DialogUtil.logErrorException(e);
//			return null;
//		} catch (ApplicationException e) {
//			DialogUtil.showExceptionMessageBox(e);
//			return null;
//		} catch (SAXException e) {
//			DialogUtil.showExceptionMessageBox(e);
//			return null;
//		}
//		return model;
//	}
//    public String convertResourcePath( String path, String file_ext ){
//        return path.replace('.', '/') +'.' + file_ext;
//    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}

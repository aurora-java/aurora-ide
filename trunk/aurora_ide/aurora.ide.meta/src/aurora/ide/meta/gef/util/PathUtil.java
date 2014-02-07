package aurora.ide.meta.gef.util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


public class PathUtil {
	static public IPath makeRelative(String path, String base) {
		IPath afp = new Path(base);
		IPath p = new Path(path);
		IPath makeRelativeTo = p.makeRelativeTo(afp);
		return makeRelativeTo;
	}

	static public IPath makeRelative(IPath path, IPath base) {
		IPath makeRelativeTo = path.makeRelativeTo(base);
		return makeRelativeTo;
	}
	static public IPath makeAbsolute(String path,String base) {
		IPath afp = new Path(base);
		IPath p = new Path(path);
		return afp.append(p);
	}
	static public IPath makeAbsolute(IPath path,IPath base) {
		return base.append(path);
	}
}

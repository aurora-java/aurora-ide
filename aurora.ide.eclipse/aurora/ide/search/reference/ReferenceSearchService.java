package aurora.ide.search.reference;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.search.ui.ISearchQuery;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.search.core.AbstractSearchService;
import aurora.ide.search.core.CompositeMapIteator;
import aurora.ide.search.core.Util;

public class ReferenceSearchService extends AbstractSearchService implements
		IDataFilter {

	private IResource scope;

	public ReferenceSearchService(IResource scope, Object source,
			ISearchQuery query) {
		super(new IResource[] { scope }, source, query);
		this.scope = scope;
		this.setSupportJS(true);
	}

	protected CompositeMapIteator createIterationHandle(IFile file) {
		Object source = this.getSource();
		if (source instanceof IFile) {
			String fileExtension = ((IFile) source).getFileExtension();
			if ("bm".equalsIgnoreCase(fileExtension)) {
				return new MultiReferenceTypeFinder(bmReference)
						.addReferenceType(urlReference);
			}
			if ("screen".equalsIgnoreCase(fileExtension)
					|| "svc".equalsIgnoreCase(fileExtension)) {
				return new MultiReferenceTypeFinder(screenReference)
						.addReferenceType(urlReference);
			}
		}
		return null;
	}

	public boolean found(CompositeMap map, Attribute attrib) {
		if (attrib == null) {
			return false;
		}
		IType attributeType = attrib.getAttributeType();
		if (attributeType instanceof SimpleType) {
			String fileExtension = ((IFile) this.getSource())
					.getFileExtension();
			if ("bm".equalsIgnoreCase(fileExtension)) {
				return bmRefMatch(map, attrib);
			}
			if ("screen".equalsIgnoreCase(fileExtension)
					|| "svc".equalsIgnoreCase(fileExtension)) {
				return screenRefMatch(map, attrib);
			}
		}
		return false;

	}

	protected boolean screenRefMatch(CompositeMap map, Attribute attrib) {
		IFile findScreenFile = findScreenFile(map, attrib);
		return this.getSource().equals(findScreenFile);
	}

	protected IFile findScreenFile(CompositeMap map, Attribute attrib) {
		IFile file = this.getFile(map.getRoot());
//		Object pkg = map.get(attrib.getName());
		Object pkg = CompositeMapUtil.getValueIgnoreCase(attrib, map);
		if (pkg == null) {
			return null;
		}
		boolean isScreenRef = false;
		if (attrib.getAttributeType() instanceof SimpleType) {
			isScreenRef = screenReference.equals(((SimpleType) attrib
					.getAttributeType()).getReferenceTypeQName());
		}
		IFile findScreenFile = null;
		if (isScreenRef) {
			IContainer webInf = Util.findWebInf(file);
			if (webInf != null) {
				IPath find = webInf.getParent().getProjectRelativePath()
						.append(pkg.toString());
//				Util.findScreenFile(file, pkg);
//				findScreenFile = file.getProject().getFile(find);
				findScreenFile = Util.findScreenFile(file, pkg);
			}
		} else {
			findScreenFile = Util.findScreenFile(file, pkg);
		}
		return findScreenFile;
	}

	protected boolean bmRefMatch(CompositeMap map, Attribute attrib) {
		Object pattern = getSearchPattern(this.getRoots(), this.getSource());
		return bmRefMatch(map, attrib, pattern);
	}

	protected boolean bmRefMatch(CompositeMap map, Attribute attrib,
			Object pattern) {
//		Object data = map.get(attrib.getName());
		Object data = CompositeMapUtil.getValueIgnoreCase(attrib, map);
		if (data instanceof String && Util.bmRefMatch(pattern, (String) data)) {
			return true;
		}
		return pattern == null ? false : pattern.equals(data);
	}

	protected IDataFilter getDataFilter(final IResource[] scope,
			final Object source) {
		return this;
	}

	protected Object createPattern(IResource[] roots, Object source) {
		if (source instanceof IFile) {
			IFile file = (IFile) source;
			String fileExtension = file.getFileExtension();
			if ("bm".equalsIgnoreCase(fileExtension)) {
				return getBMPKG(scope, file);
			}
			if ("screen".equalsIgnoreCase(fileExtension)
					|| "svc".equalsIgnoreCase(fileExtension)) {
				return getScreenPKG(scope, file);
			}
		}
		return null;
	}

	private Object getScreenPKG(IResource scope, IFile file) {
		return file.getName();
	}

	private Object getBMPKG(IResource scope, IFile file) {
		return Util.toBMPKG(file);
	}

}

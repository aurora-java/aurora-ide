package aurora.ide.search.reference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.search.ui.ISearchQuery;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.search.core.CompositeMapIteator;
import aurora.ide.search.core.Util;

public class BMFieldReferenceService extends ReferenceSearchService {

	private String fieldName;
	private String bmPkg;
	private IResource scope;

	private class BMFieldReferenceTypeFinder extends ReferenceTypeFinder {

		public BMFieldReferenceTypeFinder(QualifiedName referenceType) {
			super(referenceType);
		}

		protected boolean isReferenceType(IType attributeType) {
			if (attributeType instanceof SimpleType) {
				QualifiedName referenceTypeQName = ((SimpleType) attributeType)
						.getReferenceTypeQName();
				return foreignFieldReference.equals(referenceTypeQName)
						|| localFieldReference.equals(referenceTypeQName);
			}
			return false;
		}

	}

	public BMFieldReferenceService(IResource scope, Object source,
			ISearchQuery query, String fieldName) {
		super(scope, source, query);
		this.fieldName = fieldName;
		this.scope = scope;
		this.setSupportJS(false);
	}

	protected CompositeMapIteator createIterationHandle(IFile resource) {
		return new BMFieldReferenceTypeFinder(bmReference);
	}

	protected IDataFilter getDataFilter(final IResource[] roots,
			final Object source) {
		IDataFilter filter = new IDataFilter() {
			public boolean found(CompositeMap map, Attribute attrib) {
				boolean isLocal = isLocalRef(attrib);
				if (isLocal) {
					return localFieldFilter(map, attrib);
				} else {
					return foreignFieldFilter(map, attrib);
				}

			}

			private boolean foreignFieldFilter(CompositeMap map,
					Attribute attrib) {
				boolean isRef = isReferenceModel(map.getParent());
				if (isRef) {
					// in bm relation/refmodel
					return isMatch(scope, source, map, attrib);

				} else if (isReferenceModel(map.getParent().getParent())) {
					// in screen/dataset/lov mapping
					return isMatch(scope, source, map, attrib);
				}
				return false;
			}

			private boolean isReferenceModel(CompositeMap parent) {
				Object pkg = Util.getReferenceModelPKG(parent);
				return bmPkg == null ? false : bmPkg.equals(pkg);
			}

			private boolean localFieldFilter(CompositeMap map, Attribute attrib) {
				CompositeMap root = map.getRoot();
				IFile file = getFile(root);
				if (file.equals(source)) {
					return isMatch(scope, source, map, attrib);
				}
				return false;
			}

			private boolean isMatch(final IResource scope, final Object source,
					CompositeMap map, Attribute attrib) {
				Object pattern = getSearchPattern(new IResource[] { scope }, source);
//				Object data = map.get(attrib.getName());
				Object data = CompositeMapUtil.getValueIgnoreCase(attrib, map);
				return pattern == null ? false : pattern.equals(data);
			}

			protected boolean isLocalRef(Attribute attrib) {

				IType attributeType = attrib.getAttributeType();
				if (attributeType instanceof SimpleType) {
					QualifiedName referenceTypeQName = ((SimpleType) attributeType)
							.getReferenceTypeQName();
					return localFieldReference.equals(referenceTypeQName);
				}
				return false;
			}
		};
		return filter;
	}

	protected Object createPattern(IResource[] roots, Object source) {
		bmPkg = (String) super.createPattern(roots, source);
		return fieldName;
	}

}

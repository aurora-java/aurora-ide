package aurora.ide.search.reference;

import java.util.ArrayList;
import java.util.List;

import uncertain.composite.QualifiedName;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;

public class MultiReferenceTypeFinder extends ReferenceTypeFinder {

	private List<QualifiedName> referenceTypes;

	public MultiReferenceTypeFinder(QualifiedName referenceType) {
		super(referenceType);
		this.referenceTypes = new ArrayList<QualifiedName>();
		referenceTypes.add(referenceType);
	}

	public MultiReferenceTypeFinder addReferenceType(QualifiedName referenceType) {
		referenceTypes.add(referenceType);
		return this;
	}

	@Override
	protected boolean isReferenceType(IType attributeType) {
		if (attributeType instanceof SimpleType) {
			return referenceTypes.contains(((SimpleType) attributeType)
					.getReferenceTypeQName());
		}
		return false;
	}

}

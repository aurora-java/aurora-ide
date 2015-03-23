package aurora.ide.search.reference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.search.core.CompositeMapIteator;

public class TypeFinder extends CompositeMapIteator {

	private QualifiedName type;

	public int process(CompositeMap map) {
		try {
			List match = getMatch(map);
			if (match != null) {
				this.getResult().add(new MapFinderResult(map, match));
			}
		} catch (ApplicationException e) {
			e.printStackTrace();

		}
		return IterationHandle.IT_CONTINUE;
	}

	protected List getMatch(CompositeMap map) throws ApplicationException {
		List matchs = new ArrayList();
		boolean isMacth = false;
//		Element element = LoadSchemaManager.getSchemaManager().getElement(map);
		Element element = CompositeMapUtil.getElement(map);
		
		if (element != null) {
			List attrib_list = element.getAllAttributes();
			for (Iterator it = attrib_list.iterator(); it.hasNext();) {
				Attribute attrib = (Attribute) it.next();
				boolean referenceOf = isType(attrib);
				if (referenceOf) {
					boolean found = true;
					IDataFilter filter = getFilter();
					if (filter != null) {
						found = filter.found(map, attrib);
					}
					if (found) {
						isMacth = true;
						matchs.add(attrib);
					}
				}
			}
			if (isMacth) {
				return matchs;
			}
		}

		return null;
	}

	protected boolean isType(Attribute attribute) {
		return type.equals(attribute.getTypeQName());
	}

	public TypeFinder(QualifiedName referenceType) {
		this.type = referenceType;
	}

}

package aurora.ide.views.prompts.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.view.ViewNode;

public class PromptsFinder implements IterationHandle {

	private List<ViewNode> vns = new ArrayList<ViewNode>();
	private String promptPrefix;
	public final static QualifiedName PromptsTypeName = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "Prompts");

	public PromptsFinder(String promptPrefix) {
		this.promptPrefix = promptPrefix;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int process(CompositeMap map) {
		Element element = CompositeMapUtil.getElement(map);
		if (element != null) {
			List attrib_list = element.getAllAttributes();
			for (Iterator it = attrib_list.iterator(); it.hasNext();) {
				Attribute attrib = (Attribute) it.next();
				boolean referenceOf = isType(attrib);
				if (referenceOf) {

					ViewNode e = new ViewNode(map, attrib,promptPrefix);
					if (!(e.getZhsPrompt() == null)){
						
						vns.add(e);
					}
				}
			}
		}
		return IterationHandle.IT_CONTINUE;
	}

	public ViewNode[] getResult() {
		return vns.toArray(new ViewNode[vns.size()]);
	}

	protected boolean isType(Attribute attribute) {
		return PromptsTypeName.equals(attribute.getTypeQName());
	}

}

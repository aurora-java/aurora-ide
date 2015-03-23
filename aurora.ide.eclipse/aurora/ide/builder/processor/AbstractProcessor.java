package aurora.ide.builder.processor;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.builder.BuildContext;
import aurora.ide.search.core.Util;

public abstract class AbstractProcessor {
	public abstract void processMap(BuildContext bc);

	/**
	 * 调用此方法应重写visitAttribute方法,以完成相应操作
	 * 
	 * @param file
	 * @param map
	 * @param doc
	 * @param info
	 */
	public final void processAttribute(BuildContext bc) {
		List<Attribute> list = bc.list;
		if (list == null) {
			return;
		}
		for (Attribute a : list) {
			if (bc.map.get(a.getName()) != null)
				visitAttribute(a, bc);
		}
	}

	/**
	 * 当所有结点遍历完成后调用,以便统一处理保存的数据<br/>
	 * (不是当前结点遍历完成后调用)
	 * 
	 * @param file
	 * @param map
	 * @param doc
	 */
	public abstract void processComplete(IFile file, CompositeMap map,
			IDocument doc);

	/**
	 * 遍历当前map的所有在Schema中定义过且在当前map中出现过的属性
	 * 
	 * @param a
	 * @param file
	 * @param map
	 * @param doc
	 */
	protected void visitAttribute(Attribute a, BuildContext bc) {
	}

}

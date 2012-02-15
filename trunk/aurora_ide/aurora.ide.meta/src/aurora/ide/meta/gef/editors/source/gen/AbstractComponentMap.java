package aurora.ide.meta.gef.editors.source.gen;

import uncertain.composite.CompositeMap;

abstract public class AbstractComponentMap {
	abstract public CompositeMap toCompositMap() ;
	abstract public boolean  isCompositMapKey(String key) ;
}

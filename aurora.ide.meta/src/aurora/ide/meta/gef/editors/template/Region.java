package aurora.ide.meta.gef.editors.template;

import aurora.ide.meta.gef.editors.models.Container;

public interface Region {

	Model getModel();
	void setModel(Model model);
	
	String getName();
	void setName(String name);
	
	String getID();
	void setID(String id);
	
	Container getContainer();
	void setContainer(Container container);
	
}

package aurora.ide.meta.gef.editors.models;

import java.util.List;

import aurora.ide.meta.gef.editors.models.link.Parameter;

public interface ILink {
	public String getOpenPath();

	public void setOpenPath(String openPath);

	public List<Parameter> getParameters();

	public void addParameter(Parameter para);
}
